package org.quintilis.economy.exceptions;

public class ListingNotActive extends RuntimeException {
    public ListingNotActive(int id) {
        super("Listing not active with id " + id);
    }
}
