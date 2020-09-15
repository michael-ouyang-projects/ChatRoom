package tw.ouyang.server;

import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class User {

    private String name;
    private Socket socket;
    private Queue<String> messagesFormOtherUsers;

    public User(String name, Socket socket) {
        this.name = name;
        this.socket = socket;
        this.messagesFormOtherUsers = new LinkedList<>();
    }

    public void addMessage(String message) {
        messagesFormOtherUsers.add(message);
    }

    public String getName() {
        return name;
    }

    public Socket getSocket() {
        return socket;
    }

    public Queue<String> getMessagesFormOtherUsers() {
        return messagesFormOtherUsers;
    }

}
