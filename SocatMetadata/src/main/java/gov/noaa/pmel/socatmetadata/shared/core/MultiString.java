package gov.noaa.pmel.socatmetadata.shared.core;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Represents a field with multiple lines.  Includes methods to parse a multiline
 * String as well as generate a multiline string.  Currently, parsing a multiline
 * String is done by splitting on one or more newline-type whitespace characters,
 * and generating a multiline String is done by joining the lines with the newline
 * character "\n" between each line.
 */
public class MultiString implements Iterable<String>, IsSerializable, Serializable {

    private static final long serialVersionUID = -6036572548870698604L;

    private ArrayList<String> stringList;

    /**
     * Creates with an empty list of lines
     */
    public MultiString() {
        stringList = new ArrayList<String>();
    }

    /**
     * Creates with a copy of the list of lines in the given MultiString
     * If null is given, creates with an empty list of lines.
     */
    public MultiString(MultiString other) {
        if ( other == null )
            stringList = new ArrayList<String>();
        else
            stringList = new ArrayList<String>(other.stringList);
    }

    /**
     * Creates with the list of lines obtained from the multiline String.
     * If null is given, creates with an empty list of lines.
     */
    public MultiString(String multiline) {
        stringList = new ArrayList<String>();
        if ( multiline != null )
            append(multiline);
    }

    /**
     * @param multiline
     *         add the lines obtained from this multiline String; cannot be null.
     *         Any blank lines obtained are ignored.
     */
    public void append(String multiline) {
        for (String strval : multiline.split("\\R+")) {
            if ( !strval.trim().isEmpty() )
                stringList.add(strval);
        }
    }

    /**
     * Removes and returns the first line from the list of lines.
     *
     * @return the line removed, or null if there are no lines
     */
    public String pop() {
        if ( stringList.isEmpty() )
            return null;
        return stringList.remove(0);
    }

    /**
     * Removes all of lines from the list of lines. This MultiString will be empty after this call returns.
     */
    public void clear() {
        stringList.clear();
    }

    /**
     * @return true if this MultiString contains no lines
     */
    public boolean isEmpty() {
        return stringList.isEmpty();
    }

    /**
     * @return the multiline representation of the MultiString.  Never null but may be empty.
     */
    public String asOneString() {
        String repr = "";
        for (String strval : stringList) {
            repr += strval;
            repr += "\n";
        }
        return repr.trim();
    }

    /**
     * Provided to satisfy JavaBean requirements for XML encoding/decoding.
     *
     * @return a copy of the list of strings in this object; never null
     */
    public ArrayList<String> getStringList() {
        return new ArrayList<String>(stringList);
    }

    /**
     * Provided to satisfy JavaBean requirements for XML encoding/decoding.
     *
     * @param stringList
     *         the list of strings to assign to this object;
     *         if null, an empty list is assigned
     */
    public void setStringList(ArrayList<String> stringList) {
        this.stringList.clear();
        if ( stringList != null ) {
            for (String strval : stringList) {
                append(strval);
            }
        }
    }

    @Override
    public Iterator<String> iterator() {
        return stringList.iterator();
    }

    @Override
    public int hashCode() {
        return stringList.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( !(obj instanceof MultiString) )
            return false;
        MultiString other = (MultiString) obj;
        return stringList.equals(other.stringList);
    }

    @Override
    public String toString() {
        String repr = "MultiString{";
        for (String strval : stringList) {
            repr += "\n----\"" + strval + "\"----";
        }
        repr += "\n}";
        return repr;
    }

}
