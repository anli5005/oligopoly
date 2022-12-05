package dev.anli.oligopoly.board.card;

import dev.anli.oligopoly.board.Action;
import dev.anli.oligopoly.board.tile.PropertyTile;
import dev.anli.oligopoly.board.tile.Tile;
import dev.anli.oligopoly.state.Game;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public record GoToPropertyCard(
    String propertyId, String title, String description
) implements Card {

    @Nonnull
    @Override
    public String getTitle() {
        return title;
    }

    @Nonnull
    @Override
    public String getCardDescription() {
        return description;
    }

    @Nonnull
    @Override
    public List<Action> getCardActions(@Nonnull Game game) {
        return Collections.singletonList(Action.make("Go!", () -> {
            game.setCurrentCard(null);

            List<Tile> tiles = game.getBoard().tiles();
            for (int i = 0; i < tiles.size(); i++) {
                Tile tile = tiles.get((i + game.getCurrentPlayer().getLocation()) % tiles.size());
                if (tile instanceof PropertyTile propertyTile) {
                    if (propertyTile.itemId().equals(propertyId)) {
                        game.move(i);
                        return;
                    }
                }
            }

            game.setCurrentActions(Collections.singletonList(game.endTurnAction()));
        }));
    }
}
