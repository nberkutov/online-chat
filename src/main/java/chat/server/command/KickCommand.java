package chat.server.command;

import chat.Command;
import chat.model.AdminDecorator;
import chat.model.ModeratorDecor;
import chat.model.User;
import chat.server.ErrorCode;
import chat.server.ServerException;
import chat.service.UserService;

import java.io.IOException;

public class KickCommand implements Command {
    private final UserService userService;
    private final String login;
    private final User moderator;

    public KickCommand(UserService userService, User moderator, String login) {
        this.userService = userService;
        this.moderator = moderator;
        this.login = login;
    }

    @Override
    public void execute() throws ServerException, IOException {
        if (login.equals(moderator.getLogin())) {
            throw new ServerException(ErrorCode.SELF_KICK_ATTEMPT);
        }
        if (!userService.isModerator(moderator.getLogin()) && !(moderator instanceof AdminDecorator)) {
            throw new ServerException(ErrorCode.NO_MODERATOR_RIGHTS);
        }
        if (userService.getUsersOnline().get(login) instanceof AdminDecorator) {
            throw new ServerException(ErrorCode.NO_MODERATOR_RIGHTS);
        }
        userService.kickUser(login);
        moderator.getOutput().writeUTF(String.format("Server: %s was kicked!", login));
    }
}
