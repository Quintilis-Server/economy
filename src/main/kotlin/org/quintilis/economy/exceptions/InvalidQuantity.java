package org.quintilis.economy.exceptions;

public class InvalidQuantity extends RuntimeException {
    public InvalidQuantity(int quantity, int listingId) {
        super("Invalid quantity: "+ quantity + " for listing " + listingId);
    }
}
