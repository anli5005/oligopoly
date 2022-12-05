package dev.anli.oligopoly.board.tile;

import dev.anli.oligopoly.gui.Utils;
import dev.anli.oligopoly.board.Action;
import dev.anli.oligopoly.state.Game;
import dev.anli.oligopoly.state.Items;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public record TaxTile(String name, Items tax) implements Tile {
    @Nonnull
    @Override
    public List<Action> getTileActions(@Nonnull Game game) {
        if (game.isPostMove()) {
            return Collections.singletonList(new Action() {
                @Nonnull
                @Override
                public String getName() {
                    return "Pay Tax";
                }

                @Nonnull
                @Override
                public Items getCost() {
                    return tax;
                }

                @Override
                public boolean isAllowed() {
                    return true;
                }

                @Override
                public void perform() {
                    game.getCurrentPlayer().setLastCreditor(-1);
                    game.setCurrentActions(Collections.singletonList(game.endTurnAction()));
                }
            });
        } else {
            return Collections.singletonList(game.moveAction());
        }
    }

    @Override
    public void drawSideTile(@Nonnull Graphics2D graphics, @Nonnull Game game) {
        graphics.setFont(graphics.getFont().deriveFont(8.0F));
        graphics.setColor(Color.BLACK);
        Utils.drawStringWrapped(name, graphics, 5, 10, Tile.SIDE_TILE_SIZE.width - 10);
        Utils.drawStringWrapped(
            String.format("Pay %s", tax.format(game.getBoard())),
            graphics,
            0,
            70,
            Tile.SIDE_TILE_SIZE.width
        );

        graphics.rotate(
            Math.PI / 4,
            Tile.SIDE_TILE_SIZE.width / 2.0,
            Tile.SIDE_TILE_SIZE.height / 2.0
        );

        graphics.fillRect(
            Tile.SIDE_TILE_SIZE.width / 2 - 5,
            Tile.SIDE_TILE_SIZE.height / 2 - 5,
            10,
            10
        );
    }
}
