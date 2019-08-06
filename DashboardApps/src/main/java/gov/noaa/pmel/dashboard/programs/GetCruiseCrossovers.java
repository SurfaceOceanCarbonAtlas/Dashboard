package gov.noaa.pmel.dashboard.programs;

import gov.noaa.pmel.dashboard.actions.CrossoverChecker;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.shared.Crossover;
import gov.noaa.pmel.dashboard.shared.DatasetQCStatus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Finds high-quality crossovers within sets of datasets
 *
 * @author Karl Smith
 */
public class GetCruiseCrossovers {

    /**
     * @param args
     *         ExpocodesFile - a file containing expocodes of the set of datasets
     *         to examine for high-quality crossovers
     */
    public static void main(String[] args) {
        if ( args.length != 1 ) {
            System.err.println("Arguments:  ExpocodesFile");
            System.err.println();
            System.err.println("ExpocodesFile");
            System.err.println("    is a file containing expocodes, one per line, of the set of datasets ");
            System.err.println("    to examine for high-quality crossovers. ");
            System.err.println();
            System.exit(1);
        }
        String exposFilename = args[0];

        TreeSet<String> givenExpocodes = new TreeSet<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(exposFilename));
            try {
                String dataline = reader.readLine();
                while ( dataline != null ) {
                    dataline = dataline.trim().toUpperCase();
                    if ( !dataline.isEmpty() )
                        givenExpocodes.add(dataline);
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

            DsgNcFileHandler dsgHandler = configStore.getDsgNcFileHandler();

            long startTime = System.currentTimeMillis();

            // Get the QC flags for the datasets from the DSG files
            double timeDiff = (System.currentTimeMillis() - startTime) / (60.0 * 1000.0);
            System.err.format("%.2fm - getting QC flags for the datasets\n", timeDiff);
            TreeMap<String,String> datasetFlagsMap = new TreeMap<String,String>();
            TreeSet<String> notActuallyAExpos = new TreeSet<String>();
            for (String expo : givenExpocodes) {
                try {
                    String[] flagVersionStatus = dsgHandler.getDatasetQCFlagAndVersionStatus(expo);
                    DatasetQCStatus qcFlag = DatasetQCStatus.fromString(flagVersionStatus[0]);
                    if ( DatasetQCStatus.Status.ACCEPTED_A.equals(qcFlag.getActual()) ||
                            DatasetQCStatus.Status.ACCEPTED_B.equals(qcFlag.getActual()) ) {
                        datasetFlagsMap.put(expo, flagVersionStatus[0]);
                    }
                    else {
                        throw new Exception("QC flag is " + flagVersionStatus[0]);
                    }
                    // Add all flag-A expocodes, then remove those that actually do have crossovers
                    if ( DatasetQCStatus.Status.ACCEPTED_A.equals(qcFlag.getActual()) )
                        notActuallyAExpos.add(expo);
                } catch ( Exception ex ) {
                    System.err.println("Problems with expocode " + expo + ": " + ex.getMessage());
                    // Skip this expocode and continue on
                }
            }

            TreeMap<String,TreeSet<Crossover>> crossoversMap = null;
            try {
                Set<String> expoSet = datasetFlagsMap.keySet();
                CrossoverChecker crossChecker = new CrossoverChecker(configStore.getDsgNcFileHandler());
                crossoversMap = crossChecker.findCrossovers(expoSet, expoSet, System.err, startTime);
            } catch ( Exception ex ) {
                System.err.println("Problems checking for crossovers: " + ex.getMessage());
                ex.printStackTrace();
                System.exit(1);
            }
            for (Map.Entry<String,TreeSet<Crossover>> entry : crossoversMap.entrySet()) {
                String expo = entry.getKey();
                notActuallyAExpos.remove(expo);
                for (Crossover cross : entry.getValue()) {
                    String[] expoPair = cross.getDatasetIds();
                    String otherExpo = expo.equals(expoPair[0]) ? expoPair[1] : expoPair[0];
                    System.out.format("%s (%s) high-quality cross-over with %s (%s) : d=%.2f\n",
                            expo, datasetFlagsMap.get(expo), otherExpo, datasetFlagsMap.get(otherExpo),
                            cross.getMinDistance().doubleValue());
                }
            }

            System.out.println();

            System.out.println(Integer.toString(notActuallyAExpos.size()) +
                    " datasets with a QC flag of A  but without a high-quality crossover: ");
            for (String expo : notActuallyAExpos) {
                System.out.println(expo);
            }

            System.out.println();

        } finally {
            DashboardConfigStore.shutdown();
        }
        System.exit(0);
    }

}
