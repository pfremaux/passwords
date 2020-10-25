package passwords.encryption.annotation;

import commons.lib.AnnotationUtils;
import commons.lib.SystemUtils;
import commons.lib.UnrecoverableException;
import passwords.encryption.EncryptionFactory;
import passwords.encryption.EncryptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public final class InitAnnotationsForVersionedEncryptionClasses {

    private static final Logger logger = LoggerFactory.getLogger(InitAnnotationsForVersionedEncryptionClasses.class);
    private final EncryptionFactory encryptionFactory;
    private final Map<Integer, EncryptionService> encryptionServiceRegistry = new HashMap<>();

    public InitAnnotationsForVersionedEncryptionClasses() {
        try {
            final String packageName = EncryptionFactory.class.getPackageName();
            logger.info("Searching annotated classes in package {}", packageName);
            final List<Class<?>> classes;
            classes = AnnotationUtils.getClassesFromPackageName(packageName);
            logger.info("Found {} class to analyze", classes.size());
            for (Class aClass : classes) {
                initEncVersion(aClass);
            }
        } catch (ClassNotFoundException | IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            final String errorMsg = "Error while processing the annotated classes.";
            logger.error(errorMsg, e);
            throw new UnrecoverableException(
                    errorMsg,
                    new String[]{"An error due to a programmer's mistake forced the application to stop"},
                    e,
                    SystemUtils.EXIT_PROGRAMMER_ERROR);
        }
        encryptionFactory = new EncryptionFactory(encryptionServiceRegistry);
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
