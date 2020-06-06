package gov.noaa.pmel.tmap.las.ui.state;



import gov.noaa.pmel.tmap.las.util.NameValuePair;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Roland Schweitzer
 *
 */
public class StateNameValueList extends ArrayList<NameValuePair> {
    
    private String current;
    private String previous;
    
    public StateNameValueList() {
        super();
    }
    
    public StateNameValueList (ArrayList<NameValuePair> list) {
        super(list);
        if ( list != null && list.size() > 0 ) {
           current = list.get(0).getValue();
           previous = list.get(0).getValue();
        } else {
            current = null;
            previous = null;
        }
        
    }
    
    /**
     * @return Returns the current.
     */
    public String getCurrent() {
        return current;
    }
    /**
     * @param current The current to set.
     */
    public void setCurrent(String current) {
        previous = this.current;
        this.current = current;
    }
    /**
     * Set the previous to the current.
     */
    public void setPrevious(String previous) {
        this.previous = previous;
    }
    
    /**
     * @return the previous value
     */
    public String getPrevious() {
        return previous;
    }
    
    /**
     * Has this state changed?
     */
    public boolean hasChanged() {
        if ( previous == null || current == null ) {
            return true;
        }
        if ( previous.equals(current) ) {
            return false;
        }
        return true;
    }
    
    public NameValuePair getCurrentAsBean() {
        return getByValue(current);
    }
    
    public NameValuePair getByValue(String value) {
        for (Iterator it = this.iterator(); it.hasNext();) {
            NameValuePair nvb = (NameValuePair) it.next();
            if ( nvb.getValue().equals(value) ) {
                return nvb;
            }
        }
        return null;
    }
    
    public NameValuePair getByName(String name) {
        for (Iterator it = this.iterator(); it.hasNext();) {
            NameValuePair nvb = (NameValuePair) it.next();
            if ( nvb.getName().equals(name) ) {
                return nvb;
            }
        }
        return null;
    }

}
