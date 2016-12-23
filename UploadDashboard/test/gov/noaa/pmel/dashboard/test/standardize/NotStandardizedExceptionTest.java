/**
 * 
 */
package gov.noaa.pmel.dashboard.test.standardize;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import gov.noaa.pmel.dashboard.server.DashDataType;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.standardize.NotStandardizedException;

/**
 * Unit tests for method in {@link gov.noaa.pmel.dashboard.standardize.NotStandardizedException}
 * 
 * @author Karl Smith
 */
public class NotStandardizedExceptionTest {

	private static final String MSG = "Some error message";
	private static final List<DashDataType> REQD_TYPES = 
			Arrays.asList(DashboardServerUtils.LONGITUDE, DashboardServerUtils.LATITUDE);
	private static final List<String> REQD_VARNAMES =
			Arrays.asList(DashboardServerUtils.LONGITUDE.getVarName(), DashboardServerUtils.LATITUDE.getVarName());

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.standardize.NotStandardizedException#getRequiredTypes()}.
	 */
	@Test
	public void testGetRequiredTypes() {
		NotStandardizedException ex = new NotStandardizedException(null);
		assertNull( ex.getRequiredTypes() );
		ex = new NotStandardizedException(MSG);
		assertNull( ex.getRequiredTypes() );
		ex = new NotStandardizedException(null, null);
		assertNull( ex.getRequiredTypes() );
		ex = new NotStandardizedException(MSG, null);
		assertNull( ex.getRequiredTypes() );
		ex = new NotStandardizedException(null, REQD_TYPES);
		assertEquals(new HashSet<DashDataType>(REQD_TYPES), ex.getRequiredTypes());
		ex = new NotStandardizedException(MSG, REQD_TYPES);
		assertEquals(new HashSet<DashDataType>(REQD_TYPES), ex.getRequiredTypes());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.standardize.NotStandardizedException#toString()}.
	 */
	@Test
	public void testToString() {
		NotStandardizedException ex = new NotStandardizedException(null);
		assertNull( ex.getMessage() );
		assertEquals("NotStandardizedException: null" +
				"\n    requiredTypes=[]", ex.toString());
		ex = new NotStandardizedException(MSG);
		assertEquals(MSG, ex.getMessage());
		assertEquals("NotStandardizedException: " + MSG + 
				"\n    requiredTypes=[]", ex.toString());
		ex = new NotStandardizedException(null, null);
		assertNull( ex.getMessage() );
		assertEquals("NotStandardizedException: null" + 
				"\n    requiredTypes=[]", ex.toString());
		ex = new NotStandardizedException(MSG, null);
		assertEquals(MSG, ex.getMessage());
		assertEquals("NotStandardizedException: " + MSG + 
				"\n    requiredTypes=[]", ex.toString());
		ex = new NotStandardizedException(null, REQD_TYPES);
		assertNull( ex.getMessage() );
		assertEquals("NotStandardizedException: null" + 
				"\n    requiredTypes=" + REQD_VARNAMES.toString(), ex.toString());
		ex = new NotStandardizedException(MSG, REQD_TYPES);
		assertEquals(MSG, ex.getMessage());
		assertEquals("NotStandardizedException: " + MSG + 
				"\n    requiredTypes=" + REQD_VARNAMES.toString(), ex.toString());
	}

}
