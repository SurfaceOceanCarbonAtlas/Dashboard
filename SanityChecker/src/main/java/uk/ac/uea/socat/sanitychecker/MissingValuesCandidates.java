package uk.ac.uea.socat.sanitychecker;

import java.util.HashMap;
import java.util.Map;

/**
 * A utility class for logging candidates for numeric column values
 * that equate to the missing value, and establishing the most likely
 * such candidate.
 * 
 * The most likely candidate is the value that appears the most times.
 */
public class MissingValuesCandidates {

	/**
	 * Storage for counting occurrences of missing value candidates
	 */
	private Map<String, Integer> itsCandidates;
	
	/**
	 * Simple constructor - initialises the data structure
	 */
	public MissingValuesCandidates() {
		itsCandidates = new HashMap<String, Integer>();
	}
	
	/**
	 * Add a value to the list of candidates. If the value hasn't
	 * already been added, it is given a count of one. Otherwise the
	 * count for that value is incremented
	 * @param value The missing value candidate
	 */
	public void add(String value) {
		Integer existing = itsCandidates.get(value);
		if (null == existing) {
			itsCandidates.put(value, new Integer(1));
		} else {
			itsCandidates.put(value, new Integer(existing.intValue() + 1));
		}
	}
	
	/**
	 * Establish which candidate is most likely to represent a missing value.
	 * This is the one with the highest count. If more than one value has the
	 * highest count, we cannot say which is the best candidate.
	 * 
	 * @return The candidate most likely to represent a missing value, or {@code null}
	 * if there is no clear candidate.
	 */
	public String getBestCandidate() {
		String result = null;
		int highestCount = 0;
		
		for (String value : itsCandidates.keySet()) {
			int count = itsCandidates.get(value).intValue();
			
			if (count > highestCount) {
				result = value;
				highestCount = count;
			} else if (count == highestCount) {
				result = null;
			}
		}
		
		// There must be more than one value with the highest count
		// for it to be considered a missing value
		if (highestCount == 1) {
			result = null;
		}
		
		return result;
	}
}
