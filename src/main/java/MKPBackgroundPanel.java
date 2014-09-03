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

public class MKPBackgroundPanel extends JPanel {


  SerializableBufferedImage backgroundImage_;


  public void setBackgroundImage(SerializableBufferedImage image) { backgroundImage_ = image;}


 @Override
  public void paintComponent(Graphics g){

    super.paintComponent(g);

     if (backgroundImage_ != null)
	g.drawImage(backgroundImage_.getImage(),(int)Math.round(getBounds().getX()),(int)Math.round(getBounds().getY()),null);

  }
}
