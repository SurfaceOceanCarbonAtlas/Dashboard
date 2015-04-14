/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.Comparator;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Currently represents an uploaded metadata file of unknown contents.
 * This may change in the future when contents are standardized.
 *  
 * @author Karl Smith
 */
public class DashboardMetadata implements Serializable, IsSerializable {

	private static final long serialVersionUID = 2996234104405680120L;

	/**
	 * The "upload filename" for all OME metadata files.  (The name of the 
	 * actual file on the server has the expocode prefixed to this name.)
	 */
	public static final String OME_FILENAME = "OME.xml";

	/**
	 * Separator between the filename and the upload timestamp 
	 * in additional document titles.
	 */
	private static final String TITLE_SEPARATOR = " ; ";

	protected boolean selected;
	protected String expocode;
	protected String filename;
	protected String uploadTimestamp;
	protected String owner;
	protected boolean conflicted;
	protected String version;

	/**
	 * Creates an empty metadata document record
	 */
	public DashboardMetadata() {
		selected = false;
		expocode = "";
		filename = "";
		uploadTimestamp = "";
		owner = "";
		conflicted = false;
		version = "";
	}

	/**
	 * Returns the additional documents title for this metadata.
	 * Normally this title is the filename, followed by a space, a semicolon, 
	 * another space, and the upload timestamp.  If the filename is empty,
	 * this title is empty.  If the upload timestamp is empty, the 
	 * title is just the filename.
	 */
	public String getAddlDocsTitle() {
		if ( filename.isEmpty() )
			return "";
		if ( uploadTimestamp.isEmpty() )
			return filename;
		return filename + TITLE_SEPARATOR + uploadTimestamp;
	}

	/**
	 * Returns the metadata filename and the upload timestamp
	 * given in the document title.
	 * 
	 * @param docTitle
	 * 		document title to parse
	 * @return
	 * 		string array of length two with the filename as the
	 * 		first string and the timestamp as the second filename.
	 * 		If the title is empty, both strings in the returned 
	 * 		array will be empty.
	 * 		If the title does not have a timestamp, the timestamp
	 * 		in the returned array will be empty.
	 */
	public static String[] splitAddlDocsTitle(String docTitle) {
		String[] pieces = docTitle.split(TITLE_SEPARATOR, 2);
		if ( pieces.length == 1 )
			pieces = new String[] { docTitle, "" };
		return pieces;
	}

	/**
	 * @return 
	 * 		if the metadata document is selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected 
	 * 		set whether this metadata document is selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return 
	 * 		the cruise expocode; never null, but may be empty
	 */
	public String getExpocode() {
		return expocode;
	}

	/**
	 * @param expocode 
	 * 		the cruise expocode to set; if null, an empty string is assigned
	 */
	public void setExpocode(String expocode) {
		if ( expocode != null )
			this.expocode = expocode;
		else
			this.expocode = "";
	}

	/**
	 * @return 
	 * 		the filename; never null, but may be empty
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename 
	 * 		the filename to set; if null, an empty string is assigned
	 */
	public void setFilename(String filename) {
		if ( filename != null )
			this.filename = filename;
		else
			this.filename = "";
	}

	/**
	 * @return 
	 * 		the upload timestamp; never null, but may be empty
	 */
	public String getUploadTimestamp() {
		return uploadTimestamp;
	}

	/**
	 * @param uploadTimestamp 
	 * 		the upload timestamp to set; if null, an empty string is assigned
	 */
	public void setUploadTimestamp(String uploadTimestamp) {
		if ( uploadTimestamp != null )
			this.uploadTimestamp = uploadTimestamp;
		else
			this.uploadTimestamp = "";
	}

	/**
	 * @return 
	 * 		the owner; never null, but may be empty
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner 
	 * 		the owner to set; if null, an empty string is assigned
	 */
	public void setOwner(String owner) {
		if ( owner != null )
			this.owner = owner;
		else
			this.owner = "";
	}

	/**
	 * @return 
	 *		{@code true} if conflicts have been detected in the metadata; {@code false} otherwise.
	 */
	public boolean isConflicted() {
		return conflicted;
	}

	/**
	 * @param conflicted 
	 * 		set whether or not there are any conflicts detected in the metadata object.
	 */
	public void setConflicted(boolean conflicted) {
		this.conflicted = conflicted;
	}

	/**
	 * @return the SOCAT version; never null, but may be empty if not assigned
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version 
	 * 		the SOCAT version to set; if null, an empty string is assigned
	 */
	public void setVersion(String version) {
		if ( version != null )
			this.version = version;
		else
			this.version = "";
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = Boolean.valueOf(selected).hashCode();
		result = result * prime + expocode.hashCode();
		result = result * prime + filename.hashCode();
		result = result * prime + uploadTimestamp.hashCode();
		result = result * prime + owner.hashCode();
		result = result * prime + Boolean.valueOf(conflicted).hashCode();
		result = result * prime + version.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! (obj instanceof DashboardMetadata) )
			return false;
		DashboardMetadata other = (DashboardMetadata) obj;

		if ( selected != other.selected )
			return false;
		if ( expocode != other.expocode )
			return false;
		if ( ! filename.equals(other.filename) )
			return false;
		if ( ! uploadTimestamp.equals(other.uploadTimestamp) )
			return false;
		if ( ! owner.equals(other.owner) )
			return false;
		if ( conflicted != other.conflicted )
			return false;
		if ( ! version.equals(other.version) )
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DashboardMetadata" +
				"[ selected=" + Boolean.toString(selected) + 
				",\n  expocode=" + expocode +
				",\n  filename=" + filename +
				",\n  uploadTimestamp=" + uploadTimestamp +
				",\n  owner=" + owner + 
				",\n  conflicted=" + conflicted + 
				",\n  version=" + version + 
				" ]";
	}

	/**
	 * Compare using the "selected" property of the metadata documents.
	 * Note that this is inconsistent with DashboardMetadata.equals
	 * in that this is only examining one field of DashboardMetadata.
	 */
	public static Comparator<DashboardMetadata> selectedComparator =
			new Comparator<DashboardMetadata>() {
		@Override
		public int compare(DashboardMetadata m1, DashboardMetadata m2) {
			if ( m1 == m2 )
				return 0;
			if ( m1 == null )
				return -1;
			if ( m2 == null )
				return 1;
			Boolean s1 = m1.isSelected();
			return s1.compareTo(m2.isSelected());
		}
	};

	/**
	 * Compare using the expocode of the cruise metadata.
	 * Note that this is inconsistent with DashboardMetadata.equals 
	 * in that this is only examining one field of DashboardMetadata.
	 */
	public static Comparator<DashboardMetadata> expocodeComparator =
			new Comparator<DashboardMetadata>() {
		@Override
		public int compare(DashboardMetadata m1, DashboardMetadata m2) {
			if ( m1 == m2 )
				return 0;
			if ( m1 == null )
				return -1;
			if ( m2 == null )
				return 1;
			return m1.getExpocode().compareTo(m2.getExpocode());
		}
	};

	/**
	 * Compare using the filename of the cruise metadata.
	 * Note that this is inconsistent with DashboardMetadata.equals 
	 * in that this is only examining one field of DashboardMetadata.
	 */
	public static Comparator<DashboardMetadata> filenameComparator =
			new Comparator<DashboardMetadata>() {
		@Override
		public int compare(DashboardMetadata m1, DashboardMetadata m2) {
			if ( m1 == m2 )
				return 0;
			if ( m1 == null )
				return -1;
			if ( m2 == null )
				return 1;
			return m1.getFilename().compareTo(m2.getFilename());
		}
	};

	/**
	 * Compare using the upload timestamp of the cruise metadata.
	 * Note that this is inconsistent with DashboardMetadata.equals 
	 * in that this is only examining one field of DashboardMetadata.
	 */
	public static Comparator<DashboardMetadata> uploadTimestampComparator =
			new Comparator<DashboardMetadata>() {
		@Override
		public int compare(DashboardMetadata m1, DashboardMetadata m2) {
			if ( m1 == m2 )
				return 0;
			if ( m1 == null )
				return -1;
			if ( m2 == null )
				return 1;
			return m1.getUploadTimestamp().compareTo(m2.getUploadTimestamp());
		}
	};

	/**
	 * Compare using the owner of the cruise metadata.
	 * Note that this is inconsistent with DashboardMetadata.equals 
	 * in that this is only examining one field of DashboardMetadata.
	 */
	public static Comparator<DashboardMetadata> ownerComparator =
			new Comparator<DashboardMetadata>() {
		@Override
		public int compare(DashboardMetadata m1, DashboardMetadata m2) {
			if ( m1 == m2 )
				return 0;
			if ( m1 == null )
				return -1;
			if ( m2 == null )
				return 1;
			return m1.getOwner().compareTo(m2.getOwner());
		}
	};
}
