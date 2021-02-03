package chat.server;

public enum ErrorCode {
    ADDRESSEE_IS_UNKNOWN("Server: use /list command to choose a user to text!"),
    NOT_AUTHENTICATED("Server: you are not in the chat!"),
    INVALID_LOGIN("Server: incorrect login!"),
    INVALID_PASSWORD("Server: incorrect password!"),
    REGISTER_ADMIN_ATTEMPT("Server: The user admin already exist!"),
    SHORT_PASSWORD("Server: the password is too short!"),
    LOGIN_IS_TAKEN("Server: this login is already taken! Choose another one."),
    USER_IS_OFFLINE("Server: the user is not online!"),
    INVALID_COMMAND("Server: incorrect command!"),

    USER_BANNED("Server: you are banned!"),
    SELF_KICK_ATTEMPT("Server: you can't kick yourself!"),
    NO_MODERATOR_RIGHTS("Server: you are not a moderator or an admin!"),
    NO_ADMIN_RIGHTS("Server: you are not an admin!");

    private String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
