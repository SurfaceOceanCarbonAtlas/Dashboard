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

public class TestMask extends TestCase {
	@Test
	public final void testOISST() {
		// This is a test to see if we skip the mask in the composite...  Datasets are offline.
		String url = DODSNetcdfFile.canonicalURL("http://oos.soest.hawaii.edu/thredds/dodsC/hioos/roms_forec/hiig/HI-ROMS_Forecast_Model_Run_Collection_best.ncd");
		NetcdfDataset ncds;
		try {
			ADDXMLProcessor addxml = new ADDXMLProcessor();
			HashMap<String, String> options= new HashMap<String, String>();
			addxml.setOptions(options);
			ncds = ucar.nc2.dataset.NetcdfDataset.openDataset(url);
			Document hiroms = addxml.createXMLfromNetcdfDataset(ncds , url);
			Element dataset = (Element) hiroms.getRootElement().getChild("datasets").getChildren().get(0);
			List composite = dataset.getChildren("composite");
			// The is one set of u,v velocities and one set of masks.  We should have only found the vector.
			assertTrue(composite.size() == 1);
			Element comp = (Element) composite.get(0);
			// make sure we actually got a composite element.
			assertTrue(comp.getName().equals("composite"));
		} catch (IOException e) {
			fail("Unable to connect to OPeNDAP server.");
		}
	}
}
