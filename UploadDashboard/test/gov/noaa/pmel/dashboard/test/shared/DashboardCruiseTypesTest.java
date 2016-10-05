/**
 * 
 */
package gov.noaa.pmel.dashboard.test.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.dashboard.shared.DashboardCruiseTypes;
import gov.noaa.pmel.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

import java.util.ArrayList;

import org.junit.Test;

/**
 * Units tests for {@link gov.noaa.pmel.dashboard.shared.DashboardCruiseTypes}
 * 
 * @author Karl Smith
 */
public class DashboardCruiseTypesTest {

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruiseTypes#getAllKnownTypes()}.
	 */
	@Test
	public void testGetSetAllKnownTypes() {
		ArrayList<DataColumnType> knownTypes = new ArrayList<DataColumnType>(5);
		knownTypes.add(DashboardUtils.EXPOCODE);
		knownTypes.add(DashboardUtils.LONGITUDE);
		knownTypes.add(DashboardUtils.LATITUDE);
		knownTypes.add(DashboardUtils.TIMESTAMP);
		knownTypes.add(DashboardUtils.QC_FLAG);

		DashboardCruiseTypes cruiseTypes = new DashboardCruiseTypes();
		assertNull( cruiseTypes.getAllKnownTypes() );
		cruiseTypes.setAllKnownTypes(knownTypes);
		assertEquals(knownTypes, cruiseTypes.getAllKnownTypes());
		cruiseTypes.setAllKnownTypes(null);
		assertNull( cruiseTypes.getAllKnownTypes() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruiseTypes#getCruiseData()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardCruiseTypes#setCruiseData(gov.noaa.pmel.dashboard.shared.DashboardCruiseWithData)}.
	 */
	@Test
	public void testGetSetCruiseData() {
		DashboardCruiseWithData cruiseData = new DashboardCruiseWithData();
		cruiseData.setExpocode("ABCD20161003");
		cruiseData.setQcStatus(DashboardUtils.QC_STATUS_SUSPENDED);

		DashboardCruiseTypes cruiseTypes = new DashboardCruiseTypes();
		assertNull( cruiseTypes.getCruiseData() );
		cruiseTypes.setCruiseData(cruiseData);
		assertEquals(cruiseData, cruiseTypes.getCruiseData());
		assertNull( cruiseTypes.getAllKnownTypes() );
		cruiseTypes.setCruiseData(null);
		assertNull( cruiseTypes.getCruiseData() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruiseTypes#hashCode()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardCruiseTypes#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEquals() {
		ArrayList<DataColumnType> knownTypes = new ArrayList<DataColumnType>(5);
		knownTypes.add(DashboardUtils.EXPOCODE);
		knownTypes.add(DashboardUtils.LONGITUDE);
		knownTypes.add(DashboardUtils.LATITUDE);
		knownTypes.add(DashboardUtils.TIMESTAMP);
		knownTypes.add(DashboardUtils.QC_FLAG);

		DashboardCruiseWithData cruiseData = new DashboardCruiseWithData();
		cruiseData.setExpocode("ABCD20161003");
		cruiseData.setQcStatus(DashboardUtils.QC_STATUS_SUSPENDED);

		DashboardCruiseTypes cruiseTypes = new DashboardCruiseTypes();
		assertFalse( cruiseTypes.equals(null) );
		assertFalse( cruiseTypes.equals(knownTypes) );

		DashboardCruiseTypes otherTypes = new DashboardCruiseTypes();
		assertTrue( cruiseTypes.hashCode() == otherTypes.hashCode() );
		assertTrue( cruiseTypes.equals(otherTypes) );

		cruiseTypes.setAllKnownTypes(knownTypes);
		assertFalse( cruiseTypes.hashCode() == otherTypes.hashCode() );
		assertFalse( cruiseTypes.equals(otherTypes) );
		otherTypes.setAllKnownTypes(knownTypes);
		assertTrue( cruiseTypes.hashCode() == otherTypes.hashCode() );
		assertTrue( cruiseTypes.equals(otherTypes) );

		cruiseTypes.setCruiseData(cruiseData);
		assertFalse( cruiseTypes.hashCode() == otherTypes.hashCode() );
		assertFalse( cruiseTypes.equals(otherTypes) );
		otherTypes.setCruiseData(cruiseData);
		assertTrue( cruiseTypes.hashCode() == otherTypes.hashCode() );
		assertTrue( cruiseTypes.equals(otherTypes) );
	}

}
