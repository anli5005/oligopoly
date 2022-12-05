package dev.anli.oligopoly.board;

import dev.anli.oligopoly.gui.Utils;
import dev.anli.oligopoly.board.card.Card;
import dev.anli.oligopoly.board.tile.Tile;
import dev.anli.oligopoly.state.Game;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * A singleton "go to jail" tile.
 */
public final class GoToJail implements Tile, Card {
    private GoToJail() {
        // Do nothing.
    }

    private final static GoToJail instance = new GoToJail();

    /**
     * Gets the singleton instance.
     */
    public static GoToJail getInstance() {
        return instance;
    }

    @Nonnull
    @Override
    public List<Action> getTileActions(@Nonnull Game game) {
        return Collections.singletonList(Action.make("Accept Fate", () -> {
            game.getCurrentPlayer().sendToJail(game.getBoard());
            game.endTurn();
        }));
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
            "Go to Jail",
            graphics,
            20,
            25,
            Tile.SIDE_TILE_SIZE.height - 40
        );
    }

    @Nonnull
    @Override
    public String getTitle() {
        return "Go to Jail";
    }

    @Nonnull
    @Override
    public String getCardDescription() {
        return "Go directly to Jail. Do not pass GO. Do not collect $200.";
    }

    @Nonnull
    @Override
    public List<Action> getCardActions(@Nonnull Game game) {
        return getTileActions(game);
    }
}
