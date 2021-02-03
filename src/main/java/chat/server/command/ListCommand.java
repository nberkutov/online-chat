package chat.server.command;

import chat.Command;
import chat.model.User;
import chat.service.UserService;

import java.io.IOException;
import java.util.Map;

public class ListCommand implements Command {
    private final UserService userService;
    private final User user;

    public ListCommand(UserService userService, User user) {
        this.userService = userService;
        this.user = user;
    }

    @Override
    public void execute() throws IOException {
        Map<String, User> usersOnline = userService.getUsersOnline();
        if (usersOnline.size() == 1) {
            user.getOutput().writeUTF("Server: no one online");
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (var onlineUser : usersOnline.values()) {
            if (!onlineUser.getLogin().equals(user.getLogin())) {
                stringBuilder.append(onlineUser.getLogin()).append(" ");
            }
        }
        user.getOutput().writeUTF("Server: online: " + stringBuilder.toString());
    }
}