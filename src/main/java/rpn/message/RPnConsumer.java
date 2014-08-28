/*
 * Instituto de Matematica Pura e Aplicada - IMPA
 * Departamento de Dinamica dos Fluidos
 *
 */

package rpn.message;

import javax.naming.*;
import javax.jms.*;
import java.io.StringBufferInputStream;
import org.xml.sax.helpers.XMLReaderFactory;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author mvera
 */
public class RPnConsumer implements RPnResetableListener {

    private boolean end_ = false;
    private QueueConnection queueConnection_ = null;
    private QueueReceiver receiver_ = null;
    private QueueConnectionFactory cf_ = null;
    private javax.jms.Queue queue_ = null;
    private String listeningName_;
    private int ackModel_ = Session.AUTO_ACKNOWLEDGE;

    private boolean isLocal_ = false;
    private boolean persistent_ = false;
    

    public RPnConsumer(String queueName) {
        this(queueName,false,false);
    }

    public RPnConsumer(String queueName,boolean persistent) {
        this(queueName,persistent,false);
    }

    public RPnConsumer(String queueName,boolean persistent,boolean isLocal) {

        listeningName_ = queueName;
        persistent_ = persistent;
        isLocal_ = isLocal;

        if (!RPnNetworkStatus.instance().isFirewalled())
            connect();

    }

    public void connect() {
        
        if (persistent_)
            ackModel_ = Session.CLIENT_ACKNOWLEDGE;

            try {


              Context context = null;

              if (!isLocal_) {

                    context = RPnSender.getInitialMDBContext();
                    cf_ = (QueueConnectionFactory) context.lookup("jms/RemoteConnectionFactory");
                    
                }
                else {

                    context = new InitialContext();
                    cf_ = (QueueConnectionFactory) context.lookup("java:/ConnectionFactory");
                    
                }

                queue_ = (javax.jms.Queue) context.lookup(listeningName_);

                queueConnection_ = cf_.createQueueConnection("rpn", "rpn.fluid");


                QueueSession queueSession = queueConnection_.createQueueSession(false, ackModel_);


                // this will keep the messages on the queue_...
                //QueueSession queueSession = queueConnection_.createQueueSession(false, Session.CLIENT_ACKNOWLEDGE);

                receiver_ = queueSession.createReceiver(queue_);

                queueConnection_.start();



                /*MBeanServer mBeanServer  = java.lang.management.ManagementFactory.getPlatformMBeanServer();
                ObjectName on = ObjectNameBuilder.DEFAULT.getJMSServerObjectName();
                MBeanInfo mbi = mBeanServer.getMBeanInfo(on);
                System.out.println(mbi.getClassName());
                MBeanAttributeInfo[] mbas = mbi.getAttributes();
                for (MBeanAttributeInfo mba : mbas)
                {
                System.out.println("attr: " + mba.getName() + " of type " + mba.getType());
                }

                MBeanOperationInfo[] mbos = mbi.getOperations();
                for (MBeanOperationInfo mbo : mbos)
                {
                System.out.println("oper: " + mbo.getName() );
                MBeanParameterInfo[] mbps = mbo.getSignature();
                for (MBeanParameterInfo mbp : mbps)
                {
                System.out.println("  param: " + mbp.getName());
                }
                System.out.println("   returns: " + mbo.getReturnType());
                }

                //get attributes on the JMSServerControl

                String[] qnames = (String[]) mBeanServer.getAttribute(on, "QueueNames");

                //invoke methods on the JMSServerControl
                mBeanServer.invoke(on, "createQueue" ...)

                JMXConnector connector = JMXConnectorFactory.connect(new JMXServiceURL(JMX_URL), new HashMap());

                MBeanServerConnection mbsc = connector.getMBeanServerConnection();

                ObjectName name=new ObjectName("org.jboss.messaging:module=JMS,type=Server");
                JMSServerControlMBean control = (JMSServerControlMBean)MBeanServerInvocationHandler.newProxyInstance(mbsc,name,JMSServerControlMBean.class,false);
                control.createQueue("TestQ","test");
                 */
            } catch (Exception exc) {

                exc.printStackTrace();

            }

        
    }

    public void startsListening() {
           
        try {

            if (queueConnection_ == null)
                connect();

            while (!end_) {

                System.out.println("Will now listen to " + listeningName_ + '\n');

                //Message message = receiver_.receive((long)15000);
                Message message = consume();
                

                if (message instanceof TextMessage) {

                    System.out.println("Message recieved from : " + listeningName_ + '\n');
                    
                    String text = ((TextMessage) message).getText();
                    parseMessageText(text);

                } 
            }

        } catch (Exception exc) {

            exc.printStackTrace();

        } 
    }

    public void stopsListening() {

        end_ = true;

        if (queueConnection_ != null) {
            try {
                queueConnection_.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }       
    }

    public Message consume() {

        try {
                System.out.println("Will now consume from queue... " + listeningName_ + '\n');
                //return receiver_.receiveNoWait();
                return receiver_.receive((long)5000);

        } catch (Exception exc) {

            exc.printStackTrace();
            return null;

        }
    }

    public void reset() {

        consume();
        stopsListening();

    }

    public boolean check() {

        if ((persistent_) && (consume() != null))
                return true;

        return false;
    }

     public void parseMessageObject(Object obj) {

        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "Object message received and not treated !");
     }

    public void parseMessageText(String text) {

        try {
            /*
             * checks if CONTROL MSG or COMMAND MSG
             */


            // CONTROL MESSAGES PARSING
            if (text.startsWith(RPnNetworkStatus.MASTER_ACK_LOG_MSG)) {

                // DO NOTHING...

            } else if (text.startsWith(RPnNetworkStatus.SLAVE_REQ_LOG_MSG)) {

                if (RPnNetworkStatus.instance().isMaster()) {

                    RPnSlaveReqDialog reqDialog = new RPnSlaveReqDialog(RPnNetworkStatus.filterClientID(text));
                    reqDialog.setVisible(true);

                }

            } else if (text.startsWith(RPnNetworkStatus.RPN_COMMAND_PREFIX)) {

                // COMMAND MESSAGES PARSING
                mkp.MKPCommandModule.init(XMLReaderFactory.createXMLReader(), new StringBufferInputStream(text));

		//TODO update the FRAME !!!
            }

        } catch (Exception exc) {

            exc.printStackTrace();

        }
    }

    public String listeningName() {
        return listeningName_;
    }
}
