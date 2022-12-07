package dev.anli.oligopoly.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that handles deserialization of objects.
 */
public class Deserializer {
    private final BufferedReader reader;

    /**
     * Constructs a deserializer with the given reader.
     */
    public Deserializer(Reader reader) {
        this.reader = new BufferedReader(reader);
    }

    /**
     * Reads a line from the reader.
     * @throws IOException if the reader threw one or if the end of the file was reached
     */
    public String readLine() throws IOException {
        String line = reader.readLine();
        if (line == null) {
            throw new IOException("Reached end of file");
        }
        return line.replace("\\n", "\n").replace("\\s", "\\");
    }

    /**
     * Reads and deserializes an integer.
     * @throws IOException if there was an error or the integer could not be decoded
     */
    public int readInt() throws IOException {
        try {
            return Integer.parseInt(readLine());
        } catch (NumberFormatException e) {
            throw new IOException("Invalid number format");
        }
    }

    /**
     * Reads and deserializes a long integer.
     * @throws IOException if there was an error or the integer could not be decoded
     */
    public long readLong() throws IOException, NumberFormatException {
        try {
            return Long.parseLong(readLine());
        } catch (NumberFormatException e) {
            throw new IOException("Invalid number format");
        }
    }

    /**
     * Reads and deserializes a boolean.
     */
    public boolean readBoolean() throws IOException {
        return readLine().equals("true");
    }

    /**
     * A function that deserializes an object.
     */
    @FunctionalInterface
    public interface DeserializationFunction<E> {
        /**
         * Deserialize an object with the given deserializer.
         * @throws IOException if there was an error or the object could not be decoded
         */
        E deserialize(Deserializer deserializer) throws IOException;
    }

    /**
     * Deserializes a list of objects by reading its size and applying the given deserialization
     * function.
     * @throws IOException if there was an error or the list could not be decoded
     */
    public <E> List<E> readList(DeserializationFunction<E> deserializationFunction)
        throws IOException {
        long size = readLong();
        List<E> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(deserializationFunction.deserialize(this));
        }
        return list;
    }

    /**
     * Deserializes a map of strings to objects by reading its size and applying the given
     * deserialization function.
     * @throws IOException if there was an error or the map could not be decoded
     */
    public <E> Map<String, E> readMap(DeserializationFunction<E> deserializationFunction)
        throws IOException {
        long size = readLong();
        Map<String, E> map = new HashMap<>();
        for (int i = 0; i < size; i++) {
            String key = readLine();
            E value = deserializationFunction.deserialize(this);
            map.put(key, value);
        }
        return map;
    }

    /**
     * Closes the underlying buffered reader.
     */
    public void close() throws IOException {
        reader.close();
    }
}
