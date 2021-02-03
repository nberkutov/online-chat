package chat.server;

public class ServerException extends Exception {
    private ErrorCode errorCode;

    public ServerException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}
