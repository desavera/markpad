/*
 * Instituto de Matematica Pura e Aplicada - IMPA
 * Departamento de Dinamica dos Fluidos
 *
 */
package rpn.message;

import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import mkp.MKPCommandModule;

/**
 *
 *
 * <p>The class that stores and manages the network communications parameters </p>

 */
public class RPnNetworkStatus {


    private static RPnNetworkStatus instance_= null;

    public static boolean NO_BUS_CONTROL_ = false;

    private String clientID_;
    private boolean isMaster_;
    private boolean isOnline_;
    private boolean isFirewalled_;

    // queues up a slave request (SLAVE)
    private RPnHttpSender slaveRequestSender_ = null;
    // listens to the slave request queue (MASTER)
    private RPnConsumerThread slaveReqConsumerThread_ = null;
    // subscription for an acknowledge given to a SLAVE to join the session (ALL)
    private RPnSubscriberThread slaveAckSubscriberThread_ = null;

    // subscription to RPn COMMANDs topic (SLAVE)
    private RPnSubscriberThread commandSubscriberThread_ = null;
    // publisher for RPn COMMAND (MASTER)
    private RPnHttpPublisher commandPublisher_ = null;

    // publisher for MASTER request
    private RPnHttpPublisher masterRequestPublisher_ = null;
    // subscription to MASTER request
    private RPnSubscriberThread masterReqSubscriberThread_ = null;
    // subscription to MASTER acknowledge (SLAVE)
    private RPnSubscriberThread masterAckSubscriberThread_ = null;

    // TODO : this is not necessary
    private RPnResetableListener masterResetConsumer_ = null;
    private RPnResetableListener masterCheckConsumer_ = null;
    private RPnHttpSender masterSender_ = null;


    public static String  SERVERNAME = new String("147.65.7.10");
    public static String  QUEUE_NAME = "QUEUE_NAME";
    public static String  TOPIC_NAME = "TOPIC_NAME";
    
    /*
     * MASTER command publishing TOPIC
     */
    public static String  RPN_COMMAND_TOPIC_NAME = new String("jms/topic/RPN_COMMAND_TOPIC_1234");
    /*
     * MASTER listening on SLAVE REQ QUEUE and publishing on SLAVE ACK
     */
    public static String RPN_SLAVE_REQ_QUEUE_NAME = new String("jms/queue/RPN_SLAVE_REQ_QUEUE_1234");
    public static String RPN_MASTER_REQ_TOPIC_NAME = new String("jms/topic/RPN_MASTER_REQ_TOPIC_1234");
    /*
     * MASTER ACKNOWLEDGE
     */
    public static String RPN_MASTER_QUEUE_NAME = new String("jms/queue/RPN_MASTER_QUEUE_1234");
    
    
    public static String RPN_SLAVE_ACK_TOPIC_NAME = new String("jms/topic/RPN_SLAVE_ACK_TOPIC_1234");
    public static String RPN_MASTER_ACK_TOPIC_NAME = new String("jms/topic/RPN_MASTER_ACK_TOPIC_1234");

    /*
     * RPN CONTROL MESSAGES
     */
    public static String MASTER_REQUEST_LOG_MSG = new String ("MASTER_REQUEST");
    public static String MASTER_ACK_LOG_MSG = new String ("MASTER_ACK");
    public static String SLAVE_REQ_LOG_MSG = new String ("SLAVE_REQ");
    public static String SLAVE_ACK_LOG_MSG = new String ("SLAVE_ACK");
    //public static String NO_MASTER_MSG = new String ("NO MASTER");
    public static String NULL_MSG = new String("");
    public static String RPN_COMMAND_PREFIX = new String("<COMMAND");

    /*
     * RPN CLIENT/SERVER CONTROL MESSAGES
     */
    public static String RPN_MEDIATORPROXY_REQ_ID_TAG="REQ_ID";
    public static String RPN_MEDIATORPROXY_COMMAND_TAG="RPN_COMMAND";
    public static String RPN_MEDIATORPROXY_CLIENT_ID_TAG="CLIENT_ID";
    public static String RPN_MEDIATORPROXY_SESSION_ID_TAG="SESSION_ID";

    public static String RPN_MEDIATORPROXY_MASTER_UPDATE_TAG="MASTER_UPDATE";
    public static String RPN_MEDIATORPROXY_MASTER_CHECK_TAG="MASTER_CHECK";
    public static String RPN_MEDIATORPROXY_MASTER_RESET_TAG="MASTER_RESET";
    public static String RPN_MEDIATORPROXY_POLL_TAG="POLL";
    public static String RPN_MEDIATORPROXY_NOTEBOARD_POLL_TAG="OBJ_POLL";

    public static String RPN_MEDIATORPROXY_SEND_TAG="SEND";
    public static String RPN_MEDIATORPROXY_PUBLISH_TAG="PUB";
    
    public static String RPN_MEDIATORPROXY_LISTENING_NAME_TAG="LISTENING_NAME";
    public static String RPN_MEDIATORPROXY_LISTENING_TAG="SUBSREC";
    public static String RPN_MEDIATORPROXY_LOG_MSG_TAG="LOG_MSG";

    //public static String RPN_MEDIATORPROXY_URL="http://" + SERVERNAME + ":8080/rpnmediatorproxy/rpnmediatorproxy?REQ_ID=";
    //public static String RPN_MEDIATORPROXY_URL="http://" + SERVERNAME + ":8080/mkpmediatorproxy/";    
    public static String RPN_MEDIATORPROXY_URL="http://" + SERVERNAME + ":8080/rpnmediatorproxy/";    

    public static String ACTIVATED_FRAME_TITLE = "NO_TITLE";

    //
    // Constructors/Initializers
    //
    private RPnNetworkStatus() {

        isOnline_ = false;
        isFirewalled_ = true;
    }

    //
    // Accessors/Mutators
    //
    public boolean isMaster() {
        return isMaster_;
    }

    public boolean isOnline() {
        return isOnline_;
    }

    public String clientID() {
        return clientID_;
    }

    public boolean isFirewalled() {
        return isFirewalled_;
    }
    //
    // Methods
    //

    public String log() {
        return RPnNetworkDialog.infoText.getText();
    }

    public void log(String logMessage) {

        RPnNetworkDialog.infoText.append(logMessage + '\n');


    }


    public void connect(String clientID,boolean isMaster,boolean isFirewalled) {

        clientID_ = clientID;
        isMaster_ = isMaster;

	// when no bus control activated each one is a slave but capable of posting COMMANDs...
	if (NO_BUS_CONTROL_) {

		 isMaster_ = false;
		 ackMasterRequest(clientID_);
	}	
        
        //isFirewalled_ = isFirewalled;
        isFirewalled_ = true;
        
        // EVERYONE is notified for a MASTER change !!!
        subsMasterAck();

        // EVERYONE is notified of a SLAVE joining the session !!!
        subsSlaveAck();



        if (!isMaster_) {

            System.out.println("MKP user : " +  clientID_ + " will request to follow RPNSESSION with ID : " + MKPCommandModule.SESSION_ID_ + '\n');
            sendSlaveRequest();
            
        }
        else {

            System.out.println("MKP user : " +  clientID_ + " will request MASTER access to RPNSESSION with ID : " + MKPCommandModule.SESSION_ID_ + '\n');
            sendMasterRequest();
            
        }

        isOnline_ = true;
        log("Connected to JBoss server : " + SERVERNAME);
    }

    public void disconnect() {


        if (commandPublisher_ != null) {
            commandPublisher_.close();
            commandPublisher_ = null;            
        }
        
        if (commandSubscriberThread_ != null) {
            commandSubscriberThread_.unsubscribe();
            commandSubscriberThread_ = null;
        }
        
        if (masterAckSubscriberThread_ != null) {
            masterAckSubscriberThread_.unsubscribe();
            masterAckSubscriberThread_ = null;
        }

        if (masterReqSubscriberThread_ != null) {
            masterReqSubscriberThread_.unsubscribe();
            masterReqSubscriberThread_ = null;
        }


        if (masterCheckConsumer_ != null) {
            masterCheckConsumer_.stopsListening();
            masterCheckConsumer_ = null;
        }
        
        if (masterResetConsumer_ != null) {
            masterResetConsumer_.stopsListening();
            masterResetConsumer_ = null;
        }

        if (masterSender_ != null) {
            masterSender_.close();
            masterSender_ = null;
        }
        
        if (slaveRequestSender_ != null) {
            slaveRequestSender_.close();
            slaveRequestSender_ = null;
        }
        
        if (masterRequestPublisher_ != null) {
            masterRequestPublisher_.close();
            masterRequestPublisher_ = null;
        }

        if (slaveReqConsumerThread_ != null) {
            slaveReqConsumerThread_.stopsListening();
            slaveReqConsumerThread_ = null;
        }
        
        if (slaveAckSubscriberThread_ != null) {
            slaveAckSubscriberThread_.unsubscribe();
            slaveAckSubscriberThread_ = null;
        }

        if (isMaster_ && !NO_BUS_CONTROL_) {

            resetMasterQueue();

            try {

                // needs time for reset
                Thread.sleep((long)1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }


            log("All Connections closed for MASTER session ...");
            // TODO notify that SESSION has no MASTER now...
        }
            
        else {


            log("All Connections closed for SLAVE session ...");
            //RPnDesktopPlotter.getUIFrame().enableNoteboard();
            //RPnDesktopPlotter.getUIFrame().enableAllCommands();            

        }

        isOnline_ = false;

    }

    public void subsMasterAck() {

        if (masterAckSubscriberThread_ == null)
            masterAckSubscriberThread_ = new RPnSubscriberThread(RPN_MASTER_ACK_TOPIC_NAME);

        masterAckSubscriberThread_.start();
        

        System.out.println("Will be listening to MASTER ACK now...");

    }

    public void updateMasterQueue() {


        /*
         *  UPDATES THE MASTER_QUEUE STATUS
         */
         if (!isFirewalled()) {

            // FILLs UP THE MASTER_QUEUE with proper CONTROL MSGs
            if (masterSender_ == null) {
                masterSender_ = new RPnHttpSender(RPN_MASTER_QUEUE_NAME);
            }

            // CHECK IF FIFO really ??
            masterSender_.send(MASTER_ACK_LOG_MSG + '|' + clientID_);

         } else {

                try {

                System.out.println("Will now hit MKP Mediator URL..." + '\n');

                URL rpnMediatorURL = new URL(RPN_MEDIATORPROXY_URL + "rpnmasterqueueproxy" + "?" + RPnNetworkStatus.RPN_MEDIATORPROXY_REQ_ID_TAG + '=' +
                                             RPnNetworkStatus.RPN_MEDIATORPROXY_MASTER_UPDATE_TAG + "&" +
                                             RPnNetworkStatus.RPN_MEDIATORPROXY_CLIENT_ID_TAG + '=' + clientID());

                URLConnection rpnMediatorConn = rpnMediatorURL.openConnection();
                BufferedReader buffReader = new BufferedReader(new InputStreamReader(rpnMediatorConn.getInputStream()));

                String text;
		StringBuffer fullText = new StringBuffer();
		Boolean buffFlag = false;

		while ((text = buffReader.readLine()) != null) {

		}



        } catch (Exception exc) {

            exc.printStackTrace();

        }



         }

        
    }


    public void resetMasterQueue() {


        /*
         *  RESETs THE MASTER_QUEUE STATUS // NON PERSISTENT
         */
        if (masterResetConsumer_ == null)

            if (isFirewalled()) {

                try {

                    System.out.println("WARN : a Http Polling context will be started...");
                    masterResetConsumer_ = new RPnHttpPoller(new RPnConsumer(RPN_MASTER_QUEUE_NAME,false,false),
                                          RPnHttpPoller.buildHitURL(RPN_MASTER_QUEUE_NAME));
                } catch (java.net.MalformedURLException ex) {
                    
                    ex.printStackTrace();
                }
                
            } else

                masterResetConsumer_ = new RPnConsumer(RPN_MASTER_QUEUE_NAME);

        // resets and releases the MASTER_QUEUE for others to listen to...
        masterResetConsumer_.reset();

        
        
        masterResetConsumer_ = null;
        System.out.println("MASTER QUEUE has being reset...");

    }

    public boolean checkMasterQueue() {

        // REQUESTS TO BECOME MASTER WILL BE A QUICK ACCESS METHOD TO THE MASTER_QUEUE
        boolean gotMaster = false;
        if (masterCheckConsumer_ == null)

            if (isFirewalled()) {

                try {

                    System.out.println("WARN : a Http Polling context will be started...");
                    masterCheckConsumer_ = new RPnHttpPoller(new RPnConsumer(RPN_MASTER_QUEUE_NAME,false,false),
                                          RPnHttpPoller.buildHitURL(RPN_MASTER_QUEUE_NAME));
                } catch (java.net.MalformedURLException ex) {

                    ex.printStackTrace();
                }

            } else

                // PERSISTENT !
                masterCheckConsumer_ = new RPnConsumer(RPN_MASTER_QUEUE_NAME,true,false);
  

        gotMaster = masterCheckConsumer_.check();
                
            
        // releases the MASTER_QUEUE for others to listen to...
        masterCheckConsumer_.stopsListening();
        masterCheckConsumer_ = null;
        System.out.println("CHECK MASTER QUEUE has returned : " + gotMaster + " for RPNSESSION with ID : " + MKPCommandModule.SESSION_ID_ + '\n');


        return gotMaster;

    }

    public void sendMasterRequest() {


        boolean gotMaster = checkMasterQueue();

        if (!gotMaster) {

            // I AM THE MASTER NOW...
            ackMasterRequest(clientID_);

        } else {

            // THERE IS A MASTER...SO ASK FOR THE LOCK...
            if (masterRequestPublisher_ == null)
                masterRequestPublisher_ = new RPnHttpPublisher(RPN_MASTER_REQ_TOPIC_NAME);

            masterRequestPublisher_.publish(MASTER_REQUEST_LOG_MSG + '|' + clientID_);

            System.out.println(clientID_ + " has requested MASTER lock for SESSION with ID : " + MKPCommandModule.SESSION_ID_ + '\n');

        }
    }

    public void ackMasterRequest(String clientID) {

        

        if (clientID.compareTo(clientID_) == 0) {


            // TODO : this should be a reconnect !
            if (isOnline() && !NO_BUS_CONTROL_) {

                // leaves the session as SLAVE
                disconnect();
                subsMasterAck();
                subsSlaveAck();

            }


            // and now BECOMES MASTER
            log("You are now being configured as MASTER for SESSION with ID : " + MKPCommandModule.SESSION_ID_ + '\n');

            RPnNetworkDialog.instance().setTitle(RPnNetworkDialog.TITLE + "MASTER");


            /*
             * RPN COMMAND PUBLISH
             */
            if (commandPublisher_ == null)
                commandPublisher_ = new RPnHttpPublisher(RPN_COMMAND_TOPIC_NAME);

            /*
             * SLAVE REQ RECEIVE
             */
            
            if (slaveReqConsumerThread_ == null) {
                slaveReqConsumerThread_ = new RPnConsumerThread(RPN_SLAVE_REQ_QUEUE_NAME);
                slaveReqConsumerThread_.start();
            }

            /*
             * MASTER REQ SUBS
             */
            if (masterReqSubscriberThread_ == null) {
                masterReqSubscriberThread_ = new RPnSubscriberThread(RPN_MASTER_REQ_TOPIC_NAME);
                // SETs THE MASTER_QUEUE EMPTY
                masterReqSubscriberThread_.start();
            }

             resetMasterQueue();
             updateMasterQueue();

	     if (!NO_BUS_CONTROL_)	
             	isMaster_ = true;

             isOnline_ = true;

        } else 
            log(clientID + " has being acknowledged as MASTER for SESSION with ID : " + MKPCommandModule.SESSION_ID_ + '\n');

    }

    public void ackSlaveRequest(String clientID) {


        log(clientID + " has being acknowledged as SLAVE of RPNSESSION with ID : " + MKPCommandModule.SESSION_ID_ + '\n');

        if (clientID.compareTo(clientID_) == 0) {

            // SLAVE SUBS to RPN COMMAND TOPIC
            if (commandSubscriberThread_ == null)
                commandSubscriberThread_ = new RPnSubscriberThread(RPN_COMMAND_TOPIC_NAME);

            commandSubscriberThread_.start();

            log("You are now following RPNSESSION with ID : " + MKPCommandModule.SESSION_ID_ + '\n');
            RPnNetworkDialog.instance().setTitle(RPnNetworkDialog.TITLE + "PUPIL");


            // TODO > disable ALL interface
            //RPnDesktopPlotter.getUIFrame().disableNoteboard();
            //RPnDesktopPlotter.getUIFrame().disableAllCommands();            
        }

    }

    public void subsSlaveAck() {

        if (slaveAckSubscriberThread_ == null)
            slaveAckSubscriberThread_ = new RPnSubscriberThread(RPN_SLAVE_ACK_TOPIC_NAME);

        slaveAckSubscriberThread_.start();


        System.out.println("Will be listening to SLAVE ACK now...");
    }

    public void sendSlaveRequest() {

        // REQUESTS TO BECOME A SLAVE
        if (slaveRequestSender_ == null)
            slaveRequestSender_ = new RPnHttpSender(RPN_SLAVE_REQ_QUEUE_NAME);

	if (!NO_BUS_CONTROL_) {

	        slaveRequestSender_.send(SLAVE_REQ_LOG_MSG + '|' + clientID_);
        	System.out.println(SLAVE_REQ_LOG_MSG + '|' + clientID_);

	} else {	

		// the slave request is always accepted...
        	RPnHttpPublisher publisher = new RPnHttpPublisher(RPnNetworkStatus.RPN_SLAVE_ACK_TOPIC_NAME);
        	publisher.publish(RPnNetworkStatus.SLAVE_ACK_LOG_MSG + '|' + clientID_);
        	publisher.close();
	}
    }

    public void sendCommand(String commandDesc) {
        
        commandPublisher_.publish(trimURL(commandDesc));
        
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "Command successfully published was : " + commandDesc);
        log ("COMMAND EXECUTED : = " + extractCommandName(commandDesc));
        
    }

    public void sendCommand(Object obj) {


        commandPublisher_.publish(obj);

        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "Object successfully published ! ");
        
    }

    public static RPnNetworkStatus instance() {

        if (instance_ == null) 
            instance_ = new RPnNetworkStatus();

        return instance_;
    }

    public static String filterClientID(String text) {

        String clientID = text.substring(text.indexOf('|') + 1);
        return clientID;
    }


    public static String trimLocalJmsPrefix(String jmsName) {

        // LOCAL JNDI DOES NOT USE jms/ prefix...
        return jmsName.substring(3, jmsName.length());
    }

    public static String trimURL(String url) {



        try {
            return java.net.URLEncoder.encode(url, "UTF-8");
        } catch (java.io.UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    protected String extractCommandName(String commandDesc) {
        
        // we shoud start using DOM
        /*String commandName = commandDesc.substring(15,commandDesc.indexOf("phasespace") - 2);
        if (commandName.startsWith("FOCUS"))
            return " ";
        
        return commandDesc.substring(15,commandDesc.indexOf("phasespace") - 2);                */

	if (commandDesc.contains("MARK")) return "MARK";
	if (commandDesc.contains("CLEAR")) return "CLEAR";
	if (commandDesc.contains("CLOSE")) return "CLOSE";

	return "UNKNOWN COMMAND";
    }
}
