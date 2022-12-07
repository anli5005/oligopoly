package dev.anli.oligopoly.gui;

import dev.anli.oligopoly.board.Board;
import dev.anli.oligopoly.board.debug.DebugItem;
import dev.anli.oligopoly.state.Game;
import dev.anli.oligopoly.state.Items;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * The board selection and game configuration menu.
 */
public class BoardSelect extends JPanel {
    private Board board;
    private final JLabel boardLabel;
    private final JButton startButton;
    private final JTextField playersTextField;

    /**
     * Constructs a board selection menu.
     * @param boards boards to choose from
     * @param onSelect callback when a game is started
     * @param showInstructions callback to show instructions
     */
    public BoardSelect(
        @Nonnull List<Board> boards,
        @Nonnull Consumer<Game> onSelect,
        @Nonnull Runnable showInstructions
    ) {
        board = boards.get(0);

        setBorder(new EmptyBorder(16, 16, 16, 16));
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        JLabel title = new JLabel("Select Board", SwingConstants.LEADING);
        headerPanel.add(title, BorderLayout.LINE_START);
        JButton instructionsButton = new JButton("How to Play");
        instructionsButton.addActionListener(e -> showInstructions.run());
        headerPanel.add(instructionsButton, BorderLayout.LINE_END);
        add(headerPanel, BorderLayout.PAGE_START);

        JPanel grid = new JPanel(new GridLayout(0, Math.min(boards.size(), 4)));
        boards.forEach(board -> {
            JButton button = new JButton(board.name());
            button.addActionListener(e -> {
                this.board = board;
                updatePanel();
            });
            grid.add(button);
        });
        grid.setPreferredSize(new Dimension(700, 200));
        grid.setBorder(new EmptyBorder(50, 0, 50, 0));
        add(grid, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setAlignmentX(CENTER_ALIGNMENT);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));

        boardLabel = new JLabel();
        bottom.add(boardLabel);

        bottom.add(Box.createHorizontalStrut(10));

        JCheckBox debugMode = new JCheckBox("<html><s>Cheating</s> Debug Mode?</html>");
        bottom.add(debugMode);
        bottom.add(Box.createHorizontalGlue());
        bottom.add(Box.createHorizontalStrut(10));

        bottom.add(new JLabel("# of Players:"));
        bottom.add(Box.createHorizontalStrut(5));

        playersTextField = new JTextField("4", 4);
        playersTextField.getDocument().addDocumentListener(Utils.changeListener(this::updatePanel));
        playersTextField.setMaximumSize(new Dimension(50, Short.MAX_VALUE));
        bottom.add(playersTextField);
        bottom.add(Box.createHorizontalStrut(10));

        startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            int players = Integer.parseInt(playersTextField.getText());
            Items startingItems = new Items(board.startItems());
            if (debugMode.isSelected()) {
                startingItems.add(DebugItem.ID, 1);
            }
            onSelect.accept(new Game(board, players, startingItems));
        });
        startButton.setDefaultCapable(true);
        bottom.add(startButton);

        add(bottom, BorderLayout.PAGE_END);

        updatePanel();
    }

    private void updatePanel() {
        if (board == null) {
            boardLabel.setText("No board selected");
        } else {
            boardLabel.setText(String.format("Selected Board: %s", board.name()));
        }

        int numPlayers;
        try {
            numPlayers = Integer.parseInt(playersTextField.getText());
        } catch (NumberFormatException e) {
            numPlayers = -1;
        }

        startButton.setEnabled(board != null && numPlayers >= 2);
    }
}
