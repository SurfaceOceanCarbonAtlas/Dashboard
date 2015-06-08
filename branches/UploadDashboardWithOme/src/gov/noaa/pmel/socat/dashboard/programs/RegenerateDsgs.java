/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.ferret.FerretConfig;
import gov.noaa.pmel.socat.dashboard.ferret.SocatTool;
import gov.noaa.pmel.socat.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.nc.CruiseDsgNcFile;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.socat.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Regenerates the full-data DSG files with the current data values 
 * in the DSG files but with the current metadata values in the OME 
 * XML files.  The decimated DSG files are then regenerated from the
 * full-data DSG file.
 *  
 * @author Karl Smith
 */
public class RegenerateDsgs {

	/**
	 * @param args
	 * 		ExpocodesFile - update dashboard status of these cruises
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("Arguments:  ExpocodesFile");
			System.err.println();
			System.err.println("Regenerates the full-data DSG files with the current data values ");
			System.err.println("in the DSG files but with the current metadata values in the OME "); 
			System.err.println("XML files.  The decimated DSG files are then regenerated from the ");
			System.err.println("full-data DSG file.  The default dashboard configuration is used ");
			System.err.println("for this process. "); 
			System.err.println();
			System.exit(1);
		}

		String expocodesFilename = args[0];

		boolean success = true;

		// Get the default dashboard configuration
		DashboardConfigStore configStore = null;		
		try {
			configStore = DashboardConfigStore.get();
		} catch (Exception ex) {
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
						if ( ! ( dataline.isEmpty() || dataline.startsWith("#") ) )
							allExpocodes.add(dataline);
						dataline = expoReader.readLine();
					}
				} finally {
					expoReader.close();
				}
			} catch (Exception ex) {
				System.err.println("Error getting expocodes from " + 
						expocodesFilename + ": " + ex.getMessage());
				ex.printStackTrace();
				System.exit(1);
			}

			CruiseFileHandler cruiseHandler = configStore.getCruiseFileHandler();
			DsgNcFileHandler dsgHandler = configStore.getDsgNcFileHandler();
			MetadataFileHandler metaHandler = configStore.getMetadataFileHandler();
			FerretConfig ferretConfig = configStore.getFerretConfig();

			// update each of these cruises
			for ( String expocode : allExpocodes ) {
				try {

					// Get just the filenames from the set of addition document
					DashboardCruise cruise = cruiseHandler.getCruiseFromInfoFile(expocode);
					TreeSet<String> addlDocs = new TreeSet<String>();
					for ( String docInfo : cruise.getAddlDocs() ) {
						addlDocs.add(DashboardMetadata.splitAddlDocsTitle(docInfo)[0]);
					}

					// Read the current metadata in the full-data DSG file
					CruiseDsgNcFile fullDataDsg = dsgHandler.getDsgNcFile(expocode);
					ArrayList<String> missing = fullDataDsg.read(false);
					if ( ! missing.isEmpty() )
						throw new RuntimeException("Unexpected values missing from the DSG file: " + missing);
					SocatMetadata fullDataMeta = fullDataDsg.getMetadata();
					ArrayList<SocatCruiseData> dataVals = fullDataDsg.getDataList();

					// Get the metadata in the OME XML file
					DashboardOmeMetadata omeMData = new DashboardOmeMetadata(
							metaHandler.getMetadataInfo(expocode, DashboardMetadata.OME_FILENAME), metaHandler);
					SocatMetadata updatedMeta = omeMData.createSocatMetadata(
							fullDataMeta.getSocatVersion(), addlDocs, fullDataMeta.getQcFlag());

					// Regenerate the DSG file with the OME XML metadata
					try {

						fullDataDsg.create(updatedMeta, dataVals);
						// Call Ferret to add lon360 and tmonth (calculated data should be the same)
						SocatTool tool = new SocatTool(ferretConfig);
						ArrayList<String> scriptArgs = new ArrayList<String>(1);
						scriptArgs.add(fullDataDsg.getPath());
						tool.init(scriptArgs, expocode, FerretConfig.Action.COMPUTE);
						tool.run();
						if ( tool.hasError() )
							throw new IllegalArgumentException(expocode + 
									": Failure adding computed variables: " + tool.getErrorMessage());
						// Regenerate the decimated-data DSG file 
						dsgHandler.decimateCruise(expocode);
						System.out.println("Regenerated the DSG files for " + expocode);

					} catch ( Exception ex ) {
						fullDataDsg.delete();
						System.err.println("Problems regenerating the DSG files for " + 
								expocode + ": " + ex.getMessage());
						success = false;
						continue;
					}

				} catch ( Exception ex ) {
					System.err.println("Problems working with the full-data DSG file for " + 
							expocode + ": " + ex.getMessage());
					success = false;
					continue;
				}

			}

			dsgHandler.flagErddap(true, true);

		} finally {
			configStore.shutdown();
		}

		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
