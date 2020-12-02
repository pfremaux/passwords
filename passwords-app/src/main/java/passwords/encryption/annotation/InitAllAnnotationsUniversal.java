package passwords.encryption.annotation;

import commons.lib.main.UnrecoverableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;

public final class InitAllAnnotationsUniversal {

    // TODO pas prioritaire mais ce serait cool d'avoir un gestionnaire d'annotation univ

    private final Logger logger = LoggerFactory.getLogger(InitAllAnnotationsUniversal.class);
    private final Map<Class<?>, Map<Integer, Annotated>> factories = new HashMap<>();

    public InitAllAnnotationsUniversal(Class<? extends Annotated>... classes) {
        for (Class<? extends Annotated> aClass : classes) {
            try {
                final Map<Integer, ? extends Annotated> classPerVersion = initEncVersion(aClass);
                factories.computeIfAbsent(aClass, c -> new HashMap<>()).putAll(classPerVersion);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                final String errorMsg = "Error while processing the annotated classes.";
                logger.error(errorMsg, e);
                throw new UnrecoverableException(
                        errorMsg,
                        new String[]{"A error due to a programmer's mistake forced the application to stop"},
                        e,
                        -10);
            }
        }
    }

    private Class[] getClasses(String packageName)
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
        final List<Class> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[0]);
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

    private <T> Map<Integer, T> initEncVersion(Class<T> aClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final Map<Integer, T> result = new HashMap<>();
        if (aClass.isAnnotationPresent(EncryptionVersion.class)) {
            final EncryptionVersion[] annotationsByType = aClass.getAnnotationsByType(EncryptionVersion.class);
            final T o = aClass.getConstructor().newInstance();
            T alreadyExistingVersion = result.put(annotationsByType[0].version(), o);
            if (alreadyExistingVersion != null) {
                logger.warn("There is more than one implementation for the version {}.", annotationsByType[0].version());
            }
        }
        return result;
    }

    public Map<Integer, Annotated> getFactories(Class<?> aClass) {
        return factories.get(aClass);
    }
}
