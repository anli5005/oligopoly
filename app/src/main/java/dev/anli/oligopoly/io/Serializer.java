package dev.anli.oligopoly.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serializer that serializes objects to a string.
 */
public class Serializer {
    private final List<String> strings = new ArrayList<>();

    /**
     * Adds a string to the serialization.
     */
    public void accept(String string) {
        strings.add(string);
    }

    /**
     * Adds each of the strings in the list to the serialization, without saving the list's size.
     */
    public void acceptAll(List<String> strings) {
        this.strings.addAll(strings);
    }

    /**
     * Adds a {@link Serializable} object to the serialization.
     */
    public void accept(Serializable serializable) {
        serializable.serialize(this);
    }

    /**
     * Adds an integer to the serialization.
     */
    public void accept(int x) {
        accept(String.valueOf(x));
    }

    /**
     * Adds a long integer to the serialization.
     */
    public void accept(long x) {
        accept(String.valueOf(x));
    }

    /**
     * Adds a boolean to the serialization.
     */
    public void accept(boolean x) {
        accept(x ? "true" : "false");
    }

    /**
     * Adds a list of serializables to the serialization, saving the list's size, so it can be
     * deserialized later.
     */
    public void accept(List<? extends Serializable> serializables) {
        accept(serializables.size());
        serializables.forEach(this::accept);
    }

    /**
     * Adds a map of strings to serializables to the serialization.
     */
    public void accept(Map<String, ? extends Serializable> map) {
        accept(map.size());
        map.forEach((key, value) -> {
            accept(key);
            accept(value);
        });
    }

    /**
     * Exports the serialization as a string.
     */
    public String dump() {
        return strings.stream()
            .map(str -> str.replace("\\", "\\s"))
            .map(str -> str.replace("\n", "\\n"))
            .collect(Collectors.joining("\n"));
    }
}
