/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.*;
import gov.noaa.pmel.socat.dashboard.server.RowNumSet;

import org.junit.Test;

/**
 * Unit tests for RowNumSet
 * 
 * @author Karl Smith
 */
public class RowNumSetTest {

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.server.RowNumSet#toString()}.
	 */
	@Test
	public void testToString() {
		RowNumSet rowSet = new RowNumSet();
		assertEquals("", rowSet.toString());
		rowSet.add(5);
		assertEquals("5", rowSet.toString());
		rowSet.add(6);
		assertEquals("5,6", rowSet.toString());
		rowSet.add(7);
		assertEquals("5-7", rowSet.toString());
		rowSet.add(8);
		rowSet.add(9);
		assertEquals("5-9", rowSet.toString());
		rowSet.add(12);
		assertEquals("5-9,12", rowSet.toString());
		for (int k = 55; k < 67; k++)
			rowSet.add(k);
		assertEquals("5-9,12,55-66", rowSet.toString());
		rowSet.add(2);
		assertEquals("2,5-9,12,55-66", rowSet.toString());
		rowSet.add(98);
		assertEquals("2,5-9,12,55-66,98", rowSet.toString());
		rowSet.add(60);
		assertEquals("2,5-9,12,55-66,98", rowSet.toString());
		rowSet.add(3);
		assertEquals("2,3,5-9,12,55-66,98", rowSet.toString());
	}

}

