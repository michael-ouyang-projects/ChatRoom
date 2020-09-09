package tw.ouyang.client;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class MainC {

    private static JTextArea textArea;

    public static void main(String[] args) {

        Client client = new Client();

        JFrame frame = new JFrame("ChatRoom");
        frame.setSize(965, 590);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setText("Please enter your name!\n");
        JScrollPane textPanel = new JScrollPane(textArea);
        textPanel.setPreferredSize(new Dimension(960, 360));

        JPanel typePanel = new JPanel(new BorderLayout());
        typePanel.setPreferredSize(new Dimension(960, 180));

        JTextArea typearea = new JTextArea();
        JScrollPane typeScrollPanel = new JScrollPane(typearea);
        typeScrollPanel.setPreferredSize(new Dimension(780, 180));
        typePanel.add(typeScrollPanel, BorderLayout.WEST);

        JButton sendButton = new JButton("Send");
        sendButton.setPreferredSize(new Dimension(180, 180));
        sendButton.addActionListener(event -> {
            client.addMessage(typearea.getText());
            textArea.append(typearea.getText() + "\n");
            typearea.setText("");
        });
        typePanel.add(sendButton, BorderLayout.EAST);

        frame.add(textPanel, BorderLayout.NORTH);
        frame.add(typePanel, BorderLayout.SOUTH);
        frame.setVisible(true);

        client.connect("localhost", 8888);

    }

    public static JTextArea getTextArea() {
        return textArea;
    }

}
