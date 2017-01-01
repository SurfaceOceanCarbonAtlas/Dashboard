/**
 * 
 */
package gov.noaa.pmel.dashboard.dsg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.datatype.ValueConverter;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.ADCMessage;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

/**
 * A 2-D array of objects corresponding to the standardized values of string values 
 * provided by the user.  Also contains 1-D arrays of information describing each 
 * data column.
 * 
 * @author Karl Smith
 */
public class StdUserDataArray extends StdDataArray {

	public static final String INCONSISTENT_NUMBER_OF_DATA_VALUES_MSG = 
			"inconstistent number of data values";

	private String[] userColNames;
	private String[] userUnits;
	private String[] userMissVals;
	private Boolean[] standardized;

	/**
	 * Create and assign the 1-D arrays of data column information (type, input unit, input 
	 * missing value) from the given data column descriptions.  The 2-D array of standard
	 * data objects is not created until {@link #standardizeData(ArrayList)} is called.
	 * 
	 * @param userColumnNames
	 * 		user's name for the data columns
	 * @param dataColumnTypes
	 * 		user's description of the data columns in each sample
	 * @param knownTypes
	 * 		all known user data types
	 * @throws IllegalArgumentException
	 * 		if there are no user column names,
	 * 		if a user column name is null, 
	 * 		if the number of user column names and 
	 * 			number of user data column descriptions are not the same, 
	 * 		if there are no known user data types, 
	 * 		if a data column description is not a known user data type
	 */
	public StdUserDataArray(List<String> userColumnNames, List<DataColumnType> dataColumnTypes, 
			KnownDataTypes knownTypes) throws IllegalArgumentException {
		super(dataColumnTypes, knownTypes);
		if ( (userColumnNames == null) || userColumnNames.isEmpty() )
			throw new IllegalArgumentException("no user data column names given");
		if ( userColumnNames.size() != numDataCols )
			throw new IllegalArgumentException("different number of data column names (" + 
					userColumnNames.size() + ") and types (" +  numDataCols + ")");
		userColNames = new String[numDataCols];
		userUnits = new String[numDataCols];
		userMissVals = new String[numDataCols];

		for (int k = 0; k < numDataCols; k++) {
			userColNames[k] = userColumnNames.get(k);
			if ( userColNames[k] == null )
				throw new IllegalArgumentException("missing user data column name");
			DataColumnType dataColType = dataColumnTypes.get(k);
			userUnits[k] = dataColType.getUnits().get(dataColType.getSelectedUnitIndex());
			if ( DashboardUtils.STRING_MISSING_VALUE.equals(userUnits[k]) )
				userUnits[k] = null;
			userMissVals[k] = dataColType.getSelectedMissingValue();
			if ( DashboardUtils.STRING_MISSING_VALUE.equals(userMissVals[k]) )
				userMissVals[k] = null;
		}
		standardized = new Boolean[numDataCols];
		for (int k = 0; k < numDataCols; k++)
			standardized[k] = null;
	}

	/**
	 * Create and assign the 2-D array of standard objects by interpreting the 
	 * list of lists of strings representations of these objects using data column 
	 * information provided in the constructor.  The list of lists of strings is 
	 * arranged such that each inner list gives each data column value for a 
	 * particular sample, and the outer list iterates through each sample.  
	 * <br /><br />
	 * Any data columns types matching {@link DashboardServerUtils#UNKNOWN} or 
	 * {@link DashboardServerUtils#OTHER} are ignored; {@link #getStdVal(int, int)} 
	 * will throw an IllegalArgumentException if a standard value is requested 
	 * from such a data column.
	 * <br /><br />
	 * No bounds checking of standardized data values is performed.
	 * 
	 * @param dataVals
	 * 		a list of list of data value strings where dataVals.get(j).get(k) is 
	 * 		the value of the k-th data column for the j-th sample.
	 * @return
	 * 		a list of automated data check messages describing problems (critical 
	 * 		errors) encountered when standardizing the data; never null but may 
	 * 		be empty.
	 * @throws IllegalArgumentException
	 * 		if there are no data samples (outer list is empty),
	 * 		if a required unit conversion is not supported, or
	 * 		if a standardizer for a given data type is not known
	 */
	public ArrayList<ADCMessage> standardizeData(ArrayList<ArrayList<String>> dataVals) 
													throws IllegalArgumentException {
		// Create the 2-D array 
		if ( dataVals.isEmpty() )
			throw new IllegalArgumentException("no data values given");
		numSamples = dataVals.size();
		stdObjects = new Object[numSamples][numDataCols];
		ArrayList<ADCMessage> msgList = new ArrayList<ADCMessage>();
		// Create a 2-D array of these Strings for efficiency
		String[][] strDataVals = new String[numSamples][numDataCols];
		for (int j = 0; j < numSamples; j++) {
			ArrayList<String> rowVals = dataVals.get(j);
			if ( rowVals.size() != numDataCols ) {
				// Generate a general message for this row - in case too long
				ADCMessage msg = new ADCMessage();
				msg.setSeverity(ADCMessage.SCMsgSeverity.CRITICAL);
				msg.setRowNumber(j+1);
				msg.setGeneralComment(INCONSISTENT_NUMBER_OF_DATA_VALUES_MSG);
				msg.setDetailedComment(INCONSISTENT_NUMBER_OF_DATA_VALUES_MSG + "; " + 
						numDataCols + " expected but " + rowVals.size() + " found");
				msgList.add(msg);
				// Continue on, assuming the missing values are at the end
			}
			for (int k = 0; k < numDataCols; k++) {
				try {
					strDataVals[j][k] = rowVals.get(k);
				} catch ( IndexOutOfBoundsException ex ) {
					// Setting it to null will generate a "no value given" message
					strDataVals[j][k] = null;
				}
			}
		}
		// Standardize data columns that do not require values from other data columns
		boolean needsAnotherPass;
		do {
			needsAnotherPass = false;
			for (int k = 0; k < numDataCols; k++) {
				DashDataType<?> colType = dataTypes[k];
				if ( DashboardServerUtils.UNKNOWN.typeNameEquals(colType) ||
						DashboardServerUtils.OTHER.typeNameEquals(colType) ) {
					for (int j = 0; j < numSamples; j++) {
						stdObjects[j][k] = null;
					}
					standardized[k] = null;
				}
				else {
					try {
						ValueConverter<?> stdizer = colType.getStandardizer(userUnits[k], userMissVals[k], this);
						for (int j = 0; j < numSamples; j++) {
							try {
								stdObjects[j][k] = stdizer.convertValueOf(strDataVals[j][k]);
							} catch ( IllegalArgumentException ex ) {
								stdObjects[j][k] = null;
								ADCMessage msg = new ADCMessage();
								msg.setSeverity(ADCMessage.SCMsgSeverity.CRITICAL);
								msg.setRowNumber(j+1);
								msg.setColNumber(k+1);
								msg.setColName(userColNames[k]);
								msg.setGeneralComment(ex.getMessage());
								if ( strDataVals[j][k] == null )
									msg.setDetailedComment(ex.getMessage());
								else
									msg.setDetailedComment(ex.getMessage() + ": \"" + strDataVals[j][k] + "\"");
								msgList.add(msg);
							}
						}
						standardized[k] = true;
					} catch ( IllegalStateException ex ) {
						standardized[k] = false;
						needsAnotherPass = true;
					}
				}
			}
		} while ( needsAnotherPass );

		return msgList;
	}

	/**
	 * Determines is this data column is an appropriate index.
	 * Checks that the value is in the appropriate range and 
	 * that the column with this index has been standardized.
	 * 
	 * @param idx
	 * 		index to test
	 * @return
	 * 		if the index is valid
	 */
	@Override
	public boolean isUsableIndex(int idx) {
		if ( idx < 0 )
			return false;
		if ( idx >= numDataCols )
			return false;
		return Boolean.TRUE.equals(standardized[idx]);
	}

	/**
	 * Get the standard value object for the specified value (column index) 
	 * of the specified sample (row index).
	 * 
	 * @param sampleIdx
	 * 		index of the sample (row)
	 * @param columnIdx
	 * 		index of the data column
	 * @return
	 * 		standard value object; null is returned for "missing value" or
	 * 		values that could not be interpreted
	 * @throws IndexOutOfBoundsException
	 * 		if either the sample index or the column index is invalid
	 * @throws IllegalArgumentException 
	 * 		if the value cannot be standardized
	 * @throws IllegalStateException 
	 * 		if the value has not been standardized
	 */
	@Override
	public Object getStdVal(int sampleIdx, int columnIdx) 
			throws IndexOutOfBoundsException, IllegalArgumentException, IllegalStateException {
		if ( (sampleIdx < 0) || (sampleIdx >= numSamples) )
			throw new IndexOutOfBoundsException("sample index is invalid: " + sampleIdx);
		if ( (columnIdx < 0) || (columnIdx >= numDataCols) )
			throw new IndexOutOfBoundsException("data column index is invalid: " + columnIdx);
		if ( standardized[columnIdx] == null )
			throw new IllegalArgumentException("value cannot be standardized");
		if ( ! standardized[columnIdx] )
			throw new IllegalStateException("value has not been standardized");
		return stdObjects[sampleIdx][columnIdx];
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(standardized);
		result = prime * result + Arrays.hashCode(userMissVals);
		result = prime * result + Arrays.hashCode(userUnits);
		result = prime * result + Arrays.hashCode(userColNames);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( ! super.equals(obj) )
			return false;

		if ( ! ( obj instanceof StdUserDataArray ) )
			return false;
		StdUserDataArray other = (StdUserDataArray) obj;

		if ( ! Arrays.equals(standardized, other.standardized) )
			return false;
		if ( ! Arrays.equals(userColNames, other.userColNames) )
			return false;
		if ( ! Arrays.equals(userMissVals, other.userMissVals) )
			return false;
		if ( ! Arrays.equals(userUnits, other.userUnits) )
			return false;

		return true;
	}

	@Override
	public String toString() {
		String repr = "StdUserDataArray[numSamples=" + numSamples + ", numDataCols=" + numDataCols;
		repr += ",\n  userColNames=" + Arrays.toString(userColNames);
		repr += ",\n  userUnits=" + Arrays.toString(userUnits);
		repr += ",\n  userMissVals=" + Arrays.toString(userMissVals);
		repr += ",\n  standardized=" + Arrays.toString(standardized);
		String superRepr = super.toString();
		int idx = superRepr.indexOf('\n');
		repr += "," + superRepr.substring(idx);
		return repr;
	}

}
