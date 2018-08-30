package gov.noaa.pmel.sdimetadata;

/**
 * Represents a numeric string value.
 */
public final class NumericString {

    private final String stringValue;
    private final double numericValue;

    /**
     * @param value
     *         numeric string to assign after trimming;  if null or blank,
     *         an empty string is assigned and the associated numeric value is Double.NaN
     *
     * @throws IllegalArgumentException
     *         if the given string does not represent a finite numeric value
     *         (other than null or blank)
     */
    public NumericString(String value) throws IllegalArgumentException {
        if ( (null == value) || value.trim().isEmpty() ) {
            stringValue = "";
            numericValue = Double.NaN;
        }
        else {
            String val = value.trim();
            double num;
            try {
                num = Double.parseDouble(val);
            } catch ( NumberFormatException ex ) {
                throw new IllegalArgumentException(ex);
            }
            if ( Double.isNaN(num) )
                throw new IllegalArgumentException("value is NaN");
            if ( Double.isInfinite(num) )
                throw new IllegalArgumentException("value is infinite");
            stringValue = val;
            numericValue = num;
        }
    }

    /**
     * @return if this is a valid numeric string (not an empty)
     */
    public boolean isValid() {
        if ( stringValue.isEmpty() )
            return false;
        return true;
    }

    /**
     * @return if the numeric value represented by this string is a valid positive number
     */
    public boolean isPositive() {
        if ( stringValue.isEmpty() )
            return false;
        return (numericValue > 0.0);
    }

    /**
     * @return if the numeric value represented by this string is a valid non-negative number
     */
    public boolean isNonNegative() {
        if ( stringValue.isEmpty() )
            return false;
        return (numericValue >= 0.0);
    }

    /**
     * @return if the numeric value represented by this string is a valid non-positive number
     */
    public boolean isNonPositive() {
        if ( stringValue.isEmpty() )
            return false;
        return (numericValue <= 0.0);
    }

    /**
     * @return if the numeric value represented by this string is a valid negative number
     */
    public boolean isNegative() {
        if ( stringValue.isEmpty() )
            return false;
        return (numericValue < 0.0);
    }

    /**
     * @return the numeric value represented by this string
     */
    public double numericValue() {
        return numericValue;
    }

    /**
     * @return if the numeric string this object represents is equal to the string representing the given object
     *         (the value returned its toString() method)
     */
    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        return stringValue.equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return stringValue.hashCode();
    }

    /**
     * @return the numeric string in this object
     */
    @Override
    public String toString() {
        return stringValue;
    }

}

