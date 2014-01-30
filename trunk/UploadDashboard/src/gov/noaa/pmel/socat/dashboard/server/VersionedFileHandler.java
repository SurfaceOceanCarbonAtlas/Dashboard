/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import java.io.File;
import java.util.ArrayDeque;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * Abstract file handler for dealing with subversion version
 * control of the files contained within the directory.
 * 
 * @author Karl Smith
 */
public abstract class VersionedFileHandler {

	File filesDir;
	SVNClientManager svnManager;

	/**
	 * Handles version control for files under the given working copy 
	 * directory.  Currently, only configured for SVN version control.
	 * 
	 * @param filesDirName
	 * 		name of the working copy directory
	 * @param svnUsername
	 * 		username for SVN authentication
	 * @param svnPassword
	 * 		password for SVN authentication
	 * @throws IllegalArgumentException
	 * 		if the specified directory does not exist, is not a directory 
	 * 		or is not under version control
	 */
	VersionedFileHandler(String filesDirName, String svnUsername, 
						String svnPassword) throws IllegalArgumentException {
		filesDir = new File(filesDirName);
		// Check that this is a directory under version control
		if ( ! filesDir.isDirectory() )
			throw new IllegalArgumentException(
					filesDirName + " is not a directory");
		if ( ! SVNWCUtil.isVersionedDirectory(filesDir) ) 
			throw new IllegalArgumentException(
					filesDirName + " is not under version control");
		// Create the version control manager with the provided credentials
		svnManager = SVNClientManager.newInstance(
				SVNWCUtil.createDefaultOptions(true), svnUsername, svnPassword);
	}

	/**
	 * Commits the working copy file to version control.  If the file is not
	 * currently under version control, it is added.  The commit message
	 * will include the given user name as the person responsible for the
	 * the commit, followed by any additional details provided in message.
	 * 
	 * @param wcfile
	 * 		working copy file to (add and) commit in version control
	 * @param message
	 * 		the commit message to use
	 * @throws SVNException
	 * 		if the version control engine throws one
	 */
	void commitVersion(File wcfile, String message) throws SVNException {
		boolean needsAdd = false;
		try {
			SVNStatus status = svnManager.getStatusClient()
										 .doStatus(wcfile, false);
			if ( (status.getContentsStatus() == SVNStatusType.STATUS_UNVERSIONED) ||
				 (status.getContentsStatus() == SVNStatusType.STATUS_NONE) )
				needsAdd = true;
		} catch ( SVNException ex ) {
			// At this point, assume the parent directory is not version controlled
			needsAdd = true;
		}

		if ( needsAdd ) {
			// Add the file, and any unversioned directories in its path, to version control
			svnManager.getWCClient()
					  .doAdd(wcfile, false, false, false, 
							  SVNDepth.EMPTY, false, true);
		}

		// Get the list of directories, as well as the file, that need to be committed
		ArrayDeque<File> filesToCommit = new ArrayDeque<File>();
		filesToCommit.push(wcfile);
		// Always add the parent directory to the list to be examined
		File parentToUpdate = wcfile.getParentFile();
		filesToCommit.push(parentToUpdate);
		// Work down the directory tree until we fall out 
		// or find an unchanged directory
		for (File currFile = parentToUpdate.getParentFile(); currFile != null; 
										currFile = currFile.getParentFile()) {
			SVNStatus status;
			try {
				status = svnManager.getStatusClient().doStatus(currFile, false);
			} catch ( SVNException ex ) {
				// Probably outside the working copy
				break;
			}
			SVNStatusType statType = status.getContentsStatus();
			if ( statType == SVNStatusType.STATUS_ADDED ) {
				filesToCommit.push(currFile);
				parentToUpdate = currFile;
			}
			else if ( statType == SVNStatusType.STATUS_NORMAL ) {
				// Normal revisioned directory in the working copy
				parentToUpdate = currFile;
				break;
			}
			else {
				// Unknown or non-revisioned directory
				break;
			}
		}
		File[] commitFiles = new File[filesToCommit.size()];
		commitFiles = filesToCommit.toArray(commitFiles);

		// Commit the update, including the add if applicable
		// Use SVNDepth.EMPTY so exactly the files/directory specified are committed
		// and not any other updated files under any directories specified
		svnManager.getCommitClient().doCommit(commitFiles, false, message,
									null, null, false, false, SVNDepth.EMPTY);
		// Update the existing parent directory
		svnManager.getUpdateClient().doUpdate(parentToUpdate, 
				SVNRevision.HEAD, SVNDepth.INFINITY, false, false);
	}

	/**
	 * Deletes the given file from both the working copy directory 
	 * as well as from version control.
	 * 
	 * @param wcFile
	 * 		working copy file to delete
	 * @param message
	 * 		commit message to use for this deletion
	 * @throws SVNException
	 * 		if deleting the file or committing the deletion throws one
	 */
	void deleteVersionedFile(File wcFile, String message) throws SVNException {
		// Delete the file from the working directory and
		// schedule deletion from the repository
		svnManager.getWCClient().doDelete(wcFile, false, true, false);
		// Commit the deletion from the repository
		svnManager.getCommitClient().doCommit(new File[] {wcFile}, false, message,
									null, null, false, false, SVNDepth.EMPTY);
		// Update the existing parent directory
		svnManager.getUpdateClient().doUpdate(wcFile.getParentFile(), 
				SVNRevision.HEAD, SVNDepth.INFINITY, false, false);
	}

}
