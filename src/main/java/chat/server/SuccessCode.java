package chat.server;

public enum SuccessCode {
    AUTH_SUCCESS("Server: you are authorized successfully!"),
    REG_SUCCESS("Server: you are registered successfully!");

    private String message;

    SuccessCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
