package tw.ouyang.server;

public class MainS {

    public static void main(String[] args) {

        Server server = new Server();
        server.start("localhost", 8888);

    }

}
