package dev.anli.oligopoly;

import dev.anli.oligopoly.board.Board;
import dev.anli.oligopoly.board.Item;
import dev.anli.oligopoly.io.Deserializer;
import dev.anli.oligopoly.io.Serializer;
import dev.anli.oligopoly.state.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

import static org.junit.jupiter.api.Assertions.*;

public class ItemsTest {
    private static Board board;

    private record TestItem(
        int order, IntFunction<String> formatter
    ) implements Item {
        @Nonnull
        @Override
        public String getName() {
            return "";
        }

        @Override
        public int getOrder() {
            return order;
        }

        @Override
        public boolean isFungible() {
            return false;
        }

        @Nonnull
        @Override
        public String formatQuantity(int quantity) {
            return formatter.apply(quantity);
        }
    }

    @BeforeAll
    public static void setupBoard() {
        board = new Board(
            "Test",
            Collections.emptyList(),
            Map.of(
                "A", new TestItem(3, qty -> String.format("%da", qty)),
                "B", new TestItem(-1, qty -> String.format("%db", qty)),
                "C", new TestItem(-1, qty -> String.format("%dc", qty)),
                "D", new TestItem(0, qty -> String.format("%dd", qty))
            ),
            new Items()
        );
    }

    @Test public void defaultConstructorCreatesEmpty() {
        Items items = new Items();
        assertTrue(items.isEmpty());
        assertEquals(0, items.get("GENERIC_ITEM_ID"));
    }

    @Test public void constructorCreatesEmptyOnZero() {
        Items items = new Items("STUFF", 0);
        assertTrue(items.isEmpty());
        assertEquals(0, items.get("STUFF"));
        assertEquals(0, items.get("OTHER_STUFF"));
    }

    @Test public void constructorCreatesItemWithIdAndQuantity() {
        Items items = new Items("STUFF", 123);
        assertFalse(items.isEmpty());
        assertEquals(123, items.get("STUFF"));
        assertEquals(0, items.get("OTHER_STUFF"));
    }

    @Test public void constructorCopiesItemsInstance() {
        Items original = new Items("STUFF", 123);
        Items copy = new Items(original);

        assertEquals(123, copy.get("STUFF"));
        assertEquals(0, copy.get("OTHER_STUFF"));

        // Try changing the copy to see if we can mess with the original
        copy.set("STUFF", 234);
        copy.set("OTHER_STUFF", 123);

        assertEquals(234, copy.get("STUFF"));
        assertEquals(123, copy.get("OTHER_STUFF"));
        assertEquals(123, original.get("STUFF"));
        assertEquals(0, original.get("OTHER_STUFF"));
    }

    @Test public void constructorMapsQuantities() {
        Items original = new Items();
        original.set("THING", 2);
        original.set("OTHER_THING", -3);

        Items squared = new Items(original, qty -> qty * qty);

        assertEquals(2, original.get("THING"));
        assertEquals(-3, original.get("OTHER_THING"));
        assertEquals(4, squared.get("THING"));
        assertEquals(9, squared.get("OTHER_THING"));
    }

    @Test public void hasNegativeOfItem() {
        Items items = new Items("THING", -1);
        assertFalse(items.has("THING"));
    }

    @Test public void hasZeroOfItem() {
        Items items = new Items();
        assertFalse(items.has("THING"));
    }

    @Test public void hasPositiveOfItem() {
        Items items = new Items("THING", 1);
        assertTrue(items.has("THING"));
    }

    @Test public void setEmptiesItems() {
        Items items = new Items("THING", 45);
        items.set("THING", 0);

        assertTrue(items.isEmpty());
        assertEquals(0, items.get("THING"));
    }

    @Test public void setUnemptiesItems() {
        Items items = new Items();
        items.set("THING", 234);

        assertFalse(items.isEmpty());
        assertEquals(234, items.get("THING"));
    }

    @Test public void isEmptyConsidersNegativeItems() {
        Items items = new Items("THING", -1);
        assertFalse(items.isEmpty());
        assertEquals(-1, items.get("THING"));
    }

    @Test public void addZeroOfItem() {
        Items items = new Items("THING", 12);
        items.add("THING", 0);
        assertEquals(12, items.get("THING"));
    }

    @Test public void addZeroOfItemToEmptyItems() {
        Items items = new Items();
        items.add("THING", 0);
        assertTrue(items.isEmpty());
    }

    @Test public void addPositiveOfItem() {
        Items items = new Items("THING", 5);
        items.add("THING", 7);
        assertEquals(12, items.get("THING"));
    }

    @Test public void addPositiveOfItemToEmptyItems() {
        Items items = new Items();
        items.add("THING", 7);
        assertFalse(items.isEmpty());
        assertEquals(7, items.get("THING"));
    }

    @Test public void addNegativeOfItem() {
        Items items = new Items("THING", 5);
        items.add("THING", -7);
        assertEquals(-2, items.get("THING"));
    }

    @Test public void addNegativeOfItemToEmptyItems() {
        Items items = new Items();
        items.add("THING", -7);
        assertFalse(items.isEmpty());
        assertEquals(-7, items.get("THING"));
    }

    @Test public void addEmptiesItems() {
        Items items = new Items("THING", -12);
        items.add("THING", 12);
        assertTrue(items.isEmpty());
    }

    @Test public void toMapReturnsMap() {
        Items items = new Items("THING", 12);
        items.set("OTHER_THING", -24);
        items.set("EVEN_MORE_OTHER_THING", 0);
        Map<String, Integer> map = items.toMap();

        assertEquals(2, map.size());
        assertEquals(12, map.get("THING"));
        assertEquals(-24, map.get("OTHER_THING"));
    }

    @Test public void toMapReturnsUnmodifiableMap() {
        Items items = new Items("THING", 12);
        Map<String, Integer> map = items.toMap();

        //noinspection DataFlowIssue
        assertThrows(UnsupportedOperationException.class, () -> map.put("THING", 24));
        assertEquals(12, items.get("THING"));
    }

    @Test public void toMapReturnsEmptyMap() {
        Items items = new Items();
        assertTrue(items.toMap().isEmpty());
    }

    @Test public void hasEmptyItems() {
        Items items = new Items("THING", 2134);
        assertTrue(items.has(new Items()));
    }

    @Test public void hasNegativeItems() {
        Items items = new Items("THING", -100);
        assertTrue(items.has(new Items("THING", -1)));
    }

    @Test public void hasTypicalItemsTrue() {
        Items a = new Items();
        a.set("THING", 1234);
        a.set("OTHER_THING", 234);
        a.set("QWERQWER", 12345);

        Items b = new Items();
        b.set("THING", 1);
        b.set("OTHER_THING", 34);
        assertTrue(a.has(b));
    }

    @Test public void hasTypicalItemsFalse() {
        Items a = new Items();
        a.set("THING", 1234);
        a.set("OTHER_THING", 20);
        a.set("QWERQWER", 30);

        Items b = new Items();
        b.set("THING", 12343);
        b.set("OTHER_THING", 10);
        assertFalse(a.has(b));
    }

    @Test public void hasSelf() {
        Items items = new Items();
        items.set("THING", 1234);
        items.set("OTHER_THING", 20);
        items.set("QWERQWER", 30);

        assertTrue(items.has(items));
    }

    @Test public void emptyItemsHasEmptyItems() {
        Items items = new Items();
        assertTrue(items.has(new Items()));
    }

    @Test public void emptyItemsHasItem() {
        Items items = new Items();
        assertFalse(items.has(new Items("$RQWEr", 234)));
    }

    @Test public void addItems() {
        Items items = new Items("A", 1);

        Items toAdd = new Items();
        toAdd.set("A", -2);
        toAdd.set("WWER", 234);

        items.add(toAdd);
        assertEquals(-1, items.get("A"));
        assertEquals(234, items.get("WWER"));
        assertEquals(0, items.get("WEQRQWER"));
    }

    @Test public void addEmptyItems() {
        Items items = new Items("A", 1);
        items.add(new Items());
        assertEquals(1, items.get("A"));
        assertEquals(0, items.get("WWER"));
    }

    @Test public void addItemsEmptiesItems() {
        Items items = new Items();
        items.set("A", 45);
        items.set("B", -1234);

        items.add(new Items(items, qty -> -qty));
        assertTrue(items.isEmpty());
    }

    @Test public void addSelf() {
        Items items = new Items();
        items.set("A", 45);
        items.set("B", -1234);

        items.add(items);

        assertEquals(90, items.get("A"));
        assertEquals(-2468, items.get("B"));
    }

    @Test public void subtractItems() {
        Items items = new Items("A", 1);

        Items toAdd = new Items();
        toAdd.set("A", -2);
        toAdd.set("WWER", 234);

        items.subtract(toAdd);
        assertEquals(3, items.get("A"));
        assertEquals(-234, items.get("WWER"));
        assertEquals(0, items.get("WEQRQWER"));
    }

    @Test public void subtractEmptyItems() {
        Items items = new Items("A", 1);
        items.subtract(new Items());
        assertEquals(1, items.get("A"));
        assertEquals(0, items.get("WWER"));
    }

    @Test public void subtractItemsEmptiesItems() {
        Items items = new Items();
        items.set("A", 45);
        items.set("B", -1234);

        items.subtract(items);
        assertTrue(items.isEmpty());
    }

    @Test public void clearEmptyItems() {
        Items items = new Items();
        items.clear();
        assertTrue(items.isEmpty());
    }

    @Test public void clearItems() {
        Items items = new Items();
        items.set("A", 45);
        items.set("B", -1234);

        items.clear();

        assertTrue(items.isEmpty());
        assertEquals(0, items.get("A"));
        assertEquals(0, items.get("B"));
    }

    @Test public void hasDebtEmpty() {
        Items items = new Items();
        assertFalse(items.hasDebt());
    }

    @Test public void hasDebtPositive() {
        Items items = new Items();
        items.set("A", 45);
        items.set("B", 1234);

        assertFalse(items.hasDebt());
    }

    @Test public void hasDebtNegative() {
        Items items = new Items();
        items.set("A", 45);
        items.set("B", -1234);

        assertTrue(items.hasDebt());
    }

    @Test public void equalsNull() {
        Items items = new Items("F", 2);
        assertNotEquals(null, items);
    }

    @Test public void equalsOtherClass() {
        Items items = new Items("F", 2);
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals("hi!", items);
    }

    @Test public void equalsEmptyItems() {
        Items items = new Items();
        assertEquals(new Items(), items);
    }

    @Test public void emptyItemsDoesNotEqualNonemptyItems() {
        Items a = new Items("THING", 2);
        Items b = new Items();
        assertNotEquals(a, b);
        assertNotEquals(b, a);
    }

    @Test public void itemsEqualsOtherItems() {
        Items items = new Items();
        items.set("A", 342);
        items.set("B", -234);

        assertEquals(new Items(items), items);
    }

    @Test public void itemsDoesNotEqualOtherItems() {
        Items a = new Items();
        a.set("A", 342);
        a.set("B", -234);

        Items b = new Items(a);
        b.set("B", -200);

        assertNotEquals(a, b);
    }

    @Test public void testHashCode() {
        Items a = new Items("A", 234);
        Items b = new Items("A", 234);
        Items c = new Items("A", 235);

        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a.hashCode(), c.hashCode());
    }

    @Test public void formatEmpty() {
        Items items = new Items();
        assertEquals("-", items.format(board));
    }

    @Test public void formatSingle() {
        Items items = new Items("B", 45);
        assertEquals("45b", items.format(board));
    }

    @Test public void formatMultiple() {
        Items items = new Items();
        items.set("A", 0);
        items.set("B", -435);
        items.set("C", 2);

        // Items are inherently unordered, so there's more than one possible
        // way to format.
        List<String> possible = List.of("-435b, 2c", "2c, -435b");
        assertTrue(possible.contains(items.format(board)));
    }

    @Test public void toEntryArrayEmpty() {
        Items items = new Items();
        assertArrayEquals(new Map.Entry[] {}, items.toEntryArray(board));
    }

    @Test public void toEntryArraySingle() {
        Items items = new Items("C", 56);
        Map.Entry<String, Integer>[] entries = items.toEntryArray(board);

        assertEquals(1, entries.length);
        assertEquals("C", entries[0].getKey());
        assertEquals(56, entries[0].getValue());
    }

    @Test public void toEntryArrayMultiple() {
        Items items = new Items();
        items.set("A", -234);
        items.set("B", 234);
        items.set("C", 1);
        items.set("D", 69420);

        Map.Entry<String, Integer>[] entries = items.toEntryArray(board);

        assertEquals(4, entries.length);

        assertEquals("B", entries[0].getKey());
        assertEquals(234, entries[0].getValue());

        assertEquals("C", entries[1].getKey());
        assertEquals(1, entries[1].getValue());

        assertEquals("D", entries[2].getKey());
        assertEquals(69420, entries[2].getValue());

        assertEquals("A", entries[3].getKey());
        assertEquals(-234, entries[3].getValue());
    }

    private static Items deserialize(String string) throws IOException {
        StringReader reader = new StringReader(string);
        Deserializer deserializer = new Deserializer(reader);
        return Items.deserialize(deserializer);
    }

    @Test public void serializeDeserializeEmpty() throws IOException {
        Serializer serializer = new Serializer();
        serializer.accept(new Items());
        assertEquals("0", serializer.dump());
        assertEquals(new Items(), deserialize(serializer.dump()));
    }

    @Test public void serializeDeserializeSingle() throws IOException {
        Serializer serializer = new Serializer();
        serializer.accept(new Items("234", 45));
        assertEquals("1\n234\n45", serializer.dump());
        assertEquals(new Items("234", 45), deserialize(serializer.dump()));
    }

    @Test public void serializeDeserializeMultiple() throws IOException {
        Items items = new Items();
        items.set("A", 0);
        items.set("B\nD", -435);
        items.set("C\\", 2);

        Serializer serializer = new Serializer();
        serializer.accept(items);

        // Items are inherently unordered, so there's more than one possible
        // way to format.
        List<String> possible = List.of("2\nB\\nD\n-435\nC\\s\n2", "2\nC\\s\n2\nB\\nD\n-435");
        assertTrue(possible.contains(serializer.dump()));

        assertEquals(items, deserialize(serializer.dump()));
    }

    @Test public void deserializeMalformedSize() {
        IOException e = assertThrows(
            IOException.class,
            () -> deserialize("Hi there!")
        );

        assertEquals("Invalid number format", e.getMessage());
    }

    @Test public void deserializeMalformedQuantity() {
        IOException e = assertThrows(
            IOException.class,
            () -> deserialize("1\nTHING\naSDFDF")
        );

        assertEquals("Invalid number format", e.getMessage());
    }

    @Test public void deserializeNotEnoughItems() {
        IOException e = assertThrows(
            IOException.class,
            () -> deserialize("1")
        );

        assertEquals("Reached end of file", e.getMessage());
    }

    @Test public void deserializeNoQuantity() {
        IOException e = assertThrows(
            IOException.class,
            () -> deserialize("1\nHI")
        );

        assertEquals("Reached end of file", e.getMessage());
    }

}
