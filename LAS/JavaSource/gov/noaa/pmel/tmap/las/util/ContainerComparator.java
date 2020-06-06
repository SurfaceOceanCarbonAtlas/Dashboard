/**
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development. 
 */
package gov.noaa.pmel.tmap.las.util;

import java.text.Collator;
import java.util.Comparator;

/**
 * @author Roland Schweitzer
 *
 */
public class ContainerComparator implements Comparator {
    
    String first;
    String second;
    
    public ContainerComparator(String first, String second) {
        this.first = first;
        this.second = second;
    }
    
    public ContainerComparator(String first) {
        this.first = first;
        this.second = null;
    }
    /**
     * 
     */
    public ContainerComparator() {
        this.first = "name";
        this.second = null;
    }

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object arg0, Object arg1) {
        Container one = (Container) arg0;
        Container two = (Container) arg1;
        
        String object1_string = one.getAttributeValue(this.first);
        String object2_string = two.getAttributeValue(this.first);
        
        if (this.second != null) {
            object1_string = object1_string + " " + one.getAttributeValue(this.second);
            object2_string = object2_string + " " + two.getAttributeValue(this.second);
        }
        
        Collator myCollator = Collator.getInstance();
         
        return myCollator.compare(object1_string, object2_string);
        
    }

}
