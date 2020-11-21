package cz.neumimto.rpg.api.utils;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ActionResult {

    private static final ActionResult OK = new ActionResult(true);
    private static final ActionResult NOK = new ActionResult(false);

    private final boolean ok;
    private Supplier<String> msgSupplier;
    private String message;

    private ActionResult(boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }

    private ActionResult(boolean ok, Supplier<String> message) {
        this.ok = ok;
        this.msgSupplier = message;
    }

    public ActionResult(boolean b) {
        this.ok = b;
    }

    public static ActionResult ok(String text) {
        return new ActionResult(true, text);
    }

    public static ActionResult nok() {
        return NOK;
    }

    public boolean isOk() {
        return ok;
    }

    public String getMessage() {
        return message == null ? msgSupplier.get() : message;
    }

    public static ActionResult withErrorMessage(String text) {
        return new ActionResult(false, text);
    }

    public static ActionResult withErrorMessage(Supplier<String> text) {
        return new ActionResult(false, text);
    }

    public static ActionResult ok() {
        return OK;
    }

}
