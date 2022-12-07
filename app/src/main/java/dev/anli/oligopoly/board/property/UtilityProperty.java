package dev.anli.oligopoly.board.property;

import dev.anli.oligopoly.board.Board;
import dev.anli.oligopoly.state.Game;
import dev.anli.oligopoly.state.Items;
import dev.anli.oligopoly.state.Player;
import dev.anli.oligopoly.state.PropertyState;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * A property representing a utility.
 * @param name name of the property
 * @param buyPrice price of the property
 * @param mortgagePrice mortgage value of the property
 * @param baseRent base multiplier for rent
 * @param monopolyRent multiplier for rent when all utilities are owned
 */
public record UtilityProperty(
    String name,
    Items buyPrice,
    Items mortgagePrice,
    Items baseRent,
    Items monopolyRent
) implements Property {
    /**
     * The category for all utilities.
     */
    public static final PropertyCategory CATEGORY = new PropertyCategory(
        "Utility", 999, 255, 255, 255
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

    private Items getUnmultipliedRent(@Nonnull Player owner, @Nonnull Board board) {
        return owner.hasMonopoly(CATEGORY, board) ? monopolyRent : baseRent;
    }

    @Nonnull
    @Override
    public Items getRent(@Nonnull PropertyState state, @Nonnull Player owner, @Nonnull Game game) {
        List<List<Integer>> rolls = game.getDiceRolls();
        if (rolls.isEmpty()) {
            return new Items();
        } else {
            Items unmultipliedRent = getUnmultipliedRent(owner, game.getBoard());
            int multiplier = Game.getDiceSum(rolls.get(rolls.size() - 1));
            return new Items(unmultipliedRent, qty -> qty * multiplier);
        }
    }

    @Nonnull
    @Override
    public List<String> getDisplayStats(@Nonnull String id, @Nonnull Game game) {
        List<String> stats = new ArrayList<>();
        stats.add("Category: Utility");

        game.findPlayerForItem(id).ifPresent(owner -> stats.add(String.format(
            "Current Rent: %s times dice roll",
            getUnmultipliedRent(owner, game.getBoard()).format(game.getBoard())
        )));

        stats.add(String.format("Base Rent: %s times dice roll", baseRent.format(game.getBoard())));
        stats.add(String.format(
            "w/ all utilities: %s times dice roll", monopolyRent.format(game.getBoard()))
        );
        return stats;
    }
}
