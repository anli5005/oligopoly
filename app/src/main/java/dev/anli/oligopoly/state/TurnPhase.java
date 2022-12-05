package dev.anli.oligopoly.state;

/**
 * The current phase of a turn.
 */
public enum TurnPhase {
    /**
     * The player is starting their turn.
     */
    START,
    /**
     * The player has not moved yet.
     */
    PREMOVE,
    /**
     * The player has moved.
     */
    POSTMOVE,
    /**
     * The player is the winner of the game.
     */
    WINNER
}
