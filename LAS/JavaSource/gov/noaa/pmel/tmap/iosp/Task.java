/**
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development. 
 */
package gov.noaa.pmel.tmap.iosp;

import gov.noaa.pmel.tmap.exception.LASException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactory;
/**
 * This is where the rubber meets the road and the external process defined by
 * the Tool class actually gets run.
 * @author Roland Schweitzer\
 * @author Joe W. for the Anagram Framework.
 *
 */
public class Task {
    
    private final static Logger log = LoggerFactory.getLogger(Task.class.getName());

    /** Sets up an external process. */
    public Task(String[] cmd, String[] env, File workDir, long timeLimit, String[] errors) {
        this.cmd = cmd;
        this.env = env;
        this.workDir = workDir;
        this.timeLimit = timeLimit;
        this.cmdString = buildCmdString(cmd);
        this.output = new StringBuffer();
        this.stderr = new StringBuffer();
        this.ERROR_INDICATOR = errors;
    }

    /**
     * Executes the external process, returning when it is finished or when it
     * exceeds the time limit specified in the constructor.
     * 
     * @throws LASException
     *             If the process fails, or if the output parser finds an error
     *             message in the output.
     */
    public void run() throws Exception {
        log.debug("Running task");
        try {
            long startTime = System.currentTimeMillis();

            Process process = Runtime.getRuntime().exec(cmd, env, workDir);

            if (process == null) {
                throw new Exception("creation of child process "
                        + "failed for unknown reasons\n" + "command: "
                        + cmdString);
            }

            finish(process, startTime);

        } catch (IOException ioe) {
            throw new Exception("creation of child process failed\n"
                    + "command: " + cmdString, ioe);
        }
    }

    /**
     * Executes the external process, returning when it is finished or when it
     * exceeds the time limit specified.
     * 
     * @param timeLimit
     *            Overrides the time limit specified in the constructor.
     * @throws LASException
     *             If the process fails, or if there is an error message (a line
     *             beginning with "error: ") in the output.
     */
    public void run(long timeLimit) throws Exception {

        long defaultTimeLimit = this.timeLimit;

        this.timeLimit = timeLimit;

        try {
            run();
        } catch (Exception lase) {
            throw lase;
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
    /**
     * Returns the STDERR stream as a string.
     * @return STDERR
     */
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
     * @throws LASException
     *             if anything goes wrong
     */
    protected void finish(Process process, long startTime) throws Exception {

        BufferedReader outstream = new BufferedReader(new InputStreamReader(
                process.getInputStream()));
        BufferedReader errstream = new BufferedReader(new InputStreamReader(
                process.getErrorStream()));

        char[] buffer = new char[1024];

        // wait in 10ms increments for the script to complete
        while (true) {
            try {
                process.exitValue();
                break;
            } catch (IllegalThreadStateException itse) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ie) {
                }
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

                long endTime = System.currentTimeMillis();
                if (timeLimit > 0 && endTime - startTime > timeLimit * 1000) {
                    try {
                        outstream.close();
                        errstream.close();
                    } catch (IOException ioe) {
                    }
                    process.destroy();
                    throw new Exception("process exceeded time limit of "
                            + timeLimit + " sec");
                }
            }
        }

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

        checkErrors();

        try {
            outstream.close();
        } catch (IOException ioe) {
        }
        try {
            errstream.close();
        } catch (IOException ioe) {
        }

    }

    /**
     * Checks if there is an error after the task is completed.
     * 
     * @throws LASException
     *             if there is any error
     */
    protected void checkErrors() throws Exception {

        BufferedReader in = new BufferedReader(new StringReader(stderr
                .toString()));
        boolean stderrErrors = findErrorsInStream(in);

        in = new BufferedReader(new StringReader(output.toString()));
        boolean stdoutErrors = findErrorsInStream(in);
        
        hasError = stderrErrors || stdoutErrors;
        
    }

    /**
     * Look for error information in an input stream. If there is an indication
     * of error, an LASException will be thrown.
     */
    protected boolean findErrorsInStream(BufferedReader in) throws Exception {
        String line;
        int i;
        boolean foundError = false;
        StringBuffer msg = new StringBuffer();
        try {
            while ((line = in.readLine()) != null) {
                if (!foundError) {
                    for (i = 0; i < ERROR_INDICATOR.length; i += 1) {
                        if (line.startsWith(ERROR_INDICATOR[i])) {
                            msg.append(line.substring(ERROR_INDICATOR[i]
                                    .length()));
                            foundError = true;
                            break;
                        }
                    }
                } else {
                    for (i = 0; i < ERROR_INDICATOR.length; i += 1) {
                        if (line.startsWith(ERROR_INDICATOR[i])) {
                            msg.append(";"
                                    + line.substring(ERROR_INDICATOR[i]
                                            .length()));
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
            throw new Exception("Script output error scan failed: "
                    + ioe.getMessage());
        }
        
        return foundError;
    }

    /** An array of strings that indicate there is an error in Ferret output */
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
}
