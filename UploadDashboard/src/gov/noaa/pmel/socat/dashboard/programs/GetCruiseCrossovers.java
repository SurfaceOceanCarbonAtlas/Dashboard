/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.actions.CrossoverChecker;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.socat.dashboard.shared.SocatCrossover;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
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

		DashboardConfigStore configStore = null;
		try {
			configStore = DashboardConfigStore.get();
		} catch (Exception ex) {
			System.err.println("Problems obtaining the default dashboard " +
					"configuration: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		DsgNcFileHandler dsgHandler = configStore.getDsgNcFileHandler();

		long startTime = System.currentTimeMillis();

		// Get the QC flags for the cruises from the DSG files
		double timeDiff = (System.currentTimeMillis() - startTime) / (60.0 * 1000.0);
		System.err.format("%.2fm - getting QC flags for the cruises\n", timeDiff);
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

		// Get the time and latitude limits for all the cruises in the list
		// in order to narrow down the cruises to examine for crossovers
		TreeMap<String,double[]> cruiseTimeMinMaxMap = new TreeMap<String,double[]>();
		TreeMap<String,double[]> cruiseLatMinMaxMap = new TreeMap<String,double[]>();
		for ( String expo : cruiseFlagsMap.keySet() ) {
			timeDiff = (System.currentTimeMillis() - startTime) / (60.0 * 1000.0);
			System.err.format("%.2fm - getting data limits for %s\n", timeDiff, expo);
			double[][] dataVals = null;
			try {
				dataVals = dsgHandler.readLonLatTimeDataValues(expo);
			} catch ( Exception ex ) {
				System.err.println("Unexpected error rereading " + expo + ": " + ex.getMessage());
				System.exit(1);
			}
			double[] timeMinMaxVals = CrossoverChecker.getMinMaxValidData(dataVals[2]);
			if ( (timeMinMaxVals[0] == SocatCruiseData.FP_MISSING_VALUE) ||
				 (timeMinMaxVals[1] == SocatCruiseData.FP_MISSING_VALUE) ) {
				System.err.println("No valid times for " + expo);
				System.exit(1);
			}
			cruiseTimeMinMaxMap.put(expo, timeMinMaxVals);
			double[] latMinMaxVals = CrossoverChecker.getMinMaxValidData(dataVals[1]);
			if ( (latMinMaxVals[0] == SocatCruiseData.FP_MISSING_VALUE) ||
				 (latMinMaxVals[1] == SocatCruiseData.FP_MISSING_VALUE) ) {
				System.err.println("No valid latitudes for " + expo);
				System.exit(1);
			}
			cruiseLatMinMaxMap.put(expo, latMinMaxVals);
		}

		CrossoverChecker crossChecker = new CrossoverChecker(configStore.getDsgNcFileHandler());
		TreeMap<String,SocatCrossover> crossoversMap = new TreeMap<String,SocatCrossover>();
		for ( String firstExpo : cruiseFlagsMap.keySet() ) {
			double[] firstTimeMinMax = cruiseTimeMinMaxMap.get(firstExpo);
			double[] firstLatMinMax = cruiseLatMinMaxMap.get(firstExpo);
			// Get the list of possibly-crossing cruises to check
			TreeSet<String> checkExpos = new TreeSet<String>();
			for ( String secondExpo : cruiseFlagsMap.keySet() ) {
				// Only those cruise preceding this one so not doing two checks on a pair
				if ( secondExpo.equals(firstExpo) )
					break;
				// Must be different instrument - different NODC code
				if ( firstExpo.substring(0,4).equals(secondExpo.substring(0,4)) )
					continue;
				// One of the cruises must be from the report set
				if ( ! ( reportFlagsSet.contains( cruiseFlagsMap.get(firstExpo) ) ||
						 reportFlagsSet.contains( cruiseFlagsMap.get(secondExpo) ) ) )
					continue;
				// Check that there is some overlap in time
				double[] secondTimeMinMax = cruiseTimeMinMaxMap.get(secondExpo);
				if ( (firstTimeMinMax[1] + SocatCrossover.MAX_TIME_DIFF < secondTimeMinMax[0]) ||
					 (secondTimeMinMax[1] + SocatCrossover.MAX_TIME_DIFF < firstTimeMinMax[0]) )
					continue;
				// Check that there is some overlap in latitude
				double[] secondLatMinMax = cruiseLatMinMaxMap.get(secondExpo);
				if ( (firstLatMinMax[1] + SocatCrossover.MAX_TIME_DIFF < secondLatMinMax[0]) ||
					 (secondLatMinMax[1] + SocatCrossover.MAX_TIME_DIFF < firstLatMinMax[0]) )
						continue;
				checkExpos.add(secondExpo);
			}
			// Find any crossovers with this cruise with the selected set of cruises
			if ( checkExpos.size() > 0 ) {
				try {
					ArrayList<SocatCrossover> crossList = 
							crossChecker.getCrossovers(firstExpo, checkExpos, System.err, startTime);
					for ( SocatCrossover cross : crossList ) {
						String[] expos = cross.getExpocodes();
						Long[] cruiseMinTimes = new Long[2];
						Long[] cruiseMaxTimes = new Long[2];
						for (int q = 0; q < 2; q++) {
							double[] timeMinMax = cruiseTimeMinMaxMap.get(expos[q]);
							cruiseMinTimes[q] = Math.round(timeMinMax[0]);
							cruiseMaxTimes[q] = Math.round(timeMinMax[1]);
						}
						cross.setCruiseMinTimes(cruiseMinTimes);
						cross.setCruiseMaxTimes(cruiseMaxTimes);
						crossoversMap.put(expos[0] + " and " + expos[1], cross);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					System.exit(1);
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
