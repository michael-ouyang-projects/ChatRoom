package tw.ouyang.client;

import java.util.List;
import java.util.Scanner;

public class Typer implements Runnable {

    private Scanner scanner;
    private List<String> messagesForOtherUsers;

    public Typer(Scanner scanner, List<String> messagesForOtherUsers) {
        this.scanner = scanner;
        this.messagesForOtherUsers = messagesForOtherUsers;
    }

    @Override
    public void run() {
        while (true) {
            messagesForOtherUsers.add(scanner.nextLine());
        }
    }

}
