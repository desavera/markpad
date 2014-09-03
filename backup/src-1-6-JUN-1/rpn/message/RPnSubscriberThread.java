/*
 * Instituto de Matematica Pura e Aplicada - IMPA
 * Departamento de Dinamica dos Fluidos
 *
 */


package rpn.message;

import java.net.MalformedURLException;

/**
 *
 * @author mvera
 */
public class RPnSubscriberThread extends Thread {

    private RPnMessageListener subscriber_ = null;

    public RPnSubscriberThread(String topicName) {

        try {


        if (RPnNetworkStatus.instance().isFirewalled()) {

            System.out.println("WARN : a Http Polling context will be started...");
            subscriber_ = new RPnHttpPoller(new RPnSubscriber(topicName,false),
                                            RPnHttpPoller.buildHitURL(topicName));
        }

        else
            subscriber_ = new RPnSubscriber(topicName);

        } catch (java.net.MalformedURLException ex) {
            ex.printStackTrace();
        }

    }

    public RPnSubscriberThread(RPnSubscriber subscriber) {

        subscriber_ = subscriber;
    }


    public void run() {
        subscriber_.startsListening();
    }

    public void unsubscribe() {
        subscriber_.stopsListening();
    }
}

