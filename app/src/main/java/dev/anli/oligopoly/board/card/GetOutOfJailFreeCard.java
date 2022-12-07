package dev.anli.oligopoly.board.card;

import javax.annotation.Nonnull;

/**
 * Singleton item representing a get out of jail free card held by a player.
 */
public final class GetOutOfJailFreeCard implements OwnableCard {
    /**
     * Suggested ID for get out of jail free cards.
     */
    public static final String ID = "GET_OUT_OF_JAIL_FREE";

    @Nonnull public String getId() {
        return ID;
    }

    private GetOutOfJailFreeCard() {
        // Do nothing.
    }

    private static final GetOutOfJailFreeCard instance = new GetOutOfJailFreeCard();

    /**
     * Get the singleton instance.
     */
    public static GetOutOfJailFreeCard getInstance() {
        return instance;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return "Get out of Jail Free";
    }

    @Nonnull
    @Override
    public String getCardDescription() {
        return "Use this card to get out of jail for free.";
    }

    @Override
    public String getIconText() {
        return "FREE";
    }
}
