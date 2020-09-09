package tw.ouyang.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Server {

    private final static List<User> users = new ArrayList<>();
    private final static ByteBuffer buffer = ByteBuffer.allocate(50);

    public void start(String host, int port) {
        try (ServerSocketChannel serverChannel = ServerSocketChannel.open();
                Selector selector = Selector.open()) {

            bindServerToHost(serverChannel, host, port);
            registerServerToSelector(serverChannel, selector);
            System.out.println("Server Start, On " + serverChannel.getLocalAddress());

            User user = null;
            SelectionKey selectionKey = null;
            SocketChannel channel = null;

            while (true) {
                if (selector.selectNow() > 0) {
                    Iterator<SelectionKey> selectionKeys = selector.selectedKeys().iterator();
                    while (selectionKeys.hasNext()) {
                        selectionKey = selectionKeys.next();

                        if (selectionKey.isReadable()) {
                            user = (User) selectionKey.attachment();
                            channel = user.getChannel();
                            try {
                                readMessageFromRemoteClient(user, channel);
                            } catch (IOException e) {
                                System.out.println("Remote Client Shutdown, By " + user.getName());
                                selectionKey.cancel();
                                channel.close();
                                continue;
                            }
                        }

                        if (selectionKey.isWritable()) {
                            user = (User) selectionKey.attachment();
                            channel = user.getChannel();
                            try {
                                writeMessagesToRemoteClient(user, channel);
                            } catch (IOException e) {
                                System.out.println("Remote Client Shutdown, By " + user.getName());
                                selectionKey.cancel();
                                channel.close();
                                continue;
                            }
                        }

                        if (selectionKey.isAcceptable()) {
                            channel = acceptConnection(serverChannel);
                            user = createUser(channel, registerChannelToSelector(channel, selector));
                            broadcast(user, String.format("%s join the room.\n", user.getName()));
                            System.out.println(String.format("Accept Connection, UserName %s, From: %s", user.getName(), channel.getRemoteAddress()));
                        }

                        selectionKeys.remove();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Server Shutdown");
        }
    }

    private void bindServerToHost(ServerSocketChannel serverChannel, String host, int port) throws IOException {
        serverChannel.socket().bind(new InetSocketAddress(host, port));
    }

    private void registerServerToSelector(ServerSocketChannel serverChannel, Selector selector) throws IOException {
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void readMessageFromRemoteClient(User user, SocketChannel channel) throws IOException {
        if (channel.read(buffer) > 0) {
            buffer.flip();
            String message = String.format("%s: %s \n", user.getName(), new String(buffer.array(), buffer.position(), buffer.limit()));
            broadcast(user, message);
            System.out.print(message);
            buffer.clear();
        }
    }

    private void broadcast(User user, String message) {
        users.forEach(loopUser -> {
            if (!loopUser.getName().equals(user.getName())) {
                loopUser.addMessage(message);
            }
        });
    }

    private void writeMessagesToRemoteClient(User user, SocketChannel channel) throws IOException {
        Iterator<String> messages = user.getMessagesFormOtherUsersIterator();
        while (messages.hasNext()) {
            buffer.put(messages.next().getBytes());
            buffer.flip();
            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
            buffer.clear();
            messages.remove();
        }
    }

    private SocketChannel acceptConnection(ServerSocketChannel serverChannel) throws IOException {
        return serverChannel.accept();
    }

    private SelectionKey registerChannelToSelector(SocketChannel channel, Selector selector) throws IOException {
        channel.configureBlocking(false);
        return channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }

    private User createUser(SocketChannel channel, SelectionKey key) throws IOException {
        User newUser = new User(getUserName(channel), channel);
        users.add(newUser);
        key.attach(newUser);
        return newUser;
    }

    private String getUserName(SocketChannel channel) throws IOException {
        while (true) {
            if (channel.read(buffer) > 0) {
                buffer.flip();
                String userName = new String(buffer.array(), buffer.position(), buffer.limit());
                buffer.clear();
                return userName;
            }
        }
    }

}
