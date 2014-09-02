/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mkp;

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


	JButton onButton_ = new JButton("ON");
	JButton offButton_ = new JButton("OFF");
	JButton quitButton_ = new JButton("QUIT");

	MKPGlassFrame glassFrame_;

    public MKPControlFrame(MKPGlassFrame glassFrame) {    

	// No borders.
	setUndecorated(true);
	setLayout(new FlowLayout());
        getContentPane().add(onButton_);
        getContentPane().add(offButton_);
        getContentPane().add(quitButton_);

	glassFrame_ = glassFrame;

	setVisible(false);

	onButton_.addActionListener(new ActionListener() {         
	public void actionPerformed(ActionEvent e) {

		glassFrame_.setVisible(true);
		MKPGlassPane.ACTIVE_SCR_CAPTURE = false;
	}
	}); 

	offButton_.addActionListener(new ActionListener() {         
	public void actionPerformed(ActionEvent e) {

		glassFrame_.setVisible(false);
		MKPGlassPane.ACTIVE_SCR_CAPTURE = true;
	}
	}); 

	quitButton_.addActionListener(new ActionListener() {         
	public void actionPerformed(ActionEvent e) {

		glassFrame_.execCloseCommand();

	}
	}); 
    }
}

