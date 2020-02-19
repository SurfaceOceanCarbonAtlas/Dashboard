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
 * Assigns WOCE-4 flags to the tail duplicate data points in an overlap between two datasets.
 *
 * @author Karl Smith
 */
public class WoceOverlapTail {

    /**
     * @param args
     *         firstExpo  secondExpo  maxFCO2RecDiff
     *         <p>
     *         Computes the overlaps between the two datasets indicated by the expocodes.
     *         If the overlap is one where the tail of one is a duplicate of the start of
     *         another, then the tail duplicates are all given a WOCE-4 flag on aqueous fCO2.
     */
    public static void main(String[] args) {
        if ( args.length != 3 ) {
            System.err.println();
            System.err.println("Usage:  firstExpo  secondExpo  maxFCO2RecDiff");
            System.err.println();
            System.err.println("Computes the overlaps between the two datasets indicated by the expocodes. ");
            System.err.println("If the overlap is one where the tail of the first dataset is a duplicate ");
            System.err.println("of the start of the second dataset, then the tail duplicates in the first ");
            System.err.println("dataset are all given a WOCE-4 flag on aqueous fCO2.  The value maxFCO2RecDiff ");
            System.err.println("is the maximum allowed difference of fCO2_rec values in duplicates. ");
            System.err.println();
            System.exit(1);
        }
        String firstExpo = null;
        String secondExpo = null;
        try {
            firstExpo = DashboardServerUtils.checkDatasetID(args[0]);
            secondExpo = DashboardServerUtils.checkDatasetID(args[1]);
        } catch ( Exception ex ) {
            System.err.println("Invalid  expocode: " + ex.getMessage());
            System.exit(1);
        }
        double maxFCO2RecDiff = 0.0;
        try {
            maxFCO2RecDiff = Double.parseDouble(args[2]);
        } catch ( Exception ex ) {
            System.err.println("Invalid fCO2RecMax: " + ex.getMessage());
            System.exit(1);
        }

        DashboardConfigStore configStore = null;
        try {
            configStore = DashboardConfigStore.get(false);
        } catch ( Exception ex ) {
            System.err.println("Problems obtaining the default dashboard configuration: " + ex.getMessage());
            System.exit(1);
        }

        boolean successful = true;
        try {
            try {

                DsgNcFileHandler dsgHandler = configStore.getDsgNcFileHandler();
                OverlapChecker oerlapChecker = new OverlapChecker(dsgHandler);

                ArrayList<Overlap> overlapList = oerlapChecker.getOverlaps(firstExpo,
                        Collections.singletonList(secondExpo), null, 0);
                if ( overlapList == null ) {
                    System.out.println(firstExpo + " " + secondExpo + ": no overlaps found");
                    System.err.println(firstExpo + " " + secondExpo + ": no overlaps found");
                }
                else {

                    if ( overlapList.size() != 1 )
                        throw new RuntimeException("unexpected overlap list size of " + overlapList.size());
                    Overlap oerlap = overlapList.get(0);
                    ArrayList<Integer>[] rowNums = oerlap.getRowNums();
                    int numOverlap = rowNums[0].size();
                    if ( rowNums[1].size() != numOverlap )
                        throw new RuntimeException("unexpected different number of data row numbers");

                    // Verify the overlap row numbers are appropriate for performing this automatic WOCE flagging
                    int delta = rowNums[0].get(0) - rowNums[1].get(0);
                    String[] expocodes = oerlap.getDatasetIds();
                    if ( (firstExpo.equals(expocodes[0]) && (delta < 0)) ||
                            (firstExpo.equals(expocodes[1]) && (delta > 0)) )
                        throw new IllegalArgumentException(
                                "negative delta in overlap row numbers between datasets (switch order of datasets?)");
                    for (int k = 1; k < numOverlap; k++) {
                        if ( rowNums[0].get(k) != rowNums[1].get(k) + delta )
                            throw new IllegalArgumentException(
                                    "inconsistent delta in overlap row numbers between datasets");
                    }

                    double[][] firstDataVals = oerlapChecker.getMaskedLonLatTimeSstFco2Vals(firstExpo);
                    double[] firstLons = firstDataVals[0];
                    double[] firstLats = firstDataVals[1];
                    double[] firstTimes = firstDataVals[2];
                    double[] firstFCO2s = firstDataVals[4];

                    double[][] secondDataVals = oerlapChecker.getMaskedLonLatTimeSstFco2Vals(secondExpo);
                    double[] secondFCO2s = secondDataVals[4];

                    // Verify the fCO2_rec values in the overlap are the acceptably close to each other
                    for (int q = 0; q < numOverlap; q++) {
                        int j = rowNums[0].get(q) - 1;
                        double firstFCO2 = firstFCO2s[j];
                        int k = rowNums[1].get(q) - 1;
                        double secondFCO2 = secondFCO2s[k];
                        if ( !DashboardUtils.closeTo(firstFCO2, secondFCO2, 0.0, maxFCO2RecDiff) ) {
                            throw new IllegalArgumentException("overlap has too large of a difference in fCO2_rec - [" +
                                    j + "]: " + firstFCO2 + "  vs " + "[" + k + "]: " + secondFCO2);
                        }
                    }

                    // Add the WOCE flags to the tail of the first dataset
                    ArrayList<DataLocation> locations = new ArrayList<DataLocation>(numOverlap);
                    for (int num : rowNums[0]) {
                        int k = num - 1;
                        DataLocation loc = new DataLocation();
                        loc.setDataDate(new Date(Math.round(firstTimes[k] * 1000.0)));
                        loc.setDataValue(firstFCO2s[k]);
                        loc.setLatitude(firstLats[k]);
                        loc.setLongitude(firstLons[k]);
                        loc.setRowNumber(num);
                        locations.add(loc);
                    }
                    DataQCEvent woceEvent = new DataQCEvent();
                    woceEvent.setDatasetId(firstExpo);
                    woceEvent.setVersion(configStore.getQCVersion());
                    woceEvent.setFlagName(SocatTypes.WOCE_CO2_WATER.getVarName());
                    woceEvent.setFlagValue(DashboardServerUtils.WOCE_BAD);
                    woceEvent.setFlagDate(new Date());
                    woceEvent.setComment("duplicate lon/lat/time/fCO2_rec data points with " +
                            secondExpo + " detected by automation");
                    woceEvent.setUsername(DashboardServerUtils.AUTOMATED_DATA_CHECKER_USERNAME);
                    woceEvent.setRealname(DashboardServerUtils.AUTOMATED_DATA_CHECKER_REALNAME);
                    woceEvent.setVarName(SocatTypes.FCO2_REC.getVarName());
                    woceEvent.setLocations(locations);

                    // Add the WOCE event to the database
                    configStore.getDatabaseRequestHandler().addDataQCEvent(Collections.singletonList(woceEvent));

                    // Assign the WOCE-4 flags in the full-data DSG file, and then regenerate the decimated dataset
                    ArrayList<DataLocation> unidentified = dsgHandler.updateDataQCFlags(woceEvent, false);
                    if ( !unidentified.isEmpty() ) {
                        System.out.println(firstExpo + ": unexpected " +
                                unidentified.size() + " unknown data locations");
                        for (DataLocation loc : unidentified) {
                            System.err.println(firstExpo + ": unexpected unknown data location " + loc.toString());
                        }
                        locations.removeAll(unidentified);
                        successful = false;
                    }

                    if ( !locations.isEmpty() )
                        System.out.println(firstExpo + ": WOCE-4 applied to " +
                                locations.size() + " duplicate data locations");
                    for (DataLocation loc : locations) {
                        System.err.println(firstExpo + ": WOCE-4 applied to duplicate data location " + loc.toString());
                    }

                }

            } catch ( Exception ex ) {
                System.err.println(firstExpo + " " + secondExpo + ": " + ex.getMessage());
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
