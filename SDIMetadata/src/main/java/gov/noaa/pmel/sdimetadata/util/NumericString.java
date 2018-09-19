package gov.noaa.pmel.sdimetadata.util;

/**
 * Represents a numeric string value with units (optional).
 * Used when the numeric value of the string needs to be validated and used.
 */
public final class NumericString implements Cloneable {

    private String valueString;
    private String unitString;
    // numericValue is always assigned from parsing valueString
    private double numericValue;

    /**
     * Create with empty strings, and NaN as the associated numeric value
     */
    public NumericString() {
        valueString = "";
        unitString = "";
        numericValue = Double.NaN;
    }

    /**
     * @param value
     *         numeric string to assign after trimming;  if null or blank,
     *         an empty string is assigned and the associated numeric value is Double.NaN
     * @param unit
     *         unit string for the numeric value;  if null or blank, and empty string is assigned
     *
     * @throws IllegalArgumentException
     *         if the given numeric string, if not null or blank, does not represent a finite numeric value
     */
    public NumericString(String value, String unit) throws IllegalArgumentException {
        setValueString(value);
        setUnitString(unit);
    }

    /**
     * @return the numeric string; never null but may be empty.
     *         If not empty, guaranteed to represents a finite number.
     */
    public String getValueString() {
        return valueString;
    }

    /**
     * @param valueString
     *         numeric string to assign after trimming; if null or blank,
     *         an empty string is assigned and the associated numeric value is NaN
     *
     * @throws IllegalArgumentException
     *         if the given string, if not null or blank, does not represent a finite numeric value
     */
    public void setValueString(String valueString) throws IllegalArgumentException {
        this.valueString = (valueString != null) ? valueString.trim() : "";
        if ( !this.valueString.isEmpty() ) {
            try {
                this.numericValue = Double.parseDouble(this.valueString);
            } catch ( NumberFormatException ex ) {
                throw new IllegalArgumentException(ex);
            }
            if ( Double.isNaN(this.numericValue) )
                throw new IllegalArgumentException("value is NaN");
            if ( Double.isInfinite(this.numericValue) )
                throw new IllegalArgumentException("value is infinite");
        }
        else
            this.numericValue = Double.NaN;
    }

    /**
     * @return the unit string for the numeric value; never null but may be empty
     */
    public String getUnitString() {
        return unitString;
    }

    /**
     * @param unitString
     *         assign at the unit string for the numeric value; if null or blank, an empty string is assigned
     */
    public void setUnitString(String unitString) {
        this.unitString = (unitString != null) ? unitString.trim() : "";
    }

    /**
     * @return the numeric value represented by the numeric string
     */
    public double getNumericValue() {
        return numericValue;
    }

    /**
     * Does nothing; the numeric value is always assigned from parsing the string value.
     * This only exist to satisfy JavaBean requirements.
     */
    private void setNumericValue(double numericValue) {
    }

    /**
     * @return if this represents a valid numeric string (not empty)
     */
    public boolean isValid() {
        if ( valueString.isEmpty() )
            return false;
        return true;
    }

    /**
     * @return if the numeric value represented by this string is a valid positive number
     */
    public boolean isPositive() {
        if ( valueString.isEmpty() )
            return false;
        return (numericValue > 0.0);
    }

    /**
     * @return if the numeric value represented by this string is a valid non-negative number
     */
    public boolean isNonNegative() {
        if ( valueString.isEmpty() )
            return false;
        return (numericValue >= 0.0);
    }

    /**
     * @return if the numeric value represented by this string is a valid non-positive number
     */
    public boolean isNonPositive() {
        if ( valueString.isEmpty() )
            return false;
        return (numericValue <= 0.0);
    }

    /**
     * @return if the numeric value represented by this string is a valid negative number
     */
    public boolean isNegative() {
        if ( valueString.isEmpty() )
            return false;
        return (numericValue < 0.0);
    }

    /**
     * @return the trimmed string resulting from concatenating the numeric string, a space, and the unit string
     */
    public String asOneString() {
        String repr = valueString + " " + unitString;
        return repr.trim();
    }

    @Override
    public NumericString clone() {
        NumericString dup;
        try {
            dup = (NumericString) super.clone();
        } catch ( CloneNotSupportedException ex ) {
            throw new RuntimeException(ex);
        }
        dup.valueString = valueString;
        dup.unitString = unitString;
        dup.numericValue = numericValue;
        return dup;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( !(obj instanceof NumericString) )
            return false;

        NumericString that = (NumericString) obj;

        if ( !valueString.equals(that.valueString) )
            return false;
        if ( !unitString.equals(that.unitString) )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = valueString.hashCode();
        result = result * prime + unitString.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "NumericString{" +
                "valueString='" + valueString + '\'' +
                ", unitString='" + unitString + '\'' +
                '}';
    }

}

