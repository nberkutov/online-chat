package chat.service;

import chat.Command;
import chat.database.Database;
import chat.model.User;
import chat.server.ErrorCode;
import chat.model.Message;
import chat.server.ServerCommand;
import chat.server.ServerException;
import chat.server.command.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class UserService {
    private final Database database;
    private final Map<String, User> usersOnline;
    private final Map<String, String> chats;
    private final Map<String, User> kickedUsers;

    public UserService(Database database) {
        this.database = database;
        usersOnline = new TreeMap<>(String::compareTo);
        chats = new HashMap<>();
        kickedUsers = new HashMap<>();
    }

    public void removeUser(String name) {
        database.removeName(name);
    }

    public synchronized void registerUser(User user, String password) throws ServerException {
        if (!validatePassword(password)) {
            throw new ServerException(ErrorCode.SHORT_PASSWORD);
        }
        if (user.getLogin().equals("admin")) {
            throw new ServerException(ErrorCode.REGISTER_ADMIN_ATTEMPT);
        }
        if (!database.addUser(user.getLogin(), password)) {
            throw new ServerException(ErrorCode.LOGIN_IS_TAKEN);
        }
        usersOnline.put(user.getLogin(), user);
    }

    public synchronized void authUser(User user, String password) throws ServerException {
        Integer savedPassword = database.getUserPassword(user.getLogin());
        if (savedPassword == null) {
            throw new ServerException(ErrorCode.INVALID_LOGIN);
        }
        if (savedPassword != password.hashCode()) {
            throw new ServerException(ErrorCode.INVALID_PASSWORD);
        }
        if (kickedUsers.containsKey(user.getLogin())) {
            user.setKickDate(kickedUsers.get(user.getLogin()).getKickDate());
            if (user.getKickDate().plusMinutes(5).compareTo(LocalDateTime.now()) > 0) {
                throw new ServerException(ErrorCode.USER_BANNED);
            }
        }
        usersOnline.put(user.getLogin(), user);
    }

    public boolean isModerator(String login) {
        return database.getModerators().contains(login);
    }

    public void logoutUser(String login) throws ServerException {
        if (usersOnline.remove(login) == null) {
            throw new ServerException(ErrorCode.INVALID_LOGIN);
        }
    }

    private boolean validatePassword(String password) {
        return password.length() >= 8;
    }

    public synchronized Map<String, User> getUsersOnline() {
        return usersOnline;
    }

    public boolean isOnline(String login) {
        return usersOnline.containsKey(login);
    }

    public List<Message> getDialogMessages(String senderLogin, String addresseeLogin) {
        return database.getDialogMessages(senderLogin, addresseeLogin);
    }

    public List<Message> getMessages(String sender, String addressee) {
        return database.getMessages(sender, addressee);
    }

    public void sendMessage(Message message) throws IOException {
        database.addMessage(message);
        String fullMessage = String.format("%s: %s", message.getFrom(), message.getText());
        usersOnline.get(message.getFrom()).getOutput().writeUTF(fullMessage);
        if (isOnline(message.getTo()) && getAddressee(message.getTo()).equals(message.getFrom())) {
            message.setRead(true);
            usersOnline.get(message.getTo()).getOutput().writeUTF(fullMessage);
        }
    }

    public void startCorrespondence(String user1, String user2) {
        chats.put(user1, user2);
    }

    public void kickUser(String login) throws IOException, ServerException {
        User user = usersOnline.get(login);
        if (kickedUsers.put(login, user) == null) {
            user.getOutput().writeUTF("Server: you have been kicked out of the server!");
            user.getOutput().writeUTF("/exit");
            logoutUser(login);
        }
    }

    public String getAddressee(String login) {
        return chats.getOrDefault(login, login);
    }

    public boolean grant(String login) throws ServerException, IOException {
        User user = usersOnline.get(login);
        if (user == null) {
            throw new ServerException(ErrorCode.USER_IS_OFFLINE);
        }
        if (database.addModerator(user.getLogin())) {
            user.getOutput().writeUTF("Server: you are the new moderator now!");
            return true;
        }
        return false;
    }

    public boolean revoke(String login) throws ServerException, IOException {
        User user = usersOnline.get(login);
        if (user == null) {
            throw new ServerException(ErrorCode.USER_IS_OFFLINE);
        }
        if (!database.removeModerator(login)) {
           return false;
        }
        user.getOutput().writeUTF("Server: you are no longer a moderator!");
        return true;
    }

    public boolean isKicked(String login) {
        return kickedUsers.containsKey(login);
    }

    public Set<String> getUnread(String login) {
        Set<String> senders = new TreeSet<>();
        for (var message : database.getMessages()) {
            if (message.getTo().equals(login) && !message.isRead()) {
                senders.add(message.getFrom());
            }
        }
        return senders;
    }

    public void processCommand(
            String cmdString, UserService userService, User user, DataInputStream input) throws IOException, ServerException {

        String[] parsedInput = cmdString.split("[ ]+");
        Controller controller = new Controller();
        Command command;

        switch (ServerCommand.getValue(parsedInput[0])) {
            case LIST:
                command = new ListCommand(userService, user);
                controller.setCommand(command);
                break;
            case CHAT:
                if (parsedInput.length < 2) {
                    throw new ServerException(ErrorCode.INVALID_COMMAND);
                }
                String addressee = parsedInput[1];
                command = new ChatCommand(userService, user, addressee, input);
                controller.setCommand(command);
                break;
            case KICK:
                command = new KickCommand(userService, user, parsedInput[1]);
                controller.setCommand(command);
                break;
            case GRANT:
                command = new GrantCommand(userService, user, parsedInput[1]);
                controller.setCommand(command);
                break;
            case REVOKE:
                command = new RevokeCommand(userService, user, parsedInput[1]);
                controller.setCommand(command);
                break;
            case UNREAD:
                command = new UnreadCommand(userService, user);
                controller.setCommand(command);
                break;
            case STATS:
                command = new StatsCommand(userService, user);
                controller.setCommand(command);
                break;
            case HISTORY:
                command = new HistoryCommand(userService, user, parsedInput[1]);
                controller.setCommand(command);
                break;
        }
        controller.executeCommand();
    }
}
