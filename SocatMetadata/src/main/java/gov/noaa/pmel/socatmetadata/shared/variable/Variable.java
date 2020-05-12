package gov.noaa.pmel.socatmetadata.shared.variable;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.socatmetadata.shared.core.Duplicable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Basic variable information, such as for a QC flag. It is the base class for all variable types.
 */
public class Variable implements Duplicable, Serializable, IsSerializable {

    private static final long serialVersionUID = 5735318506117787997L;

    protected String colName;
    protected String fullName;
    protected String varUnit;
    protected String missVal;
    protected ArrayList<String> addnInfo;

    /**
     * Create with all fields empty.
     */
    public Variable() {
        colName = "";
        fullName = "";
        varUnit = "";
        missVal = "";
        addnInfo = new ArrayList<String>();
    }

    /**
     * Create using as many of the values in the given variable subclass as possible.
     */
    public Variable(Variable var) {
        if ( var != null ) {
            colName = var.colName;
            fullName = var.fullName;
            varUnit = var.varUnit;
            missVal = var.missVal;
            addnInfo = new ArrayList<String>(var.addnInfo);
        }
        else {
            colName = "";
            fullName = "";
            varUnit = "";
            missVal = "";
            addnInfo = new ArrayList<String>();
        }
    }


    /**
     * @return set of field names that are currently invalid; never null but may be empty
     */
    public HashSet<String> invalidFieldNames() {
        HashSet<String> invalid = new HashSet<String>();
        if ( colName.isEmpty() )
            invalid.add("colName");
        if ( fullName.isEmpty() )
            invalid.add("fullName");
        return invalid;
    }

    /**
     * @return column name of this variable; never null but may be an empty string
     */
    public String getColName() {
        return colName;
    }

    /**
     * @param colName
     *         assign as the column name of this variable; if null, an empty string is assigned
     */
    public void setColName(String colName) {
        this.colName = (colName != null) ? colName.trim() : "";
    }

    /**
     * @return full name of this variable; never null but may be an empty string
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @param fullName
     *         assign as the full name of this variable; if null, an empty string is assigned
     */
    public void setFullName(String fullName) {
        this.fullName = (fullName != null) ? fullName.trim() : "";
    }


    /**
     * @return the unit of values for this variable; never null but may be an empty string
     */
    public String getVarUnit() {
        return varUnit;
    }

    /**
     * @param varUnit
     *         assign as the unit for values of this variable; if null, an empty string is assigned
     */
    public void setVarUnit(String varUnit) {
        this.varUnit = (varUnit != null) ? varUnit.trim() : "";
    }

    /**
     * @return the value assigned for missing values for this variable;
     *         never null but may be an empty string (which should be interpreted as standard missing values)
     */
    public String getMissVal() {
        return missVal;
    }

    /**
     * @param missVal
     *         assign as the value assigned for missing values for this variable;
     *         if null, an empty string is assigned (which should be interpreted as standard missing values)
     */
    public void setMissVal(String missVal) {
        this.missVal = (missVal != null) ? missVal.trim() : "";
    }

    /**
     * @return the list of additional information strings; never null but may be empty.
     *         Any strings given are guaranteed to be strings with content (not blank).
     */
    public ArrayList<String> getAddnInfo() {
        return new ArrayList<String>(addnInfo);
    }

    /**
     * Calls {@link #setAddnInfo(Iterable)}; added to satisfy JavaBean requirements.
     *
     * @param addnInfo
     *         assign as the list of additional information strings; if null, an empty list is assigned
     *
     * @throws IllegalArgumentException
     *         if any information string given is null or blank
     */
    public void setAddnInfo(ArrayList<String> addnInfo) throws IllegalArgumentException {
        setAddnInfo((Iterable<String>) addnInfo);
    }

    /**
     * @param addnInfo
     *         assign as the list of additional information strings; if null, an empty list is assigned
     *
     * @throws IllegalArgumentException
     *         if any information string given is null or blank
     */
    public void setAddnInfo(Iterable<String> addnInfo) throws IllegalArgumentException {
        this.addnInfo.clear();
        if ( addnInfo != null ) {
            for (String info : addnInfo) {
                if ( info == null )
                    throw new IllegalArgumentException("null information string given");
                info = info.trim();
                if ( info.isEmpty() )
                    throw new IllegalArgumentException("blank information string given");
                this.addnInfo.add(info);
            }
        }
    }

    /**
     * @return the column name of the variable if given (not empty);
     *         otherwise the full name of the variable if given (not empty);
     *         otherwise "unknown"
     */
    public String getReferenceName() {
        if ( !colName.isEmpty() )
            return colName;
        if ( !fullName.isEmpty() )
            return fullName;
        return "unknown";
    }

    @Override
    public Object duplicate(Object dup) {
        Variable var;
        if ( dup == null )
            var = new Variable();
        else
            var = (Variable) dup;
        var.colName = colName;
        var.fullName = fullName;
        var.varUnit = varUnit;
        var.missVal = missVal;
        var.addnInfo = new ArrayList<String>(addnInfo);
        return var;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof Variable) )
            return false;

        Variable other = (Variable) obj;

        if ( !colName.equals(other.colName) )
            return false;
        if ( !fullName.equals(other.fullName) )
            return false;
        if ( !varUnit.equals(other.varUnit) )
            return false;
        if ( !missVal.equals(other.missVal) )
            return false;
        if ( !addnInfo.equals(other.addnInfo) )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = colName.hashCode();
        result = result * prime + fullName.hashCode();
        result = result * prime + varUnit.hashCode();
        result = result * prime + missVal.hashCode();
        result = result * prime + addnInfo.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return getSimpleName() +
                "{ colName='" + colName + "'" +
                ", fullName='" + fullName + "'" +
                ", varUnit='" + varUnit + "'" +
                ", missVal='" + missVal + "'" +
                ", addnInfo=" + addnInfo +
                " }";
    }

    /**
     * @return the simple name for this type of variable; used to identifying the variable type in
     *         client code, and used in {@link #toString()}.  Essentially this.getClass().getSimpleName()
     *         but explicit defined for GWT client compatibility.
     */
    public String getSimpleName() {
        return "Variable";
    }

}
