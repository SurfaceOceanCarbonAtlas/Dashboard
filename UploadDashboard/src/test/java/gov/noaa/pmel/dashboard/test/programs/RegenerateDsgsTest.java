/**
 *
 */
package gov.noaa.pmel.dashboard.test.programs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.programs.RegenerateDsgs;
import gov.noaa.pmel.dashboard.server.CruiseDsgNcFile;
import gov.noaa.pmel.dashboard.server.DashDataType;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.SocatCruiseData;
import gov.noaa.pmel.dashboard.server.SocatMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.junit.Test;

/**
 * Tests of method in {@link gov.noaa.pmel.dashboard.programs.RegenerateDsgs}
 *
 * @author Karl Smith
 */
public class RegenerateDsgsTest {

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.programs.RegenerateDsgs#regenerateDsgFiles(java.lang.String, boolean)}.
     * Uses the full-data DSG file in an existing Dashboard installaltion.
     */
    @Test
    public void testRegenerateDsgFiles() throws IllegalArgumentException, IOException {
        final String expocode = "33WA20160713";

        System.setProperty("CATALINA_BASE", System.getenv("HOME"));
        System.setProperty("UPLOAD_DASHBOARD_SERVER_NAME", "SocatUploadDashboard");
        // Get the default dashboard configuration
        DashboardConfigStore configStore = null;
        try {
            configStore = DashboardConfigStore.get(false);
        } catch (Exception ex) {
            System.err.println("Problems reading the default dashboard configuration file: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        DsgNcFileHandler dsgHandler = configStore.getDsgNcFileHandler();

        // Read the original data and metadata
        CruiseDsgNcFile fullDataDsg = dsgHandler.getDsgNcFile(expocode);
        fullDataDsg.readMetadata(configStore.getKnownMetadataTypes());
        fullDataDsg.readData(configStore.getKnownDataFileTypes());
        SocatMetadata origMeta = fullDataDsg.getMetadata();
        ArrayList<SocatCruiseData> origData = fullDataDsg.getDataList();

        // Regenerate the DSG files
        RegenerateDsgs regenerator = new RegenerateDsgs(configStore);
        assertTrue("regenerateDsgFiles returned false when 'always' set to true",
                   regenerator.regenerateDsgFiles(expocode, true));

        // Re-read the data and metadata
        fullDataDsg.readMetadata(configStore.getKnownMetadataTypes());
        fullDataDsg.readData(configStore.getKnownDataFileTypes());
        SocatMetadata updatedMeta = fullDataDsg.getMetadata();
        ArrayList<SocatCruiseData> updatedData = fullDataDsg.getDataList();

        // Test that nothing has changed
        assertEquals(origMeta, updatedMeta);
        assertEquals(origData.size(), updatedData.size());

        for (int k = 0; k < origData.size(); k++) {
            SocatCruiseData origVals = origData.get(k);
            SocatCruiseData updatedVals = updatedData.get(k);

            if ( !origVals.equals(updatedVals) ) {
                // Report all problems for the measurement, not just the first problem

                TreeMap<DashDataType,Integer> origIntVals = origVals.getIntegerVariables();
                TreeMap<DashDataType,Integer> updatedIntVals = updatedVals.getIntegerVariables();
                if ( origIntVals.size() != updatedIntVals.size() )
                    System.err.println("Number of integer values: expected = " +
                                               origIntVals.size() + "; found = " + updatedIntVals.size());
                for (Entry<DashDataType,Integer> entry : origIntVals.entrySet()) {
                    DashDataType key = entry.getKey();
                    Integer original = entry.getValue();
                    Integer updated = updatedIntVals.get(key);
                    if ( !original.equals(updated) )
                        System.err.println("Value of " + key.getVarName() + ": expected = " +
                                                   original + "; found = " + updated);
                }

                TreeMap<DashDataType,Character> origCharVals = origVals.getCharacterVariables();
                TreeMap<DashDataType,Character> updatedCharVals = updatedVals.getCharacterVariables();
                if ( origCharVals.size() != updatedCharVals.size() )
                    System.err.println("Number of character values: expected = " +
                                               origCharVals.size() + "; found = " + updatedCharVals.size());
                for (Entry<DashDataType,Character> entry : origCharVals.entrySet()) {
                    DashDataType key = entry.getKey();
                    Character original = entry.getValue();
                    Character updated = updatedCharVals.get(key);
                    if ( !original.equals(updated) )
                        System.err.println("Value of " + key.getVarName() + ": expected = " +
                                                   original + "; found = " + updated);
                }

                TreeMap<DashDataType,Double> origDoubleVals = origVals.getDoubleVariables();
                TreeMap<DashDataType,Double> updatedDoubleVals = updatedVals.getDoubleVariables();
                if ( origDoubleVals.size() != updatedDoubleVals.size() )
                    System.err.println("Number of character values: expected = " +
                                               origDoubleVals.size() + "; found = " + updatedDoubleVals.size());
                for (Entry<DashDataType,Double> entry : origDoubleVals.entrySet()) {
                    DashDataType key = entry.getKey();
                    Double original = entry.getValue();
                    Double updated = updatedDoubleVals.get(key);
                    if ( !DashboardUtils.closeTo(original, updated,
                                                 DashboardUtils.MAX_RELATIVE_ERROR, DashboardUtils.MAX_ABSOLUTE_ERROR) )
                        System.err.println("Value of " + key.getVarName() + ": expected = " +
                                                   original + "; found = " + updated);
                }
            }

            assertEquals(origVals, updatedVals);
        }

    }

}
