/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mkp;

import java.awt.*;
import javax.swing.*;
import static java.awt.GraphicsDevice.WindowTranslucency.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import java.util.logging.*;
import rpn.message.*;
import rpn.*;
import wave.util.RealVector;
import wave.util.RectBoundary;
import wave.multid.Coords2D;
import wave.multid.CoordsArray;
import wave.multid.graphs.ClippedShape;
import wave.multid.view.ViewingTransform;
import wave.multid.Space;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.logging.*;
import java.awt.Dimension;
import javax.swing.JOptionPane;
import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierFactory;
import org.xml.sax.SAXParseException;

/**
 *
 * @author mvera
 */
public class MKPGlassFrame extends JFrame {


    private static MKPGlassFrame instance_;


    public static String DTDPATH = System.getProperty("mkphome") + System.getProperty("file.separator") + "share" + System.getProperty("file.separator") + "mkp-dtd" + System.getProperty("file.separator");
    //public static String DTDPATH = "/impa/home/w/mvera/oficina/impa/fluid/markpad/share/mkp-dtd/";
    private static MKPConfigReader configReader_;
    private static InputStream configStream_;

    public static Dimension SCREEN_SIZE = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

    RPnNetworkDialog netDialog_;
    ViewingTransform viewingTransf_; 
    PadDrawing padDrawing_;


    private MKPGlassFrame() {    
        
        super("markPad Window");
        //setLayout(new FlowLayout());

        setSize(900,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	padDrawing_ = new PadDrawing(this);
        getContentPane().add(padDrawing_);

        // Set the window to 55% opaque (45% translucent).
        setOpacity(0.40f);

        // Display the window.
        setVisible(true);

	setExtendedState(getExtendedState() | MAXIMIZED_BOTH);

	addWindowListener(   
	      new java.awt.event.WindowAdapter()   
     	 {  

       	 public void windowClosing( java.awt.event.WindowEvent e )   
       	 {  
	   if (RPnNetworkStatus.instance().isMaster() && RPnNetworkStatus.instance().isOnline()) {
		RPnNetworkStatus.instance().sendCommand(MKPXMLer.buildCloseXML());
		RPnNetworkStatus.instance().disconnect();
	   } else if (RPnNetworkStatus.instance().isOnline() && RPnNetworkStatus.NO_BUS_CONTROL_)
		RPnNetworkStatus.instance().disconnect();

	 	execCloseCommand();
       	 }  
      	});  

    	netDialog_ = RPnNetworkDialog.instance();
	initMultid();
    }

    protected void initMultid() {

	double[] mins = new double[]{0.,0.};
	double[] maxs = new double[]{1.,1.};

	RectBoundary boundary = new RectBoundary(new RealVector(mins),new RealVector(maxs));

	wave.multid.graphs.ClippedShape clipping = new wave.multid.graphs.ClippedShape(boundary);

	Space mSpace = new Space("", 2);
        int[] testeArrayIndex = {0, 1};
        RPnProjDescriptor projDescriptor = new RPnProjDescriptor(mSpace, "", SCREEN_SIZE.width,SCREEN_SIZE.height, testeArrayIndex, false);
        viewingTransf_ = projDescriptor.createTransform(clipping);
    }

    //
    // Accessors/Mutators
    //
    public ViewingTransform viewingTransform() { return viewingTransf_;}

    public static MKPGlassFrame instance() {


	if (instance_ == null)
		instance_ = new MKPGlassFrame();

	return instance_;

    }

    public void execCloseCommand() {

       	   	dispose() ;  
       	   	System.exit( 0 );  

    }

    public void execClearCommand() {

		padDrawing_.clear();

    }

    public void execMarkCommand(String mins,String maxs) {

	double[] p1 = new RealVector(mins).toDouble();
	double[] p2 = new RealVector(maxs).toDouble();


	Coords2D dCoords1 = new Coords2D();
	Coords2D dCoords2 = new Coords2D();
	CoordsArray wCoords1 = new CoordsArray(p1);
	CoordsArray wCoords2 = new CoordsArray(p2);

	viewingTransf_.viewPlaneTransform(wCoords1,dCoords1);
	viewingTransf_.viewPlaneTransform(wCoords2,dCoords2);

	int iP1X = dCoords1.getIntCoords()[0];
	int iP1Y = dCoords1.getIntCoords()[1];
	int iP2X = dCoords2.getIntCoords()[0];
	int iP2Y = dCoords2.getIntCoords()[1];

	padDrawing_.mark(new Point(iP1X,iP1Y),new Point(iP2X,iP2Y));
    }

    public void execNetworkMenuCommand() {       

        netDialog_.setVisible(true);
        netDialog_.toFront();

    }

    public static void main(final String[] args) {

        // Determine if the GraphicsDevice supports translucency.
        GraphicsEnvironment ge = 
            GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        //If translucent windows aren't supported, exit.
        if (!gd.isWindowTranslucencySupported(TRANSLUCENT)) {
            System.err.println(
                "Translucency is not supported");
                //System.exit(0);
        }
        
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create the GUI on the event-dispatching thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                MKPGlassFrame tw = MKPGlassFrame.instance();

		try {

		//GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();

        	// create a VerifierFactory with the default SAX parser
        	VerifierFactory factory = new com.sun.msv.verifier.jarv.TheFactoryImpl();
        	// compile a RELAX schema (or whatever schema you like)

        	org.iso_relax.verifier.Schema schema = factory.compileSchema(new File(DTDPATH + "markpad.dtd"));

        	// obtain a verifier
        	Verifier verifier = schema.newVerifier();
        	// this error handler will throw an exception if there is an error
        	verifier.setErrorHandler(com.sun.msv.verifier.util.ErrorHandlerImpl.theInstance);



		if (args.length > 0) {

        		if (verifier.verify(new File(args[0])))
            			System.out.println("The input document is valid");

	
       		     		configReader_ = MKPConfigReader.getReader(args[0], false, null);
       		     		configStream_ = configReader_.read();
       		     		configReader_.init(configStream_); //Reading input file

		}

        	} catch (FileNotFoundException ex) {

            		JOptionPane.showMessageDialog(instance_, "No input file !", "MKP", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();

        	} catch (VerifierConfigurationException ex) {

            		System.out.println("Error in configuration file");
			ex.printStackTrace();

        	} catch (SAXParseException e) {
            		System.out.println("The document is not valid");
            		System.out.println("Because:  " + e);
            		System.out.println("Line: " + e.getLineNumber());
            		System.out.println("Column: " + e.getColumnNumber());
            		// if the document is invalid, then the execution will reach here
            		// because we throw an exception for an error.
			e.printStackTrace();


        	} catch (SAXException ex) {

            		ex.printStackTrace();



        	} catch (IOException exception) {
        	} finally {
            		try {

               		 configStream_.close();
            		} catch (NullPointerException ex) {
            		} catch (IOException ex) {
               			 System.out.println("IO Error");
            		}

        	};
	
            }
        });
    }
}
