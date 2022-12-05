package dev.anli.oligopoly.state;

/**
 * A trade offer.
 */
public class Trade {
    private final int sender;
    private final int recipient;
    private final Items items;
    private TradeState state = TradeState.PROPOSED;

    public Trade(int sender, int recipient, Items offer) {
        this.sender = sender;
        this.recipient = recipient;

        items = offer;
    }

    /**
     * Accepts the trade with the given offer.
     * @param offer items to offer to the sender
     * @throws IllegalStateException if the state is not PROPOSED
     */
    public void accept(Items offer) {
        if (state != TradeState.PROPOSED) {
            throw new IllegalStateException("Not a proposed trade");
        }

        items.subtract(offer);
        state = TradeState.ACCEPTED;
    }

    /**
     * Rejects the trade.
     * @throws IllegalStateException if the state is COMPLETED
     */
    public void reject() {
        if (state == TradeState.COMPLETED) {
            throw new IllegalStateException("Can't reject a completed trade");
        }

        state = TradeState.REJECTED;
    }

    /**
     * Gets the items to be given up by the sender.
     */
    public Items getSenderOffer() {
        return new Items(items, qty -> Math.max(0, qty));
    }

    /**
     * Gets the items to be given up by the recipient.
     */
    public Items getRecipientOffer() {
        return new Items(items, qty -> -Math.min(0, qty));
    }

    /**
     * Checks whether the trade can be fulfilled.
     */
    public boolean isFulfillable(Game game) {
        Player senderPlayer = game.getPlayers().get(sender);
        Player recipientPlayer = game.getPlayers().get(recipient);

        return senderPlayer.getItems().has(getSenderOffer()) &&
            recipientPlayer.getItems().has(getRecipientOffer());
    }

    /**
     * Completes the trade.
     * @throws IllegalStateException if the state is not ACCEPTED
     */
    public void complete(Game game) {
        if (state != TradeState.ACCEPTED) {
            throw new IllegalStateException("Not an accepted trade");
        }

        game.getPlayers().get(sender).getItems().subtract(items);
        game.getPlayers().get(recipient).getItems().add(items);
    }
}
