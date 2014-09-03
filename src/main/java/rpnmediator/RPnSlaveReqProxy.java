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
public class RPnSlaveReqProxy extends RPnMediatorProxy {


    // COMMAND
    private static RPnProxyConsumer consumer_ = null;
    //private static RPnConsumerThread consumerThread_ = null;

    public static String QUEUE_NAME = RPnNetworkStatus.RPN_SLAVE_REQ_QUEUE_NAME;

    public static Vector recDatalog_ = new Vector();
    public static String clientID_ = null;


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        String reqId = (String) request.getParameter(RPnNetworkStatus.RPN_MEDIATORPROXY_REQ_ID_TAG);
        String clientId = (String) request.getParameter(RPnNetworkStatus.RPN_MEDIATORPROXY_CLIENT_ID_TAG);

        if ((reqId == null) || (clientId == null))

            responseErrorMsg(response,WRONG_INPUT_ERROR_MSG);

        else if (reqId.compareTo(RPnNetworkStatus.RPN_MEDIATORPROXY_POLL_TAG) == 0) {

                //response.setContentType("text/xml");
                //response.setContentType("text/html");

                // this will enable the browser output...
                response.setContentType( "text/xml;charset=UTF-8" );

                PrintWriter writer = response.getWriter();

               // if (clientID_.compareTo(clientId) == 0) {
                System.out.println("Will now listen to : " + RPnNetworkStatus.RPN_SLAVE_REQ_QUEUE_NAME);
                consumer_ = new RPnProxyConsumer(RPnNetworkStatus.trimLocalJmsPrefix(RPnNetworkStatus.RPN_SLAVE_REQ_QUEUE_NAME),
                                         recDatalog_,false);


                    consumer_.startsListening();

                    while (!recDatalog_.isEmpty()) {

                        String command = (String) recDatalog_.remove(recDatalog_.size() - 1);

                        // for DEBUGING
                        System.out.println("Message received at RPnSlaveReqProxy : " + '\n' + command);
                        writer.println(command);
                    }

                consumer_.stopsListening();
            

        } else if (reqId.compareTo(RPnNetworkStatus.RPN_MEDIATORPROXY_LISTENING_TAG) == 0) {

            // TODO this means only one SLAVE !!!
            clientID_ = clientId.toString();

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
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {

        
    }
}
