/**
 *
 */
package gov.noaa.pmel.dashboard.programs;

import gov.noaa.pmel.dashboard.actions.SocatCruiseReporter;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.TreeSet;

/**
 * Generate the file of data for creating the gridded datasets
 *
 * @author Karl Smith
 */
public class GenerateDataForGridding {

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
            System.err.println("Problems reading the file of expocodes '" +
                    exposFilename + "': " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        DashboardConfigStore configStore = null;
        try {
            configStore = DashboardConfigStore.get(false);
        } catch ( Exception ex ) {
            System.err.println("Problems obtaining the default dashboard " +
                    "configuration: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
        try {
            SocatCruiseReporter reporter = new SocatCruiseReporter(configStore);
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
