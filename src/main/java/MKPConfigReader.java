/*
 * Instituto de Matematica Pura e Aplicada - IMPA
 * Departamento de Dinamica dos Fluidos
 *
 */
package mkp;

import java.io.*;
import javax.swing.JApplet;
import java.awt.Font;
import org.xml.sax.SAXException;
import rpn.parser.*;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/** This class contains methods to configure the applet and the desktop versions. */
public abstract class MKPConfigReader {

    static public Font MODELPLOT_BUTTON_FONT = new Font("Arial", 1, 8);
    static public String XML_HEADER = "<?xml version=\"1.0\"?>\n<!DOCTYPE markpad>\n";

    /** Constructs a configuration object to the applet or the desktop version */
    public static MKPConfigReader getReader(String file, boolean isApplet, JApplet applet) {

        return new MKPDesktopConfigReader(file);

    }

    /** Initializes the XML parser that reads the configuration file */
    public void init(InputStream configStream) {

        try {

            // initialize the XML parser

            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            configStream.mark(0);

            MKPCommandModule.init(xmlReader, configStream);
            configStream.reset();

        } catch (Throwable any) {


            any.printStackTrace();
        }

    }

    /** Gives a stream with the configuration data */
    public abstract InputStream read();
}
