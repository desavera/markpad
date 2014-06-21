package mkp;

import java.awt.*;
import wave.multid.Coords2D;

public class MKPXMLer {



	static public String buildCloseXML() {


                  StringBuilder buffer = new StringBuilder();

                    buffer.append("<COMMAND name=\"CLOSE\">");
                    buffer.append("</COMMAND>");
		 return buffer.toString();
	}

	static public String buildChangeModeXML(int mode) {

                  StringBuilder buffer = new StringBuilder();

                    buffer.append("<COMMAND name=\"MARKMODE\" mode=\"" + mode + '"' + ">");
                    buffer.append("</COMMAND>");
		 return buffer.toString();

	}

	static public String buildClearXML() {

                  StringBuilder buffer = new StringBuilder();

                    buffer.append("<COMMAND name=\"CLEAR\">");
                    buffer.append("</COMMAND>");
		 return buffer.toString();

	}

	static public String buildBoundsXML(Coords2D p1,Coords2D p2) {

		  String minsBuffer = Double.toString(p1.getX()) + ' ' + Double.toString(p1.getY());
		  String maxsBuffer = Double.toString(p2.getX()) + ' ' + Double.toString(p2.getY());

                  StringBuilder buffer = new StringBuilder();

                    buffer.append("<COMMAND name=\"BOUNDS\">");
		    buffer.append("<COMMANDPARAM mins=\"" + minsBuffer.toString() + '"' + " maxs=\"" + maxsBuffer.toString() + '"' + "/>");
                    buffer.append("</COMMAND>");
		 return buffer.toString();
	}

	static public String buildMarkXML(Coords2D p1,Coords2D p2) {


		  String minsBuffer = Double.toString(p1.getX()) + ' ' + Double.toString(p1.getY());
		  String maxsBuffer = Double.toString(p2.getX()) + ' ' + Double.toString(p2.getY());

                  StringBuilder buffer = new StringBuilder();

                    buffer.append("<COMMAND name=\"MARK\">");
		    buffer.append("<COMMANDPARAM mins=\"" + minsBuffer.toString() + '"' + " maxs=\"" + maxsBuffer.toString() + '"' + "/>");
                    buffer.append("</COMMAND>");
		 return buffer.toString();
	}
}
