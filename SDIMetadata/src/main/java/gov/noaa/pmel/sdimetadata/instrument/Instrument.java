package gov.noaa.pmel.sdimetadata.instrument;

/**
 * Base class for instruments (eg, equilibrators)
 */
public class Instrument implements Cloneable {

    protected String addnInfo;

    public boolean isValid() {
        return true;
    }

    @Override
    public Instrument clone() {
        Instrument dup;
        try {
            dup = (Instrument) super.clone();
        } catch ( CloneNotSupportedException ex ) {
            throw new RuntimeException(ex);
        }
        dup.addnInfo = addnInfo;
        return dup;
    }

    /**
     * @return additional information about this instrument; never null but may be empty
     */
    public String getAddnInfo() {
        return addnInfo;
    }

    /**
     * @param addnInfo
     *         assign as additional information about this instrument;
     *         if null, an emptry string is assigned.
     */
    public void setAddnInfo(String addnInfo) {
        this.addnInfo = (addnInfo != null) ? addnInfo.trim() : "";
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( !(obj instanceof Instrument) )
            return false;

        Instrument that = (Instrument) obj;

        return addnInfo.equals(that.addnInfo);
    }

    @Override
    public int hashCode() {
        return addnInfo.hashCode();
    }

    @Override
    public String toString() {
        return "Instrument{" +
                "addnInfo='" + addnInfo + '\'' +
                '}';
    }

}

