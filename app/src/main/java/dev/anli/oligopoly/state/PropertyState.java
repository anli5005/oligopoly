package dev.anli.oligopoly.state;

import dev.anli.oligopoly.board.Action;
import dev.anli.oligopoly.board.property.Property;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * State of a given property.
 */
public class PropertyState {
    private boolean isMortgaged;
    private final Items items;

    /**
     * Constructs a PropertyState with the given details.
     * @param isMortgaged whether the property is mortgaged
     * @param items items the property has
     */
    public PropertyState(boolean isMortgaged, @Nonnull Items items) {
        this.isMortgaged = isMortgaged;
        this.items = items;
    }

    /**
     * Constructs an unmortgaged PropertyState without buildings.
     */
    public PropertyState() {
        this(false, new Items());
    }

    /**
     * Returns whether the property is mortgaged.
     * @return whether the property is mortgaged.
     */
    public boolean isMortgaged() {
        return isMortgaged;
    }

    /**
     * Sets whether this property is mortgaged.
     * <p>
     * Properties with items can't be mortgaged.
     *
     * @param mortgaged whether the property is mortgaged
     * @throws IllegalStateException if the property has items
     */
    public void setMortgaged(boolean mortgaged) {
        if (mortgaged && !items.isEmpty()) {
            throw new IllegalStateException("Attempt to mortgage property with items");
        }

        isMortgaged = mortgaged;
    }

    /**
     * Returns the number of houses on the property.
     * @return number of houses
     */
    public Items getItems() {
        return items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyState that = (PropertyState) o;
        return isMortgaged == that.isMortgaged && items.equals(that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isMortgaged, items);
    }

    /**
     * Returns an action to mortgage the property.
     */
    @Nonnull
    public Action mortgageAction(@Nonnull Property property) {
        return new Action() {
            @Nonnull
            @Override
            public String getName() {
                return "Mortgage";
            }

            @Nonnull
            @Override
            public Items getCost() {
                return new Items(property.getMortgagePrice(), qty -> -qty);
            }

            @Override
            public boolean isAllowed() {
                return !isMortgaged() && items.isEmpty();
            }

            @Override
            public void perform() {
                setMortgaged(true);
            }
        };
    }

    /**
     * Returns an action to unmortgage the property.
     */
    @Nonnull
    public Action unmortgageAction(@Nonnull Property property) {
        return new Action() {
            @Nonnull
            @Override
            public String getName() {
                return "Unmortgage";
            }

            @Nonnull
            @Override
            public Items getCost() {
                return new Items(property.getMortgagePrice(), qty -> qty + qty / 10);
            }

            @Override
            public boolean isAllowed() {
                return isMortgaged();
            }

            @Override
            public void perform() {
                setMortgaged(false);
            }
        };
    }
}
