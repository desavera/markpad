/*
 * Instituto de Matematica Pura e Aplicada - IMPA
 * Departamento de Dinamica dos Fluidos
 *
 */

package rpn.message;


import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mvera
 */
public class RPnHttpObjPoller implements RPnResetableListener {
    

    public static int  TEXT_POLLER = 0;
    public static int OBJ_POLLER = 1;
    public static volatile int POLLING_MODE = OBJ_POLLER;


    private RPnMessageListener messageParser_ = null;
    private String hitURL_ = null;
    private boolean end_ = false;

    public RPnHttpObjPoller(RPnMessageListener messageParser,String hitURL) {
        
        messageParser_ = messageParser;
        hitURL_ = hitURL.toString();

        connect();
    }

    public void connect() {

        try {

                

                String fullURL = new String(hitURL_ + "?" + RPnNetworkStatus.RPN_MEDIATORPROXY_REQ_ID_TAG + '=' +
                                             RPnNetworkStatus.RPN_MEDIATORPROXY_LISTENING_TAG + "&" +
                                             RPnNetworkStatus.RPN_MEDIATORPROXY_CLIENT_ID_TAG + '=' + RPnNetworkStatus.instance().clientID());              

                URL rpnMediatorURL = new URL(fullURL);

                //System.out.println("Will now connect to RPn Mediator with URL..." + fullURL + '\n');
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

    public String listeningName() {
        return messageParser_.listeningName();
    }

    public void stopsListening() {
        end_ = true;
    }

    public void startsListening() {
           
        try {

            while (!end_) {

                

                String msgCommandURL = new String(hitURL_ + "?" + RPnNetworkStatus.RPN_MEDIATORPROXY_REQ_ID_TAG + '=' +
                                             RPnNetworkStatus.RPN_MEDIATORPROXY_POLL_TAG + '&' +
                                             RPnNetworkStatus.RPN_MEDIATORPROXY_CLIENT_ID_TAG + '=' + RPnNetworkStatus.instance().clientID());
                

                String objCommandURL = new String(hitURL_ + "?" + RPnNetworkStatus.RPN_MEDIATORPROXY_REQ_ID_TAG + '=' +
                                             RPnNetworkStatus.RPN_MEDIATORPROXY_NOTEBOARD_POLL_TAG + '&' +
                                             RPnNetworkStatus.RPN_MEDIATORPROXY_CLIENT_ID_TAG + '=' + RPnNetworkStatus.instance().clientID());

                // TODO > HttpObjPoller
                if (POLLING_MODE == TEXT_POLLER) {
                
                    URL rpnMediatorURL = new URL(msgCommandURL);

                    URLConnection rpnMediatorConn = rpnMediatorURL.openConnection();


		    BufferedReader buffReader = new BufferedReader(new InputStreamReader(rpnMediatorConn.getInputStream()));

                    String text;
                    StringBuffer fullText = new StringBuffer();
                    Boolean buffFlag = false;

                    while ((text = buffReader.readLine()) != null) {
                        buffFlag = true;
                        fullText.append(text);
                    }

                    if ((buffFlag) && (fullText.length() > 5)) {
                        messageParser_.parseMessageText(fullText.toString());
                    }
                    //else
                    //  System.out.println("no message retrieved from proxy... " + '\n');
                } else {

                    if (messageParser_.listeningName() == RPnNetworkStatus.RPN_COMMAND_TOPIC_NAME) {

                        URL rpnMediatorURL = new URL(objCommandURL);



                        URLConnection rpnMediatorConn = rpnMediatorURL.openConnection();

                        
                        //Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO,"Will now check command proxy objects...");

                        try {

                            ObjectInputStream in = new ObjectInputStream(rpnMediatorConn.getInputStream());
                            messageParser_.parseMessageObject(in.readObject());

                        } catch (java.io.EOFException ex) {

                            //Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO,"No objects to be returned yet...");

                        }

                        
                    }

                }

                // this is for not bringing JBoss down !!!
                //Thread.sleep((long)500);
            }

        } catch (Exception exc) {

            exc.printStackTrace();

        } 
    }

    public void parseMessageText(String text) {
        messageParser_.parseMessageText(text);
    }

    public void parseMessageObject(Object obj) {

        messageParser_.parseMessageObject(obj);

    }

    public static String buildHitURL(String hitTarget) throws java.net.MalformedURLException {

        if (hitTarget.startsWith(RPnNetworkStatus.RPN_COMMAND_TOPIC_NAME)) {

            return RPnNetworkStatus.RPN_MEDIATORPROXY_URL + "rpncommandproxy";
            
        } else if (hitTarget.startsWith(RPnNetworkStatus.RPN_MASTER_ACK_TOPIC_NAME)) {

            return RPnNetworkStatus.RPN_MEDIATORPROXY_URL + "rpnmasterackproxy";

        } else if (hitTarget.startsWith(RPnNetworkStatus.RPN_MASTER_REQ_TOPIC_NAME)) {

            return RPnNetworkStatus.RPN_MEDIATORPROXY_URL + "rpnmasterreqproxy";

        } else if (hitTarget.startsWith(RPnNetworkStatus.RPN_SLAVE_ACK_TOPIC_NAME)) {

            return RPnNetworkStatus.RPN_MEDIATORPROXY_URL + "rpnslaveackproxy";

        } else if (hitTarget.startsWith(RPnNetworkStatus.RPN_SLAVE_REQ_QUEUE_NAME)) {

            return RPnNetworkStatus.RPN_MEDIATORPROXY_URL + "rpnslavereqproxy";

        } else if (hitTarget.startsWith(RPnNetworkStatus.RPN_MASTER_QUEUE_NAME)) {

            return RPnNetworkStatus.RPN_MEDIATORPROXY_URL + "rpnmasterqueueproxy";
        
        } else throw new java.net.MalformedURLException();

    }
    
    public static String buildHitURL(String mode,String hitTarget) throws java.net.MalformedURLException {

        if (mode.startsWith("SEND"))
            return RPnNetworkStatus.RPN_MEDIATORPROXY_URL + "rpnsendproxy";
        else if (mode.startsWith("PUBLISH"))
            return RPnNetworkStatus.RPN_MEDIATORPROXY_URL + "rpnpublishproxy";
        
        else throw new java.net.MalformedURLException();
    }



    public void reset() {

        try {

                System.out.println("Will now hit RPn Mediator URL..." + '\n');

                URL rpnMediatorURL = new URL(hitURL_ + "?" + RPnNetworkStatus.RPN_MEDIATORPROXY_REQ_ID_TAG + '=' +
                                             RPnNetworkStatus.RPN_MEDIATORPROXY_MASTER_RESET_TAG);

                URLConnection rpnMediatorConn = rpnMediatorURL.openConnection();
                BufferedReader buffReader = new BufferedReader(new InputStreamReader(rpnMediatorConn.getInputStream()));

		String text;
		StringBuffer fullText = new StringBuffer();
		Boolean buffFlag = false;

		while ((text = buffReader.readLine()) != null) {
			buffFlag = true;
			fullText.append(text);
		}



        } catch (Exception exc) {

            exc.printStackTrace();

        }



    }

    public boolean check() {


        try {

                System.out.println("Will now hit RPn Mediator URL..." + '\n');

                URL rpnMediatorURL = new URL(hitURL_ + "?" + RPnNetworkStatus.RPN_MEDIATORPROXY_REQ_ID_TAG + '=' +
                                             RPnNetworkStatus.RPN_MEDIATORPROXY_MASTER_CHECK_TAG);

                URLConnection rpnMediatorConn = rpnMediatorURL.openConnection();
                BufferedReader buffReader = new BufferedReader(new InputStreamReader(rpnMediatorConn.getInputStream()));

		String text;
		StringBuffer fullText = new StringBuffer();
		Boolean buffFlag = false;

		while ((text = buffReader.readLine()) != null) {
			buffFlag = true;
			fullText.append(text);
		}



		if (buffFlag) {

                    System.out.println("The Master check routine returned : " + fullText.toString());
                    if(fullText.toString().compareTo("1") == 0) return true;
                    if(fullText.toString().compareTo("0") == 0) return false;

                    
                }
                else {


                    System.out.println("no message retrieved from proxy... " + '\n');
                    return false;

                }

        } catch (Exception exc) {

            exc.printStackTrace();

        }


        return false;
    }
}
