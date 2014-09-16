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
public class RPnPupilPublishProxy extends RPnMediatorProxy {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String reqId = (String) request.getParameter(RPnNetworkStatus.RPN_MEDIATORPROXY_REQ_ID_TAG);
        String clientId = (String) request.getParameter(RPnNetworkStatus.RPN_MEDIATORPROXY_CLIENT_ID_TAG);
        String topicName = (String) request.getParameter(RPnNetworkStatus.TOPIC_NAME);


        if ((reqId == null) && (clientId == null) && (topicName == null))  {

                System.out.println("preparing to publish the object instance for pupil... \n");
                RPnPublisher publisher = null;
                ObjectInputStream in = new ObjectInputStream(request.getInputStream());

                try {

                    System.out.println("Will now publish the object instance for pupil... \n");
                    Object obj = in.readObject();

		    publisher = new RPnPublisher(RPnNetworkStatus.trimLocalJmsPrefix(RPnNetworkStatus.RPN_PUPIL_COMMAND_TOPIC_NAME),true);
                    publisher.publish(obj);

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                in.close();
		if (publisher != null)
                	publisher.close();           
        }

//            responseErrorMsg(response,WRONG_INPUT_ERROR_MSG);

        else if (reqId.compareTo(RPnNetworkStatus.RPN_MEDIATORPROXY_PUBLISH_TAG) == 0) {


	    // only for binary msgs topicName is null
            topicName = RPnNetworkStatus.trimLocalJmsPrefix(topicName);

            String logMsg = (String) request.getParameter(RPnNetworkStatus.RPN_MEDIATORPROXY_LOG_MSG_TAG);

            RPnPublisher publisher = new RPnPublisher(topicName,true);

            
            publisher.publish(logMsg);
            
            publisher.close();

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
