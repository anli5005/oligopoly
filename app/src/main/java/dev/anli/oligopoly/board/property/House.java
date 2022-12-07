package dev.anli.oligopoly.board.property;

import dev.anli.oligopoly.board.Item;
import dev.anli.oligopoly.state.Game;

import javax.annotation.Nonnull;

/**
 * Singleton item representing a house.
 */
public final class House implements Item {
    /**
     * Suggested ID for houses.
     */
    public static final String ID = "HOUSE";

    private House() {
        // Do nothing.
    }

    @Nonnull
    @Override
    public String getItemDescription(@Nonnull String id, @Nonnull Game game) {
        return "Congratulations, you've broken the game! No, you can't place these on the board " +
            "to celebrate.";
    }

    private static final House instance = new House();

    /**
     * Get the singleton instance.
     */
    public static House getInstance() {
        return instance;
    }

    @Nonnull
    @Override
    public String getName() {
        return "House";
    }

    @Override
    public int getOrder() {
        return 3;
    }

    @Override
    public boolean isFungible() {
        return true;
    }
}
