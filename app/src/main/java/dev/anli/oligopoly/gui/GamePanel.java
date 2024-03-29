package dev.anli.oligopoly.gui;

import dev.anli.oligopoly.board.Action;
import dev.anli.oligopoly.board.Item;
import dev.anli.oligopoly.board.Money;
import dev.anli.oligopoly.board.UnknownItem;
import dev.anli.oligopoly.board.tile.PropertyTile;
import dev.anli.oligopoly.state.Game;
import dev.anli.oligopoly.state.TurnPhase;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

/**
 * Controller that displays a game.
 */
public class GamePanel extends JPanel {
    private final Game game;
    private final JLabel statusLabel;
    private final JList<Map.Entry<String, Integer>> inventoryList;
    private final JPanel detailContainer;
    private String selectedItemId = null;
    private ItemDetailPanel detailPanel = null;
    private final JPanel actionPanel;

    /**
     * Constructs a GamePanel.
     * @param game game to manage and display
     * @param quit callback to quit the game and delete the save
     * @param showInstructions callback to show instructions
     */
    public GamePanel(
        @Nonnull Game game,
        @Nonnull Runnable quit,
        @Nonnull Runnable showInstructions
    ) {
        this.game = game;

        setBorder(new EmptyBorder(16, 16, 8, 16));
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(0, 0, 5, 0));
        JButton quitButton = new JButton("End Game");
        quitButton.addActionListener(e -> quit.run());
        headerPanel.add(quitButton, BorderLayout.LINE_START);
        statusLabel = new JLabel("Starting game...", SwingConstants.CENTER);
        headerPanel.add(statusLabel, BorderLayout.CENTER);
        JButton instructionsButton = new JButton("How to Play");
        instructionsButton.addActionListener(e -> showInstructions.run());
        headerPanel.add(instructionsButton, BorderLayout.LINE_END);
        add(headerPanel, BorderLayout.PAGE_START);

        JPanel boardContainer = new JPanel();
        BoardComponent boardComponent = new BoardComponent(game, tile -> {
            if (tile instanceof PropertyTile propertyTile) {
                selectedItemId = propertyTile.itemId();
            } else {
                selectedItemId = null;
            }
            updatePanel();
        });
        boardContainer.add(boardComponent);
        add(boardContainer, BorderLayout.CENTER);

        JPanel sidePanel = new JPanel();
        sidePanel.setBorder(new EmptyBorder(0, 16, 0, 0));
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.PAGE_AXIS));
        JLabel inventoryLabel = new JLabel("Inventory");
        inventoryLabel.setAlignmentX(LEFT_ALIGNMENT);
        sidePanel.add(inventoryLabel);
        sidePanel.add(Box.createVerticalStrut(5));

        inventoryList = new JList<>();
        inventoryList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        inventoryList.setVisibleRowCount(-1);
        inventoryList.addListSelectionListener(e -> {
            Map.Entry<String, Integer> entry = inventoryList.getSelectedValue();
            if (entry != null) {
                selectedItemId = entry.getKey();
                updatePanel();
            }
        });
        inventoryList.setCellRenderer(new ItemIconComponent(game, true, true));

        JScrollPane scrollPane = new JScrollPane(inventoryList);
        scrollPane.setPreferredSize(new Dimension(350, 320));
        scrollPane.setMinimumSize(scrollPane.getMinimumSize());
        scrollPane.setMaximumSize(scrollPane.getPreferredSize());
        scrollPane.setAlignmentX(LEFT_ALIGNMENT);
        sidePanel.add(scrollPane);
        sidePanel.add(Box.createVerticalStrut(10));

        detailContainer = new JPanel();
        detailContainer.setLayout(new BoxLayout(detailContainer, BoxLayout.PAGE_AXIS));
        detailContainer.setMinimumSize(new Dimension(350, 0));
        detailContainer.setMaximumSize(new Dimension(350, Short.MAX_VALUE));
        detailContainer.setAlignmentX(LEFT_ALIGNMENT);
        sidePanel.add(detailContainer);

        add(sidePanel, BorderLayout.LINE_END);

        actionPanel = new JPanel();
        add(actionPanel, BorderLayout.PAGE_END);

        updatePanel();
    }

    public void updatePanel() {
        String statusItemId = Money.ID;
        Item statusItem = game.getBoard().getItem(statusItemId);
        if (statusItem instanceof UnknownItem || game.getTurnPhase() == TurnPhase.START) {
            statusLabel.setText(String.format(
                "Player %d • Turn %d",
                game.getCurrentPlayer().getNumber() + 1,
                game.getTurns()
            ));
        } else {
            statusLabel.setText(String.format(
                "Player %d • Turn %d • %s",
                game.getCurrentPlayer().getNumber() + 1,
                game.getTurns(),
                statusItem.formatQuantity(game.getCurrentPlayer().getItems().get(statusItemId))
            ));
        }

        actionPanel.removeAll();
        if (game.getTurnPhase() == TurnPhase.START) {
            JButton startTurn = new JButton("Start Turn");
            startTurn.addActionListener(e -> {
                game.transitionToPremove();
                updatePanel();
            });
            actionPanel.add(startTurn);
            selectedItemId = null;
        } else if (game.getTurnPhase() != TurnPhase.WINNER) {
            game.getCurrentActions().forEach(action -> actionPanel.add(
                Action.makeButton(action, game, this::updatePanel)
            ));
        }

        if (game.getTurnPhase() == TurnPhase.START) {
            @SuppressWarnings("unchecked") Map.Entry<String, Integer>[] array = new Map.Entry[] {};
            inventoryList.setListData(array);
        } else {
            inventoryList.setListData(
                game.getCurrentPlayer().getItems().toEntryArray(game.getBoard())
            );
        }

        if (selectedItemId == null) {
            detailPanel = null;
            detailContainer.removeAll();
            addHintText();
        } else if (
            detailPanel == null || !selectedItemId.equals(detailPanel.getItemId())
        ) {
            detailContainer.removeAll();
            detailPanel = new ItemDetailPanel(selectedItemId, game, this::updatePanel);
            detailContainer.add(detailPanel);
        } else if (detailPanel != null) {
            detailPanel.updatePanel();
        }

        revalidate();
        repaint();
    }

    private void addHintText() {
        detailContainer.add(new JLabel(
            game.getTurnPhase() == TurnPhase.START ?
                "Start the turn by clicking on Start Turn." :
                "Select an item or property on the board for more info."
        ));
    }
}
