package passwords.expectation;


import commons.lib.extra.gui.ConfirmDialog;
import commons.lib.main.SystemUtils;
import commons.lib.main.UnrecoverableException;
import passwords.settings.InputParameters;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface Expectation {

    default Expectation[] prerequisite() {
        return new Expectation[0];
    }

    String question();

    boolean control();

    void action();

    default void resolve() {
        for (Expectation expectation : prerequisite()) {
            expectation.resolve();
        }
        if (!control()) {
            if (initiative() || ask()) {
                action();
            } else {
                throw new UnrecoverableException(
                        this.getClass().getCanonicalName() + " can't perform action.",
                        new String[]{"Can't continue th process."},
                        SystemUtils.EXIT_SYSTEM_ERROR);
            }
        }
    }

    default boolean initiative() {
        return false;
    }

    default boolean ask() {
        if (Boolean.parseBoolean(InputParameters.COMMAND_LINE_MODE.getPropertyString())) {
            return true;
        } else {
            final ConfirmDialog confirmDialog = new ConfirmDialog(null);
            final CompletableFuture<Boolean> waitingAnswer = new CompletableFuture<>();
            confirmDialog.ask(question(), waitingAnswer);
            try {
                return waitingAnswer.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new UnrecoverableException("", new String[]{"The commons.lib.privacy.pwdstore crashed."}, e, -11);
            }
        }
    }

}
