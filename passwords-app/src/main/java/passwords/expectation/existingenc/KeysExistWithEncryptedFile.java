package passwords.expectation.existingenc;

import passwords.expectation.Expectation;
import passwords.expectation.KeyDirectoryExist;

public class KeysExistWithEncryptedFile implements Expectation {

    @Override
    public Expectation[] prerequisite() {
        return new Expectation[]{
                new KeyDirectoryExist()
        };
    }

    @Override
    public String question() {
        return null;
    }

    @Override
    public boolean control() {
        return false;
    }

    @Override
    public void action() {

    }
}
