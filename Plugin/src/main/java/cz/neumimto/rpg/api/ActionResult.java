package cz.neumimto.rpg.api;

public class ActionResult {

    private final boolean ok;
    private String errorMesage;

    private ActionResult(boolean ok, String errorMesage) {
        this.ok = ok;
        this.errorMesage = errorMesage;
    }

    public ActionResult(boolean b) {
        this.ok = b;
    }

    public boolean isOk() {
        return ok;
    }

    public String getErrorMesage() {
        return errorMesage;
    }

    public static ActionResult withErrorMessage(String text) {
        return new ActionResult(false, text);
    }

    public static ActionResult ok() {
        return new ActionResult(true);
    }

}
