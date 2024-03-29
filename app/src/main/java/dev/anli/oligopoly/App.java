/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package dev.anli.oligopoly;

import dev.anli.oligopoly.board.Board;
import dev.anli.oligopoly.gui.BoardSelect;
import dev.anli.oligopoly.gui.GamePanel;
import dev.anli.oligopoly.io.Deserializer;
import dev.anli.oligopoly.io.Serializer;
import dev.anli.oligopoly.state.Game;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.io.*;
import java.util.List;

/**
 * App for the Oligopoly game. Displays and manages the current game being played.
 */
public class App implements Runnable {
    private final List<Board> boards;
    private Game game = null;
    private JFrame frame = null;
    private JFrame instructionsFrame = null;
    private final File saveFile = new File("game.txt");

    /**
     * Creates the app instance with the given boards.
     * @param boards list of available boards
     */
    public App(@Nonnull List<Board> boards) {
        this.boards = boards;
    }

    private void saveGame() {
        assert game != null;

        Serializer serializer = new Serializer();
        serializer.accept(game);

        try (FileWriter writer = new FileWriter(saveFile)) {
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(serializer.dump());
            bufferedWriter.close();
            System.out.printf("Game saved for turn %d\n", game.getTurns());
        } catch (IOException e) {
            System.err.println("Couldn't save game:");
            e.printStackTrace();
        }
    }

    private void updateFrame() {
        if (frame == null) {
            return;
        }

        if (game == null) {
            frame.setContentPane(new BoardSelect(boards, game -> {
                this.game = game;
                updateFrame();
                frame.pack();
                saveGame();
            }, this::showInstructions));
        } else {
            game.setGameSaver(game -> saveGame());
            frame.setContentPane(new GamePanel(game, this::quit, this::showInstructions));
        }

        frame.getContentPane().revalidate();
    }

    private void quit() {
        game = null;
        updateFrame();

        if (saveFile.delete()) {
            System.out.println("Save file deleted");
        } else {
            System.err.println("Unable to delete save file.");
        }
    }

    private void showInstructions() {
        if (instructionsFrame == null) {
            instructionsFrame = new JFrame("How to Play");

            try {
                System.out.println(getClass().getResource("instructions.html"));
                JEditorPane pane = new JEditorPane(getClass().getResource("instructions.html"));
                pane.setPreferredSize(new Dimension(400, 600));
                pane.setBorder(new EmptyBorder(16, 16, 16, 16));
                pane.addHyperlinkListener(e -> {
                    if (
                        e.getEventType() == HyperlinkEvent.EventType.ACTIVATED &&
                            Desktop.isDesktopSupported()
                    ) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (Exception ex) {
                            System.err.println("Can't open link.");
                            ex.printStackTrace();
                        }
                    } else {
                        System.err.println("Can't open link: Desktop not supported");
                    }
                });
                pane.setEditable(false);
                instructionsFrame.add(new JScrollPane(pane));
            } catch (IOException e) {
                e.printStackTrace();
                JLabel label = new JLabel("Couldn't load instructions.", SwingConstants.CENTER);
                label.setBorder(new EmptyBorder(16, 16, 16, 16));
                instructionsFrame.add(label);
            }


            instructionsFrame.pack();
        }

        // https://alvinalexander.com/blog/post/jfc-swing/how-center-jframe-java-swing/
        instructionsFrame.setLocationRelativeTo(frame);
        instructionsFrame.setVisible(true);
    }

    /**
     * Attempts to load the most recent save and presents the game window.
     */
    @Override
    public void run() {
        try (FileReader reader = new FileReader(saveFile)) {
            Deserializer deserializer = new Deserializer(reader);
            game = Game.deserialize(deserializer, boards);
            deserializer.close();
            System.out.println("Saved game found and restored");
        } catch (FileNotFoundException e) {
            System.out.println("No saved game found, starting new game");
        } catch (IOException e) {
            System.err.println("Couldn't restore saved game");
            e.printStackTrace();
        }

        frame = new JFrame("Oligopoly");
        updateFrame();

        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * Runs the Oligopoly game using the boards in {@link Boards}.
     */
    public static void main(String[] args) {
        System.setProperty("apple.awt.application.name", "Oligopoly");
        SwingUtilities.invokeLater(new App(Boards.getBoards()));
    }
}
