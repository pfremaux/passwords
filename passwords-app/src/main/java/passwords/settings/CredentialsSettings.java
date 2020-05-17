package passwords.settings;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

public final class CredentialsSettings {
    private final List<PrivateKey> privateKeys;
    private final List<PublicKey> publicKeys;
    private final List<String> passwords;
    private final List<Integer> numbers;

    public CredentialsSettings(List<PrivateKey> privateKeys, List<PublicKey> publicKeys, List<String> passwords, List<Integer> numbers) {
        this.privateKeys = privateKeys;
        this.publicKeys = publicKeys;
        this.passwords = passwords;
        this.numbers = numbers;
    }

    public List<PrivateKey> getPrivateKeys() {
        return privateKeys;
    }

    public List<PublicKey> getPublicKeys() {
        return publicKeys;
    }

    public List<String> getPasswords() {
        return passwords;
    }

    public List<Integer> getNumbers() {
        return numbers;
    }
}
