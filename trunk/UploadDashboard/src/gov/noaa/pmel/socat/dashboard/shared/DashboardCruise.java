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

	private static final long serialVersionUID = -7637218782338765114L;

	boolean selected;
	String expocode;
	String uploadFilename;
	Date dataCheckDate;
	Date metaCheckDate;
	String qcStatus;
	String archiveStatus;

	public DashboardCruise() {
		selected = false;
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
	 * 		the cruise expocode;
	 * 		may be empty, but never null
	 */
	public String getExpocode() {
		return this.expocode;
	}

	/**
	 * @param cruiseExpocode 
	 * 		the cruise expocode to set
	 */
	public void setExpocode(String expocode) {
		if ( expocode != null )
			this.expocode = expocode;
		else
			this.expocode = "";
	}

	/**
	 * @return 
	 * 		the uploaded data filename;
	 * 		may be empty, but never null
	 */
	public String getUploadFilename() {
		return this.uploadFilename;
	}

	/**
	 * @param uploadFilename 
	 * 		the uploaded data filename to set
	 */
	public void setUploadFilename(String uploadFilename) {
		if ( uploadFilename != null )
			this.uploadFilename = uploadFilename;
		else
			this.uploadFilename = "";
	}

	/**
	 * @return 
	 * 		the data check date; may be null
	 */
	public Date getDataCheckDate() {
		return this.dataCheckDate;
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
		return this.metaCheckDate;
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
	 * 		the QC submission status;
	 * 		may be empty, but never null
	 */
	public String getQCStatus() {
		return this.qcStatus;
	}

	/**
	 * @param qcStatus 
	 * 		the  QC submission status to set
	 */
	public void setQCStatus(String qcStatus) {
		if ( qcStatus != null )
			this.qcStatus = qcStatus;
		else
			this.qcStatus = "";
	}

	/**
	 * @return 
	 * 		the archive submission status;
	 * 		may be empty, but never null
	 */
	public String getArchiveStatus() {
		return this.archiveStatus;
	}

	/**
	 * @param submitStatus 
	 * 		the archive submission status to set
	 */
	public void setArchiveStatus(String archiveStatus) {
		if ( archiveStatus != null )
			this.archiveStatus = archiveStatus;
		else
			this.archiveStatus = "";
	}

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
			Date c1date = c1.getDataCheckDate();
			Date c2date = c2.getDataCheckDate();
			if ( c1date == c2date )
				return 0;
			if ( c1date == null )
				return -1;
			if ( c2date == null )
				return 1;
			return c1date.compareTo(c2date);
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
			Date c1date = c1.getMetaCheckDate();
			Date c2date = c2.getMetaCheckDate();
			if ( c1date == c2date )
				return 0;
			if ( c1date == null )
				return -1;
			if ( c2date == null )
				return 1;
			return c1date.compareTo(c2date);
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
