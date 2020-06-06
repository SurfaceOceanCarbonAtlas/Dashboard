package gov.noaa.pmel.tmap.las.service.tabledap;

import java.util.Comparator;

public class DataRowComparator implements Comparator<DataRow> {

    @Override
    public int compare(DataRow o1, DataRow o2) {
        if ( o1.getId().equals(o2.getId()) ) {
            if ( o1.getTime().doubleValue() < o2.getTime().doubleValue() ) {
                return -1;
            } else if ( o1.getTime().doubleValue() > o2.getTime().doubleValue() ) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return o1.getId().compareTo(o2.getId());
        }
    }

}
