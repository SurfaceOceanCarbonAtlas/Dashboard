package uk.ac.uea.socat.sanitychecker.sanitychecks;

import java.util.List;

import org.joda.time.DateTime;

import uk.ac.uea.socat.sanitychecker.Message;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;

public class TimeGapSanityCheck extends SanityCheck {
	
	private double itsGapLimit;
	
	private DateTime itsLastTime = null;

	public void initialise(List<String> parameters) throws SanityCheckException {
		if (parameters.size() < 1) {
			throw new SanityCheckException("Must provide upper time gap limit (in days)");
		}
		
		try {
			itsGapLimit = Double.parseDouble(parameters.get(0));
		} catch (NumberFormatException e) {
			throw new SanityCheckException("Time gap parameter must be numeric");
		}
	}
	
	public void processRecord(SocatDataRecord record) {
		
		if (null != itsLastTime) {
			DateTime recordTime = record.getTime();
			if (null != recordTime) {
				double gap = calcDayDiff(itsLastTime, recordTime);
				
				if (gap > itsGapLimit) {
					itsMessages.add(new Message(Message.DATA_MESSAGE, Message.WARNING, record.getLineNumber(), "Records are more than " + itsGapLimit + " days apart."));
				}
			}
		}
		
		// Record date ready for next record
		itsLastTime = record.getTime();
	}
	
	private double calcDayDiff(DateTime time1, DateTime time2) {
		long difference = time2.getMillis() - time1.getMillis();
		return (double) (difference / 3600000.0) / 24;
	}
}
