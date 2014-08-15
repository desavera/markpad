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
public abstract class RPnHttpPoller implements RPnResetableListener {
    

    public static int  TEXT_POLLER = 0;
    public static int OBJ_POLLER = 1;
    public static volatile int POLLING_MODE = TEXT_POLLER;


    private RPnMessageListener messageParser_ = null;
    private String hitURL_ = null;
    private boolean end_ = false;

    // OBJ and TXT Pollers have being created instead
    private RPnHttpPoller(RPnMessageListener messageParser,String hitURL) {
        
        messageParser_ = messageParser;
        hitURL_ = hitURL.toString();
    }

    public abstract void connect();

    public static String buildHitURL(String hitTarget) throws java.net.MalformedURLException {

        if (hitTarget.startsWith(RPnNetworkStatus.RPN_MASTER_COMMAND_TOPIC_NAME)) {

            return RPnNetworkStatus.RPN_MEDIATORPROXY_URL + "rpnmastercommandproxy";

	} else if (hitTarget.startsWith(RPnNetworkStatus.RPN_PUPIL_COMMAND_TOPIC_NAME)) {

            return RPnNetworkStatus.RPN_MEDIATORPROXY_URL + "rpnpupilcommandproxy";
            
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
