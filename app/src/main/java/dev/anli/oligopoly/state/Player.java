package dev.anli.oligopoly.state;

import dev.anli.oligopoly.board.*;
import dev.anli.oligopoly.board.property.Property;
import dev.anli.oligopoly.board.property.PropertyCategory;
import dev.anli.oligopoly.board.tile.JailTile;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.OptionalInt;

/**
 * A player in the game.
 */
public class Player {
    private final int number;
    private final Items items;
    private int location = 0;
    private int turnsInJail = -1;
    private boolean isAlive = true;
    private int lastCreditor = -1;

    /**
     * Constructs a new player.
     */
    public Player(int number, @Nonnull Items items) {
        this.number = number;
        this.items = items;
    }

    /**
     * Gets the (zero-indexed) player number.
     */
    public int getNumber() {
        return number;
    }

    /**
     * Gets the Player's items.
     */
    @Nonnull
    public Items getItems() {
        return items;
    }

    /**
     * Gets the player's location.
     */
    public int getLocation() {
        return location;
    }

    /**
     * Gets whether the player is jailed.
     */
    public boolean isJailed() {
        return turnsInJail != -1;
    }

    /**
     * Gets the number of turns the player has spent in jail, or -1 if the player is not jailed.
     */
    public int getTurnsInJail() {
        return turnsInJail;
    }

    /**
     * Sets up a turn by incrementing turnsInJail if needed.
     */
    public void startTurn() {
        if (isJailed()) {
            turnsInJail++;
        }
    }

    /**
     * Sets the player's location.
     */
    public void setLocation(int location) {
        this.location = location;
    }

    /**
     * Sends the player to jail, if possible. Fails if there is no jail tile.
     * @return whether sending to jail succeeded
     */
    public boolean sendToJail(Board board) {
        OptionalInt jail = board.findLocation(tile -> tile instanceof JailTile);
        if (jail.isEmpty()) {
            return false;
        }

        setLocation(jail.getAsInt());
        turnsInJail = 0;
        return true;
    }

    /**
     * Releases the player from jail.
     */
    public void releaseFromJail() {
        turnsInJail = -1;
    }

    /**
     * Checks whether the player has a monopoly in the given color and board.
     */
    public boolean hasMonopoly(PropertyCategory category, Board board) {
        return board.items().entrySet().stream().filter(entry -> {
            Item item = entry.getValue();
            if (item instanceof Property property) {
                return category.equals(property.getCategory());
            }
            return false;
        }).allMatch(entry -> getItems().has(entry.getKey()));
    }

    /**
     * Counts the number of properties the player has in a given category on a given board.
     */
    public int countProperties(PropertyCategory category, Board board) {
        return (int) board.items().entrySet().stream().filter(entry -> {
            Item item = entry.getValue();
            if (item instanceof Property property) {
                return category.equals(property.getCategory()) && getItems().has(entry.getKey());
            }
            return false;
        }).count();
    }

    /**
     * Gets whether the player has died.
     */
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * Sets whether the player has died.
     */
    public void markAsDead(Game game) {
        if (isAlive) {
            // Process the bankruptcy.
            int lastCreditor = getLastCreditor();
            if (lastCreditor == -1 || !game.getPlayers().get(lastCreditor).isAlive()) {
                // The player owes money to the bank. Remove everything they have and clear any
                // properties they own.
                getItems().toMap().keySet().forEach(id -> {
                    if (game.getBoard().getItem(id) instanceof Property) {
                        game.setPropertyState(id, new PropertyState());
                    }
                });
            } else {
                // Transfer everything the player has to the last creditor.
                game.getPlayers().get(lastCreditor).getItems().add(getItems());
            }

            getItems().clear();
        }

        isAlive = false;
    }

    /**
     * Gets the last creditor of the player (i.e. where the player's items should go upon
     * bankruptcy.) Returns -1 if it is the bank.
     */
    public int getLastCreditor() {
        return lastCreditor;
    }

    /**
     * Sets the last creditor of the player.
     */
    public void setLastCreditor(int lastCreditor) {
        this.lastCreditor = lastCreditor;
    }

    /**
     * Gets the color of the player, given the number of total players.
     * @param numPlayers total number of players in the game
     */
    public Color getColor(int numPlayers) {
        float hue = getNumber() / ((float) numPlayers);
        return Color.getHSBColor(hue, 1, 0.8F);
    }
}
