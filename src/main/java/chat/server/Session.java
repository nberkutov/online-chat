package chat.server;

import chat.Command;
import chat.model.AdminDecorator;
import chat.model.User;
import chat.server.command.*;
import chat.service.UserService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Session extends Thread {
    public final int id;
    private final Socket socket;
    private final UserService userService;

    public Session(int id, Socket socket, UserService userService) throws IOException {
        this.id = id;
        this.socket = socket;
        this.userService = userService;
    }

    @Override
    public void run() {
        Command command;
        User user = null;

        try (
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream((socket.getOutputStream()))
        ) {
            Controller controller = new Controller();
            output.writeUTF("Server: authorize or register");
            while (true) {
                String clientInput = input.readUTF();
                String[] parsedMessage = clientInput.split("[ ]+");
                if (parsedMessage.length < 3) {
                    output.writeUTF(ErrorCode.INVALID_COMMAND.getMessage());
                    continue;
                }

                try {
                    if (parsedMessage[0].equals(ServerCommand.REGISTRATION.toString())) {
                        String userName = parsedMessage[1];
                        user = new User(userName, output);
                        if ("admin".equals(userName)) {
                            throw new ServerException(ErrorCode.REGISTER_ADMIN_ATTEMPT);
                        }
                        String password = parsedMessage[2];
                        command = new RegistrationCommand(userService, user, password);
                        controller.setCommand(command);
                        controller.executeCommand();
                        output.writeUTF(SuccessCode.REG_SUCCESS.getMessage());
                        break;
                    } else if (parsedMessage[0].equals(ServerCommand.AUTH.toString())) {
                        String userName = parsedMessage[1];
                        user = new User(userName, output);
                        if ("admin".equals(userName)) {
                            user = new AdminDecorator(user);
                        }
                        command = new AuthCommand(userService, user, parsedMessage[2]);
                        controller.setCommand(command);
                        controller.executeCommand();
                        output.writeUTF(SuccessCode.AUTH_SUCCESS.getMessage());
                        break;
                    } else {
                        throw new ServerException(ErrorCode.NOT_AUTHENTICATED);
                    }
                } catch (ServerException ex) {
                    output.writeUTF(ex.getMessage());
                }
            }
            while (!userService.isKicked(user.getLogin())) {
                String clientInput = input.readUTF();
                clientInput = clientInput.trim();
                try {
                    if (clientInput.startsWith("/")) {
                        userService.processCommand(clientInput, userService, user, input);
                    } else {
                        throw new ServerException(ErrorCode.ADDRESSEE_IS_UNKNOWN);
                    }
                } catch (ServerException ex) {
                    output.writeUTF(ex.getMessage());
                }
            }
        } catch (IOException ex) {
            System.out.printf("User %d disconnected.\n", id);
        } finally {
            if (user != null) {
                try {
                    userService.logoutUser(user.getLogin());
                } catch (ServerException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }
}