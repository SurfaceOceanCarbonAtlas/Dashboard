package gov.noaa.pmel.socat.dashboard.ferret;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

public class Task {

	private static Logger log = LogManager.getLogger(Task.class.getName());

	/** An array of strings that indicate there is an error in command output */
	protected String[] ERROR_INDICATOR;

	/** Standard output buffer */
	protected StringBuffer output;

	/** Standard error output buffer */
	protected StringBuffer stderr;

	/** Command string used to create this external process */
	protected String cmdString;

	/** An array of commmands used to create this external process */
	protected String[] cmd;

	/**
	 * String array to describe the environment setting for this external
	 * process
	 */
	protected String[] env;

	/** Work directory for this external process */
	protected File workDir;

	/** Default time limit in sec for this external process */
	protected long timeLimit;

	/** The Error message, can be blank or null. */
	protected String errorMessage;

	/** Error indicator. */
	protected boolean hasError;

	protected File cancel;

	/** Sets up an external process. */
	public Task(String[] cmd, String[] env, File workDir, File cancel, long timeLimit, String[] errors) {
		this.cmd = cmd;
		this.env = env;
		this.workDir = workDir;
		this.timeLimit = timeLimit;
		this.cmdString = buildCmdString(cmd);
		this.output = new StringBuffer();
		this.stderr = new StringBuffer();
		this.cancel = cancel;
		this.ERROR_INDICATOR = errors;
	}

	/**
	 * Executes the external process, returning when it is finished or when it
	 * exceeds the time limit specified in the constructor.
	 * 
	 * @throws Exception
	 *             If the process fails, or if the output parser finds an error
	 *             message in the output.
	 */
	public void run() throws Exception {

		try {
			long startTime = System.currentTimeMillis();

			Process process = Runtime.getRuntime().exec(cmd, env, workDir);

			if (process == null) {
				throw new Exception("creation of child process failed for unknown reasons\n" + 
						"command: " + cmdString);
			}

			finish(process, startTime);

		} catch (IOException ioe) {
			throw new Exception("creation of child process failed\n"
					+ "command: " + cmdString + ioe);
		}
	}

	/**
	 * Executes the external process, returning when it is finished or when it
	 * exceeds the time limit specified.
	 * 
	 * @param timeLimit
	 *            Overrides the time limit specified in the constructor.
	 * @throws Exception
	 *             If the process fails, or if there is an error message (a line
	 *             beginning with "error: ") in the output.
	 */
	public void run(long timeLimit) throws Exception {

		long defaultTimeLimit = this.timeLimit;

		this.timeLimit = timeLimit;

		try {
			run();
		} catch (Exception lase) {
			throw new Exception(lase.toString());
		} finally {
			this.timeLimit = defaultTimeLimit;
		}
	}

	/**
	 * Returns a printable string version of the external command.
	 */
	public String getCmd() {
		return cmdString;
	}

	public String getStderr() {
		return stderr.toString();
	}

	/** Returns a string version of the command's console output */
	public String getOutput() {
		return output.toString();
	}

	/** Returns error state. */
	public boolean getHasError() {
		return hasError;
	}

	/** Returns the error message. */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Builds a command string using an array of commands
	 */
	protected String buildCmdString(String[] cmd) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < cmd.length; i++) {
			if (i > 0) {
				buffer.append(" ");
			}
			buffer.append(cmd[i]);
		}
		return buffer.toString();
	}

	/**
	 * Monitors the running process, puts the process's standard output to
	 * <code>output</code> and errors output to <code>stderr</code>. Also
	 * monitors the process' time limit and output limit, checks if there is any
	 * error generated.
	 * <p>
	 * 
	 * @param process
	 *            the running process
	 * @param startTime
	 *            the start time of the process
	 * @throws Exception
	 *             if anything goes wrong
	 */
	protected void finish(Process process, long startTime) throws Exception {

		BufferedReader outstream = new BufferedReader(new InputStreamReader(
				process.getInputStream()));
		BufferedReader errstream = new BufferedReader(new InputStreamReader(
				process.getErrorStream()));
		try {
			char[] buffer = new char[1024];

			while (true) {
				try {
					process.exitValue();
					break;
				} catch (IllegalThreadStateException itse) {
					// wait 10ms for the script to complete
					try {
						Thread.sleep(10);
					} catch (InterruptedException ie) {
					}

					// pass along any stdout and stderr
					try {
						if (outstream.ready()) {
							int charsRead = outstream.read(buffer);
							output.append(buffer, 0, charsRead);
						}
					} catch (IOException ioe) {
					}
					try {
						if (errstream.ready()) {
							int charsRead = errstream.read(buffer);
							stderr.append(buffer, 0, charsRead);
						}
					} catch (IOException ioe) {
					}

					// check if we have waited too long
					long endTime = System.currentTimeMillis();
					if (timeLimit > 0 && endTime - startTime > timeLimit * 1000) {
						process.destroy();
						throw new Exception("process exceeded time limit of " + timeLimit + " sec");
					}

					// check if the request was canceled
					if (cancel != null && cancel.exists()) {
						log.debug("Backend request canceled: "+cmdString);
						process.destroy();
						cancel.delete();
						throw new Exception("Process canceled.");
					}
				}
			}

			// pass along any remaining stdout and stderr
			try {
				while (outstream.ready()) {
					int charsRead = outstream.read(buffer);
					output.append(buffer, 0, charsRead);
				}
			} catch (IOException ioe) {
			}
			try {
				while (errstream.ready()) {
					int charsRead = errstream.read(buffer);
					stderr.append(buffer, 0, charsRead);
				}
			} catch (IOException ioe) {
			}

			// check if any error messages were output by the process
			checkErrors();

		} finally {
			outstream.close();
			errstream.close();
		}
	}

	/**
	 * Checks if there is an error after the task is completed.
	 * @throws Exception 
	 * @throws Exception 
	 * 
	 * @throws Exception
	 *             if there is any error
	 */
	protected void checkErrors() throws Exception   {

		BufferedReader in = new BufferedReader(new StringReader(stderr
				.toString()));
		boolean stderrErrors = findErrorsInStream(in);

		in = new BufferedReader(new StringReader(output.toString()));
		boolean stdoutErrors = findErrorsInStream(in);

		hasError = stderrErrors || stdoutErrors;

	}

	/**
	 * Look for error information in an input stream. If there is an indication
	 * of error, an Exception will be thrown.
	 * @throws Exception 
	 */
	protected boolean findErrorsInStream(BufferedReader in) throws Exception  {
		String line;
		int i;
		boolean foundError = false;
		StringBuffer msg = new StringBuffer();
		try {
			while ((line = in.readLine()) != null) {
				if (!foundError) {
					for (i = 0; i < ERROR_INDICATOR.length; i += 1) {
						if (line.trim().startsWith(ERROR_INDICATOR[i])) {
							msg.append(line.substring(ERROR_INDICATOR[i].length()));
							foundError = true;
							break;
						}
					}
				} else {
					for (i = 0; i < ERROR_INDICATOR.length; i += 1) {
						if (line.trim().startsWith(ERROR_INDICATOR[i])) {
							msg.append(";" + line.substring(ERROR_INDICATOR[i].length()));
							errorMessage = msg.toString().replaceAll("\"", "&quot;");
						}
					}
					String solidLine = line.trim();
					if (solidLine.length() > 0) {
						char firstChar = solidLine.charAt(0);
						if ((firstChar <= 'A' || firstChar >= 'Z')) {
							msg.append(" " + solidLine);
						}
					}
					errorMessage = msg.toString().replaceAll("\"", "&quot;");
				}
			}

			if (foundError) {
				errorMessage = msg.toString().replaceAll("\"", "&quot;");
			}

		} catch (IOException ioe) {
			throw new Exception("Script output error scan failed: " + ioe.getMessage());
		}

		return foundError;
	}
}
