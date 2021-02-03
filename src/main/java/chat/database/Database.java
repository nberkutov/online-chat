package chat.database;

import chat.model.Message;

import java.io.*;
import java.util.*;

public class Database implements Serializable {
    private static final long serialVersionUID = 1L;

    private static volatile Map<String, Integer> users;
    private static List<Message> messages;
    private static Set<String> names;
    private static Database instance;
    private static Set<String> moderators;

    private static final String fileName = "users.dat";
    private static final String messagesFile = "messages.dat";
    private static final String adminsFile = "admins.dat";
    private static final String adminPassword = "12345678";

    private Database() {
        users = new HashMap<>();
        users.put("admin", adminPassword.hashCode());
        messages = new ArrayList<>();
        names = new HashSet<>();
        moderators = new HashSet<>();
        File file = new File(fileName);
        File msgFile = new File(messagesFile);
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String str = scanner.nextLine();
                String[] words = str.split("[ ]+");
                try {
                    users.put(words[0], Integer.parseInt(words[1]));
                } catch (NumberFormatException e) {
                }
            }
            scanner.close();
            scanner = new Scanner(msgFile);
            while (scanner.hasNext()) {
                String from = scanner.nextLine();
                String to = scanner.nextLine();
                boolean isRead = Boolean.parseBoolean(scanner.nextLine());
                String text = scanner.nextLine();
                messages.add(new Message(from, to, text, isRead));
            }
            scanner.close();

            scanner = new Scanner(adminsFile);
            while (scanner.hasNext()) {
                moderators.add(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException ex) {
        }
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public static Database getInstance(String fileName) {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public Set<String> getModerators() {
        return moderators;
    }

    public  List<Message> getMessages() {
        return messages;
    }

    public List<Message> getMessages(String from, String to) {
        List<Message> userMessages = new ArrayList<>();
        for (var msg : messages) {
            if ((msg.getFrom().equals(from) && msg.getTo().equals(to))) {
                userMessages.add(msg);
            }
        }
        return userMessages;
    }

    public List<Message> getDialogMessages(String from, String to) {
        List<Message> userMessages = new ArrayList<>();
        for (var msg : messages) {
            if ((msg.getFrom().equals(from) && msg.getTo().equals(to)) || msg.getFrom().equals(to) && msg.getTo().equals(from)) {
                userMessages.add(msg);
            }
        }
        return userMessages;
    }

    public synchronized boolean addModerator(String login) {
        return moderators.add(login);
    }

    public synchronized boolean removeModerator(String login) {
        return moderators.remove(login);
    }

    public synchronized void addMessage(Message message) {
        messages.add(message);
    }

    public synchronized boolean addUser(String login, String password) {
        int passwordHash = password.hashCode();
        if (users.putIfAbsent(login, passwordHash) != null) {
            return false;
        }
        users.put(login, passwordHash);
        return true;
    }

    public synchronized boolean removeUser(String login, String name) {
        return users.remove(login, name);
    }

    public synchronized boolean containsUser(String name) {
        return users.containsValue(name);
    }

    public synchronized Integer getUserPassword(String login) {
        return users.get(login);
    }

    public synchronized boolean addName(String name) {
        return names.add(name);
    }

    public synchronized boolean removeName(String name) {
        return names.remove(name);
    }

    public void save() {
        File usersFile = new File(fileName);
        File msgFile = new File(messagesFile);
        try {
            if (!usersFile.exists()) {
                usersFile.createNewFile();
            }
            Writer writer = new FileWriter(usersFile, true);
            for (var user : users.entrySet()) {
                String str = user.getKey() + " " + user.getValue() + "\n";
                writer.write(str);
            }
            writer.close();

            writer = new FileWriter(msgFile, false);
            for (var message : messages) {
                writer.write(message.getFrom() + "\n");
                writer.write(message.getTo() + "\n");
                writer.write(message.isRead() + "\n");
                writer.write(message.getText() + "\n");
            }
            writer.close();

            writer = new FileWriter(adminsFile, false);
            for (var admin : moderators) {
                writer.write(admin);
            }
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


}
