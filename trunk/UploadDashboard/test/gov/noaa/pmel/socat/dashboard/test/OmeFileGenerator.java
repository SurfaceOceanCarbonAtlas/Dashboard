/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;
import gov.noaa.pmel.socat.dashboard.server.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.server.OmeMetadata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

/**
 * @author Karl Smith
 *
 */
public class OmeFileGenerator {

	/**
	 * Creates OME XML Metadata files from a file of tab-separated metadata 
	 * values in Benajamin's spreadsheet format.
	 *  
	 * @param args TSVMetadata
	 * 		TSVMetadata: name of the file of tab-separated metadata values from Benjamin's spreadsheet
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("\nArguments:  TSV_Metadata\n");
			System.exit(1);
		}
		// Get the metadata file handler
		MetadataFileHandler mdataHandler = null;
		try {
			mdataHandler = DashboardDataStore.get().getMetadataFileHandler();
		} catch (IOException ex) {
			System.err.println("Unable to read the system configuration file: " + 
								ex.getMessage());
			System.exit(1);
		}
		// Open the file containing the spreadsheet TSV table 
		File tsvFile = new File(args[0]);
		String timestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
									 .format(new Date(tsvFile.lastModified()));
		String dataline = "";
		BufferedReader tsvIn = null;
		try {
			tsvIn = new BufferedReader(new FileReader(tsvFile));
		} catch (FileNotFoundException ex) {
			System.err.println("Unable to open " + tsvFile.getPath() + 
					"\n    " + ex.getMessage());
			System.exit(1);
		}
		try {
			try {
				// Read the headers
				dataline = tsvIn.readLine();
				if ( dataline == null )
					throw new IOException("No header line given");
				String[] headers = dataline.split("\t", -1);
				if ( headers.length < 6 )
					throw new IOException("Invalid header line\n    " + dataline);
				// Get the first metadata line
				dataline = tsvIn.readLine();
				while ( dataline != null ) {
					// Get the OME metadata from the metadata line
					OmeMetadata omeMData = new OmeMetadata(headers, dataline, timestamp);
					// Get the abstract name of the OME XML file to be created
					String expocode = omeMData.getExpocode();
					File omeFile = mdataHandler.getMetadataFile(expocode, 
												OmeMetadata.generatedOmeName(expocode));
					// Make sure the parent directory for the OME XML metadata exists
					File parentFile = omeFile.getParentFile();
					if ( ! parentFile.exists() )
						if ( ! parentFile.mkdirs() ) 
							throw new IOException("Unable to create directory " + parentFile.getPath());
					// Save the minimal OME XML metadata file
					omeMData.saveAsMinimalOmeXmlDoc();
					// Get the next metadata line
					dataline = tsvIn.readLine();
				}
			} finally {
				tsvIn.close();
			}
		} catch (IOException ex) {
			System.err.println("IO Problems: " + ex.getMessage());
			System.exit(1);
		} catch (IllegalArgumentException ex) {
			System.err.println("Invalid metadata line: " + dataline + 
					"\n    " + ex.getMessage());
			System.exit(1);
		}
	}

}
