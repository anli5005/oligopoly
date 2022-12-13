package dev.anli.oligopoly.board.card;

import dev.anli.oligopoly.board.Action;
import dev.anli.oligopoly.state.Game;
import dev.anli.oligopoly.state.Items;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public final class SkipCard implements OwnableCard {
    private SkipCard() {
        // Do nothing.
    }

    private static final SkipCard INSTANCE = new SkipCard();

    /**
     * Get the singleton instance.
     */
    public static SkipCard getInstance() {
        return INSTANCE;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return "Skip";
    }

    @Nonnull
    @Override
    public String getCardDescription() {
        return "Use this card to skip any actions like paying rent and immediately end your turn.";
    }

    @Override
    public String getId() {
        return "SKIP";
    }

    @Override
    public String getIconText() {
        return "SKIP";
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
                return true;
            }

            @Override
            public void perform() {
                game.endTurn();
            }
        });
    }
}
