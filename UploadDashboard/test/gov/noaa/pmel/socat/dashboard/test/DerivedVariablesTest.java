package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import gov.noaa.pmel.socat.dashboard.ferret.FerretConfig;
import gov.noaa.pmel.socat.dashboard.ferret.SocatTool;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

public class DerivedVariablesTest {

	public static final String CONFIG_FILENAME = 
			"/home/ksmith/content/SocatUploadDashboard/FerretConfig.xml";

	@Test
	public void derivedVariablesTest() throws Exception {
		CruiseDsgNcFileTest testfile = new CruiseDsgNcFileTest();
		testfile.testCreate();
		assertTrue( testfile.getFilename() != null );
		FerretConfig ferret;
		InputStream stream = new FileInputStream(new File(CONFIG_FILENAME));
		try {
			SAXBuilder sb = new SAXBuilder();
			Document jdom = sb.build(stream);
			ferret = new FerretConfig();
			ferret.setRootElement((Element)jdom.getRootElement().clone());
		} finally {
			stream.close();
		}
		SocatTool tool = new SocatTool(ferret);
		String fullDataFilename = testfile.getFilename();
		String expocode = testfile.getExpocode();
		tool.init(fullDataFilename, null, expocode, FerretConfig.Action.COMPUTE);
		tool.run();
		assertFalse(tool.hasError());
		assertTrue((new File(fullDataFilename)).canRead());

		String decDataFilename = fullDataFilename.replace(expocode + ".nc", expocode + "_decimated.nc");
		tool = new SocatTool(ferret);
		tool.init(fullDataFilename, decDataFilename, expocode, FerretConfig.Action.DECIMATE);
		tool.run();
		assertFalse(tool.hasError());
		assertTrue((new File(decDataFilename)).canRead());
	}

	/**
	 * @param args
	 */
	 public static void main(String[] args) {

		 CruiseDsgNcFileTest testfile = new CruiseDsgNcFileTest();
		 try {
			 testfile.testCreate();
			 FerretConfig ferret;
			 InputStream stream = new FileInputStream(new File(CONFIG_FILENAME));
			 try {
				 SAXBuilder sb = new SAXBuilder();
				 Document jdom = sb.build(stream);
				 ferret = new FerretConfig();
				 ferret.setRootElement((Element)jdom.getRootElement().clone());
			 } finally {
				 stream.close();
			 }
			 SocatTool tool = new SocatTool(ferret);
			 tool.init(testfile.getFilename(), null, testfile.getExpocode(), FerretConfig.Action.COMPUTE);
			 tool.run();
			 if ( tool.hasError() ) {
				 System.err.println("Error: "+tool.getErrorMessage());
			 } else {
				 System.out.println("File created.");
			 }
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
	 }

}
