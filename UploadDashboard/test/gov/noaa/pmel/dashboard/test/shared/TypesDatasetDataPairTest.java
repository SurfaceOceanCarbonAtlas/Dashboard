/**
 * 
 */
package gov.noaa.pmel.dashboard.test.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import gov.noaa.pmel.dashboard.shared.DashboardDatasetData;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.TypesDatasetDataPair;

/**
 * Units tests for {@link gov.noaa.pmel.dashboard.shared.TypesDatasetDataPair}
 * 
 * @author Karl Smith
 */
public class TypesDatasetDataPairTest {

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.TypesDatasetDataPair#getAllKnownTypes()}.
	 */
	@Test
	public void testGetSetAllKnownTypes() {
		ArrayList<DataColumnType> knownTypes = new ArrayList<DataColumnType>(5);
		knownTypes.add(DashboardUtils.DATASET_ID);
		knownTypes.add(DashboardUtils.LONGITUDE);
		knownTypes.add(DashboardUtils.LATITUDE);
		knownTypes.add(DashboardUtils.SAMPLE_DEPTH);
		knownTypes.add(DashboardUtils.TIMESTAMP);

		TypesDatasetDataPair cruiseTypes = new TypesDatasetDataPair();
		assertNull( cruiseTypes.getAllKnownTypes() );
		cruiseTypes.setAllKnownTypes(knownTypes);
		assertEquals(knownTypes, cruiseTypes.getAllKnownTypes());
		cruiseTypes.setAllKnownTypes(null);
		assertNull( cruiseTypes.getAllKnownTypes() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.TypesDatasetDataPair#getDatasetData()}
	 * and {@link gov.noaa.pmel.dashboard.shared.TypesDatasetDataPair#setDatasetData(gov.noaa.pmel.dashboard.shared.DashboardDatasetData)}.
	 */
	@Test
	public void testGetSetDatasetData() {
		DashboardDatasetData cruiseData = new DashboardDatasetData();
		cruiseData.setDatasetId("ABCD20161003");
		cruiseData.setSubmitStatus(DashboardUtils.STATUS_SUSPENDED);

		TypesDatasetDataPair cruiseTypes = new TypesDatasetDataPair();
		assertNull( cruiseTypes.getDatasetData() );
		cruiseTypes.setDatasetData(cruiseData);
		assertEquals(cruiseData, cruiseTypes.getDatasetData());
		assertNull( cruiseTypes.getAllKnownTypes() );
		cruiseTypes.setDatasetData(null);
		assertNull( cruiseTypes.getDatasetData() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.TypesDatasetDataPair#hashCode()}
	 * and {@link gov.noaa.pmel.dashboard.shared.TypesDatasetDataPair#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEquals() {
		ArrayList<DataColumnType> knownTypes = new ArrayList<DataColumnType>(5);
		knownTypes.add(DashboardUtils.DATASET_ID);
		knownTypes.add(DashboardUtils.LONGITUDE);
		knownTypes.add(DashboardUtils.LATITUDE);
		knownTypes.add(DashboardUtils.SAMPLE_DEPTH);
		knownTypes.add(DashboardUtils.TIMESTAMP);

		DashboardDatasetData cruiseData = new DashboardDatasetData();
		cruiseData.setDatasetId("ABCD20161003");
		cruiseData.setSubmitStatus(DashboardUtils.STATUS_SUSPENDED);

		TypesDatasetDataPair cruiseTypes = new TypesDatasetDataPair();
		assertFalse( cruiseTypes.equals(null) );
		assertFalse( cruiseTypes.equals(knownTypes) );

		TypesDatasetDataPair otherTypes = new TypesDatasetDataPair();
		assertTrue( cruiseTypes.hashCode() == otherTypes.hashCode() );
		assertTrue( cruiseTypes.equals(otherTypes) );

		cruiseTypes.setAllKnownTypes(knownTypes);
		assertFalse( cruiseTypes.hashCode() == otherTypes.hashCode() );
		assertFalse( cruiseTypes.equals(otherTypes) );
		otherTypes.setAllKnownTypes(knownTypes);
		assertTrue( cruiseTypes.hashCode() == otherTypes.hashCode() );
		assertTrue( cruiseTypes.equals(otherTypes) );

		cruiseTypes.setDatasetData(cruiseData);
		assertFalse( cruiseTypes.hashCode() == otherTypes.hashCode() );
		assertFalse( cruiseTypes.equals(otherTypes) );
		otherTypes.setDatasetData(cruiseData);
		assertTrue( cruiseTypes.hashCode() == otherTypes.hashCode() );
		assertTrue( cruiseTypes.equals(otherTypes) );
	}

}
