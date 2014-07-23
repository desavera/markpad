/*
 * Instituto de Matematica Pura e Aplicada - IMPA
 * Departamento de Dinamica dos Fluidos
 *
 */

package rpnmediator;

import rpn.message.RPnConsumer;
import javax.jms.*;


import java.util.Vector;

public class RPnProxyConsumer extends RPnConsumer {


    private Vector recDatalog_ = null;


    public RPnProxyConsumer(String queueName,Vector recDatalog,boolean persistent) {

        
        super(queueName, persistent, true);
        recDatalog_ = recDatalog;
    }


    public void parseMessageText(String text) {

        recDatalog_.add(text);
    }

    public void startsListening() {

        try {

            //Message message = receiver_.receive((long)15000);
            Message message = consume();


            if (message instanceof TextMessage) {

                System.out.println("Message recieved from : " + listeningName() + '\n');

                String text = ((TextMessage) message).getText();
                parseMessageText(text);

            }


        } catch (Exception exc) {

            exc.printStackTrace();

        }
    }


}
