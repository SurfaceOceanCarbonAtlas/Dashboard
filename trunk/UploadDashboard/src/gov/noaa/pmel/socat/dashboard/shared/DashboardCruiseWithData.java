/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents the data given an uploaded cruise data file
 * 
 * @author Karl Smith
 */
public class DashboardCruiseWithData extends DashboardCruise 
						implements Serializable, IsSerializable {

	private static final long serialVersionUID = 5625861651254670342L;

	ArrayList<String> preamble;
	ArrayList<ArrayList<String>> dataValues;

	/**
	 * Creates with no cruise data
	 */
	public DashboardCruiseWithData() {
		preamble = new ArrayList<String>();
		dataValues = new ArrayList<ArrayList<String>>();
	}

	/**
	 * @return 
	 * 		the list of metadata preamble strings;
	 * 		may be empty, but never null.
	 * 		The actual list in this object is returned.
	 */
	public ArrayList<String> getPreamble() {
		return preamble;
	}

	/**
	 * @param preamble 
	 * 		the metadata preamble strings to assign.  The list in 
	 * 		this object is cleared and all the contents of the
	 * 		given list, if not null, are added. 
	 */
	public void setPreamble(ArrayList<String> preamble) {
		this.preamble.clear();
		if ( preamble != null )
			this.preamble.addAll(preamble);
	}

	/**
	 * The outer list of the data values iterates over the data samples; 
	 * the rows of a table of data.  The inner list iterates over each 
	 * particular data value for that sample; an entry in the column 
	 * of a table of data.
	 * 
	 * @return 
	 * 		the list of data string lists; 
	 * 		may be empty but never null.
	 * 		The actual list in this object is returned.
	 */
	public ArrayList<ArrayList<String>> getDataValues() {
		return dataValues;
	}

	/**
	 * The outer list of the data values iterates over the data samples; 
	 * the rows of a table of data.  The inner list iterates over each 
	 * particular data value for that sample; an entry in the column 
	 * of a table of data.
	 * 
	 * @param dataValues 
	 * 		the lists of data values to assign.  The list in this object
	 * 		is cleared and all the contents of the given list, if not 
	 * 		null, are added.  Note that this is a shallow copy; the 
	 * 		lists in the given list are not copied but used directly.
	 */
	public void setDataValues(ArrayList<ArrayList<String>> dataValues) {
		this.dataValues.clear();
		if ( dataValues != null )
			this.dataValues.addAll(dataValues);
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = super.hashCode();
		result = result * prime + preamble.hashCode();
		result = result * prime + dataValues.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if (obj == null)
			return false;

		if ( ! ( obj instanceof DashboardCruiseWithData ) )
			return false;
		DashboardCruiseWithData other = (DashboardCruiseWithData) obj;

		if ( ! super.equals(other) ) 
			return false;
		if ( ! preamble.equals(other.preamble) ) 
			return false;
		if ( ! dataValues.equals(other.dataValues) ) 
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "DashboardCruiseWithData" +
				"[ selected=" + Boolean.toString(selected) + 
				",\n    version = " + version +
				",\n    owner=" + owner + 
				",\n    expocode=" + expocode + 
				",\n    dataCheckStatus=" + dataCheckStatus +
				",\n    omeTimestamp=" + omeTimestamp + 
				",\n    addlDocs=" + addlDocs.toString() +
				",\n    qcStatus=" + qcStatus + 
				",\n    archiveStatus=" + archiveStatus + 
				",\n    cdiacDate=" + cdiacDate + 
				",\n    uploadFilename=" + uploadFilename +
				",\n    uploadTimestamp=" + uploadTimestamp +
				",\n    numDataRows=" + Integer.toString(numDataRows) +
				",\n    numErrorRows=" + Integer.toString(numErrorRows) +
				",\n    numWarnRows=" + Integer.toString(numWarnRows) +
				",\n    userColNames=" + userColNames.toString() +
				",\n    dataColTypes=" + dataColTypes.toString() +
				",\n    dataColUnits=" + dataColUnits.toString() +
				",\n    missingValues=" + missingValues.toString() +
				";\n    woceThreeRowIndices = " + woceThreeRowIndices.toString() +
				";\n    woceFourRowIndices = " + woceFourRowIndices.toString() +
				";\n    preamble = " + preamble.toString() +
				";\n    dataValues = " + dataValues.toString() +
				" ]";
	}

}
