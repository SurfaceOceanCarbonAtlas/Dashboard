package gov.noaa.pmel.socatmetadata.shared.variable;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.socatmetadata.shared.core.Duplicable;
import gov.noaa.pmel.socatmetadata.shared.core.NumericString;

import java.io.Serializable;

/**
 * Generic numeric variable information
 */
public class GenDataVar extends Variable implements Duplicable, Serializable, IsSerializable {

    private static final long serialVersionUID = -3583581974560708342L;

    private NumericString accuracy;
    private NumericString precision;
    private String flagColName;

    /**
     * Create with all fields empty.
     */
    public GenDataVar() {
        super();
        accuracy = new NumericString();
        precision = new NumericString();
        flagColName = "";
    }

    /**
     * Create using as many of the values in the given variable subclass as possible.
     */
    public GenDataVar(Variable var) {
        super(var);
        if ( var instanceof GenDataVar ) {
            GenDataVar other = (GenDataVar) var;
            accuracy = new NumericString(other.accuracy);
            precision = new NumericString(other.precision);
            flagColName = other.flagColName;
        }
        else {
            accuracy = new NumericString();
            precision = new NumericString();
            flagColName = "";
        }
    }

    /**
     * @return the accuracy (uncertainty) in values of this variable; never null but may be an empty numeric string.
     *         If not an empty numeric string, guaranteed to represent a finite positive number.
     */
    public NumericString getAccuracy() {
        return new NumericString(accuracy);
    }

    /**
     * @param accuracy
     *         assign as the accuracy (uncertainty) in values of this variable;
     *         if null, an empty NumericString is assigned
     *
     * @throws IllegalArgumentException
     *         if a numeric string is given but is not a finite positive number
     */
    public void setAccuracy(NumericString accuracy) throws IllegalArgumentException {
        if ( accuracy != null ) {
            // Empty numeric strings return false
            if ( accuracy.isNonPositive() )
                throw new IllegalArgumentException("accuracy numeric string given is not a finite positive number");
            this.accuracy = new NumericString(accuracy);
        }
        else
            this.accuracy = new NumericString();
    }

    /**
     * @return the precision (resolution) in values of this variable; never null but may be an empty numeric string.
     *         If not an empty numeric string, guaranteed to represent a finite positive number.
     */
    public NumericString getPrecision() {
        return new NumericString(precision);
    }

    /**
     * @param precision
     *         assign as the precision (resolution) in values of this variable;
     *         if null, an empty NumericString is assigned
     *
     * @throws IllegalArgumentException
     *         if a numeric string is given but is not a finite positive number
     */
    public void setPrecision(NumericString precision) {
        if ( precision != null ) {
            // Empty numeric strings return false
            if ( precision.isNonPositive() )
                throw new IllegalArgumentException("precision numeric string given is not a finite positive number");
            this.precision = new NumericString(precision);
        }
        else
            this.precision = new NumericString();
    }

    /**
     * @return the type of QC flag for this variable; never null but may be empty
     */
    public String getFlagColName() {
        return flagColName;
    }

    /**
     * @param flagColName
     *         assign as the type of QC flag for this variable; if null, an empty string is assigned
     */
    public void setFlagColName(String flagColName) {
        this.flagColName = (flagColName != null) ? flagColName.trim() : "";
    }

    /**
     * Assigns the unit in the accuracy NumericString in this object.
     * No check is made of the validity of the NumericString.
     *
     * @param unit
     *         unit string to assign
     */
    public void setAccuracyUnit(String unit) {
        accuracy.setUnitString(unit);
    }

    /**
     * Assigns the unit in the precision NumericString in this object.
     * No check is made of the validity of the NumericString.
     *
     * @param unit
     *         unit string to assign
     */
    public void setPrecisionUnit(String unit) {
        precision.setUnitString(unit);
    }

    @Override
    public Object duplicate(Object dup) {
        GenDataVar var;
        if ( dup == null )
            var = new GenDataVar();
        else
            var = (GenDataVar) dup;
        super.duplicate(var);
        var.accuracy = new NumericString(accuracy);
        var.precision = new NumericString(precision);
        var.flagColName = flagColName;
        return var;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = super.hashCode();
        result = result * prime + accuracy.hashCode();
        result = result * prime + precision.hashCode();
        result = result * prime + flagColName.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof GenDataVar) )
            return false;
        if ( !super.equals(obj) )
            return false;

        GenDataVar other = (GenDataVar) obj;

        if ( !accuracy.equals(other.accuracy) )
            return false;
        if ( !precision.equals(other.precision) )
            return false;
        if ( !flagColName.equals(other.flagColName) )
            return false;

        return true;
    }

    @Override
    public String toString() {
        String repr = super.toString().replaceFirst(super.getSimpleName(), getSimpleName());
        return repr.substring(0, repr.length() - 2) +
                ", accuracy=" + accuracy +
                ", precision=" + precision +
                ", flagColName='" + flagColName + "'" +
                " }";
    }

    @Override
    public String getSimpleName() {
        return "GenDataVar";
    }

}
