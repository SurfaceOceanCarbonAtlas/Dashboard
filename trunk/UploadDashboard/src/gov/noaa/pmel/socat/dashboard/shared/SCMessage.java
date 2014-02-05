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

	private static final long serialVersionUID = 7328022666368906828L;

	/**
	 * Enumerated type for the sanity checker message type 
	 * (message about metadata or the data values)
	 */
	public enum SCMsgType implements Serializable, IsSerializable {
		UNKNOWN,
		METADATA,
		DATA,
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
	int colNumber;
	String colName;
	String explanation;

	public SCMessage() {
		type = SCMsgType.UNKNOWN;
		severity = SCMsgSeverity.UNKNOWN;
		rowNumber = -1;
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
		result = result * prime + severity.hashCode();
		result = result * prime + rowNumber;
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
		if ( colNumber != other.colNumber )
			return false;
		if ( ! colName.equals(other.colName) )
			return false;
		if ( ! explanation.equals(other.explanation) )
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SCMessage[type=" + type + ", " + "severity=" + severity + 
				", rowNumber=" + rowNumber + ", colNumber=" + colNumber + 
				", colName=" + colName + ", explanation=" + explanation + "]";
	}

	/**
	 * Compare using the types of the messages.
	 * Note that this is inconsistent with SCMessage.equals 
	 * in that this is only examining one field of SCMessage.
	 */
	public static Comparator<SCMessage> typeComparator = 
										new Comparator<SCMessage>() {
		@Override
		public int compare(SCMessage msg1, SCMessage msg2) {
			if ( msg1 == msg2 )
				return 0;
			if ( msg1 == null )
				return -1;
			if ( msg2 == null )
				return 1;
			return msg1.getType().compareTo(msg2.getType());
		}
	};

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
			return Integer.valueOf(msg1.getRowNumber())
						  .compareTo(msg2.getRowNumber());
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
			return Integer.valueOf(msg1.getColNumber())
						  .compareTo(msg2.getColNumber());
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
