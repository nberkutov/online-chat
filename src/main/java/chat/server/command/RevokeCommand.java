package chat.server.command;

import chat.Command;
import chat.model.AdminDecorator;
import chat.model.User;
import chat.server.ErrorCode;
import chat.server.ServerException;
import chat.service.UserService;

import java.io.IOException;

public class RevokeCommand implements Command {
    private UserService userService;
    private User moderator;
    private String login;

    public RevokeCommand(UserService userService, User moderator, String login) {
        this.userService = userService;
        this.moderator = moderator;
        this.login = login;
    }

    @Override
    public void execute() throws ServerException, IOException {
        if (!userService.isModerator(moderator.getLogin()) && !(moderator instanceof AdminDecorator)) {
            throw new ServerException(ErrorCode.NO_MODERATOR_RIGHTS);
        }
        if (userService.revoke(login)) {
            moderator.getOutput().writeUTF(String.format("Server: %s is no longer a moderator!", login));
        } else {
            moderator.getOutput().writeUTF("Server: this user is not a moderator!");
        }
    }
}
