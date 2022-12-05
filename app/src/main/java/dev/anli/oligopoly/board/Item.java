package dev.anli.oligopoly.board;

import dev.anli.oligopoly.gui.Utils;
import dev.anli.oligopoly.state.Game;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * An item that can be owned by a player or property.
 */
public interface Item {
    /**
     * Returns the name of the item.
     * @return name of the item
     */
    @Nonnull String getName();

    /**
     * Returns the description of the item.
     * @param id ID of the item
     * @param game current game
     * @return description of the item
     */
    @Nonnull
    default String getItemDescription(@Nonnull String id, @Nonnull Game game) {
        return "";
    }

    /**
     * Returns the stats of an item for display.
     * @param id ID of the item
     * @param game current game
     * @return stats for the item, one entry per line
     */
    @Nonnull
    default List<String> getDisplayStats(@Nonnull String id, @Nonnull Game game) {
        return Collections.emptyList();
    }

    /**
     * Gets a list of actions that can be taken while holding this item.
     * @param id ID of the item
     * @param game current game
     */
    @Nonnull
    default List<Action> getItemActions(@Nonnull String id, @Nonnull Game game) {
        return Collections.emptyList();
    }

    /**
     * Returns the lexicographic order of the item.
     */
    int getOrder();

    /**
     * Formats a given quantity of item into a string.
     * @param quantity quantity to format
     * @return formatted quantity
     */
    @Nonnull
    default String formatQuantity(int quantity) {
        if (isFungible()) {
            return String.format("%dx %s", quantity, getName());
        } else {
            return getName();
        }
    }

    /**
     * Returns whether the item is fungible (i.e. a user can own multiple of the item.)
     * @return whether the item is fungible
     */
    boolean isFungible();

    /**
     * Size for item icons.
     */
    Dimension ICON_SIZE = new Dimension(100, 100);

    /**
     * Draws the item's icon.
     *
     * @param graphics context to draw into
     * @param id ID of the item
     * @param game current game
     */
    default void drawIcon(Graphics2D graphics, String id, Game game) {
        graphics.setColor(Color.BLACK);
        Utils.drawStringWrapped(getName(), graphics, 5, 5, ICON_SIZE.width - 10);
    }
}
