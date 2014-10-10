/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mkp;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import javax.swing.*;
import java.util.logging.*;
import rpn.RPnPhaseSpaceFrame;
import rpn.message.*;

// this is in order to make PathIterator serializable
import salvo.jesus.graph.java.awt.geom.*;
import wave.multid.Coords2D;
import wave.multid.view.ViewingTransform;


public class DrawController implements MKPGlassUI  {


   
    private GeneralPath path_;
    private GeneralPath wpath_;

    private ViewingTransform vTransform_;

    private MouseAdapter mouseAdapter_;
    private MKPGlassPane installedPanel_;

    private boolean myPen_ = true;

    private boolean active_ = false;
  
    public DrawController(MKPGlassPane panel,ViewingTransform vTransform) {

       
        path_ = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        wpath_ = new GeneralPath(GeneralPath.WIND_EVEN_ODD);

	vTransform_ = vTransform;

	installedPanel_ = panel;

     	mouseAdapter_ = new MouseAdapter() {

		public void mousePressed(MouseEvent e){
                    
		    if (active_) {
                        path_.moveTo(new Double(e.getPoint().getX()),new Double(e.getPoint().getY()));

                        Coords2D dcPoint = new Coords2D(new Double(e.getPoint().getX()),new Double(e.getPoint().getY()));
                        Coords2D wcPoint = new Coords2D();

                        getViewingTransform().dcInverseTransform(dcPoint,wcPoint);

                        wpath_.moveTo(wcPoint.getX(),wcPoint.getY());
		   }
      		}

      		public void mouseReleased(MouseEvent event) {

	       	    if (active_ && 
			!SwingUtilities.isRightMouseButton(event) && 
			RPnNetworkStatus.instance().isOnline()) {

                    	PathIterator it = wpath_.getPathIterator(new AffineTransform());

                    	// TODO add coords
        	    	if (RPnNetworkStatus.instance().isOnline()) {

                       	 SerializablePathIterator wPath = new SerializablePathIterator(wpath_.getPathIterator(new AffineTransform()));
                       	 RPnNetworkStatus.instance().sendCommand(wPath);                       

			 //wpath_.reset();
			 //path_.reset();

                    	}
		    }
                }

                public void mouseDragged(MouseEvent e) {

		    if (active_) {

                        path_.lineTo(new Double(e.getPoint().getX()), new Double(e.getPoint().getY()));

                        Coords2D dcPoint = new Coords2D(new Double(e.getPoint().getX()),new Double(e.getPoint().getY()));
                        Coords2D wcPoint = new Coords2D();
                        vTransform_.dcInverseTransform(dcPoint,wcPoint);

                        wpath_.lineTo(wcPoint.getX(),wcPoint.getY());

    			installedPanel_.repaint();
		   }
		}
        };
     }

    //
    // Accessors/Mutators
    //
    public ViewingTransform getViewingTransform() { return vTransform_;}
    public GeneralPath path() {return path_;}

    //
    // Methods
    //
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

    public void updatePath(PathIterator it) {      
        
        // a done Iterator means CLEAR for now...
        if (it.isDone()) {

            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "a done Iterator means CLEAR...");
            clear();

        } else {


            path_.reset();


            // this is the famous WC to DC transform...
            while (!it.isDone()) {


                int domainDim = vTransform_.coordSysTransform().getDomain().getDim();
                double[] wc_coords = new double[domainDim];
                int drawMode = it.currentSegment(wc_coords);
                Coords2D wcPoint = new Coords2D(wc_coords[0],wc_coords[1]);
                Coords2D dcPoint = new Coords2D();

                vTransform_.viewPlaneTransform(wcPoint, dcPoint);

                if (drawMode == PathIterator.SEG_LINETO) {

                    path_.lineTo(dcPoint.getX(), dcPoint.getY());
                    installedPanel_.repaint();
                }

                if (drawMode == PathIterator.SEG_MOVETO)
                    path_.moveTo(dcPoint.getX(), dcPoint.getY());

                it.next();

            }

	    myPen_ = false;

            installedPanel_.invalidate();
            installedPanel_.repaint();
        }
    }

    //
    // Methods
    //
    @Override
    public void paintComponent(Graphics g) {
               
	Stroke stroke = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,new float[] { 3, 1 }, 0);
	Color org_color = g.getColor();

        if (!myPen_) {
                g.setColor(Color.RED);
		myPen_ = true;
	}


    	((Graphics2D)g).setStroke(stroke);
        ((Graphics2D)g).draw(path_);

	g.setColor(org_color);
    }

    public void clear() {
        
        path_.reset();
        wpath_.reset();

	installedPanel_.invalidate();
	installedPanel_.repaint();
   
    }
}
