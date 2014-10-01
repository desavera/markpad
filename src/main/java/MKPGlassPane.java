package mkp;

import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.colorchooser.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import wave.multid.Coords2D;
import rpn.message.*;

public class MKPGlassPane extends JPanel {

    public static int HIGHLIGHT_MODE = 0;
    public static int DRAW_MODE = 1;

    public static int MIN_SIZE = 50;

    public static int MKP_SCREEN_CAPTURE_INTERVAL = 5000;

    public static volatile boolean ACTIVE_SCR_CAPTURE = false;

    Boolean doClear_;
    HighLightController highLightController_;
    DrawController drawController_;

    Color markColor_ = Color.RED;

    MKPGlassUI currentController_;	
    int currentMode_;
    MKPGlassFrame parentFrame_;
    MKPGlassPaneMouseAdapter mouseAdapter_;

    MKPControlMenu menu_;

    SerializableBufferedImage backgroundImage_;

  public MKPGlassPane(MKPGlassFrame parentFrame) {

       super();

       drawController_ = new DrawController(this,parentFrame.viewingTransform());
       highLightController_ = new HighLightController(this,parentFrame.viewingTransform());

       currentController_ = highLightController_;

       doClear_ = false;
       parentFrame_ = parentFrame;

       //setOpaque(false);	
       //setBackground(new Color(255, 0, 0, 50));

       mouseAdapter_ = new MKPGlassPaneMouseAdapter(this);

	// no menu for now...
       //menu_ = new MKPControlMenu(this);
  }

  public MKPGlassUI getController() {return currentController_;}
  public void setBackgroundImage(SerializableBufferedImage image) { backgroundImage_ = image;}
  public void setMarkColor(Color color) {markColor_ = color;}

  /*
   * no need to keep switching modes over the network...
   * as we take the uniform OBJMSG approach there will be no difference anymore.
   */
  public synchronized void setMarkMode(int mode) {

	currentMode_ = mode;

	currentController_.uninstall(this);

	if (mode == HIGHLIGHT_MODE) {

		currentController_ = highLightController_;
		//menu_.setHighlightState();
	}

	if (mode == DRAW_MODE) {

		currentController_ = drawController_;
		//menu_.setDrawState();
	}

	currentController_.install(this);
  }

  public int getMarkMode() {return currentMode_;}

 @Override
  public void paintComponent(Graphics g){

    super.paintComponent(g);

    if (doClear_) {

	g.clearRect(0,0,getWidth(),getHeight());

	highLightController_.clear();
	drawController_.clear();

        doClear_ = false;

    }

    else if (parentFrame_.getGlassState() == MKPGlassFrame.INIT_GLASS_STATE) {

    		Graphics2D g2d = (Graphics2D) g;
    		if(mouseAdapter_.point1_!=null && mouseAdapter_.point2_!=null) {

       	   		g2d.setPaint(Color.RED);
       	   		g2d.setStroke(new BasicStroke(3.0f));

			if (mouseAdapter_.point1_.x < mouseAdapter_.point2_.x) {

       	   			g2d.drawRect(mouseAdapter_.point1_.x,
       	                	mouseAdapter_.point1_.y,
       	                	mouseAdapter_.point2_.x - mouseAdapter_.point1_.x,
       	                	mouseAdapter_.point2_.y - mouseAdapter_.point1_.y);
			}

			if (mouseAdapter_.point1_.x > mouseAdapter_.point2_.x) {

       	   			g2d.drawRect(mouseAdapter_.point2_.x,
       	                	mouseAdapter_.point2_.y,
       	                	mouseAdapter_.point1_.x - mouseAdapter_.point2_.x,
       	                	mouseAdapter_.point1_.y - mouseAdapter_.point2_.y);
			}

		}

   } else  { // already initiated ... so we have a mode !

		//g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
		g.setColor(markColor_);
		//if (currentController_ != null)
		//	currentController_.paintComponent(g);

		drawController_.paintComponent(g);
		highLightController_.paintComponent(g);
	}
  }

  public void scrCapture() {

     try {


	Robot robot = new Robot();
	Rectangle captureRect = new Rectangle(parentFrame_.getBounds().x,
						parentFrame_.getBounds().y,
						parentFrame_.getBounds().width,
						parentFrame_.getBounds().height);
 
	backgroundImage_ = new SerializableBufferedImage(robot.createScreenCapture(captureRect));
	ImageIO.write(backgroundImage_.getImage(), "JPG", new File("mkp.jpeg"));
       	if (RPnNetworkStatus.instance().isOnline() && RPnNetworkStatus.instance().isMaster()) {

		System.out.println("INFO : sending background image over the network...");
		RPnNetworkStatus.instance().sendCommand(backgroundImage_);
	}

     } catch (Exception ex) {

	ex.printStackTrace();

     }
	
  }

  public void clear() {
        
	doClear_ = true;

        repaint();

        if (RPnNetworkStatus.instance().isOnline() && RPnNetworkStatus.instance().isMaster())
		RPnNetworkStatus.instance().sendCommand(MKPXMLer.buildClearXML());
  }

}

class MKPGlassPaneMouseAdapter extends MouseAdapter {

	MKPGlassPane pane_;
    	Point point1_;
    	Point point2_;

	public MKPGlassPaneMouseAdapter(MKPGlassPane pane) {

		pane_ = pane;
       		point1_ = new Point();
       		point2_ = new Point();

		pane.addMouseListener(this);
		pane.addMouseMotionListener(this);
	}
  
  	@Override
  	public void mouseClicked(MouseEvent e) {

     		if(e.getClickCount() == 2)
			pane_.getController().clear();
  	}

	@Override
	public void mousePressed(MouseEvent e) {
     
		point1_ = e.getPoint();
	}

  	@Override
  	public void mouseDragged(MouseEvent e) {

    		point2_ = e.getPoint();
    		pane_.repaint();
  	}

	@Override
	public void mouseReleased(MouseEvent e) {

		PointerInfo pointerInfo = MouseInfo.getPointerInfo();

		// these are the TRUE screen location (no relative)
		int x_loc_onscr = pointerInfo.getLocation().x;
		int y_loc_onscr = pointerInfo.getLocation().y;

    		point2_ = e.getPoint();


		if (pane_.parentFrame_.getGlassState() == MKPGlassFrame.INIT_GLASS_STATE) {

			int w = Math.abs(point2_.x - point1_.x);
			int h = Math.abs(point2_.y - point1_.y);

			if (w < MKPGlassPane.MIN_SIZE || h < MKPGlassPane.MIN_SIZE)
				return;


			pane_.parentFrame_.setGlassState(MKPGlassFrame.CONFIGURED_GLASS_STATE);

			int org_x_loc = pane_.parentFrame_.getLocation().x;
			int org_y_loc = pane_.parentFrame_.getLocation().y;


			int loc_x = 0;
			int loc_y = 0;

			if (point1_.x < point2_.x) {
				loc_x = x_loc_onscr - w;
				loc_y = y_loc_onscr - h;
			}

			if (point1_.x > point2_.x) {
				loc_x = x_loc_onscr;
				loc_y = y_loc_onscr;
			}

			// BUG Fix	
			point1_ = point2_ = null;

			pane_.parentFrame_.setSize(w,h);
			pane_.parentFrame_.setLocation(loc_x,loc_y);

			pane_.clear();

        		if (RPnNetworkStatus.instance().isOnline() && RPnNetworkStatus.instance().isMaster()) {

				double[] minValues = new double[2];
				minValues[0] = loc_x; 
				minValues[1] = loc_y;
				double[] maxValues = new double[2];
				maxValues[0] = w + minValues[0];
				maxValues[1] = h + minValues[1];

				Coords2D minCoords = new Coords2D();
				Coords2D maxCoords = new Coords2D();
       				pane_.parentFrame_.viewingTransform().dcInverseTransform(new Coords2D(minValues),minCoords);
       				pane_.parentFrame_.viewingTransform().dcInverseTransform(new Coords2D(maxValues),maxCoords);

				// resize all pupils frames...
				RPnNetworkStatus.instance().sendCommand(MKPXMLer.buildBoundsXML(minCoords,maxCoords));

				try { 

					// a little time before sending the background image
					Thread.sleep(2000);

					// the first time should print... and then depend on the ON|OFF mechanism...
					pane_.scrCapture();

					ScrCaptureRobot scrRobot = new ScrCaptureRobot(pane_);
					scrRobot.start();

				} catch (Exception ex) { ex.printStackTrace();}

			}
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) { }

	@Override
	public void mouseExited(MouseEvent e) { }

	@Override
	public void mouseMoved(MouseEvent e) { }

}

class MKPControlMenu extends JPopupMenu {


	JMenu connect_ = new JMenu("Connect as");
	JMenuItem master_ = new JMenuItem("Master");
	JMenuItem pupil_ = new JMenuItem("Pupil");

	JMenuItem disconnect_ = new JMenuItem("Disconnect");

	JMenuItem highlightMode_ = new JMenuItem("HIGHLIGHT Mode");
	JMenuItem drawMode_ = new JMenuItem("DRAW Mode");

	JMenuItem clear_ = new JMenuItem("Clear");
	JMenuItem colorSettings_ = new JMenuItem("Color Settings...");
	JMenuItem exit_ = new JMenuItem("Exit");

	MKPGlassPane renderer_;

	MKPControlMenu(MKPGlassPane renderer) {


		renderer_ = renderer;

		disconnect_.setEnabled(false);
		highlightMode_.setEnabled(false);
		drawMode_.setEnabled(false);
		clear_.setEnabled(false);
		colorSettings_.setEnabled(false);
		exit_.setEnabled(true);

		connect_.add(master_);
		connect_.add(pupil_);

		add(connect_);
		add(disconnect_);
		add(highlightMode_);
		add(drawMode_);
		add(clear_);
		add(colorSettings_);
		add(exit_);


		master_.addActionListener(
                new java.awt.event.ActionListener() {

                    public void actionPerformed(ActionEvent e) {

			try {
            			String clientID = InetAddress.getLocalHost().getHostName();
				RPnNetworkStatus.instance().connect(clientID,true,true,MKPGlassFrame.ASPECT_RATIO);

				setReadyState();

			 } catch (UnknownHostException ex) {
                    		System.out.println(ex);
        		}
                    }
                });

		pupil_.addActionListener(
                new java.awt.event.ActionListener() {

                    public void actionPerformed(ActionEvent e) {

			try {
            			String clientID = InetAddress.getLocalHost().getHostName();
				RPnNetworkStatus.instance().connect(clientID,false,true,MKPGlassFrame.ASPECT_RATIO);

				setPupilWaitingState();

			 } catch (UnknownHostException ex) {
                    		System.out.println(ex);
        		}
                    }
                });

		disconnect_.addActionListener(
                new java.awt.event.ActionListener() {

                    public void actionPerformed(ActionEvent e) {

			try {
	            		String clientID = InetAddress.getLocalHost().getHostName();
				RPnNetworkStatus.instance().disconnect();

				MKPGlassPane.ACTIVE_SCR_CAPTURE = false;

				setReadyState();

			 } catch (Exception ex) {
                    		System.out.println(ex);
        		}
                    }
                });

		highlightMode_.addActionListener(
                new java.awt.event.ActionListener() {

                    public void actionPerformed(ActionEvent e) {

				renderer_.setMarkMode(MKPGlassPane.HIGHLIGHT_MODE);

                    }
                });

		drawMode_.addActionListener(
                new java.awt.event.ActionListener() {

                    public void actionPerformed(ActionEvent e) {

				renderer_.setMarkMode(MKPGlassPane.DRAW_MODE);
                    }
                });

		clear_.addActionListener(
                new java.awt.event.ActionListener() {

                    public void actionPerformed(ActionEvent e) {

			renderer_.clear();
                    }
                });

		colorSettings_.addActionListener(
                new java.awt.event.ActionListener() {

                    public void actionPerformed(ActionEvent e) {

			Color color = JColorChooser.showDialog(renderer_.parentFrame_,"Pick a Color",Color.GREEN);
			renderer_.setMarkColor(color);
                    }
                });


		exit_.addActionListener(
                new java.awt.event.ActionListener() {

                    public void actionPerformed(ActionEvent e) {

			renderer_.parentFrame_.execCloseCommand();
                    }
                });

	}

	public void setPupilWaitingState() {


		connect_.setEnabled(false);
        	if (RPnNetworkStatus.instance().isOnline())
			disconnect_.setEnabled(true);
		else
			disconnect_.setEnabled(false);

		highlightMode_.setEnabled(true);
		drawMode_.setEnabled(true);

		clear_.setEnabled(false);
		colorSettings_.setEnabled(false);

		renderer_.setMarkMode(MKPGlassPane.HIGHLIGHT_MODE);
	}

	public void setReadyState() {


		connect_.setEnabled(false);
        	if (RPnNetworkStatus.instance().isOnline())
			disconnect_.setEnabled(true);
		else
			disconnect_.setEnabled(false);


		highlightMode_.setEnabled(true);
		drawMode_.setEnabled(true);

		//renderer_.parentFrame_.controlFrame_.pack();
		//renderer_.parentFrame_.controlFrame_.setVisible(true);

		clear_.setEnabled(true);
		colorSettings_.setEnabled(true);

		renderer_.setMarkMode(MKPGlassPane.HIGHLIGHT_MODE);
	}

	public void setDrawState() {

		highlightMode_.setEnabled(true);
		drawMode_.setEnabled(false);
	}

	public void setHighlightState() {

		drawMode_.setEnabled(true);
		highlightMode_.setEnabled(false);
	}
}


class ScrCaptureRobot extends Thread {


	MKPGlassPane pane_;

	public ScrCaptureRobot(MKPGlassPane pane) {

		pane_ = pane;

	}

	public void run() {


		while (true) {

		try {

			if (MKPGlassPane.ACTIVE_SCR_CAPTURE)
				pane_.scrCapture();

			Thread.sleep(MKPGlassPane.MKP_SCREEN_CAPTURE_INTERVAL);

		} catch (Exception ex) {

			ex.printStackTrace();
		}

		}
	}

}
