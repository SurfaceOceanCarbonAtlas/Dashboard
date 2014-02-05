package gov.noaa.pmel.socat.dashboard.test;

import gov.noaa.pmel.socat.dashboard.ferret.SocatTool;

public class DerivedVariablesTest {

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
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
