package gov.noaa.pmel.dashboard.test.actualdatapreserved;

import gov.noaa.pmel.dashboard.dsg.DsgNcFile;
import gov.noaa.pmel.dashboard.dsg.StdDataArray;
import gov.noaa.pmel.dashboard.ferret.FerretConfig;
import gov.noaa.pmel.dashboard.ferret.SocatTool;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.test.datatype.KnownDataTypesTest;
import gov.noaa.pmel.dashboard.test.dsg.DsgNcFileTest;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DerivedVariablesTest {

    @Test
    public void derivedVariablesTest() throws Exception {
        System.setProperty("CATALINA_BASE", System.getenv("HOME") + "/Tomcat");
        System.setProperty("UPLOAD_DASHBOARD_SERVER_NAME", "SocatUploadDashboard");
        DashboardConfigStore confStore = DashboardConfigStore.get(false);
        FerretConfig ferret = confStore.getFerretConfig();
        DsgNcFileTest fileTest = new DsgNcFileTest();
        fileTest.testCreate();
        DsgNcFile dsgFile = fileTest.dsgNcFile;
        String expocode = dsgFile.getMetadata().getDatasetId();
        StdDataArray stdData = dsgFile.getStdDataArray();
        int numData = stdData.getNumSamples();
        Double[] longitudes = stdData.getSampleLongitudes();
        Double[] latitudes = stdData.getSampleLatitudes();
        Double[] depths = stdData.getSampleDepths();
        Double[] times = stdData.calcSampleTimes();

        SocatTool tool = new SocatTool(ferret);
        String fullDataFilename = dsgFile.getPath();
        ArrayList<String> scriptArgs = new ArrayList<String>(1);
        scriptArgs.add(fullDataFilename);
        tool.init(scriptArgs, expocode, FerretConfig.Action.COMPUTE);
        tool.run();
        assertFalse(tool.hasError());

        ArrayList<String> unknownNames = dsgFile.readMetadata(KnownDataTypesTest.TEST_KNOWN_METADATA_FILE_TYPES);
        assertEquals(0, unknownNames.size());
        unknownNames = dsgFile.readData(KnownDataTypesTest.TEST_KNOWN_DATA_FILE_TYPES);
        assertEquals(0, unknownNames.size());
        assertEquals(expocode, dsgFile.getMetadata().getDatasetId());
        StdDataArray calcData = dsgFile.getStdDataArray();
        assertEquals(numData, calcData.getNumSamples());
        Double[] calcLons = calcData.getSampleLongitudes();
        Double[] calcLats = calcData.getSampleLatitudes();
        Double[] calcDepths = calcData.getSampleDepths();
        Double[] calcTimes = calcData.calcSampleTimes();
        for (int j = 0; j < numData; j++) {
            assertEquals(longitudes[j], calcLons[j]);
            assertEquals(latitudes[j], calcLats[j]);
            assertEquals(depths[j], calcDepths[j]);
            assertEquals(times[j], calcTimes[j]);
        }

        String decDataFilename = fullDataFilename.replace(expocode + ".nc", expocode + "_decimated.nc");
        tool = new SocatTool(ferret);
        scriptArgs.add(decDataFilename);
        tool.init(scriptArgs, expocode, FerretConfig.Action.DECIMATE);
        tool.run();
        assertFalse(tool.hasError());

        DsgNcFile decDsgFile = new DsgNcFile(decDataFilename);
        unknownNames = decDsgFile.readMetadata(KnownDataTypesTest.TEST_KNOWN_METADATA_FILE_TYPES);
        assertEquals(0, unknownNames.size());
        unknownNames = decDsgFile.readData(KnownDataTypesTest.TEST_KNOWN_DATA_FILE_TYPES);
        assertEquals(0, unknownNames.size());
        assertEquals(expocode, dsgFile.getMetadata().getDatasetId());
    }

}
