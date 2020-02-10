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
     *         firstExpo  secondExpo
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
        String firstExpo = DashboardServerUtils.checkDatasetID(args[0]);
        String secondExpo = DashboardServerUtils.checkDatasetID(args[1]);
        double maxFCO2RecDiff = Double.parseDouble(args[2]);

        DashboardConfigStore configStore = null;
        try {
            configStore = DashboardConfigStore.get(false);
        } catch ( Exception ex ) {
            System.err.println("Problems obtaining the default dashboard configuration: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
        try {

            DsgNcFileHandler dsgHandler = configStore.getDsgNcFileHandler();
            OverlapChecker oerlapChecker = new OverlapChecker(dsgHandler);

            Overlap oerlap = null;
            try {
                ArrayList<Overlap> overlapList = oerlapChecker.getOverlaps(firstExpo,
                        Collections.singletonList(secondExpo), null, 0);
                if ( overlapList == null )
                    throw new IllegalArgumentException("no overlap found");
                if ( overlapList.size() != 1 )
                    throw new RuntimeException("unexpected overlap list size of " + overlapList.size());
                oerlap = overlapList.get(0);
            } catch ( Exception ex ) {
                System.err.println("Problems getting the overlap between " + firstExpo +
                        " and " + secondExpo + ": " + ex.getMessage());
                ex.printStackTrace();
                System.exit(1);
            }

            ArrayList<Integer> firstRowNums = oerlap.getRowNums()[0];
            double[] firstFCO2 = null;
            try {
                ArrayList<Integer> secondRowNums = oerlap.getRowNums()[1];
                // Verify the overlap row numbers are appropriate for performing this automatic WOCE flagging
                if ( firstRowNums.size() != secondRowNums.size() )
                    throw new RuntimeException("unexpected different number of data row numbers");
                int delta = firstRowNums.get(0) - secondRowNums.get(0);
                String[] expocodes = oerlap.getDatasetIds();
                if ( (firstExpo.equals(expocodes[0]) && (delta < 0)) ||
                        (firstExpo.equals(expocodes[1]) && (delta > 0)) )
                    throw new IllegalArgumentException(
                            "delta in row numbers between datasets is negative (switch order of datasets)");
                for (int k = 1; k < firstRowNums.size(); k++) {
                    if ( firstRowNums.get(k) != secondRowNums.get(k) + delta )
                        throw new IllegalArgumentException("inconsistent delta in row numbers between datasets");
                }
                // Verify the fCO2_rec values are the same in the overlap
                firstFCO2 = dsgHandler.readDoubleVarDataValues(firstExpo, SocatTypes.FCO2_REC.getVarName());
                ArrayList<Double> firstOverlapFCO2 = new ArrayList<Double>(firstRowNums.size());
                for (Integer num : firstRowNums) {
                    firstOverlapFCO2.add(firstFCO2[num - 1]);
                }
                double[] secondFCO2 = dsgHandler.readDoubleVarDataValues(secondExpo, SocatTypes.FCO2_REC.getVarName());
                ArrayList<Double> secondOverlapFCO2 = new ArrayList<Double>(secondRowNums.size());
                for (Integer num : secondRowNums) {
                    secondOverlapFCO2.add(secondFCO2[num - 1]);
                }
                for (int k = 0; k < firstOverlapFCO2.size(); k++) {
                    if ( !DashboardUtils.closeTo(firstOverlapFCO2.get(k), secondOverlapFCO2.get(k),
                            0.0, maxFCO2RecDiff) ) {
                        System.err.println(" fCO2_rec: " + firstOverlapFCO2.toString());
                        System.err.println("           " + secondOverlapFCO2.toString());
                        throw new IllegalArgumentException("overlap fCO2_rec " + firstOverlapFCO2.get(k).toString() +
                                " versus " + secondOverlapFCO2.get(k).toString());
                    }
                }
            } catch ( Exception ex ) {
                System.err.println("Invalid overlap between " + firstExpo +
                        " and " + secondExpo + ": " + ex.getMessage());
                ex.printStackTrace();
                System.exit(1);
            }

            // Add the WOCE flags to the tail of the first dataset
            try {
                // Create the WOCE event
                double[][] datavals = dsgHandler.readLonLatTimeDataValues(firstExpo);
                double[] longitudes = datavals[0];
                double[] latitudes = datavals[1];
                double[] times = datavals[2];
                ArrayList<DataLocation> locations = new ArrayList<DataLocation>(firstRowNums.size());
                for (Integer num : firstRowNums) {
                    int k = num - 1;
                    DataLocation loc = new DataLocation();
                    loc.setDataDate(new Date(Math.round(times[k] * 1000.0)));
                    loc.setDataValue(firstFCO2[k]);
                    loc.setLatitude(latitudes[k]);
                    loc.setLongitude(longitudes[k]);
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
                // Assign the WOCE-4 flags in the full-data DSG file, then regenerate the decimated dataset
                ArrayList<DataLocation> unidentified = dsgHandler.updateDataQCFlags(woceEvent, false);
                if ( !unidentified.isEmpty() ) {
                    for (DataLocation loc : unidentified) {
                        System.err.println("unknown data location: " + loc.toString());
                    }
                    throw new RuntimeException("unexpected error");
                }
                // Report WOCE-4 of duplicates
                System.out.println("WOCE-4 assigned to duplicate datapoints in " +
                        firstExpo + ": " + firstRowNums.toString());
            } catch ( Exception ex ) {
                System.err.println("Problems assigning WOCE flags to " + firstExpo + ": " + ex.getMessage());
                ex.printStackTrace();
            }
        } finally {
            DashboardConfigStore.shutdown();
        }

        System.exit(0);
    }

}
