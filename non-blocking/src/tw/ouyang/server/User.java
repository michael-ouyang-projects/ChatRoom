package tw.ouyang.server;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class User {

    private String name;
    private SocketChannel channel;
    private List<String> messagesForClient;

    public User(String name, SocketChannel channel) {
        this.name = name;
        this.channel = channel;
        this.messagesForClient = new ArrayList<>();
    }

    public void addMessage(String message) {
        messagesForClient.add(message);
    }

    public String getName() {
        return name;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public List<String> getMessagesForClient() {
        return messagesForClient;
    }

}
