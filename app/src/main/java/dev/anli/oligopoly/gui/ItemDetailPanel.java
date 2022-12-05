package dev.anli.oligopoly.gui;

import dev.anli.oligopoly.board.Action;
import dev.anli.oligopoly.board.Item;
import dev.anli.oligopoly.state.Game;
import dev.anli.oligopoly.state.TurnPhase;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ItemDetailPanel extends JPanel {
    private final String itemId;
    private final Game game;
    private final JTextArea descriptionText;
    private final JPanel statsPanel;
    private final JPanel actionsPanel;
    private final Runnable triggerRootUpdate;

    public String getItemId() {
        return itemId;
    }

    private Item getItem() {
        return game.getBoard().getItem(itemId);
    }

    public ItemDetailPanel(String itemId, Game game, Runnable triggerRootUpdate) {
        this.itemId = itemId;
        this.game = game;
        this.triggerRootUpdate = triggerRootUpdate;

        setLayout(new BorderLayout());

        Item item = getItem();

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.LINE_AXIS));
        ItemIconComponent icon = new ItemIconComponent(
            itemId, 0, game, false, false
        );
        icon.setAlignmentY(CENTER_ALIGNMENT);
        headerPanel.add(icon);
        headerPanel.add(Box.createHorizontalStrut(7));
        JPanel headerTextPanel = new JPanel();
        headerTextPanel.setLayout(new BoxLayout(headerTextPanel, BoxLayout.PAGE_AXIS));
        JTextArea titleText = new JTextArea(item.getName());
        titleText.setFont(titleText.getFont().deriveFont(Font.BOLD, 18));
        titleText.setDisabledTextColor(Color.BLACK);
        titleText.setLineWrap(true);
        titleText.setWrapStyleWord(true);
        titleText.setRows(2);
        titleText.setEnabled(false);
        titleText.setBackground(getBackground());
        titleText.setAlignmentX(LEFT_ALIGNMENT);
        headerTextPanel.add(titleText);
        descriptionText = new JTextArea();
        descriptionText.setFont(descriptionText.getFont().deriveFont(Font.ITALIC));
        descriptionText.setLineWrap(true);
        descriptionText.setWrapStyleWord(true);
        descriptionText.setEnabled(false);
        descriptionText.setBackground(getBackground());
        descriptionText.setAlignmentX(LEFT_ALIGNMENT);
        descriptionText.setRows(3);
        headerTextPanel.add(descriptionText);
        headerTextPanel.setAlignmentY(CENTER_ALIGNMENT);
        headerPanel.add(headerTextPanel);
        headerPanel.setAlignmentX(CENTER_ALIGNMENT);
        add(headerPanel, BorderLayout.PAGE_START);

        statsPanel = new JPanel();
        statsPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.PAGE_AXIS));
        add(statsPanel, BorderLayout.CENTER);

        actionsPanel = new JPanel();
        actionsPanel.setAlignmentX(CENTER_ALIGNMENT);
        actionsPanel.setMinimumSize(new Dimension(330, 0));
        actionsPanel.setMaximumSize(new Dimension(330, Short.MAX_VALUE));
        add(actionsPanel, BorderLayout.PAGE_END);

        updatePanel();
    }

    public void updatePanel() {
        Item item = getItem();

        descriptionText.setText(item.getItemDescription(itemId, game));

        actionsPanel.removeAll();
        if (game.getCurrentPlayer().getItems().has(itemId) &&
            game.getTurnPhase() != TurnPhase.WINNER) {
            List<Action> actions = item.getItemActions(itemId, game);
            actionsPanel.setLayout(
                new GridLayout(0, Math.max(1, Math.min(2, actions.size())))
            );
            actions.forEach(action -> {
                JButton button = Action.makeButton(action, game, triggerRootUpdate);
                button.setAlignmentX(CENTER_ALIGNMENT);
                button.setMaximumSize(new Dimension(150, 40));
                actionsPanel.add(button);
            });
        }

        statsPanel.removeAll();
        item.getDisplayStats(itemId, game).forEach(stat -> {
            statsPanel.add(new JLabel(stat));
        });

        actionsPanel.revalidate();
        statsPanel.revalidate();
        repaint();
    }
}
