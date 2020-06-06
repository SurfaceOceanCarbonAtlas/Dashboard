package gov.noaa.pmel.tmap.addxml.test;

import gov.noaa.pmel.tmap.addxml.ADDXMLProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.junit.Test;

import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dods.DODSNetcdfFile;


public class TestGFDL extends TestCase {
	 @Test
	    public final void testGFDL() {
	    	// Test to make sure variables with names that start with u or v but aren't vectors don't get included.  There should only be one composite.
	    	String url = DODSNetcdfFile.canonicalURL("http://data1.gfdl.noaa.gov:8080/thredds/dodsC/CM2.1U_CDFef_v1.0_r1ocean");
			String[] name = new String[]{"Vector of "};
			String[] units = new String[]{"m s-1"};
			NetcdfDataset ncds;
			try {
				ADDXMLProcessor addxml = new ADDXMLProcessor();
				HashMap<String, String> options= new HashMap<String, String>();
				addxml.setOptions(options);
				ncds = ucar.nc2.dataset.NetcdfDataset.openDataset(url);
				// Test from COADS, which tests the new vectors capability
				Document leetmaa = addxml.createXMLfromNetcdfDataset(ncds , url);
				Iterator compIt = leetmaa.getRootElement().getDescendants(new ElementFilter("composite"));
				assertTrue(compIt.hasNext());
				int index = 0;
				while ( compIt.hasNext() ) {
					Element composite = (Element) compIt.next();
					List children = composite.getChildren();				
					assertTrue(children.size() == 1);
					Element variable = (Element) children.get(0);
					assertTrue(variable != null);
					String vname = variable.getAttributeValue("name");
					String vunits = variable.getAttributeValue("units");
					assertTrue(vname.contains(name[index]));
					assertTrue(vunits.equals(units[index]));
					index++;
				}
				assertTrue(index == 1);
			} catch (IOException e) {
				fail("Unable to connect to OPeNDAP server.");
			}
	    }
}
