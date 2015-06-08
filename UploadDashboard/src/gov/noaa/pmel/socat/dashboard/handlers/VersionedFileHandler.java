/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.handlers;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Timer;
import java.util.TimerTask;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * Abstract file handler for dealing with subversion version
 * control of the files contained within the directory.
 * 
 * @author Karl Smith
 */
public class VersionedFileHandler {

	private static final String SVN_COMMIT_COMMANDS_FILENAME = "commit_commands.sh";
	File filesDir;
	SVNClientManager svnManager;
	ArrayDeque<File[]> filesToCommit;
	ArrayDeque<File> parentToUpdate;
	ArrayDeque<String> commitMessage;

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
		filesToCommit = new ArrayDeque<File[]>();
		parentToUpdate = new ArrayDeque<File>();
		commitMessage = new ArrayDeque<String>();
		watchCommitQueue();
	}

	/**
	 * Adds files to queue of files to be committed.
	 * 
	 * @param commitFiles
	 * 		files to be committed
	 * @param parent
	 * 		parent directory to be updated after the commit
	 * @param message
	 * 		message to accompany the commit
	 */
	private void addFilesToCommit(File[] commitFiles, File parent, String message) {
		synchronized(filesToCommit) {
			filesToCommit.addLast(commitFiles);
			parentToUpdate.addLast(parent);
			commitMessage.addLast(message);
		}
	}

	// Check every 60 seconds
	private static long MILLISECONDS_CHECK_INTERVAL = 60 * 1000L;
	// Keep working while less than 3 seconds have passed
	private static long MILLISECONDS_WORK_INTERVAL = 3 * 1000L;
	/**
	 * Periodically checks the queue of files to be committed, and 
	 * commits any files present.  Stops when filesDir is set to null.
	 */
	private void watchCommitQueue() {
		(new Timer()).schedule(new TimerTask() {
			@Override
			public void run() {
				File[] commitFiles;
				File parent;
				String message;
				long startTime = System.currentTimeMillis();
				do {
					synchronized(filesToCommit) {
						commitFiles = filesToCommit.pollFirst();
						parent = parentToUpdate.pollFirst();
						message = commitMessage.pollFirst();
					}
					if ( commitFiles != null ) {
						if ( (parent != null) && (message != null) ) {
							try {
								/*
								// Use SVNDepth.EMPTY so exactly the files/directory specified are committed
								// and not any other updated files under any directories specified
								svnManager.getCommitClient().doCommit(commitFiles, false, 
										message, null, null, false, false, SVNDepth.EMPTY);
								// Update the parent directory
								svnManager.getUpdateClient().doUpdate(parent, 
										SVNRevision.HEAD, SVNDepth.INFINITY, false, false);
								*/
								// For v3, just write the svn commands to file to be dealt with manually
								PrintWriter cmdsWriter = new PrintWriter(new FileWriter(
										new File(filesDir, SVN_COMMIT_COMMANDS_FILENAME), true));
								cmdsWriter.print("svn commit --depth=empty -m '" + message + "'");
								for ( File svnfile : commitFiles )
									cmdsWriter.print(" " + svnfile.getPath());
								cmdsWriter.println();
								cmdsWriter.println("svn update --depth=infinity " + parent.getPath());
								cmdsWriter.close();
							} catch (Exception ex) {
								// Should not happen, but nothing can be done about it if it does
							}
						}
						if ( (System.currentTimeMillis() - startTime) > MILLISECONDS_WORK_INTERVAL )
							break;
					}
				} while ( commitFiles != null );
				// Check if this VersionedFileHander is no longer needed
				if ( (filesDir == null) && (commitFiles == null) ) {
					cancel();
					return;
				}
			}
		}, MILLISECONDS_CHECK_INTERVAL, MILLISECONDS_CHECK_INTERVAL);
	}

	/**
	 * Marks that this file handler should perform any outstanding commits
	 * and terminate the thread checking for commits.
	 */
	public void shutdown() {
		filesDir = null;
	}

	/**
	 * Commits the working copy file to version control.  
	 * If the file is not currently under version control, it is added.
	 * 
	 * @param wcfile
	 * 		working copy file to add, if needed, and commit in version control
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
			SVNStatusType contentsStatus = status.getContentsStatus();
			if ( (contentsStatus == SVNStatusType.STATUS_UNVERSIONED) ||
				 (contentsStatus == SVNStatusType.STATUS_DELETED) ||
				 (contentsStatus == SVNStatusType.STATUS_NONE) )
				needsAdd = true;
		} catch ( SVNException ex ) {
			// At this point, assume the parent directory is not version controlled
			needsAdd = true;
		}

		if ( needsAdd ) {
			// Add the file (force), and any unversioned directories in its path, to version control
			svnManager.getWCClient()
					  .doAdd(wcfile, true, false, false, SVNDepth.EMPTY, false, true);
		}

		// Get the list of directories, as well as the file, that need to be committed
		ArrayDeque<File> commitFiles = new ArrayDeque<File>();
		commitFiles.push(wcfile);
		// Work down the directory tree until we fall out 
		// or find an unchanged directory
		File parent = wcfile;
		for (File currFile = wcfile.getParentFile(); currFile != null; 
										currFile = currFile.getParentFile()) {
			SVNStatus status;
			try {
				status = svnManager.getStatusClient().doStatus(currFile, false);
			} catch ( SVNException ex ) {
				// Probably outside the working copy
				break;
			}
			SVNStatusType statType = status.getContentsStatus();
			if ( (statType == SVNStatusType.STATUS_ADDED) ||
				 (statType == SVNStatusType.STATUS_MODIFIED) ||
				 (statType == SVNStatusType.STATUS_REPLACED) ) {
				commitFiles.push(currFile);
				parent = currFile;
			}
			else if ( statType == SVNStatusType.STATUS_NORMAL ) {
				// An unmodified directory under version control 
				parent = currFile;
				break;
			}
			else {
				// A directory outside version control
				break;
			}
		}
		// schedule committing the changes
		addFilesToCommit(commitFiles.toArray(new File[commitFiles.size()]), parent, message);
	}

	/**
	 * Moves a working copy file.
	 * 
	 * @param oldWcFile
	 * 		existing working copy file to move
	 * @param newWcFile
	 * 		new name and location for the file; the parent directory must exist
	 * 		but does not need to be under version control (will be added if not)
	 * @param message
	 * 		commit message for this move
	 * @throws SVNException
	 * 		if the version control engine throws one
	 */
	void moveVersionedFile(File oldWcFile, File newWcFile, String message) throws SVNException {
		// Make sure the parent directory of the new file is under version control
		File parent = newWcFile.getParentFile();
		boolean needsAdd = false;
		try {
			SVNStatus status = svnManager.getStatusClient()
										 .doStatus(parent, false);
			SVNStatusType contentsStatus = status.getContentsStatus();
			if ( (contentsStatus == SVNStatusType.STATUS_UNVERSIONED) ||
				 (contentsStatus == SVNStatusType.STATUS_DELETED) ||
				 (contentsStatus == SVNStatusType.STATUS_NONE) )
				needsAdd = true;
		} catch ( SVNException ex ) {
			// At this point, assume the parent directory is not version controlled
			needsAdd = true;
		}
		if ( needsAdd ) {
			// Add the file (force), and any unversioned directories in its path, to version control
			svnManager.getWCClient()
					  .doAdd(parent, true, false, false, SVNDepth.EMPTY, false, true);
		}
		// Move the old file to the new location
		svnManager.getMoveClient().doMove(oldWcFile, newWcFile);
		
		// Get the list of directories, as well as the file, that need to be committed
		ArrayDeque<File> commitFiles = new ArrayDeque<File>();
		commitFiles.push(oldWcFile);
		commitFiles.push(newWcFile);
		// Work down the directory tree until we fall out 
		// or find an unchanged directory
		for (File currFile = newWcFile.getParentFile(); currFile != null; 
										currFile = currFile.getParentFile()) {
			SVNStatus status;
			try {
				status = svnManager.getStatusClient().doStatus(currFile, false);
			} catch ( SVNException ex ) {
				// Probably outside the working copy
				break;
			}
			SVNStatusType statType = status.getContentsStatus();
			if ( (statType == SVNStatusType.STATUS_ADDED) ||
				 (statType == SVNStatusType.STATUS_MODIFIED) ||
				 (statType == SVNStatusType.STATUS_REPLACED) ) {
				commitFiles.push(currFile);
				parent = currFile;
			}
			else if ( statType == SVNStatusType.STATUS_NORMAL ) {
				// An unmodified directory under version control 
				parent = currFile;
				break;
			}
			else {
				// A directory outside version control
				break;
			}
		}
		// schedule committing the changes
		addFilesToCommit(commitFiles.toArray(new File[commitFiles.size()]), parent, message);
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
		// Delete the file (force) from the working directory and version control
		svnManager.getWCClient().doDelete(wcFile, true, true, false);
		// schedule committing the changes
		addFilesToCommit(new File[] {wcFile}, wcFile.getParentFile(), message);
	}
	
}
