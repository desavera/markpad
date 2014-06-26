/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mkp;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import static java.awt.GraphicsDevice.WindowTranslucency.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import java.util.logging.*;
import rpn.message.*;
import rpn.*;
import salvo.jesus.graph.java.awt.geom.*;
import wave.util.RealVector;
import wave.util.RectBoundary;
import wave.multid.Coords2D;
import wave.multid.graphs.ClippedShape;
import wave.multid.view.ViewingTransform;
import wave.multid.Space;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.logging.*;
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

    public static int INIT_GLASS_STATE = 0;
    public static int CONFIGURED_GLASS_STATE = 1;

    public static float INIT_GLASS_OPACITY = 0.3f;
    public static float CONFIGURED_GLASS_OPACITY = 0.3f;
    public static float CONFIGURED_PUPIL_GLASS_OPACITY = 0.3f;

    private static boolean TRANSLUCENCY_SUPPORT = false;




    public static String DTDPATH = System.getProperty("mkphome") + System.getProperty("file.separator") + "share" + System.getProperty("file.separator") + "mkp-dtd" + System.getProperty("file.separator");
    private static MKPConfigReader configReader_;
    private static InputStream configStream_;

    ViewingTransform viewingTransf_; 
    MKPGlassPane pad_;
    int glassState_;
    MKPBackgroundFrame bckgdFrame_;
    MKPControlFrame controlFrame_;
    Dimension savedSize_;
    Point savedLocation_;

    private MKPGlassFrame() {    
        
        super("markPad Window");

        setSize(Toolkit.getDefaultToolkit().getScreenSize().width,Toolkit.getDefaultToolkit().getScreenSize().height);
        //setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	// No borders.
	setUndecorated(true);

	initMultid();

	pad_ = new MKPGlassPane(this);
        getContentPane().add(pad_);

        // Set the window to 55% opaque (45% translucent).
	if (TRANSLUCENCY_SUPPORT) setOpacity(INIT_GLASS_OPACITY);
	//setBackground(new Color(0, 255, 0, 0));

	// No control to platform at all...
	//setLocationByPlatform(false);

        // Display the window.
        setVisible(true);

	//setExtendedState(getExtendedState() | MAXIMIZED_BOTH);

	glassState_ = INIT_GLASS_STATE;


	controlFrame_ = new MKPControlFrame(this);

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

    }

    protected void initMultid() {

	double[] mins = new double[]{0.,0.};
	double[] maxs = new double[]{1.,1.};

	RectBoundary boundary = new RectBoundary(new RealVector(mins),new RealVector(maxs));

	wave.multid.graphs.ClippedShape clipping = new wave.multid.graphs.ClippedShape(boundary);

	Space mSpace = new Space("", 2);
        int[] testeArrayIndex = {0, 1};
        RPnProjDescriptor projDescriptor = new RPnProjDescriptor(mSpace, "", 700, 700, testeArrayIndex, false);
        viewingTransf_ = projDescriptor.createTransform(clipping);
    }

    //
    // Accessors/Mutators
    //
    public MKPGlassPane padPane() {return pad_;}
    public int getGlassState() {return glassState_;}
    public void setGlassState(int state) {

	glassState_ = state;
/*
       	if (RPnNetworkStatus.instance().isOnline() && RPnNetworkStatus.instance().isMaster())
		if (state == CONFIGURED_GLASS_STATE && TRANSLUCENCY_SUPPORT)
			setOpacity(CONFIGURED_GLASS_OPACITY);

       	if (RPnNetworkStatus.instance().isOnline() && !RPnNetworkStatus.instance().isMaster())
		if (state == CONFIGURED_GLASS_STATE && TRANSLUCENCY_SUPPORT)
			setOpacity(CONFIGURED_PUPIL_GLASS_OPACITY);
*/
    }

    public ViewingTransform viewingTransform() { return viewingTransf_;}

    public static MKPGlassFrame instance() {


	if (instance_ == null)
		instance_ = new MKPGlassFrame();

	return instance_;

    }

    public void execSetPadBackgroundCommand(SerializableBufferedImage bi) {

	bckgdFrame_.setSize(getSize());
	bckgdFrame_.setLocation(getLocation());

	bckgdFrame_.bckgdPanel_.setBackgroundImage(bi);

	setVisible(false);
	
	bckgdFrame_.setVisible(true);
	bckgdFrame_.bckgdPanel_.invalidate();
	bckgdFrame_.bckgdPanel_.repaint();

	setVisible(true);

       	if (RPnNetworkStatus.instance().isOnline() && !RPnNetworkStatus.instance().isMaster())
       		RPnHttpPoller.POLLING_MODE = RPnHttpPoller.TEXT_POLLER;
    }

    public void execCloseCommand() {

       	dispose();  
	RPnNetworkStatus.instance().disconnect();
       	System.exit(0);  
    }

    public void execClearCommand() {

	if (pad_.getController() != null)
		pad_.getController().clear();
    }

    public void execMarkCommand(String mins,String maxs) {

	double[] p1 = new RealVector(mins).toDouble();
	double[] p2 = new RealVector(maxs).toDouble();

	pad_.setMarkMode(MKPGlassPane.HIGHLIGHT_MODE);
	pad_.highLightController_.mark(p1,p2);
    }

    public void execDrawCommand(SerializablePathIterator it) {

	pad_.setMarkMode(MKPGlassPane.DRAW_MODE);
	pad_.drawController_.updatePath(it);
    }

    public void execChangeMarkModeCommand(int mode) {

	pad_.setMarkMode(mode);
    }

    public void execSetPadBoundsCommand(String mins,String maxs) {

	double[] p1 = new RealVector(mins).toDouble();
	double[] p2 = new RealVector(maxs).toDouble();

	Coords2D minDCoords = new Coords2D();
	Coords2D maxDCoords = new Coords2D();
	
	viewingTransf_.viewPlaneTransform(new Coords2D(p1),minDCoords);
	viewingTransf_.viewPlaneTransform(new Coords2D(p2),maxDCoords);

	setSize(new Double(maxDCoords.getX() - minDCoords.getX()).intValue(),new Double(maxDCoords.getY() - minDCoords.getY()).intValue());
	setLocation(new Double(minDCoords.getX()).intValue(),new Double(minDCoords.getY()).intValue());

	setGlassState(MKPGlassFrame.CONFIGURED_GLASS_STATE);
	pad_.menu_.setReadyState();

        if (RPnNetworkStatus.instance().isOnline() && !RPnNetworkStatus.instance().isMaster()) {
	
		bckgdFrame_ = new MKPBackgroundFrame();
        	RPnHttpPoller.POLLING_MODE = RPnHttpPoller.OBJ_POLLER;
	}
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

        } else TRANSLUCENCY_SUPPORT = true;
        
        //JFrame.setDefaultLookAndFeelDecorated(true);

        // Create the GUI on the event-dispatching thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                MKPGlassFrame tw = MKPGlassFrame.instance();

		tw.setAlwaysOnTop(false);
		//com.sun.awt.AWTUtilities.setWindowOpaque(tw,false);

		try {

		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();

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

