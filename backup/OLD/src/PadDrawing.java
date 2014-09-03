
package mkp;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;

import rpn.message.RPnNetworkStatus;
import rpn.message.RPnNetworkDialog;


public class PadDrawing extends JPanel implements MouseMotionListener, MouseListener{

    Point point1_;
    Point point2_;
    Boolean doClear_;
    MKPGlassFrame parentFrame_;

  public PadDrawing(MKPGlassFrame parentFrame) {

       super();

       doClear_ = false;
       parentFrame_ = parentFrame;	
       point1_ = new Point();
       point2_ = new Point();
       setDoubleBuffered(false);
       addMouseListener(this);
       addMouseMotionListener(this);
    }

 @Override
  public void paintComponent(Graphics g){

    super.paintComponent(g);

    Graphics2D g2d = (Graphics2D) g;
    if(point1_!=null && point2_!=null && !doClear_){

          g2d.setPaint(Color.RED);
          g2d.setStroke(new BasicStroke(2.0f));
          g2d.fillRect(point1_.x,
                       point1_.y,
                       point2_.x - point1_.x,
                       point2_.y - point1_.y);

    }

    if (doClear_) {
	g2d.clearRect(0,0,getWidth(),getHeight());
	doClear_ = false;
    }
  }   

  public void mark(Point point1,Point point2) {


	point1_ = point1;
	point2_ = point2;

	invalidate();
	repaint();

  }

  public void clear() {
        
	point1_ = new Point();
	point2_ = new Point();
	doClear_ = true;

        invalidate();
        repaint();

        if (RPnNetworkStatus.instance().isOnline() && RPnNetworkStatus.instance().isMaster())
		RPnNetworkStatus.instance().sendCommand(MKPXMLer.buildClearXML());
   

  }


  @Override
  public void mouseDragged(MouseEvent e) {

    point2_ = e.getPoint();
    repaint();

  }

   @Override
   public void mouseMoved(MouseEvent e) {

   }

   @Override
   public void mouseClicked(MouseEvent e) {

     if(e.getClickCount() == 2)
	clear();
  }

   @Override
   public void mousePressed(MouseEvent e) {

     point1_ = e.getPoint();

   }

  @Override
  public void mouseReleased(MouseEvent e) {
	

	if (SwingUtilities.isRightMouseButton(e)) {

        	SwingUtilities.invokeLater(new Runnable() {
            		@Override
            		public void run() {
 				RPnNetworkDialog netDialog = RPnNetworkDialog.instance();
        			netDialog.setVisible(true);
			}
		});
	} else

	 // NETWORK !
         //if (RPnNetworkStatus.instance().isOnline() && RPnNetworkStatus.instance().isMaster())
         if (RPnNetworkStatus.instance().isOnline() && RPnNetworkStatus.NO_BUS_CONTROL_)
		RPnNetworkStatus.instance().sendCommand(MKPXMLer.buildMarkXML(parentFrame_.viewingTransform(),point1_,point2_));

	invalidate();
	repaint();

  }

 @Override
 public void mouseEntered(MouseEvent e) {

 }

 @Override
 public void mouseExited(MouseEvent e) {

 }


}
