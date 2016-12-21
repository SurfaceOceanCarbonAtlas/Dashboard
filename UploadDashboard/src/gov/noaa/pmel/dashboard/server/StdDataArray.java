/**
 * 
 */
package gov.noaa.pmel.dashboard.server;

import java.util.ArrayList;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

/**
 * An 2-D array of objects corresponding to the standardized values in a dataset.
 * 
 * @author Karl Smith
 */
public class StdDataArray {
	private Object[][] stdObjects;
	private int numSamples;
	private int numDataCols;

	/**
	 * Create with the specified number of samples (rows), 
	 * each with the specified number of data columns.
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
	}

	public StdDataArray(ArrayList<ArrayList<String>> dataVals, 
			ArrayList<DataColumnType> dataTypes) throws IllegalArgumentException {
		this(dataVals.size(), dataTypes.size());
		for (int j = 0; j < numSamples; j++)
			if ( dataVals.get(j).size() != numDataCols )
				throw new IllegalArgumentException("Inconsistent number of data columns (" + 
						dataVals.get(j).size() + " instead of " + numDataCols + 
						") in sample values");
		for (int k = 0; k < numDataCols; k++) {
			DataColumnType colType = dataTypes.get(k);
			String dataClassName = colType.getDataClassName();
			String fromUnits = colType.getUnits().get(colType.getSelectedUnitIndex());
			if ( DashboardUtils.STRING_MISSING_VALUE.equals(fromUnits) )
				fromUnits = null;
			String toUnits = colType.getUnits().get(0);
			if ( DashboardUtils.STRING_MISSING_VALUE.equals(toUnits) )
				toUnits = null;
			String missVal = colType.getSelectedMissingValue();
			if ( DashboardUtils.STRING_MISSING_VALUE.equals(missVal) )
				missVal = null;
			if ( DashboardUtils.STRING_DATA_CLASS_NAME.equals(dataClassName) ) {
				for (int j = 0; j < numSamples; j++) {
					stdObjects[j][k] = getStringValue(dataVals.get(j).get(k), fromUnits, toUnits, missVal);
				}
			}
			else if ( DashboardUtils.CHAR_DATA_CLASS_NAME.equals(dataClassName) ) {
				for (int j = 0; j < numSamples; j++) {
					stdObjects[j][k] = getCharacterValue(dataVals.get(j).get(k), fromUnits, toUnits, missVal);
				}
			}
			else if ( DashboardUtils.INT_DATA_CLASS_NAME.equals(dataClassName) ) {
				for (int j = 0; j < numSamples; j++) {
					stdObjects[j][k] = getIntegerValue(dataVals.get(j).get(k), fromUnits, toUnits, missVal);
				}
			}
			else if ( DashboardUtils.DOUBLE_DATA_CLASS_NAME.equals(dataClassName) ) {
				if ( DashboardServerUtils.LONGITUDE.typeNameEquals(colType) ) {
					for (int j = 0; j < numSamples; j++) {
						stdObjects[j][k] = getLongitudeValue(dataVals.get(j).get(k), fromUnits, toUnits, missVal);
					}
				}
				else if ( DashboardServerUtils.LATITUDE.typeNameEquals(colType) ) {
					for (int j = 0; j < numSamples; j++) {
						stdObjects[j][k] = getLatitudeValue(dataVals.get(j).get(k), fromUnits, toUnits, missVal);
					}
				}
				else {
					for (int j = 0; j < numSamples; j++) {
						stdObjects[j][k] = getDoubleValue(dataVals.get(j).get(k), fromUnits, toUnits, missVal);
					}
				}
			}
			else {
				throw new IllegalArgumentException("Unknown data class name of " + dataClassName);
			}
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
	 * 		standard value object
	 * @throws IndexOutOfBoundsException
	 * 		if either the sample index of the value index is invalid
	 */
	public Object getStdVal(int sampleIdx, int columnIdx) throws IndexOutOfBoundsException {
		if ( (sampleIdx < 0) || (sampleIdx >= numSamples) )
			throw new IndexOutOfBoundsException("sample index is invalid " + sampleIdx);
		if ( (columnIdx < 0) || (columnIdx >= numDataCols) )
			throw new IndexOutOfBoundsException("data column index is invalid " + columnIdx);
		return stdObjects[sampleIdx][columnIdx];
	}

	/**
	 * Assign the standard value object for the specified value (column index)
	 * of the specified sample (row index).
	 * 
	 * @param sampleIdx
	 * 		index of the sample (row)
	 * @param columnIdx
	 * 		index of the data column
	 * @param stdValue
	 * 		standard value object to assign
	 * @return
	 * 		previous standard value object at that location
	 * @throws IndexOutOfBoundsException
	 * 		if either the sample index of the value index is invalid
	 */
	public Object setStdVal(int sampleIdx, int columnIdx, Object stdValue) throws IndexOutOfBoundsException {
		if ( (sampleIdx < 0) || (sampleIdx >= numSamples) )
			throw new IndexOutOfBoundsException("sample index is invalid " + sampleIdx);
		if ( (columnIdx < 0) || (columnIdx >= numDataCols) )
			throw new IndexOutOfBoundsException("data column index is invalid " + columnIdx);
		Object oldStdVal = stdObjects[sampleIdx][columnIdx];
		stdObjects[sampleIdx][columnIdx] = stdValue;
		return oldStdVal;
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = numDataCols;
		result = prime * result + numSamples;
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
		String repr = "StdDataArray[numSamples=" + numSamples + 
				", numDataCols=" + numDataCols + "stdObjects=";
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
