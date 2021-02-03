package chat.server.command;

import chat.Command;
import chat.model.User;
import chat.server.ServerException;
import chat.service.UserService;

public class RegistrationCommand implements Command {
    private User user;
    private String password;
    private UserService userService;

    public RegistrationCommand(UserService userService, User user, String password) {
        this.user = user;
        this.password = password;
        this.userService = userService;
    }

    public void execute() throws ServerException {
        userService.registerUser(user, password);
    }
}