package passwords.encryption;

import commons.lib.extra.security.asymetric.AsymmetricKeyHandler;
import junit.framework.TestCase;
import org.junit.FixMethodOrder;
import passwords.pojo.CredentialDatum;
import passwords.settings.CredentialsSettings;
import passwords.settings.InputParameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@FixMethodOrder
public class RecursiveAsymetricAndSymmetricEncryption2Test extends TestCase {
    private final String strTmp = System.getProperty("java.io.tmpdir");
    private final RecursiveAsymetricAndSymmetricEncryption2 recursiveAsymetricAndSymmetricEncryption2 = new RecursiveAsymetricAndSymmetricEncryption2();
    static final List<CredentialDatum> credentials = new ArrayList<>();
    static final List<KeyPair> keyPairs = new ArrayList<>();
    static final List<PrivateKey> privateKeys = new ArrayList<>();
    static final List<PublicKey> publicKeys = new ArrayList<>();
    static final List<String> passwords = new ArrayList<>();

    static {
        try {
            keyPairs.add(AsymmetricKeyHandler.createPair());
            keyPairs.add(AsymmetricKeyHandler.createPair());
            keyPairs.add(AsymmetricKeyHandler.createPair());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        for (KeyPair keyPair : keyPairs) {
            privateKeys.add(keyPair.getPrivate());
            publicKeys.add(keyPair.getPublic());
        }

        credentials.add(new CredentialDatum("root", "http://", "login", "password", "comments"));
        credentials.add(new CredentialDatum("root > bank", "http://ca", "login1", "password", "comments"));
        credentials.add(new CredentialDatum("root > bank", "http://ce", "login2", "password", "comments"));
        credentials.add(new CredentialDatum("root > bank > blockchain", "http://bitcoin", "login3", "password", "comments"));
        credentials.add(new CredentialDatum("root > bank > blockchain", "http://ethereum", "login4", "password", "comments"));
    }

    public void test_1_Encrypt() throws IOException {
        Path path = Path.of(strTmp);
        Files.deleteIfExists(path.resolve(InputParameters.ENCRYPTED_FILENAME.getPropertyPath()));
        final CredentialsSettings settings = new CredentialsSettings(privateKeys, publicKeys, passwords, Arrays.asList(0, 1, 2));
        recursiveAsymetricAndSymmetricEncryption2.encrypt(path, credentials, settings);
    }

    public void test_2_Decrypt() {
        final CredentialsSettings settings = new CredentialsSettings(privateKeys, publicKeys, passwords, Arrays.asList(0, 1, 2));
        final List<CredentialDatum> deciphered = recursiveAsymetricAndSymmetricEncryption2.decrypt(Path.of(strTmp), settings);
        org.junit.Assert.assertFalse("Deciphered file shouldn't be empty.", deciphered.isEmpty());
        compareCredentialsData(deciphered);
    }

    private void compareCredentialsData(List<CredentialDatum> deciphered) {
        for (CredentialDatum credentialDatum : deciphered) {
            boolean found = false;
            for (CredentialDatum expectedCredential : credentials) {
                if (expectedCredential.getUrl().equals(credentialDatum.getUrl())) {
                    org.junit.Assert.assertEquals("Hierarchy must be equal for " + expectedCredential.getUrl(), expectedCredential.getHierarchy(), credentialDatum.getHierarchy());
                    org.junit.Assert.assertEquals("Logins must be equal for " + expectedCredential.getUrl(), expectedCredential.getLogin(), credentialDatum.getLogin());
                    org.junit.Assert.assertEquals("Passwords must be equal for " + expectedCredential.getUrl(), expectedCredential.getPassword(), credentialDatum.getPassword());
                    org.junit.Assert.assertEquals("Comments must be equal for " + expectedCredential.getUrl(), expectedCredential.getComments(), credentialDatum.getComments());
                    found = true;
                }
            }
            if (!found) {
                org.junit.Assert.fail(credentialDatum.getUrl() + " not found.");
            }
        }
    }
}