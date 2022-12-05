package dev.anli.oligopoly.board.card;

import dev.anli.oligopoly.Utils;
import dev.anli.oligopoly.board.Action;
import dev.anli.oligopoly.board.Item;
import dev.anli.oligopoly.state.Game;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * A card that exists in item form.
 */
public interface OwnableCard extends Card, Item {
    /**
     * Gets the preferred ID of the item form of this card.
     */
    String getId();

    @Override
    @Nonnull
    default String getName() {
        return String.format("%s Card", getTitle());
    }

    @Nonnull
    @Override
    default String getItemDescription(@Nonnull String id, @Nonnull Game game) {
        return getCardDescription();
    }

    @Override
    default boolean isFungible() {
        return true;
    }

    @Override
    default void drawIcon(Graphics2D graphics, String id, Game game) {
        graphics.setColor(Color.GRAY);
        graphics.fillRect(0, 0, 100, 100);
        graphics.rotate(Math.PI / 24, 50, 50);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(20, 30, 60, 40);
        graphics.setColor(Color.BLACK);
        graphics.setStroke(new BasicStroke(4));
        graphics.drawRect(20, 30, 60, 40);
        graphics.setFont(graphics.getFont().deriveFont(Font.ITALIC + Font.BOLD, 18));
        Utils.drawStringWrapped(getIconText(), graphics, 0, 40, 100);
    }

    /**
     * Gets the text to draw on the icon.
     */
    default String getIconText() {
        return "";
    }

    @Nonnull
    @Override
    default List<Action> getCardActions(@Nonnull Game game) {
        return Collections.singletonList(Action.make("Accept", () -> {
            game.getCurrentPlayer().getItems().add(getId(), 1);
            game.setCurrentCard(null);
            game.setCurrentActions(Collections.singletonList(game.endTurnAction()));
        }));
    }

    @Override
    default int getOrder() {
        return 2;
    }
}
