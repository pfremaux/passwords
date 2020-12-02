package passwords.expectation.existingenc;

import commons.lib.extra.gui.ConfirmDialog;
import passwords.expectation.Expectation;
import passwords.expectation.FileUtils;
import passwords.expectation.KeysExist;
import passwords.expectation.SaveDirExist;
import passwords.settings.InputParameters;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class EncryptedFileExist implements Expectation {

    @Override
    public Expectation[] prerequisite() {
        return new Expectation[]{
                new SaveDirExist()
        };
    }

    @Override
    public String question() {
        return null;
    }

    @Override
    public boolean initiative() {
        return true;
    }

    @Override
    public boolean control() {
        return FileUtils.isFileExist(InputParameters.SAVE_DIR.getPropertyPath());
    }

    @Override
    public void action() {
        KeysExist keysExist = new KeysExist();
        if (!keysExist.control()) {
            ConfirmDialog confirmDialog = new ConfirmDialog(null);
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            confirmDialog.ask("One or more asymetric key is missing.\n" +
                    "Since your launching the commons.lib.privacy.pwdstore with an existing encrypted file,\n" +
                    "are you sure you want to continue ?", future);
            try {
                if (!future.get()) {
                    System.exit(-1);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
