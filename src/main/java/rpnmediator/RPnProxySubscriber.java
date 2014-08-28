/*
 * Instituto de Matematica Pura e Aplicada - IMPA
 * Departamento de Dinamica dos Fluidos
 *
 */
package rpnmediator;

import rpn.message.RPnSubscriber;



import java.util.*;


public class RPnProxySubscriber extends RPnSubscriber {


    private HashMap subsDatalogOBJ_;
    private HashMap subsDatalogTXT_;


    public RPnProxySubscriber(String topicName,HashMap subsDatalogOBJ,HashMap subsDatalogTXT)  {

        super(topicName,true);
        subsDatalogOBJ_ = subsDatalogOBJ;
        subsDatalogTXT_ = subsDatalogTXT;
    }

    public void parseMessageText(String text) {


        System.out.println("Subscriber Proxy will parse msgs now..." + text);
        Set entries = subsDatalogTXT_.entrySet();

        Iterator it = entries.iterator();
        while (it.hasNext()) {

            System.out.println("Map for subs has entries...");
            Map.Entry entry = (Map.Entry) it.next();
            Vector data = (Vector) entry.getValue();
            data.add(text);



        }
    }

    public void parseMessageObject(Object obj) {

        System.out.println("Subscriber Proxy will parse object now...");
        Set entries = subsDatalogOBJ_.entrySet();

        Iterator it = entries.iterator();
        while (it.hasNext()) {

            System.out.println("Map for subs has entries...");
            Map.Entry entry = (Map.Entry) it.next();
            Vector data = (Vector) entry.getValue();
            data.add(obj);
        }

    }
}
