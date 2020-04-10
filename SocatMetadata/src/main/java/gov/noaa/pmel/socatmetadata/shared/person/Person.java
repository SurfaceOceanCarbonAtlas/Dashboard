package gov.noaa.pmel.socatmetadata.shared.person;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.socatmetadata.shared.core.Duplicable;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Basic information to uniquely describe an investigator.
 */
public class Person implements Duplicable, Serializable, IsSerializable {

    private static final long serialVersionUID = -318083558608430425L;

    protected String lastName;
    protected String firstName;
    protected String middle;
    protected String id;
    protected String idType;
    protected String organization;

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
    public String toString() {
        return "Person{" +
                "lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middle='" + middle + '\'' +
                ", id='" + id + '\'' +
                ", idType='" + idType + '\'' +
                ", organization='" + organization + '\'' +
                '}';
    }

}
