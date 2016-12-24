/**
 * 
 */
package gov.noaa.pmel.dashboard.standardize;

import java.util.ArrayList;

import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

/**
 * A 2-D array of objects corresponding to the standardized values in a dataset.
 * 
 * @author Karl Smith
 */
public class StdDataArray {
	private Boolean[] standardized;
	private Object[][] stdObjects;
	private int numSamples;
	private int numDataCols;

	/**
	 * Create a 2-D standard data array with the specified number of 
	 * samples (rows), each with the specified number of data columns.  
	 * All objects are initialized to null.
	 *  
	 * @param numSamples
	 * 		number of samples (rows)
	 * @param numDataCols
	 * 		number of data columns in each sample
	 * @throws IllegalArgumentException
	 * 		if the specified number of samples or data columns is not positive
	 */
	public StdDataArray(int numSamples, int numDataCols) throws IllegalArgumentException {
		if ( numSamples <= 0 )
			throw new IllegalArgumentException("Invalid number of samples " + numSamples);
		if ( numDataCols <= 0 )
			throw new IllegalArgumentException("Invalid number of data columns " + numDataCols);
		this.numSamples = numSamples;
		this.numDataCols = numDataCols;
		stdObjects = new Object[numSamples][numDataCols];
		for (int j = 0; j < numSamples; j++)
			for (int k = 0; k < numDataCols; k++)
				stdObjects[j][k] = null;
		standardized = new Boolean[numDataCols];
		for (int k = 0; k < numDataCols; k++)
			standardized[k] = null;
	}

	/**
	 * Create a 2-D standard data array from interpreting the list of list of strings
	 * corresponding to the data column values (in the inner list) of samples (in outer 
	 * list) with the corresponding data column types.  Any data columns types matching 
	 * {@link DashboardServerUtils#UNKNOWN} or {@link DashboardServerUtils#OTHER} are
	 * ignored (their standard data objects will be null but getStdVal will throw an
	 * IllegalArgumentException rather than returning null).
	 * 
	 * @param dataVals
	 * 		a list of list of data value strings where dataVals.get(j).get(k) is the
	 * 		value of k-th data column for the j-th sample.
	 * @param dataTypes
	 * 		a list of the data column types; the data type as well as the selected unit,
	 * 		standard unit (first element of the list of units), and selected missing value 
	 * 		of these types are used in interpreting the strings.
	 * @throws IllegalArgumentException
	 * 		if there is an inconsistent number of columns (defined by the number of
	 * 			elements in the data types array) in any of the samples, 
	 * 		if a string cannot be interpreted to the appropriate data type, or 
	 * 		if unit conversion of a data value cannot be accomplished.
	 */
	public StdDataArray(ArrayList<ArrayList<String>> dataVals, 
			ArrayList<DataColumnType> dataTypes) throws IllegalArgumentException {
		this(dataVals.size(), dataTypes.size());
		for (int j = 0; j < numSamples; j++)
			if ( dataVals.get(j).size() != numDataCols )
				throw new IllegalArgumentException("Inconsistent number of data columns (" + 
						Integer.toString(dataVals.get(j).size()) + " instead of " + 
						Integer.toString(numDataCols) + ") in value for sample number " + 
						Integer.toString(j+1));
		// Create a 2-D array of these Strings for efficiency
		String[][] strDataVals = new String[numSamples][numDataCols];
		for (int j = 0; j < numSamples; j++) {
			ArrayList<String> rowVals = dataVals.get(j);
			for (int k = 0; k < numDataCols; k++)
				strDataVals[j][k] = rowVals.get(k);
		}
		// Standardize data columns that do not require values from other data columns
		boolean needsAnotherPass = false;
		for (int k = 0; k < numDataCols; k++) {
			DataColumnType colType = dataTypes.get(k);
			String dataClassName = colType.getDataClassName();
			if ( DashboardServerUtils.UNKNOWN.typeNameEquals(colType) ||
				 DashboardServerUtils.OTHER.typeNameEquals(colType) ) {
				standardized[k] = null;
			}
			else if ( DashboardUtils.STRING_DATA_CLASS_NAME.equals(dataClassName) ) {
				try {
					StringStandardizer stdizer = new StringStandardizer(colType);
					for (int j = 0; j < numSamples; j++) {
						try {
							stdObjects[j][k] = stdizer.getStandardValue(strDataVals[j][k]);
						} catch ( IllegalArgumentException ex ) {
							throw new IllegalArgumentException("Problems with sample number " + 
									Integer.toString(j+1) + ", column number " + Integer.toString(k+1) + 
									": " + ex.getMessage(), ex);
						}
					}
					standardized[k] = true;
				} catch ( IllegalStateException ex ) {
					standardized[k] = false;
					needsAnotherPass = true;
				}
			}
			else if ( DashboardUtils.CHAR_DATA_CLASS_NAME.equals(dataClassName) ) {
				try {
					CharStandardizer stdizer = new CharStandardizer(colType);
					for (int j = 0; j < numSamples; j++) {
						try {
							stdObjects[j][k] = stdizer.getStandardValue(strDataVals[j][k]);
						} catch ( IllegalArgumentException ex ) {
							throw new IllegalArgumentException("Problems with sample number " + 
									Integer.toString(j+1) + ", column number " + Integer.toString(k+1) + 
									": " + ex.getMessage(), ex);
						}
					}
					standardized[k] = true;
				} catch ( IllegalStateException ex ) {
					standardized[k] = false;
					needsAnotherPass = true;
				}
			}
			else if ( DashboardUtils.INT_DATA_CLASS_NAME.equals(dataClassName) ) {
				try {
					IntStandardizer stdizer = new IntStandardizer(colType);
					for (int j = 0; j < numSamples; j++) {
						try {
						stdObjects[j][k] = stdizer.getStandardValue(strDataVals[j][k]);
						} catch ( IllegalArgumentException ex ) {
							throw new IllegalArgumentException("Problems with sample number " + 
									Integer.toString(j+1) + ", column number " + Integer.toString(k+1) + 
									": " + ex.getMessage(), ex);
						}
					}
					standardized[k] = true;
				} catch ( IllegalStateException ex ) {
					standardized[k] = false;
					needsAnotherPass = true;
				}
			}
			else if ( DashboardUtils.DATE_DATA_CLASS_NAME.equals(dataClassName) ) {
				try {
					DateStandardizer stdizer = new DateStandardizer(colType);
					for (int j = 0; j < numSamples; j++) {
						try {
						stdObjects[j][k] = stdizer.getStandardValue(strDataVals[j][k]);
						} catch ( IllegalArgumentException ex ) {
							throw new IllegalArgumentException("Problems with sample number " + 
									Integer.toString(j+1) + ", column number " + Integer.toString(k+1) + 
									": " + ex.getMessage(), ex);
						}
					}
					standardized[k] = true;
				} catch ( IllegalStateException ex ) {
					standardized[k] = false;
					needsAnotherPass = true;
				}
			}
			else if ( DashboardServerUtils.LONGITUDE.typeNameEquals(colType) ||
					  DashboardServerUtils.LATITUDE.typeNameEquals(colType) ) {
				try {
					LonLatStandardizer stdizer = new LonLatStandardizer(colType);
					for (int j = 0; j < numSamples; j++) {
						try {
						stdObjects[j][k] = stdizer.getStandardValue(strDataVals[j][k]);
						} catch ( IllegalArgumentException ex ) {
							throw new IllegalArgumentException("Problems with sample number " + 
									Integer.toString(j+1) + ", column number " + Integer.toString(k+1) + 
									": " + ex.getMessage(), ex);
						}
					}
					standardized[k] = true;
				} catch ( IllegalStateException ex ) {
					standardized[k] = false;
					needsAnotherPass = true;
				}
			}
			else if ( DashboardUtils.DOUBLE_DATA_CLASS_NAME.equals(dataClassName) ) {
				try {
					DoubleStandardizer stdizer = new DoubleStandardizer(colType);
					for (int j = 0; j < numSamples; j++) {
						try {
						stdObjects[j][k] = stdizer.getStandardValue(strDataVals[j][k]);
						} catch ( IllegalArgumentException ex ) {
							throw new IllegalArgumentException("Problems with sample number " + 
									Integer.toString(j+1) + ", column number " + Integer.toString(k+1) + 
									": " + ex.getMessage(), ex);
						}
					}
					standardized[k] = true;
				} catch ( IllegalStateException ex ) {
					standardized[k] = false;
					needsAnotherPass = true;
				}
			}
			else {
				throw new IllegalArgumentException("Unknown data class name of " + dataClassName);
			}
		}
		// Standardize columns that require values from other (standardized) columns
		while ( needsAnotherPass ) {
			// TODO:
			throw new IllegalArgumentException("Second pass standardization not yet implemented");
		}
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
	 * 		standard value object; null is returned for "missing value"
	 * @throws IndexOutOfBoundsException
	 * 		if either the sample index of the value index is invalid
	 * @throws IllegalArgumentException 
	 * 		if the value cannot be standardized
	 * @throws IllegalStateException 
	 * 		if the value has not been standardized
	 */
	public Object getStdVal(int sampleIdx, int columnIdx) 
			throws IndexOutOfBoundsException, IllegalArgumentException, IllegalStateException {
		if ( (sampleIdx < 0) || (sampleIdx >= numSamples) )
			throw new IndexOutOfBoundsException("sample index is invalid " + sampleIdx);
		if ( (columnIdx < 0) || (columnIdx >= numDataCols) )
			throw new IndexOutOfBoundsException("data column index is invalid " + columnIdx);
		if ( standardized[columnIdx] == null )
			throw new IllegalArgumentException("value cannot be standardized");
		if ( ! standardized[columnIdx] )
			throw new IllegalStateException("value has not been standardized");
		return stdObjects[sampleIdx][columnIdx];
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = numDataCols;
		result = prime * result + numSamples;
		for (int k = 0; k < numDataCols; k++) {
			result *= prime;
			if ( standardized[k] != null )
				result += standardized[k].hashCode();
		}
		for (int j = 0; j < numSamples; j++) {
			for (int k = 0; k < numDataCols; k++) {
				result *= prime;
				if ( stdObjects[j][k] != null )
					result += stdObjects[j][k].hashCode(); 
			}
		}
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
		if (numDataCols != other.numDataCols) {
			return false;
		}
		if (numSamples != other.numSamples) {
			return false;
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
		repr += "\n  standardized=["; 
		for (int k = 0; k < numDataCols; k++) {
			if ( k > 0 )
				repr += ", ";
			repr += String.valueOf(standardized[k]);
		}
		repr += "]\n  stdObjects=";
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
