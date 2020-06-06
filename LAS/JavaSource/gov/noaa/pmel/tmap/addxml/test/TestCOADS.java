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

public class TestCOADS extends TestCase {
	@Test
	public final void testCOADS() {
		String url = DODSNetcdfFile.canonicalURL("http://ferret.pmel.noaa.gov/thredds/dodsC/data/PMEL/coads_climatology.nc");
		NetcdfDataset ncds;
		try {
			ADDXMLProcessor addxml = new ADDXMLProcessor();
			HashMap<String, String> options= new HashMap<String, String>();
			addxml.setOptions(options);
			ncds = ucar.nc2.dataset.NetcdfDataset.openDataset(url);
			// Test from COADS, which tests the new vectors capability
			Document coads = addxml.createXMLfromNetcdfDataset(ncds , url);
			
			
			Iterator<Element> compIt = coads.getDescendants(new ElementFilter("composite"));
			
			assertTrue(compIt.hasNext());
			
			Element composite = compIt.next();
			
			assertTrue(composite != null);
			
			List children = composite.getChildren();
			
			assertTrue(children.size() == 1);
			
			Element variable = (Element) children.get(0);
			
			assertTrue(variable != null);
			
			String name = variable.getAttributeValue("name");
			String units = variable.getAttributeValue("units");
			
			assertTrue(name.equals("Vector of ZONAL WIND and MERIDIONAL WIND"));
			assertTrue(units.equals("M/S"));
			
		} catch (IOException e) {
			fail("Unable to connect to OPeNDAP server.");
		}
		
	}
}
