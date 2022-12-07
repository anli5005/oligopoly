package dev.anli.oligopoly.board.tile;

import dev.anli.oligopoly.gui.Utils;
import dev.anli.oligopoly.board.Action;
import dev.anli.oligopoly.board.Item;
import dev.anli.oligopoly.board.property.Hotel;
import dev.anli.oligopoly.board.property.House;
import dev.anli.oligopoly.board.property.Property;
import dev.anli.oligopoly.board.property.PropertyCategory;
import dev.anli.oligopoly.state.Game;
import dev.anli.oligopoly.state.Items;
import dev.anli.oligopoly.state.Player;
import dev.anli.oligopoly.state.PropertyState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * A tile that represents a property.
 * <p>
 * Technically, multiple tiles per property can exist on a single board.
 * @param itemId ID of the property on the board
 */
public record PropertyTile(String itemId) implements Tile {
    @Nullable
    public Property getProperty(@Nonnull Game game) {
        Item item = game.getBoard().items().get(itemId);
        if (item instanceof Property) {
            return (Property) item;
        }
        return null;
    }

    @Nullable
    public PropertyState getPropertyState(@Nonnull Game game) {
        if (getProperty(game) == null) {
            return null;
        }

        return game.getPropertyState(itemId);
    }

    private static Action rentAction(Player owner, Items rent, Game game) {
        return new Action() {
            @Nonnull
            @Override
            public String getName() {
                return "Pay Rent";
            }

            @Nonnull
            @Override
            public Items getCost() {
                return rent;
            }

            @Override
            public boolean isAllowed() {
                return true;
            }

            @Override
            public void perform() {
                game.getCurrentPlayer().setLastCreditor(owner.getNumber());
                owner.getItems().add(rent);
                game.setCurrentActions(Collections.singletonList(game.endTurnAction()));
            }
        };
    }

    private Action buyAction(Items price, Game game) {
        return new Action() {
            @Nonnull
            @Override
            public String getName() {
                return "Buy";
            }

            @Nonnull
            @Override
            public Items getCost() {
                return price;
            }

            @Override
            public boolean isAllowed() {
                return game.getCurrentPlayer().getItems().has(price);
            }

            @Override
            public void perform() {
                game.getCurrentPlayer().getItems().set(itemId, 1);
                game.setCurrentActions(Collections.singletonList(game.endTurnAction()));
            }
        };
    }

    @Nonnull
    @Override
    public List<Action> getTileActions(@Nonnull Game game) {
        Property property = getProperty(game);
        if (property == null) {
            return Tile.super.getTileActions(game);
        }

        PropertyState state = getPropertyState(game);
        assert state != null;

        if (game.isPostMove()) {
            if (game.getCurrentPlayer().getItems().has(itemId)) {
                return Collections.singletonList(game.endTurnAction());
            } else {
                Optional<Player> playerOptional = game.findPlayerForItem(itemId);
                if (playerOptional.isPresent()) {
                    if (state.isMortgaged()) {
                        return Collections.singletonList(game.endTurnAction());
                    } else {
                        Items rent = property.getRent(state, playerOptional.get(), game);
                        return Collections.singletonList(
                            rentAction(playerOptional.get(), rent, game)
                        );
                    }
                } else {
                    return List.of(
                        buyAction(property.getBuyPrice(), game),
                        game.endTurnAction()
                    );
                }
            }
        } else {
            return Collections.singletonList(game.moveAction());
        }
    }

    @Override
    public void drawSideTile(@Nonnull Graphics2D graphics, @Nonnull Game game) {
        Property property = getProperty(game);
        if (property == null) {
            Tile.super.drawSideTile(graphics, game);
            return;
        }

        PropertyState propertyState = getPropertyState(game);
        assert propertyState != null;

        int COLOR_HEIGHT = 20;

        PropertyCategory category = property.getCategory();
        graphics.setColor(new Color(category.red(), category.green(), category.blue()));
        graphics.fillRect(0, 0, SIDE_TILE_SIZE.width, COLOR_HEIGHT);

        graphics.setStroke(new BasicStroke(2));
        graphics.setColor(Color.BLACK);
        graphics.drawLine(0, COLOR_HEIGHT, SIDE_TILE_SIZE.width, COLOR_HEIGHT);

        graphics.setFont(graphics.getFont().deriveFont(8.0F));
        Utils.drawStringWrapped(
            property.getName(),
            graphics,
            5,
            30,
            Tile.SIDE_TILE_SIZE.width - 10
        );
        Utils.drawStringWrapped(
            property.getBuyPrice().format(game.getBoard()),
            graphics,
            0,
            70,
            Tile.SIDE_TILE_SIZE.width
        );

        graphics.setStroke(new BasicStroke(1));
        if (propertyState.getItems().has(Hotel.ID)) {
            graphics.setColor(Color.RED);
            graphics.fillRect(15, 5, SIDE_TILE_SIZE.width - 30, COLOR_HEIGHT - 10);

            graphics.setColor(Color.BLACK);
            graphics.drawRect(15, 5, SIDE_TILE_SIZE.width - 30, COLOR_HEIGHT - 10);
        } else if (propertyState.getItems().has(House.ID)) {
            int HOUSE_WIDTH = 10;
            int HOUSE_GAP = 4;
            int houses = propertyState.getItems().get(House.ID);
            int startX = SIDE_TILE_SIZE.width - houses * HOUSE_WIDTH - (houses - 1) * HOUSE_GAP;
            startX /= 2;

            for (int i = 0; i < houses; i++) {
                int x = startX + i * (HOUSE_WIDTH + HOUSE_GAP);

                graphics.setColor(Color.GREEN);
                graphics.fillRect(x, 5, 10, COLOR_HEIGHT - 10);

                graphics.setColor(Color.BLACK);
                graphics.drawRect(x, 5, 10, COLOR_HEIGHT - 10);
            }
        }
    }
}
