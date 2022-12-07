package dev.anli.oligopoly.board;

import dev.anli.oligopoly.state.Game;
import dev.anli.oligopoly.state.Items;

import javax.annotation.Nonnull;
import javax.swing.*;

/**
 * An action that can be performed in the game.
 */
public interface Action {
    /**
     * Gets the display name of the action.
     * @return display name of the action
     */
    @Nonnull String getName();

    /**
     * Returns the cost of the action.
     */
    @Nonnull Items getCost();

    /**
     * Returns whether the action is allowed.
     * <p>
     * An action might want to implement this method to check that the user has enough
     * materials to cover the cost.
     */
    boolean isAllowed();

    /**
     * Performs the action. Does not debit the cost; it is assumed that the cost has already
     * been subtracted.
     */
    void perform();

    /**
     * Creates an ad-hoc free action using a Runnable.
     * @param name name of the action
     * @param runnable runnable that performs the action
     * @return ad-hoc free action using the given name and Runnable
     */
    static Action make(String name, Runnable runnable) {
        return new Action() {
            @Nonnull
            @Override
            public String getName() {
                return name;
            }

            @Nonnull
            @Override
            public Items getCost() {
                return new Items();
            }

            @Override
            public boolean isAllowed() {
                return true;
            }

            @Override
            public void perform() {
                runnable.run();
            }
        };
    }

    /**
     * Generates a JButton that allows the current player to perform an action, if allowed.
     * @param action the action
     * @param game current game
     * @param afterRun callback to execute after the action is run (for example, for updating views)
     */
    static JButton makeButton(Action action, Game game, Runnable afterRun) {
        String title;
        if (action.getCost().isEmpty()) {
            title = action.getName();
        } else {
            title = String.format(
                "%s (%s)", action.getName(), action.getCost().format(game.getBoard())
            );
        }

        JButton button = new JButton(title);
        button.setEnabled(action.isAllowed());
        button.addActionListener(e -> {
            game.debitAndPerform(action);
            afterRun.run();
        });

        return button;
    }
}
