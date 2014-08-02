/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DataColumnType;

import java.util.ArrayList;
import java.util.HashSet;

import uk.ac.uea.socat.sanitychecker.Message;
import uk.ac.uea.socat.sanitychecker.Output;

/**
 * Processes SanityChecker messages for a cruise.
 * 
 * @author Karl Smith
 */
public class CheckerMessageHandler {

	DashboardCruiseWithData cruiseData;
	Output checkerOutput;

	/**
	 * Create a SanityChecker message handler for this DashboardCruiseWithData.
	 * This DashboardCruise object is saved and will be directly modified
	 * by methods in this class.
	 *  
	 * @param cruise
	 * 		create a SanityChecker message handler for this DashboardCruise
	 */
	public CheckerMessageHandler(DashboardCruiseWithData cruiseData, Output checkerOutput) {
		if ( (cruiseData == null) || (checkerOutput == null) )
			throw new IllegalArgumentException("CheckerMessageHandler " +
					"cannot be initialized with a null arguments");
		DashboardServerUtils.checkExpocode(cruiseData.getExpocode());
		this.cruiseData = cruiseData;
		this.checkerOutput = checkerOutput;
	}

	/**
	 * Clears and assigns the WOCE-3 or WOCE-4 flags associated with the 
	 * SanityChecker output for the cruise as well as any user-provided 
	 * WOCE flags.  A row index may appear in multiple WOCE sets, including 
	 * both WOCE-3 and WOCE-4 sets.
	 */
	public void assignWoceFlags() {
		// Directly modify the sets in the cruise data
		ArrayList<HashSet<Integer>> woceFourSets = cruiseData.getWoceFourRowIndices();
		ArrayList<HashSet<Integer>> woceThreeSets = cruiseData.getWoceThreeRowIndices();
		HashSet<Integer> noColumnWoceFourSet = cruiseData.getNoColumnWoceFourRowIndices();
		HashSet<Integer> noColumnWoceThreeSet = cruiseData.getNoColumnWoceThreeRowIndices();
		HashSet<Integer> userWoceFourSet = cruiseData.getUserWoceFourRowIndices();
		HashSet<Integer> userWoceThreeSet = cruiseData.getUserWoceThreeRowIndices();

		// Clear all WOCE flag sets
		for ( HashSet<Integer> rowIdxSet : woceFourSets )
			rowIdxSet.clear();
		for ( HashSet<Integer> rowIdxSet : woceThreeSets )
			rowIdxSet.clear();
		noColumnWoceFourSet.clear();
		noColumnWoceThreeSet.clear();
		userWoceFourSet.clear();
		userWoceThreeSet.clear();

		// Assign WOCE flags from the SanityChecker output
		for ( Message msg : checkerOutput.getMessages().getMessages() ) {
			int rowIdx = msg.getLineNumber();
			if ( (rowIdx <= 0) || (rowIdx > cruiseData.getNumDataRows()) )
				throw new RuntimeException("Unexpected row number of " + 
						Integer.toString(rowIdx) + " in the sanity checker message\n" +
						"    " + msg.toString());
			// Change row number to row index
			rowIdx--;

			int colIdx = msg.getInputItemIndex();
			if ( (colIdx == 0) || (colIdx > cruiseData.getDataColTypes().size()) )
				throw new RuntimeException("Unexpected input column number of " + 
						Integer.toString(colIdx) + " in the sanity checker message\n" +
						"    " + msg.toString());
			// Change column number to column index; 
			// negative numbers indicate an ambiguous source of error
			if ( colIdx > 0 )
				colIdx--;

			if ( msg.isError() ) {
				if ( colIdx < 0 )
					noColumnWoceFourSet.add(rowIdx);
				else
					woceFourSets.get(colIdx).add(rowIdx);
			}
			else if ( msg.isWarning() ) {
				if ( colIdx < 0 )
					noColumnWoceThreeSet.add(rowIdx);
				else
					woceThreeSets.get(colIdx).add(rowIdx);
			}
			else {
				// Should never happen
				throw new RuntimeException(
						"Unexpected message that is neither an error nor a warning:\n" +
						"    " + msg.toString());
			}
		}

		// Assign any user-provided WOCE-3 and WOCE-4 flags
		ArrayList<DataColumnType> columnTypes = cruiseData.getDataColTypes();
		for (int k = 0; k < columnTypes.size(); k++) {
			DataColumnType colType = columnTypes.get(k);
			if ( ! ( colType.equals(DataColumnType.WOCE_CO2_WATER) ||
					 colType.equals(DataColumnType.WOCE_CO2_ATM) ) )
				continue;
			for (int rowIdx = 0; rowIdx < cruiseData.getNumDataRows(); rowIdx++) {
				try {
					int value = Integer.parseInt(cruiseData.getDataValues().get(rowIdx).get(k));
					if ( value == 4 )
						userWoceFourSet.add(rowIdx);
					else if ( value == 3 )
						userWoceThreeSet.add(rowIdx);
					// Only handle 3 and 4
				} catch (NumberFormatException ex) {
					// Assuming a missing value
				}
			}
		}
	}

}
