package gov.noaa.pmel.socatmetadata.shared.person;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.socatmetadata.shared.core.Duplicable;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Basic information to uniquely describe an investigator.
 */
public class Person implements Duplicable, Serializable, IsSerializable {

    private static final long serialVersionUID = -7541265229091626959L;

    private String lastName;
    private String firstName;
    private String middle;
    private String id;
    private String idType;
    private String organization;

    /**
     * Create with all empty fields.
     */
    public Person() {
        lastName = "";
        firstName = "";
        middle = "";
        id = "";
        idType = "";
        organization = "";
    }

    /**
     * Create with a copy of the information from the given Person
     */
    public Person(Person other) {
        lastName = other.lastName;
        firstName = other.firstName;
        middle = other.middle;
        id = other.id;
        idType = other.idType;
        organization = other.organization;
    }

    /**
     * Create with the arguments given passed to the appropriate setters
     */
    public Person(String lastName, String firstName, String middle, String id, String idType, String organization) {
        this();
        setLastName(lastName);
        setFirstName(firstName);
        setMiddle(middle);
        setId(id);
        setIdType(idType);
        setOrganization(organization);
    }

    /**
     * @return set of field names that are currently invalid
     */
    public HashSet<String> invalidFieldNames() {
        HashSet<String> invalid = new HashSet<String>();
        if ( lastName.isEmpty() )
            invalid.add("lastName");
        if ( firstName.isEmpty() )
            invalid.add("firstName");
        return invalid;
    }

    /**
     * @return the last name; never null but may be empty
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName
     *         assign as the last name; if null, an empty string is assigned
     */
    public void setLastName(String lastName) {
        this.lastName = (lastName != null) ? lastName.trim() : "";
    }

    /**
     * @return the first name; never null but may be empty
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName
     *         assign as the first name; if null, an empty string is assigned
     */
    public void setFirstName(String firstName) {
        this.firstName = (firstName != null) ? firstName.trim() : "";
    }

    /**
     * @return the middle name or initial(s); never null but may be empty
     */
    public String getMiddle() {
        return middle;
    }

    /**
     * @param middle
     *         assign as the middle name or initial(s); if null, an empty string is assigned
     */
    public void setMiddle(String middle) {
        this.middle = (middle != null) ? middle.trim() : "";
    }

    /**
     * @return the investigator ID; never null but may be empty
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *         assign as the investigator ID; if null, an empty string is assigned
     */
    public void setId(String id) {
        this.id = (id != null) ? id.trim() : "";
    }

    /**
     * @return the type / issuer of the investigator ID; never null but may be empty
     */
    public String getIdType() {
        return idType;
    }

    /**
     * @param idType
     *         assign as the type / issuer of the investigator ID; if null, an empty string is assigned
     */
    public void setIdType(String idType) {
        this.idType = (idType != null) ? idType.trim() : "";
    }

    /**
     * @return the organization name; never null but may be empty
     */
    public String getOrganization() {
        return organization;
    }

    /**
     * @param organization
     *         assign as the organization; if null, an empty string is assigned
     */
    public void setOrganization(String organization) {
        this.organization = (organization != null) ? organization.trim() : "";
    }

    /**
     * @return the name of the person in "(lastName), (firstName) (middle)" format;
     *         "Unknown" if lastName and firstName are empty,
     *         "Unknown (firstName) (middle)" if lastName is empty but firstName is not,
     *         "(lastName)" if firstName is empty but lastName is not, regardless of the value of middle.
     */
    public String getReferenceName() {
        String refName;
        if ( lastName.isEmpty() ) {
            refName = "Unknown";
            if ( !firstName.isEmpty() ) {
                refName += " " + firstName;
                if ( !middle.isEmpty() )
                    refName += " " + middle;
            }
        }
        else {
            refName = lastName;
            if ( !firstName.isEmpty() ) {
                refName += ", " + firstName;
                if ( !middle.isEmpty() )
                    refName += " " + middle;
            }
        }
        return refName;
    }

    /**
     * Generates initial(s) for the first name.  Normally this is the first character of the first name
     * followed by a period, but it can contain multiple letters in the case of hyphenated or
     * multiple names
     *
     * @return the initial(s) of the first name; never null but could be empty
     */
    public String firstInitial() {
        if ( firstName.isEmpty() )
            return "";
        String initial = null;
        for (String spacePiece : firstName.split("\\s+")) {
            String part = null;
            for (String hyphenPiece : spacePiece.split("-")) {
                if ( !hyphenPiece.isEmpty() ) {
                    if ( part == null )
                        part = hyphenPiece.substring(0, 1).toUpperCase() + ".";
                    else
                        part += "-" + hyphenPiece.substring(0, 1).toUpperCase() + ".";
                }
            }
            if ( (part != null) && !part.isEmpty() ) {
                if ( initial == null )
                    initial = part;
                else
                    initial += " " + part;
            }
        }
        if ( initial == null )
            initial = "";

        return initial;
    }

    /**
     * Creates a "clean" name for comparisons.  Currently this is done by removing punctuation,
     * replacing any amount of whitespace with a single space character, and trimming.
     *
     * @param name
     *         name to clean
     *
     * @return cleaned name
     */
    private String cleanName(String name) {
        return name.toUpperCase()
                   .replaceAll("\\p{Punct}+", "")
                   .replaceAll("\\s+", " ")
                   .trim();
    }

    public boolean matchesName(String name) {
        // null and empty names never match
        if ( (name == null) || lastName.isEmpty() )
            return false;
        // strip out punctuation and clean up spaces to simplify matching
        String cmpName = cleanName(name);
        // "Unknown"-type names never match
        if ( cmpName.isEmpty() )
            return false;
        String cmpUnknown = cleanName("Unknown");
        // note that the startsWith check assumes punctuation was removed
        if ( cmpName.equals(cmpUnknown) || cmpName.startsWith(cmpUnknown + " ") )
            return false;
        // check if just the last name - probably the most common case
        String cmpLast = cleanName(lastName);
        if ( cmpName.equals(cmpLast) )
            return true;
        String firstInit = firstInitial();
        // checks for names starting with the last name
        // note that the startsWith check assumes punctuation was removed
        if ( cmpName.startsWith(cmpLast + " ") && !firstName.isEmpty() ) {
            // Last, First M.
            if ( !middle.isEmpty() ) {
                if ( cmpName.equals(cleanName(lastName + ", " + firstName + " " + middle)) )
                    return true;
            }
            // Last, First
            if ( cmpName.equals(cleanName(lastName + ", " + firstName)) )
                return true;
            // Last, F.
            if ( cmpName.equals(cleanName(lastName + ", " + firstInit)) )
                return true;
            // Last, F.M.
            if ( !middle.isEmpty() ) {
                if ( cmpName.equals(cleanName(lastName + ", " + firstInit + middle)) )
                    return true;
                if ( cmpName.equals(cleanName(lastName + ", " + firstInit + " " + middle)) )
                    return true;
            }
        }
        // check for names start with the first name or first initial
        if ( !firstName.isEmpty() ) {
            // First M. Last
            if ( !middle.isEmpty() ) {
                if ( cmpName.equals(cleanName(firstName + " " + middle + " " + lastName)) )
                    return true;
            }
            // First Last
            if ( cmpName.equals(cleanName(firstName + " " + lastName)) )
                return true;
            // F. Last
            if ( cmpName.equals(cleanName(firstInit + " " + lastName)) )
                return true;
            // F. M. Last
            if ( !middle.isEmpty() ) {
                if ( cmpName.equals(cleanName(firstInit + " " + middle + " " + lastName)) )
                    return true;
            }
        }
        return false;
    }

    @Override
    public Object duplicate(Object dup) {
        Person person;
        if ( dup == null )
            person = new Person();
        else
            person = (Person) dup;
        person.lastName = lastName;
        person.firstName = firstName;
        person.middle = middle;
        person.id = id;
        person.idType = idType;
        person.organization = organization;
        return person;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = lastName.hashCode();
        result = result * prime + firstName.hashCode();
        result = result * prime + middle.hashCode();
        result = result * prime + id.hashCode();
        result = result * prime + idType.hashCode();
        result = result * prime + organization.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof Person) )
            return false;

        Person person = (Person) obj;

        if ( !lastName.equals(person.lastName) )
            return false;
        if ( !firstName.equals(person.firstName) )
            return false;
        if ( !middle.equals(person.middle) )
            return false;
        if ( !id.equals(person.id) )
            return false;
        if ( !idType.equals(person.idType) )
            return false;
        if ( !organization.equals(person.organization) )
            return false;

        return true;
    }

    @Override
    public String toString() {
        return getSimpleName() +
                "{ lastName='" + lastName + "'" +
                ", firstName='" + firstName + "'" +
                ", middle='" + middle + "'" +
                ", id='" + id + "'" +
                ", idType='" + idType + "'" +
                ", organization='" + organization + "'" +
                " }";
    }

    public String getSimpleName() {
        return "Person";
    }

}
