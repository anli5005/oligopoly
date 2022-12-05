package dev.anli.oligopoly.board.property;

import dev.anli.oligopoly.gui.Utils;
import dev.anli.oligopoly.board.Action;
import dev.anli.oligopoly.board.Item;
import dev.anli.oligopoly.state.Game;
import dev.anli.oligopoly.state.Items;
import dev.anli.oligopoly.state.Player;
import dev.anli.oligopoly.state.PropertyState;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A property in the game.
 */
public interface Property extends Item {
    /**
     * Returns the name of the property.
     * @return name of the property
     */
    @Nonnull String getName();

    @Nonnull
    @Override
    default String getItemDescription(@Nonnull String id, @Nonnull Game game) {
        return game.findPlayerForItem(id).map(player -> String.format(
            "%s by Player %d",
            game.getPropertyState(id).isMortgaged() ? "Mortgaged" : "Owned",
            player.getNumber() + 1
        )).orElse("Unowned");
    }

    @Nonnull
    @Override
    default List<Action> getItemActions(@Nonnull String id, @Nonnull Game game) {
        PropertyState state = game.getPropertyState(id);
        return List.of(
            state.mortgageAction(this),
            state.unmortgageAction(this)
        );
    }

    @Override
    default int getOrder() {
        return getCategory().order() + 100;
    }

    /**
     * Returns the category of the property.
     * @return category of the property
     */
    @Nonnull PropertyCategory getCategory();

    /**
     * Returns whether the property is fungible. Typically, properties are not fungible.
     * @return whether the property is fungible
     */
    @Override
    default boolean isFungible() {
        return false;
    }

    @Override
    default void drawIcon(Graphics2D graphics, String id, Game game) {
        PropertyState state = game.getPropertyState(id);
        if (state.isMortgaged()) {
            graphics.setColor(Color.PINK);
            graphics.fillRect(0, 0, 100, 100);
        }

        graphics.rotate(Math.PI / 24, 50, 50);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(30, 20, 40, 60);

        PropertyCategory category = getCategory();
        graphics.setColor(new Color(category.red(), category.green(), category.blue()));
        graphics.fillRect(30, 20, 40, 15);

        graphics.setColor(Color.BLACK);
        graphics.setStroke(new BasicStroke(4));
        graphics.drawRect(30, 20, 40, 60);
        graphics.drawLine(30, 35, 70, 35);

        String[] words = getName().split(" ");
        if (words.length > 0) {
            Utils.drawStringWrapped(words[0], graphics, 30, 40, 40);
        }
    }

    /**
     * Returns the buy price of the property.
     * @return buy price of the property
     */
    @Nonnull
    Items getBuyPrice();

    /**
     * Returns the mortgage price of the property.
     * @return mortgage price of the property
     */
    @Nonnull Items getMortgagePrice();

    /**
     * Calculates and returns the rent to charge a player on the property.
     * @param state state of the property
     * @param game current game
     * @return rent to charge a player
     */
    @Nonnull Items getRent(@Nonnull PropertyState state, @Nonnull Player owner, @Nonnull Game game);

    @Nonnull
    @Override
    default List<String> getDisplayStats(@Nonnull String id, @Nonnull Game game) {
        Optional<Player> player = game.findPlayerForItem(id);
        String categoryStat = String.format("Category: %s", getCategory().name());
        return player.map(owner -> {
            PropertyState state = game.getPropertyState(id);
            return List.of(categoryStat, String.format(
                "Current Rent: %s",
                getRent(state, owner, game).format(game.getBoard())
            ));
        }).orElse(Collections.singletonList(categoryStat));
    }
}
