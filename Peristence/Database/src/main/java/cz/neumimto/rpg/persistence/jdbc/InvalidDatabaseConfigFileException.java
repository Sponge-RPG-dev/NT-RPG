package cz.neumimto.rpg.persistence.jdbc;

public class InvalidDatabaseConfigFileException extends RuntimeException {
    public InvalidDatabaseConfigFileException(String message) {
        super(message);
    }
}
