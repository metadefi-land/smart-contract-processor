package land.metadefi.enumrable;

public enum MintStatus {
    PENDING("PENDING"),
    ;

    private final String status;

    MintStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }
}
