package chat;

import chat.server.ServerException;

import java.io.IOException;

public interface Command {
    void execute() throws ServerException, IOException;
}
