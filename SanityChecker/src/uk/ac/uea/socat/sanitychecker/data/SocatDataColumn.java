package uk.ac.uea.socat.sanitychecker.data;

import java.lang.reflect.Method;

import uk.ac.exeter.QCRoutines.data.DataColumn;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfigItem;
import uk.ac.uea.socat.sanitychecker.data.calculate.DataCalculator;

/**
 * Represents a single SOCAT output column value
 */
public class SocatDataColumn extends DataColumn {
	
	private StandardColumnInfo itsColumnInfo;

	/**
	 * Creates a new, empty data field ready to be populated
	 * @param config
	 */
	public SocatDataColumn(SocatDataRecord parent, SocatColumnConfigItem config, StandardColumnInfo colInfo) {
		super(parent, config);
		itsColumnInfo = colInfo;
	}
	
	/**
	 * Returns the data source for this column
	 * @return The data source
	 */
	public int getDataSource() {
		return getConfigItem().getDataSource();
	}
	
	/**
	 * Returns the name of the metadata item to be used to populate this column
	 * @return The name of the metadata item to be used to populate this column
	 */
	public String getMetadataSourceName() {
		return getConfigItem().getMetadataName();
	}
	
	/**
	 * Returns the object containing the data calculation method for this column
	 * @return The object containing the data calculation method for this column
	 */
	public DataCalculator getCalculatorObject() {
		return getConfigItem().getCalculatorObject();
	}
	
	/**
	 * Returns the method to be invoked to calculate the data value for this column
	 * @return The method to be invoked to calculate the data value for this column
	 */
	public Method getCalculatorMethod() {
		return getConfigItem().getCalculatorMethod();
	}
	
	public int getInputColumnIndex() {
		int result = -1;
		
		if (null != itsColumnInfo) {
			result = itsColumnInfo.getInputColumnIndex();
		}
		
		return result;
	}
	
	public String getInputColumnName() {
		String result = null;
		
		if (null != itsColumnInfo) {
			result = itsColumnInfo.getInputColumnName();
		}
		
		return result;
	}
	
	private SocatColumnConfigItem getConfigItem() {
		return (SocatColumnConfigItem) columnConfig;
	}
	
	public String getRequiredGroup() {
		return getConfigItem().getRequiredGroup();
	}
}
