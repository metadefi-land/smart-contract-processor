package land.metadefi.error;

public class TransactionToInvalidException extends RuntimeException {

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
