package dev.anli.oligopoly.board.card;

import dev.anli.oligopoly.board.Action;
import dev.anli.oligopoly.board.tile.PropertyTile;
import dev.anli.oligopoly.board.tile.Tile;
import dev.anli.oligopoly.state.Game;
import dev.anli.oligopoly.state.Items;
import dev.anli.oligopoly.state.Player;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class StealCard implements OwnableCard {
    private StealCard() {
        // Do nothing.
    }

    private static final StealCard instance = new StealCard();

    /**
     * Get the singleton instance.
     */
    public static StealCard getInstance() {
        return instance;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return "Wharton Strats";
    }

    @Nonnull
    @Override
    public String getCardDescription() {
        return "Before you roll the dice, use this property to steal the " +
            "property you're on from someone else!";
    }

    @Override
    public String getId() {
        return "STEAL";
    }

    @Override
    public String getIconText() {
        return "WH";
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
                    Optional<Player> owner = game.findPlayerForItem(property.itemId());
                    return owner.filter(player -> player != game.getCurrentPlayer()).isPresent();
                } else {
                    return false;
                }
            }

            @Override
            public void perform() {
                int location = game.getCurrentPlayer().getLocation();
                PropertyTile tile = (PropertyTile) game.getBoard().tiles().get(location);

                game.getPropertyState(tile.itemId()).setMortgaged(false);
                game.findPlayerForItem(tile.itemId())
                    .ifPresent(owner -> owner.getItems().set(tile.itemId(), 0));
                game.getCurrentPlayer().getItems().set(tile.itemId(), 1);
            }
        });
    }
}
