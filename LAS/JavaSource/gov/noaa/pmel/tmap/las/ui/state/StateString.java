package gov.noaa.pmel.tmap.las.ui.state;


public class StateString {
     
     String current;
     String previous;
     
     public StateString (String current) {
         setCurrent(current);
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
     * @return Returns the previous.
     */
    public String getPrevious() {
        return previous;
    }
    /**
     * @param previous The previous to set.
     */
    public void setPrevious(String previous) {
        this.previous = previous;
    }
    /**
     * Determine if the value of this state variable has changed
     */
    public boolean hasChanged() {
        if ( current.equals(previous)) {
            return false;
        } else  {
            return true;
        }
    }
     
}
