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

    private RPnSubscriberThread objSubscriberThread_ = null;
    private RPnSubscriberThread txtSubscriberThread_ = null;

    public RPnSubscriberThread(String topicName) {

        try {


        if (RPnNetworkStatus.instance().isFirewalled()) {

            System.out.println("WARN : a Http Polling context will be started...");
            objSubscriberThread_ = new RPnSubscriberThread(new RPnHttpObjPoller(new RPnSubscriber(topicName,false),
                                            RPnHttpPoller.buildHitURL(topicName)));
            txtSubscriberThread_ = new RPnSubscriberThread(new RPnHttpTxtPoller(new RPnSubscriber(topicName,false),
                                            RPnHttpPoller.buildHitURL(topicName)));
        }

        else
            subscriber_ = new RPnSubscriber(topicName);

        } catch (java.net.MalformedURLException ex) {
            ex.printStackTrace();
        }

    }

    public RPnSubscriberThread(RPnMessageListener subscriber) {

        subscriber_ = subscriber;
    }


    public void run() {
	if (subscriber_ != null)
        	subscriber_.startsListening();

	else {
	
		objSubscriberThread_.start();
		txtSubscriberThread_.start();
	}
    }

    public void unsubscribe() {
	if (subscriber_ != null)
        	subscriber_.stopsListening();

	else {

		objSubscriberThread_.unsubscribe();
		txtSubscriberThread_.unsubscribe();
	}
    }
}

