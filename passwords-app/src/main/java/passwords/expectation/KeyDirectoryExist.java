package passwords.expectation;

import passwords.settings.InputParameters;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class KeyDirectoryExist implements Expectation {

    @Override
    public Expectation[] prerequisite() {
        return new Expectation[]{};
    }

    @Override
    public boolean initiative() {
        return true;
    }

    @Override
    public String question() {
        // TODO bundle question.directory.keys.missing
        return "Répertoire des clés manquant. Voulez vous créer le répertoire ?";
    }

    @Override
    public boolean control() {
        return FileUtils.isDirectoryAndExist(Paths.get(InputParameters.KEYS_DIR.getPropertyString()));
    }

    @Override
    public void action() {
        final Path path = Paths.get(InputParameters.KEYS_DIR.getPropertyString());
        FileUtils.createDirectory(path);
    }
}
