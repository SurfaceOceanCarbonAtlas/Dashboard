package gov.noaa.pmel.tmap.las.jdom;

import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.jdom.LASDocument;
import gov.noaa.pmel.tmap.las.util.NameValuePair;
import gov.noaa.pmel.tmap.las.util.Option;
import gov.noaa.pmel.tmap.las.util.Option;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Test;
import junit.framework.TestCase;

public class LASConfigTest extends TestCase {

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("gov.noaa.pmel.tmap.las.jdom.LASConfigTest");
    }

    public LASConfigTest() {
        super();
    }

    /*
     * Test method for 'gov.noaa.pmel.tmap.las.jdom.LASConfig.ServerConfig()'
     */
    @Test
    public final void testServerConfig() {
        // TODO Auto-generated method stub
        
        File file = new File("las.xml");
        
        LASConfig las_config = new LASConfig();
        
        try {
            JDOMUtils.XML2JDOM(file, las_config);
        } catch (IOException e) {
            System.out.println("Could not parse the the las config file "+file+" "+e.toString());
        } catch (JDOMException e) {
            System.out.println("Could not parse the las config file "+file+" "+e.toString());
        }
        
        String v7OperationsFileName = "operationsV7.xml";
        
        File v7OperationsFile = new File(v7OperationsFileName);
        LASDocument v7operationsDoc = new LASDocument();
        try {
            JDOMUtils.XML2JDOM(v7OperationsFile, v7operationsDoc);
        } catch (IOException e) {
            System.out.println("Could not parse the v7 operations file "+v7OperationsFileName+" "+e.toString());
        } catch (JDOMException e) {
            System.out.println("Could not parse the v7 operations file "+v7OperationsFileName+" "+e.toString());
        }
        
        List v7operations = v7operationsDoc.getRootElement().getChildren("operation");
        Element operations = las_config.getRootElement().getChild("operations");
        for (Iterator opIt = v7operations.iterator(); opIt.hasNext();) {
            Element op = (Element) opIt.next();
            operations.addContent((Element)op.clone());
        }
        las_config.mergeProperites();
        
        try {
			las_config.convertToSeven();
		} catch (JDOMException e3) {
			
			e3.printStackTrace();
			fail();
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
			fail();
		} catch (LASException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }
        
      
            try {
                las_config.addIntervalsAndPoints();
            } catch (LASException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
                fail();
            } catch (JDOMException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
                fail();
            }
            
        System.out.println(las_config.toString());
        
        String dsID = "coads_climatology_cdf";
        String varID = "sst";
        String operationsID = "Plot_2D_XY";
        
      
            try {
                System.out.println("Data Access URL (coads; fds false): " + las_config.getDataAccessURL(dsID, varID, false));
                System.out.println("Data Access URL (coads; fds true): " + las_config.getDataAccessURL(dsID, varID, true));
                System.out.println("Data Access URL (NCEP Pac Ocn.; fds true: " + las_config.getDataAccessURL("NOAA-CIRES-CDC-Leetmaa_Ocean_Analysis","CDC_DS17-otemp2027",true));
                System.out.println("Data Access URL (NCEP Pac Ocn.; fds false: " + las_config.getDataAccessURL("NOAA-CIRES-CDC-Leetmaa_Ocean_Analysis","CDC_DS17-otemp2027",false));
            } catch (LASException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (JDOMException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            
           
        
        
        
        
        ArrayList<Option> options=null;
  
            try {
                options = las_config.getOptions(operationsID);
            } catch (JDOMException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        
        int i=1;
        if ( options != null ) {
        	for (Iterator opIt = options.iterator(); opIt.hasNext();) {
        		Option option = (Option) opIt.next();
        		System.out.println("Option "+i);
        		System.out.println("Title:  " + option.getTitle());
        		System.out.println("Type:  " + option.getType());
        		ArrayList<NameValuePair> items = option.getMenu();
        		if (items != null) {
        			for (Iterator itemIt = items.iterator(); itemIt.hasNext();) {
        				NameValuePair item = (NameValuePair) itemIt.next();
        				System.out.println("\tLabel:  "+item.getName()+" Value: "+item.getValue());
        			}
        		}
        		i++;
        	}  
        }
    }
}
