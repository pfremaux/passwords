package passwords.gui;

import commons.lib.extra.security.asymetric.AsymmetricKeyHandler;
import commons.lib.extra.security.asymetric.PrivateKeyHandler;
import commons.lib.extra.security.asymetric.PublicKeyHandler;
import commons.lib.main.UnrecoverableException;
import commons.lib.main.os.LogUtils;
import passwords.settings.CredentialsSettings;
import passwords.settings.InputParameters;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CredentialSettingsManager {
    private final Logger logger = LogUtils.initLogs();

    public CredentialsSettings getCredentialsSettings(Path pkPath, List<String> pwds, List<Integer> numbers) {
        LogUtils.debug("Building credentials settings with {} passwords and {} public keys", pwds.size(), numbers.size());
        final List<PrivateKey> pvKeys = new ArrayList<>();
        final List<PublicKey> publicKeys = new ArrayList<>();
        final PrivateKeyHandler privateKeyHandler = new PrivateKeyHandler();
        final PublicKeyHandler publicKeyHandler = new PublicKeyHandler();
        final String publicFileExtension = InputParameters.PUBLIC_FILE_EXTENSION.getPropertyString();
        final String privateFileExtension = InputParameters.PRIVATE_FILE_EXTENSION.getPropertyString();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(pkPath, "*.{" + publicFileExtension + "," + privateFileExtension + "}")) {
            for (Path entry : stream) {
                final String fileName = entry.toFile().getName();
                final String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
                if (extension.equals(privateFileExtension)) {
                    final PrivateKey rsa = privateKeyHandler.load(entry.toAbsolutePath().toString(), AsymmetricKeyHandler.ASYMMETRIC_ALGORITHM);
                    pvKeys.add(rsa);
                } else if (extension.equals(publicFileExtension)) {
                    final PublicKey rsa = publicKeyHandler.load(entry.toAbsolutePath().toString(), AsymmetricKeyHandler.ASYMMETRIC_ALGORITHM);
                    publicKeys.add(rsa);
                } else {
                    logger.warning("Unexpected file extension : " + extension);
                }
                LogUtils.debug("File {} found.", fileName);
            }
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException x) {
            // IOException can never be thrown by the iteration.
            // In this snippet, it can // only be thrown by newDirectoryStream.
            final String s = "Error while generating credentials settings.";
            logger.throwing("CredentialSettingsManager", "getCredentialsSettings", x);
            throw new UnrecoverableException(s,
                    new String[]{
                            "The application crashed.",
                            "It might be an error caused by your settings or the programmer.",
                            "Exiting..."}, x, -30);
        }
        return new CredentialsSettings(pvKeys, publicKeys, pwds, numbers);
    }

}
