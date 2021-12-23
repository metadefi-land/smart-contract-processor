package land.metadefi.error;

public class TokenIdInvalidException extends RuntimeException {

    public TokenIdInvalidException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
