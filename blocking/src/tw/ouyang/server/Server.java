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
                User user = acceptConnection(serverSocket);
                startCommunication(user);
                broadcast(user, String.format("%s join the room.\n", user.getName()));
                System.out.println(String.format("Accept Connection, UserName %s, From: %s", user.getName(), user.getSocket().getRemoteSocketAddress()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Server Shutdown");
        }
    }

    private User acceptConnection(ServerSocket serverSocket) throws IOException {
        Socket socket = serverSocket.accept();
        User user = new User(getUserName(socket), socket);
        users.add(user);
        return user;
    }

    private String getUserName(Socket socket) throws IOException {
        byte[] dataByte = new byte[50];
        return new String(dataByte, 0, socket.getInputStream().read(dataByte));
    }

    private void startCommunication(User user) {
        executeTask(() -> {
            try {
                readMessageFromRemoteClient(user);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        executeTask(() -> {
            try {
                writeMessagesToRemoteClient(user);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void readMessageFromRemoteClient(User user) throws IOException {
        try {
            byte[] dataByte = new byte[50];
            while (true) {
                String message = new String(dataByte, 0, user.getSocket().getInputStream().read(dataByte));
                message = String.format("%s: %s", user.getName(), message);
                broadcast(user, message);
                System.out.print(message);
            }
        } catch (IOException e) {
            user.getSocket().close();
            deleteUser(user);
            System.out.println("Remote Client Shutdown, By " + user.getName());
        }
    }

    private void writeMessagesToRemoteClient(User user) throws IOException {
        try {
            while (true) {
                String message = user.getMessagesFromOtherUsers().poll();
                if (message != null) {
                    System.out.println("HI");
                    user.getSocket().getOutputStream().write(message.getBytes());
                }
            }
        } catch (IOException e) {
            user.getSocket().close();
            deleteUser(user);
            System.out.println("Remote Client Shutdown, By " + user.getName());
        }
    }

    private void broadcast(User user, String message) {
        users.forEach(loopUser -> {
            if (!loopUser.getName().equals(user.getName())) {
                loopUser.addMessage(message);
            }
        });
    }

    private void deleteUser(User user) {
        users.remove(user);
    }

    private void executeTask(Runnable task) {
        executorService.submit(task);
    }

}
