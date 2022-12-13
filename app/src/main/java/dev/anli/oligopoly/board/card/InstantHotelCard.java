package dev.anli.oligopoly.board.card;

import dev.anli.oligopoly.board.Action;
import dev.anli.oligopoly.board.property.Hotel;
import dev.anli.oligopoly.board.property.House;
import dev.anli.oligopoly.board.property.StreetProperty;
import dev.anli.oligopoly.board.tile.PropertyTile;
import dev.anli.oligopoly.board.tile.Tile;
import dev.anli.oligopoly.state.Game;
import dev.anli.oligopoly.state.Items;
import dev.anli.oligopoly.state.PropertyState;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public final class InstantHotelCard implements OwnableCard {
    private InstantHotelCard() {
        // Do nothing.
    }

    private static final InstantHotelCard INSTANCE = new InstantHotelCard();

    /**
     * Get the singleton instance.
     */
    public static InstantHotelCard getInstance() {
        return INSTANCE;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return "Tourist Trap";
    }

    @Nonnull
    @Override
    public String getCardDescription() {
        return "Create the board's most luxurious tourist destination! Before you roll the dice, " +
            "use this card to upgrade any unmortgaged street with a hotel!";
    }

    @Override
    public String getId() {
        return "HOTEL_UPGRADE";
    }

    @Override
    public String getIconText() {
        return "TT";
    }

    @Nonnull
    @Override
    public List<Action> getItemActions(@Nonnull String id, @Nonnull Game game) {
        return Collections.singletonList(new Action() {
            @Nonnull
            @Override
            public String getName() {
                return "Use";
            }

            @Nonnull
            @Override
            public Items getCost() {
                return new Items(getId(), 1);
            }

            @Override
            public boolean isAllowed() {
                if (game.isPostMove()) {
                    return false;
                }

                int location = game.getCurrentPlayer().getLocation();
                Tile tile = game.getBoard().tiles().get(location);
                if (tile instanceof PropertyTile property) {
                    if (game.getBoard().getItem(property.itemId()) instanceof StreetProperty) {
                        PropertyState state = game.getPropertyState(property.itemId());
                        return !state.isMortgaged() && !state.getItems().has(Hotel.ID);
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }

            @Override
            public void perform() {
                int location = game.getCurrentPlayer().getLocation();
                PropertyTile tile = (PropertyTile) game.getBoard().tiles().get(location);

                Items items = game.getPropertyState(tile.itemId()).getItems();
                items.set(House.ID, 0);
                items.set(Hotel.ID, 1);
            }
        });
    }
}
