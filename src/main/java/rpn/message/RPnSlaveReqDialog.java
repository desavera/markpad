/*
 * Instituto de Matematica Pura e Aplicada - IMPA
 * Departamento de Dinamica dos Fluidos
 *
 */

package rpn.message;


import java.awt.*;
import javax.swing.*;
import javax.swing.JDialog;
import java.awt.event.*;

/**
 *
 * <p>The dialog used to grant permission to a Slave user to take part on a RPN SESSION </p>
 */
public class RPnSlaveReqDialog extends JDialog {

    private String clientID;

    JPanel mainPanel = new JPanel();    
    JPanel infoPanel = new JPanel();    
    JPanel buttonsPanel = new JPanel();    

    JButton allowButton = new JButton("Allow");
    JButton denyButton = new JButton("Deny");

    BorderLayout gridLayout = new BorderLayout();

    public JLabel infoLabel = new JLabel("A request for joining the session has arrived from : ");



    public RPnSlaveReqDialog(String reqClientID) {

        try {

            init(reqClientID);

            setLocationRelativeTo(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void init(String reqClientID) throws Exception {


        clientID = new String(reqClientID);

        setResizable(false);
        setTitle("RPn Session Access Grant");

        infoLabel.setText(infoLabel.getText() + clientID);
        infoPanel.add(infoLabel);

        buttonsPanel.add(allowButton);
        buttonsPanel.add(denyButton);

        mainPanel.setLayout(gridLayout);
        mainPanel.add(infoPanel,BorderLayout.NORTH);
        mainPanel.add(buttonsPanel,BorderLayout.SOUTH);

        getContentPane().add(mainPanel);

        allowButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                allowButton_actionPerformed(e);
            }
        });

        denyButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                denyButton_actionPerformed(e);
            }
        });


        pack();


    }

    void allowButton_actionPerformed(ActionEvent e) {

        RPnHttpPublisher publisher = new RPnHttpPublisher(RPnNetworkStatus.RPN_SLAVE_ACK_TOPIC_NAME);
        publisher.publish(RPnNetworkStatus.SLAVE_ACK_LOG_MSG + '|' + clientID + '|' + RPnNetworkStatus.instance().aspectRatio());
        publisher.close();

        dispose();

    }

    void denyButton_actionPerformed(ActionEvent e) {

        dispose();
    }



}
