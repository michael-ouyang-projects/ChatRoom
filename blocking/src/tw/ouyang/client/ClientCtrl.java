package tw.ouyang.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class ClientCtrl {

    private Client clientView;
    private final Queue<String> messagesForServer = new LinkedList<>();

    public ClientCtrl(Client clientView) {
        this.clientView = clientView;
    }

    public void connect(String host, int port, String userName) {
        try (Socket socket = new Socket();
                Scanner scanner = new Scanner(System.in)) {
            connectToServer(socket, host, port);
            socket.getOutputStream().write(userName.getBytes());

            new Thread(() -> {
                try {
                    byte[] dataByte = new byte[50];
                    while (true) {
                        clientView.addMessageToDisplayBlock(new String(dataByte, 0, socket.getInputStream().read(dataByte)));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            try {
                while (true) {
                    String message = messagesForServer.poll();
                    if (message != null) {
                        socket.getOutputStream().write(message.getBytes());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Client Shutdown");
        }
    }

    private void connectToServer(Socket socket, String host, int port) throws IOException {
        socket.connect(new InetSocketAddress(host, port));
        clientView.addMessageToDisplayBlock(String.format("Connected to %s\n", socket.getRemoteSocketAddress()));
    }

    public void addMessage(String message) {
        messagesForServer.add(message);
    }

}
