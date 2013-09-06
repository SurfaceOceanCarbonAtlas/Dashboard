/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import java.io.File;
import java.util.ArrayDeque;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusType;

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
	 * Handles version control for files under the given 
	 * working copy directory.  Currently, only configured
	 * for SVN version control.
	 * 
	 * @param filesDirName
	 * 		name of the working copy directory
	 * @throws SVNException
	 * 		if the specified directory does not exist,
	 * 		is not a directory, is not under version
	 * 		control, or is in a conflicted state
	 */
	VersionedFileHandler(String filesDirName) throws SVNException {
		filesDir = new File(filesDirName);
		if ( ! filesDir.isDirectory() )
			throw new SVNException(SVNErrorMessage.create(
					SVNErrorCode.BAD_FILENAME,
					"invalid directory " + filesDirName));
		svnManager = SVNClientManager.newInstance();
		// Check this directory is under SVN version control
		SVNInfo info = svnManager.getWCClient()
								 .doInfo(filesDir, SVNRevision.HEAD);
		if ( info.getTreeConflict() != null )
			throw new SVNException(SVNErrorMessage.create(
					SVNErrorCode.FS_CONFLICT,
					filesDirName + " contains an SVN conflict"));
	}

	/**
	 * Updates the working copy file or directory to the latest version 
	 * from the version control repository.  The update depth is set to
	 * infinity, so all contents under a directory are updated.
	 * 
	 * @param wcfile
	 * 		working copy file to update
	 * @throws SVNException
	 * 		if the version control engine throws one
	 */
	void updateVersion(File wcfile) throws SVNException {
		svnManager.getUpdateClient().doUpdate(wcfile, 
				SVNRevision.HEAD, SVNDepth.INFINITY, false, false);
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
			if ( status.getContentsStatus() == SVNStatusType.STATUS_UNVERSIONED )
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
		for (File currFile = wcfile.getParentFile(); currFile != null; 
										currFile = currFile.getParentFile()) {
			SVNStatus status = svnManager.getStatusClient()
										 .doStatus(currFile, false);
			if ( status.getContentsStatus() == SVNStatusType.STATUS_ADDED )
				filesToCommit.push(currFile);
			else
				break;
		}
		File[] commitFiles = new File[filesToCommit.size()];
		commitFiles = filesToCommit.toArray(commitFiles);

		// Commit the update, including the add if applicable
		// Use SVNDepth.EMPTY so exactly the files/directory specified are committed
		// and not any other updated files under any directories specified
		svnManager.getCommitClient().doCommit(commitFiles, false, message,
									null, null, false, false, SVNDepth.EMPTY);
	}

}
