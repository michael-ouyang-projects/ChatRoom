package tw.ouyang.client;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class ClientView extends JFrame {

    private ClientCtrl clientCtrl;
    private JTextArea displayMessagesArea;
    private JTextArea typeMessageArea;

    public ClientView() {
        initConfiguration();
        // JPanel infoPanel = new JPanel(new GridLayout(4, 1));
        // this.add(infoPanel);
        // this.setVisible(true);
        initGui();
    }

    private void initConfiguration() {
        this.setTitle("ChatRoom");
        this.setSize(965, 590);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createClientController();
    }

    private void initGui() {
        this.add(createDisplayBlock(), BorderLayout.NORTH);
        this.add(createUserBlock(), BorderLayout.SOUTH);
        this.setVisible(true);
    }

    private void createClientController() {
        clientCtrl = new ClientCtrl(this);
    }

    private JScrollPane createDisplayBlock() {
        displayMessagesArea = new JTextArea();
        displayMessagesArea.setEditable(false);
        displayMessagesArea.setText("Please enter your name!\n");
        JScrollPane displayMessagesScrollPanel = new JScrollPane(displayMessagesArea);
        displayMessagesScrollPanel.setPreferredSize(new Dimension(960, 360));
        return displayMessagesScrollPanel;
    }

    private JPanel createUserBlock() {
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setPreferredSize(new Dimension(960, 180));
        userPanel.add(createTypeBlock(), BorderLayout.WEST);
        userPanel.add(createSendButton(), BorderLayout.EAST);
        return userPanel;
    }

    private JScrollPane createTypeBlock() {
        typeMessageArea = new JTextArea();
        JScrollPane typeMessageScrollPanel = new JScrollPane(typeMessageArea);
        typeMessageScrollPanel.setPreferredSize(new Dimension(780, 180));
        return typeMessageScrollPanel;
    }

    private JButton createSendButton() {
        JButton sendMessageButton = new JButton("Send");
        sendMessageButton.setPreferredSize(new Dimension(180, 180));
        sendMessageButton.addActionListener(event -> {
            String message = String.format("%s\n", typeMessageArea.getText());
            clientCtrl.addMessage(message);
            displayMessagesArea.append("You: " + message);
            typeMessageArea.setText("");
        });
        return sendMessageButton;
    }

    public void requestConnectionToServer(String host, int port) {
        clientCtrl.connect("localhost", 8888);
    }

    public void addMessageToDisplayBlock(String message) {
        displayMessagesArea.append(message);
    }

}
