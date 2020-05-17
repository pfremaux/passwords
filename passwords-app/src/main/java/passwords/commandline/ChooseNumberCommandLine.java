package passwords.commandline;

import passwords.gui.CredentialSettingsManager;
import passwords.settings.CredentialsSettings;
import passwords.settings.InputParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Console;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChooseNumberCommandLine {
    private final Logger logger = LoggerFactory.getLogger(ChooseNumberCommandLine.class);

    public Optional<CredentialsSettings> readAndGetCredentialsSettings() {
        Console console = System.console();
        if (console == null) {
            logger.info("No console instance");
            return Optional.empty();
        } else {
            List<String> strPwds = new ArrayList<>();
            List<Integer> numbersAuth = new ArrayList<>();
            console.printf("How many passwords ?\n");
            int numberAuth = 0;
            while (numberAuth <= 0) {
                console.printf("Please enter a positive value. (> 0)\n");
                numberAuth = readInt(console);
            }
            // TODO choisir entre les 2 methodes car on a un legacy
            char[][] pwds = new char[numberAuth][];
            for (int i = 0; i < numberAuth; i++) {
                console.flush();
                pwds[i] = console.readPassword("Password n.%d", i);
                String pwd = new String(pwds[i]);
                logger.info("You wrote -{}-", pwd);
                strPwds.add(pwd);
            }
            String[] combinations;
            boolean allInteger = false;
            console.flush();
            while (!allInteger) {
                // TODO plutot les passer en password pour plus de confid
                console.printf("All the auth values must be integers in the range set in your configuration.\n");
                console.printf("Please enter the %d values separated by a comma. (5,2,15,...)\n", numberAuth);
                String s = console.readLine();
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

    public static int readInt(Console console) {
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
