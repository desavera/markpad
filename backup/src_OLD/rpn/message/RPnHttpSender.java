/*
 * Instituto de Matematica Pura e Aplicada - IMPA
 * Departamento de Dinamica dos Fluidos
 *
 */

package rpn.message;


import java.io.*;
import java.net.*;


public class RPnHttpSender extends RPnSender {


    private String hitURL_ = null;
    private String queueName_ = null;

    public RPnHttpSender(String queueName) {

        try {
            queueName_ = queueName.toString();
            hitURL_ = RPnHttpPoller.buildHitURL("SEND",queueName.toString());
        } catch (MalformedURLException ex) {
                ex.printStackTrace();
            }
    }

    public void send(String msg) {

       try {           

                String fullURL = new String(hitURL_ + "?" + RPnNetworkStatus.RPN_MEDIATORPROXY_REQ_ID_TAG + '='
                                                        + RPnNetworkStatus.RPN_MEDIATORPROXY_SEND_TAG 
                                                        + '&' + RPnNetworkStatus.QUEUE_NAME + '=' + queueName_
                                                        + '&' + RPnNetworkStatus.RPN_MEDIATORPROXY_CLIENT_ID_TAG + '=' + RPnNetworkStatus.instance().clientID()
                                                        + '&' + RPnNetworkStatus.RPN_MEDIATORPROXY_LOG_MSG_TAG + '=' + msg.toString());

                System.out.println("The URL message to be sent is : " + fullURL);

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

    public void close() {}

}
