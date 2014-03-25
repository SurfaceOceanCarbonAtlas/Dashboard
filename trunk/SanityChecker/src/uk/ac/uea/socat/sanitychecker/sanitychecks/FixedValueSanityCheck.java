package uk.ac.uea.socat.sanitychecker.sanitychecks;

import java.util.List;

import uk.ac.uea.socat.sanitychecker.config.ConfigException;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfig;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfigItem;
import uk.ac.uea.socat.sanitychecker.config.SocatDataBaseException;
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

		try {
			SocatColumnConfig columnConfig = SocatColumnConfig.getInstance();
			if (null == columnConfig.getColumnConfig(itsColumnName)) {
				throw new SanityCheckException("Unrecognised column name '" + itsColumnName + "' in parameter to FixedValueSanityCheck");
			}
		} catch (ConfigException e) {
			throw new SanityCheckException("Unhandled error while checking FixedValueSanityCheck parameters", e);
		}
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
