package beechat.connection;

import javax.swing.*;
import java.time.LocalTime;
import java.util.Vector;

/**
 * A dialog box which update the user on the status of the key exchange.
 * @see KeyExchanger
 */
class LoadingBox {
    private JPanel panel;
    private JList<String> list;
    private Vector<String> listData = new Vector<>();
    private JProgressBar progressBar;
    private int progressBarPercentage = 0;
    private JLabel label;


    /**
     * Create and display a new LoadingBox
     */
    LoadingBox(){

        JFrame frame = new JFrame("Loading");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setAutoRequestFocus(true);
        frame.pack();
        frame.setVisible(true);

    }


    /**
     * Update the status message displayed to the user
     * @param message the message to be displayed
     */
    void updateLabel(String message) {
        label.setText(message);
    }

    /**
     * Add a message to the log
     * @param message the message to be added
     */
    void addToLog(String message) {
        listData.add(LocalTime.now().toString() + ": " + message);
        list.setListData(listData);
    }

    void setProgressBarPercentage(int i) {

    }

    int getProgressBarPercentage() {
        return progressBarPercentage;
    }


}
