package uk.ac.uea.socat.sanitychecker.metadata;

import java.text.ParseException;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import uk.ac.uea.socat.sanitychecker.config.MetadataConfigItem;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeHandler;


/**
 * Implementation of the {@code MetadataItem} class
 * to find specific columns in the data
 */
public class ColumnExistsMetadataItem extends MetadataItem {
	
	/**
	 * Flag to indicate the existence of a column
	 */
	private boolean columnExists = false;
	
	/**
	 * Constructs a metadata item object.
	 * @param config The configuration for the metadata item
	 * @param value The value of the metadata item
	 * @throws ParseException If the supplied in value could not be parsed into the correct data type
	 */
	public ColumnExistsMetadataItem(MetadataConfigItem config, int line, Logger logger) throws ParseException {
		super(config, line, logger);
		itCanGenerate = true;
		itCanGenerateFromOneRecord = true;
	}

	@Override
	public void generateValue(DateTimeHandler dateTimeHandler) throws MetadataException {
		setValue(columnExists);
	}

	@Override
	public void processRecordForValue(Map<String, MetadataItem> metadataSet, SocatDataRecord record) throws MetadataException {
		Set<String> columnNames = record.getColumnNames();
		columnExists = columnNames.contains(itsConfigItem.getGeneratorParameter());
	}
}
