package chat.server;

public enum ServerCommand {
    AUTH("/auth"),
    REGISTRATION("/registration"),
    KICK("/kick"),
    GRANT("/grant"),
    REVOKE("/revoke"),
    UNREAD("/unread"),
    STATS("/stats"),
    LIST("/list"),
    CHAT("/chat"),
    HISTORY("/history");

    private String command;

    ServerCommand(String command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return command;
    }

    public static ServerCommand getValue(String value) throws ServerException {
        switch (value) {
            case "/auth":
                return AUTH;
            case "/registration":
                return REGISTRATION;
            case "/list":
                return LIST;
            case "/chat":
                return CHAT;
            case "/kick":
                return KICK;
            case "/grant":
                return GRANT;
            case "/revoke":
                return REVOKE;
            case "/unread":
                return UNREAD;
            case "/stats":
                return STATS;
            case "/history":
                return HISTORY;
            default:
                throw new ServerException(ErrorCode.INVALID_COMMAND);
        }
    }
}
