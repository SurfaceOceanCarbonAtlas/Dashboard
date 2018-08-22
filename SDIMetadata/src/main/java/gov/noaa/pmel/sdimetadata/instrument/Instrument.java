package gov.noaa.pmel.sdimetadata.instrument;

import java.util.ArrayList;

/**
 * Base class for instruments (equilibrators,
 */
public class Instrument implements Cloneable {

    protected ArrayList<String> sensorNames;
    protected String addnInfo;

    public boolean isValid() {
        if ( sensorNames.isEmpty() )
            return false;
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
        dup.sensorNames = new ArrayList<String>(sensorNames);
        dup.addnInfo = addnInfo;
        return dup;
    }

}

