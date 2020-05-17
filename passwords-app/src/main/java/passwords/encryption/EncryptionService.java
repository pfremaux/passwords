package passwords.encryption;


import passwords.pojo.CredentialDatum;
import passwords.settings.CredentialsSettings;

import java.nio.file.Path;
import java.util.List;

public interface EncryptionService {

    /**
     * Encrypt the specified <tt>data</tt> thanks to <tt>credentialsSettings</tt>.
     * <tt>credentialsSettings</tt>contains the rules necessary to encrypt <tt>data</tt>.
     *
     * @param savePath            Path where you want to save the encrypted data.
     * @param credentialData      The data you want to encrypt.
     * @param credentialsSettings The information needed to encrypt.
     */
    void encrypt(Path savePath, List<CredentialDatum> credentialData, CredentialsSettings credentialsSettings);

    /**
     * Decrypt the specified encrypted file/directory <tt>encodedDataPath</tt> thanks to <tt>credentialsSettings</tt>.
     * <tt>credentialsSettings</tt>contains the rules necessary to decrypt <tt>encodedDataPath</tt>.
     *
     * @param encodedDataPath     A valid path to the encrypted dat.
     * @param credentialsSettings The information needed to decrypt.
     * @return The decrypted credentials.
     */
    List<CredentialDatum> decrypt(Path encodedDataPath, CredentialsSettings credentialsSettings);
}
