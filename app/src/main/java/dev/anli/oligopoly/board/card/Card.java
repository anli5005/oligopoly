package dev.anli.oligopoly.board.card;

import dev.anli.oligopoly.board.Action;
import dev.anli.oligopoly.state.Game;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A card in the game.
 */
public interface Card {
    /**
     * Gets the title of the card.
     * @return title of the card
     */
    @Nonnull String getTitle();

    /**
     * Gets the description (body text) of the card.
     * @return description of the card
     */
    @Nonnull String getCardDescription();

    /**
     * Gets a list of actions to be presented when drawing the card.
     * @param game current game
     * @return list of actions to be presented
     */
    @Nonnull List<Action> getCardActions(@Nonnull Game game);
}
