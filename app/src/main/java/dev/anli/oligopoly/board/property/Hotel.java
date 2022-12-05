package dev.anli.oligopoly.board.property;

import dev.anli.oligopoly.board.Item;

import javax.annotation.Nonnull;

/**
 * Singleton item representing a hotel.
 */
public final class Hotel implements Item {
    /**
     * Suggested ID for hotels.
     */
    public static final String ID = "HOTEL";

    private Hotel() {
        // Do nothing.
    }

    private static final Hotel instance = new Hotel();

    /**
     * Get the singleton instance.
     */
    public static Hotel getInstance() {
        return instance;
    }

    @Nonnull
    @Override
    public String getName() {
        return "Hotel";
    }

    @Override
    public int getOrder() {
        return 3;
    }

    @Override
    public boolean isFungible() {
        return false;
    }
}
