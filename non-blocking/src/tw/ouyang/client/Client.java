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

public class Client {

    public static void main(String[] args) throws Exception {

        try (SocketChannel channel = SocketChannel.open();
                Selector selector = Selector.open();
                Scanner scanner = new Scanner(System.in);) {

            List<String> messagesForServer = Collections.synchronizedList(new ArrayList<>());
            ByteBuffer buffer = ByteBuffer.allocate(50);

            System.out.print("Please enter your name: ");
            messagesForServer.add(scanner.nextLine());
            channel.connect(new InetSocketAddress("localhost", 8888));
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            System.out.println("Connected to " + channel.getRemoteAddress());

            new Thread(new Typer(scanner, messagesForServer)).start();

            while (true) {
                if (selector.selectNow() > 0) {
                    Iterator<SelectionKey> selectionKeys = selector.selectedKeys().iterator();
                    while (selectionKeys.hasNext()) {
                        SelectionKey selectionKey = selectionKeys.next();

                        if (selectionKey.isReadable()) {
                            if (channel.read(buffer) > 0) {
                                buffer.flip();
                                System.out.print(new String(buffer.array(), buffer.position(), buffer.limit()));
                                buffer.clear();
                            }
                        }

                        if (selectionKey.isWritable()) {
                            Iterator<String> messages = messagesForServer.iterator();
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
}
