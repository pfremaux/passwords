package passwords.commandline.v1;

import commons.lib.main.console.ConsoleFactory;
import commons.lib.main.console.CustomConsole;
import commons.lib.main.os.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import passwords.gui.CredentialSettingsManager;
import passwords.settings.CredentialsSettings;
import passwords.settings.InputParameters;

import java.io.Console;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChooseNumberCommandLine {
    private final Logger logger = LoggerFactory.getLogger(ChooseNumberCommandLine.class);

    public Optional<CredentialsSettings> readAndGetCredentialsSettings() {
        final CustomConsole customConsole = ConsoleFactory.getInstance();
        if (customConsole == null) {
            LogUtils.debug("No console instance");
            return Optional.empty();
        } else {
            List<String> strPwds = new ArrayList<>();
            List<Integer> numbersAuth = new ArrayList<>();
            customConsole.printf("How many passwords ?\n");
            int numberAuth = -1;
            while (numberAuth < 0) {
                customConsole.printf("Please enter a positive value. (>= 0)\n");
                numberAuth = readInt(customConsole);
            }
            // TODO choisir entre les 2 methodes car on a un legacy
            char[][] pwds = new char[numberAuth][];
            for (int i = 0; i < numberAuth; i++) {
                customConsole.printf("Password n.%d ", i);
                pwds[i] = customConsole.readPassword();
                String pwd = new String(pwds[i]);
                LogUtils.debug("You wrote -{}-", pwd);
                strPwds.add(pwd);
            }
            String[] combinations;
            boolean allInteger = false;

            while (!allInteger) {
                // TODO plutot les passer en password pour plus de config
                customConsole.printf("All the auth values must be integers in the range set in your configuration.\n");
                customConsole.printf("Please enter the %d values separated by a comma. (5,2,15,...)\n", numberAuth);
                String s = customConsole.readLine();
                combinations = s.split(",");
                allInteger = true;
                for (String combination : combinations) {
                    try {
                        numbersAuth.add(Integer.valueOf(combination));
                    } catch (Exception e) {
                        allInteger = false;
                        break;
                    }
                }
            }
            final Path keysPath = InputParameters.KEYS_DIR.getPropertyPath();
            CredentialSettingsManager credentialSettingsManager = new CredentialSettingsManager();
            return Optional.of(credentialSettingsManager.getCredentialsSettings(keysPath, strPwds, numbersAuth));
        }
    }

    public static int readInt(CustomConsole console) {
        String s;
        int result = -99;
        while (result == -99) {
            try {
                s = console.readLine();
                result = Integer.parseInt(s);
            } catch (Exception e) {
                console.printf("Invalid number, retry");
            }
        }
        return result;
    }

}
