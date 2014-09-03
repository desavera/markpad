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
public class RPnHttpTxtPoller extends RPnHttpPoller {
    

    public RPnHttpTxtPoller(RPnMessageListener messageParser,String hitURL) {
	super(messageParser,hitURL);
    }

    public void startsListening() {
           
        try {

            while (!end()) {

                String msgCommandURL = new String(hitURL() + "?" + RPnNetworkStatus.RPN_MEDIATORPROXY_REQ_ID_TAG + '=' +
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
                        messageParser().parseMessageText(fullText.toString());
                    }
                    //else
                    //  System.out.println("no message retrieved from proxy... " + '\n');

                // this is for not bringing JBoss down !!!
                //Thread.sleep((long)500);
            }

        } catch (Exception exc) {

            exc.printStackTrace();

        } 
    }
}
