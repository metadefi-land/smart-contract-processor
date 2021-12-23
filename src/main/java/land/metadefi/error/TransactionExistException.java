package land.metadefi.error;

public class TransactionExistException extends RuntimeException {

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
