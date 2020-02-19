package gov.noaa.pmel.dashboard.programs;

import gov.noaa.pmel.dashboard.actions.OverlapChecker;
import gov.noaa.pmel.dashboard.datatype.SocatTypes;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.qc.DataLocation;
import gov.noaa.pmel.dashboard.qc.DataQCEvent;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.Overlap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * WOCE out any duplicates of lon/lat/time/fCO2_rec in a dataset
 *
 * @author Karl Smith
 */
public class WoceDuplicates {

    /**
     * @param args
     *         expocode - expoocode of the dataset to search for and WOCE-4
     *         any duplicate lon/lat/time/fCO2_rec duplicates
     */
    public static void main(String[] args) {
        if ( args.length != 2 ) {
            System.err.println();
            System.err.println("Arguments:  expocode  maxFCO2RecDiff");
            System.err.println();
            System.err.println("Search for lon/lat/time/fCO2_rec duplicates within the dataset ");
            System.err.println("with the given expocode.  Any data points with a WOCE-4 flag or ");
            System.err.println("with fCO2_rec missing are ignored.  Any duplicates found are ");
            System.err.println("assigned a WOCE-4 flag by the automated data checker with an ");
            System.err.println("appropriate message.  If duplicates are found, the database and ");
            System.err.println("full-data DSG file are updated and the decimated-data DSG file is ");
            System.err.println("regenerated.  ERDDAP is NOT notified of any changes since this ");
            System.err.println("program may be run repeatedly for a number of different expocodes. ");
            System.err.println("The value maxFCO2RecDiff is the maximum allowed difference of ");
            System.err.println("fCO2_rec values in duplicates. ");
            System.err.println();
            System.exit(1);
        }
        String expocode = null;
        try {
            expocode = DashboardServerUtils.checkDatasetID(args[0]);
        } catch ( Exception ex ) {
            System.err.println("Invalid expocode: " + ex.getMessage());
            System.exit(1);
        }
        double maxFCO2RecDiff = 0.0;
        try {
            maxFCO2RecDiff = Double.parseDouble(args[1]);
        } catch ( Exception ex ) {
            System.err.println("Invalid maxFCO2RecDiff: " + ex.getMessage());
            System.exit(1);
        }

        // Get the default dashboard configuration
        DashboardConfigStore configStore = null;
        try {
            configStore = DashboardConfigStore.get(false);
        } catch ( Exception ex ) {
            System.err.println("Problems reading the default dashboard configuration file: " + ex.getMessage());
            System.exit(1);
        }

        boolean successful = true;
        try {

            try {
                // Find all lon/lat/time overlaps within this dataset, ignoring
                // data points already WOCE-4 and those without fCO2_rec values
                DsgNcFileHandler dsgFileHandler = configStore.getDsgNcFileHandler();
                OverlapChecker oerlapChecker = new OverlapChecker(dsgFileHandler);
                ArrayList<Overlap> oerlapList = oerlapChecker.getOverlaps(expocode,
                        Collections.singletonList(expocode), null, 0);

                // If no overlaps found, nothing to do
                if ( oerlapList.isEmpty() ) {
                    System.out.println(expocode + ": no overlaps found");
                    System.err.println(expocode + ": no overlaps found");
                }
                else {

                    // Since checked against only one expocode, there should never be more than one Overlap object
                    if ( oerlapList.size() != 1 )
                        throw new RuntimeException("unexpected error - more than one Overlap object returned " +
                                "from OverlapChecker.getOverlaps when comparing a dataset with only itself");
                    Overlap oerlap = oerlapList.get(0);

                    // Get the version number from the DSG file
                    String versionStatus = dsgFileHandler.getDatasetQCFlagAndVersionStatus(expocode)[1];
                    // Remove the final 'U' or 'N' off the version-status
                    String version = versionStatus.substring(0, versionStatus.length() - 1);

                    // Get the lon/lat/time/fCO2_rec data values from the DSG file;
                    // any WOCE-4 data points has reset fCO2_rec to FP_MISSING_VALUE
                    double[][] dataVals = oerlapChecker.getMaskedLonLatTimeSstFco2Vals(expocode);
                    double[] lons = dataVals[0];
                    double[] lats = dataVals[1];
                    double[] times = dataVals[2];
                    double[] fco2s = dataVals[4];

                    ArrayList<Integer>[] rowNums = oerlap.getRowNums();
                    int numPts = rowNums[0].size();
                    if ( (numPts < 1) || (rowNums[1].size() != numPts) )
                        throw new RuntimeException("unexpected error - invalid number of overlapping data points");
                    ArrayList<DataLocation> dupDatInf = new ArrayList<DataLocation>(numPts);
                    int tooBig = 0;
                    for (int q = 0; q < numPts; q++) {
                        int j = rowNums[0].get(q) - 1;
                        int k = rowNums[1].get(q) - 1;
                        if ( DashboardUtils.closeTo(fco2s[j], fco2s[k], 0.0, maxFCO2RecDiff) ) {
                            // WOCE-4 the latter of the two; if there are multiple duplications, only the first will be kept
                            if ( k < j )
                                k = j;
                            DataLocation datinf = new DataLocation();
                            datinf.setRowNumber(k + 1);
                            datinf.setLongitude(lons[k]);
                            datinf.setLatitude(lats[k]);
                            datinf.setDataDate(new Date(Math.round(1000.0 * times[k])));
                            datinf.setDataValue(fco2s[k]);
                            dupDatInf.add(datinf);
                        }
                        else {
                            System.err.println(expocode + ": overlap has too large of a difference in fCO2_rec - [" +
                                    j + "]: " + fco2s[j] + " vs [" + k + "]: " + fco2s[k]);
                            tooBig++;
                        }
                    }
                    if ( tooBig > 0 )
                        System.out.println(expocode + ": " + tooBig +
                                " overlaps have too large of a difference in fCO2_rec");

                    // Assign the WOCE-4 flag for duplicates
                    DataQCEvent woceEvent = new DataQCEvent();
                    woceEvent.setDatasetId(expocode);
                    woceEvent.setVersion(version);
                    woceEvent.setFlagName(SocatTypes.WOCE_CO2_WATER.getVarName());
                    woceEvent.setFlagValue(DashboardServerUtils.WOCE_BAD);
                    woceEvent.setFlagDate(new Date());
                    woceEvent.setComment("duplicate lon/lat/time/fCO2_rec data points detected by automation");
                    woceEvent.setUsername(DashboardServerUtils.AUTOMATED_DATA_CHECKER_USERNAME);
                    woceEvent.setRealname(DashboardServerUtils.AUTOMATED_DATA_CHECKER_REALNAME);
                    woceEvent.setVarName(SocatTypes.FCO2_REC.getVarName());
                    woceEvent.setLocations(dupDatInf);

                    // Add the WOCE event to the database
                    configStore.getDatabaseRequestHandler().addDataQCEvent(Collections.singletonList(woceEvent));

                    // Assign the WOCE-4 flags in the full-data DSG file, and then regenerate the decimated dataset
                    ArrayList<DataLocation> unidentified = dsgFileHandler.updateDataQCFlags(woceEvent, false);
                    if ( !unidentified.isEmpty() ) {
                        System.out.println(expocode + ": unexpected " +
                                unidentified.size() + " unknown data locations");
                        for (DataLocation loc : unidentified) {
                            System.err.println(expocode + ": unexpected unknown data location " + loc.toString());
                        }
                        dupDatInf.removeAll(unidentified);
                        successful = false;
                    }

                    if ( !dupDatInf.isEmpty() )
                        System.out.println(expocode + ": WOCE-4 applied to " +
                                dupDatInf.size() + " duplicate data locations");
                    for (DataLocation loc : dupDatInf) {
                        System.err.println(expocode + ": WOCE-4 applied to duplicate data location " + loc.toString());
                    }
                }

            } catch ( Exception ex ) {
                System.out.println(expocode + ": " + ex.getMessage());
                ex.printStackTrace();
                successful = false;
            }

        } finally {
            DashboardConfigStore.shutdown();
        }

        if ( !successful )
            System.exit(1);
        System.exit(0);
    }

}

