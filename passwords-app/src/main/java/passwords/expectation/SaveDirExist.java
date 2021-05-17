package passwords.expectation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import passwords.settings.InputParameters;

public class SaveDirExist implements Expectation {

    private final Logger logger = LoggerFactory.getLogger(SaveDirExist.class);

    @Override
    public boolean initiative() {
        return true;
    }

    @Override
    public String question() {
        return "Data dir does not exist ? create ?";
    }

    @Override
    public boolean control() {
        logger.info("Verify if the data directory exists...");
        return FileUtils.isDirectoryAndExist(InputParameters.SAVE_DIR.getPropertyPath());
    }

    @Override
    public void action() {
        logger.info("Creating data directory...");
        FileUtils.createDirectory(InputParameters.SAVE_DIR.getPropertyPath());
    }
}
