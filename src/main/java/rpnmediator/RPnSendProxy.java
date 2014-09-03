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
public class RPnSendProxy extends RPnMediatorProxy {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String reqId = (String) request.getParameter(RPnNetworkStatus.RPN_MEDIATORPROXY_REQ_ID_TAG);
        String clientId = (String) request.getParameter(RPnNetworkStatus.RPN_MEDIATORPROXY_CLIENT_ID_TAG);

        if ((reqId == null) || (clientId == null))

            responseErrorMsg(response,WRONG_INPUT_ERROR_MSG);

        else if (reqId.compareTo(RPnNetworkStatus.RPN_MEDIATORPROXY_SEND_TAG) == 0) {


            String queueName = RPnNetworkStatus.trimLocalJmsPrefix((String) request.getParameter(RPnNetworkStatus.QUEUE_NAME));
            String logMsg = (String) request.getParameter(RPnNetworkStatus.RPN_MEDIATORPROXY_LOG_MSG_TAG);

            RPnSender sender = new RPnSender(queueName,true);
            sender.send(logMsg);

            sender.close();

        }
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
