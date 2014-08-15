package rpnmediator;

/*
 * Instituto de Matematica Pura e Aplicada - IMPA
 * Departamento de Dinamica dos Fluidos
 *
 */


import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebListener;




import rpn.message.*;


/**
 *
 * @author mvera
 */
@WebListener
public class RPnMasterCommandProxy extends RPnMediatorProxy {


    // COMMAND
    private static RPnProxySubscriber subscriber_ = null;
    private static RPnSubscriberThread subscriberThread_ = null;

    public static String TOPIC_NAME = RPnNetworkStatus.RPN_MASTER_COMMAND_TOPIC_NAME;

    public static HashMap subsDatalogOBJ_ = new HashMap();
    public static HashMap subsDatalogTXT_ = new HashMap();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        String reqId = (String) request.getParameter(RPnNetworkStatus.RPN_MEDIATORPROXY_REQ_ID_TAG);
        String clientId = (String) request.getParameter(RPnNetworkStatus.RPN_MEDIATORPROXY_CLIENT_ID_TAG);


        if ((reqId == null) || (clientId == null))

               responseErrorMsg(response,WRONG_INPUT_ERROR_MSG);
                
        else if (reqId.compareTo(RPnNetworkStatus.RPN_MEDIATORPROXY_POLL_TAG) == 0) {
                
                    Vector msgQueue = (Vector)subsDatalogTXT_.get(clientId);
                    
                    if (msgQueue != null)
                    while (!msgQueue.isEmpty()) {

                        Object removed = msgQueue.remove(msgQueue.size() - 1);

                        if (removed instanceof String) {

                            // this will enable the browser output...
                            response.setContentType("text/xml;charset=UTF-8");

                            PrintWriter writer = response.getWriter();

                            String command = (String) removed;

                            // for DEBUGING
                            System.out.println("Message being retrieved by RPnCommandProxy : " + '\n' + command);
                            writer.println(command);

                        } 
                    }

        } else if (reqId.compareTo(RPnNetworkStatus.RPN_MEDIATORPROXY_NOTEBOARD_POLL_TAG) == 0) {

            Vector msgQueue = (Vector)subsDatalogOBJ_.get(clientId);

            if (msgQueue != null)
            while (!msgQueue.isEmpty()) {

                Object removed = msgQueue.remove(msgQueue.size() - 1);
                response.setContentType("application/octet-stream");

                ObjectOutputStream out = new ObjectOutputStream(response.getOutputStream());

                System.out.println("Object being retrieved by RPnCommandProxy : " + '\n');
                out.writeObject(removed);
            }
           
        } else if (reqId.compareTo(RPnNetworkStatus.RPN_MEDIATORPROXY_LISTENING_TAG) == 0) {

            System.out.println("Listener " + clientId + " willing to be registred...");

	    // either one ... just chose TXT
            if (!subsDatalogTXT_.containsKey(clientId)) {
                System.out.println("Listener " + clientId + " is being registred...");
                subsDatalogTXT_.put(clientId, new Vector());
                subsDatalogOBJ_.put(clientId, new Vector());
            }
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        doGet(request,response);
    }

    @Override
    public void init() throws ServletException {

    }
    
    @Override
    public void contextInitialized(ServletContextEvent event) {
     
        System.out.println("Will now listen to : " + RPnNetworkStatus.RPN_MASTER_COMMAND_TOPIC_NAME);
        subscriber_ = new RPnProxySubscriber(RPnNetworkStatus.trimLocalJmsPrefix(RPnNetworkStatus.RPN_MASTER_COMMAND_TOPIC_NAME),
                                            subsDatalogOBJ_,subsDatalogTXT_);

        if (subscriberThread_ == null) {
            subscriberThread_ = new RPnSubscriberThread(subscriber_);
        }

        subscriberThread_.start();

    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {

        if (subscriberThread_ != null)
            subscriberThread_.unsubscribe();
    }
}
