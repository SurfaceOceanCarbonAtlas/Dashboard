/**
 *
 */
package gov.noaa.pmel.dashboard.test.actualdata;

import gov.noaa.pmel.dashboard.handlers.DataFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetData;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Test of {@link DataFileHandler}.
 * Uses an existing UploadDashboard installation
 *
 * @author Karl Smith
 */
public class DataFileHandlerTest {

    // Only uses the Dashboard configuration files; tests data given below

    /**
     * Test method for {@link DataFileHandler#assignDatasetDataFromInput(DashboardDatasetData,
     * BufferedReader, String, String, int, int)}
     */
    @Test
    public void testAssignDatasetDataFromInput() throws IOException {
        System.setProperty("CATALINA_BASE", System.getenv("HOME"));
        System.setProperty("UPLOAD_DASHBOARD_SERVER_NAME", "SocatUploadDashboard");
        DataFileHandler dataHandler = DashboardConfigStore.get(false).getDataFileHandler();
        BufferedReader cruiseReader = new BufferedReader(new StringReader(CSV_DATA));
        DashboardDatasetData cruiseData = dataHandler.assignDatasetDataFromInput(null, cruiseReader,
                DashboardUtils.COMMA_FORMAT_TAG, DATASET_OWNER, 0, -1);

        assertEquals(META_PREAMBLE.size(), cruiseData.getPreamble().size());
        for (int k = 0; k < META_PREAMBLE.size(); k++) {
            assertEquals(META_PREAMBLE.get(k), cruiseData.getPreamble().get(k));
        }
        assertEquals(HEADERS.size(), cruiseData.getUserColNames().size());
        for (int k = 0; k < HEADERS.size(); k++) {
            assertEquals(HEADERS.get(k), cruiseData.getUserColNames().get(k));
        }
        assertEquals(DATA_ARRAY.size(), cruiseData.getDataValues().size());
        for (int k = 0; k < DATA_ARRAY.size(); k++) {
            ArrayList<String> expected = DATA_ARRAY.get(k);
            ArrayList<String> found = cruiseData.getDataValues().get(k);
            assertEquals(expected.size(), found.size());
            for (int j = 0; j < expected.size(); j++) {
                assertEquals(expected.get(j), found.get(j));
            }
        }
    }

    private static final String DATASET_OWNER = "Kevin Sullivan";
    private static final String UPLOAD_FILENAME = "Atlantis April 2012.csv";
    private static final String UPLOAD_TIMESTAMP = "2012-06-25 12:35+4";
    private static final String CSV_DATA = "Expocode: 00KS20120419 , , , , , , , , , , , , , , , , , , ,\n" +
            "Ship: Atlantis         , , , , , , , , , , , , , , , , , , ,\n" +
            "\"PI: Wanninkhof, R.\"     , , , , , , , , , , , , , , , , , , ,\n" +
            ", , , , , , , , , , , , , , , , , , ,\n" +

            "Group_Ship,Cruise ID,JD_GMT,DATE_UTC__ddmmyyyy,TIME_UTC_hh:mm:ss,LAT_dec_degree,LONG_dec_degree," +
            "xCO2_EQU_ppm,xCO2_ATM_ppm,xCO2_ATM_interpolated_ppm,PRES_EQU_hPa,PRES_ATM@SSP_hPa,TEMP_EQU_C,SST_C," +
            "SAL_permil,fCO2_SW@SST_uatm,fCO2_ATM_interpolated_uatm,dfCO2_uatm,WOCE_QC_FLAG,QC_SUBFLAG\n" +

            "AOML_Atlantis,20-01B,110.79219,19042012,19:00:45,12.638,-59.239," +
            "394.227,-999,395.34,1009.3,1012.01,27.55,27.4844," +
            "35.17,376.43,379.65,-3.22,2,\n" +

            "AOML_Atlantis,20-01B,110.79391,19042012,19:03:14,12.633,-59.233," +
            "393.483,-999,395.33,1009.6,1012.31,27.57,27.4945," +
            "35.17,375.66,379.74,-4.08,2,\n" +

            ", , , , , , , , , , , , , , , , , , ,\n" +

            "AOML_Atlantis,20-01B,110.79564,19042012,19:05:43,12.628,-59.228," +
            "393.249,-999,395.33,1009.1,1011.91,27.57,27.5008," +
            "35.17,375.35,379.58,-4.24,2,\n" +

            "AOML_Atlantis,20-01B,110.79736,19042012,19:08:12,12.622,-59.222," +
            "393.455,-999,395.32,1009.1,1012.21,27.57,27.4981," +
            "35.13,375.5,379.69,-4.19,2,\n" +

            " , , , , , , , , , , , , , , , , , , , \n";

    private static final ArrayList<String> META_PREAMBLE = new ArrayList<String>(Arrays.asList(
            "Expocode: 00KS20120419",
            "Ship: Atlantis",
            "PI: Wanninkhof, R.",
            ""));

    private static final ArrayList<String> HEADERS = new ArrayList<String>(Arrays.asList(
            "Group_Ship", "Cruise ID", "JD_GMT", "DATE_UTC__ddmmyyyy", "TIME_UTC_hh:mm:ss", "LAT_dec_degree",
            "LONG_dec_degree", "xCO2_EQU_ppm", "xCO2_ATM_ppm", "xCO2_ATM_interpolated_ppm", "PRES_EQU_hPa",
            "PRES_ATM@SSP_hPa", "TEMP_EQU_C", "SST_C", "SAL_permil", "fCO2_SW@SST_uatm", "fCO2_ATM_interpolated_uatm",
            "dfCO2_uatm", "WOCE_QC_FLAG", "QC_SUBFLAG"
    ));

    private static final ArrayList<ArrayList<String>> DATA_ARRAY = new ArrayList<ArrayList<String>>(Arrays.asList(
            new ArrayList<String>(Arrays.asList(
                    "AOML_Atlantis", "20-01B", "110.79219", "19042012", "19:00:45", "12.638", "-59.239",
                    "394.227", "-999", "395.34", "1009.3", "1012.01", "27.55", "27.4844",
                    "35.17", "376.43", "379.65", "-3.22", "2", ""
            )),
            new ArrayList<String>(Arrays.asList(
                    "AOML_Atlantis", "20-01B", "110.79391", "19042012", "19:03:14", "12.633", "-59.233",
                    "393.483", "-999", "395.33", "1009.6", "1012.31", "27.57", "27.4945",
                    "35.17", "375.66", "379.74", "-4.08", "2", ""
            )),
            new ArrayList<String>(Arrays.asList(
                    "AOML_Atlantis", "20-01B", "110.79564", "19042012", "19:05:43", "12.628", "-59.228",
                    "393.249", "-999", "395.33", "1009.1", "1011.91", "27.57", "27.5008",
                    "35.17", "375.35", "379.58", "-4.24", "2", ""
            )),
            new ArrayList<String>(Arrays.asList(
                    "AOML_Atlantis", "20-01B", "110.79736", "19042012", "19:08:12", "12.622", "-59.222",
                    "393.455", "-999", "395.32", "1009.1", "1012.21", "27.57", "27.4981",
                    "35.13", "375.5", "379.69", "-4.19", "2", ""
            ))
    ));

}
