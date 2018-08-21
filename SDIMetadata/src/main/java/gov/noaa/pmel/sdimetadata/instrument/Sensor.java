package gov.noaa.pmel.sdimetadata.instrument;

import java.util.ArrayList;

public class Sensor implements Cloneable {
    protected String location;
    protected String manufacturer;
    protected String model;
    protected String calibration;
    protected ArrayList<String> comments;

    /**
     * Create with all fields set to empty Strings or an empty List
     */
    public Sensor() {
        location = "";
        manufacturer = "";
        model = "";
        calibration = "";
        comments = new ArrayList<String>();
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
        this.location = (location != null) ? location : "";
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
        this.manufacturer = (manufacturer != null) ? manufacturer : "";
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
        this.model = (model != null) ? model : "";
    }

    /**
     * @return calibration information for the sensor; never null but may be an empty string
     */
    public String getCalibration() {
        return calibration;
    }

    /**
     * @param calibration
     *         assign as the calibration information for the sensor; if null, an empty string is assigned
     */
    public void setCalibration(String calibration) {
        this.calibration = (calibration != null) ? calibration : "";
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
     * @return whether all the required fields are assigned with valid values.  Currently this means
     *         location, manufacturer, and model are not blank.
     */
    public boolean isValid() {
        if ( location.isEmpty() || manufacturer.isEmpty() || model.isEmpty() )
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
        sensor.location = location;
        sensor.manufacturer = manufacturer;
        sensor.model = model;
        sensor.calibration = calibration;
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

        if ( !location.equals(sensor.location) )
            return false;
        if ( !manufacturer.equals(sensor.manufacturer) )
            return false;
        if ( !model.equals(sensor.model) )
            return false;
        if ( !calibration.equals(sensor.calibration) )
            return false;
        if ( !comments.equals(sensor.comments) )
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = location.hashCode();
        result = result * prime + manufacturer.hashCode();
        result = result * prime + model.hashCode();
        result = result * prime + calibration.hashCode();
        result = result * prime + comments.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "location='" + location + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", calibration='" + calibration + '\'' +
                ", comments=" + comments +
                '}';
    }

}

