package mkp;

import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import javax.swing.*;
import java.util.Vector;

import rpn.message.RPnNetworkStatus;
import rpn.message.RPnNetworkDialog;
import wave.multid.Coords2D;
import wave.multid.view.ViewingTransform;

public class HighLightController implements MKPGlassUI {

    Point point1_;
    Point point2_;
    Boolean doClear_;

    private MouseAdapter mouseAdapter_;
    private ViewingTransform vTransform_;
    private MKPGlassPane installedPanel_;

    private boolean active_ = false;

    public HighLightController(MKPGlassPane panel,ViewingTransform vTransform) {

       doClear_ = false;
       point1_ = new Point();
       point2_ = new Point();

       installedPanel_ = panel;
       vTransform_ = vTransform;
  
       mouseAdapter_ = new MouseAdapter() {

  		@Override
  		public void mouseDragged(MouseEvent e) {

		    if (active_) {
    			point2_ = e.getPoint();
    			installedPanel_.repaint();
		    } 	

  		}

  		@Override
  		public void mousePressed(MouseEvent e) {

		    if (active_)
     			point1_ = e.getPoint();


  		}

  		@Override
  		public void mouseReleased(MouseEvent e) {
	
			if (active_ &&
			!SwingUtilities.isRightMouseButton(e) && 
				RPnNetworkStatus.instance().isOnline()) {
        
        			Coords2D dcPoint1 = new Coords2D(point1_.getX(),point1_.getY());
        			Coords2D wcPoint1 = new Coords2D();

				getViewingTransform().dcInverseTransform(dcPoint1,wcPoint1);

        			Coords2D dcPoint2 = new Coords2D(point2_.getX(),point2_.getY());
        			Coords2D wcPoint2 = new Coords2D();

				getViewingTransform().dcInverseTransform(dcPoint2,wcPoint2);

				RPnNetworkStatus.instance().sendCommand(MKPXMLer.buildMarkXML(wcPoint1,wcPoint2));
			}
 		}
 	};
    }

    public ViewingTransform getViewingTransform() { return vTransform_;}

    //
    // Methods
    // 
    public void mark(double[] min,double[] max) {

        Coords2D dcPoint1 = new Coords2D();
        Coords2D wcPoint1 = new Coords2D(min);

        getViewingTransform().viewPlaneTransform(wcPoint1,dcPoint1);

        Coords2D dcPoint2 = new Coords2D();
        Coords2D wcPoint2 = new Coords2D(max);

        getViewingTransform().viewPlaneTransform(wcPoint2,dcPoint2);

	point1_ = new Point((int)Math.round(dcPoint1.getX()),(int)Math.round(dcPoint1.getY()));
	point2_ = new Point((int)Math.round(dcPoint2.getX()),(int)Math.round(dcPoint2.getY()));

	installedPanel_.invalidate();
	installedPanel_.repaint();
    }

    public void install(JPanel panel) {

	panel.addMouseListener(mouseAdapter_);
	panel.addMouseMotionListener(mouseAdapter_);

	// just one single panel for now...
	installedPanel_ = (MKPGlassPane)panel;

	active_ = true;
    }

    public void uninstall(JPanel panel) {

	panel.removeMouseListener(mouseAdapter_);
	panel.removeMouseMotionListener(mouseAdapter_);

	active_ = false;

    }

    @Override
    public void paintComponent(Graphics g){

    	Graphics2D g2d = (Graphics2D) g;
    	if(point1_!=null && point2_!=null){

       	   g2d.setStroke(new BasicStroke(2.0f));

       	   g2d.fillRect(point1_.x,
       	                point1_.y,
       	                point2_.x - point1_.x,
       	                point2_.y - point1_.y);
    	}
   }   

   public void clear() {
        
	point1_ = new Point();
	point2_ = new Point();

	installedPanel_.invalidate();
	installedPanel_.repaint();
  }
}

