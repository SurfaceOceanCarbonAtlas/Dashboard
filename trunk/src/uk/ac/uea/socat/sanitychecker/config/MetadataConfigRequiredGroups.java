package uk.ac.uea.socat.sanitychecker.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * There are some groups of metadata items for which one or more must exist.
 * This class is used to maintain the list of those groups.
 */
public class MetadataConfigRequiredGroups {
	
	/**
	 * The set of grouped metadata items. Each group has a name (the key) and
	 * a list of metadata items, at least one of which must be present in any
	 * file's metadata.
	 */
	private HashMap<String, List<String>> itsGroups;
	
	/**
	 * Initialise all variables
	 */
	protected MetadataConfigRequiredGroups() {
		itsGroups = new HashMap<String, List<String>>();
	}
	
	/**
	 * Add a metadata item to a specified group. If the group doesn't already exist,
	 * it is created.
	 * @param group The name of the group
	 * @param name The name of the item to be added to that group
	 */
	protected void addGroupEntry(String group, String name) {
		List<String> groupEntry = itsGroups.get(group.toLowerCase());
		
		if (null == groupEntry) {
			groupEntry = new ArrayList<String>();
			itsGroups.put(group.toLowerCase(), groupEntry);
		}
		
		if (!groupEntry.contains(name.toLowerCase())) {
			groupEntry.add(name.toLowerCase());
		}
	}
	
	/**
	 * Returns the list of metadata items for a specified group.
	 * @param name The name of the group
	 * @return The list of metadata items in that group
	 */
	public List<String> getGroupedItemNames(String name) {
		return itsGroups.get(name.toLowerCase());
	}
	
	/**
	 * Returns an iterator that will step through all configured groups
	 * @return The iterator for all configured groups
	 */
	public Set<String> getGroupNames() {
		return itsGroups.keySet();
	}

}
