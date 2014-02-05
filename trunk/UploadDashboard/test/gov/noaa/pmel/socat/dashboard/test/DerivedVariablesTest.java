package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertFalse;
import gov.noaa.pmel.socat.dashboard.ferret.SocatTool;

import org.junit.Test;

public class DerivedVariablesTest {

    @Test
    public void derivedVariablesTest() {
        CruiseDsgNcFileTest testfile = new CruiseDsgNcFileTest();
        try {
            testfile.testCreate();
            if ( testfile.getFilename() != null ) {
                SocatTool tool = new SocatTool();
                tool.init(testfile.getFilename());
                tool.run();
                assertFalse(tool.hasError());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /**
     * @param args
     */
    public static void main(String[] args) {

        CruiseDsgNcFileTest testfile = new CruiseDsgNcFileTest();
        try {
            testfile.testCreate();
            if ( testfile.getFilename() != null ) {
                SocatTool tool = new SocatTool();
                tool.init(testfile.getFilename());
                tool.run();
                if ( tool.hasError() ) {
                    System.err.println("Error: "+tool.getErrorMessage());
                } else {
                    System.out.println("File created.");
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
