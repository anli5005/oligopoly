package dev.anli.oligopoly.board.property;

import javax.annotation.Nonnull;

/**
 * A property category.
 */
public record PropertyCategory(@Nonnull String name, int order, int red, int green, int blue) {
    // It's nice when Java handles stuff for you
}
