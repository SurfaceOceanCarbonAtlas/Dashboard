package gov.noaa.pmel.tmap.addxml.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		Class[] testClasses = { TestCOADS.class, 
				                TestGFDL.class, 
				                TestLeetmaa.class, 
				                // TestNGDC.class, the data set has disappeared
				                TestOISST.class,
				                TestNYHOPS.class,
				                // Data set off line ...  TestMask.class
				                };
		TestSuite suite= new TestSuite(testClasses);
		return suite;
	}

}
