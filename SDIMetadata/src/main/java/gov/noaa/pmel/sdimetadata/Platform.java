package gov.noaa.pmel.sdimetadata;

public class Platform implements Cloneable {

    public boolean isValid() {
        return true;
    }

    @Override
    public Platform clone() {
        Platform dup;
        try {
            dup = (Platform) super.clone();
        } catch ( CloneNotSupportedException ex ) {
            throw new RuntimeException(ex);
        }
        return dup;
    }

}

