/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mkp;

import java.net.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import java.util.logging.*;
import rpn.message.*;
import rpn.*;
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

/**
 *
 * @author mvera
 */
public class MKPControlFrame extends JFrame {


	JButton masterConnButton_ = new JButton("MASTER");
	JButton pupilConnButton_ = new JButton("PUPIL");
	JButton highButton_ = new JButton("HIGHLIGHT");
	JButton drawButton_ = new JButton("DRAW");
	JButton clearButton_ = new JButton("CLEAR");
	JButton onButton_ = new JButton("ON");
	JButton offButton_ = new JButton("OFF");
	JButton quitButton_ = new JButton("QUIT");

	MKPGlassFrame glassFrame_;

    public MKPControlFrame(MKPGlassFrame glassFrame) {    

	// No borders.
	setUndecorated(true);
	setLayout(new FlowLayout());
        getContentPane().add(masterConnButton_);
        getContentPane().add(pupilConnButton_);
        getContentPane().add(highButton_);
        getContentPane().add(drawButton_);
        getContentPane().add(clearButton_);
        getContentPane().add(onButton_);
        getContentPane().add(offButton_);
        getContentPane().add(quitButton_);

	glassFrame_ = glassFrame;


	masterConnButton_.addActionListener(new ActionListener() {         
	public void actionPerformed(ActionEvent e) {

	    try {

            	String clientID = InetAddress.getLocalHost().getHostName();
		RPnNetworkStatus.instance().connect(clientID,true,true,MKPGlassFrame.ASPECT_RATIO);
		masterConnButton_.setEnabled(false);
		pupilConnButton_.setEnabled(false);
		
	    } catch (UnknownHostException ex) {
       		System.out.println(ex);
            }
	}
	}); 

	pupilConnButton_.addActionListener(new ActionListener() {         
	public void actionPerformed(ActionEvent e) {
 	    try {

            	String clientID = InetAddress.getLocalHost().getHostName();
		RPnNetworkStatus.instance().connect(clientID,false,true,MKPGlassFrame.ASPECT_RATIO);
		pupilConnButton_.setEnabled(false);
		masterConnButton_.setEnabled(false);

	    } catch (UnknownHostException ex) {
       		System.out.println(ex);
            }
	}
	}); 

	highButton_.addActionListener(new ActionListener() {         
	public void actionPerformed(ActionEvent e) {

		glassFrame_.pad_.setMarkMode(MKPGlassPane.HIGHLIGHT_MODE);
		highButton_.setEnabled(false);
		drawButton_.setEnabled(true);
	}
	}); 

	drawButton_.addActionListener(new ActionListener() {         
	public void actionPerformed(ActionEvent e) {

		glassFrame_.pad_.setMarkMode(MKPGlassPane.DRAW_MODE);
		drawButton_.setEnabled(false);
		highButton_.setEnabled(true);
	}
	}); 

	clearButton_.addActionListener(new ActionListener() {         
	public void actionPerformed(ActionEvent e) {

		glassFrame_.pad_.clear();
	}
	}); 

	onButton_.addActionListener(new ActionListener() {         
	public void actionPerformed(ActionEvent e) {

		glassFrame_.setVisible(true);
		MKPGlassPane.ACTIVE_SCR_CAPTURE = false;
		onButton_.setEnabled(false);
		offButton_.setEnabled(true);
	}
	}); 

	offButton_.addActionListener(new ActionListener() {         
	public void actionPerformed(ActionEvent e) {

		glassFrame_.setVisible(false);
		glassFrame_.pad_.clear();
		MKPGlassPane.ACTIVE_SCR_CAPTURE = true;
		offButton_.setEnabled(false);
		onButton_.setEnabled(true);
	}
	}); 

	quitButton_.addActionListener(new ActionListener() {         
	public void actionPerformed(ActionEvent e) {

		glassFrame_.execCloseCommand();
	}
	}); 

	masterConnButton_.setEnabled(true);
	pupilConnButton_.setEnabled(true);
	highButton_.setEnabled(true);
	drawButton_.setEnabled(true);
	clearButton_.setEnabled(true);
	onButton_.setEnabled(true);
	offButton_.setEnabled(true);
	quitButton_.setEnabled(true);

	pack();

	setVisible(true);
    }
}
