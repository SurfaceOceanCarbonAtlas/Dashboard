package gov.noaa.pmel.dashboard.test.actualdatapreserved;

import gov.noaa.pmel.dashboard.handlers.DataFileHandler;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.metadata.OmeUtils;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DatasetQCStatus;
import gov.noaa.pmel.socatmetadata.shared.SocatMetadata;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AutoQCTest {

    /**
     * Test of {@link OmeUtils#createSdiMetadataFromCdiacOme(Reader, ArrayList, ArrayList)} and
     * {@link OmeUtils#suggestDatasetQCFlag(SocatMetadata, DashboardDataset)}
     */
    @Test
    public void testSuggestDatasetQCFlag() throws Exception {
        System.setProperty("CATALINA_BASE", System.getenv("HOME"));
        System.setProperty("UPLOAD_DASHBOARD_SERVER_NAME", "SocatUploadDashboard");
        DashboardConfigStore confStore = DashboardConfigStore.get(false);
        DataFileHandler dataHandler = confStore.getDataFileHandler();
        MetadataFileHandler metaHandler = confStore.getMetadataFileHandler();

        DatasetQCStatus.Status expAutoStatus = DatasetQCStatus.Status.ACCEPTED_B;
        String expCommentStart = "(from automated QC) Accuracy of aqueous CO2 less than 2 uatm.  " +
                "Accuracy of temperature measurements 0.05 deg C or less.  " +
                "Accuracy of pressure measurements 2.0 hPa or less " +
                "(no attempt was made to adjust accuracy for differential pressure instruments).  ";
        int expStartLen = expCommentStart.length();
        String expCommentMiddle = " calibration gasses, ";
        int expMiddleLen = expCommentMiddle.length();
        String expCommentEnd = " of which have non-zero concentrations.  " +
                "No attempt was made to find high-quality crossovers.";
        int expEndLen = expCommentEnd.length();
        String expFullComment = expCommentStart + "#" + expCommentMiddle + "#" + expCommentEnd;
        int expFullLen = expFullComment.length();

        for (String expocode : TEST_EXPOCODES) {
            DashboardDataset dset = dataHandler.getDatasetFromInfoFile(expocode);
            File cdiacFile = metaHandler.getMetadataFile(expocode, DashboardUtils.PI_OME_FILENAME);
            FileReader cdiacReader = new FileReader(cdiacFile);
            SocatMetadata mdata = OmeUtils.createSdiMetadataFromCdiacOme(cdiacReader,
                    dset.getUserColNames(), dset.getDataColTypes());
            DatasetQCStatus status = OmeUtils.suggestDatasetQCFlag(mdata, dset);
            DatasetQCStatus.Status auto = status.getAutoSuggested();
            String comment = status.getComments().get(0);
            String info = expocode + ": " + auto.toString() + " " + comment;
            if ( ! expAutoStatus.equals(auto) ) {
                fail(info);
            }
            if ( comment.length() >= expFullLen ) {
                assertEquals(info, expCommentStart, comment.substring(0, expStartLen));
                assertEquals(info, expCommentMiddle, comment.substring(expStartLen + 1, expStartLen + expMiddleLen + 1));
                assertEquals(info, expCommentEnd, comment.substring(expStartLen + expMiddleLen + 2));
            }
            else {
                assertEquals(info, expFullComment, comment);
            }
        }

    }

    /**
     * Expocodes of datasets with a recent PI_OME.xml file
     */
    private static final String[] TEST_EXPOCODES = {
            "33GG20180114",
            "33GG20180126",
            "33GG20180212",
            "33GG20180301",
            "33GG20180624",
            "33GG20180711",
            "33GG20180720",
            "33GG20180806",
            "33GG20180822",
            "33GG20180902",
            "33GG20180911",
            "33GG20181110",
            "33GG20190508",
            "33GG20190529",
            "33GG20190615",
            "33GG20190702",
            "33GG20190719",
            "33HH20180314",
            "33HH20180328",
            "33HH20180414",
            "33HH20180502",
            "33HH20180521",
            "33HH20180523",
            "33HH20180625",
            "33HH20180718",
            "33HH20180801",
            "33HH20180812",
            "33HH20180904",
            "33HH20180925",
            "33HH20181009",
            "33HH20181030",
            "33HH20190308",
            "33HH20190326",
            "33HH20190411",
            "33HH20190501",
            "33HH20190522",
            "33HH20190614",
            "33RO20180216",
            "33RO20180222",
            "33RO20180307",
            "33RO20180423",
            "33RO20180519",
            "33RO20180728",
            "33RO20180910",
            "33RO20181008",
            "33WA20180425",
            "33WA20180430",
            "MLCE20180106",
            "MLCE20180111",
            "MLCE20180117",
            "MLCE20180120",
            "MLCE20180127",
            "MLCE20180203",
            "MLCE20180210",
            "MLCE20180217",
            "MLCE20180224",
            "MLCE20180303",
            "MLCE20180310",
            "MLCE20180317",
            "MLCE20180324",
            "MLCE20180331",
            "MLCE20180407",
            "MLCE20180415",
            "MLCE20180423",
            "MLCE20180504",
            "MLCE20180514",
            "MLCE20180525",
            "MLCE20180602",
            "MLCE20180609",
            "MLCE20180616",
            "MLCE20180623",
            "MLCE20180630",
            "MLCE20180707",
            "MLCE20180714",
            "MLCE20180721",
            "MLCE20180728",
            "MLCE20180804",
            "MLCE20180811",
            "MLCE20180818",
            "MLCE20180825",
            "MLCE20180903",
            "MLCE20180914",
            "MLCE20180924",
            "MLCE20181005",
            "MLCE20181015",
            "MLCE20181023",
            "MLCE20181026",
            "MLCE20181105",
            "MLCE20181116",
            "MLCE20181124",
            "MLCE20181201",
            "MLCE20181208",
            "MLCE20181215",
            "MLCE20181222",
            "MLCE20190105",
            "MLCE20190112",
            "MLCE20190119",
            "MLCE20190126",
            "MLCE20190202",
            "MLCE20190209",
            "MLCE20190216",
            "MLCE20190223",
            "MLCE20190302",
            "MLCE20190309",
            "MLCE20190316",
            "MLCE20190323",
            "MLCE20190330",
            "MLCE20190406",
            "MLCE20190413",
            "MLCE20190420",
            "MLCE20190629",
            "MLCE20190706",
            "MLCE20190713",
            "MLCE20190720",
            "MLCE20190727"
    };

}
