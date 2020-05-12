package gov.noaa.pmel.socatmetadata.shared.variable;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.socatmetadata.shared.core.Duplicable;
import gov.noaa.pmel.socatmetadata.shared.core.NumericString;

import java.io.Serializable;

/**
 * Generic numeric variable information
 */
public class GenDataVar extends Variable implements Duplicable, Serializable, IsSerializable {

    private static final long serialVersionUID = -3105506219374546272L;

    protected NumericString accuracy;
    protected NumericString precision;
    protected String flagColName;

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
            accuracy = (NumericString) (other.accuracy.duplicate(null));
            precision = (NumericString) (other.precision.duplicate(null));
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
        return (NumericString) (accuracy.duplicate(null));
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
            this.accuracy = (NumericString) (accuracy.duplicate(null));
        }
        else
            this.accuracy = new NumericString();
    }

    /**
     * @return the precision (resolution) in values of this variable; never null but may be an empty numeric string.
     *         If not an empty numeric string, guaranteed to represent a finite positive number.
     */
    public NumericString getPrecision() {
        return (NumericString) (precision.duplicate(null));
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
            this.precision = (NumericString) (precision.duplicate(null));
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

    @Override
    public Object duplicate(Object dup) {
        GenDataVar var;
        if ( dup == null )
            var = new GenDataVar();
        else
            var = (GenDataVar) dup;
        super.duplicate(var);
        var.accuracy = (NumericString) (accuracy.duplicate(null));
        var.precision = (NumericString) (precision.duplicate(null));
        var.flagColName = flagColName;
        return var;
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
    public int hashCode() {
        final int prime = 37;
        int result = super.hashCode();
        result = result * prime + accuracy.hashCode();
        result = result * prime + precision.hashCode();
        result = result * prime + flagColName.hashCode();
        return result;
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
