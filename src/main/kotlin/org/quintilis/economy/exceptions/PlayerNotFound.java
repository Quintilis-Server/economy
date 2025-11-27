package org.quintilis.economy.exceptions;

public class PlayerNotFound extends RuntimeException {
    public PlayerNotFound() {
        super("Player not found");
    }
}
