package dev.anli.oligopoly.state;

import dev.anli.oligopoly.board.*;
import dev.anli.oligopoly.board.card.Card;
import dev.anli.oligopoly.board.Action;
import dev.anli.oligopoly.board.property.Property;
import dev.anli.oligopoly.board.tile.Tile;
import dev.anli.oligopoly.io.Deserializer;
import dev.anli.oligopoly.io.Serializable;
import dev.anli.oligopoly.io.Serializer;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.sql.Array;
import java.util.*;
import java.util.function.Consumer;

/**
 * An Oligopoly game and its state.
 */
public class Game implements Serializable {
    private final Board board;
    private final List<Player> players;
    private int currentPlayerNumber;
    private final List<List<Integer>> diceRolls = new ArrayList<>();
    private Card currentCard = null;
    private List<Action> currentActions = Collections.emptyList();
    private int turns;
    private TurnPhase turnPhase = TurnPhase.START;
    private final Map<String, PropertyState> propertyStates;
    private Consumer<Game> gameSaver = null;

    private Game(
        @Nonnull Board board,
        @Nonnull List<Player> players,
        int currentPlayerNumber,
        int turns,
        Map<String, PropertyState> propertyStates,
        boolean isComplete
    ) {
        this.board = board;
        this.players = players;
        this.currentPlayerNumber = currentPlayerNumber;
        this.turns = turns;
        this.propertyStates = propertyStates;

        if (isComplete) {
            turnPhase = TurnPhase.WINNER;
        } else {
            startTurn();
        }
    }

    private static List<Player> createPlayers(int numPlayers, Items startingItems) {
        if (numPlayers <= 0) {
            throw new IllegalArgumentException("numPlayers must be positive");
        }

        List<Player> players = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            players.add(new Player(i, new Items(startingItems)));
        }
        return players;
    }

    /**
     * Constructs a Game from the given board with the given number of players and starting items.
     * @param board board to play
     * @param numPlayers number of players
     * @param startingItems items players should start with
     * @throws IllegalArgumentException if numPlayers <= 0
     */
    public Game(@Nonnull Board board, int numPlayers, @Nonnull Items startingItems) {
        this(
            board,
            createPlayers(numPlayers, startingItems),
            0,
            0,
            new HashMap<>(),
            false
        );
    }

    /**
     * Constructs a Game from the given board with the given number of players and recommended
     * starting items.
     * @param board board to play
     * @param numPlayers number of players
     * @throws IllegalArgumentException if numPlayers <= 0
     */
    public Game(@Nonnull Board board, int numPlayers) {
        this(board, numPlayers, board.startItems());
    }

    /**
     * Gets the board.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Gets the list of players.
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Gets the current player.
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerNumber);
    }

    /**
     * Gets the current player number.
     */
    public int getCurrentPlayerNumber() {
        return currentPlayerNumber;
    }

    /**
     * Gets the current phase of the turn.
     */
    public TurnPhase getTurnPhase() {
        return turnPhase;
    }

    /**
     * Gets whether the player moved in the current turn.
     */
    public boolean isPostMove() {
        return turnPhase == TurnPhase.POSTMOVE || turnPhase == TurnPhase.WINNER;
    }

    /**
     * Gets the dice rolls for this turn.
     */
    public List<List<Integer>> getDiceRolls() {
        return diceRolls;
    }

    /**
     * Rolls the dice.
     * @return result of the dice roll
     */
    @Nonnull
    public List<Integer> rollDice() {
        List<Integer> result = List.of(
            (int) (Math.random() * 6 + 1), (int) (Math.random() * 6 + 1)
        );
        diceRolls.add(result);
        return result;
    }

    /**
     * Returns whether the given dice roll is a double (i.e. all elements are the same.)
     */
    public static boolean isDouble(@Nonnull List<Integer> roll) {
        return Objects.equals(roll.get(0), roll.get(1));
    }

    /**
     * Returns the sum of the given dice roll.
     */
    public static int getDiceSum(@Nonnull List<Integer> roll) {
        return roll.get(0) + roll.get(1);
    }

    /**
     * Starts a new turn.
     */
    public void startTurn() {
        turnPhase = TurnPhase.START;
        diceRolls.clear();
        turns++;
        getCurrentPlayer().startTurn();
        if (gameSaver != null) {
            gameSaver.accept(this);
        }
    }

    /**
     * Transitions the turn to the pre-move phase.
     */
    public void transitionToPremove() {
        Player player = getCurrentPlayer();
        Tile tile = board.tiles().get(player.getLocation());

        turnPhase = TurnPhase.PREMOVE;
        setCurrentActions(tile.getTileActions(this));
    }

    /**
     * Debits an action's cost and performs it.
     */
    public void debitAndPerform(@Nonnull Action action) {
        getCurrentPlayer().getItems().subtract(action.getCost());
        action.perform();
    }

    /**
     * Moves the current player by the specified number of tiles.
     */
    public void move(int tiles) {
        turnPhase = TurnPhase.POSTMOVE;
        setCurrentCard(null);

        int start = getCurrentPlayer().getLocation();
        int end = (start + tiles) % board.tiles().size();
        getCurrentPlayer().setLocation(end);

        for (int i = 1; i < tiles - 1; i++) {
            int location = (start + i) % board.tiles().size();
            Tile tile = board.tiles().get(location);
            tile.onPass(this);
        }

        Tile destination = board.tiles().get(end);
        destination.onLand(this);
        setCurrentActions(destination.getTileActions(this));
    }

    /**
     * Rolls the dice, checks if the player should go to jail, and moves the current player.
     */
    public void rollDiceAndMove() {
        List<Integer> roll = rollDice();
        if (isDouble(roll) && getDiceRolls().size() >= 3) {
            // Go to jail. Go directly to jail. Do not pass go. Do not collect $200.
            if (getCurrentPlayer().sendToJail(getBoard())) {
                turnPhase = TurnPhase.POSTMOVE;
                Tile tile = board.tiles().get(getCurrentPlayer().getLocation());
                tile.onLand(this);
                setCurrentActions(tile.getTileActions(this));
            }

            // If the jail fails, eh - let the player have another go.
            return;
        }

        move(getDiceSum(roll));
    }

    /**
     * Returns an action allowing the player to move.
     */
    @Nonnull
    public Action moveAction() {
        return Action.make("Move", this::rollDiceAndMove);
    }

    /**
     * Advances to the next turn.
     */
    public void endTurn() {
        setCurrentActions(Collections.emptyList());
        setCurrentCard(null);

        // Check to see if the player is now bankrupt.
        if (getCurrentPlayer().getItems().hasDebt()) {
            getCurrentPlayer().markAsDead(this);
        }

        // How many people are still alive?
        long alivePlayers = getPlayers().stream().filter(Player::isAlive).count();

        // Move onto the next alive player.
        for (int i = 1; i <= getPlayers().size(); i++) {
            int candidate = (getCurrentPlayerNumber() + i) % getPlayers().size();
            if (getPlayers().get(candidate).isAlive()) {
                currentPlayerNumber = candidate;
                if (alivePlayers == 1L) {
                    declareWinner();
                } else {
                    startTurn();
                }
                return;
            }
        }

        // If we get to this point, everyone's dead. Oh well, just declare the current player to be
        // the victor.
        declareWinner();
    }

    /**
     * Declares the current player as the victor.
     */
    public void declareWinner() {
        turnPhase = TurnPhase.WINNER;
        gameSaver.accept(this);
    }

    /**
     * Returns an action allowing the player to end their turn. Returns moveAction() instead if the
     * player last rolled a double and is not in jail.
     */
    @Nonnull
    public Action endTurnAction() {
        if (!diceRolls.isEmpty() &&
            isDouble(diceRolls.get(diceRolls.size() - 1)) &&
            !getCurrentPlayer().isJailed()
        ) {
            return moveAction();
        }

        return Action.make("End Turn", this::endTurn);
    }

    /**
     * Finds a player who owns the given item, if one exists.
     */
    @Nonnull public Optional<Player> findPlayerForItem(@Nonnull String itemId) {
        return players.stream().filter(player -> player.getItems().has(itemId)).findFirst();
    }

    /**
     * Returns the current card, or null if there is none.
     */
    public Card getCurrentCard() {
        return currentCard;
    }

    /**
     * Sets the current card to the given card (or null.)
     */
    public void setCurrentCard(Card card) {
        currentCard = card;
        if (card != null) {
            setCurrentActions(card.getCardActions(this));
        }
    }

    /**
     * Gets the current actions.
     */
    @Nonnull
    public List<Action> getCurrentActions() {
        return currentActions;
    }

    /**
     * Sets the current actions.
     */
    public void setCurrentActions(@Nonnull List<Action> actions) {
        currentActions = actions;
    }

    /**
     * Gets the number of turns.
     */
    public int getTurns() {
        return turns;
    }

    /**
     * Gets the property state for the given property ID.
     * @throws IllegalArgumentException if the ID is not an ID of a property
     */
    @Nonnull
    public PropertyState getPropertyState(@Nonnull String id) {
        if (getBoard().getItem(id) instanceof Property) {
            if (propertyStates.containsKey(id)) {
                return propertyStates.get(id);
            } else {
                PropertyState state = new PropertyState();
                propertyStates.put(id, state);
                return state;
            }
        } else {
            throw new IllegalArgumentException("ID is not a property");
        }
    }

    /**
     * Sets the property state for the given property ID.
     * @throws IllegalArgumentException if the ID is not an ID of a property
     */
    public void setPropertyState(@Nonnull String id, @Nonnull PropertyState state) {
        if (getBoard().getItem(id) instanceof Property) {
            propertyStates.put(id, state);
        } else {
            throw new IllegalArgumentException("ID is not a property");
        }
    }

    @Override
    public void serialize(Serializer serializer) {
        serializer.accept(board.name());
        serializer.accept(players);
        serializer.accept(currentPlayerNumber);
        serializer.accept(turns);
        serializer.accept(propertyStates);
        serializer.accept(getTurnPhase() == TurnPhase.WINNER);
    }

    /**
     * Sets a callback that saves the game at the start of each turn.
     * @param saver runnable that saves the game
     */
    public void setGameSaver(Consumer<Game> saver) {
        gameSaver = saver;
    }

    /**
     * Deserializes a game object and checks for integrity.
     * @throws IOException if there was an error or if the game object is invalid
     */
    public static Game deserialize(Deserializer deserializer, List<Board> boards)
        throws IOException {
        String boardName = deserializer.readLine();
        Board board = boards.stream()
            .filter(b -> b.name().equals(boardName))
            .findFirst()
            .orElseThrow(() -> new IOException("Failed to find board with name"));

        List<Player> players = deserializer.readList(Player::deserialize);
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);

            if (player.getNumber() != i) {
                throw new IOException("Invalid player number for position in list");
            }

            if (player.getLocation() < 0 || player.getLocation() >= board.tiles().size()) {
                throw new IOException("Illegal location for player");
            }

            if (player.getLastCreditor() < -1 || player.getLastCreditor() >= players.size()) {
                throw new IOException("Illegal last creditor for player");
            }
        }

        int currentPlayerNumber = deserializer.readInt();
        if (currentPlayerNumber < 0 || currentPlayerNumber >= players.size()) {
            throw new IOException("Invalid current player number");
        }

        int turns = deserializer.readInt();

        Map<String, PropertyState> propertyStates =
            deserializer.readMap(PropertyState::deserialize);

        boolean isComplete = deserializer.readBoolean();

        return new Game(board, players, currentPlayerNumber, turns, propertyStates, isComplete);
    }
}
