package dev.anli.oligopoly.board;

import javax.annotation.Nonnull;

/**
 * A placeholder item for use if an item ID is unknown.
 */
public record UnknownItem(String id) implements Item {
    @Nonnull
    @Override
    public String getName() {
        return String.format("Bug! (%s)", id);
    }

    @Override
    public int getOrder() {
        return 100000;
    }

    @Override
    public boolean isFungible() {
        return false;
    }
}
