package passwords.expectation;

import java.nio.file.Path;

public class DirExist implements Expectation {

    private final Path dir;

    public DirExist(Path dir) {
        this.dir = dir;
    }

    @Override
    public Expectation[] prerequisite() {
        return new Expectation[0];
    }

    @Override
    public String question() {
        return "Répertoire manquant. Voulez vous créer le répertoire ?";
    }

    @Override
    public boolean control() {
        return FileUtils.isDirectoryAndExist(dir);
    }

    @Override
    public void action() {
        FileUtils.createDirectory(dir);
    }

    @Override
    public void resolve() {

    }

    @Override
    public boolean initiative() {
        return false;
    }

    @Override
    public boolean ask() {
        return false;
    }

}
