package chat.server.command;

import chat.Command;
import chat.model.User;
import chat.server.ServerException;
import chat.service.UserService;

import java.io.IOException;
import java.util.Set;

public class UnreadCommand implements Command {
    private UserService userService;
    private User user;

    public UnreadCommand(UserService userService, User user) {
        this.userService = userService;
        this.user = user;
    }

    @Override
    public void execute() throws ServerException, IOException {
        StringBuilder stringBuilder = new StringBuilder();
        Set<String> unread = userService.getUnread(user.getLogin());
        if (unread.isEmpty()) {
            user.getOutput().writeUTF("Server: no one unread");
            return;
        }
        for (var sender : unread) {
            stringBuilder.append(sender).append(" ");
        }
        user.getOutput().writeUTF(String.format("Server: unread from: %s", stringBuilder));
    }
}
