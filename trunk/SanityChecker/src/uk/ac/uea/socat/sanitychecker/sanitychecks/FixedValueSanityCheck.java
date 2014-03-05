package uk.ac.uea.socat.sanitychecker.sanitychecks;

import java.util.List;

import org.apache.log4j.Logger;

import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfigItem;
import uk.ac.uea.socat.sanitychecker.config.SocatDataBaseException;
import uk.ac.uea.socat.sanitychecker.data.ColumnSpec;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;

public class FixedValueSanityCheck extends SanityCheck {

	private String itsColumnName = null;
	
	private String itsFixedValue = null;
	
	@Override
	public void initialise(List<String> parameters) throws SanityCheckException {
		if (parameters.size() < 1) {
			throw new SanityCheckException("Must supply column name");
		}
		
		itsColumnName = parameters.get(0);
	}

	@Override
	public boolean checkParameters(ColumnSpec colSpec, Logger logger) throws SanityCheckException {
		boolean result = true;
		
		if (null == colSpec.getColumnInfo(itsColumnName)) {
			logger.fatal("Bad configuration for Fixed Value sanity checker - unknown column '" + itsColumnName + "'");
			result = false;
		}
		
		return result;
	}


	@Override
	public void processRecord(SocatDataRecord record)
			throws SanityCheckException {
		
		
		
		String value = record.getColumn(itsColumnName).getValue();
		
		// We don't check null values
		if (null != value) {
			if (null == itsFixedValue) {
				itsFixedValue = value;
			} else {
				if (value.compareTo(itsFixedValue) != 0) {
					try {
						String message = "Value for column has changed - it must be the same in all records";
						record.getColumn(itsColumnName).setFlag(SocatColumnConfigItem.BAD_FLAG, itsMessages, record.getLineNumber(), message);
					}
					catch (SocatDataBaseException e) {
						throw new SanityCheckException ("Error while setting flag on record", e);
					}
				}
			}
		}
	}
}
