package land.metadefi.enumrable;

public enum NFTType {
    LAND("LAND"),
    HERO("HERO");

    private final String name;

    NFTType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
