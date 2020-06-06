package gov.noaa.pmel.tmap.las.client.serializable;

import java.util.Comparator;

public class VariableNameOrSortOrderComparator implements Comparator<VariableSerializable>{

    @Override
    public int compare(VariableSerializable o1, VariableSerializable o2) {
        String a1 = o1.getAttributes().get("sort_order");
        String a2 = o2.getAttributes().get("sort_order");
        if ( a1 != null && a2 != null ) {
            return a1.compareTo(a2);
        } else {
            return o1.getName().compareTo(o2.getName());
        }
    }

}
