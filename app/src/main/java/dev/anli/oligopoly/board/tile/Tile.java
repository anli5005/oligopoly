package dev.anli.oligopoly.board.tile;

import dev.anli.oligopoly.board.Action;
import dev.anli.oligopoly.state.Game;
import dev.anli.oligopoly.state.Player;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * A tile on the board.
 */
public interface Tile {
    /**
     * Gets a list of actions that can be taken on the tile.
     * <p>
     * The default implementation lets the player move or end their turn, depending on the phase.
     * @param game current game
     */
    @Nonnull
    default List<Action> getTileActions(@Nonnull Game game) {
        if (game.isPostMove()) {
            return Collections.singletonList(game.endTurnAction());
        } else {
            return Collections.singletonList(game.moveAction());
        }
    }

    /**
     * Updates the game state accordingly whenever this tile is passed over.
     * <p>
     * This method is not called when a player lands on the tile. The default implementation just
     * does nothing.
     *
     * @param game current game
     */
    default void onPass(@Nonnull Game game) {
        // Do nothing.
    }

    /**
     * Updates the game state accordingly whenever this tile is landed on.
     * <p>
     * The default implementation just does nothing.
     *
     * @param game current game
     */
    default void onLand(@Nonnull Game game) {
        // Do nothing.
    }

    /**
     * Draws the tile as a side tile.
     * @param graphics graphics context to draw the tile in
     * @param game current game
     */
    default void drawSideTile(@Nonnull Graphics2D graphics, @Nonnull Game game) {
        graphics.setColor(Color.BLACK);
        graphics.drawString(getClass().getSimpleName(), 0, 20);
    }

    /**
     * Draws the tile as a corner tile. (oriented so that the next tile is to the left.)
     * @param graphics graphics context to draw the tile in
     * @param game current game
     */
    default void drawCornerTile(@Nonnull Graphics2D graphics, @Nonnull Game game) {
        graphics.setColor(Color.BLACK);
        graphics.drawString(getClass().getSimpleName(), 0, 20);
    }

    /**
     * Gets the recommended location to draw a given player on the tile as a side tile.
     */
    default Point suggestSidePlayerLocation(@Nonnull Player player, @Nonnull Game game) {
        return new Point(Tile.SIDE_TILE_SIZE.width / 2, Tile.SIDE_TILE_SIZE.height / 2);
    }

    /**
     * Gets the recommended location to draw a given player on the tile as a corner tile.
     */
    default Point suggestCornerPlayerLocation(@Nonnull Player player, @Nonnull Game game) {
        return new Point(Tile.SIDE_TILE_SIZE.height / 2, Tile.SIDE_TILE_SIZE.height / 2);
    }

    /**
     * The size of a side tile.
     */
    Dimension SIDE_TILE_SIZE = new Dimension(54, 90);
}