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
public class RPnHttpObjPoller extends RPnHttpPoller {
    

    public RPnHttpObjPoller(RPnMessageListener messageParser,String hitURL) {
	super(messageParser,hitURL);
    }

    public void startsListening() {
           
        try {

            while (!end()) {

                

                String objCommandURL = new String(hitURL() + "?" + RPnNetworkStatus.RPN_MEDIATORPROXY_REQ_ID_TAG + '=' +
                                             RPnNetworkStatus.RPN_MEDIATORPROXY_NOTEBOARD_POLL_TAG + '&' +
                                             RPnNetworkStatus.RPN_MEDIATORPROXY_CLIENT_ID_TAG + '=' + RPnNetworkStatus.instance().clientID());

                    //Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO,"Will now check command proxy objects...");
                    if (messageParser().listeningName() == RPnNetworkStatus.RPN_COMMAND_TOPIC_NAME) {

                        URL rpnMediatorURL = new URL(objCommandURL);

                        URLConnection rpnMediatorConn = rpnMediatorURL.openConnection();

                        try {

                            ObjectInputStream in = new ObjectInputStream(rpnMediatorConn.getInputStream());
                            messageParser().parseMessageObject(in.readObject());

                        } catch (java.io.EOFException ex) {

                            //Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO,"No objects to be returned yet...");

                        }
                    }

                // this is for not bringing JBoss down !!!
                //Thread.sleep((long)500);
            }

        } catch (Exception exc) {

            exc.printStackTrace();

        } 
    }

}
