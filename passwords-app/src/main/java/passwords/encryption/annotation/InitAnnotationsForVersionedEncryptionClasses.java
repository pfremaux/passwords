package passwords.encryption.annotation;

import commons.lib.main.AnnotationUtils;
import commons.lib.main.SystemUtils;
import commons.lib.main.UnrecoverableException;
import commons.lib.main.os.LogUtils;
import passwords.encryption.EncryptionFactory;
import passwords.encryption.EncryptionService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class InitAnnotationsForVersionedEncryptionClasses {

    private static final Logger logger = LogUtils.initLogs();
    private final EncryptionFactory encryptionFactory;
    private final Map<Integer, EncryptionService> encryptionServiceRegistry = new HashMap<>();

    public InitAnnotationsForVersionedEncryptionClasses() {
        try {
            final String packageName = EncryptionFactory.class.getPackageName();
            LogUtils.debug("Searching annotated classes in package " + packageName);
            final List<Class<?>> classes;
            classes = AnnotationUtils.getClassesFromPackageName(packageName);
            LogUtils.debug("Found " + classes.size() + " class to analyze");
            for (Class aClass : classes) {
                initEncVersion(aClass);
            }
        } catch (ClassNotFoundException | IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            final String errorMsg = "Error while processing the annotated classes.";
            LogUtils.error(errorMsg + " " + e.getMessage());
            throw new UnrecoverableException(
                    errorMsg,
                    new String[]{"An error due to a programmer's mistake forced the application to stop"},
                    e,
                    SystemUtils.EXIT_PROGRAMMER_ERROR);
        }
        LogUtils.debug(encryptionServiceRegistry.size() + " encryptions registered");
        encryptionFactory = new EncryptionFactory(encryptionServiceRegistry);
    }


    private void initEncVersion(Class<?> aClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        LogUtils.debug("is "+aClass+" an annotation of encryption ?");
        if (aClass.isAnnotationPresent(EncryptionVersion.class)) {
            LogUtils.debug(aClass+" is an annotation of encryption");
            final EncryptionVersion[] annotationsByType = aClass.getAnnotationsByType(EncryptionVersion.class);
            final EncryptionService o = (EncryptionService) aClass.getConstructor().newInstance();
            final EncryptionService alreadyExistingVersion = encryptionServiceRegistry.put(annotationsByType[0].version(), o);
            if (alreadyExistingVersion != null) {
                LogUtils.warning("There is more than one implementation for the version " + annotationsByType[0].version());
            }
        }
    }

    public EncryptionFactory getEncryptionFactory() {
        return encryptionFactory;
    }
}
