/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents an uploaded cruise and its current status.
 * 
 * @author Karl Smith
 */
public class DashboardCruise implements Serializable, IsSerializable {

	private static final long serialVersionUID = 5834625831282119117L;

	boolean selected;
	String owner;
	String expocode;
	String dataCheckStatus;
	TreeSet<String> metadataFilenames;
	String qcStatus;
	String archiveStatus;
	String uploadFilename;
	int numDataRows;
	ArrayList<String> userColNames;
	ArrayList<DataColumnType> dataColTypes;
	ArrayList<String> dataColUnits;
	ArrayList<Integer> dataColQualities;

	public DashboardCruise() {
		selected = false;
		owner = "";
		expocode = "";
		dataCheckStatus = "";
		metadataFilenames = new TreeSet<String>();
		qcStatus = "";
		archiveStatus = "";
		uploadFilename = "";
		numDataRows = 0;
		dataColTypes = new ArrayList<DataColumnType>();
		userColNames = new ArrayList<String>();
		dataColUnits = new ArrayList<String>();
		dataColQualities = new ArrayList<Integer>();
	}

	/**
	 * @return
	 * 		if the cruise is selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected
	 * 		set if the cruise is selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return 
	 * 		the owner for this cruise; never null
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner 
	 * 		the cruise owner (after trimming) to set;
	 * 		if null, sets to an empty string
	 */
	public void setOwner(String owner) {
		if ( owner == null )
			this.owner = "";
		else
			this.owner = owner.trim();
	}

	/**
	 * @return 
	 * 		the cruise expocode; never null
	 */
	public String getExpocode() {
		return expocode;
	}

	/**
	 * @param cruiseExpocode 
	 * 		the cruise expocode to set (after trimming 
	 * 		and converting to upper-case) to set;
	 * 		if null, sets to an empty string
	 */
	public void setExpocode(String expocode) {
		if ( expocode == null )
			this.expocode = "";
		else
			this.expocode = expocode.trim().toUpperCase();
	}

	/**
	 * @return 
	 * 		the data check status; never null
	 */
	public String getDataCheckStatus() {
		return dataCheckStatus;
	}

	/**
	 * @param dataCheckStatus 
	 * 		the data check status to set;
	 * 		if null, sets to an empty string
	 */
	public void setDataCheckStatus(String dataCheckStatus) {
		if ( dataCheckStatus == null )
			this.dataCheckStatus = "";
		else
			this.dataCheckStatus = dataCheckStatus;
	}

	/**
	 * @return 
	 * 		the metadata filenames associated with this cruise; 
	 * 		may be empty but never null.  The actual set of strings
	 * 		in this object is returned.
	 */
	public TreeSet<String> getMetadataFilenames() {
		return metadataFilenames;
	}

	/**
	 * @param metadataFilenames
	 * 		the set of metadata filenames for this cruise.  The set in 
	 * 		this object is cleared and all the contents of the given set, 
	 * 		if not null, are added. 
	 */
	public void setMetadataFilenames(TreeSet<String> metadataFilenames) {
		this.metadataFilenames.clear();
		if ( metadataFilenames != null )
			this.metadataFilenames.addAll(metadataFilenames);
	}

	/**
	 * @return 
	 * 		the QC submission status; never null
	 */
	public String getQcStatus() {
		return qcStatus;
	}

	/**
	 * @param qcStatus 
	 * 		the  QC submission status (after trimming) to set;
	 * 		if null, sets to an empty string
	 */
	public void setQcStatus(String qcStatus) {
		if ( qcStatus == null )
			this.qcStatus = "";
		else
			this.qcStatus = qcStatus.trim();
	}

	/**
	 * @return 
	 * 		the archive submission status; never null
	 */
	public String getArchiveStatus() {
		return archiveStatus;
	}

	/**
	 * @param submitStatus 
	 * 		the archive submission status (after trimming) to set;
	 * 		if null, sets to an empty string
	 */
	public void setArchiveStatus(String archiveStatus) {
		if ( archiveStatus == null )
			this.archiveStatus = "";
		else
			this.archiveStatus = archiveStatus.trim();
	}

	/**
	 * @return 
	 * 		the uploaded data filename; never null
	 */
	public String getUploadFilename() {
		return uploadFilename;
	}

	/**
	 * @param uploadFilename 
	 * 		the uploaded data filename (after trimming) to set;
	 * 		if null, sets to an empty string
	 */
	public void setUploadFilename(String uploadFilename) {
		if ( uploadFilename == null )
			this.uploadFilename = "";
		else
			this.uploadFilename = uploadFilename.trim();
	}

	/**
	 * @return 
	 * 		the total number of data measurements (data rows) 
	 * 		for the cruise
	 */
	public int getNumDataRows() {
		return numDataRows;
	}

	/**
	 * @param numDataRows 
	 * 		the total number of data measurements (data rows) 
	 * 		to set for the cruise 
	 */
	public void setNumDataRows(int numDataRows) {
		this.numDataRows = numDataRows;
	}

	/**
	 * @return the userColNames
	 * 		the list of data column header names as they appeared in 
	 * 		the original user-provided data file for this cruise; may 
	 * 		be empty but never null.  The actual list in this object 
	 * 		is returned. 
	 */
	public ArrayList<String> getUserColNames() {
		return userColNames;
	}

	/**
	 * @param userColNames 
	 * 		the list of data column header names as they appeared in 
	 * 		the original user-provided data file for this cruise.  The 
	 * 		list in this object is cleared and all the contents of the  
	 * 		given list, if not null, are added. 
	 */
	public void setUserColNames(ArrayList<String> userColNames) {
		this.userColNames.clear();
		if ( userColNames != null )
			this.userColNames.addAll(userColNames);
	}

	/**
	 * @return 
	 * 		the list of data column types for this cruise; may be empty 
	 * 		but never null.  The actual list in this object is returned.
	 */
	public ArrayList<DataColumnType> getDataColTypes() {
		return dataColTypes;
	}

	/**
	 * @param dataColTypes 
	 * 		the list of data column types for this cruise.  The list in 
	 * 		this object is cleared and all the contents of the given list, 
	 * 		if not null, are added. 
	 */
	public void setDataColTypes(ArrayList<DataColumnType> dataColTypes) {
		this.dataColTypes.clear();
		if ( dataColTypes != null )
			this.dataColTypes.addAll(dataColTypes);
	}

	/**
	 * @return 
	 * 		the list of data column units for this cruise; may be empty 
	 * 		but never null.  The actual list in this object is returned. 
	 */
	public ArrayList<String> getDataColUnits() {
		return dataColUnits;
	}

	/**
	 * @param dataColUnits 
	 * 		the list of data column units for this cruise.  The list 
	 * 		in this object is cleared and all the contents of the given 
	 * 		list, if not null, are added. 
	 */
	public void setDataColUnits(ArrayList<String> dataColUnits) {
		this.dataColUnits.clear();
		if ( dataColUnits != null )
			this.dataColUnits.addAll(dataColUnits);
	}

	/**
	 * @return 
	 * 		the list of data column qualities (a WOCE-like flag for each
	 * 		data column taken as a whole) for this cruise; may be empty 
	 * 		but never null.  The actual list in this object is returned. 
	 */
	public ArrayList<Integer> getDataColQualities() {
		return dataColQualities;
	}

	/**
	 * @param dataColQualities 
	 * 		the list of data column qualities (a WOCE-like flag for each
	 * 		data column taken as a whole) for this cruise.  The list in 
	 * 		this object is cleared and all the contents of the given list, 
	 * 		if not null, are added. 
	 */
	public void setDataColQualities(ArrayList<Integer> dataColQualities) {
		this.dataColQualities.clear();
		if ( dataColQualities != null )
			this.dataColQualities.addAll(dataColQualities);
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = Boolean.valueOf(selected).hashCode();
		result = result * prime + owner.hashCode();
		result = result * prime + expocode.hashCode();
		result = result * prime + dataCheckStatus.hashCode();
		result = result * prime + metadataFilenames.hashCode();
		result = result * prime + qcStatus.hashCode();
		result = result * prime + archiveStatus.hashCode();
		result = result * prime + uploadFilename.hashCode();
		result = result * prime + numDataRows;
		result = result * prime + userColNames.hashCode();
		result = result * prime + dataColTypes.hashCode();
		result = result * prime + dataColUnits.hashCode();
		result = result * prime + dataColQualities.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! (obj instanceof DashboardCruise) )
			return false;
		DashboardCruise other = (DashboardCruise) obj;

		if ( selected != other.selected )
			return false;
		if ( ! owner.equals(other.owner) )
			return false;
		if ( ! expocode.equals(other.expocode) )
			return false;
		if ( ! dataCheckStatus.equals(other.dataCheckStatus) )
			return false;
		if ( ! metadataFilenames.equals(other.metadataFilenames) )
			return false;
		if ( ! qcStatus.equals(other.qcStatus) )
			return false;
		if ( ! archiveStatus.equals(other.archiveStatus) )
			return false;
		if ( ! uploadFilename.equals(other.uploadFilename) )
			return false;
		if ( numDataRows != other.numDataRows )
			return false;
		if ( ! userColNames.equals(other.userColNames) )
			return false;
		if ( ! dataColTypes.equals(other.dataColTypes) )
			return false;
		if ( ! dataColUnits.equals(other.dataColUnits) )
			return false;
		if ( ! dataColQualities.equals(other.dataColQualities) )
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DashboardCruise" +
				"[ selected=" + Boolean.toString(selected) + 
				",\n    owner=" + owner + 
				",\n    expocode=" + expocode + 
				",\n    dataCheckStatus=" + dataCheckStatus +
				",\n    metadataFilenames=" + metadataFilenames +
				",\n    qcStatus=" + qcStatus + 
				",\n    archiveStatus=" + archiveStatus + 
				",\n    uploadFilename=" + uploadFilename +
				",\n    numDataRows=" + Integer.toString(numDataRows) +
				",\n    userColNames=" + userColNames.toString() +
				",\n    dataColTypes=" + dataColTypes.toString() +
				",\n    dataColUnits=" + dataColUnits.toString() +
				",\n    dataColQualities=" + dataColQualities.toString() +
				" ]";
	}

	/**
	 * Compare using the "selected" property of cruises.
	 * Note that this is inconsistent with DashboardCruise.equals
	 * in that this is only examining one field of DashboardCruise.
	 */
	public static Comparator<DashboardCruise> selectedComparator =
			new Comparator<DashboardCruise>() {
		@Override
		public int compare(DashboardCruise c1, DashboardCruise c2) {
			if ( c1 == c2 )
				return 0;
			if ( c1 == null )
				return -1;
			if ( c2 == null )
				return 1;
			Boolean s1 = c1.isSelected();
			return s1.compareTo(c2.isSelected());
		}
	};

	/**
	 * Compare using the owner of cruises
	 * Note that this is inconsistent with DashboardCruise.equals 
	 * in that this is only examining one field of DashboardCruise.
	 */
	public static Comparator<DashboardCruise> ownerComparator =
			new Comparator<DashboardCruise>() {
		@Override
		public int compare(DashboardCruise c1, DashboardCruise c2) {
			if ( c1 == c2 )
				return 0;
			if ( c1 == null )
				return -1;
			if ( c2 == null )
				return 1;
			return c1.getOwner().compareTo(c2.getOwner());
		}
	};

	/**
	 * Compare using the expocode of the cruises
	 * Note that this is inconsistent with DashboardCruise.equals 
	 * in that this is only examining one field of DashboardCruise.
	 */
	public static Comparator<DashboardCruise> expocodeComparator = 
			new Comparator<DashboardCruise>() {
		@Override
		public int compare(DashboardCruise c1, DashboardCruise c2) {
			if ( c1 == c2 )
				return 0;
			if ( c1 == null )
				return -1;
			if ( c2 == null )
				return 1;
			return c1.getExpocode().compareTo(c2.getExpocode());
		}
	};

	/**
	 * Compare using the data check status of the cruises
	 * Note that this is inconsistent with DashboardCruise.equals 
	 * in that this is only examining one field of DashboardCruise.
	 */
	public static Comparator<DashboardCruise> dataCheckComparator = 
			new Comparator<DashboardCruise>() {
		@Override
		public int compare(DashboardCruise c1, DashboardCruise c2) {
			if ( c1 == c2 )
				return 0;
			if ( c1 == null )
				return -1;
			if ( c2 == null )
				return 1;
			return c1.getDataCheckStatus().compareTo(c2.getDataCheckStatus());
		}
	};

	/**
	 * Compare using the metadata filenames of the cruises
	 * Note that this is inconsistent with DashboardCruise.equals 
	 * in that this is only examining one field of DashboardCruise.
	 */
	public static Comparator<DashboardCruise> metadataFilenamesComparator = 
			new Comparator<DashboardCruise>() {
		@Override
		public int compare(DashboardCruise c1, DashboardCruise c2) {
			if ( c1 == c2 )
				return 0;
			if ( c1 == null )
				return -1;
			if ( c2 == null )
				return 1;
			Iterator<String> iter1 = c1.getMetadataFilenames().iterator();
			Iterator<String> iter2 = c2.getMetadataFilenames().iterator();
			while ( iter1.hasNext() && iter2.hasNext() ) {
				int result = iter1.next().compareTo(iter2.next());
				if ( result != 0 )
					return result;
			}
			// The lists are the same up to the minimum number of strings given,
			// so the list with more items is larger; or they are equal if they
			// both have no more items
			if ( iter1.hasNext() )
				return 1;
			if ( iter2.hasNext() )
				return -1;
			return 0;
		}
	};

	/**
	 * Compare using the QC status string of the cruises
	 * Note that this is inconsistent with DashboardCruise.equals 
	 * in that this is only examining one field of DashboardCruise.
	 */
	public static Comparator<DashboardCruise> qcStatusComparator = 
			new Comparator<DashboardCruise>() {
		@Override
		public int compare(DashboardCruise c1, DashboardCruise c2) {
			if ( c1 == c2 )
				return 0;
			if ( c1 == null )
				return -1;
			if ( c2 == null )
				return 1;
			return c1.getQcStatus().compareTo(c2.getQcStatus());
		}
	};

	/**
	 * Compare using the archive status of the cruises
	 * Note that this is inconsistent with DashboardCruise.equals 
	 * in that this is only examining one field of DashboardCruise.
	 */
	public static Comparator<DashboardCruise> archiveStatusComparator = 
			new Comparator<DashboardCruise>() {
		@Override
		public int compare(DashboardCruise c1, DashboardCruise c2) {
			if ( c1 == c2 )
				return 0;
			if ( c1 == null )
				return -1;
			if ( c2 == null )
				return 1;
			return c1.getArchiveStatus().compareTo(c2.getArchiveStatus());
		}
	};

	/**
	 * Compare using the upload filename of the cruises
	 * Note that this is inconsistent with DashboardCruise.equals 
	 * in that this is only examining one field of DashboardCruise.
	 */
	public static Comparator<DashboardCruise> filenameComparator = 
			new Comparator<DashboardCruise>() {
		@Override
		public int compare(DashboardCruise c1, DashboardCruise c2) {
			if ( c1 == c2 )
				return 0;
			if ( c1 == null )
				return -1;
			if ( c2 == null )
				return 1;
			return c1.getUploadFilename().compareTo(c2.getUploadFilename());
		}
	};

}
