/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.HashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents a QC event giving a flag (or just a comment) on a region of a cruise. 
 * Note that the inherited id field is ignored in the hashCode and equals methods.
 * 
 * @author Karl Smith
 */
public class SocatQCEvent extends SocatEvent implements Serializable, IsSerializable {

	private static final long serialVersionUID = -2998774807126254182L;

	// All possible QC flags
	public static final Character QC_A_FLAG = 'A';
	public static final Character QC_B_FLAG = 'B';
	public static final Character QC_C_FLAG = 'C';
	public static final Character QC_D_FLAG = 'D';
	public static final Character QC_E_FLAG = 'E';
	public static final Character QC_F_FLAG = 'F';
	public static final Character QC_COMMENT = 'H';
	public static final Character QC_NEW_FLAG = 'N';
	public static final Character QC_PREVIEW_FLAG = 'P';
	public static final Character QC_CONFLICT_FLAG = 'Q';
	public static final Character QC_RENAMED_FLAG = 'R';
	public static final Character QC_SUSPEND_FLAG = 'S';
	public static final Character QC_UPDATED_FLAG = 'U';
	public static final Character QC_EXCLUDE_FLAG = 'X';

	// Cruise QC strings - cruises that can be modified
	public static final String QC_STATUS_NOT_SUBMITTED = "";
	public static final String QC_STATUS_PREVIEW = "Previewing";
	public static final String QC_STATUS_SUSPENDED = "Suspended";
	public static final String QC_STATUS_EXCLUDED = "Excluded";

	// Cruise QC strings - cruises that cannot be modified
	public static final String QC_STATUS_SUBMITTED = "Submitted";
	public static final String QC_STATUS_ACCEPTED_A = "Flag A";
	public static final String QC_STATUS_ACCEPTED_B = "Flag B";
	public static final String QC_STATUS_ACCEPTED_C = "Flag C";
	public static final String QC_STATUS_ACCEPTED_D = "Flag D";
	public static final String QC_STATUS_ACCEPTED_E = "Flag E";
	public static final String QC_STATUS_UNACCEPTABLE = "Unacceptable";
	public static final String QC_STATUS_CONFLICT = "Conflict";
	public static final String QC_STATUS_RENAMED = "Renamed";

	/**
	 * Map of QC status flag characters to QC status strings
	 */
	public static final HashMap<Character,String> FLAG_STATUS_MAP = 
			new HashMap<Character,String>();
	static {
		FLAG_STATUS_MAP.put(QC_A_FLAG, QC_STATUS_ACCEPTED_A);
		FLAG_STATUS_MAP.put(QC_B_FLAG, QC_STATUS_ACCEPTED_B);
		FLAG_STATUS_MAP.put(QC_C_FLAG, QC_STATUS_ACCEPTED_C);
		FLAG_STATUS_MAP.put(QC_D_FLAG, QC_STATUS_ACCEPTED_D);
		FLAG_STATUS_MAP.put(QC_E_FLAG, QC_STATUS_ACCEPTED_E);
		FLAG_STATUS_MAP.put(QC_F_FLAG, QC_STATUS_UNACCEPTABLE);
		FLAG_STATUS_MAP.put(QC_NEW_FLAG, QC_STATUS_SUBMITTED);
		FLAG_STATUS_MAP.put(QC_PREVIEW_FLAG, QC_STATUS_PREVIEW);
		FLAG_STATUS_MAP.put(QC_CONFLICT_FLAG, QC_STATUS_CONFLICT);
		FLAG_STATUS_MAP.put(QC_RENAMED_FLAG, QC_STATUS_RENAMED);
		FLAG_STATUS_MAP.put(QC_SUSPEND_FLAG, QC_STATUS_SUSPENDED);
		FLAG_STATUS_MAP.put(QC_UPDATED_FLAG, QC_STATUS_SUBMITTED);
		FLAG_STATUS_MAP.put(QC_EXCLUDE_FLAG, QC_STATUS_EXCLUDED);
	}

	/**
	 * Map of QC status strings to QC status flag characters 
	 * QC_STATUS_SUBMITTED is mapped to QC_UPDATED_FLAG
	 */
	public static final HashMap<String,Character> STATUS_FLAG_MAP = 
			new HashMap<String,Character>();
	static {
		STATUS_FLAG_MAP.put(QC_STATUS_ACCEPTED_A, QC_A_FLAG);
		STATUS_FLAG_MAP.put(QC_STATUS_ACCEPTED_B, QC_B_FLAG);
		STATUS_FLAG_MAP.put(QC_STATUS_ACCEPTED_C, QC_C_FLAG);
		STATUS_FLAG_MAP.put(QC_STATUS_ACCEPTED_D, QC_D_FLAG);
		STATUS_FLAG_MAP.put(QC_STATUS_ACCEPTED_E, QC_E_FLAG);
		STATUS_FLAG_MAP.put(QC_STATUS_UNACCEPTABLE, QC_F_FLAG);
		STATUS_FLAG_MAP.put(QC_STATUS_PREVIEW, QC_PREVIEW_FLAG);
		STATUS_FLAG_MAP.put(QC_STATUS_CONFLICT, QC_CONFLICT_FLAG);
		STATUS_FLAG_MAP.put(QC_STATUS_RENAMED, QC_RENAMED_FLAG);
		STATUS_FLAG_MAP.put(QC_STATUS_SUSPENDED, QC_SUSPEND_FLAG);
		STATUS_FLAG_MAP.put(QC_STATUS_SUBMITTED, QC_UPDATED_FLAG);
		STATUS_FLAG_MAP.put(QC_STATUS_EXCLUDED, QC_EXCLUDE_FLAG);
	}
	Character flag;
	Character regionID;

	/**
	 * Creates an empty QC flag as a comment in the global region 
	 */
	public SocatQCEvent() {
		super();
		flag = QC_COMMENT;
		regionID = DataLocation.GLOBAL_REGION_ID;
	}

	/**
	 * @return 
	 * 		the flag; never null
	 */
	public Character getFlag() {
		return flag;
	}

	/**
	 * @param flag 
	 * 		the flag to set; if null {@link #QC_COMMENT} is assigned
	 */
	public void setFlag(Character flag) {
		if ( flag == null )
			this.flag = QC_COMMENT;
		else
			this.flag = flag;
	}

	/**
	 * @return 
	 * 		the region ID for this QC flag; never null
	 */
	public Character getRegionID() {
		return regionID;
	}

	/**
	 * @param regionID 
	 * 		the region ID to set for this QC flag; 
	 * 		if null, {@link DataLocation#GLOBAL_REGION_ID} is assigned
	 */
	public void setRegionID(Character regionID) {
		if ( regionID == null )
			this.regionID = DataLocation.GLOBAL_REGION_ID;
		else
			this.regionID = regionID;
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = super.hashCode();
		result = result * prime + flag.hashCode();
		result = result * prime + regionID.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! (obj instanceof SocatQCEvent) )
			return false;
		SocatQCEvent other = (SocatQCEvent) obj;

		if ( ! super.equals(other) )
			return false;
		if ( ! flag.equals(other.flag) )
			return false;
		if ( ! regionID.equals(other.regionID) )
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "SocatQCEvent" +
				"[\n    id=" + id.toString() +
				",\n    flag='" + flag.toString() + "'" +
				",\n    flagDate=" + flagDate.toString() + 
				",\n    expocode=" + expocode + 
				",\n    restoredSocatVersion=" + socatVersion.toString() + 
				",\n    regionID='" + regionID.toString() + "'" + 
				",\n    username=" + username + 
				",\n    realname=" + realname + 
				",\n    comment=" + comment + 
				"]";
	}

}
