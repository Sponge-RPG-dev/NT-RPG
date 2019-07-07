package cz.neumimto.rpg.api.utils;

public class ActionResult {

    private final boolean ok;
    private String message;

    private ActionResult(boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }

    public ActionResult(boolean b) {
        this.ok = b;
    }

    public static ActionResult ok(String text) {
        return new ActionResult(true, text);
    }

    public static ActionResult nok() {
        return new ActionResult(false);
    }

    public boolean isOk() {
        return ok;
    }

    public String getMessage() {
        return message;
    }

    public static ActionResult withErrorMessage(String text) {
        return new ActionResult(false, text);
    }

    public static ActionResult ok() {
        return new ActionResult(true);
    }

}
