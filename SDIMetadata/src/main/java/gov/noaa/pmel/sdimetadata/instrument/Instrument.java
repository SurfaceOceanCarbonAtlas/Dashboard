package gov.noaa.pmel.sdimetadata.instrument;

import java.util.ArrayList;

/**
 * Base class for instruments (eg, sensors, equilibrators)
 */
public class Instrument implements Cloneable {

    protected String name;
    protected String id;
    protected String manufacturer;
    protected String model;
    protected String location;
    protected ArrayList<String> addnInfo;

    /**
     * Create with all fields empty
     */
    public Instrument() {
        name = "";
        id = "";
        manufacturer = "";
        model = "";
        location = "";
        addnInfo = new ArrayList<String>();
    }

    /**
     * @return name for this instrument; never null but may be an empty string.
     *         This name should be unique for instruments in this dataset.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *         assign as the name for this instrument; if null, an empty string is assigned
     *         This name should be unique for instrument in this dataset.
     */
    public void setName(String name) {
        this.name = (name != null) ? name.trim() : "";
    }

    /**
     * @return unique ID (such as a serial number) of this instrument; never null but may be an empty string
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *         assign as the unique ID (such as a serial number) of this instrument;
     *         if null, an empty string is assigned
     */
    public void setId(String id) {
        this.id = (id != null) ? id.trim() : "";
    }

    /**
     * @return manufacturer of the instrument; never null but may be an empty string
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * @param manufacturer
     *         assign as the manufacturer of the instrument; if null, an empty string is assigned
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = (manufacturer != null) ? manufacturer.trim() : "";
    }

    /**
     * @return model of the instrument; never null but may be an empty string
     */
    public String getModel() {
        return model;
    }

    /**
     * @param model
     *         assign as the model of the instrument; if null, an empty string is assigned
     */
    public void setModel(String model) {
        this.model = (model != null) ? model.trim() : "";
    }

    /**
     * @return description of the location of the instrument; never null but may be an empty string
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location
     *         assign as the description of the location of the instrument; if null, an empty string is assigned
     */
    public void setLocation(String location) {
        this.location = (location != null) ? location.trim() : "";
    }

    /**
     * @return the list of additional information about this instrument; never null but may be empty.
     *         Any information strings given are guaranteed to have some content (not null, not blank).
     */
    public ArrayList<String> getAddnInfo() {
        return new ArrayList<String>(addnInfo);
    }

    /**
     * @param addnInfo
     *         assign as the list of additional information about this instrument;
     *         if null, an empty list is assigned
     *
     * @throws IllegalArgumentException
     *         if any of the information strings are null or empty
     */
    public void setAddnInfo(Iterable<String> addnInfo) throws IllegalArgumentException {
        this.addnInfo.clear();
        if ( addnInfo != null ) {
            for (String info : addnInfo) {
                if ( info == null )
                    throw new IllegalArgumentException("null information string given");
                info = info.trim();
                if ( info.isEmpty() )
                    throw new IllegalArgumentException("blank information string given");
                this.addnInfo.add(info);
            }
        }
    }

    /**
     * @return is all required fields are appropriately assigned
     */
    public boolean isValid() {
        if ( name.isEmpty() )
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
        dup.name = name;
        dup.id = id;
        dup.manufacturer = manufacturer;
        dup.model = model;
        dup.location = location;
        dup.addnInfo = new ArrayList<String>(addnInfo);
        return dup;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( !(obj instanceof Instrument) )
            return false;

        Instrument that = (Instrument) obj;

        if ( !name.equals(that.name) )
            return false;
        if ( !id.equals(that.id) )
            return false;
        if ( !manufacturer.equals(that.manufacturer) )
            return false;
        if ( !model.equals(that.model) )
            return false;
        if ( !location.equals(that.location) )
            return false;
        if ( !addnInfo.equals(that.addnInfo) )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = name.hashCode();
        result = result * prime + id.hashCode();
        result = result * prime + manufacturer.hashCode();
        result = result * prime + model.hashCode();
        result = result * prime + location.hashCode();
        result = result * prime + addnInfo.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Instrument{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", location='" + location + '\'' +
                ", addnInfo=" + addnInfo +
                '}';
    }

}

