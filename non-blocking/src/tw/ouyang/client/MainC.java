package tw.ouyang.client;

public class MainC {

    public static void main(String[] args) {

        Client client = new Client();
        client.connect("localhost", 8888);

    }

}
