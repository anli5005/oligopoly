package dev.anli.oligopoly.board.tile;

import dev.anli.oligopoly.gui.Utils;
import dev.anli.oligopoly.state.Game;
import dev.anli.oligopoly.state.Items;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * A tile that provides a reward when it is passed over or landed on.
 * @param reward reward to grant on passing or landing
 */
public record GoTile(Items reward) implements Tile {
    @Override
    public void onPass(@Nonnull Game game) {
        game.getCurrentPlayer().getItems().add(reward);
    }

    @Override
    public void onLand(@Nonnull Game game) {
        onPass(game);
    }

    @Override
    public void drawCornerTile(@Nonnull Graphics2D graphics, @Nonnull Game game) {
        graphics.setColor(Color.BLACK);
        graphics.setFont(graphics.getFont().deriveFont(Font.PLAIN, 7));
        Utils.drawStringWrapped(
            String.format("Collect %s salary as you pass", reward.format(game.getBoard())),
            graphics,
            10,
            15,
            Tile.SIDE_TILE_SIZE.height - 20
        );

        graphics.setColor(Color.RED);
        graphics.setFont(graphics.getFont().deriveFont(Font.BOLD, 36));
        //noinspection SuspiciousNameCombination
        Utils.drawStringWrapped(
            "GO",
            graphics,
            0,
            30,
            Tile.SIDE_TILE_SIZE.height
        );

        graphics.setColor(Color.GRAY);
        graphics.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawLine(10, 75, Tile.SIDE_TILE_SIZE.height - 10,  75);
        graphics.drawLine(10, 75, 15, 70);
        graphics.drawLine(10, 75, 15, 80);
    }
}
