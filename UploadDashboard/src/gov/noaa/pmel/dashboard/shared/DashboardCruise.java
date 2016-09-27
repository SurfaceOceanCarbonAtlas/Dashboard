/**
 * 
 */
package gov.noaa.pmel.dashboard.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents an uploaded cruise and its current status.
 * 
 * @author Karl Smith
 */
public class DashboardCruise implements Serializable, IsSerializable {

	private static final long serialVersionUID = 4299268768779494106L;

	boolean selected;
	String version;
	String owner;
	String expocode;
	String dataCheckStatus;
	String omeTimestamp;
	TreeSet<String> addlDocs;
	String qcStatus;
	String archiveStatus;
	String cdiacDate;
	String uploadFilename;
	String uploadTimestamp;
	String origDoi;
	String socatDoi;
	int numDataRows;
	int numErrorRows;
	int numWarnRows;
	ArrayList<String> userColNames;
	// For each data column, a DataColumnType with type, unit, and missing value
	ArrayList<DataColumnType> dataColTypes;
	// For each data column, a set of row indices with questionable data
	ArrayList<HashSet<Integer>> woceThreeRowIndices;
	// For each data column, a set of row indices with bad data
	ArrayList<HashSet<Integer>> woceFourRowIndices;
	// Row indices with questionable data not specific to a column
	HashSet<Integer> noColumnWoceThreeRowIndices;
	// Row indices with bad data not specific to a column
	HashSet<Integer> noColumnWoceFourRowIndices;
	// Row indices designated by the PI as questionable
	HashSet<Integer> userWoceThreeRowIndices;
	// Row indices designated by the PI as bad
	HashSet<Integer> userWoceFourRowIndices;

	public DashboardCruise() {
		selected = false;
		version = DashboardUtils.STRING_MISSING_VALUE;
		owner = DashboardUtils.STRING_MISSING_VALUE;
		expocode = DashboardUtils.STRING_MISSING_VALUE;
		dataCheckStatus = DashboardUtils.STRING_MISSING_VALUE;
		omeTimestamp = DashboardUtils.STRING_MISSING_VALUE;
		addlDocs = new TreeSet<String>();
		qcStatus = DashboardUtils.QC_STATUS_NOT_SUBMITTED;
		archiveStatus = DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED;
		cdiacDate = DashboardUtils.STRING_MISSING_VALUE;
		uploadFilename = DashboardUtils.STRING_MISSING_VALUE;
		uploadTimestamp = DashboardUtils.STRING_MISSING_VALUE;
		origDoi = DashboardUtils.STRING_MISSING_VALUE;
		socatDoi = DashboardUtils.STRING_MISSING_VALUE;
		numDataRows = 0;
		numErrorRows = 0;
		numWarnRows = 0;
		userColNames = new ArrayList<String>();
		dataColTypes = new ArrayList<DataColumnType>();
		woceThreeRowIndices = new ArrayList<HashSet<Integer>>();
		woceFourRowIndices = new ArrayList<HashSet<Integer>>();
		noColumnWoceThreeRowIndices = new HashSet<Integer>();
		noColumnWoceFourRowIndices = new HashSet<Integer>();
		userWoceThreeRowIndices = new HashSet<Integer>();
		userWoceFourRowIndices = new HashSet<Integer>();
	}

	/**
	 * @param cruise
	 * 		cruise to check
	 * @return
	 * 		Boolean.TRUE if the cruise is suspended, excluded, in preview, or not submitted, 
	 * 		Boolean.FALSE if the cruise is submitted or acceptable but unpublished cruise,
	 * 		null if the cruise is (acceptable and) published (previous version)
	 */
	public Boolean isEditable() {
		// true for cruises that are not submitted, suspended, or excluded
		String status = getQcStatus();
		if ( status.equals(DashboardUtils.QC_STATUS_NOT_SUBMITTED) || 
			 status.equals(DashboardUtils.QC_STATUS_SUSPENDED) ||
			 status.equals(DashboardUtils.QC_STATUS_EXCLUDED)  ) 
			return Boolean.TRUE;
		// false for submitted or acceptable unpublished cruises
		status = getArchiveStatus();
		if ( status.equals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED) ||
			 status.equals(DashboardUtils.ARCHIVE_STATUS_WITH_SOCAT) ||
			 status.equals(DashboardUtils.ARCHIVE_STATUS_SENT_CDIAC) ||
			 status.equals(DashboardUtils.ARCHIVE_STATUS_OWNER_ARCHIVE) ) 
			return Boolean.FALSE;
		// null for acceptable published cruises
		return null;
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
	 * 		the cruise version; 
	 * 		never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version 
	 * 		the cruise version (after trimming) to set;
	 * 		if null, sets to {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public void setVersion(String version) {
		if ( version == null )
			this.version = DashboardUtils.STRING_MISSING_VALUE;
		else
			this.version = version.trim();
	}

	/**
	 * @return 
	 * 		the owner for this cruise; 
	 * 		never null but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner 
	 * 		the cruise owner (after trimming) to set;
	 * 		if null, sets to {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public void setOwner(String owner) {
		if ( owner == null )
			this.owner = DashboardUtils.STRING_MISSING_VALUE;
		else
			this.owner = owner.trim();
	}

	/**
	 * @return 
	 * 		the cruise expocode; 
	 * 		never null but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getExpocode() {
		return expocode;
	}

	/**
	 * @param cruiseExpocode 
	 * 		the cruise expocode (after trimming and converting to upper-case) to set;
	 * 		if null, sets to {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public void setExpocode(String expocode) {
		if ( expocode == null )
			this.expocode = DashboardUtils.STRING_MISSING_VALUE;
		else
			this.expocode = expocode.trim().toUpperCase();
	}

	/**
	 * @return 
	 * 		the data check status; 
	 * 		never null but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getDataCheckStatus() {
		return dataCheckStatus;
	}

	/**
	 * @param dataCheckStatus 
	 * 		the data check status to set;
	 * 		if null, sets to {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public void setDataCheckStatus(String dataCheckStatus) {
		if ( dataCheckStatus == null )
			this.dataCheckStatus = DashboardUtils.STRING_MISSING_VALUE;
		else
			this.dataCheckStatus = dataCheckStatus;
	}

	/**
	 * @return 
	 * 		the OME metadata timestamp; 
	 * 		never null but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getOmeTimestamp() {
		return omeTimestamp;
	}

	/**
	 * @param omeTimestamp 
	 * 		the OME metadata timestamp to set;
	 * 		if null, sets to {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public void setOmeTimestamp(String omeTimestamp) {
		if ( omeTimestamp == null )
			this.omeTimestamp = DashboardUtils.STRING_MISSING_VALUE;
		else
			this.omeTimestamp = omeTimestamp;
	}

	/**
	 * @return 
	 * 		the additional document "filename; timestamp" strings 
	 * 		associated with this cruise; never null but may be empty.  
	 * 		The actual set of strings in this object is returned.
	 */
	public TreeSet<String> getAddlDocs() {
		return addlDocs;
	}

	/**
	 * @param addlDocs
	 * 		the set of additional document "filename; timestamp" strings
	 * 		for this cruise.  The set in this object is cleared and all 
	 * 		the contents of the given set, if not null, are added. 
	 */
	public void setAddlDocs(TreeSet<String> addlDocs) {
		this.addlDocs.clear();
		if ( addlDocs != null )
			this.addlDocs.addAll(addlDocs);
	}

	/**
	 * @return 
	 * 		the QC submission status; 
	 * 		never null but may be {@link #QC_STATUS_NOT_SUBMITTED} if not assigned
	 */
	public String getQcStatus() {
		return qcStatus;
	}

	/**
	 * @param qcStatus 
	 * 		the  QC submission status (after trimming) to set;
	 * 		if null, {@link #QC_STATUS_NOT_SUBMITTED} is assigned
	 */
	public void setQcStatus(String qcStatus) {
		if ( qcStatus == null )
			this.qcStatus = DashboardUtils.QC_STATUS_NOT_SUBMITTED;
		else
			this.qcStatus = qcStatus.trim();
	}

	/**
	 * @return 
	 * 		the archive submission status; 
	 * 		never null but may be {@link DashboardUtils#ARCHIVE_STATUS_NOT_SUBMITTED} if not assigned
	 */
	public String getArchiveStatus() {
		return archiveStatus;
	}

	/**
	 * @param submitStatus 
	 * 		the archive submission status (after trimming) to set;
	 * 		if null, {@link DashboardUtils#ARCHIVE_STATUS_NOT_SUBMITTED} is assigned
	 */
	public void setArchiveStatus(String archiveStatus) {
		if ( archiveStatus == null )
			this.archiveStatus = DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED;
		else
			this.archiveStatus = archiveStatus.trim();
	}

	/**
	 * @return 
	 * 		the CDIAC submission date; 
	 * 		never null but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getCdiacDate() {
		return cdiacDate;
	}

	/**
	 * @param cdiacDate 
	 * 		the CDIAC submission date (after trimming) to set;
	 * 		if null, sets to {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public void setCdiacDate(String cdiacDate) {
		if ( cdiacDate == null )
			this.cdiacDate = DashboardUtils.STRING_MISSING_VALUE;
		else
			this.cdiacDate = cdiacDate.trim();
	}

	/**
	 * @return 
	 * 		the uploaded data filename; 
	 * 		never null but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getUploadFilename() {
		return uploadFilename;
	}

	/**
	 * @param uploadFilename 
	 * 		the uploaded data filename (after trimming) to set;
	 * 		if null, sets to {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public void setUploadFilename(String uploadFilename) {
		if ( uploadFilename == null )
			this.uploadFilename = DashboardUtils.STRING_MISSING_VALUE;
		else
			this.uploadFilename = uploadFilename.trim();
	}

	/**
	 * @return 
	 * 		the uploaded data timestamp; 
	 * 		never null but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getUploadTimestamp() {
		return uploadTimestamp;
	}

	/**
	 * @param uploadTimestamp 
	 * 		the uploaded data timestamp (after trimming) to set;
	 * 		if null, sets to {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public void setUploadTimestamp(String uploadTimestamp) {
		if ( uploadTimestamp == null )
			this.uploadTimestamp = DashboardUtils.STRING_MISSING_VALUE;
		else
			this.uploadTimestamp = uploadTimestamp.trim();
	}

	/**
	 * @return 
	 * 		the DOI of the original data document;
	 * 		never null but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getOrigDoi() {
		return origDoi;
	}

	/**
	 * @param socatDoi
	 * 		the DOI (after trimming) of the original data document to set;
	 * 		if null, sets to {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public void setOrigDoi(String origDoi) {
		if ( origDoi == null )
			this.origDoi = DashboardUtils.STRING_MISSING_VALUE;
		else
			this.origDoi = origDoi.trim();
	}

	/**
	 * @return 
	 * 		the DOI of the SOCAT-enhanced data document;
	 * 		never null but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getSocatDoi() {
		return socatDoi;
	}

	/**
	 * @param socatDoi
	 * 		the DOI (after trimming) of the SOCAT-enhanced data document to set;
	 * 		if null, sets to {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public void setSocatDoi(String socatDoi) {
		if ( socatDoi == null )
			this.socatDoi = DashboardUtils.STRING_MISSING_VALUE;
		else
			this.socatDoi = socatDoi.trim();
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
	 * @return 
	 * 		the number of data rows with error messages from the sanity checker
	 */
	public int getNumErrorRows() {
		return numErrorRows;
	}

	/**
	 * @param numErrorRows 
	 * 		the number of data rows with error messages from the sanity checker to set
	 */
	public void setNumErrorRows(int numErrorRows) {
		this.numErrorRows = numErrorRows;
	}

	/**
	 * @return 
	 * 		the number of data rows with warning messages from the sanity checker
	 */
	public int getNumWarnRows() {
		return numWarnRows;
	}

	/**
	 * @param numWarnRows 
	 * 		the number of data rows with warning messages from the sanity checker to set
	 */
	public void setNumWarnRows(int numWarnRows) {
		this.numWarnRows = numWarnRows;
	}

	/**
	 * @return the userColNames
	 * 		the list of data column header names as they appeared in 
	 * 		the original user-provided data file for this cruise; 
	 * 		never null but may be empty.  The actual list in this 
	 * 		object is returned. 
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
	 * The list of sets of WOCE-3 data row indices iterates over the 
	 * columns of the data table.  A set in this list specifies the 
	 * row indices where the data of the column has a WOCE-3 
	 * (questionable) flag.  Presumably these sets will be small and 
	 * could be empty. 
	 * 
	 * @return 
	 * 		the list of sets of WOCE-3 data row indices; 
	 * 		may be empty but never null.
	 * 		The actual list in this object is returned.
	 */
	public ArrayList<HashSet<Integer>> getWoceThreeRowIndices() {
		return woceThreeRowIndices;
	}

	/**
	 * The list of sets of WOCE-3 data row indices iterates over the 
	 * columns of the data table.  A set in this list specifies the 
	 * row indices where the data of the column has a WOCE-3 
	 * (questionable) flag.  Presumably these sets will be small and 
	 * could be empty. 
	 * 
	 * @param woceThreeRowIndices 
	 * 		the list of sets of WOCE-3 data row indices to assign. 
	 * 		The list in this object is cleared and all the contents 
	 * 		of the given list, if not null, are added.  Note that 
	 * 		this is a shallow copy; the sets in the given list are 
	 * 		not copied but used directly.
	 */
	public void setWoceThreeRowIndices(
					ArrayList<HashSet<Integer>> woceThreeRowIndices) {
		this.woceThreeRowIndices.clear();
		if ( woceThreeRowIndices != null )
			this.woceThreeRowIndices.addAll(woceThreeRowIndices);
	}

	/**
	 * The list of sets of WOCE-4 data row indices iterates over the 
	 * columns of the data table.  A set in this list specifies the 
	 * row indices where the data of the column has a WOCE-4 (bad)  
	 * flag.  Presumably these sets will be small and could be empty. 
	 * 
	 * @return 
	 * 		the list of sets of WOCE-4 data row indices; 
	 * 		may be empty but never null.
	 * 		The actual list in this object is returned.
	 */
	public ArrayList<HashSet<Integer>> getWoceFourRowIndices() {
		return woceFourRowIndices;
	}

	/**
	 * The list of sets of WOCE-4 data row indices iterates over the 
	 * columns of the data table.  A set in this list specifies the 
	 * row indices where the data of the column has a WOCE-4 (bad)  
	 * flag.  Presumably these sets will be small and could be empty. 
	 * 
	 * @param woceFourRowIndices 
	 * 		the list of sets of WOCE-4 data row indices to assign. 
	 * 		The list in this object is cleared and all the contents 
	 * 		of the given list, if not null, are added.  Note that 
	 * 		this is a shallow copy; the sets in the given list are 
	 * 		not copied but used directly.
	 */
	public void setWoceFourRowIndices(
					ArrayList<HashSet<Integer>> woceFourRowIndices) {
		this.woceFourRowIndices.clear();
		if ( woceFourRowIndices != null )
			this.woceFourRowIndices.addAll(woceFourRowIndices);
	}

	/**
	 * @return
	 * 		The set of row indices with questionable (WOCE-3) data that
	 * 		cannot be directly attributed to a specific data column.
	 * 		The actual set in this object is returned.
	 */
	public HashSet<Integer> getNoColumnWoceThreeRowIndices() {
		return noColumnWoceThreeRowIndices;
	}

	/**
	 * @param noColumnWoceThreeRowIndices
	 * 		The set to assign of row indices with questionable (WOCE-3) 
	 * 		data that cannot be directly attributed to a specific data 
	 * 		column.  The set in this object is cleared and all the 
	 * 		contents of the given set, if not null, are added.
	 */
	public void setNoColumnWoceThreeRowIndices(
			HashSet<Integer> noColumnWoceThreeRowIndices) {
		this.noColumnWoceThreeRowIndices.clear();
		if ( noColumnWoceThreeRowIndices != null )
			this.noColumnWoceThreeRowIndices.addAll(noColumnWoceThreeRowIndices);
	}

	/**
	 * @return
	 * 		The set of row indices with bad (WOCE-4) data that 
	 * 		cannot be directly attributed to a specific data column.
	 * 		The actual set in this object is returned.
	 */
	public HashSet<Integer> getNoColumnWoceFourRowIndices() {
		return noColumnWoceFourRowIndices;
	}

	/**
	 * @param noColumnWoceFourRowIndices 
	 * 		The set to assign of row indices with bad (WOCE-4) 
	 * 		data that cannot be directly attributed to a specific data 
	 * 		column.  The set in this object is cleared and all the 
	 * 		contents of the given set, if not null, are added.
	 */
	public void setNoColumnWoceFourRowIndices(
			HashSet<Integer> noColumnWoceFourRowIndices) {
		this.noColumnWoceFourRowIndices.clear();
		if ( noColumnWoceFourRowIndices != null )
			this.noColumnWoceFourRowIndices.addAll(noColumnWoceFourRowIndices);
	}

	/**
	 * @return the userWoceThreeRowIndices
	 * 		The set of row indices designated as questionable (WOCE-3) 
	 * 		data by the PI.  The actual set in this object is returned.
	 */
	public HashSet<Integer> getUserWoceThreeRowIndices() {
		return userWoceThreeRowIndices;
	}

	/**
	 * @param userWoceThreeRowIndices
	 * 		The set to assign of row indices designated as questionable 
	 * 		(WOCE-3) data by the PI.  The set in this object is cleared 
	 * 		and all the contents of the given set, if not null, are added.
	 */
	public void setUserWoceThreeRowIndices(HashSet<Integer> userWoceThreeRowIndices) {
		this.userWoceThreeRowIndices.clear();
		if ( userWoceThreeRowIndices != null )
			this.userWoceThreeRowIndices.addAll(userWoceThreeRowIndices);
	}

	/**
	 * @return 
	 * 		The set of row indices designated as bad (WOCE-4) 
	 * 		data by the PI.  The actual set in this object is returned.
	 */
	public HashSet<Integer> getUserWoceFourRowIndices() {
		return userWoceFourRowIndices;
	}

	/**
	 * @param userWoceFourRowIndices
	 * 		The set to assign of row indices designated as bad 
	 * 		(WOCE-4) data by the PI.  The set in this object is cleared 
	 * 		and all the contents of the given set, if not null, are added.
	 */
	public void setUserWoceFourRowIndices(HashSet<Integer> userWoceFourRowIndices) {
		this.userWoceFourRowIndices.clear();
		if ( userWoceFourRowIndices != null )
			this.userWoceFourRowIndices.addAll(userWoceFourRowIndices);
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = Boolean.valueOf(selected).hashCode();
		result = result * prime + version.hashCode();
		result = result * prime + owner.hashCode();
		result = result * prime + expocode.hashCode();
		result = result * prime + dataCheckStatus.hashCode();
		result = result * prime + omeTimestamp.hashCode();
		result = result * prime + addlDocs.hashCode();
		result = result * prime + qcStatus.hashCode();
		result = result * prime + archiveStatus.hashCode();
		result = result * prime + cdiacDate.hashCode();
		result = result * prime + uploadFilename.hashCode();
		result = result * prime + uploadTimestamp.hashCode();
		result = result * prime + origDoi.hashCode();
		result = result * prime + socatDoi.hashCode();
		result = result * prime + numDataRows;
		result = result * prime + numErrorRows;
		result = result * prime + numWarnRows;
		result = result * prime + userColNames.hashCode();
		result = result * prime + dataColTypes.hashCode();
		result = result * prime + woceThreeRowIndices.hashCode();
		result = result * prime + woceFourRowIndices.hashCode();
		result = result * prime + noColumnWoceThreeRowIndices.hashCode();
		result = result * prime + noColumnWoceFourRowIndices.hashCode();
		result = result * prime + userWoceThreeRowIndices.hashCode();
		result = result * prime + userWoceFourRowIndices.hashCode();
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
		if ( ! version.equals(other.version) ) 
			return false;
		if ( ! owner.equals(other.owner) )
			return false;
		if ( ! expocode.equals(other.expocode) )
			return false;
		if ( ! dataCheckStatus.equals(other.dataCheckStatus) )
			return false;
		if ( ! omeTimestamp.equals(other.omeTimestamp) )
			return false;
		if ( ! addlDocs.equals(other.addlDocs) )
			return false;
		if ( ! qcStatus.equals(other.qcStatus) )
			return false;
		if ( ! archiveStatus.equals(other.archiveStatus) )
			return false;
		if ( ! cdiacDate.equals(other.cdiacDate) )
			return false;
		if ( ! uploadFilename.equals(other.uploadFilename) )
			return false;
		if ( ! uploadTimestamp.equals(other.uploadTimestamp) )
			return false;
		if ( ! origDoi.equals(other.origDoi) )
			return false;
		if ( ! socatDoi.equals(other.socatDoi) )
			return false;
		if ( numDataRows != other.numDataRows )
			return false;
		if ( numErrorRows != other.numErrorRows )
			return false;
		if ( numWarnRows != other.numWarnRows )
			return false;
		if ( ! userColNames.equals(other.userColNames) )
			return false;
		if ( ! dataColTypes.equals(other.dataColTypes) )
			return false;
		if ( ! woceThreeRowIndices.equals(other.woceThreeRowIndices) ) 
			return false;
		if ( ! woceFourRowIndices.equals(other.woceFourRowIndices) ) 
			return false;
		if ( ! noColumnWoceThreeRowIndices.equals(other.noColumnWoceThreeRowIndices) ) 
			return false;
		if ( ! noColumnWoceFourRowIndices.equals(other.noColumnWoceFourRowIndices) ) 
			return false;
		if ( ! userWoceThreeRowIndices.equals(other.userWoceThreeRowIndices) ) 
			return false;
		if ( ! userWoceFourRowIndices.equals(other.userWoceFourRowIndices) ) 
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DashboardCruise" +
				"[ selected=" + Boolean.toString(selected) + 
				",\n    version = " + version.toString() +
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
				",\n    origDoi=" + origDoi +
				",\n    socatDoi=" + socatDoi +
				",\n    numDataRows=" + Integer.toString(numDataRows) +
				",\n    numErrorRows=" + Integer.toString(numErrorRows) +
				",\n    numWarnRows=" + Integer.toString(numWarnRows) +
				",\n    userColNames=" + userColNames.toString() +
				",\n    dataColTypes=" + dataColTypes.toString() +
				";\n    woceThreeRowIndices = " + woceThreeRowIndices.toString() +
				";\n    woceFourRowIndices = " + woceFourRowIndices.toString() +
				";\n    noColumnWoceThreeRowIndices = " + noColumnWoceThreeRowIndices.toString() +
				";\n    noColumnWoceFourRowIndices = " + noColumnWoceFourRowIndices.toString() +
				";\n    userWoceThreeRowIndices = " + userWoceThreeRowIndices.toString() +
				";\n    userWoceFourRowIndices = " + userWoceFourRowIndices.toString() +
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
	 * Compare using the owner of cruises.
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
	 * Compare using the expocode of the cruises.
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
	 * Compare using the upload timestamp of the cruises.
	 * Note that this is inconsistent with DashboardCruise.equals 
	 * in that this is only examining one field of DashboardCruise.
	 */
	public static Comparator<DashboardCruise> timestampComparator = 
			new Comparator<DashboardCruise>() {
		@Override
		public int compare(DashboardCruise c1, DashboardCruise c2) {
			if ( c1 == c2 )
				return 0;
			if ( c1 == null )
				return -1;
			if ( c2 == null )
				return 1;
			return c1.getUploadTimestamp().compareTo(c2.getUploadTimestamp());
		}
	};

	/**
	 * Compare using the data check status of the cruises.
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
	 * Compare using the OME metadata timestamp of the cruises.
	 * Note that this is inconsistent with DashboardCruise.equals 
	 * in that this is only examining one field of DashboardCruise.
	 */
	public static Comparator<DashboardCruise> omeTimestampComparator = 
			new Comparator<DashboardCruise>() {
		@Override
		public int compare(DashboardCruise c1, DashboardCruise c2) {
			if ( c1 == c2 )
				return 0;
			if ( c1 == null )
				return -1;
			if ( c2 == null )
				return 1;
			return c1.getOmeTimestamp().compareTo(c2.getOmeTimestamp());
		}
	};

	/**
	 * Compare using the additional document "filename; timestamp" 
	 * strings of the cruises.  Note that this is inconsistent with 
	 * DashboardCruise.equals in that this is only examining one 
	 * field of DashboardCruise.
	 */
	public static Comparator<DashboardCruise> addlDocsComparator = 
			new Comparator<DashboardCruise>() {
		@Override
		public int compare(DashboardCruise c1, DashboardCruise c2) {
			if ( c1 == c2 )
				return 0;
			if ( c1 == null )
				return -1;
			if ( c2 == null )
				return 1;
			Iterator<String> iter1 = c1.getAddlDocs().iterator();
			Iterator<String> iter2 = c2.getAddlDocs().iterator();
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
	 * Compare using the SOCAT version of the cruises.
	 * Note that this is inconsistent with DashboardCruise.equals 
	 * in that this is only examining one field of DashboardCruise.
	 */
	public static Comparator<DashboardCruise> versionComparator = 
			new Comparator<DashboardCruise>() {
		@Override
		public int compare(DashboardCruise c1, DashboardCruise c2) {
			if ( c1 == c2 )
				return 0;
			if ( c1 == null )
				return -1;
			if ( c2 == null )
				return 1;
			return c1.getVersion().compareTo(c2.getVersion());
		}
	};

	/**
	 * Compare using the QC status string of the cruises.
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
	 * Compare using the archive status of the cruises.
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
	 * Compare using the upload filename of the cruises.
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
