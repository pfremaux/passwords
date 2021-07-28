package passwords.encryption;

import commons.lib.extra.security.asymetric.AsymmetricKeyHandler;
import commons.lib.extra.security.asymetric.PrivateKeyHandler;
import commons.lib.extra.security.asymetric.PublicKeyHandler;
import commons.lib.extra.security.symetric.SymmetricHandler;
import commons.lib.main.UnrecoverableException;
import commons.lib.main.filestructure.Hexa;
import commons.lib.main.filestructure.StructuredFile;
import commons.lib.main.os.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import passwords.StructuredFileHelper;
import passwords.encryption.annotation.EncryptionVersion;
import passwords.pojo.CredentialDatum;
import passwords.settings.CredentialsSettings;
import passwords.settings.InputParameters;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@EncryptionVersion(version = 5)
public final class RecursiveAsymetricAndSymmetricEncryption2 implements EncryptionService {
    private final Logger logger = LoggerFactory.getLogger(RecursiveAsymetricAndSymmetricEncryption2.class);


    @Override
    public void encrypt(Path savePath, List<CredentialDatum> credentialData, CredentialsSettings credentialsSettings) {
        LogUtils.debug("Encrypting with " + this.getClass().getName());

        final String separator = InputParameters.FILE_DATUM_SEPARATOR.getPropertyString();
        List<CredentialDatum> formattedCredentialData = getFormattedCredentialData(credentialData);
        final StructuredFile structuredFile = StructuredFileHelper.getInstance(separator, formattedCredentialData);
        final LinkedList<PublicKey> publicKeys = new LinkedList<>();
        LogUtils.debug("{} asymetric key to apply.", credentialsSettings.getNumbers());
        for (Integer number : credentialsSettings.getNumbers()) {
            LogUtils.debug("Getting public key number " + number);
            publicKeys.add(credentialsSettings.getPublicKeys().get(number));
        }
        PublicKeyHandler publicKeyHandler = new PublicKeyHandler();

        try {
            LogUtils.debug("Applying recursive asymmetric encryption");
            BufferedInputStream bufferedInputStream = publicKeyHandler.recursiveProcessor(publicKeys, AsymmetricKeyHandler.toBufferedInputStream(structuredFile.toByteArray()));
            byte[] data = bufferedInputStream.readAllBytes();
            int counter = 0;
            for (String password : credentialsSettings.getPasswords()) {
                String wellSizedPassword = SymmetricHandler.fillPassword(password);
                final SecretKeySpec aes = SymmetricHandler.getKey(wellSizedPassword, SymmetricHandler.DEFAULT_SYMMETRIC_ALGO);
                try {
                    LogUtils.debug("Applying symmetric with pwd n." + counter);
                    counter++;
                    data = SymmetricHandler.encrypt(aes, data, SymmetricHandler.DEFAULT_SYMMETRIC_ALGO);
                } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                    e.printStackTrace();
                }
            }

            Path saveFileFullPath = savePath.resolve(InputParameters.ENCRYPTED_FILENAME.getPropertyPath());
            LogUtils.debug("Saving file " + saveFileFullPath.toFile().getAbsolutePath());
            Files.write(saveFileFullPath, data);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            throw new UnrecoverableException(e.getMessage(), e.getMessage(), e, -2);
        }
    }

    @Override
    public List<CredentialDatum> decrypt(Path encodedDataPath, CredentialsSettings credentialsSettings) {
        try {
            byte[] allEncryptedFile = Files.readAllBytes(encodedDataPath.resolve(InputParameters.ENCRYPTED_FILENAME.getPropertyPath()));
            final List<String> passwords = new ArrayList<>(credentialsSettings.getPasswords());
            Collections.reverse(passwords);

            int counter = 0;
            for (String password : passwords) {
                String wellSizedPassword = SymmetricHandler.fillPassword(password);
                LogUtils.debug("Decrypting with password n." + counter);
                counter++;
                final SecretKeySpec aes = SymmetricHandler.getKey(wellSizedPassword, SymmetricHandler.DEFAULT_SYMMETRIC_ALGO);
                allEncryptedFile = SymmetricHandler.decrypt(aes, allEncryptedFile, SymmetricHandler.DEFAULT_SYMMETRIC_ALGO);
            }

            final LinkedList<PrivateKey> privateKeys = new LinkedList<>();
            for (Integer number : credentialsSettings.getNumbers()) {
                LogUtils.debug("Getting private key " + number);
                PrivateKey privateKey = credentialsSettings.getPrivateKeys().get(number);
                privateKeys.add(privateKey);
            }

            Collections.reverse(privateKeys);
            final PrivateKeyHandler privateKeyHandler = new PrivateKeyHandler();
            Hexa.log(logger, allEncryptedFile, 30);
            final BufferedInputStream bufferedInputStream = privateKeyHandler.recursiveProcessor(privateKeys, AsymmetricKeyHandler.toBufferedInputStream(allEncryptedFile));
            final byte[] bytesProcessed = bufferedInputStream.readAllBytes();
            final String separator = InputParameters.FILE_DATUM_SEPARATOR.getPropertyString();
            final StructuredFile load = StructuredFile.load(bytesProcessed, separator, CredentialDatum.NBR_FIELDS);
            LogUtils.debug("File loaded with {} lines and {} bytes", load.getFileData().size(), bytesProcessed.length);
            return StructuredFileHelper.getCredentialData(load);
        } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new UnrecoverableException("Bad private keys or password or programming error.", new String[]{"The keys you provided are not valid."
            }, e, -3);
        }
    }

    private List<CredentialDatum> getFormattedCredentialData(List<CredentialDatum> credData) {
        List<CredentialDatum> result = new ArrayList<>();
        for (CredentialDatum credDatum : credData) {
            result.add(new CredentialDatum(
                    credDatum.getHierarchy(),
                    credDatum.getUrl(),
                    credDatum.getLogin(),
                    credDatum.getPassword(),
                    credDatum.getComments().replaceAll("\n", " ").replaceAll("\r", " ")));
        }
        return result;
    }

}
