package chat.server.command;

import chat.Command;
import chat.model.AdminDecorator;
import chat.model.ModeratorDecor;
import chat.model.User;
import chat.server.ErrorCode;
import chat.server.ServerException;
import chat.service.UserService;

import java.io.IOException;

public class GrantCommand implements Command {
    private final UserService userService;
    private final User moderator;
    private final String login;

    public GrantCommand(UserService userService, User moderator, String login) throws ServerException, IOException {
        this.userService = userService;
        this.moderator = moderator;
        this.login = login;
    }

    @Override
    public void execute() throws ServerException, IOException {
        if (!((moderator instanceof ModeratorDecor) || (moderator instanceof AdminDecorator))) {
            throw new ServerException(ErrorCode.NO_MODERATOR_RIGHTS);
        }
        if (userService.grant(login)) {
            moderator.getOutput().writeUTF(String.format("Server: %s is the new moderator!", login));
        } else {
            moderator.getOutput().writeUTF("Server: this user is already a moderator!");
        }
    }
}
