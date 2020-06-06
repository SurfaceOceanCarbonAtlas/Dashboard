package gov.noaa.pmel.tmap.addxml.test;

import gov.noaa.pmel.tmap.addxml.ADDXMLProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.jdom.Document;
import org.jdom.Element;
import org.junit.Test;

import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dods.DODSNetcdfFile;

public class TestOISST extends TestCase {
	@Test
	public final void testOISST() {
		// This is a test of the hack in the code to identify an climatology from ESRL/PSD.  There should be a modulo=true attribute
		// on the time axis.
		String url = DODSNetcdfFile.canonicalURL("http://ferret.pmel.noaa.gov/thredds/dodsC/data/PMEL/sst.ltm.1971-2000.nc");
		NetcdfDataset ncds;
		try {
			ADDXMLProcessor addxml = new ADDXMLProcessor();
			HashMap<String, String> options= new HashMap<String, String>();
			addxml.setOptions(options);
			ncds = ucar.nc2.dataset.NetcdfDataset.openDataset(url);
			Document coads = addxml.createXMLfromNetcdfDataset(ncds , url);
			Element axes = coads.getRootElement().getChild("axes");
			List<Element> axisList = axes.getChildren();
			assertTrue(axisList.size() > 0);
			for (Iterator axisIt = axisList.iterator(); axisIt.hasNext();) {
				Element axis = (Element) axisIt.next();
				String type = axis.getAttributeValue("type");
				if ( type.equals("t") ) {
					String mod = axis.getAttributeValue("modulo");
					assertTrue(mod != null);
					assertTrue(mod.equals("true"));
				}
			}
		} catch (IOException e) {
			fail("Unable to connect to OPeNDAP server.");
		}
	}
}
