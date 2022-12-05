package dev.anli.oligopoly.board.tile;

import dev.anli.oligopoly.gui.Utils;
import dev.anli.oligopoly.board.Action;
import dev.anli.oligopoly.board.card.GetOutOfJailFreeCard;
import dev.anli.oligopoly.board.Money;
import dev.anli.oligopoly.state.Game;
import dev.anli.oligopoly.state.Items;
import dev.anli.oligopoly.state.Player;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A jail tile.
 */
public record JailTile(Items fine, Items getOutOfJailFree) implements Tile {
    /**
     * Creates a jail tile with defaults from the Monopoly game.
     */
    public JailTile() {
        this(new Items(Money.ID, 50), new Items(GetOutOfJailFreeCard.ID, 1));
    }

    private List<Action> getJailedActions(boolean didRoll, @Nonnull Game game) {
        List<Action> actions = new ArrayList<>();

        boolean canSkip = game.getCurrentPlayer().getTurnsInJail() < 3;

        actions.add(new Action() {
            @Nonnull
            @Override
            public String getName() {
                return "Use Card";
            }

            @Nonnull
            @Override
            public Items getCost() {
                return getOutOfJailFree;
            }

            @Override
            public boolean isAllowed() {
                return game.getCurrentPlayer().getItems().has(getOutOfJailFree);
            }

            @Override
            public void perform() {
                game.getCurrentPlayer().releaseFromJail();
                game.setCurrentActions(Collections.singletonList(game.moveAction()));
            }
        });

        if (!didRoll) {
            actions.add(Action.make("Attempt Roll", () -> {
                List<Integer> roll = game.rollDice();
                if (Game.isDouble(roll)) {
                    game.getCurrentPlayer().releaseFromJail();
                    game.move(Game.getDiceSum(roll));
                } else {
                    game.setCurrentActions(getJailedActions(true, game));
                }
            }));
        }

        actions.add(new Action() {
            @Nonnull
            @Override
            public String getName() {
                return "Pay Fine";
            }

            @Nonnull
            @Override
            public Items getCost() {
                return fine;
            }

            @Override
            public boolean isAllowed() {
                return !canSkip || game.getCurrentPlayer().getItems().has(fine);
            }

            @Override
            public void perform() {
                game.getCurrentPlayer().setLastCreditor(-1);
                game.getCurrentPlayer().releaseFromJail();
                game.setCurrentActions(Collections.singletonList(game.moveAction()));
            }
        });

        if (canSkip) {
            actions.add(game.endTurnAction());
        }

        return actions;
    }

    public static int INNER_SIZE = 60;

    @Nonnull
    @Override
    public List<Action> getTileActions(@Nonnull Game game) {
        if (game.getCurrentPlayer().isJailed() && !game.isPostMove()) {
            return getJailedActions(false, game);
        } else {
            return Tile.super.getTileActions(game);
        }
    }

    @Override
    public void drawCornerTile(@Nonnull Graphics2D graphics, @Nonnull Game game) {
        graphics.setColor(new Color(255, 128, 0));
        graphics.fillRect(0, 0, INNER_SIZE, INNER_SIZE);

        graphics.setColor(Color.BLACK);
        graphics.setStroke(new BasicStroke(2));
        graphics.drawLine(60, 0, 60, 60);
        graphics.drawLine(0, 60, 60, 60);

        graphics.setFont(graphics.getFont().deriveFont(Font.BOLD));

        Utils.drawStringWrapped(
            "Just",
            graphics,
            0,
            67,
            INNER_SIZE
        );

        graphics.rotate(
            -Math.PI / 2,
            Tile.SIDE_TILE_SIZE.height / 2.0,
            Tile.SIDE_TILE_SIZE.height / 2.0
        );
        Utils.drawStringWrapped(
            "Visiting",
            graphics,
            Tile.SIDE_TILE_SIZE.height - INNER_SIZE,
            67,
            INNER_SIZE
        );

        graphics.rotate(
            Math.PI / 4,
            Tile.SIDE_TILE_SIZE.height / 2.0,
            Tile.SIDE_TILE_SIZE.height / 2.0
        );
        Utils.drawStringWrapped(
            "In Jail",
            graphics,
            10,
            20,
            Tile.SIDE_TILE_SIZE.height - 20
        );
    }

    @Override
    public Point suggestCornerPlayerLocation(@Nonnull Player player, @Nonnull Game game) {
        if (player.isJailed()) {
            return new Point(INNER_SIZE / 2, INNER_SIZE / 2);
        } else {
            int coordinate = (INNER_SIZE + Tile.SIDE_TILE_SIZE.height) / 2;
            return new Point(coordinate, coordinate);
        }
    }
}
