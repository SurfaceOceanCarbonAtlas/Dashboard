/**
 * 
 */
package gov.noaa.pmel.dashboard.datatype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.ADCMessage;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

/**
 * A 2-D array of objects corresponding to the standardized values in a dataset, 
 * as well as 1-D arrays of information describing each data column.
 * 
 * @author Karl Smith
 */
public class StdDataArray {

	public static final String INCONSISTENT_NUMBER_OF_DATA_VALUES_MSG = 
			"inconstistent number of data values";

	private int numSamples;
	private int numDataCols;
	private String[] userColNames;
	private DashDataType<?>[] dataTypes;
	private String[] userUnits;
	private String[] userMissVals;
	private Boolean[] standardized;
	private Object[][] stdObjects;

	/**
	 * Create and assign the 1-D arrays of data column information (type, input unit, input 
	 * missing value) from the given data column descriptions.  The 2-D array of standard
	 * data objects is not created until {@link #standardizeData(ArrayList)} is called.
	 * 
	 * @param userColumnNames
	 * 		user's name for the data columns
	 * @param dataColumnTypes
	 * 		description of the data columns in each sample
	 * @param knownTypes
	 * 		all known (user-provided) data types
	 * @throws IllegalArgumentException
	 * 		if the specified number of samples is not positive,
	 * 		if dataColumnTypes is empty, or
	 * 		if a data column descriptions is not a known data type
	 */
	public StdDataArray(ArrayList<String> userColumnNames, ArrayList<DataColumnType> dataColumnTypes, 
			KnownDataTypes knownTypes) throws IllegalArgumentException {
		if ( (dataColumnTypes == null) || dataColumnTypes.isEmpty() )
			throw new IllegalArgumentException("no data column types given");
		numDataCols = dataColumnTypes.size();
		if ( userColumnNames.size() != numDataCols )
			throw new IllegalArgumentException("Different number of data column names (" + 
					userColumnNames.size() + ") and types (" +  numDataCols + ")");
		userColNames = new String[numDataCols];
		dataTypes = new DashDataType<?>[numDataCols];
		userUnits = new String[numDataCols];
		userMissVals = new String[numDataCols];
		for (int k = 0; k < numDataCols; k++) {
			userColNames[k] = userColumnNames.get(k);
			DataColumnType dataColType = dataColumnTypes.get(k);
			dataTypes[k] = knownTypes.getDataType(dataColType);
			if ( dataTypes[k] == null )
				throw new IllegalArgumentException("unknown data column type: " + 
						dataColType.getDisplayName());
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
		numSamples = 0;
		stdObjects = null;
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
	 * @return
	 * 		an unmodifiable list of types for the data columns.
	 */
	public List<DashDataType<?>> getDataTypes() {
		return Collections.unmodifiableList(Arrays.asList(dataTypes));
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
		int result = 0;
		for (int j = 0; j < numSamples; j++) {
			for (int k = 0; k < numDataCols; k++) {
				result *= prime;
				if ( stdObjects[j][k] != null )
					result += stdObjects[j][k].hashCode(); 
			}
		}
		for (int k = 0; k < numDataCols; k++) {
			result *= prime;
			if ( standardized[k] != null )
				result += standardized[k].hashCode();
		}
		for (int k = 0; k < numDataCols; k++) {
			result *= prime;
			if ( userMissVals[k] != null )
				result += userMissVals[k].hashCode();
		}
		for (int k = 0; k < numDataCols; k++) {
			result *= prime;
			if ( userUnits[k] != null )
				result += userUnits[k].hashCode();
		}
		for (int k = 0; k < numDataCols; k++) {
			result *= prime;
			if ( dataTypes[k] != null )
				result += dataTypes[k].hashCode();
		}
		for (int k = 0; k < numDataCols; k++) {
			result *= prime;
			if ( userColNames[k] != null )
				result += userColNames[k].hashCode();
		}
		result = prime * result + numDataCols;
		result = prime * result + numSamples;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if ( ! (obj instanceof StdDataArray) ) {
			return false;
		}
		StdDataArray other = (StdDataArray) obj;
		if (numSamples != other.numSamples) {
			return false;
		}
		if (numDataCols != other.numDataCols) {
			return false;
		}
		for (int k = 0; k < numDataCols; k++) {
			if ( userColNames[k] == null ) {
				if ( other.userColNames[k] != null )
					return false;
			}
			else {
				if ( ! userColNames[k].equals(other.userColNames[k]) )
					return false;
			}
		}
		for (int k = 0; k < numDataCols; k++) {
			if ( dataTypes[k] == null ) {
				if ( other.dataTypes[k] != null )
					return false;
			}
			else {
				if ( ! dataTypes[k].equals(other.dataTypes[k]) )
					return false;
			}
		}
		for (int k = 0; k < numDataCols; k++) {
			if ( userUnits[k] == null ) {
				if ( other.userUnits[k] != null )
					return false;
			}
			else {
				if ( ! userUnits[k].equals(other.userUnits[k]) )
					return false;
			}
		}
		for (int k = 0; k < numDataCols; k++) {
			if ( userMissVals[k] == null ) {
				if ( other.userMissVals[k] != null )
					return false;
			}
			else {
				if ( ! userMissVals[k].equals(other.userMissVals[k]) )
					return false;
			}
		}
		for (int k = 0; k < numDataCols; k++) {
			if ( standardized[k] == null ) {
				if ( other.standardized[k] != null )
					return false;
			}
			else {
				if ( ! standardized[k].equals(other.standardized[k]) )
					return false;
			}
		}
		for (int j = 0; j < numSamples; j++) {
			for (int k = 0; k < numDataCols; k++) {
				if ( stdObjects[j][k] == null ) {
					if ( other.stdObjects[j][k] != null )
						return false;
				}
				else {
					if ( ! stdObjects[j][k].equals(other.stdObjects[j][k]) )
						return false;
				}
			}
		}
		return true;
	}

	@Override
	public String toString() {
		String repr = "StdDataArray[numSamples=" + numSamples + ", numDataCols=" + numDataCols;
		List<String> namesList = Arrays.asList(userColNames);
		repr += "\n  userColNames=" + namesList.toString(); 
		List<DashDataType<?>> typesList = Arrays.asList(dataTypes);
		repr += "\n  userColTypes=" + typesList.toString(); 
		namesList = Arrays.asList(userUnits);
		repr += "\n  userUnits=" + namesList.toString();
		namesList = Arrays.asList(userMissVals);
		repr += "\n  userMissVals=" + namesList.toString();
		List<Boolean> boolList = Arrays.asList(standardized);
		repr += "\n  standardized=" + boolList.toString();
		repr += "\n  stdObjects=[";
		for (int j = 0; j < numSamples; j++) {
			if ( j > 0 )
				repr += ",";
			repr += "\n    [ ";
			for (int k = 0; k < numDataCols; k++) {
				if ( k > 0 )
					repr += ", ";
				repr += String.valueOf(stdObjects[j][k]);
			}
			repr += " ]";
		}
		repr += "\n]";
		return repr;
	}

}
