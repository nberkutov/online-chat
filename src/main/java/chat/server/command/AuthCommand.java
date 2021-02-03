package chat.server.command;

import chat.Command;
import chat.model.ModeratorDecor;
import chat.model.User;
import chat.server.ServerException;
import chat.service.UserService;

public class AuthCommand implements Command {
    private final UserService userService;
    private final String password;
    private User user;

    public AuthCommand(UserService userService, User user, String password) {
        this.userService = userService;
        this.user = user;
        this.password = password;
    }

    @Override
    public void execute() throws ServerException {
        if (userService.isModerator(user.getLogin())) {
            user = new ModeratorDecor(user);
        }
        userService.authUser(user, password);
    }
}