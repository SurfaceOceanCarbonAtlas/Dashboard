package gov.noaa.pmel.dashboard.test.shared;

import gov.noaa.pmel.dashboard.shared.DashboardDatasetData;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.DatasetQCStatus;
import gov.noaa.pmel.dashboard.shared.TypesDatasetDataPair;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Units tests for {@link TypesDatasetDataPair}
 *
 * @author Karl Smith
 */
public class TypesDatasetDataPairTest {

    /**
     * Test method for {@link TypesDatasetDataPair#getAllKnownTypes()}.
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
        assertNull(cruiseTypes.getAllKnownTypes());
        cruiseTypes.setAllKnownTypes(knownTypes);
        assertEquals(knownTypes, cruiseTypes.getAllKnownTypes());
        cruiseTypes.setAllKnownTypes(null);
        assertNull(cruiseTypes.getAllKnownTypes());
    }

    /**
     * Test method for {@link TypesDatasetDataPair#getDatasetData()}
     * and {@link TypesDatasetDataPair#setDatasetData(DashboardDatasetData)}.
     */
    @Test
    public void testGetSetDatasetData() {
        DashboardDatasetData cruiseData = new DashboardDatasetData();
        cruiseData.setDatasetId("ABCD20161003");
        cruiseData.setSubmitStatus(new DatasetQCStatus(DatasetQCStatus.Status.SUSPENDED, ""));

        TypesDatasetDataPair cruiseTypes = new TypesDatasetDataPair();
        assertNull(cruiseTypes.getDatasetData());
        cruiseTypes.setDatasetData(cruiseData);
        assertEquals(cruiseData, cruiseTypes.getDatasetData());
        assertNull(cruiseTypes.getAllKnownTypes());
        cruiseTypes.setDatasetData(null);
        assertNull(cruiseTypes.getDatasetData());
    }

    /**
     * Test method for {@link TypesDatasetDataPair#hashCode()} and {@link TypesDatasetDataPair#equals(Object)}.
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
        cruiseData.setSubmitStatus(new DatasetQCStatus(DatasetQCStatus.Status.SUSPENDED, ""));

        TypesDatasetDataPair cruiseTypes = new TypesDatasetDataPair();
        assertFalse(cruiseTypes.equals(null));
        assertFalse(cruiseTypes.equals(knownTypes));

        TypesDatasetDataPair otherTypes = new TypesDatasetDataPair();
        assertEquals(cruiseTypes.hashCode(), otherTypes.hashCode());
        assertTrue(cruiseTypes.equals(otherTypes));

        cruiseTypes.setAllKnownTypes(knownTypes);
        assertNotEquals(cruiseTypes.hashCode(), otherTypes.hashCode());
        assertFalse(cruiseTypes.equals(otherTypes));
        otherTypes.setAllKnownTypes(knownTypes);
        assertEquals(cruiseTypes.hashCode(), otherTypes.hashCode());
        assertTrue(cruiseTypes.equals(otherTypes));

        cruiseTypes.setDatasetData(cruiseData);
        assertNotEquals(cruiseTypes.hashCode(), otherTypes.hashCode());
        assertFalse(cruiseTypes.equals(otherTypes));
        otherTypes.setDatasetData(cruiseData);
        assertEquals(cruiseTypes.hashCode(), otherTypes.hashCode());
        assertTrue(cruiseTypes.equals(otherTypes));
    }

}
