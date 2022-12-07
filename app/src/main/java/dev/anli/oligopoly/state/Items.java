package dev.anli.oligopoly.state;

import dev.anli.oligopoly.board.Board;
import dev.anli.oligopoly.board.Item;
import dev.anli.oligopoly.io.Deserializer;
import dev.anli.oligopoly.io.Serializable;
import dev.anli.oligopoly.io.Serializer;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.*;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a collection of item IDs, with possibly multiple copies of each. Note that it is
 * possible to have a negative amount of items (say, in case someone overdraws.)
 */
public final class Items implements Serializable {
    /**
     * Underlying map. At no point should this map contain a null value or 0.
     */
    private final Map<String, Integer> map;

    /**
     * Constructs an empty Items instance.
     */
    public Items() {
        map = new HashMap<>();
    }

    /**
     * Constructs an Items instance with the given item ID and quantity.
     * @param itemId item ID
     * @param quantity amount of the given item ID
     */
    public Items(@Nonnull String itemId, int quantity) {
        this();
        set(itemId, quantity);
    }

    /**
     * Constructs an Items instance as a copy of another Items instance.
     * @param other other Items instance
     */
    public Items(@Nonnull Items other) {
        this();
        map.putAll(other.toMap());
    }

    /**
     * Constructs an Items instance with quantities of another Items instance mapped by the given
     * transformation.
     * @param other other Items instance
     * @param function function to apply to the quantities
     */
    public Items(@Nonnull Items other, @Nonnull IntUnaryOperator function) {
        this();
        other.toMap().forEach((itemId, quantity) -> set(itemId, function.applyAsInt(quantity)));
    }

    /**
     * Gets the amount of a given item ID in the Items instance.
     * @param itemId item ID
     * @return amount of the given item ID
     */
    public int get(@Nonnull String itemId) {
        return map.getOrDefault(itemId, 0);
    }

    /**
     * Returns whether the Items instance has a given item ID (that is, whether there is a positive
     * amount of that item ID.)
     * @param itemId item ID
     * @return whether the Items instance has the given item ID
     */
    public boolean has(String itemId) {
        return get(itemId) > 0;
    }

    /**
     * Sets the amount of a given item ID in the Items instance.
     * @param itemId item ID
     * @param quantity quantity to set
     */
    public void set(String itemId, int quantity) {
        if (quantity == 0) {
            map.remove(itemId);
        } else {
            map.put(itemId, quantity);
        }
    }

    /**
     * Adds the given quantity of the given item ID to the Items instance.
     * @param itemId item ID
     * @param quantity quantity to add
     * @return new quantity of the item ID
     */
    public int add(String itemId, int quantity) {
        int updated = get(itemId) + quantity;
        set(itemId, updated);
        return updated;
    }

    /**
     * Returns the instance's map of item IDs to quantities.
     * <p>
     * The result of this method is unmodifiable.
     *
     * @return a map of item IDs to quantities
     */
    @Nonnull
    public Map<String, Integer> toMap() {
        return Collections.unmodifiableMap(map);
    }

    /**
     * Returns whether this Items instance contains all the items in another instance.
     * <p>
     * An Items instance is always considered to "have" a negative quantity of any particular item.
     * @param other another Items instance
     * @return whether this Items instance has the items in other
     */
    public boolean has(@Nonnull Items other) {
        return other.toMap().entrySet().stream()
            .allMatch(entry -> entry.getValue() < 0 || get(entry.getKey()) >= entry.getValue());
    }

    /**
     * Adds the items in the given Items instance to the items in this instance.
     * @param other items to add
     */
    public void add(@Nonnull Items other) {
        // We create a new map to avoid ConcurrentModificationExceptions
        Map<String, Integer> map = new HashMap<>(other.toMap());
        map.forEach(this::add);
    }

    /**
     * Subtracts the items in the given Items instance to the items in this instance.
     * @param other items to subtract
     */
    public void subtract(@Nonnull Items other) {
        Map<String, Integer> map = new HashMap<>(other.toMap());
        map.forEach((item, quantity) -> add(item, -quantity));
    }

    /**
     * Clears all items.
     */
    public void clear() {
        map.clear();
    }

    /**
     * Checks whether any of the quantities are negative.
     * @return whether any of the quantities are negative
     */
    public boolean hasDebt() {
        return toMap().values().stream().anyMatch(qty -> qty < 0);
    }

    /**
     * Returns whether the Items instance is empty (has no items.)
     * @return whether the Items instance is empty
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Items items = (Items) o;
        return map.equals(items.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(map);
    }

    /**
     * Formats this instance's contents as a string under a given board.
     */
    public String format(Board board) {
        if (isEmpty()) {
            return "-";
        }

        return toMap().entrySet().stream().map(
            entry -> board.getItem(entry.getKey()).formatQuantity(entry.getValue())
        ).collect(Collectors.joining(", "));
    }

    /**
     * Returns a sorted array of entries for each item and quantity.
     * <p>
     * Items are sorted first by order, then by their IDs.
     */
    @SuppressWarnings("unchecked")
    public Map.Entry<String, Integer>[] toEntryArray(Board board) {
        return toMap().entrySet().stream().sorted((a, b) -> {
            Item aItem = board.getItem(a.getKey());
            Item bItem = board.getItem(b.getKey());

            int order = aItem.getOrder() - bItem.getOrder();
            if (order != 0) {
                return order;
            }

            return a.getKey().compareTo(b.getKey());
        }).toList().toArray(new Map.Entry[] {});
    }

    @Override
    public void serialize(Serializer serializer) {
        serializer.accept(map.size());
        serializer.acceptAll(toMap().entrySet().stream()
            .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue().toString()))
            .toList());
    }

    /**
     * Deserializes items with the given deserializer.
     */
    public static Items deserialize(Deserializer deserializer) throws IOException {
        int size = deserializer.readInt();
        Items items = new Items();
        for (int i = 0; i < size; i++) {
            String item = deserializer.readLine();
            int quantity = deserializer.readInt();
            items.set(item, quantity);
        }
        return items;
    }
}
