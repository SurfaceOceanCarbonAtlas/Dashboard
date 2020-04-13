/**
 *
 */
package gov.noaa.pmel.dashboard.programs;

import gov.noaa.pmel.dashboard.handlers.DataFileHandler;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeSet;

/**
 * Updates the metadata and additional documents for datasets
 * with the all available documents for each dataset.
 *
 * @author Karl Smith
 */
public class AddAllMetadata {

    /**
     * @param args
     *         IDsFile - update metadata and additional documents for datasets with these IDs
     */
    public static void main(String[] args) {
        if ( args.length != 1 ) {
            System.err.println();
            System.err.println("Arguments:  IDsFile");
            System.err.println();
            System.err.println("Updates the metadata and additional documents for the datasets ");
            System.err.println("specified in IDsFile with all available documents for each dataset. ");
            System.err.println("The default dashboard configuration is used for this process. ");
            System.err.println();
            System.exit(1);
        }

        String idsFilename = args[0];

        // Get the IDs of the datasets to update
        TreeSet<String> idsSet = new TreeSet<String>();
        try {
            BufferedReader idsReader = new BufferedReader(new FileReader(idsFilename));
            try {
                String dataline = idsReader.readLine();
                while ( dataline != null ) {
                    dataline = dataline.trim();
                    if ( !(dataline.isEmpty() || dataline.startsWith("#")) )
                        idsSet.add(dataline);
                    dataline = idsReader.readLine();
                }
            } finally {
                idsReader.close();
            }
        } catch ( Exception ex ) {
            System.err.println("Error reading dataset IDs from " + idsFilename + ": " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        // Get the default dashboard configuration
        DashboardConfigStore configStore = null;
        try {
            configStore = DashboardConfigStore.get(false);
        } catch ( Exception ex ) {
            System.err.println("Problems reading the default dashboard configuration file: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
        boolean success = true;
        try {

            DataFileHandler dataHandler = configStore.getDataFileHandler();
            MetadataFileHandler metaHandler = configStore.getMetadataFileHandler();
            for (String datasetId : idsSet) {
                try {
                    DashboardDataset dataset = dataHandler.getDatasetFromInfoFile(datasetId);
                    if ( dataset == null ) {
                        System.err.println("No dataset with the ID " + datasetId);
                        success = false;
                        continue;
                    }
                    // Clear the OME upload timestamp and create a new list of supplemental documents
                    dataset.setOmeTimestamp(null);
                    TreeSet<String> addlDocs = new TreeSet<String>();
                    // Add all existing metadata documents for this dataset
                    for (DashboardMetadata mdata : metaHandler.getMetadataFiles(datasetId)) {
                        if ( mdata.getFilename().equals(DashboardServerUtils.OME_FILENAME) ||
                                mdata.getFilename().equals(DashboardServerUtils.PI_OME_PDF_FILENAME) ) {
                            // Ignore the OME.xml stub and the PI_OME.pdf file
                        }
                        else if ( mdata.getFilename().equals(DashboardServerUtils.PI_OME_FILENAME) ) {
                            // PI-provided OME.xml file - set the OME upload timestamp
                            dataset.setOmeTimestamp(mdata.getUploadTimestamp());
                        }
                        else {
                            // Add the "filename ; timestamp" additional document string
                            addlDocs.add(mdata.getAddlDocsTitle());
                        }
                    }
                    dataset.setAddlDocs(addlDocs);
                    // Save the updated dataset informations
                    // but do not commit the change - to be done manually
                    dataHandler.saveDatasetInfoToFile(dataset, null);
                    System.err.println("Documents updated for " + datasetId);
                } catch ( Exception ex ) {
                    System.err.println("Problems working with " + datasetId + ": " + ex.getMessage());
                    ex.printStackTrace();
                    success = false;
                    continue;
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
