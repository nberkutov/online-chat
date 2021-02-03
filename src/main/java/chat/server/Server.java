package chat.server;

import chat.database.Database;
import chat.service.UserService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 8088;

    public static void main(String[] args) {
        final Database database = Database.getInstance();
        final UserService userService = new UserService(database);

        try (ServerSocket server = new ServerSocket(PORT)) {
            //server.setSoTimeout(14000);
            System.out.println("Server started!");
            int sessionsCount = 0;
            while (true) {
                Socket socket = server.accept();
                Session session = new Session(++sessionsCount, socket, userService);
                session.start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            database.save();
        }
    }
}
