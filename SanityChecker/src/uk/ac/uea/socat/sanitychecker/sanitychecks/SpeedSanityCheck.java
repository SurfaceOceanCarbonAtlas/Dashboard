package uk.ac.uea.socat.sanitychecker.sanitychecks;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import uk.ac.uea.socat.sanitychecker.Message;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfigItem;
import uk.ac.uea.socat.sanitychecker.data.SocatDataColumn;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;

public class SpeedSanityCheck extends SanityCheck {

	private static final double EARTH_RADIUS = 6367.5;
	
	private SocatDataRecord itsLastRecord = null;
	
	private double itsQuestionableSpeedLimit = 0.0;
	
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
	}

	@Override
	public void processRecord(SocatDataRecord record) {
		
		if (recordOK(record)) {
		
			if (null != itsLastRecord) {
				double lastLon = itsLastRecord.getLongitude();
				double lastLat = itsLastRecord.getLatitude();
				DateTime lastTime = itsLastRecord.getTime();
				
				double thisLon = record.getLongitude();
				double thisLat = record.getLatitude();
				DateTime thisTime = record.getTime();
				
				double distance = calcDistance(lastLon, lastLat, thisLon, thisLat);
				double hourDiff = calcHourDiff(lastTime, thisTime);
				
				if (hourDiff <= 0.0) {
					itsMessages.add(new Message(Message.DATA_MESSAGE, Message.ERROR, record.getLineNumber(), "Zero or negative time between measurements: cannot calculate speed"));
				} else {
					double speed = distance / hourDiff;
					if (speed > itsBadSpeedLimit) {
						itsMessages.add(new Message(Message.DATA_MESSAGE, Message.ERROR, record.getLineNumber(), "Ship speed between measurements is " + speed + "km/h: should be <= " + itsBadSpeedLimit + "km/h"));
					} else if (speed > itsQuestionableSpeedLimit) {
						itsMessages.add(new Message(Message.DATA_MESSAGE, Message.WARNING, record.getLineNumber(), "Ship speed between measurements is " + speed + "km/h: should be <= " + itsQuestionableSpeedLimit + "km/h"));
					}
				}
			}
			
		
			itsLastRecord = record;
		}
	}
	
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
	
	private double calcRadians(double degrees) {
		return degrees * (Math.PI / 180);
	}
	
	private double calcHourDiff(DateTime time1, DateTime time2) {
		Seconds diffSeconds = Seconds.secondsBetween(time1, time2);
		return ((double) diffSeconds.getSeconds()) / 3600.0;
	}
}
