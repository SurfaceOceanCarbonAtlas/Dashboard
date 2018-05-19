/**
 *
 */
package gov.noaa.pmel.dashboard.test.shared;

import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetList;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for methods of {@link DashboardDatasetList}
 *
 * @author Karl Smith
 */
public class DashboardDatasetListTest {

    /**
     * Test method for {@link DashboardDatasetList#getUsername()} and {@link DashboardDatasetList#setUsername(String)}.
     */
    @Test
    public void testGetSetUsername() {
        String myUsername = "User";
        DashboardDatasetList cruiseList = new DashboardDatasetList();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseList.getUsername());
        cruiseList.setUsername(myUsername);
        assertEquals(myUsername, cruiseList.getUsername());
        assertEquals(0, cruiseList.size());
        cruiseList.setUsername(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseList.getUsername());
    }

    /**
     * Test method for {@link DashboardDatasetList#isManager()} and {@link DashboardDatasetList#setManager(boolean)}.
     */
    @Test
    public void testIsSetManager() {
        DashboardDatasetList cruiseList = new DashboardDatasetList();
        assertFalse(cruiseList.isManager());
        cruiseList.setManager(true);
        assertTrue(cruiseList.isManager());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseList.getUsername());
        assertEquals(0, cruiseList.size());
    }

    /**
     * Test method for {@link DashboardDatasetList#hashCode()} and {@link DashboardDatasetList#equals(Object)}.
     */
    @Test
    public void testHashCodeEquals() {
        String myUsername = "User";
        String myVersion = "4";
        String myExpocode = "ABCD20050728";
        DashboardDataset cruise = new DashboardDataset();
        cruise.setOwner(myUsername);
        cruise.setDatasetId(myExpocode);
        DashboardDataset sameDataset = new DashboardDataset();
        sameDataset.setOwner(myUsername);
        sameDataset.setDatasetId(myExpocode);
        DashboardDataset otherDataset = new DashboardDataset();
        otherDataset.setOwner(myUsername);
        otherDataset.setDatasetId("XXXX20030918");

        DashboardDatasetList firstList = new DashboardDatasetList();
        assertFalse(firstList.equals(null));
        assertFalse(firstList.equals(cruise));
        DashboardDatasetList secondList = new DashboardDatasetList();
        assertEquals(firstList.hashCode(), secondList.hashCode());
        assertTrue(firstList.equals(secondList));

        firstList.setUsername(myUsername);
        assertNotEquals(firstList.hashCode(), secondList.hashCode());
        assertFalse(firstList.equals(secondList));
        secondList.setUsername(myUsername);
        assertEquals(firstList.hashCode(), secondList.hashCode());
        assertTrue(firstList.equals(secondList));

        firstList.setManager(true);
        assertNotEquals(firstList.hashCode(), secondList.hashCode());
        assertFalse(firstList.equals(secondList));
        secondList.setManager(true);
        assertEquals(firstList.hashCode(), secondList.hashCode());
        assertTrue(firstList.equals(secondList));

        firstList.put(cruise.getDatasetId(), cruise);
        assertNotEquals(firstList.hashCode(), secondList.hashCode());
        assertFalse(firstList.equals(secondList));
        secondList.put(sameDataset.getDatasetId(), sameDataset);
        assertEquals(firstList.hashCode(), secondList.hashCode());
        assertTrue(firstList.equals(secondList));

        firstList.put(otherDataset.getDatasetId(), otherDataset);
        secondList.clear();
        secondList.put(otherDataset.getDatasetId(), otherDataset);
        secondList.put(sameDataset.getDatasetId(), sameDataset);
        assertEquals(firstList.hashCode(), secondList.hashCode());
        assertTrue(firstList.equals(secondList));
    }

}
