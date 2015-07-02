package uk.ac.uea.socat.omemetadata.checker;

import uk.ac.uea.socat.omemetadata.OmeMetadata;

/**
 * Checks OmeMetadata objects for errors
 * 
 * @author uuk07qzu
 */
public class MetadataChecker {

	public static MetadataCheckResult checkMetadata(OmeMetadata metadata) {
		MetadataCheckResult result = new MetadataCheckResult();
		result.setCheckPassed(!metadata.isDraft());
		return result;
	}
}
