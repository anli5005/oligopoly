package dev.anli.oligopoly.board.debug;

import dev.anli.oligopoly.board.Action;
import dev.anli.oligopoly.board.Item;
import dev.anli.oligopoly.board.Money;
import dev.anli.oligopoly.board.property.Hotel;
import dev.anli.oligopoly.board.property.House;
import dev.anli.oligopoly.board.property.Property;
import dev.anli.oligopoly.state.Game;
import dev.anli.oligopoly.state.Items;

import javax.annotation.Nonnull;
import java.util.List;

public class DebugItem implements Item {
    public static String ID = "DEBUG";

    private DebugItem() {
        // Do nothing.
    }

    private static final DebugItem instance = new DebugItem();

    public static DebugItem getInstance() {
        return instance;
    }

    @Nonnull
    @Override
    public String getName() {
        return "Debug";
    }

    @Nonnull
    @Override
    public String getItemDescription(@Nonnull String id, @Nonnull Game game) {
        return "Assists in debugging... or just bending the game to your will. (May cause bugs!)";
    }

    @Nonnull
    @Override
    public List<Action> getItemActions(@Nonnull String id, @Nonnull Game game) {
        return List.of(
            Action.make("Move +1", () -> game.move(1)),
            Action.make("Move +10", () -> game.move(10)),
            Action.make("Skip Dice", () -> game.move(0)),
            Action.make(
                "Bankrupt", () -> game.getCurrentPlayer().getItems().set(Money.ID, -90000)
            ),
            Action.make("Spawn Items", () -> {
                game.getBoard().items().forEach((itemId, item) -> {
                    if (item.isFungible()) {
                        game.getCurrentPlayer().getItems().add(itemId, 10000);
                    }
                });
            }),
            Action.make("Unknown Item", () -> {
                game.getCurrentPlayer().getItems().add("ASDFQWERQWRIO", 1);
            }),
            Action.make("Total Monopoly", () -> {
                game.getBoard().items().forEach((itemId, item) -> {
                    if (item instanceof Property) {
                        game.findPlayerForItem(itemId)
                            .ifPresent(owner -> owner.getItems().set(itemId, 0));
                        game.getCurrentPlayer().getItems().set(itemId, 1);
                    }
                });
            }),
            Action.make("Penntrify", () -> {
                game.getBoard().items().forEach((itemId, item) -> {
                    if (item instanceof Property) {
                        Items items = game.getPropertyState(itemId).getItems();
                        items.set(House.ID, 0);
                        items.set(Hotel.ID, 1);
                    }
                });
            })
        );
    }

    @Override
    public int getOrder() {
        return -1;
    }

    @Override
    public boolean isFungible() {
        return false;
    }
}
