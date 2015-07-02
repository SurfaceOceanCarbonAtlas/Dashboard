package uk.ac.uea.socat.sanitychecker;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import uk.ac.uea.socat.omemetadata.OmeMetadata;
import uk.ac.uea.socat.sanitychecker.config.BaseConfig;
import uk.ac.uea.socat.sanitychecker.config.ColumnConversionConfig;
import uk.ac.uea.socat.sanitychecker.config.ConfigException;
import uk.ac.uea.socat.sanitychecker.data.ColumnSpec;
import uk.ac.uea.socat.sanitychecker.data.InvalidColumnSpecException;
import uk.ac.uea.socat.sanitychecker.messages.Message;
import uk.ac.uea.socat.sanitychecker.messages.MessageException;
import uk.ac.uea.socat.sanitychecker.messages.MessageSummary;
import uk.ac.uea.socat.sanitychecker.messages.Messages;

/**
 * This is an executable that can run the Sanity Checker as a standalone program.
 * The results of the Sanity Checker are written to files, with a summary printed
 * on the console.
 */
public class SanityCheckerRun {

	/**
	 * The location of the base configuration file
	 */
	private static final String BASE_CONFIG_LOCATION = "config.properties";
	
	/**
	 * The name of the input dir
	 */
	private String itsInputDir = null;
	
	/**
	 * The name of the output dir
	 */
	private String itsOutputDir = null;
	
	/**
	 * The name of the input data file
	 */
	private String itsDataFilename = null;
	
	/**
	 * The name of the XML file containing the column spec
	 */
	private String itsColSpecFilename = null;
	
	/**
	 * The date format used in the file
	 */
	private String itsDateFormat = null;
	
	/**
	 * Base config
	 */
	private BaseConfig itsBaseConfig = null;
	
	/**
	 * Logger object
	 */
	private Logger itsLogger = null;
	
	/**
	 * Sanity checks the file whose details were passed in on
	 * the command line
	 * @param args The command line arguments
	 */
	private SanityCheckerRun(String[] args) {
		// Extract what we need from the data file
		ColumnSpec colSpec = null;
		StringBuffer metadataHeader = new StringBuffer();
		ArrayList<ArrayList<String>> records = new ArrayList<ArrayList<String>>();
		
		// Read in the command line arguments
		readCommandArgs(args);
		
		// Process the command line arguments to make sure they're
		// OK and everything exists
		boolean ok = checkCommandArgs();
		
		if (!ok) {
			System.out.println("Command line arguments are bad.");
		}
		
		if (ok) {
			// Initialise the logger
			itsLogger = Logger.getLogger(itsDataFilename);
			
			// Read in base config and build the column spec object
			try {
				// Load in the basic properties
				SanityChecker.initConfig(BASE_CONFIG_LOCATION);
				itsBaseConfig = BaseConfig.getInstance();

				ColumnConversionConfig.init(itsBaseConfig.getColumnConversionConfigFile(), itsLogger);
				colSpec = ColumnSpec.importSpec(new File(itsInputDir, itsColSpecFilename), new File(itsBaseConfig.getColumnSpecSchemaFile()), ColumnConversionConfig.getInstance(), itsLogger);
			} catch(SanityCheckerException e) {
				System.out.println("Error initialising configuration: " + e.getMessage());
				ok = false;
			} catch (InvalidColumnSpecException e) {
				System.out.println("The Column Spec is invalid:" + e.getMessage());
				ok = false;
			} catch (ConfigException e) {
				System.out.println("Configuration error:" + e.getMessage());
				ok = false;
			}  
		}
		
		if (ok) {
			
			try {
				readInputFile(metadataHeader, records);
			} catch (IOException e) {
				System.out.println("Error reading from file: " + e.getMessage());
				ok = false;
			}
		}
		
		if (ok) {
			//System.out.println("FILE: " + itsDataFilename);
			
			try {
				// Create the Sanity Checker and process the file
				OmeMetadata metadata = new OmeMetadata("");
				metadata.assignFromHeaderText(metadataHeader.toString());
				SanityChecker checker = new SanityChecker(itsDataFilename, metadata, colSpec, records, itsDateFormat);
				Output checkerOutput = checker.process();
				
				// Print summary
				//System.out.println("Output generated? " + checkerOutput.outputGenerated());
				
				if (checkerOutput.outputGenerated()) {
					//System.out.println("Metadata Items: " + checkerOutput.getMetadataCount());
					//System.out.println("Records: " + checkerOutput.getRecordCount());
				}
				
				Messages messages = checkerOutput.getMessages();
				if (messages != null) {
					try {
						writeMessages(messages);
					} catch (Exception e) {
						System.out.println("ERROR WRITING OUTPUT FILES");
						e.printStackTrace();
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				ok = false;
			}
		}
	}
	
	/**
	 * Writes a set of messages to an output file.
	 * @param messages The messages to be written
	 * @throws IOException If writing to the file fails.
	 */
	private void writeMessages(Messages messages) throws IOException, MessageException {
		
		PrintWriter writer = new PrintWriter(new File(itsOutputDir, getMessagesFilename()));
		for (MessageSummary summary : messages.getMessageSummaries()) {
			writer.println(summary.getSummaryString() + " - " + summary.getWarningCount() + " warnings, " + summary.getErrorCount() + " errors");
		}
		
		writer.print("\n\n\n");
		
		for (Message message : messages.getMessages()) {
			writer.println(message.getMessageString());
		}

		writer.close();
	}

	private String getMessagesFilename() {
		int underscoreIndex = itsDataFilename.lastIndexOf('_');
		int dotIndex = itsDataFilename.lastIndexOf('.');
		
		return itsDataFilename.substring(underscoreIndex + 1, dotIndex) + ".messages.txt";
	}
	
	/**
	 * Opens an input file and extracts the metadata and data records from it.
	 * @param metadata The object where the extracted metadata will be stored
	 * @param records The object where the extracted data records will be stored
	 * @throws FileNotFoundException If the input file (specified on the command line) doesn't exist
	 * @throws IOException If reading the input file fails.
	 */
	private void readInputFile(StringBuffer metadata, ArrayList<ArrayList<String>> records) throws FileNotFoundException, IOException {
		
		File inputFile = new File(itsInputDir, itsDataFilename);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(inputFile))));
		
		extractMetadata(reader, metadata);
		
		// The next line in the file is the col headers. While we don't want it,
		// we can use it to see if the file is CSV or TSV
		String splitChar = "\t";
		String headerLine = getNextLine(reader);
		String[] split = headerLine.split(",");
		if (split.length > 1) {
			splitChar = ",";
		}
		
		extractRecords(reader, records, splitChar);
	}

	/**
	 * Read records from the file into the required format for the Sanity Checker
	 * @param reader The input {@code Reader} object
	 * @param records The list of records
	 * @param splitChar The character that delimits fields on each line
	 * @throws IOException If an error occurs reading from the file.
	 */
	private void extractRecords(BufferedReader reader, ArrayList<ArrayList<String>> records, String splitChar) throws IOException {
		String line = getNextLine(reader);
		while (null != line) {
			String[] fields = line.split(splitChar);
			ArrayList<String> listFields = new ArrayList<String>(fields.length);
			for (int i = 0; i < fields.length; i++) {
				listFields.add(fields[i]);
			}
			
			records.add(listFields);
			
			line = getNextLine(reader);
		}
	}
	
	
	/**
	 * Extracts the metadata from the file header.
	 * @param reader The reader object for the data file
	 * @param metadata The properties object that will contain the metadata values
	 * @throws IOException If an error occurs while reading from the file
	 */
	private void extractMetadata(BufferedReader reader, StringBuffer metadata) throws IOException {

		// Read lines from the file until we find a slash-star on its own.
		String line = getNextLine(reader);
		if (!line.equalsIgnoreCase("/*")) {
			itsLogger.fatal("File does not begin with metadata section");
			metadata = null;
		} else {
			
			// Keep reading lines until we find a slash-star or fall off the end of the file
			boolean metadataEnded = false;
			
			while (!metadataEnded) {
				line = getNextLine(reader);
				if (null == line) {
					// We've fallen off the end of the file. This is bad.
					itsLogger.fatal("Metadata section has no end '*/'");
					metadata = null;
					metadataEnded = true;
				} else if (line.equalsIgnoreCase("*/")) {
					// We've found the end of the metadata header. We can stop now!
					metadataEnded = true;
				} else {
					metadata.append(line);
					metadata.append("\n");
				}
			}
		}
	}

	
	
	/**
	 * Read in the command line arguments
	 * @param args The command line arguments
	 */
	private void readCommandArgs(String[] args) {

        // Walk through all arguments
        int currentPos = 0;
        while (currentPos < args.length) {

        	// Input directory
        	if (args[currentPos].startsWith("-I")) {
            	itsInputDir = args[currentPos].substring(2);
        	// Output directory
        	} else if (args[currentPos].startsWith("-O")) {
                itsOutputDir = args[currentPos].substring(2);
        	// Column spec filename
        	} else if (args[currentPos].startsWith("-C")) {
                itsColSpecFilename = args[currentPos].substring(2);
            // Data Filename
            } else if (args[currentPos].startsWith("-F")) {
                itsDataFilename = args[currentPos].substring(2);
            } else if (args[currentPos].startsWith("-D")) {
            	itsDateFormat = args[currentPos].substring(2);
            }
            // Move to the next argument
            currentPos++;
        }
	}
	
	/**
	 * Check that the command line arguments are OK. Directories exist, that kind of thing.
	 * A false result indicates that we can't make any further progress.
	 * 
	 * @return {@code true} if they're fine; {@code false} otherwise.
	 */
	private boolean checkCommandArgs() {
		boolean result = true;
		
		// Check the input dir
		File inputDir = new File(itsInputDir);
		if (!inputDir.exists()) {
			System.out.println("Input directory '" + itsInputDir + "' doesn't exist");
			result = false;
		} else if (!inputDir.isDirectory()) {
			System.out.println("Input directory '" + itsInputDir + "' isn't a directory");
			result = false;
		}
		
		// Check the output dir
		File outputDir = new File(itsOutputDir);
		if (!outputDir.exists()) {
			System.out.println("Output directory '" + itsOutputDir + "' doesn't exist");
			result = false;
		} else if (!outputDir.isDirectory()) {
			System.out.println("Output directory '" + itsOutputDir + "' isn't a directory");
			result = false;
		} else if (!outputDir.canWrite()) {
			System.out.println("Output directroy '" + itsOutputDir + "' isn't writeable");
			result = false;
		}
		
		// Only do these checks if the others passed
		if (result) {
			// Check input file
			File inputFile = new File(itsInputDir, itsDataFilename);
			if (!inputFile.exists()) {
				System.out.println("Data file '" + itsDataFilename + "' doesn't exist");
				result = false;
			} else if (!inputFile.canRead()) {
				System.out.println("Data file '" + itsDataFilename + "' cannot be read");
				result = false;
			}

			// Check input file
			File colSpecFile = new File(itsInputDir, itsColSpecFilename);
			if (!colSpecFile.exists()) {
				System.out.println("Column Spec file '" + itsColSpecFilename + "' doesn't exist");
				result = false;
			} else if (!colSpecFile.canRead()) {
				System.out.println("Column Spec file '" + itsColSpecFilename + "' cannot be read");
				result = false;
			}
		}
		
		return result;
	}
	
	/**
	 * The main method. This does nothing except call {@link #SanityCheckerRun(String[])}.
	 * 
	 * There is optional (commented out) code to wait while a remote debugger/profiler is attached.
	 * @param args
	 */
	public static void main(String[] args) {
		
		// These lines should be uncommented when profiling.
		// Start the program, start & attach the profiler, then press ENTER.
		//
		// System.out.print("ENTER when ready... ");
		// new Scanner(System.in).nextLine();
		
		new SanityCheckerRun(args);
	}	

	/**
	 * Read a line from a file, ignoring blank lines and commented lines.
	 * @param reader The file reader object
	 * @return The next non-blank, non-comment line, or {@code null} if the end of the file is reached.
	 * @throws IOException If an error occurs while reading from the file
	 */
	private String getNextLine(BufferedReader reader) throws IOException {
		String nextLine = null;
		boolean foundLine = false;
		
		while (!foundLine) {
			nextLine = reader.readLine();
			if (null == nextLine) {
				// We've reached the end of the file. The calling function must deal with that.
				foundLine = true;
			} else {
				
				// Empty lines are ignored
				nextLine = nextLine.trim();
				if (nextLine.length() > 0) {
					
					// See if there's a comment indicator.
					// If it's at the start of the line, we ignore the whole line.
					// Otherwise just strip it off and return the rest of the line.
					int commentIndex = nextLine.indexOf("//");
					
					if (commentIndex > 0) {
						nextLine = nextLine.substring(0, commentIndex).trim();
						foundLine = true;
					} else if (commentIndex == -1) {
						foundLine = true;
					}
				}
			}
		}
		
		return nextLine;
	}


}

