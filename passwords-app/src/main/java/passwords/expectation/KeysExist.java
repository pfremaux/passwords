package passwords.expectation;


import commons.lib.security.asymetric.AsymmetricKeyHandler;
import commons.lib.security.asymetric.PrivateKeyHandler;
import commons.lib.security.asymetric.PublicKeyHandler;
import passwords.settings.InputParameters;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public class KeysExist implements Expectation {

    @Override
    public Expectation[] prerequisite() {
        return new Expectation[]{new KeyDirectoryExist()};
    }

    @Override
    public String question() {
        return "Clés manquantes voulez vous les générer ?";
    }

    @Override
    public boolean control() {
        final String keypairsProperty = InputParameters.NBR_KEYPAIRS.getPropertyString();
        final int nbrKeyPairs = Integer.parseInt(keypairsProperty);
        for (int i = 0; i < nbrKeyPairs; i++) {
            final Path fullPathPublicKey = getFullPathPublicKey(i);
            final Path fullPathPrivateKey = getFullPathPrivateKey(i);
            if (!FileUtils.isFileExist(fullPathPublicKey)) {
                return false;
            }
            if (!FileUtils.isFileExist(fullPathPrivateKey)) {
                return false;
            }
        }
        return true;
    }

    private Path getFullPathPublicKey(int index) {
        final String pvKeyExtension = InputParameters.PUBLIC_FILE_EXTENSION.getPropertyString();
        return Paths.get(
                InputParameters.KEYS_DIR.getPropertyString(),
                String.format("%s." + pvKeyExtension, index));
    }

    private Path getFullPathPrivateKey(int index) {
        final String pvKeyExtension = InputParameters.PRIVATE_FILE_EXTENSION.getPropertyString();

        return Paths.get(
                InputParameters.KEYS_DIR.getPropertyString(),
                String.format("%s." + pvKeyExtension, index));
    }

    @Override
    public void action() {
        final String keypairsProperty = InputParameters.NBR_KEYPAIRS.getPropertyString();
        final int nbrKeyPairs = Integer.parseInt(keypairsProperty);
        for (int i = 0; i < nbrKeyPairs; i++) {
            try {
                final KeyPair keyPair = AsymmetricKeyHandler.createPair();
                final PublicKeyHandler publicKeyHandler = new PublicKeyHandler();
                final Path fullPathPublicKey = getFullPathPublicKey(i);
                publicKeyHandler.save(fullPathPublicKey.toFile().getAbsolutePath(), keyPair.getPublic());
                final PrivateKeyHandler privateKeyHandler = new PrivateKeyHandler();
                final Path fullPathPrivateKey = getFullPathPrivateKey(i);
                privateKeyHandler.save(fullPathPrivateKey.toFile().getAbsolutePath(), keyPair.getPrivate());
            } catch (NoSuchAlgorithmException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
