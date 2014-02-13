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
			"/home/ksmith/workspace/SocatUploadDashboard/test/gov/noaa/pmel/socat/dashboard/test/FerretConfig.xml";

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
    	tool.init(testfile.getFilename());
    	tool.run();
    	assertFalse(tool.hasError());
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
        	tool.init(testfile.getFilename());
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
