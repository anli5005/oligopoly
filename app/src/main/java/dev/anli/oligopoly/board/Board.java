package dev.anli.oligopoly.board;

import dev.anli.oligopoly.board.tile.Tile;
import dev.anli.oligopoly.state.Items;

import java.util.*;
import java.util.function.Predicate;

/**
 * An Oligopoly board.
 */
public record Board(String name, List<Tile> tiles, Map<String, Item> items, Items startItems) {
    /**
     * Gets the item for the given item ID. Returns an UnknownItem if the item is not found.
     *
     * @param itemId item ID
     * @return the item
     */
    public Item getItem(String itemId) {
        if (items.containsKey(itemId)) {
            return items.get(itemId);
        } else {
            return new UnknownItem(itemId);
        }
    }

    /**
     * Finds the location of the first tile matching the given predicate.
     * @return an int optional representing the location
     */
    public OptionalInt findLocation(Predicate<Tile> predicate) {
        for (int i = 0; i < tiles().size(); i++) {
            if (predicate.test(tiles().get(i))) {
                return OptionalInt.of(i);
            }
        }

        return OptionalInt.empty();
    }
}
