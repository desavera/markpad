/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rpn.message;

/**
 *
 * @author mvera
 */
public interface RPnResetableListener extends RPnMessageListener {

    void reset();
    boolean check();

}
