package land.metadefi.error;

public class ValueNotEqualException extends RuntimeException {

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
