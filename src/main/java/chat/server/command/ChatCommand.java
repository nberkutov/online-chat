package chat.server.command;

import chat.Command;
import chat.model.Message;
import chat.model.User;
import chat.server.ErrorCode;
import chat.server.ServerException;
import chat.service.UserService;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

public class ChatCommand implements Command {
    private final UserService userService;
    private final User user;
    private final String addressee;
    private final DataInputStream input;

    public ChatCommand(UserService userService, User user, String addressee, DataInputStream input) {
        this.userService = userService;
        this.addressee = addressee;
        this.input = input;
        this.user = user;
    }

    @Override
    public void execute() throws ServerException, IOException {
        if (!userService.isOnline(addressee)) {
            throw new ServerException(ErrorCode.USER_IS_OFFLINE);
        }
        String clientInput = "";
        userService.startCorrespondence(user.getLogin(), addressee);
        List<Message> messages = userService.getDialogMessages(user.getLogin(), addressee);


        Stack<Message> dialog = new Stack<>();
        int readCount = 0;
        for (int i = messages.size() - 1; i >=0 && i >= messages.size() - 25; i--) {
            Message message = messages.get(i);
            if (message.isRead()) {
                readCount++;
            }
            if (readCount > 10) {
                break;
            }
            dialog.add(message);
        }

        while (!dialog.isEmpty()) {
            Message message = dialog.pop();
            String fullMessage = String.format("%s: %s", message.getFrom(), message.getText());
            if (!message.isRead()) {
                fullMessage = "(new) " + fullMessage;
            }
            user.getOutput().writeUTF(fullMessage);
        }

        boolean isChatting = true;
        while (isChatting) {
            clientInput = input.readUTF();
            if (clientInput.startsWith("/")) {
                userService.processCommand(clientInput, userService, user, input);
                isChatting = false;
            } else {
                userService.sendMessage(new Message(user.getLogin(), addressee, clientInput));
            }
        }
    }
}