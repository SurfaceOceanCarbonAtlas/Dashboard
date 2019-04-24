package gov.noaa.pmel.dashboard.programs;

import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.datatype.SocatTypes;
import gov.noaa.pmel.dashboard.dsg.DsgNcFile;
import gov.noaa.pmel.dashboard.dsg.StdDataArray;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Generate the file of data for creating the gridded datasets
 *
 * @author Karl Smith
 */
public class GenerateDataForGridding {

    private DsgNcFileHandler dsgFileHandler;
    private KnownDataTypes knownDataFileTypes;

    public GenerateDataForGridding(DashboardConfigStore configStore) {
        dsgFileHandler = configStore.getDsgNcFileHandler();
        knownDataFileTypes = configStore.getKnownDataFileTypes();
    }

    /**
     * Tab-separated data column names for the data printed by {@link #generateDataFileForGrids(TreeSet, File)}
     */
    private static final String GENERATE_DATA_FILE_FOR_GRIDS_HEADER = "data_id\t" +
            "latitude\t" +
            "longitude\t" +
            "datetime\t" +
            "expocode\t" +
            "fCO2rec\t" +
            "SST\t" +
            "salinity";


    /**
     * Print the data needed to generate the gridded-data NetCDF files. Only WOCE-2 data with valid fCO2rec values are
     * printed for the given datasets.  Data is printed in order of datasetIds as they are given and the in increasing
     * time order.  Only one copy of any data points in a dataset with identical valid values for latitude, longitude,
     * time, fCO2rec, and WOCE flag are printed.
     *
     * @param expocodes
     *         report the data in the datasets with these IDs
     * @param outputFile
     *         print the data to this File
     *
     * @throws IllegalArgumentException
     *         if an dataset ID is invalid, or if the full-data DSG file for a dataset is invalid
     * @throws IOException
     *         if reading from a DSG file throws one, or
     *         if creating or writing to the output file throws one
     */
    public void generateDataFileForGrids(TreeSet<String> expocodes, File outputFile)
            throws IllegalArgumentException, IOException {
        PrintWriter report = new PrintWriter(outputFile);
        report.println(GENERATE_DATA_FILE_FOR_GRIDS_HEADER);
        long dataID = 0L;
        try {
            // Read and report the data for one cruise at a time
            for (String expo : expocodes) {
                // Read the data for this cruise
                String upperExpo = DashboardServerUtils.checkDatasetID(expo);
                DsgNcFile dsgFile = dsgFileHandler.getDsgNcFile(upperExpo);
                ArrayList<String> unknownVars = dsgFile.readData(knownDataFileTypes);
                if ( unknownVars.size() > 0 ) {
                    String msg = upperExpo + " unassigned data variables: ";
                    for (String var : unknownVars) {
                        msg += var + "; ";
                    }
                    throw new IllegalArgumentException(msg);
                }
                StdDataArray dataVals = dsgFile.getStdDataArray();

                Integer longitudeIdx = dataVals.getIndexOfType(DashboardServerUtils.LONGITUDE);
                if ( longitudeIdx == null )
                    throw new IOException("The DSG file for " + upperExpo +
                            " does not contain the variable " + DashboardServerUtils.LONGITUDE.getVarName());

                Integer latitudeIdx = dataVals.getIndexOfType(DashboardServerUtils.LATITUDE);
                if ( latitudeIdx == null )
                    throw new IOException("The DSG file for " + upperExpo +
                            " does not contain the variable " + DashboardServerUtils.LATITUDE.getVarName());

                Integer sectimeIdx = dataVals.getIndexOfType(DashboardServerUtils.TIME);
                if ( sectimeIdx == null )
                    throw new IOException("The DSG file for " + upperExpo +
                            " does not contain the variable " + DashboardServerUtils.TIME.getVarName());

                Integer fco2RecIdx = dataVals.getIndexOfType(SocatTypes.FCO2_REC);
                if ( fco2RecIdx == null )
                    throw new IOException("The DSG file for " + upperExpo +
                            " does not contain the variable " + SocatTypes.FCO2_REC.getVarName());

                Integer salIdx = dataVals.getIndexOfType(SocatTypes.SALINITY);
                if ( salIdx == null )
                    throw new IOException("The DSG file for " + upperExpo +
                            " does not contain the variable " + SocatTypes.SALINITY.getVarName());

                Integer sstIdx = dataVals.getIndexOfType(SocatTypes.SST);
                if ( sstIdx == null )
                    throw new IOException("The DSG file for " + upperExpo +
                            " does not contain the variable " + SocatTypes.SST.getVarName());

                Integer woceWaterIdx = dataVals.getIndexOfType(SocatTypes.WOCE_CO2_WATER);
                if ( woceWaterIdx == null )
                    throw new IOException("The DSG file for " + upperExpo +
                            " does not contain the variable " + SocatTypes.WOCE_CO2_WATER.getVarName());

                // Collect and sort the acceptable data for this cruise
                // Any duplicates are eliminated in this process
                TreeSet<DataPoint> datSet = new TreeSet<DataPoint>();
                for (int j = 0; j < dataVals.getNumSamples(); j++) {
                    Double fco2rec = (Double) dataVals.getStdVal(j, fco2RecIdx);
                    if ( fco2rec == null )
                        continue;
                    String woceFlag = (String) dataVals.getStdVal(j, woceWaterIdx);
                    if ( !((woceFlag == null) || woceFlag.isEmpty() ||
                            DashboardServerUtils.WOCE_ACCEPTABLE.equals(woceFlag)) )
                        continue;
                    DataPoint datpt = new DataPoint(upperExpo, (Double) dataVals.getStdVal(j, sectimeIdx),
                            (Double) dataVals.getStdVal(j, latitudeIdx), (Double) dataVals.getStdVal(j, longitudeIdx),
                            (Double) dataVals.getStdVal(j, sstIdx), (Double) dataVals.getStdVal(j, salIdx), fco2rec);
                    if ( !datSet.add(datpt) )
                        System.err
                                .println("Ignored duplicate datapoint for " + upperExpo + ": " + datpt.toString());
                }
                // Print the sorted data for this cruise
                for (DataPoint datPt : datSet) {
                    dataID++;
                    report.format("%d\t%.6f\t%.6f\t%s\t%s\t%.6f\t%.3f\t%.3f\n",
                            Long.valueOf(dataID), datPt.latitude, datPt.longitude,
                            datPt.getDateTimeString(), upperExpo, datPt.fco2rec, datPt.sst, datPt.sal);
                }
            }
        } finally {
            report.close();
        }
    }

    /**
     * @param args
     *         ExpocodesFile  DataOutputFile
     *         where:
     *         ExpocodesFile is a file containing expocodes of the cruises to report;
     *         DataOutputFile is the name of the file to contain the data reported
     */
    public static void main(String[] args) {
        if ( args.length != 2 ) {
            System.err.println("Arguments:  ExpocodesFile  DataOutputFile");
            System.err.println();
            System.err.println("ExpocodesFile");
            System.err.println("    is a file containing expocodes, one per line, to report");
            System.err.println("DataOutputFile");
            System.err.println("    the name of the file to contain the data reported");
            System.exit(1);
        }
        String exposFilename = args[0];
        String destName = args[1];

        TreeSet<String> expocodes = new TreeSet<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(exposFilename));
            try {
                String dataline = reader.readLine();
                while ( dataline != null ) {
                    dataline = dataline.trim().toUpperCase();
                    if ( !dataline.isEmpty() )
                        expocodes.add(dataline);
                    dataline = reader.readLine();
                }
            } finally {
                reader.close();
            }
        } catch ( Exception ex ) {
            System.err.println("Problems reading the file of expocodes '" + exposFilename + "': " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        DashboardConfigStore configStore = null;
        try {
            configStore = DashboardConfigStore.get(false);
        } catch ( Exception ex ) {
            System.err.println("Problems obtaining the default dashboard configuration: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
        try {
            GenerateDataForGridding reporter = new GenerateDataForGridding(configStore);
            try {
                reporter.generateDataFileForGrids(expocodes, new File(destName));
            } catch ( Exception ex ) {
                System.err.println("Problems generating the data file: " + ex.getMessage());
                ex.printStackTrace();
                System.exit(1);
            }
        } finally {
            DashboardConfigStore.shutdown();
        }

        System.exit(0);
    }

}
