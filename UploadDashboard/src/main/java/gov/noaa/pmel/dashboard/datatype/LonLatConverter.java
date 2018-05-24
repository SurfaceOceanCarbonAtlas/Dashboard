/**
 *
 */
package gov.noaa.pmel.dashboard.datatype;

import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.ADCMessage;
import gov.noaa.pmel.dashboard.shared.QCFlag;

import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * For converting degree, degree-minute, and degree-minute-second string values to decimal degree values.
 *
 * @author Karl Smith
 */
public class LonLatConverter extends ValueConverter<Double> {

    public static final String DEGREE_SYMBOL = "\u00B0";

    // TreeSet so can do case insensitive comparisons
    private static final TreeSet<String> SUPPORTED_FROM_UNITS;

    static {
        SUPPORTED_FROM_UNITS = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        SUPPORTED_FROM_UNITS.add("from \"deg E\" to \"deg E\"");
        SUPPORTED_FROM_UNITS.add("from \"deg min E\" to \"deg E\"");
        SUPPORTED_FROM_UNITS.add("from \"deg min sec E\" to \"deg E\"");
        SUPPORTED_FROM_UNITS.add("from \"DDD.MMSSss E\" to \"deg E\"");
        SUPPORTED_FROM_UNITS.add("from \"deg W\" to \"deg E\"");
        SUPPORTED_FROM_UNITS.add("from \"deg min W\" to \"deg E\"");
        SUPPORTED_FROM_UNITS.add("from \"deg min sec W\" to \"deg E\"");
        SUPPORTED_FROM_UNITS.add("from \"DDD.MMSSss W\" to \"deg E\"");
        SUPPORTED_FROM_UNITS.add("from \"deg N\" to \"deg N\"");
        SUPPORTED_FROM_UNITS.add("from \"deg min N\" to \"deg N\"");
        SUPPORTED_FROM_UNITS.add("from \"deg min sec N\" to \"deg N\"");
        SUPPORTED_FROM_UNITS.add("from \"DD.MMSSss N\" to \"deg N\"");
        SUPPORTED_FROM_UNITS.add("from \"deg S\" to \"deg N\"");
        SUPPORTED_FROM_UNITS.add("from \"deg min S\" to \"deg N\"");
        SUPPORTED_FROM_UNITS.add("from \"deg min sec S\" to \"deg N\"");
        SUPPORTED_FROM_UNITS.add("from \"DD.MMSSss S\" to \"deg N\"");
    }

    private static final Pattern DEG_MIN_SPLIT_PATTERN = Pattern.compile("[ " + DEGREE_SYMBOL + "',]+");
    private static final Pattern DEG_MIN_SEC_SPLIT_PATTERN = Pattern.compile("[ " + DEGREE_SYMBOL + "'\",]+");

    public LonLatConverter(String inputUnit, String outputUnit, String missingValue)
            throws IllegalArgumentException, IllegalStateException {
        super(inputUnit, outputUnit, missingValue);
        String key = "from \"" + fromUnit + "\" to \"" + toUnit + "\"";
        if ( !SUPPORTED_FROM_UNITS.contains(key) )
            throw new IllegalArgumentException("conversion " + key + " not supported");
    }

    @Override
    public Double convertValueOf(String valueString) throws IllegalArgumentException, IllegalStateException {
        // Deal with missing values
        if ( isMissingValue(valueString, true) )
            return null;
        // Make sure nothing unexpected was added
        if ( !("deg E".equalsIgnoreCase(toUnit) || "deg N".equalsIgnoreCase(toUnit)) )
            throw new IllegalArgumentException("conversion to \"" + toUnit + "\" is not supported");
        // Given value can be given in decimal degrees, or
        // degrees and decimal minutes, or
        // degrees, minutes, and decimal seconds, or
        // as the DDD.MMSSsss floating point value.
        Double value;
        if ( "deg E".equalsIgnoreCase(fromUnit) || "deg W".equalsIgnoreCase(fromUnit) ||
                "deg N".equalsIgnoreCase(fromUnit) || "deg S".equalsIgnoreCase(fromUnit) ) {
            try {
                value = Double.valueOf(valueString);
            } catch ( Exception ex ) {
                throw new IllegalArgumentException("not a floating point value");
            }
        }
        else if ( "deg min E".equalsIgnoreCase(fromUnit) || "deg min W".equalsIgnoreCase(fromUnit) ||
                "deg min N".equalsIgnoreCase(fromUnit) || "deg min S".equalsIgnoreCase(fromUnit) ) {
            try {
                String[] parts = DEG_MIN_SPLIT_PATTERN.split(valueString, 0);
                if ( parts.length != 2 )
                    throw new Exception();
                value = Double.valueOf(parts[0]);
                value += (Double.valueOf(parts[1]) / 60.0);
            } catch ( Exception ex ) {
                throw new IllegalArgumentException("not a degree minute value");
            }
        }
        else if ( "deg min sec E".equalsIgnoreCase(fromUnit) || "deg min sec W".equalsIgnoreCase(fromUnit) ||
                "deg min sec N".equalsIgnoreCase(fromUnit) || "deg min sec S".equalsIgnoreCase(fromUnit) ) {
            try {
                String[] parts = DEG_MIN_SEC_SPLIT_PATTERN.split(valueString, 0);
                if ( parts.length != 3 )
                    throw new Exception();
                value = Double.valueOf(parts[0]);
                value += (Double.valueOf(parts[1]) / 60.0);
                value += (Double.valueOf(parts[2]) / 3600.0);
            } catch ( Exception ex ) {
                throw new IllegalArgumentException("not a degree minute second value");
            }
        }
        else if ( "DDD.MMSSss E".equalsIgnoreCase(fromUnit) || "DD.MMSSss N".equalsIgnoreCase(fromUnit) ||
                "DDD.MMSSss W".equalsIgnoreCase(fromUnit) || "DD.MMSSss S".equalsIgnoreCase(fromUnit) ) {
            try {
                // Just verify the string is in the appropriate format - that of a floating-point value
                // which happens to be the value only if minutes and seconds are not specified
                value = Double.valueOf(valueString);
                int dot = valueString.indexOf('.');
                if ( (dot >= 0) && (dot + 1 < valueString.length()) ) {
                    // Append zeros just to make it easier to deal with partial specification
                    String expanded = valueString + "0000000";
                    if ( dot > 0 )
                        value = Double.valueOf(valueString.substring(0, dot));
                    else
                        value = 0.0;
                    double minval = Double.valueOf(expanded.substring(dot + 1, dot + 3));
                    double secval = Double.valueOf(expanded.substring(dot + 3, dot + 5) +
                            "." + expanded.substring(dot + 5));
                    if ( value < 0.0 ) {
                        value -= minval / 60.0;
                        value -= secval / 3600.0;
                    }
                    else {
                        value += minval / 60.0;
                        value += secval / 3600.0;
                    }
                }
            } catch ( Exception ex ) {
                throw new IllegalArgumentException("not a DDD.MMSSss value");
            }
        }
        else {
            throw new IllegalArgumentException("conversion from \"" + fromUnit + "\" is not supported");
        }
        // check if it needs to be negated
        if ( fromUnit.endsWith("W") || fromUnit.endsWith("S") || fromUnit.endsWith("w") || fromUnit.endsWith("s") ) {
            if ( value != null )
                value *= -1.0;
        }
        // if longitude, if not an outrageous value, convert to (-180,180]
        if ( toUnit.endsWith("E") || toUnit.endsWith("e") ) {
            ADCMessage msg = DashboardServerUtils.LONGITUDE.boundsCheckStandardValue(value);
            if ( (msg == null) || msg.getSeverity().equals(QCFlag.Severity.WARNING) ) {
                while ( value <= -180.0 ) {
                    value += 360.0;
                }
                while ( value > 180.0 ) {
                    value -= 360.0;
                }
            }
        }
        return value;
    }

}
