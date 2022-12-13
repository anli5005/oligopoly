package dev.anli.oligopoly.board;

import dev.anli.oligopoly.gui.Utils;
import dev.anli.oligopoly.state.Game;
import dev.anli.oligopoly.state.Items;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * Singleton item representing Monopoly money.
 */
public final class Money implements Item {
    /**
     * Suggested ID for money.
     */
    public static final String ID = "MONEY";

    private Money() {
        // Do nothing.
    }

    private static final Money INSTANCE = new Money();

    /**
     * Get the singleton instance.
     */
    public static Money getInstance() {
        return INSTANCE;
    }

    @Nonnull
    @Override
    public String getName() {
        return "Money";
    }

    @Nonnull
    @Override
    public String getItemDescription(@Nonnull String id, @Nonnull Game game) {
        return "What FTX lost in the span of a few short weeks.";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Nonnull
    @Override
    public String formatQuantity(int quantity) {
        return String.format("$%d", quantity);
    }

    @Override
    public boolean isFungible() {
        return true;
    }

    @Override
    public void drawIcon(Graphics2D graphics, String id, Game game) {
        graphics.setColor(new Color(0, 128, 0));
        graphics.fillRect(0,  0, ICON_SIZE.width, ICON_SIZE.height);

        graphics.setColor(Color.GREEN);
        graphics.setFont(graphics.getFont().deriveFont(Font.BOLD, 64));
        Utils.drawStringWrapped("$", graphics, 0, 5, ICON_SIZE.width);
    }

    /**
     * Gets an Items instance with the specified amount of money.
     */
    public static Items of(int quantity) {
        return new Items(ID, quantity);
    }
}
