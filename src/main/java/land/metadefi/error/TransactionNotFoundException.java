package land.metadefi.error;

public class TransactionNotFoundException extends RuntimeException {

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
