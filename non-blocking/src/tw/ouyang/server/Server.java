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

    public static void main(String[] args) throws Exception {

        List<User> users = new ArrayList<>();

        try (ServerSocketChannel serverChannel = ServerSocketChannel.open();
                Selector selector = Selector.open();) {

            serverChannel.socket().bind(new InetSocketAddress("localhost", 8888));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Server Start, On " + serverChannel.getLocalAddress());

            SelectionKey selectionKey = null;
            User user = null;
            SocketChannel channel = null;
            ByteBuffer buffer = ByteBuffer.allocate(50);

            while (true) {
                if (selector.selectNow() > 0) {
                    Iterator<SelectionKey> selectionKeys = selector.selectedKeys().iterator();
                    while (selectionKeys.hasNext()) {
                        selectionKey = selectionKeys.next();

                        try {
                            if (selectionKey.isReadable()) {
                                user = (User) selectionKey.attachment();
                                channel = user.getChannel();
                                if (channel.read(buffer) > 0) {
                                    buffer.flip();
                                    String message = String.format("From %s: %s \n", user.getName(), new String(buffer.array(), buffer.position(), buffer.limit()));
                                    users.forEach(loopUser -> {
                                        loopUser.addMessage(message);
                                    });
                                    System.out.print(message);
                                    buffer.clear();
                                }
                            }
                        } catch (IOException e) {
                            System.out.println("Remote Client Shutdown, By " + user.getName());
                            selectionKey.cancel();
                            channel.close();
                            continue;
                        }

                        try {
                            if (selectionKey.isWritable()) {
                                user = (User) selectionKey.attachment();
                                channel = user.getChannel();
                                Iterator<String> messages = user.getMessagesForClient().iterator();
                                while (messages.hasNext()) {
                                    String message = messages.next();
                                    buffer.put(message.getBytes());
                                    buffer.flip();
                                    while (buffer.hasRemaining()) {
                                        channel.write(buffer);
                                    }
                                    buffer.clear();
                                    messages.remove();
                                }
                            }
                        } catch (IOException e) {
                            System.out.println("Remote Client Shutdown, By " + user.getName());
                            selectionKey.cancel();
                            channel.close();
                            continue;
                        }

                        if (selectionKey.isAcceptable()) {
                            channel = serverChannel.accept();
                            channel.configureBlocking(false);
                            SelectionKey key = channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                            String userName = null;
                            do {
                                if (channel.read(buffer) > 0) {
                                    buffer.flip();
                                    userName = new String(buffer.array(), buffer.position(), buffer.limit());
                                    buffer.clear();
                                }
                            } while (userName == null);
                            User newUser = new User(userName, channel);
                            users.add(newUser);
                            key.attach(newUser);
                            System.out.println(String.format("Accept Connection, From %s, By %s", channel.getRemoteAddress(), userName));
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
}
