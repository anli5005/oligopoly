package dev.anli.oligopoly.board.property;

import dev.anli.oligopoly.board.Action;
import dev.anli.oligopoly.board.Money;
import dev.anli.oligopoly.state.Game;
import dev.anli.oligopoly.state.Items;
import dev.anli.oligopoly.state.Player;
import dev.anli.oligopoly.state.PropertyState;

import javax.annotation.Nonnull;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A typical Monopoly property representing a street.
 */
public record StreetProperty(
    String name,
    PropertyCategory category,
    Items buyPrice,
    Items mortgagePrice,
    Items housePrice,
    Items hotelPrice,
    Items baseRent,
    List<Items> houseRent,
    Items hotelRent
) implements Property {
    /**
     * Convenience initializer for a street property.
     */
    public StreetProperty(
        String name, PropertyCategory category, int buy, int mortgage, int house, int hotel,
        int rent, int house1Rent, int house2Rent, int house3Rent, int house4Rent, int hotelRent
    ) {
        this(
            name, category, Money.of(buy), Money.of(mortgage), Money.of(house), Money.of(hotel),
            Money.of(rent), List.of(
                Money.of(house1Rent),
                Money.of(house2Rent),
                Money.of(house3Rent),
                Money.of(house4Rent)
            ), Money.of(hotelRent)
        );
    }

    @Nonnull
    @Override
    public String getName() {
        return name();
    }

    @Nonnull
    @Override
    public List<Action> getItemActions(@Nonnull String id, @Nonnull Game game) {
        boolean hasMonopoly = game.getCurrentPlayer().hasMonopoly(category(), game.getBoard());
        PropertyState state = game.getPropertyState(id);
        List<Action> result = new ArrayList<>(Property.super.getItemActions(id, game));
        result.addAll(List.of(
            new Action() {
                @Nonnull
                @Override
                public String getName() {
                    return "Buy House";
                }

                @Nonnull
                @Override
                public Items getCost() {
                    return housePrice;
                }

                @Override
                public boolean isAllowed() {
                    return hasMonopoly
                        && !state.isMortgaged()
                        && game.getCurrentPlayer().getItems().has(housePrice)
                        && state.getItems().get(House.ID) < houseRent().size()
                        && !state.getItems().has(Hotel.ID);
                }

                @Override
                public void perform() {
                    state.getItems().add(House.ID, 1);
                }
            },
            new Action() {
                @Nonnull
                @Override
                public String getName() {
                    return "Sell House";
                }

                @Nonnull
                @Override
                public Items getCost() {
                    return new Items(housePrice, qty -> -qty / 2);
                }

                @Override
                public boolean isAllowed() {
                    return state.getItems().has(House.ID);
                }

                @Override
                public void perform() {
                    state.getItems().add(House.ID, -1);
                }
            },
            new Action() {
                @Nonnull
                @Override
                public String getName() {
                    return "Buy Hotel";
                }

                @Nonnull
                @Override
                public Items getCost() {
                    return hotelPrice;
                }

                @Override
                public boolean isAllowed() {
                    return hasMonopoly
                        && !state.isMortgaged()
                        && game.getCurrentPlayer().getItems().has(hotelPrice)
                        && state.getItems().get(House.ID) == houseRent().size()
                        && !state.getItems().has(Hotel.ID);
                }

                @Override
                public void perform() {
                    state.getItems().set(House.ID, 0);
                    state.getItems().add(Hotel.ID, 1);
                }
            },
            new Action() {
                @Nonnull
                @Override
                public String getName() {
                    return "Sell Hotel";
                }

                @Nonnull
                @Override
                public Items getCost() {
                    return new Items(hotelPrice, qty -> -qty / 2);
                }

                @Override
                public boolean isAllowed() {
                    return state.getItems().has(Hotel.ID);
                }

                @Override
                public void perform() {
                    state.getItems().set(Hotel.ID, 0);
                }
            }
        ));
        return result;
    }

    @Nonnull
    @Override
    public PropertyCategory getCategory() {
        return category();
    }

    @Nonnull
    @Override
    public Items getBuyPrice() {
        return buyPrice;
    }

    @Nonnull
    @Override
    public Items getMortgagePrice() {
        return mortgagePrice;
    }

    @Nonnull
    @Override
    public Items getRent(@Nonnull PropertyState state, @Nonnull Player owner, @Nonnull Game game) {
        if (state.getItems().has(Hotel.ID)) {
            return hotelRent;
        }

        int houses = state.getItems().get(House.ID);
        if (houses > 0 && houses <= houseRent.size()) {
            return houseRent.get(houses - 1);
        }

        if (owner.hasMonopoly(category(), game.getBoard())) {
            return new Items(baseRent, qty -> qty * 2);
        }

        return baseRent;
    }

    @Nonnull
    @Override
    public List<String> getDisplayStats(@Nonnull String id, @Nonnull Game game) {
        List<String> stats = new ArrayList<>(Property.super.getDisplayStats(id, game));

        stats.add(String.format("Base Rent: %s", baseRent.format(game.getBoard())));
        stats.add(String.format(
            "w/ color group: %s", new Items(baseRent, qty -> qty * 2).format(game.getBoard()))
        );

        for (int i = 0; i < houseRent.size(); i++) {
            stats.add(String.format(
                "w/ %d houses: %s", i + 1, houseRent.get(i).format(game.getBoard())
            ));
        }

        stats.add(String.format("w/ hotel: %s", hotelRent.format(game.getBoard())));

        return stats;
    }
}
