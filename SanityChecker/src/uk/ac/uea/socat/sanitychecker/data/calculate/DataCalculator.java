package uk.ac.uea.socat.sanitychecker.data.calculate;

import uk.ac.uea.socat.metadata.OmeMetadata.OmeMetadata;
import uk.ac.uea.socat.metadata.OmeMetadata.OmeMetadataException;
import uk.ac.uea.socat.sanitychecker.data.SocatDataException;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeHandler;

/**
 * Interface providing prototypes for methods to dynamically calculate data values
 */
public interface DataCalculator {
	
	/**
	 * Calculates the data value
	 * @param metadata The metadata from the data file
	 * @param record The data record being processed
	 * @param colIndex The index of the column whose value is being calculated
	 * @param colName The name of the column whose value is being calculated
	 * @return The calculated value
	 * @throws SocatDataException If the value cannot be calculated
	 */
	public String calculateDataValue(OmeMetadata metadata, SocatDataRecord record, int colIndex, String colName, DateTimeHandler dateTimeHander) throws SocatDataException, OmeMetadataException;
}
