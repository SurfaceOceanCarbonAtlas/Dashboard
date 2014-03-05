package uk.ac.uea.socat.sanitychecker.sanitychecks;

import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.Seconds;

import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfigItem;
import uk.ac.uea.socat.sanitychecker.config.SocatDataBaseException;
import uk.ac.uea.socat.sanitychecker.data.ColumnSpec;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;

public class ConstantSanityCheck extends SanityCheck {
	
	private String itsColumnName = null;
	
	private int itsMaxDuration = 0;
	
	private SocatDataRecord itsFirstRecord = null;
	
	private SocatDataRecord itsLastRecord = null;

	@Override
	public void initialise(List<String> parameters) throws SanityCheckException {
		if (parameters.size() < 2) {
			throw new SanityCheckException("Must supply two parameters: Column name and duration");
		}
		
		itsColumnName = parameters.get(0);
		
		try {
			itsMaxDuration = Integer.parseInt(parameters.get(1));
			if (itsMaxDuration <= 0) {
				throw new SanityCheckException("Max duration must be larger than zero");
			}
		} catch(NumberFormatException e) {
			throw new SanityCheckException("All speed parameters must be numeric");
		}
	}
	
	@Override
	public boolean checkParameters(ColumnSpec colSpec, Logger logger) throws SanityCheckException {
		boolean result = true;
		
		if (null == colSpec.getColumnInfo(itsColumnName)) {
			logger.fatal("Bad configuration for Constant sanity checker - unknown column '" + itsColumnName + "'");
			result = false;
		}
		
		return result;
	}

	@Override
	public void processRecord(SocatDataRecord record) throws SanityCheckException {
		
		if (recordOK(record)) {
			if (null == itsLastRecord) {
				itsFirstRecord = record;
				itsLastRecord = record;
			} else {
				
				if (equalsConstant(record)) {
					itsLastRecord = record;
				} else {
					// Check duration
					doDurationCheck();
					
					// Reset
					itsFirstRecord = record;
					itsLastRecord = record;
				}
			}
		}
	}
	
	@Override
	public void performFinalCheck() throws SanityCheckException {
		doDurationCheck();
	}
	

	private boolean equalsConstant(SocatDataRecord record) throws SanityCheckException {

		boolean result = false;
		
		try {
			double constantValue = Double.parseDouble(itsFirstRecord.getColumn(itsColumnName).getValue());
			double recordValue = Double.parseDouble(record.getColumn(itsColumnName).getValue());
			
			result = (constantValue == recordValue);
		} catch (NumberFormatException e) {
			throw new SanityCheckException("Cannot compare non-numeric values", e);
		}
		
		return result;
	}
	
	private boolean recordOK(SocatDataRecord record) {
		boolean result = true;
		
		if (record.getColumn(itsColumnName).getFlag() == SocatColumnConfigItem.BAD_FLAG || record.getDateFlag() == SocatColumnConfigItem.BAD_FLAG) {
			result = false;
		}
		
		return result;
	}
	
	private void doDurationCheck() throws SanityCheckException {

		if (null != itsFirstRecord && null != itsLastRecord) {
		
			double secondsDifference = Seconds.secondsBetween(itsFirstRecord.getTime(), itsLastRecord.getTime()).getSeconds();
			double minutesDifference = secondsDifference / 60.0;
			
			
			if (minutesDifference > itsMaxDuration) {
				try {
					String message = "Value for column is constant for longer than " + itsMaxDuration + " minutes";
					itsLastRecord.getColumn(itsColumnName).setFlag(SocatColumnConfigItem.BAD_FLAG, itsMessages, itsLastRecord.getLineNumber(), message);
				} catch (SocatDataBaseException e) {
					throw new SanityCheckException ("Error while setting flag on record", e);
				}
			}
		}
	}
}
