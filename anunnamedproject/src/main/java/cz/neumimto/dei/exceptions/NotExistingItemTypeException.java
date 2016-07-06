package cz.neumimto.dei.exceptions;

/**
 * Created by NeumimTo on 6.7.2016.
 */
public class NotExistingItemTypeException extends RuntimeException {
    public NotExistingItemTypeException(String s) {
        super("Not existing ItemType: " + s);
    }
}
