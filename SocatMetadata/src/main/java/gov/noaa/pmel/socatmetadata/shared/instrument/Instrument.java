package gov.noaa.pmel.socatmetadata.shared.instrument;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.socatmetadata.shared.core.Duplicable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Base class for instruments (eg, sensors, equilibrators)
 */
public class Instrument implements Duplicable, Serializable, IsSerializable {

    private static final long serialVersionUID = 5194493772199222454L;

    protected String name;
    protected String id;
    protected String manufacturer;
    protected String model;
    protected ArrayList<String> addnInfo;

    /**
     * Create with all fields empty
     */
    public Instrument() {
        name = "";
        id = "";
        manufacturer = "";
        model = "";
        addnInfo = new ArrayList<String>();
    }

    /**
     * @return set of field names that are currently invalid
     */
    public HashSet<String> invalidFieldNames() {
        HashSet<String> invalid = new HashSet<String>();
        if ( name.isEmpty() )
            invalid.add("name");
        return invalid;
    }

    /**
     * @return name for this instrument; never null but may be an empty string.  This name is used to identify
     *         this instrument in other classes and should be unique among instument names in this dataset.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *         assign as the name for this instrument; if null, an empty string is assigned.  This name is used to
     *         identify this instrument in other classes and should be unique among instument names in this dataset.
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
     * @return the list of additional information about this instrument; never null but may be empty.
     *         Any information strings given are guaranteed to have some content (not null, not blank).
     */
    public ArrayList<String> getAddnInfo() {
        return new ArrayList<String>(addnInfo);
    }

    /**
     * Calls {@link #setAddnInfo(Iterable)}; added to satisfy JavaBean requirements.
     *
     * @param addnInfo
     *         assign as the list of additional information about this instrument;
     *         if null, an empty list is assigned
     *
     * @throws IllegalArgumentException
     *         if any of the information strings are null or empty
     */
    public void setAddnInfo(ArrayList<String> addnInfo) throws IllegalArgumentException {
        setAddnInfo((Iterable<String>) addnInfo);
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
     * @return the name of the instrument if given (not empty);
     *         otherwise the ID of the instrument if given (not empty);
     *         otherwise "unknown"
     */
    public String getReferenceName() {
        if ( !name.isEmpty() )
            return name;
        if ( !id.isEmpty() )
            return id;
        return "unknown";
    }

    @Override
    public Object duplicate(Object dup) {
        Instrument inst;
        if ( dup == null )
            inst = new Instrument();
        else
            inst = (Instrument) dup;
        inst.name = name;
        inst.id = id;
        inst.manufacturer = manufacturer;
        inst.model = model;
        inst.addnInfo = new ArrayList<String>(addnInfo);
        return inst;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof Instrument) )
            return false;

        Instrument other = (Instrument) obj;

        if ( !name.equals(other.name) )
            return false;
        if ( !id.equals(other.id) )
            return false;
        if ( !manufacturer.equals(other.manufacturer) )
            return false;
        if ( !model.equals(other.model) )
            return false;
        if ( !addnInfo.equals(other.addnInfo) )
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
        result = result * prime + addnInfo.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return getSimpleName() +
                "{ name='" + name + "'" +
                ", id='" + id + "'" +
                ", manufacturer='" + manufacturer + "'" +
                ", model='" + model + "'" +
                ", addnInfo=" + addnInfo +
                " }";
    }

    /**
     * @return the simple name for this type of instrument; used to identifying the instrument type in
     *         client code, and used in {@link #toString()}.  Essentially this.getClass().getSimpleName()
     *         but explicit defined for GWT client compatibility.
     */

    public String getSimpleName() {
        return "Instrument";
    }

}
