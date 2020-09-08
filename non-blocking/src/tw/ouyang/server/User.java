package tw.ouyang.server;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class User {

    private String name;
    private SocketChannel channel;
    private List<String> messagesFormOtherUsers;

    public User(String name, SocketChannel channel) {
        this.name = name;
        this.channel = channel;
        this.messagesFormOtherUsers = new ArrayList<>();
    }

    public void addMessage(String message) {
        messagesFormOtherUsers.add(message);
    }

    public String getName() {
        return name;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public Iterator<String> getMessagesFormOtherUsersIterator() {
        return messagesFormOtherUsers.iterator();
    }

}
