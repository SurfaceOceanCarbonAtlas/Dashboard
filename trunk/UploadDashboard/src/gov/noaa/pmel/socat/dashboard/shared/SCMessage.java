/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.Comparator;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Parts of a SanityChecker Message object 
 * that needs to be transferred to the client for display.
 *  
 * @author Karl Smith
 */
public class SCMessage implements Serializable, IsSerializable {

	private static final long serialVersionUID = -2717225935448011731L;

	private static final double MAX_ABSOLUTE_ERROR = 1.0E-4;

	/**
	 * Enumerated type for the sanity checker message type 
	 * (message about metadata or the data values)
	 */
	public enum SCMsgType implements Serializable, IsSerializable {
		UNKNOWN,
		METADATA,
		DATA_QUESTIONABLE_VALUE,
		DATA_BAD_VALUE,
		DATA_TIME_SEQUENCE,
		DATA_TIME_VALUE,
		DATA_QUESTIONABLE_SPEED,
		DATA_BAD_SPEED,
		DATA_MISSING,
		DATA_CONSTANT,
		DATA_OUTLIER,
		DATA_GAP,
		DATA_METADATA_NOT_SAME,
		DATA_ERROR,
	}

	/**
	 * Enumerated type for the severity of the issue in the message
	 */
	public enum SCMsgSeverity implements Serializable, IsSerializable {
		UNKNOWN,
		ERROR,
		WARNING,
	}

	SCMsgType type;
	SCMsgSeverity severity;
	int rowNumber;
	String timestamp;
	double longitude;
	double latitude;
	int colNumber;
	String colName;
	String explanation;

	/**
	 * Create an empty message of an unknown type and severity
	 */
	public SCMessage() {
		type = SCMsgType.UNKNOWN;
		severity = SCMsgSeverity.UNKNOWN;
		rowNumber = -1;
		timestamp = "";
		longitude = Double.NaN;
		latitude = Double.NaN;
		colNumber = -1;
		colName = "";
		explanation = "";
	}

	/**
	 * @return 
	 * 		the message type; never null
	 */
	public SCMsgType getType() {
		return type;
	}

	/**
	 * @param type 
	 * 		the message type to set; 
	 * 		if null, {@link SCMsgType#UNKNOWN} is assigned 
	 */
	public void setType(SCMsgType type) {
		if ( type == null )
			this.type = SCMsgType.UNKNOWN;
		else
			this.type = type;
	}

	/**
	 * @return 
	 * 		the severity of the message; never null
	 */
	public SCMsgSeverity getSeverity() {
		return severity;
	}

	/**
	 * @param severity 
	 * 		the severity of the message to set;
	 * 		if null, {@link SCMsgSeverity#UNKNOWN} is assigned
	 */
	public void setSeverity(SCMsgSeverity severity) {
		if ( severity == null )
			this.severity = SCMsgSeverity.UNKNOWN;
		else
			this.severity = severity;
	}

	/**
	 * @return 
	 * 		the data row number, or -1 for metadata messages
	 */
	public int getRowNumber() {
		return rowNumber;
	}

	/**
	 * @param rowNumber 
	 * 		the data row number to set; 
	 * 		for metadata, assign -1 as the row number
	 */
	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}

	/**
	 * @return 
	 * 		the timestamp; never null but may be empty
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp 
	 * 		the timestamp to set;
	 * 		if null, an empty string is assigned
	 */
	public void setTimestamp(String timestamp) {
		if ( timestamp == null )
			this.timestamp = "";
		else
			this.timestamp = timestamp;
	}

	/**
	 * @return 
	 * 		the longitude; may be {@link Double#NaN} if missing
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude 
	 * 		the longitude to set; if infinite, Double.NaN is assigned
	 */
	public void setLongitude(double longitude) {
		if ( Double.isInfinite(longitude) || Double.isNaN(longitude) )
			this.longitude = Double.NaN;
		else
			this.longitude = longitude;
	}

	/**
	 * @return 
	 * 		the latitude; may be {@link Double#NaN} if missing
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude 
	 * 		the latitude to set; if infinite, Double.NaN is assigned
	 */
	public void setLatitude(double latitude) {
		if ( Double.isInfinite(latitude) || Double.isNaN(latitude) )
			this.latitude = Double.NaN;
		else
			this.latitude = latitude;
	}

	/**
	 * @return 
	 * 		the input data column number; or -1 if ambiguous
	 */
	public int getColNumber() {
		return colNumber;
	}

	/**
	 * @param colNumber 
	 * 		the input data column number to set;
	 * 		if ambiguous, assign -1 as the column number
	 */
	public void setColNumber(int colNumber) {
		this.colNumber = colNumber;
	}

	/**
	 * @return 
	 * 		the input data column name; 
	 * 		never null, but may be empty if unknown or ambiguous
	 */
	public String getColName() {
		return colName;
	}

	/**
	 * @param colName 
	 * 		the input data column name to set; 
	 * 		if null, an empty string is assigned
	 */
	public void setColName(String colName) {
		if ( colName == null )
			this.colName = "";
		else
			this.colName = colName;
	}

	/**
	 * @return 
	 * 		the sanity checker explanation of the issue;
	 * 		never null but may be empty
	 */
	public String getExplanation() {
		return explanation;
	}

	/**
	 * @param explanation 
	 * 		the sanity checker explanation of the issue to set;
	 * 		if null, an empty string is assigned
	 */
	public void setExplanation(String explanation) {
		if ( explanation == null )
			this.explanation = "";
		else
			this.explanation = explanation;
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = type.hashCode();
		// Do not use floating point values for the hash code
		// since they do not have to be exactly equal
		result = result * prime + severity.hashCode();
		result = result * prime + rowNumber;
		result = result * prime + timestamp.hashCode();
		result = result * prime + colNumber;
		result = result * prime + colName.hashCode();
		result = result * prime + explanation.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! (obj instanceof SCMessage) )
			return false;
		SCMessage other = (SCMessage) obj;

		if ( type != other.type )
			return false;
		if ( severity != other.severity )
			return false;
		if ( rowNumber != other.rowNumber )
			return false;
		if ( ! timestamp.equals(other.timestamp) )
			return false;
		if ( colNumber != other.colNumber )
			return false;
		if ( ! colName.equals(other.colName) )
			return false;
		if ( ! explanation.equals(other.explanation) )
			return false;

		// Returns true if both NaN
		if ( ! DashboardUtils.closeTo(latitude, other.latitude, 0, MAX_ABSOLUTE_ERROR) )
			return false;

		// Longitudes have modulo 360.0, so 359.999999 is close to 0.0
		// NaN is checked here since we are adding values to them. 
		if ( ! Double.isNaN(longitude) ) {
			if ( Double.isNaN(other.longitude) )
				return false;
			if ( ! DashboardUtils.closeTo(longitude, other.longitude, 0.0, MAX_ABSOLUTE_ERROR) )
				if ( ! DashboardUtils.closeTo(longitude + 360.0, other.longitude, 0.0, MAX_ABSOLUTE_ERROR) )
					if ( ! DashboardUtils.closeTo(longitude, other.longitude + 360.0, 0.0, MAX_ABSOLUTE_ERROR) )
						return false;
		}
		else {
			if ( ! Double.isNaN(other.longitude) )
				return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "SCMessage[type=" + type + ", severity=" + severity + 
				", rowNumber=" + rowNumber + ", timestamp=" + timestamp +
				", longitude=" + longitude + ", latitude=" + latitude +
				", colNumber=" + colNumber + ", colName=" + colName + 
				", explanation=" + explanation + "]";
	}

	/**
	 * Compare using the severity of the messages.
	 * Note that this is inconsistent with SCMessage.equals 
	 * in that this is only examining one field of SCMessage.
	 */
	public static Comparator<SCMessage> severityComparator = 
										new Comparator<SCMessage>() {
		@Override
		public int compare(SCMessage msg1, SCMessage msg2) {
			if ( msg1 == msg2 )
				return 0;
			if ( msg1 == null )
				return -1;
			if ( msg2 == null )
				return 1;
			return msg1.getSeverity().compareTo(msg2.getSeverity());
		}
	};

	/**
	 * Compare using the row number of the messages.
	 * Note that this is inconsistent with SCMessage.equals 
	 * in that this is only examining one field of SCMessage.
	 */
	public static Comparator<SCMessage> rowNumComparator = 
										new Comparator<SCMessage>() {
		@Override
		public int compare(SCMessage msg1, SCMessage msg2) {
			if ( msg1 == msg2 )
				return 0;
			if ( msg1 == null )
				return -1;
			if ( msg2 == null )
				return 1;
			return ( msg1.getRowNumber() - msg2.getRowNumber() );
		}
	};

	/**
	 * Compare using the timestamp of the messages.
	 * Note that this is inconsistent with SCMessage.equals 
	 * in that this is only examining one field of SCMessage.
	 */
	public static Comparator<SCMessage> timestampComparator = 
										new Comparator<SCMessage>() {
		@Override
		public int compare(SCMessage msg1, SCMessage msg2) {
			if ( msg1 == msg2 )
				return 0;
			if ( msg1 == null )
				return -1;
			if ( msg2 == null )
				return 1;
			return msg1.getTimestamp().compareTo(msg2.getTimestamp());
		}
	};

	/**
	 * Compare using the longitude of the messages.
	 * Note that this is inconsistent with SCMessage.equals 
	 * in that this is only examining one field of SCMessage.
	 * This also does not allow some "slop" for two floating
	 * point values to be equal, nor does it take into account
	 * the modulo 360 nature of longitudes.
	 */
	public static Comparator<SCMessage> longitudeComparator = 
										new Comparator<SCMessage>() {
		@Override
		public int compare(SCMessage msg1, SCMessage msg2) {
			if ( msg1 == msg2 )
				return 0;
			if ( msg1 == null )
				return -1;
			if ( msg2 == null )
				return 1;
			if ( Double.isNaN(msg1.getLongitude()) ) {
				if ( Double.isNaN(msg2.getLongitude()) )
					return 0;
				return -1;
			}
			if ( Double.isNaN(msg2.getLongitude()) )
				return 1;
			if ( msg1.getLongitude() > msg2.getLongitude() )
				return 1;
			if ( msg1.getLongitude() < msg2.getLongitude() )
				return -1;
			return 0;
		}
	};

	/**
	 * Compare using the latitude of the messages.
	 * Note that this is inconsistent with SCMessage.equals 
	 * in that this is only examining one field of SCMessage.
	 * This also does not allow some "slop" for two floating
	 * point values to be equal.
	 */
	public static Comparator<SCMessage> latitudeComparator = 
										new Comparator<SCMessage>() {
		@Override
		public int compare(SCMessage msg1, SCMessage msg2) {
			if ( msg1 == msg2 )
				return 0;
			if ( msg1 == null )
				return -1;
			if ( msg2 == null )
				return 1;
			if ( Double.isNaN(msg1.getLatitude()) ) {
				if ( Double.isNaN(msg2.getLatitude()) )
					return 0;
				return -1;
			}
			if ( Double.isNaN(msg2.getLatitude()) )
				return 1;
			if ( msg1.getLatitude() > msg2.getLatitude() )
				return 1;
			if ( msg1.getLatitude() < msg2.getLatitude() )
				return -1;
			return 0;
		}
	};

	/**
	 * Compare using the column number of the messages.
	 * Note that this is inconsistent with SCMessage.equals 
	 * in that this is only examining one field of SCMessage.
	 */
	public static Comparator<SCMessage> colNumComparator = 
										new Comparator<SCMessage>() {
		@Override
		public int compare(SCMessage msg1, SCMessage msg2) {
			if ( msg1 == msg2 )
				return 0;
			if ( msg1 == null )
				return -1;
			if ( msg2 == null )
				return 1;
			return ( msg1.getColNumber() - msg2.getColNumber() );
		}
	};

	/**
	 * Compare using the column name of the messages.
	 * Note that this is inconsistent with SCMessage.equals 
	 * in that this is only examining one field of SCMessage.
	 */
	public static Comparator<SCMessage> colNameComparator = 
										new Comparator<SCMessage>() {
		@Override
		public int compare(SCMessage msg1, SCMessage msg2) {
			if ( msg1 == msg2 )
				return 0;
			if ( msg1 == null )
				return -1;
			if ( msg2 == null )
				return 1;
			return msg1.getColName().compareTo(msg2.getColName());
		}
	};

	/**
	 * Compare using the column name of the messages.
	 * Note that this is inconsistent with SCMessage.equals 
	 * in that this is only examining one field of SCMessage.
	 */
	public static Comparator<SCMessage> explanationComparator = 
										new Comparator<SCMessage>() {
		@Override
		public int compare(SCMessage msg1, SCMessage msg2) {
			if ( msg1 == msg2 )
				return 0;
			if ( msg1 == null )
				return -1;
			if ( msg2 == null )
				return 1;
			return msg1.getExplanation().compareTo(msg2.getExplanation());
		}
	};

}
