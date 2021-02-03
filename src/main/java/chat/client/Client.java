package chat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static String SERVER_ADDRESS = "127.0.0.1";
    private static int PORT = 8088;

    private static class ChatReader implements Runnable {
        private final Socket socket;

        private ChatReader(Socket socket) throws IllegalArgumentException {
            if (socket == null) {
                throw new IllegalArgumentException("\"socket\" argument is null.");
            }
            this.socket = socket;
        }

        @Override
        public void run() {
            try (DataInputStream input = new DataInputStream(socket.getInputStream())) {
                while (true) {
                    String message = input.readUTF();
                    if (message.equals("/exit")) {
                        break;
                    }
                    System.out.println(message);
                }
            } catch (IOException ex) {
                //ex.printStackTrace();
            }
        }
    }

    private static class ChatWriter implements Runnable {
        private final Socket socket;

        private ChatWriter(Socket socket) throws IllegalArgumentException {
            if (socket == null) {
                throw new IllegalArgumentException("\"socket\" argument is null.");
            }
            this.socket = socket;
        }

        @Override
        public void run() {
            Scanner scanner = new Scanner(System.in);
            try (DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
                String message;
                while (true) {
                    message = scanner.nextLine();
                    if (message.equals("/exit")) {
                        break;
                    }
                    output.writeUTF(message);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT)) {
            System.out.println("Client started!");
            Thread readerThread = new Thread(new ChatReader(socket));
            Thread writerThread = new Thread(new ChatWriter(socket));
            readerThread.start();
            writerThread.start();
            readerThread.join();

        } catch (IOException | InterruptedException ex) {
            System.out.println("Connection was closed.");
        }
    }
}
