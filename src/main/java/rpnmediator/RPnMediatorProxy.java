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

@WebListener
public class RPnMediatorProxy extends HttpServlet  implements ServletContextListener {

    
    public static String WRONG_INPUT_ERROR_MSG = "WRONG INPUT PARAMETERS...";
    public static String RECEIVER_INITIALIZATION_ERROR_MSG = "RECEIVER NOT INITIALIZED...";
    public static String QUEUECONNECTION_CLOSE_ERROR_MSG = "WAS UNABLE TO CLOSE QUEUE CONNECTION...";


    protected void responseErrorMsg(HttpServletResponse response,String errMessage) {

        

            response.setContentType("text/html");

            try {

                PrintWriter writer = response.getWriter();

                writer.println("<html>");
                writer.println("<head>");
                writer.println("<title>JMS Servlet</title>");
                writer.println("</head>");
                writer.println("<body bgcolor=white>");
              

                writer.println("<h1>Response Error... " + errMessage + "</h1>");

                writer.println("</body>");
                writer.println("</html>");

            } catch (IOException exc) {
                
                exc.printStackTrace();
            }
    }

    protected String ex2str(Throwable t)
    {

       try {

          ByteArrayOutputStream os = new ByteArrayOutputStream();
          PrintWriter pw = new PrintWriter(os);
          t.printStackTrace(pw);
          pw.flush();
          
          return new String(os.toByteArray());

       } catch (Throwable e) {
          return t.toString();
       }
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
     
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {


    }
}
