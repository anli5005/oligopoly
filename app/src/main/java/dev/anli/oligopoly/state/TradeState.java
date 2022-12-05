package dev.anli.oligopoly.state;

/**
 * State of a trade.
 */
public enum TradeState {
    /**
     * The trade has been proposed.
     */
    PROPOSED,

    /**
     * The trade was rejected by either party.
     */
    REJECTED,

    /**
     * The trade was accepted by the destination.
     */
    ACCEPTED,

    /**
     * The trade was completed by the sender.
     */
    COMPLETED
}
