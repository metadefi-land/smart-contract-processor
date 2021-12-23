package land.metadefi.error;

public class TransactionFromInvalidException extends RuntimeException {

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
