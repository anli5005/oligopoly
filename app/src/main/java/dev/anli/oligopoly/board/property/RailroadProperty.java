package dev.anli.oligopoly.board.property;

import dev.anli.oligopoly.state.Game;
import dev.anli.oligopoly.state.Items;
import dev.anli.oligopoly.state.Player;
import dev.anli.oligopoly.state.PropertyState;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * A property representing a railroad.
 * @param name name of the property
 * @param buyPrice price of the property
 * @param mortgagePrice mortgage value of the property
 * @param rent list of rents, corresponding to number of railroads owned (starting at 1)
 */
public record RailroadProperty(
    @Nonnull String name,
    @Nonnull Items buyPrice,
    @Nonnull Items mortgagePrice,
    @Nonnull List<Items> rent
) implements Property {
    /**
     * The category for all railroads.
     */
    public static final PropertyCategory CATEGORY = new PropertyCategory(
        "Railroad", 998, 0, 0, 0
    );

    @Nonnull
    @Override
    public String getName() {
        return name();
    }

    @Nonnull
    @Override
    public PropertyCategory getCategory() {
        return CATEGORY;
    }

    @Nonnull
    @Override
    public Items getBuyPrice() {
        return buyPrice;
    }

    @Nonnull
    @Override
    public Items getMortgagePrice() {
        return mortgagePrice;
    }

    @Nonnull
    @Override
    public Items getRent(@Nonnull PropertyState state, @Nonnull Player owner, @Nonnull Game game) {
        if (rent.isEmpty()) {
            return new Items();
        }

        int count = owner.countProperties(CATEGORY, game.getBoard());
        return rent.get(Math.max(0, Math.min(rent.size() - 1, count - 1)));
    }

    @Nonnull
    @Override
    public List<String> getDisplayStats(@Nonnull String id, @Nonnull Game game) {
        List<String> stats = new ArrayList<>(Property.super.getDisplayStats(id, game));
        for (int i = 0; i < rent.size(); i++) {
            stats.add(String.format("w/ %d owned: %s", i + 1, rent.get(i).format(game.getBoard())));
        }
        return stats;
    }
}
