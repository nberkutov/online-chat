package chat.server.command;

import chat.Command;
import chat.server.ServerException;

import java.io.IOException;

public class Controller {
    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void executeCommand() throws ServerException, IOException {
        command.execute();
    }
}