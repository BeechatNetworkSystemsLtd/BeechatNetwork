package beechat.connection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

class ChatForm {
    private JPanel panel;
    private JList<String> list;
    private Vector<String> messages = new Vector<>();
    private JTextField textField;
    private JButton sendButton;

    Node node;

    ChatForm(Node node) {
        this.node = node;

        JFrame frame = new JFrame("Chatting with " + node.getREMOTE_DEVICE().getNodeID());
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                node.sendMessage(textField.getText());
                messages.add(node.getLOCAL_DEVICE().getNodeID() + ": " + textField.getText());
                list.setListData(messages);
                textField.setText("");
            }
        });
    }

    void addMessage(String nodeID, String message ) {
        messages.add(nodeID + ": " + message);
        this.list.setListData(messages);
    }


}
