/**
 * 
 */
package gov.noaa.pmel.dashboard.shared;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A simple pair of a list of known data column types 
 * and a DashboardCruiseWithData
 *  
 * @author Karl Smith
 */
public class DashboardCruiseTypes implements Serializable, IsSerializable {

	private static final long serialVersionUID = 8578550732667367014L;

	protected ArrayList<DataColumnType> allKnownTypes;
	protected DashboardCruiseWithData cruiseData;

	public DashboardCruiseTypes() {
		allKnownTypes = null;
		cruiseData = null;
	}

	/**
	 * @return 
	 * 		list of all known data types;
	 * 		the actual list in this instance (not a copy) is returned
	 */
	public ArrayList<DataColumnType> getAllKnownTypes() {
		return allKnownTypes;
	}

	/**
	 * @param allKnownTypes 
	 * 		the list of all known data types to set;
	 * 		the given list (not a copy) is assigned to this instance
	 */
	public void setAllKnownTypes(ArrayList<DataColumnType> allKnownTypes) {
		this.allKnownTypes = allKnownTypes;
	}

	/**
	 * @return 
	 * 		the dashboard cruise with data;
	 * 		the actual list in this instance (not a copy) is returned
	 */
	public DashboardCruiseWithData getCruiseData() {
		return cruiseData;
	}

	/**
	 * @param cruiseData 
	 * 		the dashboard cruise with data to set;
	 * 		the given dashboard cruise with data (not a copy) is assigned to this instance
	 */
	public void setCruiseData(DashboardCruiseWithData cruiseData) {
		this.cruiseData = cruiseData;
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = 0;
		if ( allKnownTypes != null )
			result += allKnownTypes.hashCode();
		result *= prime;
		if ( cruiseData != null )
			result += cruiseData.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;

		if ( ! (obj instanceof DashboardCruiseTypes) )
			return false;
		DashboardCruiseTypes other = (DashboardCruiseTypes) obj;

		if ( allKnownTypes == null ) {
			if ( other.allKnownTypes != null ) {
				return false;
			}
		} 
		else if ( ! allKnownTypes.equals(other.allKnownTypes) ) {
			return false;
		}

		if ( cruiseData == null ) {
			if ( other.cruiseData != null ) {
				return false;
			}
		} 
		else if ( ! cruiseData.equals(other.cruiseData) ) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "DashboardCruiseTypes[\n" +
				"allKnownTypes=" + allKnownTypes.toString() + ",\n" +
				"cruiseData=" + cruiseData.toString() + "\n" +
				"]";
	}
	
}
