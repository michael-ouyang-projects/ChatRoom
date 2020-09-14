package tw.ouyang.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class Client extends JFrame {

    private JTextField hostField;
    private JTextField portField;
    private JTextField userNameField;
    private JButton connectButton;
    private JPanel connectionPage;

    private ClientCtrl clientCtrl;
    private JTextArea displayMessagesArea;
    private JTextArea typeMessageArea;
    private JButton sendMessageButton;

    public static void main(String[] args) {
        new Client();
    }

    public Client() {
        basicConfiguration();
        createConnectionPage();
        this.setVisible(true);
        userNameField.requestFocus();
    }

    private void basicConfiguration() {
        this.setTitle("ChatRoom");
        this.setSize(965, 590);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void createConnectionPage() {
        connectionPage = new JPanel(new BorderLayout());
        connectionPage.add(createInputBlock(), BorderLayout.CENTER);
        connectionPage.add(createConnectButton(), BorderLayout.SOUTH);
        this.add(connectionPage);
        this.getRootPane().setDefaultButton(connectButton);
    }

    private JPanel createInputBlock() {
        JPanel infoInputPanel = new JPanel(new GridLayout(3, 4));
        infoInputPanel.setPreferredSize(new Dimension(960, 380));
        addHostInputBlock(infoInputPanel);
        addPortInputBlock(infoInputPanel);
        addUserNameInputBlock(infoInputPanel);
        return infoInputPanel;
    }

    private void addHostInputBlock(JPanel infoInputPanel) {
        infoInputPanel.add(new JLabel());
        infoInputPanel.add(new JLabel("Host: "));
        hostField = new JTextField("localhost");
        infoInputPanel.add(hostField);
        infoInputPanel.add(new JLabel());
    }

    private void addPortInputBlock(JPanel infoInputPanel) {
        infoInputPanel.add(new JLabel());
        infoInputPanel.add(new JLabel("Port: "));
        portField = new JTextField("8888");
        infoInputPanel.add(portField);
        infoInputPanel.add(new JLabel());
    }

    private void addUserNameInputBlock(JPanel infoInputPanel) {
        infoInputPanel.add(new JLabel());
        infoInputPanel.add(new JLabel("User Name: "));
        userNameField = new JTextField();
        infoInputPanel.add(userNameField);
        infoInputPanel.add(new JLabel());
    }

    private JButton createConnectButton() {
        connectButton = new JButton("Connect");
        connectButton.setPreferredSize(new Dimension(960, 160));
        connectButton.addActionListener((event) -> {
            this.remove(connectionPage);
            createChatPage(userNameField.getText());
            new Thread(() -> {
                requestConnectionToServer(hostField.getText(), Integer.parseInt(portField.getText()), userNameField.getText());
            }).start();
        });
        return connectButton;
    }

    private void createChatPage(String userName) {
        this.setTitle("ChatRoom - " + userName);
        this.add(createDisplayBlock(), BorderLayout.NORTH);
        this.add(createUserBlock(), BorderLayout.SOUTH);
        this.revalidate();
        this.repaint();
        typeMessageArea.requestFocus();
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
        userPanel.add(createTypeArea(), BorderLayout.WEST);
        userPanel.add(createSendButton(), BorderLayout.EAST);
        return userPanel;
    }

    private JScrollPane createTypeArea() {
        typeMessageArea = new JTextArea() {
            @Override
            public void processKeyEvent(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    sendMessageButton.doClick();
                } else {
                    super.processKeyEvent(e);
                }
            }
        };
        JScrollPane typeMessageScrollPanel = new JScrollPane(typeMessageArea);
        typeMessageScrollPanel.setPreferredSize(new Dimension(780, 180));
        return typeMessageScrollPanel;
    }

    private JButton createSendButton() {
        sendMessageButton = new JButton("Send");
        sendMessageButton.setPreferredSize(new Dimension(180, 180));
        sendMessageButton.addActionListener(event -> {
            if (typeMessageArea.getText().length() > 0) {
                String message = String.format("%s\n", typeMessageArea.getText());
                clientCtrl.addMessage(message);
                displayMessagesArea.append("You: " + message);
                typeMessageArea.setText(null);
            }

        });
        return sendMessageButton;
    }

    public void requestConnectionToServer(String host, int port, String userName) {
        clientCtrl = new ClientCtrl(this);
        clientCtrl.connect(host, port, userName);
    }

    public void addMessageToDisplayBlock(String message) {
        displayMessagesArea.append(message);
    }

}
