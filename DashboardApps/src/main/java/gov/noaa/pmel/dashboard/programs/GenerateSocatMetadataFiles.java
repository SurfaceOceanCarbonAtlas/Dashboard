package gov.noaa.pmel.dashboard.programs;

import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.metadata.DashboardOmeMetadata;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.socatmetadata.shared.core.SocatMetadata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeSet;

public class GenerateSocatMetadataFiles {

    private final MetadataFileHandler metadataHandler;

    public GenerateSocatMetadataFiles(MetadataFileHandler metadataHandler) {
        this.metadataHandler = metadataHandler;
    }

    /**
     * Creates a SocatMetadata object from the contents of the indicated OME metadata file
     * then saves it as the standard metadata for this dataset.
     *
     * @param id
     *         ID of the dataset associated with the metadata
     * @param omeFilename
     *         name of the OME metadata file to examine
     *
     * @throws IllegalArgumentException
     *         if there are problems reading the OME metadata,
     *         if there are problems creating the SocatMetaadata from the OME metadata, or
     *         if there are problems saving the SocatMetadata object
     */
    public void generateSocatMetadataFromOme(String id, String omeFilename) throws IllegalArgumentException {
        DashboardOmeMetadata omeMData = metadataHandler.getOmeFromFile(id, omeFilename);
        SocatMetadata socatMData = omeMData.createSocatMetadata();
        metadataHandler.saveSocatMetadata(omeMData.getOwner(), id, socatMData);
    }

    public static void main(String[] args) {
        if ( args.length != 1 ) {
            System.err.println("Arguments:  IDsFile");
            System.err.println();
            System.err.println("Generates the SocatMetadata file for each of dataset with an ID specified ");
            System.err.println("in IDsFile. Each SocatMetadata file is generated from the PI_OME.xml file, ");
            System.err.println("if present;  otherwise, from the OME.xml stub file.  The default dashboard ");
            System.err.println("configuration specified by the environment variable UPLOAD_DASHBOARD_SERVER_NAME ");
            System.err.println("is used for this process.");
            System.err.println();
            System.exit(1);
        }
        String idsFilename = args[0];

        TreeSet<String> idsSet = new TreeSet<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(idsFilename));
            try {
                String dataline = reader.readLine();
                while ( dataline != null ) {
                    dataline = dataline.trim();
                    if ( !(dataline.isEmpty() || dataline.startsWith("#")) )
                        idsSet.add(dataline);
                    dataline = reader.readLine();
                }
            } finally {
                reader.close();
            }
        } catch ( Exception ex ) {
            System.err.println("Error reading dataset IDs from " + idsFilename + ": " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        DashboardConfigStore configStore = null;
        try {
            configStore = DashboardConfigStore.get(false);
        } catch ( Exception ex ) {
            System.err.println("Problems reading the default dashboard configuration file: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
        boolean problems = false;
        try {
            MetadataFileHandler metadataHandler = configStore.getMetadataFileHandler();
            GenerateSocatMetadataFiles generator = new GenerateSocatMetadataFiles(metadataHandler);
            for (String id : idsSet) {
                boolean success = false;
                if ( metadataHandler.getMetadataFile(id, DashboardServerUtils.PI_OME_FILENAME).exists() ) {
                    try {
                        generator.generateSocatMetadataFromOme(id, DashboardServerUtils.PI_OME_FILENAME);
                        System.out.println(id + " -- SocatMetadata file generated from " +
                                DashboardServerUtils.PI_OME_FILENAME);
                        success = true;
                    } catch ( Exception ex ) {
                        System.out.println(id + " -- unable to generate SocatMetadata file from " +
                                DashboardServerUtils.PI_OME_FILENAME + ": " + ex.getMessage());
                    }
                }
                if ( !success ) {
                    try {
                        generator.generateSocatMetadataFromOme(id, DashboardServerUtils.OME_FILENAME);
                        System.out.println(id + " -- SocatMetadata file generated from " +
                                DashboardServerUtils.OME_FILENAME);
                        success = true;
                    } catch ( IllegalArgumentException ex ) {
                        System.out.println(id + " -- unable generate SocatMetadata file from " +
                                DashboardServerUtils.OME_FILENAME + ": " + ex.getMessage());
                    }
                }
                if ( !success )
                    problems = true;
            }
        } finally {
            DashboardConfigStore.shutdown();
        }

        if ( problems )
            System.exit(1);
        System.exit(0);
    }

}
