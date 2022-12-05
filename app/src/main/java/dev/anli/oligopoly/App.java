/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package dev.anli.oligopoly;

import dev.anli.oligopoly.board.Board;
import dev.anli.oligopoly.board.Money;
import dev.anli.oligopoly.state.Game;
import dev.anli.oligopoly.state.Items;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.util.List;

/**
 * App for the Oligopoly game. Displays and manages the current game being played.
 */
public class App implements Runnable {
    private final List<Board> boards;
    private Game game = null;
    private JFrame frame = null;

    public App(@Nonnull List<Board> boards) {
        this.boards = boards;
    }

    private void updateFrame() {
        if (frame == null) {
            return;
        }

        if (game == null) {
            frame.setContentPane(new BoardSelect(boards, game -> {
                this.game = game;
                updateFrame();
            }));
        } else {
            frame.setContentPane(new GamePanel(game));
        }

        // The dark arts of changing the content pane, I guess
        // https://stackoverflow.com/questions/28739402
        frame.setVisible(true);
    }

    @Override
    public void run() {
        // game = new Game(boards.get(0), 4, Money.of(1500));

        frame = new JFrame("Oligopoly");
        updateFrame();

        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        System.setProperty("apple.awt.application.name", "Oligopoly");
        SwingUtilities.invokeLater(new App(Boards.getBoards()));
    }
}
