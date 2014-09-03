/*
 * Instituto de Matematica Pura e Aplicada - IMPA
 * Departamento de Dinamica dos Fluidos
 *
 */
package mkp;

import java.awt.Color;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;

import wave.util.RealVector;

import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.ContentHandler;
import org.xml.sax.XMLReader;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import wave.multid.CoordsArray;

import rpn.message.RPnNetworkStatus;

public class MKPCommandModule {

    public static String SESSION_ID_ = "8888";

    static public class MKPCommandParser implements ContentHandler {

        private String currentElement_;
        private String currentCommand_;
        private String senderID_;
        private String modeParam_;
        private String mins_;
	private String maxs_;

        public MKPCommandParser() {

            mins_ = new String("NULL");
            maxs_ = new String("NULL");

	    currentElement_ = new String("NULL");
	    currentCommand_ = new String("NULL");
	    senderID_ = new String("NULL");

        }

        @Override
        public void endDocument() {
        }

        @Override
        public void startElement(String uri, String name, String qName, Attributes att) throws
                SAXException {

            currentElement_ = name;


            if (currentElement_.equals("MKPSESSION")) {

                SESSION_ID_ = att.getValue("id");

            } else

            if (currentElement_.equalsIgnoreCase("COMMAND")) {

                currentCommand_ = att.getValue("name");
		senderID_ = att.getValue("sender_id");

		if (currentCommand_.equalsIgnoreCase("MARKMODE"))
			modeParam_ = att.getValue("mode");

                Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "Current command is :" + currentCommand_);

	   } else

           if (currentElement_.equals("COMMANDPARAM")) {

		mins_ = att.getValue("mins").trim();
                maxs_ = att.getValue("maxs").trim();

	   }
        }

        @Override
        public void endElement(String uri, String name, String qName) throws SAXException {


            if (name.equals("COMMAND") && (senderID_.compareTo(RPnNetworkStatus.instance().clientID()) != 0)) {

                if (currentCommand_.equalsIgnoreCase("MARK")) {

                   MKPGlassFrame.instance().execMarkCommand(mins_,maxs_);

                } else

                if (currentCommand_.equalsIgnoreCase("CLEAR")) {

                   MKPGlassFrame.instance().execClearCommand();

		} else

                if (currentCommand_.equalsIgnoreCase("BOUNDS") && 
        		 (RPnNetworkStatus.instance().isOnline() && !RPnNetworkStatus.instance().isMaster())) {
                   MKPGlassFrame.instance().execSetPadBoundsCommand(mins_,maxs_);

		} else

                if (currentCommand_.equalsIgnoreCase("CLOSE")) {

                   MKPGlassFrame.instance().execCloseCommand();
		}

                if (currentCommand_.equalsIgnoreCase("MARKMODE")) {

                   MKPGlassFrame.instance().execChangeMarkModeCommand(Integer.parseInt(modeParam_));
		}
            }
        }

        public void characters(char[] c, int i, int j) {
        }

        public void setDocumentLocator(Locator locator) {
        }

        public void startDocument() throws SAXException {
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException {
        }

        public void endPrefixMapping(String prefix) throws SAXException {
        }

        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        }

        public void processingInstruction(String target, String data) throws SAXException {
        }

        public void skippedEntity(String name) throws SAXException {
        }

    }

    //
    // Initializers
    //        
    /**
     * Initializes the XML parser to reload a previous session.
     */
    public static void init(XMLReader parser, InputStream inputStream) {

        try {

            parser.setContentHandler(new MKPCommandParser());
            System.out.println("Command Module parsing started...");
            parser.parse(new InputSource((inputStream)));
            System.out.println("Command Module parsing finished sucessfully !");

        } catch (Exception saxex) {

            if (saxex instanceof org.xml.sax.SAXParseException) {
                System.out.println("Line: "
                        + ((org.xml.sax.SAXParseException) saxex).getLineNumber());
                System.out.println("Column: "
                        + ((org.xml.sax.SAXParseException) saxex).getColumnNumber());
            }

            saxex.printStackTrace();
        }
    }
}
