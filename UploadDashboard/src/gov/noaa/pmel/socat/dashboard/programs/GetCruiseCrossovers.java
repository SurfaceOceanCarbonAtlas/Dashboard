/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.actions.CrossoverChecker;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;
import gov.noaa.pmel.socat.dashboard.shared.SocatCrossover;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Finds high-quality crossovers within sets of cruises
 * 
 * @author Karl Smith
 */
public class GetCruiseCrossovers {

	/** QC flags of cruises to report any crossovers */
	static final TreeSet<Character> reportFlagsSet = new TreeSet<Character>(
			Arrays.asList(
					SocatQCEvent.QC_A_FLAG, 
					SocatQCEvent.QC_B_FLAG,
					SocatQCEvent.QC_C_FLAG,
					SocatQCEvent.QC_D_FLAG,
					SocatQCEvent.QC_E_FLAG,
					SocatQCEvent.QC_NEW_FLAG,
					SocatQCEvent.QC_CONFLICT_FLAG,
					SocatQCEvent.QC_UPDATED_FLAG ) );

	/** QC flags of cruises that can be involved in crossovers */
	static final TreeSet<Character> acceptableFlagsSet = new TreeSet<Character>(
			Arrays.asList(
					SocatQCEvent.QC_A_FLAG, 
					SocatQCEvent.QC_B_FLAG,
					SocatQCEvent.QC_C_FLAG,
					SocatQCEvent.QC_D_FLAG,
					SocatQCEvent.QC_E_FLAG,
					SocatQCEvent.QC_NEW_FLAG,
					SocatQCEvent.QC_CONFLICT_FLAG,
					SocatQCEvent.QC_UPDATED_FLAG ) );

	/**
	 * @param args
	 * 		ExpocodesFile - a file containing expocodes of the set of cruises 
	 * 		to examine for high-quality crossovers
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("Arguments:  ExpocodesFile");
			System.err.println();
			System.err.println("ExpocodesFile");
			System.err.println("    is a file containing expocodes, one per line, of the set of cruises ");
			System.err.println("    to examine for high-quality crossovers. ");
			System.err.println();
			System.exit(1);
		}
		String exposFilename = args[0];

		TreeSet<String> givenExpocodes = new TreeSet<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(exposFilename));
			try {
				String dataline = reader.readLine();
				while ( dataline != null ) {
					dataline = dataline.trim().toUpperCase();
					if ( ! dataline.isEmpty() )
						givenExpocodes.add(dataline);
					dataline = reader.readLine();
				}
			} finally {
				reader.close();
			}
		} catch (Exception ex) {
			System.err.println("Problems reading the file of expocodes '" + 
					exposFilename + "': " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}

		DashboardDataStore dataStore = null;
		try {
			dataStore = DashboardDataStore.get();
		} catch (Exception ex) {
			System.err.println("Problems obtaining the default dashboard " +
					"configuration: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		DsgNcFileHandler dsgHandler = dataStore.getDsgNcFileHandler();

		// Get the QC flags for the cruises from the DSG files
		TreeMap<String,Character> cruiseFlagsMap = new TreeMap<String,Character>();
		for ( String expo : givenExpocodes ) {
			try {
				Character qcFlag = dsgHandler.getQCFlag(expo);
				if ( acceptableFlagsSet.contains(qcFlag) ) {
					cruiseFlagsMap.put(expo, qcFlag);
				}
				else {
					throw new Exception("QC flag is " + qcFlag);
				}
			} catch ( Exception ex ) {
				System.err.println("Problems with expocode " + expo + ": " + ex.getMessage());
			}
		}

		CrossoverChecker crossChecker = new CrossoverChecker(dataStore.getDsgNcFileHandler());
		long startTime = System.currentTimeMillis();
		Double timeDiff;
		TreeMap<String,SocatCrossover> crossoversMap = new TreeMap<String,SocatCrossover>();
		for ( String firstExpo : cruiseFlagsMap.keySet() ) {
			for ( String secondExpo : cruiseFlagsMap.keySet() ) {
				if ( secondExpo.equals(firstExpo) )
					break;
				if ( ! ( reportFlagsSet.contains( cruiseFlagsMap.get(firstExpo) ) ||
						 reportFlagsSet.contains( cruiseFlagsMap.get(secondExpo) ) ) )
					continue;
				timeDiff = (System.currentTimeMillis() - startTime) / (1000.0 * 60.0);
				System.err.format("%.1fm - examining %s and %s: ", timeDiff, firstExpo, secondExpo);
				System.err.flush();
				try {
					SocatCrossover cross = crossChecker.checkForCrossover(new String[] {firstExpo, secondExpo});
					timeDiff = 60.0 * ( (System.currentTimeMillis() - startTime) / (1000.0 * 60.0) - timeDiff );
					if ( cross != null ) {
						System.err.format("%.1fs - crossover found: %s\n", timeDiff * 60.0, cross.toString());
						crossoversMap.put(firstExpo + " and " + secondExpo, cross);
					}
					else {
						System.err.format("%.1fs - no crossover\n", timeDiff);
					}
				} catch (Exception ex) {
					System.err.println("problems: " + ex.getMessage());
				}
			}
		}

		// Report crossovers found for each QC flag to report on
		TreeSet<String> reportStrings = new TreeSet<String>();
		for ( Character reportFlag : reportFlagsSet ) {
			for ( String expocodePair : crossoversMap.keySet() ) {
				String[] expocodes = expocodePair.split(" and ");
				Character[] qcFlags = new Character[] { cruiseFlagsMap.get(expocodes[0]), 
														cruiseFlagsMap.get(expocodes[1]) };
				if ( reportFlag.equals(qcFlags[0]) ) {
					reportStrings.add( "    " + 
							expocodes[0] + " (" + qcFlags[0] + ") high-quality cross-over with " +
							expocodes[1] + " (" + qcFlags[1] + ")" );
				}
				if ( reportFlag.equals(qcFlags[1]) ) {
					reportStrings.add( "    " + 
							expocodes[1] + " (" + qcFlags[1] + ") high-quality cross-over with " +
							expocodes[0] + " (" + qcFlags[0] + ")" );
				}
			}

			System.out.println(Integer.toString(reportStrings.size()) + 
					" crossovers of cruises with a QC flag of " + reportFlag + ": ");
			for ( String report : reportStrings )
				System.out.println(report);
			System.out.println();
			reportStrings.clear();
		}

		// Check for 'A' cruises without high-quality crossovers
		for ( String expo : cruiseFlagsMap.keySet() ) {
			if ( ! SocatQCEvent.QC_A_FLAG.equals( cruiseFlagsMap.get(expo) ) )
				continue;
			boolean found = false;
			for ( String expocodePair : crossoversMap.keySet() ) {
				String[] expocodes = expocodePair.split(" and ");
				if ( expo.equals(expocodes[0]) || expo.equals(expocodes[1]) ) {
					found = true;
					break;
				}
			}
			if ( ! found ) {
				reportStrings.add("    " + expo);
			}
		}
		System.out.println(Integer.toString(reportStrings.size()) + 
				" cruises with a QC flag of " + SocatQCEvent.QC_A_FLAG + 
				" with no high-quality crossovers: ");
		for ( String report : reportStrings )
			System.out.println(report);
		System.out.println();

		System.exit(0);
	}

}
