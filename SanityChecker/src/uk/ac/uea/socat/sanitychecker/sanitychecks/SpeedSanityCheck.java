package uk.ac.uea.socat.sanitychecker.sanitychecks;

import java.util.List;

import org.joda.time.DateTime;

import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfigItem;
import uk.ac.uea.socat.sanitychecker.data.SocatDataColumn;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.messages.Message;
import uk.ac.uea.socat.sanitychecker.messages.MessageType;

/**
 * Sanity checker to check a ship's speed between two records
 */
public class SpeedSanityCheck extends SanityCheck {

	private static final String SPEED_ID = "SHIP_SPEED";
	
	private static MessageType SPEED_TYPE = null;
	
	private static final String BACKWARDS_TIME_ID = "BACKWARDS_TIME";
	
	private static MessageType BACKWARDS_TIME_TYPE = null;

	/**
	 * The radius of the earth in kilometres
	 */
	private static final double EARTH_RADIUS = 6367.5;
	
	/**
	 * The previous record processed. This distance between this and the current record
	 * is used to calculate the ship speed.
	 */
	private SocatDataRecord itsLastRecord = null;
	
	/**
	 * The limit at which a ship's speed is questionable
	 */
	private double itsQuestionableSpeedLimit = 0.0;
	
	/**
	 * The limit at which a ship's speed is considered bad
	 */
	private double itsBadSpeedLimit = 0.0;
	
	public SpeedSanityCheck() {
		super();
	}

	@Override
	public void initialise(List<String> parameters) throws SanityCheckException {
		if (parameters.size() < 2) {
			throw new SanityCheckException("Must supply two parameters: Questionable Speed Limit and Bad Speed Limit");
		}
		
		try {
			itsQuestionableSpeedLimit = Double.parseDouble(parameters.get(0));
			itsBadSpeedLimit = Double.parseDouble(parameters.get(1));
			
		} catch(NumberFormatException e) {
			throw new SanityCheckException("All speed parameters must be numeric");
		}
		
		if (itsQuestionableSpeedLimit > itsBadSpeedLimit) {
			throw new SanityCheckException("Bad speed limit must be >= Questionable speed limit"); 
		}
		
		if (null == BACKWARDS_TIME_TYPE) {
			BACKWARDS_TIME_TYPE = new MessageType(BACKWARDS_TIME_ID, "The timestamp is either identical to or before the previous record", "Times out of order");
		}

		if (null == SPEED_TYPE) {
			SPEED_TYPE = new MessageType(SPEED_ID, "Ship speed is faster than " + MessageType.VALID_VALUE_IDENTIFIER + "km/h", "Ship speed is too fast");
		}
	}
	
	@Override
	public void processRecord(SocatDataRecord record) throws SanityCheckException {

		// We only check records that haven't already been marked as bad
		if (recordOK(record)) {
			
			if (null != itsLastRecord) {
				double lastLon = itsLastRecord.getLongitude();
				double lastLat = itsLastRecord.getLatitude();
				DateTime lastTime = itsLastRecord.getTime();
				
				double thisLon = record.getLongitude();
				double thisLat = record.getLatitude();
				DateTime thisTime = record.getTime();
				
				if (null != lastTime && null != thisTime) {
				
					double distance = calcDistance(lastLon, lastLat, thisLon, thisLat);
					double hourDiff = calcHourDiff(lastTime, thisTime);
					
					if (hourDiff <= 0.0) {
						itsMessages.add(new Message(Message.DATE_TIME_COLUMN_INDEX, BACKWARDS_TIME_TYPE, Message.ERROR, record.getLineNumber(), null, null));
					} else if (calcSecondsDiff(lastTime, thisTime) > 1) {
						double speed = distance / hourDiff;
						if (speed > itsBadSpeedLimit) {
							itsMessages.add(new Message(Message.SHIP_SPEED_COLUMN_INDEX, SPEED_TYPE, Message.ERROR, record.getLineNumber(), Double.toString(speed), Double.toString(itsBadSpeedLimit)));
						} else if (speed > itsQuestionableSpeedLimit) {
							itsMessages.add(new Message(Message.SHIP_SPEED_COLUMN_INDEX, SPEED_TYPE, Message.WARNING, record.getLineNumber(), Double.toString(speed), Double.toString(itsQuestionableSpeedLimit)));
						}
					}
				}
			}
			
		
			itsLastRecord = record;
		}
	}

	/**
	 * Determines whether or not the pertinent flags in the record are bad. If they are,
	 * we don't process it.
	 * @param record The record to be checked
	 * @return {@code true} if the record is 'good' and can be checked; {@code false} otherwise.
	 */
	private boolean recordOK(SocatDataRecord record) {
		
		boolean result = true;
		
		SocatDataColumn longitudeColumn = record.getColumn(SocatDataRecord.LONGITUDE_COLUMN_NAME);
		if (longitudeColumn.getFlag() == SocatColumnConfigItem.BAD_FLAG) {
			result = false;
		}
		
		if (result) {
			SocatDataColumn latitudeColumn = record.getColumn(SocatDataRecord.LATITUDE_COLUMN_NAME);
			if (latitudeColumn.getFlag() == SocatColumnConfigItem.BAD_FLAG){
				result = false;
			}
		}
		
		if (result) {
			if (record.getDateFlag() == SocatColumnConfigItem.BAD_FLAG) {
				result = false;
			}
		}
		
		return result;
		
	}
	
	/**
	 * Calculate the distance between two points in kilometres
	 * @param lon1 The longitude of the first point
	 * @param lat1 The latitude of the first point
	 * @param lon2 The longitude of the second point
	 * @param lat2 The latitude of the second point
	 * @return The distance between the two points
	 */
	private double calcDistance(double lon1, double lat1, double lon2, double lat2) {
		
		double lon1Rad = calcRadians(lon1);
		double lat1Rad = calcRadians(lat1);
		double lon2Rad = calcRadians(lon2);
		double lat2Rad = calcRadians(lat2);
		
		double deltaLon = lon2Rad - lon1Rad;
		double deltaLat = lat2Rad - lat2Rad;
		
		double a = Math.pow(Math.sin(deltaLat / 2), 2) + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.pow(Math.sin(deltaLon / 2), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		
		return c * EARTH_RADIUS;
	}
	
	/**
	 * Convert degrees to radians
	 * @param degrees The degrees value to be converted
	 * @return The radians value
	 */
	private double calcRadians(double degrees) {
		return degrees * (Math.PI / 180);
	}
	
	/**
	 * Calculate the difference between two times in hours
	 * @param time1 The first time
	 * @param time2 The second time
	 * @return The difference between the two times
	 */
	private double calcHourDiff(DateTime time1, DateTime time2) {
		long difference = time2.getMillis() - time1.getMillis();
		return (double) difference / 3600000.0;
	}
	
	/**
	 * Calculate the difference between two times in seconds
	 * @param time1 The first time
	 * @param time2 The second time
	 * @return The difference between the two times
	 */
	private double calcSecondsDiff(DateTime time1, DateTime time2) {
		long difference = time2.getMillis() - time1.getMillis();
		return (double) difference / 1000.0;
	}
}
