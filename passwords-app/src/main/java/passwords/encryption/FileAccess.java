package passwords.encryption;

import commons.lib.main.FileUtils;
import commons.lib.main.os.LogUtils;
import passwords.pojo.CredentialDatum;
import passwords.settings.CredentialsSettings;
import passwords.settings.InputParameters;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FileAccess {
    // TODO move
    public static List<CredentialDatum> decipher(EncryptionFactory encryptionFactory, CredentialsSettings securitySettings, ResourceBundle uiMessages) {
        final Path fullPathSaveDir = InputParameters.SAVE_DIR.getPropertyPath();
        final Path fullPathSaveFile = fullPathSaveDir.resolve(InputParameters.ENCRYPTED_FILENAME.getPropertyPath());
        final List<CredentialDatum> allCredentials;
        if (FileUtils.isFileExist(fullPathSaveFile)) {
            LogUtils.debug("Getting the Decryptor version {}", InputParameters.DECRYPT_VERSION.getPropertyInt());
            final EncryptionService encryptionService = encryptionFactory.getService(InputParameters.DECRYPT_VERSION.getPropertyInt());
            LogUtils.debug("EncryptionService found : {}", encryptionService);
            allCredentials = encryptionService.decrypt(fullPathSaveDir, securitySettings);
        } else {
            LogUtils.debug("No encrypted file found at {}", fullPathSaveFile.toAbsolutePath().toString());
            LogUtils.debug("No encrypted file found.");
            allCredentials = new ArrayList<>();
        }
        return allCredentials;
    }

    public static void cipher(List<CredentialDatum> credentials, EncryptionFactory encryptionFactory, CredentialsSettings securitySettings, ResourceBundle uiMessages) {
        final Path fullPathSaveDir = InputParameters.SAVE_DIR.getPropertyPath();
        //final Path fullPathSaveFile = fullPathSaveDir.resolve(InputParameters.ENCRYPTED_FILENAME.getPropertyPath());
        LogUtils.debug("Getting the encryptor version {}", InputParameters.DECRYPT_VERSION.getPropertyInt());
        LogUtils.debug(""+encryptionFactory);
        LogUtils.debug(""+InputParameters.DECRYPT_VERSION);
        final EncryptionService encryptionService = encryptionFactory.getService(InputParameters.DECRYPT_VERSION.getPropertyInt());
        LogUtils.debug("EncryptionService found : {}", encryptionService);
        encryptionService.encrypt(fullPathSaveDir, credentials,  securitySettings);
    }

}
