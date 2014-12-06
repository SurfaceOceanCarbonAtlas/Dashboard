/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.handlers;

import gov.noaa.pmel.socat.dashboard.server.DashboardServerUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * @author Karl Smith
 */
public class CruiseFlagsHandler {

	private static final String FLAG_MSGS_FILENAME_SUFFIX = "_WOCE_flags.tsv";

	private File filesDir;

	/**
	 * Handler for cruise flag messages.  At this time, just WOCE flags.
	 * 
	 * @param filesDirName
	 * 		save user-readable WOCE flag messages in files under this directory
	 */
	public CruiseFlagsHandler(String filesDirName) {
		filesDir = new File(filesDirName);
		if ( ! filesDir.isDirectory() )
			throw new IllegalArgumentException(filesDirName + " is not a directory");
	}

	/**
	 * 
	 * @param expocode
	 * 		expocode of the cruise
	 * @return
	 * 		the file of user-readable WOCE flag messages associated with the cruise
	 * @throws IllegalArgumentException
	 * 		if the cruise expocode is invalid
	 */
	private File cruiseFlagMsgsFile(String expocode) throws IllegalArgumentException {
		// Check that the expocode is somewhat reasonable
		String upperExpo = DashboardServerUtils.checkExpocode(expocode);
		// Get the name of the cruise messages file
		return new File(filesDir, upperExpo.substring(0,4) + 
				File.separatorChar + upperExpo + FLAG_MSGS_FILENAME_SUFFIX);
	}

	public void generateWoceFlagMsgsFile(String expocode, DatabaseRequestHandler dbHandler, 
			ArrayList<String> summaryMsgs) throws IllegalArgumentException {
		File msgsFile = cruiseFlagMsgsFile(expocode);
		PrintWriter msgsWriter;
		try {
			msgsWriter = new PrintWriter(msgsFile);
		} catch (FileNotFoundException ex) {
			throw new IllegalArgumentException(
					"Unexpected error opening WOCE flag messages file " + 
					msgsFile.getPath() + "\n    " + ex.getMessage(), ex);
		}
		try {
			// TODO: Write the summary messages to file
			// TODO: get the current WOCE flags and write to file
		} finally {
			msgsWriter.close();
		}
	}
}
