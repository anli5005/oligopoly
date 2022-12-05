package dev.anli.oligopoly;

import dev.anli.oligopoly.board.Board;
import dev.anli.oligopoly.board.Money;
import dev.anli.oligopoly.state.Game;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class BoardSelect extends JPanel {
    private Board board;
    private JLabel boardLabel;
    private JButton startButton;
    private JTextField playersTextField;
    private final Consumer<Game> onSelect;

    public BoardSelect(List<Board> boards, Consumer<Game> onSelect) {
        this.onSelect = onSelect;
        board = boards.get(0);

        setBorder(new EmptyBorder(16, 16, 16, 16));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JLabel title = new JLabel("Select Board", SwingConstants.CENTER);
        title.setAlignmentX(CENTER_ALIGNMENT);
        add(title);

        add(Box.createVerticalStrut(5));

        JPanel grid = new JPanel(new GridLayout(0, 4));
        boards.forEach(board -> {
            JButton button = new JButton(board.name());
            button.addActionListener(e -> {
                this.board = board;
                updatePanel();
            });
            grid.add(button);
        });
        grid.setAlignmentX(CENTER_ALIGNMENT);
        add(grid);

        add(new Box.Filler(
            new Dimension(0, 5),
            new Dimension(0, Short.MAX_VALUE),
            new Dimension(0, Short.MAX_VALUE)
        ));

        JPanel bottom = new JPanel();
        bottom.setAlignmentX(CENTER_ALIGNMENT);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));

        boardLabel = new JLabel();
        bottom.add(boardLabel);

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
        startButton.addActionListener(e -> onSelect.accept(
            new Game(board, Integer.parseInt(playersTextField.getText()))
        ));
        startButton.setDefaultCapable(true);
        bottom.add(startButton);

        add(bottom);

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
