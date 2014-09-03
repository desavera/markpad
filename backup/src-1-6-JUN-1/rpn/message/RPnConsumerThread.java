/*
 * Instituto de Matematica Pura e Aplicada - IMPA
 * Departamento de Dinamica dos Fluidos
 *
 */

package rpn.message;

import java.net.MalformedURLException;

public class RPnConsumerThread extends Thread {

    private RPnMessageListener consumer_ = null;


    public RPnConsumerThread(String queueName) {
        this(queueName,false);
    }

    public RPnConsumerThread(String queueName,boolean persistent) {

        try {


        if (RPnNetworkStatus.instance().isFirewalled()) {

            System.out.println("WARN : a Http Polling context will be started for... " + queueName);
            consumer_ = new RPnHttpPoller(new RPnConsumer(queueName,persistent,false),
                                          RPnHttpPoller.buildHitURL(queueName));
        } else

            consumer_ = new RPnConsumer(queueName,persistent,false);
        } catch (java.net.MalformedURLException ex) {
            ex.printStackTrace();
        }
    }

    public RPnConsumerThread(RPnConsumer consumer) {
        consumer_ = consumer;
    }

    public void run() {       
        consumer_.startsListening();        
    }

    public void stopsListening() {
        consumer_.stopsListening();
    }
}
