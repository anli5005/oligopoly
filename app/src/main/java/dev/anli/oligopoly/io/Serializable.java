package dev.anli.oligopoly.io;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An interface that represents data that can be serialized.
 */
public interface Serializable {
    /**
     * Serializes this object with the given serializer.
     */
    void serialize(Serializer serializer);
}
