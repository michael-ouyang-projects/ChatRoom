package tw.ouyang.client;

public class MainC {

    public static void main(String[] args) {

        ClientView clientView = new ClientView();
        clientView.requestConnectionToServer("localhost", 8888);

    }

}
