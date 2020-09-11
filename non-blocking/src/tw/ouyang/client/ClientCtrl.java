package tw.ouyang.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class ClientCtrl {

    private Client clientView;
    private final ByteBuffer buffer = ByteBuffer.allocate(50);
    private final List<String> messagesForServer = Collections.synchronizedList(new ArrayList<>());

    public ClientCtrl(Client clientView) {
        this.clientView = clientView;
    }

    public void connect(String host, int port, String userName) {
        try (SocketChannel channel = SocketChannel.open();
                Selector selector = Selector.open();
                Scanner scanner = new Scanner(System.in)) {

            connectToServer(channel, host, port);
            registerChannelToSelector(channel, selector);
            addMessage(userName);

            while (true) {
                if (selector.selectNow() > 0) {
                    Iterator<SelectionKey> selectionKeys = selector.selectedKeys().iterator();
                    while (selectionKeys.hasNext()) {
                        SelectionKey selectionKey = selectionKeys.next();

                        if (selectionKey.isReadable()) {
                            try {
                                readMessagesFromRemoteServer(channel);
                            } catch (IOException e) {
                                System.out.println("Remote Server Down");
                                selectionKey.cancel();
                                channel.close();
                                continue;
                            }
                        }

                        if (selectionKey.isWritable()) {
                            try {
                                writeMessageToRemoteServer(channel);
                            } catch (IOException e) {
                                System.out.println("Remote Server Down");
                                selectionKey.cancel();
                                channel.close();
                                continue;
                            }
                        }

                        selectionKeys.remove();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Client Shutdown");
        }
    }

    private void connectToServer(SocketChannel channel, String host, int port) throws IOException {
        channel.connect(new InetSocketAddress(host, port));
        clientView.addMessageToDisplayBlock(String.format("Connected to %s\n", channel.getRemoteAddress()));
    }

    private void registerChannelToSelector(SocketChannel channel, Selector selector) throws IOException {
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }

    private void readMessagesFromRemoteServer(SocketChannel channel) throws IOException {
        if (channel.read(buffer) > 0) {
            buffer.flip();
            clientView.addMessageToDisplayBlock(new String(buffer.array(), buffer.position(), buffer.limit()));
            buffer.clear();
        }
    }

    private void writeMessageToRemoteServer(SocketChannel channel) throws IOException {
        Iterator<String> messages = messagesForServer.iterator();
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

    public void addMessage(String message) {
        messagesForServer.add(message);
    }

}
