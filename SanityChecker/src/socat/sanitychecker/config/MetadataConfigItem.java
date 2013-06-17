package socat.sanitychecker.config;

import org.apache.log4j.Logger;

/**
 * This class represents a single entry in the metadata configuration for the Sanity Checker.
 *
 */
public class MetadataConfigItem {

	/**
	 * The name of the metadata configuration item, as used in the header
	 * of SOCAT data submission files.
	 */
	private String itsName;
		
	/**
	 * Indicates whether or not this metadata item is generated automatically by the Sanity Checker.
	 * If the Sanity Checker is to generate the value, anything supplied in the header will be ignored
	 * and a warning generated.
	 */
	private boolean itMustGenerate;
	
	/**
	 * Indicates whether or not this metadata item is required
	 */
	private boolean itIsRequired;
	
	/**
	 * Indicates the group of items that constitute a required metadata entry.
	 * At least one of such a group is required.
	 */
	private String itsRequiredGroup;
	
	/**
	 * Indicates the type of data held in this metadata item.
	 */
	private int itsDataType;
	
	/**
	 * Indicates whether or not this item can span multiple lines in the
	 * data file header
	 */
	private boolean itIsMultiLine;
	
	/**
	 * The range limit of this metadata item
	 */
	private ConfigValueRange itsRange;
	
	/**
	 * The class to be used when constructing a Metadata item of this type
	 */
	@SuppressWarnings("rawtypes")
	private Class itsItemClass;
	
	/**
	 * The parameter to be passed to the generator function
	 */
	private String itsGeneratorParameter;
	
	/**
	 * Constructor for the metadata configuration object
	 */
	@SuppressWarnings("rawtypes")
	public MetadataConfigItem(String name, int dataType, boolean required, boolean generate,
			String requiredGroup, ConfigValueRange range, boolean multiline, Class itemClass,
			String generatorParameter, Logger logger) throws ConfigException {
		
		logger.trace("Creating Metadata Config Item " + name);
		
		itsName = name;
		itsDataType = dataType;
		itIsRequired = required;
		itMustGenerate = generate;
		itsRequiredGroup = requiredGroup;
		itsRange = range;
		itIsMultiLine = multiline;
		itsItemClass = itemClass;
		itsGeneratorParameter = generatorParameter;
	}
	
	/**
	 * Returns the name of this metadata item as it should appear in the data file
	 * @return The name of this metadata item
	 */
	public String getName() {
		return itsName;
	}
	
	/**
	 * Returns the data type of this metadata item.
	 * @return The data type of this metadata item
	 */
	public int getType() {
		return itsDataType;
	}
	
	/**
	 * Indicates whether or not this metadata item has an associated data range.
	 * @return {@code true} if a data range has been specified; {@code false} otherwise.
	 */
	public boolean hasRange() {
		return (itsRange != null);
	}
	
	/**
	 * Returns the data range object for this metadata item
	 * @return The data range specification
	 */
	public ConfigValueRange getRange() {
		return itsRange;
	}
	
	/**
	 * Indicates whether or not this metadata item is required
	 * @return {@code true} if the metadata item is required; {@code false} otherwise
	 */
	public boolean isRequired() {
		return itIsRequired;
	}
	
	/**
	 * Indicates whether or not this item is part of a group of items where at least
	 * one of them is required
	 * @return {@code true} if this item is part of a requirement group; {@code false} otherwise.
	 */
	public boolean isInRequiredGroup() {
		return (itsRequiredGroup != null && !itsRequiredGroup.equalsIgnoreCase(""));
	}
	
	/**
	 * Returns the name of the group of values that constitute a requirement (at least
	 * one of the values in the group is required).
	 * @return The name of the requirement group
	 */
	public String getRequiredGroup() {
		return itsRequiredGroup;
	}
	
	/**
	 * Indicates whether or not the value for this metadata item can
	 * span multiple lines in the header.
	 * @return {@code true} if the value can span multiple lines; {@code false} otherwise.
	 */
	public boolean isMultiline() {
		return itIsMultiLine;
	}
	
	/**
	 * Indicates whether or not the Sanity Checker must generate the metadata value itself. 
	 * @return {@code true} if the Sanity Checker will generate the value; {@code false} otherwise.
	 */
	public boolean autoGenerated() {
		return itMustGenerate;
	}
	
	/**
	 * Returns the class to be used for the metadata item.
	 * @return The class to be used for the metadata item
	 */
	@SuppressWarnings("rawtypes")
	public Class getItemClass() {
		return itsItemClass;
	}
	
	/**
	 * Returns the value to be passed to the metadata item's generator function.
	 * This only has meaning if {@code autoGenerated()) returns {@code true}.
	 * @return The value to be passed to the generator function.
	 */
	public String getGeneratorParameter() {
		return itsGeneratorParameter;
	}
	
	
	/**
	 * See if two configuration objects refer to the same metadata item.
	 * This is done by checking the name only. The passed in object can be either
	 * a {@code String} or another {@code MetadataConfigItem} object.
	 */
	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		
		if (obj instanceof String) {
			result = ((String) obj).equals(itsName);
		} else if (obj instanceof MetadataConfigItem) {
			result = ((MetadataConfigItem) obj).getName().equals(itsName);
		}
		
		return result;
	}
}
