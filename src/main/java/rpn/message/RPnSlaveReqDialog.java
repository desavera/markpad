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

    JComboBox colorCombo = new JComboBox();

    JButton allowButton = new JButton("Allow");
    JButton denyButton = new JButton("Deny");

    BorderLayout gridLayout = new BorderLayout();

    String colorChosen = new String("Blue");

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

	colorCombo.addItem("Blue");
	colorCombo.addItem("Yellow");

	colorCombo.addActionListener(new ActionListener() {
      		public void actionPerformed(ActionEvent e) {
       		 colorChosen = (String)((JComboBox) e.getSource()).getSelectedItem();
      		}
    	});

        buttonsPanel.setLayout(new BorderLayout());
        buttonsPanel.add(denyButton,BorderLayout.WEST);
        buttonsPanel.add(allowButton,BorderLayout.CENTER);

	JPanel leftPanel = new JPanel();
	leftPanel.add(buttonsPanel);

	JPanel rightPanel = new JPanel();
	rightPanel.add(colorCombo);

	JPanel fullPanel = new JPanel();
	fullPanel.setLayout(new FlowLayout());
	fullPanel.add(leftPanel);
	fullPanel.add(rightPanel);

        mainPanel.setLayout(gridLayout);
        mainPanel.add(infoPanel,BorderLayout.NORTH);
        mainPanel.add(fullPanel,BorderLayout.SOUTH);

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
        publisher.publish(RPnNetworkStatus.SLAVE_ACK_LOG_MSG + '|' + clientID + '|' + RPnNetworkStatus.instance().aspectRatio() + '|' + colorChosen);
        publisher.close();

        dispose();

    }

    void denyButton_actionPerformed(ActionEvent e) {

        dispose();
    }



}
