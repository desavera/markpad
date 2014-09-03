package mkp;

import java.awt.Point;
import wave.multid.view.ViewingTransform;
import wave.multid.*;

public class MKPXMLer {



	static public String buildCloseXML() {


                  StringBuilder buffer = new StringBuilder();

                    buffer.append("<COMMAND name=\"CLOSE\">");
                    buffer.append("</COMMAND>");
		 return buffer.toString();
	}

	static public String buildClearXML() {

                  StringBuilder buffer = new StringBuilder();

                    buffer.append("<COMMAND name=\"CLEAR\">");
                    buffer.append("</COMMAND>");
		 return buffer.toString();

	}

	static public String buildMarkXML(ViewingTransform transform,Point p1,Point p2) {




		  Coords2D dCoords1 = new Coords2D(p1.getX(),p1.getY());
		  Coords2D dCoords2 = new Coords2D(p2.getX(),p2.getY());
		  CoordsArray wCoords1 = new CoordsArray(new Space(" ",2));
		  CoordsArray wCoords2 = new CoordsArray(new Space(" ",2));

		  transform.dcInverseTransform(dCoords1,wCoords1);
		  transform.dcInverseTransform(dCoords2,wCoords2);


		  //System.out.println("W COORDS 1 = " + wCoords1.toString());
		  //System.out.println("W COORDS 2 = " + wCoords2.toString());

		  String minsBuffer = Double.toString(wCoords1.getCoords()[0]) + ' ' + Double.toString(wCoords1.getCoords()[1]);
		  String maxsBuffer = Double.toString(wCoords2.getCoords()[0]) + ' ' + Double.toString(wCoords2.getCoords()[1]);
                  StringBuilder buffer = new StringBuilder();

                    buffer.append("<COMMAND name=\"MARK\">");
		    buffer.append("<COMMANDPARAM mins=\"" + minsBuffer.toString() + '"' + " maxs=\"" + maxsBuffer.toString() + '"' + "/>");
                    buffer.append("</COMMAND>");
		 return buffer.toString();
	}
}
