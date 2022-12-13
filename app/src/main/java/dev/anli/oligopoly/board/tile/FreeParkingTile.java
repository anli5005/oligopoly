package dev.anli.oligopoly.board.tile;

import dev.anli.oligopoly.gui.Utils;
import dev.anli.oligopoly.state.Game;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * A singleton free parking tile.
 */
public final class FreeParkingTile implements Tile {
    private FreeParkingTile() {
        // Do nothing.
    }

    private final static FreeParkingTile INSTANCE = new FreeParkingTile();

    /**
     * Gets the singleton instance.
     */
    public static FreeParkingTile getInstance() {
        return INSTANCE;
    }

    @Override
    public void drawCornerTile(@Nonnull Graphics2D graphics, @Nonnull Game game) {
        graphics.rotate(
            -Math.PI / 4,
            Tile.SIDE_TILE_SIZE.height / 2.0,
            Tile.SIDE_TILE_SIZE.height / 2.0
        );

        graphics.setColor(Color.BLACK);
        graphics.setFont(graphics.getFont().deriveFont(Font.BOLD));

        Utils.drawStringWrapped(
            "Free Parking",
            graphics,
            10,
            25,
            Tile.SIDE_TILE_SIZE.height - 20
        );
    }
}
