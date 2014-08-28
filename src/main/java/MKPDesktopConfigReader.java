/*
 * Instituto de Matematica Pura e Aplicada - IMPA
 * Departamento de Dinamica dos Fluidos
 *
 */
package mkp;

import java.io.*;

public class MKPDesktopConfigReader extends MKPConfigReader{

    private FileInputStream configFileInputStream_;
    private ByteArrayInputStream buff_;

  

    public MKPDesktopConfigReader (String file){

	try {

	    configFileInputStream_= new FileInputStream(file);

	    File configFile = new File (file);

	    byte [] bufArray=new byte[(int)configFile.length()];
            
	    configFileInputStream_.read(bufArray,0,(int)configFile.length());

	    buff_= new ByteArrayInputStream(bufArray);

	}catch (Exception e){

	    System.err.println(e.toString());
	}

    }


    public InputStream  read(){

	return  buff_;


    }
}

