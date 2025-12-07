package org.quintilis.economy.exceptions;

public class ListingNotFound extends RuntimeException {
    public ListingNotFound(int id) {
        super("Listing not found with id " + id);
    }
}
