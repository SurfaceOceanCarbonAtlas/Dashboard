package uk.ac.uea.socat.sanitychecker.sanitychecks;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.Seconds;

import uk.ac.uea.socat.sanitychecker.config.ConfigException;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfig;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfigItem;
import uk.ac.uea.socat.sanitychecker.config.SocatDataBaseException;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;

public class ConstantSanityCheck extends SanityCheck {
	
	private String itsColumnName = null;
	
	private int itsMaxDuration = 0;
	
	private List<SocatDataRecord> itsRecords = new ArrayList<SocatDataRecord>();
	
	@Override
	public void initialise(List<String> parameters) throws SanityCheckException {
		if (parameters.size() < 2) {
			throw new SanityCheckException("Must supply two parameters: Column name and duration");
		}
		
		itsColumnName = parameters.get(0);
		
		try {
			SocatColumnConfig columnConfig = SocatColumnConfig.getInstance();
			if (null == columnConfig.getColumnConfig(itsColumnName)) {
				throw new SanityCheckException("Unrecognised column name '" + itsColumnName + "' in parameter to ConstantSanityCheck");
			}
		} catch (ConfigException e) {
			throw new SanityCheckException("Unhandled error while checking ConstantSanityCheck parameters", e);
		}
		
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
	public void processRecord(SocatDataRecord record) throws SanityCheckException {
		
		if (recordOK(record)) {
			if (itsRecords.size() == 0) {
				itsRecords.add(record);
			} else {
				if (equalsConstant(record)) {
					itsRecords.add(record);
				} else {
					doDurationCheck();
					
					itsRecords.clear();
					itsRecords.add(record);
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
			double currentValue = getRecordValue(itsRecords.get(0));
			double recordValue = getRecordValue(record);
			
			result = (currentValue == recordValue);
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

		if (itsRecords.size() > 1) {
		
			double secondsDifference = Seconds.secondsBetween(itsRecords.get(0).getTime(), itsRecords.get(itsRecords.size() - 1).getTime()).getSeconds();
			double minutesDifference = secondsDifference / 60.0;
			
			
			if (minutesDifference > itsMaxDuration) {
				try {
					String message = "Value for column is constant for longer than " + itsMaxDuration + " (" + minutesDifference + ") minutes";
					
					for (SocatDataRecord record : itsRecords) {
						record.getColumn(itsColumnName).setFlag(SocatColumnConfigItem.BAD_FLAG, itsMessages, record.getLineNumber(), message);
					}
				} catch (SocatDataBaseException e) {
					throw new SanityCheckException ("Error while setting flag on record", e);
				}
			}
		}
	}
	
	private double getRecordValue(SocatDataRecord record) {
		return Double.parseDouble(record.getColumn(itsColumnName).getValue());
	}
}
