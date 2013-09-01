/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import java.io.File;

/**
 * Abstract file handler for dealing with subversion version
 * control of the files contained within the directory.
 * 
 * @author Karl Smith
 */
public abstract class VersionedFileHandler {

	File filesDir;

	VersionedFileHandler(String filesDirName) {
		filesDir = new File(filesDirName);
		if ( ! filesDir.isDirectory() )
			throw new IllegalArgumentException(
					"invalid directory " + filesDirName);
		// TODO: check it is under SVN version control
	}

	//TODO: methods for adding directories and adding/updating files
}
