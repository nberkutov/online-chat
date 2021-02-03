package chat.server.command;

import chat.Command;
import chat.model.Message;
import chat.model.User;
import chat.server.ErrorCode;
import chat.server.ServerException;
import chat.service.UserService;

import java.io.IOException;
import java.util.List;

public class StatsCommand implements Command {
    private final UserService userService;
    private final User user;

    public StatsCommand(UserService userService, User user) {
        this.userService = userService;
        this.user = user;
    }

    @Override
    public void execute() throws ServerException, IOException {
        String addressee = userService.getAddressee(user.getLogin());
        if (addressee.equals(user.getLogin())) {
            throw new ServerException(ErrorCode.ADDRESSEE_IS_UNKNOWN);
        }
        List<Message> messagesFromUser = userService.getMessages(user.getLogin(), addressee);
        List<Message> messagesFromAddressee = userService.getMessages(addressee, user.getLogin());
        user.getOutput().writeUTF("Server:");
        user.getOutput().writeUTF(String.format("Statistics with %s:", addressee));
        user.getOutput().writeUTF(String.format("Total messages: %d", messagesFromUser.size() + messagesFromAddressee.size()));
        user.getOutput().writeUTF(String.format("Messages from %s: %d", user.getLogin(), messagesFromUser.size()));
        user.getOutput().writeUTF(String.format("Messages from %s: %d",  addressee, messagesFromAddressee.size()));
    }
}
