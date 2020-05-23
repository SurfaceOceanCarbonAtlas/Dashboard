package gov.noaa.pmel.socatmetadata.shared.core;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Represents a field containing multiple unique simple names.  Includes methods to
 * parse and generate string representations of this field.  Currently parsing is done
 * by splitting on comma characters (escaping and quoting not recognized) and trimming
 * whitespace; generating is a simple concatenation of the names using a comma and
 * space character as the separator.
 */
public class MultiNames implements Iterable<String>, IsSerializable, Serializable {

    private static final long serialVersionUID = -187285446844804775L;

    private TreeSet<String> nameSet;

    /**
     * Creates with an empty set of names
     */
    public MultiNames() {
        nameSet = new TreeSet<String>();
    }

    /**
     * Creates with a copy of the set of names in the given MultiNames
     * If null is given, creates with an empty set of names.
     */
    public MultiNames(MultiNames other) {
        if ( other == null )
            nameSet = new TreeSet<String>();
        else
            nameSet = new TreeSet<String>(other.nameSet);
    }

    /**
     * Creates with the set of names obtained from the given multiple names string.
     * If null is given, creates with an empty set of names.
     */
    public MultiNames(String namestring) {
        nameSet = new TreeSet<String>();
        if ( namestring != null )
            add(namestring);
    }

    /**
     * @param namestring
     *         add the names obtained from this multiple names string; cannot be null.
     *         Any blank names obtained from parsing are ignored
     */
    public void add(String namestring) {
        for (String strval : namestring.split(",+")) {
            strval = strval.trim();
            if ( !strval.isEmpty() )
                nameSet.add(strval);
        }
    }

    /**
     * Removes and returns the first name from the set of names.
     *
     * @return the name removed, or null if there are no names
     */
    public String pop() {
        return nameSet.pollFirst();
    }

    /**
     * Removes all of names from the set of names. This MultiNames will be empty after this call returns.
     */
    public void clear() {
        nameSet.clear();
    }

    /**
     * @return true if this MultiNames contains no names
     */
    public boolean isEmpty() {
        return nameSet.isEmpty();
    }

    /**
     * @param name
     *         name to check; if null or blank, false is returned
     *
     * @return if the given name is in this set of names
     */
    public boolean contains(String name) {
        if ( (name == null) || name.trim().isEmpty() )
            return false;
        return nameSet.contains(name);
    }

    /**
     * @return the multiple names string representation of the set of names.  Never null but may be empty.
     */
    public String asOneString() {
        String repr = null;
        for (String strval : nameSet) {
            if ( repr == null )
                repr = strval;
            else
                repr += ", " + strval;
        }
        if ( repr == null )
            repr = "";
        return repr;
    }

    /**
     * Provided to satisfy JavaBean requirements for XML encoding/decoding.
     *
     * @return a copy of the set of names in this object; never null
     */
    public TreeSet<String> getNameSet() {
        return new TreeSet<String>(nameSet);
    }

    /**
     * Provided to satisfy JavaBean requirements for XML encoding/decoding.
     *
     * @param nameSet
     *         the set of names to assign to this object; if null, an empty set is assigned
     */
    public void setNameSet(TreeSet<String> nameSet) {
        this.nameSet.clear();
        if ( nameSet != null ) {
            for (String strval : nameSet) {
                add(strval);
            }
        }
    }

    @Override
    public Iterator<String> iterator() {
        return nameSet.iterator();
    }

    @Override
    public int hashCode() {
        return nameSet.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( !(obj instanceof MultiNames) )
            return false;
        MultiNames other = (MultiNames) obj;
        return nameSet.equals(other.nameSet);
    }

    @Override
    public String toString() {
        return "MultiNames{ " + asOneString() + " }";
    }

}
