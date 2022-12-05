package dev.anli.oligopoly;

import dev.anli.oligopoly.board.card.Card;
import dev.anli.oligopoly.board.tile.Tile;
import dev.anli.oligopoly.state.Game;
import dev.anli.oligopoly.state.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

public class BoardComponent extends JComponent {
    private Game game;

    public static final int BORDER_WIDTH = 2;

    public static final int TILES_PER_SIDE = 9;

    public static Dimension calculateSize() {
        int totalSize = TILES_PER_SIDE * Tile.SIDE_TILE_SIZE.width + 2 * Tile.SIDE_TILE_SIZE.height;
        totalSize += (TILES_PER_SIDE + 3) * BORDER_WIDTH;
        return new Dimension(totalSize, totalSize);
    }

    public BoardComponent(Game game) {
        this.game = game;

        Dimension size = calculateSize();
        setMinimumSize(size);
        setPreferredSize(size);
        setMaximumSize(size);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D graphics = (Graphics2D) g;
        Dimension size = calculateSize();

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, size.width, size.height);

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

        {
            AffineTransform old = graphics.getTransform();
            graphics.rotate(
                -Math.PI / 4,
                size.width / 2.0,
                size.height / 2.0
            );

            Font oldFont = graphics.getFont();
            graphics.setFont(oldFont.deriveFont(Font.PLAIN, 96));
            graphics.setColor(Color.LIGHT_GRAY);
            Utils.drawStringWrapped(
                "OLIGOPOLY",
                graphics,
                0,
                size.height / 2 - 60,
                size.width
            );
            graphics.setFont(oldFont);

            graphics.setTransform(old);
        }

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

        List<Tile> tiles = game.getBoard().tiles();
        for (int i = 0; i < Math.min(4 * TILES_PER_SIDE + 4, tiles.size()); i++) {
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

                    Font oldFont = graphics.getFont();
                    if (isCurrent) {
                        graphics.setFont(oldFont.deriveFont(Font.BOLD, 32));
                    } else {
                        graphics.setFont(oldFont.deriveFont(Font.PLAIN, 10));
                    }

                    graphics.setColor(Color.WHITE);
                    Utils.drawStringWrapped(
                        Integer.toString(player.getNumber() + 1),
                        graphics,
                        playerX,
                        playerY,
                        (int) playerSize
                    );

                    graphics.setFont(oldFont);
                }
            });
        }

        List<List<Integer>> rolls = game.getDiceRolls();
        if (!rolls.isEmpty()) {
            List<Integer> roll = rolls.get(rolls.size() - 1);
            int diceSize = 60;
            int diceGap = 15;
            int baseX = (size.width - 2 * diceSize - diceGap) / 2;
            int baseY = (size.height - diceSize) / 2;

            graphics.setStroke(new BasicStroke(3));
            Font oldFont = graphics.getFont();
            graphics.setFont(oldFont.deriveFont(Font.BOLD, 40));
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
            graphics.setFont(oldFont);
        }

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

            Font oldFont = graphics.getFont();
            graphics.setFont(oldFont.deriveFont(Font.BOLD, 36));
            Utils.drawStringWrapped(
                card.getTitle(),
                graphics,
                cardX + 10,
                cardY + 80,
                cardWidth - 20
            );
            graphics.setFont(oldFont.deriveFont(Font.PLAIN, 20));
            Utils.drawStringWrapped(
                card.getCardDescription(),
                graphics,
                cardX + 10,
                cardY + 150,
                cardWidth - 20
            );
            graphics.setFont(oldFont);

            graphics.setTransform(old);
        }
    }
}
