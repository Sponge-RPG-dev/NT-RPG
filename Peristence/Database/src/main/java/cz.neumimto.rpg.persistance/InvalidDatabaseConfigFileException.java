package cz.neumimto.rpg.persistance;

public class InvalidDatabaseConfigFileException extends RuntimeException {
    public InvalidDatabaseConfigFileException(String message) {
        super(message);
    }
}
