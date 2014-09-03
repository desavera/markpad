package mkp;

import java.awt.*;
import wave.multid.Coords2D;
import rpn.message.RPnNetworkStatus;

public class MKPXMLer {



	static public String buildCloseXML() {


                  StringBuilder buffer = new StringBuilder();

                    buffer.append("<COMMAND name=\"CLOSE\" sender_id=\"" + RPnNetworkStatus.instance().clientID() + '"' + ">");
                    buffer.append("</COMMAND>");
		 return buffer.toString();
	}

	static public String buildChangeModeXML(int mode) {

                  StringBuilder buffer = new StringBuilder();

                    buffer.append("<COMMAND name=\"MARKMODE\" mode=\"" + mode + '"' + " sender_id=\"" +  RPnNetworkStatus.instance().clientID() + '"' + ">");
                    buffer.append("</COMMAND>");
		 return buffer.toString();

	}

	static public String buildClearXML() {

                  StringBuilder buffer = new StringBuilder();

                    buffer.append("<COMMAND name=\"CLEAR\"" + " sender_id=\"" +  RPnNetworkStatus.instance().clientID() + '"' + ">");
                    buffer.append("</COMMAND>");
		 return buffer.toString();

	}

	static public String buildBoundsXML(Coords2D p1,Coords2D p2) {

		  String minsBuffer = Double.toString(p1.getX()) + ' ' + Double.toString(p1.getY());
		  String maxsBuffer = Double.toString(p2.getX()) + ' ' + Double.toString(p2.getY());

                  StringBuilder buffer = new StringBuilder();

                    buffer.append("<COMMAND name=\"BOUNDS\"" + " sender_id=\"" +  RPnNetworkStatus.instance().clientID() + '"' + ">");
		    buffer.append("<COMMANDPARAM mins=\"" + minsBuffer.toString() + '"' + " maxs=\"" + maxsBuffer.toString() + '"' + "/>");
                    buffer.append("</COMMAND>");
		 return buffer.toString();
	}

	static public String buildMarkXML(Coords2D p1,Coords2D p2) {


		  String minsBuffer = Double.toString(p1.getX()) + ' ' + Double.toString(p1.getY());
		  String maxsBuffer = Double.toString(p2.getX()) + ' ' + Double.toString(p2.getY());

                  StringBuilder buffer = new StringBuilder();

                    buffer.append("<COMMAND name=\"MARK\"" + " sender_id=\"" +  RPnNetworkStatus.instance().clientID() + '"' + ">");
		    buffer.append("<COMMANDPARAM mins=\"" + minsBuffer.toString() + '"' + " maxs=\"" + maxsBuffer.toString() + '"' + "/>");
                    buffer.append("</COMMAND>");
		 return buffer.toString();
	}
}
