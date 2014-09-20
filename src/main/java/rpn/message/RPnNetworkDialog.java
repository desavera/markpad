/*
 * Instituto de Matematica Pura e Aplicada - IMPA
 * Departamento de Dinamica dos Fluidos
 *
 */
package rpn.message;

import java.net.*;
import java.awt.*;
import javax.swing.*;
import java.beans.*;
import java.awt.event.*;
import javax.swing.JDialog;

/**
 *
 * <p>The dialog used to connect , disconnect and setup the master status
 * in the network communication </p>
 */
public class RPnNetworkDialog extends JDialog implements PropertyChangeListener {

    private static RPnNetworkDialog instance_= null;

    public static String TITLE = "RPn Network Control - ";

    JPanel mainPanel = new JPanel();
    JPanel inputPanel = new JPanel();
    JPanel infoPanel = new JPanel();
    JPanel masterPanel = new JPanel();
    JPanel buttonsPanel = new JPanel();
    JPanel statusPanel = new JPanel();
    JButton onlineButton = new JButton();    
    JScrollPane scrollPane = new JScrollPane();

    public static JTextField serverTextBox = new JTextField(RPnNetworkStatus.SERVERNAME);
    public static JTextArea infoText = new JTextArea();
    
    BorderLayout gridLayout = new BorderLayout();
    JCheckBox masterCheckBox = new JCheckBox("Master");
    JCheckBox firewallCheckBox = new JCheckBox("Firewall protected");
    public static JLabel infoLabel = new JLabel();
    
    

    private RPnNetworkDialog() {

        try {

            init();

            setLocationRelativeTo(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {

        if (evt.getPropertyName().equals("Master Status")) {

            Boolean masterNewStatus = (Boolean) evt.getNewValue();

            if (masterNewStatus.booleanValue()) {

                // willing to become master ???
                if (RPnNetworkStatus.instance().isOnline()) {

                    RPnNetworkStatus.instance().disconnect();
                    RPnNetworkStatus.instance().sendMasterRequest();
                }

            } else {

            }

        }

        if (evt.getPropertyName().equals("Online Status")) {

            Boolean onlineStatus = (Boolean) evt.getNewValue();



            if (onlineStatus.booleanValue()) {
                onlineButton.setText("Disconnect");
                masterCheckBox.setEnabled(false);

            } else {

                onlineButton.setText("Connect");
                masterCheckBox.setEnabled(true);
            }

        }

        if (evt.getPropertyName().equals("Enabled")) {
            Boolean enabled = (Boolean) evt.getNewValue();
            if (enabled.booleanValue()) {
                onlineButton.setEnabled(true);
                masterCheckBox.setEnabled(true);

            } else {
                onlineButton.setText("Connect");
                onlineButton.setEnabled(false);
                masterCheckBox.setEnabled(false);

            }

            RPnNetworkDialog.infoLabel.setText("Server: " + RPnNetworkStatus.SERVERNAME);
        }

    }

    private void init() throws Exception {


        //setAlwaysOnTop(true);

        onlineButton.setText("Connect");

        getContentPane().add(mainPanel);
        scrollPane = new JScrollPane(infoText);
        mainPanel.setLayout(gridLayout);


        mainPanel.add(inputPanel,BorderLayout.NORTH);


        inputPanel.setLayout(new FlowLayout());

        // the SERVER property will be hardcoded for now...
        // inputPanel.add(serverTextBox);

	if (!RPnNetworkStatus.NO_BUS_CONTROL_)
        	inputPanel.add(masterCheckBox);

        // by default we will always use HTTP for now...
        //inputPanel.add(firewallCheckBox);


        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        statusPanel.add(scrollPane);


        infoText.setColumns(40);
        infoText.setRows(15);
        infoText.setLineWrap(false);


        infoText.setText(RPnNetworkStatus.instance().log());

        mainPanel.add(buttonsPanel,BorderLayout.CENTER);

        buttonsPanel.add(onlineButton);
    


        mainPanel.add(statusPanel,BorderLayout.SOUTH);


        this.addComponentListener(new ComponentAdapter() {

            public void componentShown(ComponentEvent e) {
               // this_componentShown(e);


            }
        });

        this.addWindowListener(new java.awt.event.WindowAdapter() {

            public void windowClosing(WindowEvent e) {
               // this_windowClosing(e);
            }
        });
       
        //this.setResizable(false);
        setTitle(TITLE + "OFFLINE ");


        onlineButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                onlineButton_actionPerformed(e);
            }
        });

        pack();


    }

    void configButton_actionPerformed(ActionEvent e) {
    }

    void onlineButton_actionPerformed(ActionEvent e) {

        
        try {
                        
            String clientID = InetAddress.getLocalHost().getHostName();
                      

            if (RPnNetworkStatus.instance().isOnline()) {
               
                RPnNetworkStatus.instance().disconnect();

                // stops listening...
                infoText.append("RPn user : " +  clientID + " is now off RPNSESSION with ID : " + mkp.MKPCommandModule.SESSION_ID_ + '\n');
                               
                onlineButton.setText("Connect");

                // a new connection context can be started...
                masterCheckBox.setEnabled(true);
                firewallCheckBox.setEnabled(true);


                onlineButton.repaint();

            } else {

                // either starts listening or becomes master...
                onlineButton.setText("Disconnect");
                onlineButton.repaint();

                RPnNetworkStatus.instance().connect(clientID,masterCheckBox.isSelected(),firewallCheckBox.isSelected(),mkp.MKPGlassFrame.ASPECT_RATIO);

                // once connected ...controls are disabled !
                masterCheckBox.setEnabled(false);
                firewallCheckBox.setEnabled(false);

            }

        } catch (UnknownHostException ex) {
                    System.out.println(ex);
        }

    }

    public static RPnNetworkDialog instance() {

        if (instance_ == null)
            instance_ = new RPnNetworkDialog();

        return instance_;
    }

}
