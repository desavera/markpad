/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rpn.message;

/**
 *
 * @author mvera
 */
public interface RPnMessageListener {

    void connect();
    void parseMessageText(String text);
    void parseMessageObject(Object obj);
    String listeningName();
    void startsListening();
    void stopsListening();

}
