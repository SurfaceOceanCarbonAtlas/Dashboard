/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import java.util.TreeSet;

/**
 * Extension of a TreeSet of Integers whose toString() method prints the 
 * contents in a compact form.  A sequence of consecutive integers are 
 * printed using the first value, a hyphen, and the last value, such as: 
 * "5-35, 67, 88-99"
 * 
 * @author Karl Smith
 */
public class RowNumSet extends TreeSet<Integer> {

	private static final long serialVersionUID = 8977687481510947560L;

	/**
	 * Creates an empty set.
	 */
	public RowNumSet() {
		super();
	}

	/**
	 * Prints the contents of this set of integers in a compact form.
	 * A sequence of consecutive integers are printed using the first 
	 * value, a hyphen, and the last value, such as: 
	 * "5-35,67,68,88-99"
	 */
	@Override
	public String toString() {
		if ( this.size() < 1 )
			return "";
		StringBuilder sb = new StringBuilder();
		Integer start = null;
		Integer end = null;
		boolean first = true;
		for ( Integer val : this ) {
			if ( start == null ) {
				// start of a new sequence
				start = val;
				end = val;
			}
			else if ( val.equals(end + 1) ) {
				// continue the current sequence
				end = val;
			}
			else {
				// gap - print the stored sequence
				if ( first ) {
					first = false;
				}
				else {
					sb.append(",");
				}
				if ( start.equals(end) ) {
					// singleton
					sb.append(start.toString());
				}
				else if ( start.equals(end - 1) ) {
					// pair
					sb.append(start.toString());
					sb.append(",");
					sb.append(end.toString());
				}
				else {
					// range 
					sb.append(start.toString());
					sb.append("-");
					sb.append(end.toString());
				}
				// start a new sequence
				start = val;
				end = val;
			}
		}
		// print the final stored sequence
		if ( ! first ) {
			sb.append(",");
		}
		if ( start.equals(end) ) {
			// singleton
			sb.append(start.toString());
		}
		else if ( start.equals(end - 1) ) {
			// pair
			sb.append(start.toString());
			sb.append(",");
			sb.append(end.toString());
		}
		else {
			// range 
			sb.append(start.toString());
			sb.append("-");
			sb.append(end.toString());
		}

		return sb.toString();
	}
}

