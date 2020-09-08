package tw.ouyang.client;

import java.util.List;
import java.util.Scanner;

public class Typer implements Runnable {

    private Scanner scanner;
    private List<String> messagesForServer;

    public Typer(Scanner scanner, List<String> messagesForServer) {
        this.scanner = scanner;
        this.messagesForServer = messagesForServer;
    }

    @Override
    public void run() {

        while (true) {

            System.out.print("Type: ");
            messagesForServer.add(scanner.nextLine());

        }

    }

}
