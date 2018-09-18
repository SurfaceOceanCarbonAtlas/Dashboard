package gov.noaa.pmel.sdimetadata.xml;

import gov.noaa.pmel.sdimetadata.person.Person;
import gov.noaa.pmel.sdimetadata.platform.PlatformType;
import gov.noaa.pmel.sdimetadata.util.Datestamp;
import gov.noaa.pmel.sdimetadata.util.NumericString;
import org.jdom2.Element;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

public abstract class DocumentHandler {

    /**
     * separator between element names when specifying the path name to an element
     */
    public static final String SEP = "\t";

    private static final SimpleDateFormat DATE_NUMBER_PARSER = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat DATE_SLASH_PARSER = new SimpleDateFormat("yyyy/MM/dd");
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    static {
        TimeZone utc = TimeZone.getTimeZone("UTC");
        DATE_NUMBER_PARSER.setTimeZone(utc);
        DATE_NUMBER_PARSER.setLenient(false);
        DATE_SLASH_PARSER.setTimeZone(utc);
        DATE_SLASH_PARSER.setLenient(false);
        DATE_FORMATTER.setTimeZone(utc);
        DATE_FORMATTER.setLenient(false);
    }

    protected Element rootElement = null;

    private static final Pattern EXPOCODE_PATTERN =
            Pattern.compile("[\\p{javaUpperCase}\\p{Digit}]{4}[\\p{Digit}]{8}");

    /**
     * NODC codes (all upper-case) for Moorings and Fixed Buoys
     */
    private static final HashSet<String> FIXED_PLATFORM_NODC_CODES =
            new HashSet<String>(Arrays.asList("067F", "08FS", "09FS", "147F", "187F", "18FX", "247F", "24FS",
                    "267F", "26FS", "297F", "3119", "3164", "317F", "32FS", "33GO", "33TT", "357F", "48MB",
                    "497F", "49FS", "747F", "74FS", "767F", "77FS", "907F", "91FS", "GH7F"));

    /**
     * NODC codes (all upper-case) for Drifting Buoys
     */
    private static final HashSet<String> DRIFTING_BUOY_NODC_CODES =
            new HashSet<String>(Arrays.asList("09DB", "18DZ", "35DR", "49DZ", "61DB", "74DZ", "91DB", "99DB"));

    /**
     * Guesses the platform type from the platform name or the NODC code from the dataset ID.
     * If the platform name or NODC code is that of a mooring or drifting buoy, that type is returned;
     * otherwise it is assumed to be a ship.
     *
     * @param name
     *         name of the platform; cannot be null
     * @param datasetId
     *         dataset ID; if it matches the expocode pattern,
     *         it is used to obtain the NODC code of the platform; cannot be null
     *
     * @return one of "Mooring", "Drifting Buoy", or "Ship"
     */
    public static PlatformType guessPlatformType(String name, String datasetId) {
        if ( name.toUpperCase().contains("MOORING") )
            return PlatformType.MOORING;
        if ( name.toUpperCase().contains("DRIFTING BUOY") )
            return PlatformType.DRIFTING_BUOY;
        if ( name.toUpperCase().contains("BUOY") )
            return PlatformType.MOORING;

        String expocode = datasetId.toUpperCase();
        int len = expocode.length();
        // Trim off the -N at the end of NODCYYYYMMDD-N
        if ( (len == 14) && (expocode.charAt(12) == '-') &&
                (expocode.charAt(13) >= '1') && (expocode.charAt(13) <= '9') ) {
            expocode = expocode.substring(0, 12);
            len = expocode.length();
        }
        // Only exception to NODCYYYYMMDD is QUIMAYYYYMMDD; but QUIM which is not one of the codes
        if ( (len == 12) && EXPOCODE_PATTERN.matcher(expocode).matches() ) {
            String nodc = expocode.substring(0, 4);
            if ( FIXED_PLATFORM_NODC_CODES.contains(nodc) )
                return PlatformType.MOORING;
            if ( DRIFTING_BUOY_NODC_CODES.contains(nodc) )
                return PlatformType.DRIFTING_BUOY;
        }

        return PlatformType.SHIP;
    }

    /**
     * Determine the lastName, firstName, and middle fields from a given full name.
     *
     * @param fullname
     *         the full name of the person; if null, all fields in the returned Person will be empty
     *
     * @return a Person with just the lastName, firstName and middle fields assigned; never null but fields may be empty
     */
    public static Person getPersonNames(String fullname) {
        Person person = new Person();
        if ( (fullname != null) && !fullname.isEmpty() ) {
            String[] pieces = fullname.split("[ \t]+");
            if ( pieces.length > 0 ) {
                if ( pieces[0].endsWith(",") || pieces[0].endsWith(";") ) {
                    person.setLastName(pieces[0].substring(0, pieces[0].length() - 1));
                    if ( pieces.length > 1 )
                        person.setFirstName(pieces[1]);
                    if ( pieces.length > 2 ) {
                        String middle = pieces[2];
                        for (int k = 3; k < pieces.length; k++) {
                            middle += " " + pieces[k];
                        }
                        person.setMiddle(middle);
                    }
                }
                else if ( pieces.length > 1 ) {
                    person.setFirstName(pieces[0]);
                    person.setLastName(pieces[pieces.length - 1]);
                    if ( pieces.length > 2 ) {
                        String middle = pieces[1];
                        for (int k = 2; k < pieces.length - 1; k++) {
                            middle += " " + pieces[k];
                        }
                        person.setMiddle(middle);
                    }
                }
                else {
                    person.setLastName(pieces[0]);
                }
            }
        }
        return person;
    }

    /**
     * @param multiline
     *         string containing line breaks to separate individual line strings;
     *         if null or blank, an empty list is returned
     *
     * @return list of trimmed individual line strings; never null but may be empty
     */
    public static ArrayList<String> getListOfLines(String multiline) {
        ArrayList<String> lineList = new ArrayList<String>();
        if ( multiline != null ) {
            for (String val : multiline.split("[\r\n]+")) {
                val = val.trim();
                if ( !val.isEmpty() )
                    lineList.add(val);
            }
        }
        return lineList;
    }

    /**
     * @param datestring
     *         date stamp as yyyyMMdd or yyyy/MM/dd or yyyy-MM-dd; if null or empty, null is returned
     *
     * @return datestamp representing this date, or null if the date string is not in a valid format
     *         (the validity of the date itself is not checked)
     */
    public static Datestamp getDatestamp(String datestring) {
        if ( (null == datestring) || datestring.trim().isEmpty() )
            return null;
        String hypenstring;
        try {
            // Convert yyyyMMdd to yyyy-MM-dd, checking if valid
            hypenstring = DATE_FORMATTER.format(DATE_NUMBER_PARSER.parse(datestring));
        } catch ( ParseException ex ) {
            hypenstring = null;
        }
        if ( null == hypenstring ) {
            // Convert yyyy/MM/dd to yyyy-MM-dd, checking if valid
            try {
                hypenstring = DATE_FORMATTER.format(DATE_SLASH_PARSER.parse(datestring));
            } catch ( ParseException ex ) {
                // leave hypenstring as null
            }
        }
        if ( null == hypenstring ) {
            // Check if given as yyyy-MM-dd and is valid
            try {
                hypenstring = DATE_FORMATTER.format(DATE_FORMATTER.parse(datestring));
            } catch ( ParseException ex ) {
                // leave hypenstring as null
            }
        }
        if ( null == hypenstring )
            return null;

        String[] pieces = hypenstring.split("-");
        if ( pieces.length != 3 )
            throw new RuntimeException("Unexpected hyphenated date of: " + hypenstring);
        try {
            return new Datestamp(pieces[0], pieces[1], pieces[2]);
        } catch ( IllegalArgumentException ex ) {
            return null;
        }
    }

    /**
     * @param date
     *         date to use; if null, null is returned
     *
     * @return datestamp of the given date; the time portion of the given date is ignored
     */
    public static Datestamp getDatestamp(Date date) {
        if ( null == date )
            return null;
        String hypenstring = DATE_FORMATTER.format(date);
        String[] pieces = hypenstring.split("-");
        if ( pieces.length != 3 )
            throw new RuntimeException("Unexpected hyphenated date of: " + hypenstring);
        try {
            return new Datestamp(pieces[0], pieces[1], pieces[2]);
        } catch ( IllegalArgumentException ex ) {
            throw new RuntimeException("Unexpected failure to make a datestamp from: " + hypenstring);
        }
    }

    /**
     * @param numVal
     *         the numeric string, possibly with an appended unit string if the given unit string is null;
     *         if null, an empty numeric string is assigned
     * @param unitVal
     *         the unit string; if null, the numeric string is examined for an appended unit string
     *
     * @return the numeric string object; never null but may be empty
     */
    public static NumericString getNumericString(String numVal, String unitVal) {
        String numStr = numVal;
        String unitStr = unitVal;
        if ( numStr != null ) {
            // Strip off any initial non-numeric values (such as ~ or ±)
            for (int k = 0; k < numStr.length(); k++) {
                char chr = numStr.charAt(k);
                if ( (chr == '-') || (chr == '+') || (chr == '.') || Character.isDigit(chr) ) {
                    numStr = numStr.substring(k);
                    break;
                }
            }
            if ( unitStr == null ) {
                // Check if unit is part of the string in the number element
                String[] pieces = numStr.split("[ \t\\(\\)\\[\\]\\{\\}]+");
                if ( pieces.length > 1 ) {
                    numStr = pieces[0];
                    unitStr = pieces[1];
                    for (int k = 2; k < pieces.length; k++) {
                        unitStr += " " + pieces[k];
                    }
                }
                else {
                    numStr = pieces[0];
                }
            }
        }
        try {
            return new NumericString(numStr, unitStr);
        } catch ( Exception ex ) {
            return new NumericString();
        }
    }

    /**
     * Get the list of all child elements matching a name path
     *
     * @param listFullElementName
     *         path from the root element to the desired elements; cannot be null or blank.
     *         Element names in the path should be separated by {@link #SEP}.
     *
     * @return list of all child elements matching the name path;
     *         an empty list is returned if no elements matching the path are found
     */
    public List<Element> getElementList(String listFullElementName) {
        Element elem = rootElement;
        String[] names = listFullElementName.split(SEP);
        for (int k = 0; k < names.length - 1; k++) {
            elem = elem.getChild(names[k]);
            if ( null == elem )
                return new ArrayList<Element>(0);
        }
        return elem.getChildren(names[names.length - 1]);
    }

    /**
     * Get the text from a specified element under the root element.
     *
     * @param fullElementName
     *         path from the root element to the element containing the text; cannot be null or blank.
     *         Element names in the path should be separated by {@link #SEP}.
     *
     * @return trimmed text of the specified element; an empty string is returned
     *         if the element is not found or if the element does not contain text
     */
    public String getElementText(String fullElementName) {
        Element elem = rootElement;
        for (String name : fullElementName.split(SEP)) {
            elem = elem.getChild(name);
            if ( null == elem )
                return "";
        }
        return elem.getTextTrim();
    }

    /**
     * Any content (child elements or text) in the indicated element is deleted and the given text is assigned as
     * its content.  Any elements in the path that do not exist are created unless the given text is null or blank.
     *
     * @param fullElementName
     *         path from the root element to the element to assign the text to; cannot be null or blank.
     *         Element names in the path should be separated by {@link #SEP}.
     * @param text
     *         text to assign after trimming
     */
    public void setElementText(String fullElementName, String text) {
        boolean noText = (null == text) || text.trim().isEmpty();
        Element parent = rootElement;
        for (String name : fullElementName.split(SEP)) {
            Element elem = parent.getChild(name);
            if ( null == elem ) {
                if ( noText )
                    return;
                elem = new Element(name);
                parent.addContent(elem);
            }
            parent = elem;
        }
        parent.setText(noText ? "" : text.trim());
    }

    /**
     * Creates an element with the indicated name as an new child element of its parent element.  Any parent elements
     * (all but the final name in the name path) that exist are reused, and those that do not exist are created.
     *
     * @param listFullElementName
     *         path from the root element to the child element to be created; cannot be null or blank.
     *         Element names in the path should be separated by {@link #SEP}.
     *
     * @return the new empty child element
     */
    public Element addListElement(String listFullElementName) {
        Element parent = rootElement;
        String[] names = listFullElementName.split(SEP);
        for (int k = 0; k < names.length - 1; k++) {
            Element elem = parent.getChild(names[k]);
            if ( null == elem ) {
                elem = new Element(names[k]);
                parent.addContent(elem);
            }
            parent = elem;
        }
        Element child = new Element(names[names.length - 1]);
        parent.addContent(child);
        return child;
    }

}

