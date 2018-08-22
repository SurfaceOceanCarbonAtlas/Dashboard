package gov.noaa.pmel.sdimetadata;

import java.util.ArrayList;

/**
 * Information about a generic sensor.
 * The super class for specific sensors.
 */
public class Sensor implements Cloneable {
    protected String sensorName;
    protected String sensorId;
    protected String manufacturer;
    protected String model;
    protected String location;
    protected ArrayList<String> comments;

    /**
     * Create with all fields set to empty Strings or an empty List
     */
    public Sensor() {
        sensorName = "";
        sensorId = "";
        manufacturer = "";
        model = "";
        location = "";
        comments = new ArrayList<String>();
    }

    /**
     * @return name for this sensor; never null but may be an empty string.
     *         This name should be unique for sensors in this dataset.
     */
    public String getSensorName() {
        return sensorName;
    }

    /**
     * @param sensorName
     *         assign as the name for this sensor; if null, an empty string is assigned
     *         This name should be unique for sensors in this dataset.
     */
    public void setSensorName(String sensorName) {
        this.sensorName = (sensorName != null) ? sensorName.trim() : "";
    }

    /**
     * @return unique ID (such as a serial number) of this sensor; never null but may be an empty string
     */
    public String getSensorId() {
        return sensorId;
    }

    /**
     * @param sensorId
     *         assign as the unique ID (such as a serial number) of this sensor; if null, an empty string is assigned
     */
    public void setSensorId(String sensorId) {
        this.sensorId = (sensorId != null) ? sensorId.trim() : "";
    }

    /**
     * @return manufacturer of the sensor; never null but may be an empty string
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * @param manufacturer
     *         assign as the manufacturer of the sensor; if null, an empty string is assigned
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = (manufacturer != null) ? manufacturer.trim() : "";
    }

    /**
     * @return model of the sensor; never null but may be an empty string
     */
    public String getModel() {
        return model;
    }

    /**
     * @param model
     *         assign as the model of the sensor; if null, an empty string is assigned
     */
    public void setModel(String model) {
        this.model = (model != null) ? model.trim() : "";
    }

    /**
     * @return description of the location of the sensor; never null but may be an empty string
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location
     *         assign as the description of the location of the sensor; if null, an empty string is assigned
     */
    public void setLocation(String location) {
        this.location = (location != null) ? location.trim() : "";
    }

    /**
     * @return the list of comments about this sensor; never null but may be empty.
     *         Any strings given are guaranteed to have some content (not null, not blank).
     */
    public ArrayList<String> getComments() {
        return new ArrayList<String>(comments);
    }

    /**
     * @param comments
     *         assign as the list of comment strings about this sensor;
     *         if null, an empty list is assigned
     *
     * @throws IllegalArgumentException
     *         if any of the comment strings are null or empty
     */
    public void setComments(Iterable<String> comments) throws IllegalArgumentException {
        this.comments.clear();
        if ( comments != null ) {
            for (String info : comments) {
                if ( info == null )
                    throw new IllegalArgumentException("null comment string given");
                info = info.trim();
                if ( info.isEmpty() )
                    throw new IllegalArgumentException("bland comment string given");
                this.comments.add(info);
            }
        }
    }

    /**
     * @return whether all the required fields are assigned with valid values.
     *         Currently this means name, manufacturer, and model are not blank.
     */
    public boolean isValid() {
        if ( sensorName.isEmpty() || manufacturer.isEmpty() || model.isEmpty() )
            return false;
        return true;
    }

    @Override
    public Sensor clone() {
        Sensor sensor;
        try {
            sensor = (Sensor) super.clone();
        } catch ( CloneNotSupportedException ex ) {
            throw new RuntimeException(ex);
        }
        sensor.sensorName = sensorName;
        sensor.sensorId = sensorId;
        sensor.manufacturer = manufacturer;
        sensor.model = model;
        sensor.location = location;
        sensor.comments = new ArrayList<String>(comments);
        return sensor;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( !(obj instanceof Sensor) )
            return false;

        Sensor sensor = (Sensor) obj;

        if ( !sensorName.equals(sensor.sensorName) )
            return false;
        if ( !sensorId.equals(sensor.sensorId) )
            return false;
        if ( !manufacturer.equals(sensor.manufacturer) )
            return false;
        if ( !model.equals(sensor.model) )
            return false;
        if ( !location.equals(sensor.location) )
            return false;
        if ( !comments.equals(sensor.comments) )
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = sensorName.hashCode();
        result = result * prime + sensorId.hashCode();
        result = result * prime + manufacturer.hashCode();
        result = result * prime + model.hashCode();
        result = result * prime + location.hashCode();
        result = result * prime + comments.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "sensorName='" + sensorName + '\'' +
                ", sensorId='" + sensorId + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", location='" + location + '\'' +
                ", comments=" + comments +
                '}';
    }

}

