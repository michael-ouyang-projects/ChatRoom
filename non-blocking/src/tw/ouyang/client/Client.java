package tw.ouyang.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
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

    private ClientCtrl clientCtrl;
    private JTextArea displayMessagesArea;
    private JTextArea typeMessageArea;
    private JButton sendMessageButton;

    public static void main(String[] args) {
        new Client();
    }

    public Client() {
        initConfiguration();

        JLabel hostLabel = new JLabel("Host: ");
        JTextField hostField = new JTextField("localhost");
        JLabel portLabel = new JLabel("Port: ");
        JTextField portField = new JTextField("8888");
        JLabel userNameLabel = new JLabel("User Name: ");
        JTextField userNameField = new JTextField();
        JPanel infoPanel = new JPanel(new GridLayout(3, 4));
        infoPanel.setPreferredSize(new Dimension(960, 380));
        infoPanel.add(new JLabel());
        infoPanel.add(hostLabel);
        infoPanel.add(hostField);
        infoPanel.add(new JLabel());
        infoPanel.add(new JLabel());
        infoPanel.add(portLabel);
        infoPanel.add(portField);
        infoPanel.add(new JLabel());
        infoPanel.add(new JLabel());
        infoPanel.add(userNameLabel);
        infoPanel.add(userNameField);
        infoPanel.add(new JLabel());

        JButton connectButton = new JButton("Connect");
        connectButton.setPreferredSize(new Dimension(960, 160));

        JPanel connectionPanel = new JPanel(new BorderLayout());
        connectionPanel.add(infoPanel, BorderLayout.CENTER);
        connectionPanel.add(connectButton, BorderLayout.SOUTH);

        connectButton.addActionListener((event) -> {
            String host = hostField.getText();
            Integer port = Integer.parseInt(portField.getText());
            String userName = userNameField.getText();
            this.remove(connectionPanel);
            initGui(userName);
            new Thread(() -> {
                requestConnectionToServer(host, port, userName);
            }).start();
        });

        this.add(connectionPanel);
        this.getRootPane().setDefaultButton(connectButton);
        this.setVisible(true);
        userNameField.requestFocus();
    }

    private void initConfiguration() {
        this.setTitle("ChatRoom");
        this.setSize(965, 590);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createClientController();
    }

    private void initGui(String userName) {
        this.setTitle("ChatRoom - " + userName);
        this.add(createDisplayBlock(), BorderLayout.NORTH);
        this.add(createUserBlock(), BorderLayout.SOUTH);
        this.revalidate();
        this.repaint();
        typeMessageArea.requestFocus();
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
        userPanel.add(createTypeArea(), BorderLayout.WEST);
        userPanel.add(createSendButton(), BorderLayout.EAST);
        return userPanel;
    }

    private JScrollPane createTypeArea() {
        typeMessageArea = new JTextArea();
        typeMessageArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessageButton.doClick();
                    typeMessageArea.setCaretPosition(1);
                }
            }
        });
        JScrollPane typeMessageScrollPanel = new JScrollPane(typeMessageArea);
        typeMessageScrollPanel.setPreferredSize(new Dimension(780, 180));
        return typeMessageScrollPanel;
    }

    private JButton createSendButton() {
        sendMessageButton = new JButton("Send");
        sendMessageButton.setPreferredSize(new Dimension(180, 180));
        sendMessageButton.addActionListener(event -> {
            String message = String.format("%s\n", typeMessageArea.getText());
            clientCtrl.addMessage(message);
            displayMessagesArea.append("You: " + message);
            typeMessageArea.setText("");
        });
        return sendMessageButton;
    }

    public void requestConnectionToServer(String host, int port, String userName) {
        clientCtrl.connect(host, port, userName);
    }

    public void addMessageToDisplayBlock(String message) {
        displayMessagesArea.append(message);
    }

}
