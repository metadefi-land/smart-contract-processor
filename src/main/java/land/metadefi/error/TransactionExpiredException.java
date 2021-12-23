package land.metadefi.error;

public class TransactionExpiredException extends RuntimeException {

    public TransactionExpiredException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
