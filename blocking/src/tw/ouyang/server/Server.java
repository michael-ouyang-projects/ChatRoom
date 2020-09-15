package tw.ouyang.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final static List<User> users = new ArrayList<>();
    private final static ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        new Server().start("localhost", 8888);
    }

    public void start(String host, int port) {
        try (ServerSocket serverSocket = new ServerSocket(8888)) {
            System.out.println("Server Start, On " + serverSocket.getLocalSocketAddress());
            while (true) {
                try {
                    User user = acceptConnection(serverSocket);
                    users.add(user);
                    startCommunication(user);
                    broadcast(user, String.format("%s join the room.\n", user.getName()));
                    System.out.println(String.format("Accept Connection, UserName %s, From: %s", user.getName(), user.getSocket().getRemoteSocketAddress()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Server Shutdown");
        }
    }

    private User acceptConnection(ServerSocket serverSocket) throws IOException {
        Socket socket = serverSocket.accept();
        byte[] dataByte = new byte[50];
        int lenthOfName = socket.getInputStream().read(dataByte);
        return new User(new String(dataByte, 0, lenthOfName), socket);
    }

    private void startCommunication(User user) {
        executeTask(() -> {
            try {
                byte[] dataByte = new byte[50];
                while (true) {
                    int lenthOfMessage = user.getSocket().getInputStream().read(dataByte);
                    if (lenthOfMessage > 0) {
                        String message = String.format("%s: %s", user.getName(), new String(dataByte, 0, lenthOfMessage));
                        broadcast(user, message);
                        System.out.print(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        executeTask(() -> {
            try {
                while (true) {
                    String message = user.getMessagesFormOtherUsers().poll();
                    if (message != null) {
                        user.getSocket().getOutputStream().write(message.getBytes());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void broadcast(User user, String message) {
        users.forEach(loopUser -> {
            if (!loopUser.getName().equals(user.getName())) {
                loopUser.addMessage(message);
            }
        });
    }

    private void executeTask(Runnable task) {
        executorService.submit(task);
    }

}
