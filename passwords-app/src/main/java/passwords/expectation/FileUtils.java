package passwords.expectation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileUtils {
    private FileUtils() {

    }

    public static String readFile(Path path) throws IOException {
        return Files.readString(path);
    }

    public static boolean isDirectoryAndExist(String path) {
        return isDirectoryAndExist(Paths.get(path));
    }

    public static boolean isDirectoryAndExist(Path path) {
        return Files.isDirectory(path);
    }

    public static boolean isFileExist(Path path) {
        return Files.isRegularFile(path);
    }

    public static boolean createDirectory(String path) {
        return createDirectory(Paths.get(path));
    }

    public static boolean createDirectory(Path path) {
        try {
            Files.createDirectory(path);
        } catch (IOException e) {
            e.printStackTrace();
            // log + warn dialog
            return false;
        }
        return true;
    }

}
