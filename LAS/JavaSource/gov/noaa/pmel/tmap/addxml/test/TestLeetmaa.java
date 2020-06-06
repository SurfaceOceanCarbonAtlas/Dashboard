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


public class TestLeetmaa extends TestCase {
	@Test
	public final void testLeetmaa() {
		String url = DODSNetcdfFile.canonicalURL("http://iridl.ldeo.columbia.edu/SOURCES/.NOAA/.NCEP/.EMC/.CMB/.Pacific/.monthly/dods");
		String[] name = new String[]{"Vector of zonal velocity and meridional velocity", "Vector of zonal wind stress and meridional wind stress"};
		String[] units = new String[]{"cm/s", "unitless"};
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
				assertTrue(vname.equals(name[index]));
				assertTrue(vunits.equals(units[index]));
				index++;
			}
			Iterator arIt = leetmaa.getDescendants(new ElementFilter("arange"));
			assertTrue(arIt.hasNext());
			while ( arIt.hasNext() ) {
				Element arange = (Element) arIt.next();
				String start = arange.getAttributeValue("start");
				if ( start.length() > 6 ) {
					// It's the time...
					assertTrue(start.equals("1980-01-01"));
				}
			}
		} catch (IOException e) {
			fail("Unable to connect to OPeNDAP server.");
		}
	}
}
