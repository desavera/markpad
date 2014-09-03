package mkp;

import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import javax.swing.*;

import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import wave.multid.Coords2D;
import rpn.message.RPnNetworkStatus;
import rpn.message.RPnNetworkDialog;

public class MKPGlassPane extends JPanel {

    public static int HIGHLIGHT_MODE = 0;
    public static int DRAW_MODE = 1;

    Boolean doClear_;
    HighLightController highLightController_;
    DrawController drawController_;

    MKPGlassUI currentController_;	
    MKPGlassFrame parentFrame_;
    MKPGlassPaneMouseAdapter mouseAdapter_;

    BufferedImage backgroundImage_;

  public MKPGlassPane(MKPGlassFrame parentFrame) {

       super();

       drawController_ = new DrawController(parentFrame.viewingTransform());
       highLightController_ = new HighLightController(parentFrame.viewingTransform());

       doClear_ = false;
       parentFrame_ = parentFrame;

       //setOpaque(false);	
       //setBackground(new Color(255, 0, 0, 50));

       mouseAdapter_ = new MKPGlassPaneMouseAdapter(this);

       addMouseListener(mouseAdapter_);
       addMouseMotionListener(mouseAdapter_);
  }

  public MKPGlassUI getController() {return currentController_;}

  public void setBackgroundImage(BufferedImage image) { backgroundImage_ = image;}

  public void setMode(int mode) {

	if (mode == HIGHLIGHT_MODE) {

		drawController_.uninstall(this);
		highLightController_.install(this);
		currentController_ = highLightController_;
	}

	if (mode == DRAW_MODE) {

		highLightController_.uninstall(this);
		drawController_.install(this);
		currentController_ = drawController_;
	}

	clear();

  }

 @Override
  public void paintComponent(Graphics g){

    super.paintComponent(g);

    if (doClear_) {

	g.clearRect(0,0,getWidth(),getHeight());
	currentController_.clear();
        doClear_ = false;

    }

    else

	if (parentFrame_.getGlassState() == MKPGlassFrame.INIT_GLASS_STATE) {

    		Graphics2D g2d = (Graphics2D) g;
    		if(mouseAdapter_.point1_!=null && mouseAdapter_.point2_!=null){

       	   	g2d.setPaint(Color.RED);
       	   	g2d.setStroke(new BasicStroke(3.0f));

       	   	g2d.drawRect(mouseAdapter_.point1_.x,
       	                mouseAdapter_.point1_.y,
       	                mouseAdapter_.point2_.x - mouseAdapter_.point1_.x,
       	                mouseAdapter_.point2_.y - mouseAdapter_.point1_.y);
    	}


	} else  { // already initiated ... so we have a mode !

	    if (backgroundImage_ == null) {

		try {                
          		backgroundImage_ = ImageIO.read(new File("mkp.jpeg"));
       		} catch (IOException ex) {

            		ex.printStackTrace();
       		}

	    }

		if (backgroundImage_ != null)
			g.drawImage(backgroundImage_,(int)Math.round(getBounds().getX()),(int)Math.round(getBounds().getY()),null);

		Graphics2D g2d = (Graphics2D)g;
		//g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
		//g2d.setColor(getBackground());
		//g2d.fill(getBounds());

		currentController_.paintComponent(g);

	}

  }

  public void scrCapture() {

     try {

	Robot robot = new Robot();
 
	//BufferedImage screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
	BufferedImage screenShot = robot.createScreenCapture(parentFrame_.getBounds());
	ImageIO.write(screenShot, "JPG", new File("mkp.jpeg"));

     } catch (Exception ex) {

	ex.printStackTrace();

     }
	
  }

  public void clear() {
        
	doClear_ = true;

        invalidate();
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
	
    		point2_ = e.getPoint();

		if (SwingUtilities.isRightMouseButton(e)) {

			MKPControlMenu menu = new MKPControlMenu(pane_);
        		menu.show(e.getComponent(), e.getX(), e.getY());

		} else 

		if (pane_.parentFrame_.getGlassState() == MKPGlassFrame.INIT_GLASS_STATE) {

			pane_.parentFrame_.setLayout(null);
	
			pane_.parentFrame_.setGlassState(MKPGlassFrame.CONFIGURED_GLASS_STATE);
			pane_.setSize(point2_.x - point1_.x,
				      point2_.y - point1_.y);
			pane_.setLocation(point1_.x,point1_.y);
			pane_.invalidate();

			pane_.clear();

			pane_.setMode(MKPGlassPane.HIGHLIGHT_MODE);

        		if (RPnNetworkStatus.instance().isOnline() && RPnNetworkStatus.instance().isMaster())
				RPnNetworkStatus.instance().sendCommand(MKPXMLer.buildBoundsXML(new Coords2D(),new Coords2D()));
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

}

class MKPControlMenu extends JPopupMenu {


	JMenuItem connect_ = new JMenuItem("Connect");
	JMenuItem disconnect_ = new JMenuItem("Disconnect");

	JMenuItem uiMode1_ = new JMenuItem("HIGHLIGHT Mode");
	JMenuItem uiMode2_ = new JMenuItem("DRAW Mode");

	JMenuItem clear_ = new JMenuItem("Clear");
	JMenuItem scrCapture_ = new JMenuItem("Screen Capture");
	JMenuItem exit_ = new JMenuItem("Exit");

	MKPGlassPane renderer_;

	MKPControlMenu(MKPGlassPane renderer) {


		renderer_ = renderer;

		disconnect_.setEnabled(false);

		add(connect_);
		add(disconnect_);
		add(uiMode1_);
		add(uiMode2_);
		add(clear_);
		add(scrCapture_);
		add(exit_);


		connect_.addActionListener(
                new java.awt.event.ActionListener() {

                    public void actionPerformed(ActionEvent e) {

			try {

            			String clientID = InetAddress.getLocalHost().getHostName();
				RPnNetworkStatus.instance().connect(clientID,false,true);

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

			 } catch (UnknownHostException ex) {
                    		System.out.println(ex);
        		}
                    }
                });

		uiMode1_.addActionListener(
                new java.awt.event.ActionListener() {

                    public void actionPerformed(ActionEvent e) {

				renderer_.setMode(MKPGlassPane.HIGHLIGHT_MODE);

                    }
                });

		uiMode2_.addActionListener(
                new java.awt.event.ActionListener() {

                    public void actionPerformed(ActionEvent e) {

				renderer_.setMode(MKPGlassPane.DRAW_MODE);

                    }
                });

		clear_.addActionListener(
                new java.awt.event.ActionListener() {

                    public void actionPerformed(ActionEvent e) {

			renderer_.clear();

                    }
                });

		scrCapture_.addActionListener(
                new java.awt.event.ActionListener() {

                    public void actionPerformed(ActionEvent e) {

			renderer_.scrCapture();

                    }
                });


		exit_.addActionListener(
                new java.awt.event.ActionListener() {

                    public void actionPerformed(ActionEvent e) {

			renderer_.parentFrame_.execCloseCommand();

                    }
                });

	}
}
