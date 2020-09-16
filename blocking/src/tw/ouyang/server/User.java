package tw.ouyang.server;

import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class User {

    private String name;
    private Socket socket;
    private Queue<String> messagesFromOtherUsers;

    public User(String name, Socket socket) {
        this.name = name;
        this.socket = socket;
        this.messagesFromOtherUsers = new ConcurrentLinkedQueue<>();
    }

    public void addMessage(String message) {
        messagesFromOtherUsers.add(message);
    }

    public String getName() {
        return name;
    }

    public Socket getSocket() {
        return socket;
    }

    public Queue<String> getMessagesFromOtherUsers() {
        return messagesFromOtherUsers;
    }

}
