package dev.anli.oligopoly.gui;

import dev.anli.oligopoly.board.card.Card;
import dev.anli.oligopoly.board.tile.Tile;
import dev.anli.oligopoly.state.Game;
import dev.anli.oligopoly.state.Player;
import dev.anli.oligopoly.state.TurnPhase;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * A view that draws the board on screen.
 */
public class BoardComponent extends JComponent {
    private final Game game;

    /**
     * The border width applied to tiles and the board.
     */
    public static final int BORDER_WIDTH = 2;

    /**
     * The number of side tiles per side.
     */
    public static final int TILES_PER_SIDE = 9;

    /**
     * Calculates the size of the board.
     */
    public static Dimension calculateSize() {
        int totalSize = TILES_PER_SIDE * Tile.SIDE_TILE_SIZE.width + 2 * Tile.SIDE_TILE_SIZE.height;
        totalSize += (TILES_PER_SIDE + 3) * BORDER_WIDTH;
        return new Dimension(totalSize, totalSize);
    }

    /**
     * Constructs a BoardComponent.
     * @param game game to draw
     * @param tileClickHandler function called when tiles are clicked
     */
    public BoardComponent(@Nonnull Game game, @Nonnull Consumer<Tile> tileClickHandler) {
        this.game = game;

        Dimension size = calculateSize();
        setMinimumSize(size);
        setPreferredSize(size);
        setMaximumSize(size);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                getTileAtPosition(e.getPoint()).ifPresent(tileClickHandler);
            }
        });
    }

    private OptionalInt getLocationByOffset(double offset, int startPoint) {
        double subOffset = offset - Tile.SIDE_TILE_SIZE.height;
        int tileSize = Tile.SIDE_TILE_SIZE.width + BORDER_WIDTH;
        double index = Math.min(subOffset / tileSize, TILES_PER_SIDE);

        if (index < 0) {
            return OptionalInt.of(startPoint);
        } else if (subOffset - Math.floor(index) * tileSize >= BORDER_WIDTH) {
            return OptionalInt.of((int) index + startPoint + 1);
        }

        return OptionalInt.empty();
    }

    private OptionalInt getLocationAtPosition(Point point) {
        double topDist = point.getY();
        double leftDist = point.getX();
        double rightDist = getSize().getWidth() - point.getX();
        double bottomDist = getSize().getHeight() - point.getY();

        int sideHeight = Tile.SIDE_TILE_SIZE.height + BORDER_WIDTH * 2;

        if (topDist <= sideHeight) {
            return getLocationByOffset(leftDist, TILES_PER_SIDE + 1);
        }

        if (leftDist <= sideHeight) {
            return getLocationByOffset(bottomDist, 0);
        }

        if (rightDist <= sideHeight) {
            return getLocationByOffset(topDist, 2 * (TILES_PER_SIDE + 1));
        }

        if (bottomDist <= sideHeight) {
            return getLocationByOffset(rightDist, 3 * (TILES_PER_SIDE + 1));
        }

        return OptionalInt.empty();
    }

    private Optional<Tile> getTileAtPosition(Point point) {
        OptionalInt locationOptional = getLocationAtPosition(point);
        if (locationOptional.isPresent()) {
            int location = locationOptional.getAsInt();
            List<Tile> tiles = game.getBoard().tiles();
            if (location < tiles.size()) {
                return Optional.of(tiles.get(location));
            }
        }

        return Optional.empty();
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D graphics = (Graphics2D) g;
        Dimension size = calculateSize();

        // Draw the background.

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, size.width, size.height);

        // Draw the borders.

        graphics.setStroke(new BasicStroke(BORDER_WIDTH));
        graphics.setColor(Color.BLACK);

        graphics.drawRect(
            BORDER_WIDTH / 2,
            BORDER_WIDTH / 2,
            size.width - BORDER_WIDTH,
            size.height - BORDER_WIDTH
        );

        int coordinate = Tile.SIDE_TILE_SIZE.height + BORDER_WIDTH * 3 / 2;
        graphics.drawLine(0, coordinate, size.width, coordinate);
        graphics.drawLine(coordinate, 0, coordinate, size.height);

        coordinate = size.height - Tile.SIDE_TILE_SIZE.height - BORDER_WIDTH * 3 / 2;
        graphics.drawLine(0, coordinate, size.width, coordinate);
        graphics.drawLine(coordinate, 0, coordinate, size.height);

        for (int i = 1; i < 9; i++) {
            coordinate = Tile.SIDE_TILE_SIZE.height + BORDER_WIDTH * 3 / 2;
            coordinate += i * (Tile.SIDE_TILE_SIZE.width + BORDER_WIDTH);

            graphics.drawLine(
                0,
                coordinate,
                Tile.SIDE_TILE_SIZE.height + BORDER_WIDTH,
                coordinate
            );
            graphics.drawLine(
                size.width - Tile.SIDE_TILE_SIZE.height - BORDER_WIDTH,
                coordinate,
                size.width,
                coordinate
            );
            graphics.drawLine(
                coordinate,
                0,
                coordinate,
                Tile.SIDE_TILE_SIZE.height + BORDER_WIDTH
            );
            graphics.drawLine(
                coordinate,
                size.height - Tile.SIDE_TILE_SIZE.height - BORDER_WIDTH,
                coordinate,
                size.height
            );
        }

        // Draw the Oligopoly title or player win text.
        Font font = graphics.getFont();
        if (game.getTurnPhase() == TurnPhase.WINNER) {
            graphics.setFont(font.deriveFont(Font.BOLD, 64));
            graphics.setColor(game.getCurrentPlayer().getColor(game.getPlayers().size()));
            Utils.drawStringWrapped(
                String.format("Player %d\nwins!", game.getCurrentPlayer().getNumber() + 1),
                graphics,
                0,
                size.height / 2 - 40,
                size.width
            );
            graphics.setFont(font);
        } else {
            AffineTransform old = graphics.getTransform();
            graphics.rotate(
                -Math.PI / 4,
                size.width / 2.0,
                size.height / 2.0
            );

            graphics.setFont(font.deriveFont(Font.PLAIN, 96));
            graphics.setColor(Color.LIGHT_GRAY);
            Utils.drawStringWrapped(
                "OLIGOPOLY",
                graphics,
                0,
                size.height / 2 - 60,
                size.width
            );

            graphics.setTransform(old);
        }

        graphics.setFont(font);

        // Find the players on each tile.
        Map<Integer, List<Player>> locationsToPlayers = new HashMap<>();
        game.getPlayers().forEach(player -> {
            if (player.isAlive()) {
                int location = player.getLocation();
                List<Player> list = locationsToPlayers.getOrDefault(location, new ArrayList<>());
                list.add(player);
                locationsToPlayers.put(location, list);
            }
        });
        int totalPlayers = game.getPlayers().size();

        // Draw each tile.
        List<Tile> tiles = game.getBoard().tiles();
        for (int i = 0; i < Math.min(4 * TILES_PER_SIDE + 4, tiles.size()); i++) {
            // Create a child graphics context and rotate if needed.
            // Then ask the tile to draw itself.

            Tile tile = tiles.get(i);
            int side = i / (TILES_PER_SIDE + 1);
            int sideIndex = i % (TILES_PER_SIDE + 1);

            double rotation = (side + 1) * (Math.PI / 2);
            AffineTransform old = graphics.getTransform();
            graphics.rotate(rotation, size.width / 2.0, size.height / 2.0);

            int originX, originY;

            if (sideIndex == 0) {
                originX = size.width - Tile.SIDE_TILE_SIZE.height - BORDER_WIDTH;
                originY = size.height - Tile.SIDE_TILE_SIZE.height - BORDER_WIDTH;
                Graphics2D tileGraphics = (Graphics2D) graphics.create(
                    originX,
                    originY,
                    Tile.SIDE_TILE_SIZE.height,
                    Tile.SIDE_TILE_SIZE.height
                );
                tile.drawCornerTile(tileGraphics, game);
            } else {
                originX = (TILES_PER_SIDE - sideIndex) * (Tile.SIDE_TILE_SIZE.width + BORDER_WIDTH)
                    + Tile.SIDE_TILE_SIZE.height + BORDER_WIDTH * 2;
                originY = size.height - Tile.SIDE_TILE_SIZE.height - BORDER_WIDTH;
                Graphics2D tileGraphics = (Graphics2D) graphics.create(
                    originX,
                    originY,
                    Tile.SIDE_TILE_SIZE.width,
                    Tile.SIDE_TILE_SIZE.height
                );
                tile.drawSideTile(tileGraphics, game);
            }

            graphics.setTransform(old);

            // Draw the players by finding their location on the tile, then compositing them.
            List<Player> players = locationsToPlayers.getOrDefault(i, Collections.emptyList());
            Map<Point, List<Player>> pointsToPlayers = new HashMap<>();
            players.forEach(player -> {
                Point point;
                if (sideIndex == 0) {
                    point = tile.suggestCornerPlayerLocation(player, game);
                } else {
                    point = tile.suggestSidePlayerLocation(player, game);
                }

                List<Player> list = pointsToPlayers.getOrDefault(point, new ArrayList<>());
                list.add(player);
                pointsToPlayers.put(point, list);
            });

            AffineTransform rotatedTransform = AffineTransform.getRotateInstance(
                rotation,
                size.width / 2.0,
                size.height / 2.0
            );
            pointsToPlayers.forEach((point, pointPlayers) -> {
                int baseSize = 10;
                int gap = 5;
                int numPlayers = pointPlayers.size();
                double baseX = originX + point.getX();
                double baseY = originY + point.getY() -
                    (numPlayers * baseSize + (numPlayers - 1) * gap) / 2.0;
                for (int j = 0; j < numPlayers; j++) {
                    Player player = pointPlayers.get(j);

                    Point2D raw = new Point2D.Double(baseX, baseY + j * (baseSize + gap));
                    Point2D transformed = rotatedTransform.transform(raw, null);

                    boolean isCurrent = game.getCurrentPlayer() == player;
                    double playerSize = isCurrent ? 40 : baseSize;
                    int playerX = (int) (transformed.getX() - playerSize / 2);
                    int playerY = (int) (transformed.getY() - playerSize / 2);

                    graphics.setColor(player.getColor(totalPlayers));
                    graphics.fillOval(
                        playerX,
                        playerY,
                        (int) playerSize,
                        (int) playerSize
                    );

                    graphics.setColor(Color.BLACK);
                    graphics.setStroke(new BasicStroke(1));
                    graphics.drawOval(
                        playerX,
                        playerY,
                        (int) playerSize,
                        (int) playerSize
                    );

                    if (isCurrent) {
                        graphics.setFont(font.deriveFont(Font.BOLD, 32));
                    } else {
                        graphics.setFont(font.deriveFont(Font.PLAIN, 10));
                    }

                    graphics.setColor(Color.WHITE);
                    Utils.drawStringWrapped(
                        Integer.toString(player.getNumber() + 1),
                        graphics,
                        playerX,
                        playerY,
                        (int) playerSize
                    );

                    graphics.setFont(font);
                }
            });
        }

        // Draw the dice.
        List<List<Integer>> rolls = game.getDiceRolls();
        if (!rolls.isEmpty()) {
            List<Integer> roll = rolls.get(rolls.size() - 1);
            int diceSize = 60;
            int diceGap = 15;
            int baseX = (size.width - 2 * diceSize - diceGap) / 2;
            int baseY = (size.height - diceSize) / 2;

            graphics.setStroke(new BasicStroke(3));
            graphics.setFont(font.deriveFont(Font.BOLD, 40));
            for (int i = 0; i < 2; i++) {
                int diceX = baseX + i * (diceSize + diceGap);
                graphics.setColor(Color.WHITE);
                graphics.fillRect(diceX, baseY, diceSize, diceSize);
                graphics.setColor(Color.BLACK);
                graphics.drawRect(diceX, baseY, diceSize, diceSize);
                Utils.drawStringWrapped(
                    roll.get(i).toString(), graphics, diceX, baseY + 5, diceSize
                );
            }
            graphics.setFont(font);
        }

        // Draw the card if one is being presented.
        Card card = game.getCurrentCard();
        if (card != null) {
            AffineTransform old = graphics.getTransform();
            graphics.rotate(
                -Math.PI / 36,
                size.width / 2.0,
                size.height / 2.0
            );

            int cardWidth = 500;
            int cardHeight = 300;
            int cardX = (size.width - cardWidth) / 2;
            int cardY = (size.height - cardHeight) / 2;

            graphics.setColor(Color.WHITE);
            graphics.fillRect(cardX, cardY, cardWidth, cardHeight);
            graphics.setStroke(new BasicStroke(4));
            graphics.setColor(Color.BLACK);
            graphics.drawRect(cardX, cardY, cardWidth, cardHeight);

            graphics.setFont(font.deriveFont(Font.BOLD, 36));
            Utils.drawStringWrapped(
                card.getTitle(),
                graphics,
                cardX + 10,
                cardY + 80,
                cardWidth - 20
            );
            graphics.setFont(font.deriveFont(Font.PLAIN, 20));
            Utils.drawStringWrapped(
                card.getCardDescription(),
                graphics,
                cardX + 10,
                cardY + 150,
                cardWidth - 20
            );
            graphics.setFont(font);

            graphics.setTransform(old);
        }
    }
}
