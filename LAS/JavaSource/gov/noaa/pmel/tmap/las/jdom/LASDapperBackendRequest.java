package gov.noaa.pmel.tmap.las.jdom;

import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.las.util.Constraint;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ucar.nc2.units.DateUnit;
import ucar.nc2.units.SimpleUnit;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A subclass of LASBackendRequest, specific to Dapper requests.
 *
 * @author Bob Simons (bob.simons@noaa.gov)
 */
public class LASDapperBackendRequest extends LASBackendRequest {
    
    /**
     * This class is the intantiation of the XML request to the backend service
     * from the product server.
     */
    private static final long serialVersionUID = 8177345236093847496L;  //super class is 5 at end
    private static Logger log = LoggerFactory.getLogger(LASDapperBackendRequest.class.getName());

    /**
     * This throws an LASException("Required value wasn't specified: " + id)
     * if s is null or "".
     * [This method could be in LASBackendRequest.]
     *
     * @param s a string which may be null or ""
     * @param id e.g., "database_access property 'url'"
     * @return s (for convenience)
     * @throws LASException("Required value wasn't specified: " + id)
     * if s is null or "".
     */
    public static String required(String s, String id) throws LASException {
        if (s == null || s.equals("")) 
            throw new LASException ("Required value wasn't specified: " + id + ".");
        return s;
    }

    /**
     * This returns a required databaseProperty.
     * [This method could be part of TemplateTool.]
     *
     * @param propertyName
     * @return the reqested databaseProperty.
     * @throws LASException if the property isn't found or is "".
     */
    public String getRequiredDatabaseProperty(String propertyName) throws LASException {
    	return required(getDatabaseProperty(propertyName), "database property \"" + propertyName + "\"");
    }

    /**
     * This generates the axis' constraint arrayList.
     * 
     * @param axis "y", "z", or "t"  (not "x")
     * @return the constraint for the axis, with the ampersand prefix (or "" if none). 
     * @throws LASException if trouble
     */
    public String getAxisConstraint(String axis) throws LASException {
        ArrayList<String> al = getAxisConstraintArrayList(axis);
        if (al.size() == 0) return "";
        return al.get(0);
    }

    /**
     * This generates the axis' constraint arrayList.
     * 
     * @param axis "x", "y", "z", or "t"
     * @return the constraints for the axis.
     *    For "x", there may be 0, 1, or 2 constraints. 
     *    For the others, there may be 0 or 1 constraints. 
     *    Each constraint includes the ampersand prefix.
     * @throws LASException if trouble
     */
    public ArrayList<String> getAxisConstraintArrayList(String axis) throws LASException {
        //this is modified only slightly from LASBackendRequest.getAxisAsDRDSConstraint
        String lo = "";
        String hi = "";
        String name = "";
        String quotes = "";

        if (axis.equals("x")) {
            lo = getDatabaseXlo();  //modified to be 0 - 360, or -180 - 180
            hi = getDatabaseXhi();
            name = getRequiredDatabaseProperty("longitude");
        } else if (axis.equals("y")) {
            lo = getYlo();
            hi = getYhi();
            name = getRequiredDatabaseProperty("latitude");
        } else if (axis.equals("z")) {
            lo = getZlo();
            hi = getZhi();
            name = getRequiredDatabaseProperty("depth");
        } else if (axis.equals("t")) {
            String tlo = getTlo();
            String thi = getThi();
            lo = getDatabaseTime(tlo);
            hi = getDatabaseTime(thi);
            name = getRequiredDatabaseProperty("time");
            String time_type = getRequiredDatabaseProperty("time_type");
            if ( time_type.equalsIgnoreCase("string") ) {
                quotes = "\"";
            }
        }

        return axisDapperConstraint(name, axis, lo, hi, quotes);
    }

    /**
     * Generates the constraints for the axis.
     *
     * @param column the name of the column
     * @param axis "x", "y", "z", or "t"
     * @param the lo value to be used in the constraint, or null or ""
     * @param the hi value to be used in the constraint, or null or ""
     * @param quotes used around the lo and hi values, e.g., "" or "\"" (sometimes for t axis)
     * @return the constraints for the axis.
     *    There may be 0, 1, or 2 constraints. Each includes the ampersand prefix.
     * @throws LASException if trouble
     */
    private ArrayList<String> axisDapperConstraint(String column, String axis, 
        String lo, String hi, String quotes) throws LASException {
        //this is modified only slightly from LASBackendRequest.axisDRDSConstraint

        ArrayList<String> axisConstraints = new ArrayList<String>();
        String glue = "&";
        String constraint = "";
        if (axis.equals("x") && 
            lo != null && lo.length() > 0 &&
            hi != null && hi.length() > 0 &&
            Float.valueOf(hi).floatValue() < Float.valueOf(lo).floatValue()) {

           /* 
            *             ------------------------------
            *             |                            |
            * xhi < xlo   |----x                 x-----|
            *             |                            |
            * xlo < xhi   |    x-----------------x     |
            *             |                            |
            *             ------------------------------
            *           
            * xhi < xlo, so make two constraints
            */
            constraint = glue + column + ">=" + quotes  + lo + quotes ;
            axisConstraints.add(constraint);

            constraint = glue + column + "<=" + quotes  + hi + quotes ;
            axisConstraints.add(constraint);

            return axisConstraints;
        }
        
        //make one, combined constraint  (or no constraint)
        if (lo != null && !lo.equals("")) 
            constraint = glue + column + ">=" + quotes + lo + quotes;
        
        if (hi != null && !hi.equals("")) 
            constraint += glue + column + "<=" + quotes + hi + quotes;

        if (constraint.length() > 0) 
           axisConstraints.add(constraint);

        return axisConstraints;

    }

    /**
     * Returns an array list of gov.noaa.pmel.tmap.las.util.Constraint objects
     * of the specified type (e.g., "variable", "text").
     * <pre>
     * &lt;constraint type="variable"&gt;
     *   &lt;lhs&gt;genus&lt;/lhs&gt;
     *   &lt;op&gt;eq&lt;/op&gt;
     *   &lt;rhs&gt;Macrocystis&lt;/rhs&gt;
     * &lt;/constraint&gt;
     * </pre>
     *
     * @return constraints an ArrayList of gov.noaa.pmel.tmap.las.util.Constraint objects
     */
    public ArrayList getConstraintsByType(String type) {
        ArrayList<Constraint> constraints = new ArrayList<Constraint>();
        List constraintElements = this.getRootElement().getChildren("constraint");
        for (Iterator cIt = constraintElements.iterator(); cIt.hasNext();) {
            Element constraint = (Element) cIt.next();
            String tType = constraint.getAttributeValue("type");
            if (tType == null || !tType.equals(type)) 
                continue;
            String rhsString = constraint.getChildText("rhs");
            String lhsString = constraint.getChildText("lhs");
            String opString = constraint.getChildText("op");
            Constraint c = new Constraint(lhsString, opString, rhsString);
            constraints.add(c);
        }
        return constraints;
    }

    /**
     * This returns the "variable" constraints (from getConstraintsByType("variable"))
     * as an opendap constraint string, e.g.,
     * "&amp;genus=Macrocystis&amp;species=integrifolia".
     *
     * @return the "variable" constraints (from getConstraintsByType("variable"))
     *   as an opendap constraint string. Returns "" if no variable constraints.
     *   There is no checking of the validity of the constraints.
     * @throws Exception if trouble, e.g., unknown op
     */
     public String getOpendapConstraint() {
         ArrayList al = getConstraintsByType("variable");
         StringBuffer sb = new StringBuffer();
         for (int i = 0; i < al.size(); i++) {
             Constraint c = (Constraint)al.get(i);
             sb.append("&" + c.getLhs() + c.getOpAsSymbol() + c.getRhs());
         }
         return sb.toString();
     }
}
