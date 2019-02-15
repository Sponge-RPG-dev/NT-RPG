package cz.neumimto.rpg.players;

import org.spongepowered.api.text.Text;

public class ActionResult {

    private final boolean ok;
    private Text errorMesage;

    private ActionResult(boolean ok, Text errorMesage) {
        this.ok = ok;
        this.errorMesage = errorMesage;
    }

    public ActionResult(boolean b) {
        this.ok = b;
    }

    public boolean isOk() {
        return ok;
    }

    public Text getErrorMesage() {
        return errorMesage;
    }

    public static ActionResult withErrorMessage(Text text) {
        return new ActionResult(false, text);
    }

    public static ActionResult ok() {
        return new ActionResult(true);
    }

}
