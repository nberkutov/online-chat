package chat.server.command;

import chat.Command;
import chat.model.Message;
import chat.model.User;
import chat.server.ErrorCode;
import chat.server.ServerException;
import chat.service.UserService;

import java.io.IOException;
import java.util.List;

public class HistoryCommand implements Command {
    private UserService userService;
    private User user;
    private String quantityStr;

    public HistoryCommand(UserService userService, User user, String quantityStr) {
        this.userService = userService;
        this.user = user;
        this.quantityStr = quantityStr;
    }

    @Override
    public void execute() throws ServerException, IOException {
        String addressee = userService.getAddressee(user.getLogin());
        if (addressee.equals(user.getLogin())) {
            throw new ServerException(ErrorCode.ADDRESSEE_IS_UNKNOWN);
        }
        try {
            int n = Integer.parseInt(quantityStr);
            if (n > 25) {
                n = 25;
            }
            List<Message> messages = userService.getDialogMessages(user.getLogin(), addressee);
            int start = 0;
            if (messages.size() > n) {
                start = messages.size() - n;
            }
            for (int i = start; i < messages.size(); i++) {
                user.getOutput().writeUTF(String.format("%s: %s", messages.get(i).getFrom(), messages.get(i).getText()));
            }
        } catch (NumberFormatException ex) {
            user.getOutput().writeUTF(String.format("Server: %s is not a number!", quantityStr));
        }

    }
}
