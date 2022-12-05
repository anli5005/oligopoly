package dev.anli.oligopoly.board.tile;

import dev.anli.oligopoly.gui.Utils;
import dev.anli.oligopoly.board.Action;
import dev.anli.oligopoly.board.card.Card;
import dev.anli.oligopoly.state.Game;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public record CardTile(String name, List<Card> cards) implements Tile {
    @Nonnull
    @Override
    public List<Action> getTileActions(@Nonnull Game game) {
        if (game.isPostMove() && !cards.isEmpty()) {
            return Collections.singletonList(Action.make("Draw Card", () -> {
                if (cards.isEmpty()) {
                    game.endTurn();
                    return;
                }

                Card card = cards.get((int) (Math.random() * cards.size()));
                game.setCurrentCard(card);
            }));
        } else {
            return Tile.super.getTileActions(game);
        }
    }

    @Override
    public void drawSideTile(@Nonnull Graphics2D graphics, @Nonnull Game game) {
        graphics.setFont(graphics.getFont().deriveFont(8.0F));
        graphics.setColor(Color.BLACK);
        Utils.drawStringWrapped(name, graphics, 5, 10, Tile.SIDE_TILE_SIZE.width - 10);
    }
}
