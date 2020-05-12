package gov.noaa.pmel.dashboard.test;

import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.DoubleDashDataType;
import gov.noaa.pmel.dashboard.datatype.SocatTypes;
import gov.noaa.pmel.dashboard.dsg.DsgMetadata;
import gov.noaa.pmel.dashboard.dsg.DsgNcFile;
import gov.noaa.pmel.dashboard.dsg.StdDataArray;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.programs.RegenerateDsgs;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests of {@link RegenerateDsgs}
 *
 * @author Karl Smith
 */
public class RegenerateDsgsTest {

    /**
     * Test method for {@link RegenerateDsgs#regenerateDsgFiles(String, boolean)}.
     * Uses the full-data DSG file in an existing dashboard installation.
     */
    @Test
    public void testRegenerateDsgFiles() throws IllegalArgumentException, IOException {
        final String expocode = "PAT520151021";

        System.setProperty("CATALINA_BASE", System.getenv("HOME") + "/Tomcat");
        System.setProperty("UPLOAD_DASHBOARD_SERVER_NAME", "SocatUploadDashboard");
        // Get the default dashboard configuration
        DashboardConfigStore configStore = null;
        try {
            configStore = DashboardConfigStore.get(false);
        } catch ( Exception ex ) {
            System.err.println("Problems reading the default dashboard configuration file: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        try {
            DsgNcFileHandler dsgHandler = configStore.getDsgNcFileHandler();

            // Read the original data and metadata
            DsgNcFile fullDataDsg = dsgHandler.getDsgNcFile(expocode);
            fullDataDsg.readMetadata(configStore.getKnownMetadataTypes());
            fullDataDsg.readData(configStore.getKnownDataFileTypes());
            DsgMetadata origMeta = fullDataDsg.getMetadata();
            StdDataArray origData = fullDataDsg.getStdDataArray();

            // Regenerate the DSG files
            RegenerateDsgs regenerator = new RegenerateDsgs(configStore);
            assertTrue("regenerateDsgFiles returned false when 'always' set to true",
                    regenerator.regenerateDsgFiles(expocode, true));

            // Re-read the data and metadata
            fullDataDsg.readMetadata(configStore.getKnownMetadataTypes());
            fullDataDsg.readData(configStore.getKnownDataFileTypes());
            DsgMetadata updatedMeta = fullDataDsg.getMetadata();
            StdDataArray updatedData = fullDataDsg.getStdDataArray();

            // Test that nothing has changed
            assertEquals(origMeta, updatedMeta);
            assertNotSame(origMeta, updatedMeta);
            // Check some pieces (to easier see differences) before checking the whole thing
            assertEquals(origData.getNumDataCols(), updatedData.getNumDataCols());
            assertEquals(origData.getNumSamples(), updatedData.getNumSamples());
            for (int j = 0; j < origData.getNumSamples(); j++) {
                for (int k = 0; k < origData.getNumDataCols(); k++) {
                    DashDataType<?> dtype = origData.getDataTypes().get(k);
                    if ( SocatTypes.GVCO2.typeNameEquals(dtype) ) {
                        // GVCO2 varies slightly as more data is assimilated into the model
                        double expval = (Double) origData.getStdVal(j, k);
                        double actval = (Double) updatedData.getStdVal(j, k);
                        assertEquals(dtype.getDisplayName() + "StdVal(" + j + "," + k + ")",
                                expval, actval, 1.0);
                    }
                    else if ( dtype instanceof DoubleDashDataType ) {
                        Object expObj = origData.getStdVal(j, k);
                        Object actObj = updatedData.getStdVal(j, k);
                        if ( (expObj == null) && (actObj != null) ) {
                            fail(dtype.getDisplayName() + "StdVal(" + j + "," + k +
                                    "): expected null, found " + actObj);
                        }
                        else if ( (expObj != null) && (actObj == null) ) {
                            fail(dtype.getDisplayName() + "StdVal(" + j + "," + k +
                                    "): expected " + expObj + " found null");
                        }
                        else if ( (expObj != null) && (actObj != null) ) {
                            double expval = (Double) expObj;
                            double actval = (Double) actObj;
                            assertEquals(dtype.getDisplayName() + "StdVal(" + j + "," + k + ")",
                                    expval, actval, 1.0E-7);
                        }
                    }
                    else {
                        assertEquals(dtype.getDisplayName() + "StdVal(" + j + "," + k + ")",
                                origData.getStdVal(j, k), updatedData.getStdVal(j, k));
                    }
                }
            }
            assertNotSame(origData, updatedData);

        } finally {
            DashboardConfigStore.shutdown();
        }

    }

}
