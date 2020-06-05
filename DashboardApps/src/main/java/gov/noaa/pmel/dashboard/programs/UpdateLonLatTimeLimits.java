package gov.noaa.pmel.dashboard.programs;

import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.datatype.SocatTypes;
import gov.noaa.pmel.dashboard.dsg.DsgMetadata;
import gov.noaa.pmel.dashboard.dsg.DsgNcFile;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.metadata.DashboardOmeMetadata;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.TreeSet;

public class UpdateLonLatTimeLimits {

    DsgNcFileHandler dsgHandler;
    MetadataFileHandler metadataHandler;
    KnownDataTypes fileMetadataTypes;

    public UpdateLonLatTimeLimits(DashboardConfigStore configStore) {
        dsgHandler = configStore.getDsgNcFileHandler();
        metadataHandler = configStore.getMetadataFileHandler();
        fileMetadataTypes = configStore.getKnownMetadataTypes();
    }

    /**
     * @param expocode
     *         update the longitude, latitude, and time limits in the OME.xml file for the dataset with this expocode.
     *         The changes are not committed to version control.
     *
     * @throws IOException
     *         if problems reading data or updating the metadata
     * @throws IllegalArgumentException
     *         if the expocode is invalid
     */
    public void updateLimits(String expocode) throws IOException, IllegalArgumentException {
        // Get the longitudes, latitudes, times, and WOCE flags
        DsgNcFile dsgFile = dsgHandler.getDsgNcFile(expocode);
        double[][] lonlattimes = dsgFile.readLonLatTimeDataValues();
        String[] woceFlags = dsgFile.readStringVarDataValues(SocatTypes.WOCE_CO2_WATER.getVarName());
        int numData = woceFlags.length;
        if ( (lonlattimes[0].length != numData) || (lonlattimes[1].length != numData) || (lonlattimes[2].length != numData) )
            throw new IllegalArgumentException("Unexpected different number of data values " +
                    "for WOCE flags, longitudes, latitudes, and times");
        Double[] longitudes = new Double[numData];
        Double[] latitudes = new Double[numData];
        Double[] times = new Double[numData];
        HashSet<Integer> errRows = new HashSet<Integer>();
        for (int k = 0; k < numData; k++) {
            longitudes[k] = lonlattimes[0][k];
            latitudes[k] = lonlattimes[1][k];
            times[k] = lonlattimes[2][k];
            // If not WOCE-2 or WOCE-3, or if lon/lat/time missing, mark this as a row to ignore
            if ( (!(" ".equals(woceFlags[k]) || "2".equals(woceFlags[k]) || "3".equals(woceFlags[k]))) ||
                    DashboardUtils.closeTo(longitudes[k], DashboardUtils.FP_MISSING_VALUE,
                            DashboardUtils.MAX_RELATIVE_ERROR, DashboardUtils.MAX_ABSOLUTE_ERROR) ||
                    DashboardUtils.closeTo(latitudes[k], DashboardUtils.FP_MISSING_VALUE,
                            DashboardUtils.MAX_RELATIVE_ERROR, DashboardUtils.MAX_ABSOLUTE_ERROR) ||
                    DashboardUtils.closeTo(times[k], DashboardUtils.FP_MISSING_VALUE,
                            DashboardUtils.MAX_RELATIVE_ERROR, DashboardUtils.MAX_ABSOLUTE_ERROR) ) {
                errRows.add(k);
            }
        }

        // Get the OME metadata for this dataset
        DashboardMetadata omeInfo = metadataHandler.getMetadataInfo(expocode,
                DashboardServerUtils.OME_FILENAME);
        DashboardOmeMetadata omeMData = metadataHandler.getOmeFromFile(omeInfo);

        // Get the lon/lat/time limits using DsgMetadata
        DsgMetadata dsgMData = omeMData.createDsgMetadata(fileMetadataTypes);
        dsgMData.assignLonLatTimeLimits(longitudes, latitudes, times, errRows);

        // Copy the lon/lat/time limits to OME.xml and save
        omeMData.setWestmostLongitude(dsgMData.getWestmostLongitude());
        omeMData.setEastmostLongitude(dsgMData.getEastmostLongitude());
        omeMData.setSouthmostLatitude(dsgMData.getSouthmostLatitude());
        omeMData.setNorthmostLatitude(dsgMData.getNorthmostLatitude());
        omeMData.setDataBeginTime(dsgMData.getBeginTime());
        omeMData.setDataEndTime(dsgMData.getEndTime());
        metadataHandler.saveOmeToFile(omeMData, null);
    }

    /**
     * @param args
     *         ExpocodesFile - update dashboard status of these cruises
     */
    public static void main(String[] args) {
        if ( args.length != 1 ) {
            System.err.println("Arguments:  ExpocodesFile");
            System.err.println();
            System.err.println("Updates the longitude, latitude, and time limits of all submitted datasets ");
            System.err.println("specified by the expocodes given in ExpocodesFile.  The longitude, latitudes, ");
            System.err.println("times, and latest WOCE flags are obtained from the full-data DSG files for ");
            System.err.println("this dataset.  Changes are NOT committed to version control. ");
            System.err.println("The default dashboard configuration is used for this process. ");
            System.err.println();
            System.exit(1);
        }

        String expocodesFilename = args[0];

        boolean success = true;

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
            // Get the expocode of the cruises to update
            TreeSet<String> allExpocodes = new TreeSet<String>();
            try {
                BufferedReader expoReader = new BufferedReader(new FileReader(expocodesFilename));
                try {
                    String dataline = expoReader.readLine();
                    while ( dataline != null ) {
                        dataline = dataline.trim();
                        if ( !(dataline.isEmpty() || dataline.startsWith("#")) )
                            allExpocodes.add(dataline);
                        dataline = expoReader.readLine();
                    }
                } finally {
                    expoReader.close();
                }
            } catch ( Exception ex ) {
                System.err.println("Error getting expocodes from " + expocodesFilename + ": " + ex.getMessage());
                ex.printStackTrace();
                System.exit(1);
            }

            UpdateLonLatTimeLimits updater = new UpdateLonLatTimeLimits(configStore);

            // update each of these datasets
            for (String expocode : allExpocodes) {
                try {
                    updater.updateLimits(expocode);
                } catch ( Exception ex ) {
                    System.err.println("Error updating the lon/lat/time limits for " + expocode +
                            " : " + ex.getMessage());
                    success = false;
                }
            }
        } finally {
            DashboardConfigStore.shutdown();
        }
        if ( !success )
            System.exit(1);
        System.exit(0);
    }

}
