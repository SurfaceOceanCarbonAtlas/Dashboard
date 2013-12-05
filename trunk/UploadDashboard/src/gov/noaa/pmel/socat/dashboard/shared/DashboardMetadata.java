/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.Comparator;
import java.util.TreeSet;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Currently represents an uploaded metadata file of unknown contents.
 * This may change in the future when contents are standardized.
 *  
 * @author Karl Smith
 */
public class DashboardMetadata implements Serializable, IsSerializable {

	private static final long serialVersionUID = -4312888451252120669L;

	boolean selected;
	String owner;
	String uploadFilename;
	String expocodeFilename;
	TreeSet<String> associatedCruises;

	/**
	 * Creates an empty metadata document record
	 */
	public DashboardMetadata() {
		selected = false;
		owner = "";
		uploadFilename = "";
		expocodeFilename = "";
		associatedCruises = new TreeSet<String>();
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
	 * 		the upload filename; never null, but may be empty
	 */
	public String getUploadFilename() {
		return uploadFilename;
	}

	/**
	 * @param uploadFilename 
	 * 		the upload filename to set; if null, an empty string is assigned
	 */
	public void setUploadFilename(String uploadFilename) {
		if ( uploadFilename != null )
			this.uploadFilename = uploadFilename;
		else
			this.uploadFilename = "";
	}

	/**
	 * @return 
	 * 		the expocodeFilename; never null, but may be empty
	 */
	public String getExpocodeFilename() {
		return expocodeFilename;
	}

	/**
	 * @param expocodeFilename 
	 * 		the expocode filename to set; if null, an empty string is assigned
	 */
	public void setExpocodeFilename(String expocodeFilename) {
		if ( expocodeFilename != null )
			this.expocodeFilename = expocodeFilename;
		else
			this.expocodeFilename = "";
	}

	/**
	 * @return 
	 * 		the expocodes of cruises associated with this metadata 
	 * 		document;  never null but may be empty.  The actual set 
	 * 		used in this object instance is returned.
	 */
	public TreeSet<String> getAssociatedCruises() {
		return associatedCruises;
	}

	/**
	 * @param associatedCruises 
	 * 		the expocodes of cruises to associate with this metadata.
	 * 		The set of expocodes in this object instance is cleared 
	 * 		and this set of expocodes, if not null, is then added.
	 */
	public void setAssociatedCruises(TreeSet<String> associatedCruises) {
		this.associatedCruises.clear();
		if ( associatedCruises != null )
			this.associatedCruises.addAll(associatedCruises);
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = Boolean.valueOf(selected).hashCode();
		result = result * prime + owner.hashCode();
		result = result * prime + uploadFilename.hashCode();
		result = result * prime + expocodeFilename.hashCode();
		result = result * prime + associatedCruises.hashCode();
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
		if ( ! owner.equals(other.owner) )
			return false;
		if ( ! uploadFilename.equals(other.uploadFilename) )
			return false;
		if ( ! expocodeFilename.equals(other.expocodeFilename) )
			return false;
		if ( ! associatedCruises.equals(other.associatedCruises) )
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DashboardMetadata" +
				"[ selected=" + Boolean.toString(selected) + 
				",\n  owner=" + owner + 
				",\n  uploadFilename=" + uploadFilename +
				",\n  expocodeFilename=" + expocodeFilename +
				",\n  associatedCruises=" + associatedCruises.toString() +
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

	/**
	 * Compare using the upload filename of the cruise metadata.
	 * Note that this is inconsistent with DashboardMetadata.equals 
	 * in that this is only examining one field of DashboardMetadata.
	 */
	public static Comparator<DashboardMetadata> uploadFilenameComparator =
			new Comparator<DashboardMetadata>() {
		@Override
		public int compare(DashboardMetadata m1, DashboardMetadata m2) {
			if ( m1 == m2 )
				return 0;
			if ( m1 == null )
				return -1;
			if ( m2 == null )
				return 1;
			return m1.getUploadFilename().compareTo(m2.getUploadFilename());
		}
	};

	/**
	 * Compare using the expocode filename of the cruise metadata.
	 * Note that this is inconsistent with DashboardMetadata.equals 
	 * in that this is only examining one field of DashboardMetadata.
	 */
	public static Comparator<DashboardMetadata> expocodeFilenameComparator =
			new Comparator<DashboardMetadata>() {
		@Override
		public int compare(DashboardMetadata m1, DashboardMetadata m2) {
			if ( m1 == m2 )
				return 0;
			if ( m1 == null )
				return -1;
			if ( m2 == null )
				return 1;
			return m1.getExpocodeFilename().compareTo(m2.getExpocodeFilename());
		}
	};

}
