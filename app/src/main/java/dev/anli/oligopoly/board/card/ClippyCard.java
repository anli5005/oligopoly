package dev.anli.oligopoly.board.card;

import dev.anli.oligopoly.board.Action;
import dev.anli.oligopoly.board.Money;
import dev.anli.oligopoly.state.Game;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public final class ClippyCard implements Card {
    private ClippyCard() {
        // Do nothing.
    }

    private static final ClippyCard instance = new ClippyCard();

    /**
     * Get the singleton instance.
     */
    public static ClippyCard getInstance() {
        return instance;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return "Hi, I'm Clippy!";
    }

    @Nonnull
    @Override
    public String getCardDescription() {
        return "Remember me, your beloved CIS 1200 Paint Intelligent Assistant?";
    }

    @Nonnull
    @Override
    public List<Action> getCardActions(@Nonnull Game game) {
        return Collections.singletonList(
            Action.make("Oh no, not you again.", () -> game.setCurrentCard(new Card() {
                @Nonnull
                @Override
                public String getTitle() {
                    return "Awww, you miss me!";
                }

                @Nonnull
                @Override
                public String getCardDescription() {
                    return "It looks like you are trying to backstab all your friends. " +
                        "Would you like help?";
                }

                @Nonnull
                @Override
                public List<Action> getCardActions(@Nonnull Game game) {
                    return List.of(
                        Action.make(
                            "Backstab my friends with help",
                            () -> {
                                game.getCurrentPlayer().getItems()
                                    .add(Money.ID, game.getPlayers().size() * 50);
                                game.getPlayers().forEach(
                                    player -> player.getItems().add(Money.ID, -50)
                                );
                                game.setCurrentCard(new Card() {
                                    @Nonnull
                                    @Override
                                    public String getTitle() {
                                        return "No need to thank me!";
                                    }

                                    @Nonnull
                                    @Override
                                    public String getCardDescription() {
                                        return "Actually, there is. Give Anthony Li a 100 on " +
                                            "Gradescope!";
                                    }

                                    @Nonnull
                                    @Override
                                    public List<Action> getCardActions(@Nonnull Game game) {
                                        return Collections.singletonList(game.endTurnAction());
                                    }
                                });
                            }
                        ),
                        Action.make(
                            "Just backstab my friends without help",
                            () -> {
                                game.getCurrentPlayer().getItems().add(Money.ID, -100);
                                game.setCurrentCard(new Card() {
                                    @Nonnull
                                    @Override
                                    public String getTitle() {
                                        return "Wow, ungrateful.";
                                    }

                                    @Nonnull
                                    @Override
                                    public String getCardDescription() {
                                        return "Thanks for the money, though.";
                                    }

                                    @Nonnull
                                    @Override
                                    public List<Action> getCardActions(@Nonnull Game game) {
                                        return Collections.singletonList(game.endTurnAction());
                                    }
                                });
                            }
                        )
                    );
                }
            }))
        );
    }
}
