package passwords.expectation;

import commons.lib.main.os.LogUtils;
import passwords.settings.InputParameters;

import java.util.logging.Logger;

public class SaveDirExist implements Expectation {

    private final Logger logger = LogUtils.initLogs();

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
        LogUtils.debug("Verify if the data directory exists...");
        return FileUtils.isDirectoryAndExist(InputParameters.SAVE_DIR.getPropertyPath());
    }

    @Override
    public void action() {
        LogUtils.debug("Creating data directory...");
        FileUtils.createDirectory(InputParameters.SAVE_DIR.getPropertyPath());
    }
}
