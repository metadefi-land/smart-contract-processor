package land.metadefi.error;

public class NFTTypeInvalidException extends RuntimeException {

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
