/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 * Represents an uploaded cruise and its current status.
 * 
 * @author Karl Smith
 */
public class DashboardCruise implements Serializable {

	private static final long serialVersionUID = 1655717551218368707L;

	boolean selected;
	String owner;
	String expocode;
	String uploadFilename;
	Date dataCheckDate;
	Date metaCheckDate;
	String qcStatus;
	String archiveStatus;

	public DashboardCruise() {
		selected = false;
		owner = "";
		expocode = "";
		uploadFilename = "";
		dataCheckDate = null;
		metaCheckDate = null;
		qcStatus = "";
		archiveStatus = "";
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
	 * 		the data check date; may be null
	 */
	public Date getDataCheckDate() {
		return dataCheckDate;
	}

	/**
	 * @param dataCheckDate 
	 * 		the data check date to set
	 */
	public void setDataCheckDate(Date dataCheckDate) {
		this.dataCheckDate = dataCheckDate;
	}

	/**
	 * @return 
	 * 		the metadata check date; may be null
	 */
	public Date getMetaCheckDate() {
		return metaCheckDate;
	}

	/**
	 * @param metadataCheckDate
	 * 		 the metadata check date to set
	 */
	public void setMetaCheckDate(Date metaCheckDate) {
		this.metaCheckDate = metaCheckDate;
	}

	/**
	 * @return 
	 * 		the QC submission status; never null
	 */
	public String getQCStatus() {
		return qcStatus;
	}

	/**
	 * @param qcStatus 
	 * 		the  QC submission status (after trimming) to set;
	 * 		if null, sets to an empty string
	 */
	public void setQCStatus(String qcStatus) {
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

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = Boolean.valueOf(selected).hashCode();
		result = result * prime + owner.hashCode();
		result = result * prime + expocode.hashCode();
		result = result * prime + uploadFilename.hashCode();
		result *= prime;
		if ( dataCheckDate != null )
			result += dataCheckDate.hashCode();
		result *= prime;
		if ( metaCheckDate != null )
			result += metaCheckDate.hashCode();
		result = result * prime + qcStatus.hashCode();
		result = result * prime + archiveStatus.hashCode();
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

		if ( ! uploadFilename.equals(other.uploadFilename) )
			return false;

		if ( dataCheckDate == null ) {
			if ( other.dataCheckDate != null )
				return false;
		} 
		else if ( ! dataCheckDate.equals(other.dataCheckDate) )
			return false;

		if ( metaCheckDate == null ) {
			if ( other.metaCheckDate != null )
				return false;
		} 
		else if ( ! metaCheckDate.equals(other.metaCheckDate) )
			return false;

		if ( ! qcStatus.equals(other.qcStatus) )
			return false;

		if ( ! archiveStatus.equals(other.archiveStatus) )
			return false;

		return true;
	}

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
	 * Comparator using the expocodes of the cruises
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
	 * Comparator using the upload filenames of the cruises
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

	/**
	 * Comparator using the data check dates of the cruises
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
			Date d1 = c1.getDataCheckDate();
			Date d2 = c2.getDataCheckDate();
			if ( d1 == d2 )
				return 0;
			if ( d1 == null )
				return -1;
			if ( d2 == null )
				return 1;
			return d1.compareTo(d2);
		}
	};

	/**
	 * Comparator using the metadata check dates of the cruises
	 */
	public static Comparator<DashboardCruise> metaCheckComparator = 
			new Comparator<DashboardCruise>() {
		@Override
		public int compare(DashboardCruise c1, DashboardCruise c2) {
			if ( c1 == c2 )
				return 0;
			if ( c1 == null )
				return -1;
			if ( c2 == null )
				return 1;
			Date d1 = c1.getMetaCheckDate();
			Date d2 = c2.getMetaCheckDate();
			if ( d1 == d2 )
				return 0;
			if ( d1 == null )
				return -1;
			if ( d2 == null )
				return 1;
			return d1.compareTo(d2);
		}
	};

	/**
	 * Comparator using the QC status string of the cruises
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
			return c1.getQCStatus().compareTo(c2.getQCStatus());
		}
	};

	/**
	 * Comparator using the archive status of the cruises
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

}
