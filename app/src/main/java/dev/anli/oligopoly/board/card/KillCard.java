package dev.anli.oligopoly.board.card;

import dev.anli.oligopoly.gui.Utils;
import dev.anli.oligopoly.board.Action;
import dev.anli.oligopoly.state.Game;
import dev.anli.oligopoly.state.Items;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public final class KillCard implements OwnableCard {
    private KillCard() {
        // Do nothing.
    }

    private static final KillCard INSTANCE = new KillCard();

    /**
     * Get the singleton instance.
     */
    public static KillCard getInstance() {
        return INSTANCE;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return "Amogus Sus";
    }

    @Nonnull
    @Override
    public String getCardDescription() {
        return "The collective screams of kids playing Among Us VR call out to you...";
    }

    @Nonnull
    @Override
    public String getItemDescription(@Nonnull String id, @Nonnull Game game) {
        return "Use this card to immediately kill players on the same spot as you.";
    }

    @Override
    public String getId() {
        return "KILL";
    }

    @Override
    public void drawIcon(Graphics2D graphics, String id, Game game) {
        graphics.setColor(Color.RED);
        graphics.fillRect(0, 0, 100, 100);
        graphics.rotate(Math.PI / 24, 50, 50);
        graphics.setColor(Color.BLACK);
        graphics.fillRect(20, 30, 60, 40);
        graphics.setColor(Color.YELLOW);
        graphics.setStroke(new BasicStroke(4));
        graphics.drawRect(20, 30, 60, 40);
        graphics.setFont(graphics.getFont().deriveFont(Font.ITALIC + Font.BOLD, 18));
        Utils.drawStringWrapped("KILL", graphics, 0, 40, 100);
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
                int location = game.getCurrentPlayer().getLocation();
                return game.getPlayers().stream()
                    .filter(player -> player.isAlive() && player.getLocation() == location)
                    .count() > 1;
            }

            @Override
            public void perform() {
                int location = game.getCurrentPlayer().getLocation();
                game.getPlayers().forEach(player -> {
                    if (player.getLocation() == location &&
                        player.isAlive() &&
                        player != game.getCurrentPlayer()) {
                        player.markAsDead(game);
                    }
                });
            }
        });
    }
}
