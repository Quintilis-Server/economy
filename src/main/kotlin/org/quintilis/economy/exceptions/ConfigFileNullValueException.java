package org.quintilis.economy.exceptions;

public class ConfigFileNullValueException extends RuntimeException {
    public ConfigFileNullValueException(String value) {
        super("The value " + value + " is null.");
    }
}
