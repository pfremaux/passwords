package passwords.encryption;

import commons.lib.extra.security.asymetric.AsymmetricKeyHandler;
import commons.lib.extra.security.asymetric.PrivateKeyHandler;
import commons.lib.extra.security.asymetric.PublicKeyHandler;
import commons.lib.extra.security.symetric.SymmetricHandler;
import commons.lib.main.UnrecoverableException;
import commons.lib.main.filestructure.StructuredFile;
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

@EncryptionVersion(version = 4)
public final class RecursiveAsymetricAndSymmetricEncryption implements EncryptionService {
    private final Logger logger = LoggerFactory.getLogger(RecursiveAsymetricAndSymmetricEncryption.class);

    @Override
    public void encrypt(Path savePath, List<CredentialDatum> credentialData, CredentialsSettings credentialsSettings) {
        logger.info("Encrypting with " + this.getClass().getName());
        logger.info("Building structured file...");

        final String separator = InputParameters.FILE_DATUM_SEPARATOR.getPropertyString();
        final StructuredFile structuredFile = StructuredFileHelper.getInstance(separator, credentialData);
        final LinkedList<PublicKey> publicKeys = new LinkedList<>();
        for (Integer number : credentialsSettings.getNumbers()) {
            logger.info("Getting public key number " + number);
            publicKeys.add(credentialsSettings.getPublicKeys().get(number));
        }
        PublicKeyHandler publicKeyHandler = new PublicKeyHandler();

        try {
            logger.info("Applying recursive asymmetric encryption");
            BufferedInputStream bufferedInputStream = publicKeyHandler.recursiveProcessor(publicKeys, AsymmetricKeyHandler.toBufferedInputStream(structuredFile.toByteArray()));
            byte[] data = bufferedInputStream.readAllBytes();
            for (String password : credentialsSettings.getPasswords()) {
                String wellSizedPassword = SymmetricHandler.fillPassword(password);
                final SecretKeySpec aes = SymmetricHandler.getKey(wellSizedPassword, SymmetricHandler.DEFAULT_SYMMETRIC_ALGO);
                try {
                    logger.info("Applying symmetric with pwd " + password);
                    data = SymmetricHandler.encrypt(aes, data, SymmetricHandler.DEFAULT_SYMMETRIC_ALGO);
                } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                    e.printStackTrace();
                }
            }
            Path saveFileFullPath = savePath.resolve(InputParameters.ENCRYPTED_FILENAME.getPropertyPath());
            logger.info("Saving file " + saveFileFullPath.toFile().getAbsolutePath());
            Files.write(saveFileFullPath, data);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<CredentialDatum> decrypt(Path encodedDataPath, CredentialsSettings credentialsSettings) {

        try {
            byte[] allEncryptedFile = Files.readAllBytes(encodedDataPath.resolve(InputParameters.ENCRYPTED_FILENAME.getPropertyPath()));

            final List<String> passwords = new ArrayList<>(credentialsSettings.getPasswords());
            Collections.reverse(passwords);

            for (String password : passwords) {
                String wellSizedPassword = SymmetricHandler.fillPassword(password);
                logger.info("Decrypting with password " + password);
                final SecretKeySpec aes = SymmetricHandler.getKey(wellSizedPassword, SymmetricHandler.DEFAULT_SYMMETRIC_ALGO);
                allEncryptedFile = SymmetricHandler.decrypt(aes, allEncryptedFile, SymmetricHandler.DEFAULT_SYMMETRIC_ALGO);
            }

            final LinkedList<PrivateKey> privateKeys = new LinkedList<>();
            for (Integer number : credentialsSettings.getNumbers()) {
                logger.info("Getting private key " + number);
                PrivateKey privateKey = credentialsSettings.getPrivateKeys().get(number);
                privateKeys.add(privateKey);
            }

            Collections.reverse(privateKeys);
            PrivateKeyHandler privateKeyHandler = new PrivateKeyHandler();
            logger.info("Decryption asymmetric");
            BufferedInputStream bufferedInputStream = privateKeyHandler.recursiveProcessor(privateKeys, AsymmetricKeyHandler.toBufferedInputStream(allEncryptedFile));
            byte[] bytes = bufferedInputStream.readAllBytes();
            logger.info("Building data");
            final StructuredFile load = StructuredFile.load(bytes, ";", CredentialDatum.NBR_FIELDS);
            return StructuredFileHelper.getCredentialData(load);
        } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new UnrecoverableException("Bad private keys or password or programming error.", new String[]{"The keys you provided are not valid."
            }, e, -3);
        }
    }
}
