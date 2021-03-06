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
public class RPnHttpTxtPoller implements RPnResetableListener {
    

    public static int  TEXT_POLLER = 0;
    public static int OBJ_POLLER = 1;
    public static volatile int POLLING_MODE = TEXT_POLLER;


    private RPnMessageListener messageParser_ = null;
    private String hitURL_ = null;
    private boolean end_ = false;

    public RPnHttpTxtPoller(RPnMessageListener messageParser,String hitURL) {
        
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

                // this is for not bringing JBoss down !!!
                Thread.sleep((long)500);
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
