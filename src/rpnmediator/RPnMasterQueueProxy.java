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
public class RPnMasterQueueProxy extends RPnMediatorProxy {


    // COMMAND
    public static Vector masterLog_ = new Vector();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        String reqId = (String) request.getParameter(RPnNetworkStatus.RPN_MEDIATORPROXY_REQ_ID_TAG);
        String clientId = (String) request.getParameter(RPnNetworkStatus.RPN_MEDIATORPROXY_CLIENT_ID_TAG);

        if (reqId == null)

            responseErrorMsg(response,WRONG_INPUT_ERROR_MSG);

        else if (reqId.compareTo(RPnNetworkStatus.RPN_MEDIATORPROXY_MASTER_CHECK_TAG) == 0) {

                //response.setContentType("text/xml");
                //response.setContentType("text/html");

                // this will enable the browser output...
                response.setContentType( "text/xml;charset=UTF-8" );

                PrintWriter writer = response.getWriter();

                if (masterLog_.isEmpty()) {
                    // for DEBUGING
                    System.out.println("No Master registered for RPN SESSION...");
                    writer.println("0");

                }
                else {
                    // for DEBUGING
                    System.out.println("Got Master registered for RPN SESSION...");
                    writer.println("1");
                }

        } else if (reqId.compareTo(RPnNetworkStatus.RPN_MEDIATORPROXY_MASTER_UPDATE_TAG) == 0) {

            // for DEBUGING
            System.out.println("Master is being registered for RPN SESSION...");
            masterLog_.add(clientId);

        } else if (reqId.compareTo(RPnNetworkStatus.RPN_MEDIATORPROXY_MASTER_RESET_TAG) == 0) {
            
            // for DEBUGING
            System.out.println("Master is being unregistered for RPN SESSION...");
            masterLog_.removeAllElements();
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
