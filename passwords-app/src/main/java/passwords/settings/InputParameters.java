package passwords.settings;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Deprecated
public enum InputParameters {
    PUBLIC_FILE_EXTENSION("public.key.ext", "pub", "--pbext"),
    PRIVATE_FILE_EXTENSION("private.key.ext", "priv", "--pvext"),
    ENCRYPTED_FILENAME("encrypted.file.name", "enc", "-o"),
    DECRYPT_VERSION("decrypt.version", "5", "--dcv"),
    ENCRYPT_VERSION("encrypt.version", "5", "--ecv"),
    FILE_DATUM_SEPARATOR("file.datum.separator", ";", "--fsep"),
    NBR_KEYPAIRS("keypairs.number", "10", "--nkp"),
    SAVE_DIR("saveRecursivly.directory", ".", "--sdir"),
    KEYS_DIR("keys.directory", "D:\\keys", "--kdir"),
    COMMAND_LINE_MODE("mode.cli", "false", "--cli"),
    LISTENING_PORT("server.listening.port", "0", "--lp"),
    DISTANT_SERVER_HOSTNAME("server.distant.hostname", "", "--sh"),
    DISTANT_SERVER_PORT("server.distant.port", "0", "--sp"),
    CONSOLE_INPUT("console.input", "not set", "--con");

    String key;
    String defaultValue;
    String commandLineKey;

    InputParameters(String key, String defaultValue, String commandLineKey) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.commandLineKey = commandLineKey;
    }

    public String getPropertyString() {
        return System.getProperty(key, defaultValue);
    }

    public static String getPropertyString(InputParameters input) {
        return System.getProperty(input.key, input.defaultValue);
    }

    public int getPropertyInt() {
        return Integer.parseInt(System.getProperty(key, defaultValue));
    }

    public Path getPropertyPath() {
        return Paths.get(System.getProperty(key, defaultValue));
    }

    public static Optional<InputParameters> fromCommandLineKey(String cmdLine) {
        for (InputParameters parameter : InputParameters.values()) {
            if (parameter.getCommandLineKey().equals(cmdLine)) {
                return Optional.of(parameter);
            }
        }
        return Optional.empty();
    }

    public static String toPropertiesFileFormat() {
        final StringBuilder buffer = new StringBuilder();
        for (InputParameters inputParameter : values()) {
            String keyName = inputParameter.key;
            String value = getPropertyString(inputParameter);
            buffer.append(keyName);
            buffer.append(" = ");
            buffer.append(value);
            buffer.append("\n");
        }
        return buffer.toString();
    }

    public static String toCommandLineFormat() {
        final StringBuilder buffer = new StringBuilder();
        for (InputParameters inputParameter : values()) {
            String keyName = inputParameter.commandLineKey;
            if (keyName == null) {
                continue;
            }
            String value = getPropertyString(inputParameter);
            buffer.append(keyName);
            buffer.append(" ");
            buffer.append(value);
            buffer.append(" ");
        }
        return buffer.toString();
    }

    public String getCommandLineKey() {
        return commandLineKey;
    }

    public String getKey() {
        return key;
    }
}
