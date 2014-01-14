package uk.ac.uea.socat.sanitychecker;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import uk.ac.uea.socat.sanitychecker.config.BaseConfig;
import uk.ac.uea.socat.sanitychecker.config.ColumnConversionConfig;
import uk.ac.uea.socat.sanitychecker.config.ConfigException;
import uk.ac.uea.socat.sanitychecker.data.ColumnSpec;
import uk.ac.uea.socat.sanitychecker.data.InvalidColumnSpecException;

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
	 * The output date format
	 */
	private static final String DATE_FORMAT = "YYYY-MM-DD";

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
	 * Base config
	 */
	private BaseConfig itsBaseConfig = null;
	
	/**
	 * Logger object
	 */
	private Logger itsLogger = null;
	
	/**
	 * Tracker for the file line number
	 */
	private int itsCurrentLine = 0;
	
	/**
	 * Sanity checks the file whose details were passed in on
	 * the command line
	 * @param args The command line arguments
	 */
	private SanityCheckerRun(String[] args) {
		
		System.out.println("Preprocessing");
		
		// Extract what we need from the data file
		ColumnSpec colSpec;
		Properties metadata = new Properties();
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
				BaseConfig.init(BASE_CONFIG_LOCATION, itsLogger);
				itsBaseConfig = BaseConfig.getInstance();

				ColumnConversionConfig.init(itsBaseConfig.getColumnConversionConfigFile(), itsLogger);
				colSpec = ColumnSpec.importSpec(new File(itsInputDir, itsColSpecFilename), new File(itsBaseConfig.getColumnSpecSchemaFile()), ColumnConversionConfig.getInstance(), itsLogger);
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
				
				readInputFile(metadata, records);
			} catch (IOException e) {
				System.out.println("Error reading from file: " + e.getMessage());
				ok = false;
			}
		}
		
		if (ok) {
			System.out.println("Calling Sanity Checker");
		}
	}
	
	
	private void readInputFile(Properties metadata, ArrayList<ArrayList<String>> records) throws FileNotFoundException, IOException {
		
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
	 * @param reader
	 * @param records
	 * @param splitChar
	 * @throws IOException
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
	private void extractMetadata(BufferedReader reader, Properties metadata) throws IOException {

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
					// Find the = sign, and split the line into name=value
					int equalsPos = line.indexOf("=");
					
					// Make sure the equals sign is in a sensible place. We allow the equals at the end of the line
					// to signify an empty value, which may be possible. The metadata checker routines will decide about that later.
					if (equalsPos == -1 || equalsPos == 0) {
						itsLogger.trace("Malformed metadata line (" + itsCurrentLine + ")");
					} else {
					
						String name = line.substring(0, equalsPos).trim();
						String value = line.substring(equalsPos + 1).trim();
						
						metadata.put(name, value);
					}
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
            } else if (args[currentPos].startsWith("-D")) {
                itsDataFilename = args[currentPos].substring(2);
            }
            // Move to the next argument
            currentPos++;
        }
	}
	
	/**
	 * Check that the command line arguments are OK. Directories exist, that kind of thing.
	 * A false result indicates that we can't make any further progress.
	 * 
	 * @return @code{true} if they're fine; @code{false} otherwise.
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
			itsCurrentLine++;
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

