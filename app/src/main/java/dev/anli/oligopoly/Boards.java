package dev.anli.oligopoly;

import dev.anli.oligopoly.board.*;
import dev.anli.oligopoly.board.card.*;
import dev.anli.oligopoly.board.property.*;
import dev.anli.oligopoly.board.tile.*;
import dev.anli.oligopoly.state.Game;
import dev.anli.oligopoly.state.Items;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Boards that come with the game.
 */
public class Boards {
    private Boards() {
        // This is purely a container for the getBoards() method.
    }

    private static Board getStandardBoard() {
        Card bookstoreCard = new GoToPropertyCard(
            "BLUE_2",
            "Go shopping!",
            "You toured some guests around Penn, and they predictably insisted on " +
                "going to the bookstore for overpriced merch. Proceed immediately to Penn " +
                "Bookstore."
        );

        List<Card> communityChest = List.of(
            GetOutOfJailFreeCard.getInstance(),
            StealCard.getInstance(),
            KillCard.getInstance(),
            ClippyCard.getInstance(),
            InstantHotelCard.getInstance(),
            GoToJail.getInstance(),
            bookstoreCard
        );

        List<Card> chance = List.of(
            GetOutOfJailFreeCard.getInstance(),
            StealCard.getInstance(),
            KillCard.getInstance(),
            ClippyCard.getInstance(),
            InstantHotelCard.getInstance(),
            GoToJail.getInstance(),
            bookstoreCard
        );

        Map<String, Item> items = new HashMap<>(Map.of(
            Money.ID, Money.getInstance(),
            GetOutOfJailFreeCard.ID, GetOutOfJailFreeCard.getInstance(),
            StealCard.getInstance().getId(), StealCard.getInstance(),
            KillCard.getInstance().getId(), KillCard.getInstance(),
            InstantHotelCard.getInstance().getId(), InstantHotelCard.getInstance(),
            House.ID, House.getInstance(),
            Hotel.ID, Hotel.getInstance()
        ));

        PropertyCategory brown = new PropertyCategory("Brown", 0, 99, 51, 18);
        items.putAll(Map.of(
            "BROWN_1", new StreetProperty(
                "DRL", brown, 60, 30, 50, 50, 2, 10, 30, 90, 160, 250
            ),
            "BROWN_2", new StreetProperty(
                "Hill House", brown, 60, 30, 50, 50, 4, 20, 60, 180, 320, 450
            )
        ));

        PropertyCategory lightBlue = new PropertyCategory("Light Blue", 1, 114, 188, 204);
        items.putAll(Map.of(
            "LIGHT_BLUE_1", new StreetProperty(
                "The Quad", lightBlue, 100, 50, 50, 50, 6, 30, 90, 270, 400, 550
            ),
            "LIGHT_BLUE_2", new StreetProperty(
                "Van Pelt", lightBlue, 100, 50, 50, 50, 6, 30, 90, 270, 400, 550
            ),
            "LIGHT_BLUE_3", new StreetProperty(
                "Pottruck", lightBlue, 120, 60, 50, 50, 8, 40, 100, 300, 450, 600
            )
        ));

        PropertyCategory magenta = new PropertyCategory("Magenta", 2, 179, 37, 131);
        items.putAll(Map.of(
            "MAGENTA_1", new StreetProperty(
                "Kelly Writer's House", magenta, 140, 70, 100, 100, 10, 50, 150, 450, 625, 750
            ),
            "MAGENTA_2", new StreetProperty(
                "Perry World House", magenta, 140, 70, 100, 100, 10, 50, 150, 450, 625, 750
            ),
            "MAGENTA_3", new StreetProperty(
                "Class of 1920 Commons", magenta, 160, 80, 100, 100, 12, 60, 180, 500, 700, 900
            )
        ));

        PropertyCategory orange = new PropertyCategory("Orange", 3, 245, 152, 12);
        items.putAll(Map.of(
            "ORANGE_1", new StreetProperty(
                "Harnwell House", orange, 180, 90, 100, 100, 14, 70, 200, 550, 750, 950
            ),
            "ORANGE_2", new StreetProperty(
                "Harrison House", orange, 180, 90, 100, 100, 14, 70, 200, 550, 750, 950
            ),
            "ORANGE_3", new StreetProperty(
                "Rodin House", orange, 200, 100, 100, 100, 16, 80, 220, 600, 800, 1000
            )
        ));

        PropertyCategory red = new PropertyCategory("Red", 4, 184, 13, 13);
        items.putAll(Map.of(
            "RED_1", new StreetProperty(
                "KCEH", red, 220, 110, 150, 150, 18, 90, 250, 700, 875, 1050
            ),
            "RED_2", new StreetProperty(
                "Gregory House", red, 220, 110, 150, 150, 18, 90, 250, 700, 875, 1050
            ),
            "RED_3", new StreetProperty(
                "Guttman House", red, 240, 120, 150, 150, 20, 100, 300, 750, 925, 1100
            )
        ));

        PropertyCategory yellow = new PropertyCategory("Yellow", 5, 255, 238, 51);
        items.putAll(Map.of(
            "YELLOW_1", new StreetProperty(
                "ARB", yellow, 260, 130, 150, 150, 22, 110, 330, 800, 975, 1150
            ),
            "YELLOW_2", new StreetProperty(
                "Towne Building", yellow, 260, 130, 150, 150, 22, 110, 330, 800, 975, 1150
            ),
            "YELLOW_3", new StreetProperty(
                "Houston Hall", yellow, 280, 140, 150, 150, 24, 120, 360, 850, 1025, 1200
            )
        ));

        PropertyCategory green = new PropertyCategory("Green", 6, 54, 171, 48);
        items.putAll(Map.of(
            "GREEN_1", new StreetProperty(
                "Moore Building", green, 300, 150, 200, 200, 26, 130, 390, 900, 1100, 1275
            ),
            "GREEN_2", new StreetProperty(
                "Skirkanich Hall", green, 300, 150, 200, 200, 26, 130, 390, 900, 1100, 1275
            ),
            "GREEN_3", new StreetProperty(
                "Huntsman Hall", green, 320, 160, 200, 200, 28, 150, 450, 1000, 1200, 1400
            )
        ));

        PropertyCategory blue = new PropertyCategory("Blue", 7, 45, 92, 186);
        items.putAll(Map.of(
            "BLUE_1", new StreetProperty(
                "M&T Office", blue, 350, 175, 200, 200, 35, 175, 500, 1100, 1300, 1500
            ),
            "BLUE_2", new StreetProperty(
                "Penn Bookstore", blue, 400, 200, 200, 200, 50, 200, 600, 1400, 1700, 2000
            )
        ));

        items.putAll(Map.of(
            "UTILITY_1", new UtilityProperty(
                "Path@Penn",
                Money.of(150), Money.of(75),
                Money.of(4), Money.of(10)
            ),
            "UTILITY_2", new UtilityProperty(
                "Penn Dining",
                Money.of(150), Money.of(75),
                Money.of(4), Money.of(10)
            )
        ));

        List<Items> railroadRents = List.of(
            Money.of(25), Money.of(50), Money.of(100), Money.of(200)
        );
        items.putAll(Map.of(
            "RAILROAD_1", new RailroadProperty(
                "Spruce Street",
                Money.of(200), Money.of(100), railroadRents
            ),
            "RAILROAD_2", new RailroadProperty(
                "Walnut Street",
                Money.of(200), Money.of(100), railroadRents
            ),
            "RAILROAD_3", new RailroadProperty(
                "Woodland Walk",
                Money.of(200), Money.of(100), railroadRents
            ),
            "RAILROAD_4", new RailroadProperty(
                "Locust Walk",
                Money.of(200), Money.of(100), railroadRents
            )
        ));

        CardTile chanceTile = new CardTile("Chance", chance);
        CardTile communityChestTile = new CardTile("Community Chest", communityChest);
        List<Tile> tiles = List.of(
            new GoTile(Money.of(200)),
            new PropertyTile("BROWN_1"),
            communityChestTile,
            new PropertyTile("BROWN_2"),
            new TaxTile("Tuition", Money.of(200)),
            new PropertyTile("RAILROAD_1"),
            new PropertyTile("LIGHT_BLUE_1"),
            chanceTile,
            new PropertyTile("LIGHT_BLUE_2"),
            new PropertyTile("LIGHT_BLUE_3"),
            new JailTile(),
            new PropertyTile("MAGENTA_1"),
            new PropertyTile("UTILITY_1"),
            new PropertyTile("MAGENTA_2"),
            new PropertyTile("MAGENTA_3"),
            new PropertyTile("RAILROAD_2"),
            new PropertyTile("ORANGE_1"),
            communityChestTile,
            new PropertyTile("ORANGE_2"),
            new PropertyTile("ORANGE_3"),
            FreeParkingTile.getInstance(),
            new PropertyTile("RED_1"),
            chanceTile,
            new PropertyTile("RED_2"),
            new PropertyTile("RED_3"),
            new PropertyTile("RAILROAD_3"),
            new PropertyTile("YELLOW_1"),
            new PropertyTile("YELLOW_2"),
            new PropertyTile("UTILITY_2"),
            new PropertyTile("YELLOW_3"),
            GoToJail.getInstance(),
            new PropertyTile("GREEN_1"),
            new PropertyTile("GREEN_2"),
            communityChestTile,
            new PropertyTile("GREEN_3"),
            new PropertyTile("RAILROAD_4"),
            chanceTile,
            new PropertyTile("BLUE_1"),
            new TaxTile("Frat Party", Money.of(100)),
            new PropertyTile("BLUE_2")
        );

        return new Board("Standard", tiles, items, Money.of(1500));
    }

    private static Board getFriendshipEndingBoard() {
        Map<String, Item> items = new HashMap<>(Map.of(
            Money.ID, Money.getInstance(),
            GetOutOfJailFreeCard.ID, GetOutOfJailFreeCard.getInstance(),
            House.ID, House.getInstance(),
            Hotel.ID, Hotel.getInstance(),
            "PROPERTY", new StreetProperty(
                "Sanity",
                new PropertyCategory("Good Luck", 0, 255, 0, 0),
                10, -9999, 1, 1,
                2000, 3000, 4000, 5000, 6000, 7000
            )
        ));

        List<Tile> tiles = new ArrayList<>();
        CardTile cardTile = new CardTile("Oh no", List.of(new Card() {
            @Nonnull
            @Override
            public String getTitle() {
                return "Game-Breaking Exploit";
            }

            @Nonnull
            @Override
            public String getCardDescription() {
                return "The only property on the board is now yours!";
            }

            @Nonnull
            @Override
            public List<Action> getCardActions(@Nonnull Game game) {
                return List.of(Action.make("Sure I guess", () -> {
                    game.getPlayers().forEach(player -> player.getItems().set("PROPERTY", 0));
                    game.getCurrentPlayer().getItems().set("PROPERTY", 1);
                    game.setCurrentActions(List.of(game.endTurnAction()));
                    game.setCurrentCard(null);
                }));
            }
        }, ClippyCard.getInstance(), GoToJail.getInstance()));
        PropertyTile propertyTile = new PropertyTile("PROPERTY");
        for (int i = 0; i < 4; i++) {
            tiles.add(GoToJail.getInstance());
            for (int j = 0; j < 9; j++) {
                if (j % 3 == 1) {
                    tiles.add(cardTile);
                } else {
                    tiles.add(propertyTile);
                }
            }
        }
        tiles.set(0, new JailTile());

        return new Board("Hate Your Fellow TAs?", tiles, items, Money.of(1500));
    }

    private static Board getDeathmatchBoard() {
        Map<String, Item> items = new HashMap<>(Map.of(
            KillCard.getInstance().getId(), KillCard.getInstance(),
            "ANTI_KILL", new OwnableCard() {
                @Override
                public String getId() {
                    return "ANTI_KILL";
                }

                @Override
                public String getIconText() {
                    return "PI";
                }

                @Nonnull
                @Override
                public String getTitle() {
                    return "Private Investigator";
                }

                @Nonnull
                @Override
                public String getCardDescription() {
                    return "Removes all Amogus Sus cards from play. " +
                        "Can only be used once per game.";
                }

                @Nonnull
                @Override
                public List<Action> getItemActions(@Nonnull String id, @Nonnull Game game) {
                    return Collections.singletonList(new Action() {
                        @Nonnull
                        @Override
                        public String getName() {
                            return "Use";
                        }

                        @Nonnull
                        @Override
                        public Items getCost() {
                            return new Items("ANTI_KILL", 1);
                        }

                        @Override
                        public boolean isAllowed() {
                            return true;
                        }

                        @Override
                        public void perform() {
                            game.getPlayers().forEach(
                                player -> player.getItems().set(KillCard.getInstance().getId(), 0)
                            );
                        }
                    });
                }
            }
        ));

        List<Tile> tiles = new ArrayList<>();

        CardTile cardTile = new CardTile("!!!", List.of(KillCard.getInstance()));
        for (int i = 0; i < 4; i++) {
            tiles.add(FreeParkingTile.getInstance());
            for (int j = 0; j < 9; j++) {
                if (j % 3 == 1) {
                    tiles.add(cardTile);
                } else {
                    tiles.add(FreeParkingTile.getInstance());
                }
            }
        }

        return new Board("Deathmatch", tiles, items, new Items("ANTI_KILL", 1));
    }

    /**
     * Gets a list of boards.
     */
    public static List<Board> getBoards() {
        return List.of(
            getStandardBoard(),
            getFriendshipEndingBoard(),
            getDeathmatchBoard()
        );
    }
}
