package dev.anli.oligopoly;

import dev.anli.oligopoly.board.Item;
import dev.anli.oligopoly.state.Game;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * A component that renders an item's icon.
 */
public class ItemIconComponent
    extends JComponent
    implements ListCellRenderer<Map.Entry<String, Integer>> {
    private String itemId;
    private int quantity;
    private Game game;
    private final boolean isSmall;
    private final boolean showQuantity;

    public static int BORDER_WIDTH = 2;
    public static int SMALL_BORDER_WIDTH = 1;

    public ItemIconComponent(
        String itemId, int quantity, Game game, boolean isSmall, boolean showQuantity
    ) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.game = game;
        this.isSmall = isSmall;
        this.showQuantity = showQuantity;

        if (isSmall) {
            setPreferredSize(new Dimension(
                Item.ICON_SIZE.width / 2 + SMALL_BORDER_WIDTH * 2,
                Item.ICON_SIZE.height / 2 + SMALL_BORDER_WIDTH * 2
            ));
        } else {
            setPreferredSize(new Dimension(
                Item.ICON_SIZE.width + BORDER_WIDTH * 2,
                Item.ICON_SIZE.height + BORDER_WIDTH * 2
            ));
        }
        setMinimumSize(getPreferredSize());
        setMaximumSize(getPreferredSize());
    }

    public ItemIconComponent(Game game, boolean isSmall, boolean showQuantity) {
        this("", 1, game, isSmall, showQuantity);
    }

    public void setItemId(String id) {
        itemId = id;
        repaint();
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D graphics = (Graphics2D) g;
        Dimension size = getSize();
        int borderWidth = isSmall ? SMALL_BORDER_WIDTH : BORDER_WIDTH;

        Item item = game.getBoard().getItem(itemId);

        @SuppressWarnings("SuspiciousNameCombination") Graphics2D child = (Graphics2D) g.create(
            borderWidth, borderWidth,
            size.width - borderWidth * 2, size.height - borderWidth * 2
        );

        if (isSmall) {
            child.scale(0.5, 0.5);
        }

        item.drawIcon(child, itemId, game);

        if (showQuantity && item.isFungible()) {
            graphics.setFont(graphics.getFont().deriveFont(Font.BOLD, 12));
            graphics.setColor(Color.WHITE);
            Utils.drawStringWrapped(
                Integer.toString(quantity),
                graphics,
                0,
                size.height - 15,
                size.width
            );
        }

        graphics.setStroke(new BasicStroke(borderWidth));
        graphics.setColor(Color.BLACK);
        graphics.drawRect(
            borderWidth / 2,
            borderWidth / 2,
            size.width - borderWidth,
            size.height - borderWidth
        );
    }

    @Override
    public Component getListCellRendererComponent(
        JList<? extends Map.Entry<String, Integer>> list,
        Map.Entry<String, Integer> value,
        int index,
        boolean isSelected,
        boolean cellHasFocus
    ) {
        setItemId(value.getKey());
        setQuantity(value.getValue());
        return this;
    }
}
