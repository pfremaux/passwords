package passwords.encryption.annotation;

import commons.lib.SystemUtils;
import commons.lib.UnrecoverableException;
import passwords.encryption.EncryptionFactory;
import passwords.encryption.EncryptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class InitAllAnnotations {

    private final Logger logger = LoggerFactory.getLogger(InitAllAnnotations.class);
    private final EncryptionFactory encryptionFactory;
    private final Map<Integer, EncryptionService> encryptionServiceRegistry = new HashMap<>();

    public InitAllAnnotations() {
        try {
            final String packageName = EncryptionFactory.class.getPackageName();
            logger.info("Searching annotated classes in package {}", packageName);
            final List<Class<?>> hs;
            if (isInIde()) {
                hs = getClassesFromIde(packageName);
            } else {
                hs = getClassesFromJar(packageName);
            }
            logger.info("Found {} class to analyze", hs.size());
            for (Class aClass : hs) {
                initEncVersion(aClass);
            }
        } catch (ClassNotFoundException | IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            final String errorMsg = "Error while processing the annotated classes.";
            logger.error(errorMsg, e);
            throw new UnrecoverableException(
                    errorMsg,
                    new String[]{"A error due to a programmer's mistake forced the application to stop"},
                    e,
                    SystemUtils.EXIT_PROGRAMMER_ERROR);
        }
        encryptionFactory = new EncryptionFactory(encryptionServiceRegistry);
    }

    private boolean isInIde() {
        final String path = InitAllAnnotations.class.getResource("InitAllAnnotations.class").getPath();
        logger.debug("Testing if in an IDE with path {}", path);
        return path.startsWith("/");
    }

    private List<Class<?>> getClassesFromIde(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        final List<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    private List<Class<?>> getClassesFromJar(String packageName)
            throws ClassNotFoundException, IOException {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        final String path = packageName.replace('.', '/');
        logger.info("Searching in Resource {}", path);
        return listClassesPerJarFile(classLoader, path);
    }

    private List<Class<?>> listClassesPerJarFile(ClassLoader classLoader, String path) throws IOException, ClassNotFoundException {
        final List<Class<?>> result = new ArrayList<>();
        final Enumeration<URL> resources = classLoader.getResources(path);
        while (resources.hasMoreElements()) {
            final URL resource = resources.nextElement();
            final String pathJarFile = resource.getPath().substring("file:".length() /*"file:\\".length()*/, resource.getPath().indexOf("!"));
            final JarFile jarFile = new JarFile(pathJarFile);
            final Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                final JarEntry jarEntry = entries.nextElement();
                final String name = jarEntry.getName();
                if (name.startsWith(path) && name.endsWith(".class")) {
                    result.add(Class.forName(name.replaceAll("/", ".").substring(0, name.length() - ".class".length())));
                }
            }
        }
        return result;
    }

    private List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        final List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        final File[] files = directory.listFiles();
        for (File file : Optional.ofNullable(files).orElse(new File[0])) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    private void initEncVersion(Class<?> aClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        logger.debug("is {} an annotation of encryption ?", aClass);
        if (aClass.isAnnotationPresent(EncryptionVersion.class)) {
            logger.debug("{} is an annotation of encryption", aClass);
            final EncryptionVersion[] annotationsByType = aClass.getAnnotationsByType(EncryptionVersion.class);
            final EncryptionService o = (EncryptionService) aClass.getConstructor().newInstance();
            final EncryptionService alreadyExistingVersion = encryptionServiceRegistry.put(annotationsByType[0].version(), o);
            if (alreadyExistingVersion != null) {
                logger.warn("There is more than one implementation for the version {}.", annotationsByType[0].version());
            }
        }
    }

    public EncryptionFactory getEncryptionFactory() {
        return encryptionFactory;
    }
}
