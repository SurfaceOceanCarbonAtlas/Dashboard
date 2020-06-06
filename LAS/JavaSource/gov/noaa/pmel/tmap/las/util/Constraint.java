/**
 * 
 */
package gov.noaa.pmel.tmap.las.util;

import java.util.regex.Pattern;

/**
 * A simple utility class that holds the three parts of a constraint, the left
 * hand side, the operation and the right hand side.
 * @author Roland Schweitzer
 *
 */
public class Constraint {
	String label;
    String lhs;
    String rhs;
    String op;
    public Constraint() {
        this.lhs = "";
        this.rhs = "";
        this.op = "";
    }
    public Constraint(String lhs, String op, String rhs) {
        this.lhs = lhs;
        this.op = op;
        this.rhs = rhs;
    }
    public String getLhs() {
        return lhs;
    }
    public void setLhs(String lhs) {
        this.lhs = lhs;
    }
    public String getOp() {
        return op;
    }
    public void setOp(String op) {
        this.op = op;
    }
    public String getRhs() {
        return rhs;
    }
    public void setRhs(String rhs) {
        this.rhs = rhs;
    }
    public void setLabel(String label) {
    	this.label = label;
    }
    public String getLabel() {
    	return this.label;
    }
    public String getOpAsSymbol() {
        String opString = "";
        if ( op.equals("lt")) {
            opString = "<";
        } else if ( op.equals("le")) {
            opString = "<=";
        } else if (op.equals("eq")) {
            opString = "=";
        } else if (op.equals("ne") ) {
            opString = "!=";
        } else if (op.equals("gt")) {
            opString = ">";
        } else if (op.equals("ge")) {
            opString = ">=";
        } else if ( op.equals("like") ) {
            opString = "=~";
        } else if ( op.equals("is") ) {
            opString = "=~";
        }
        return opString;
    }
    /**
     * 
     * @return constraint -- the SQL ready string to go in the WHERE clause
     */
    public String getAsString() {
        String constraintString = "";

        try {
            Float.valueOf(rhs).floatValue();
            constraintString = constraintString + lhs+getOpAsSymbol()+rhs;
        } catch (NumberFormatException e) {
            constraintString = constraintString + lhs+getOpAsSymbol()+"\""+rhs+"\"";
        }
        return constraintString;
    }
    public String getAsERDDAPString() {
        // Even stuff that looks like a number has to be enclosed in quotes for ERDDAP variables that come is a list of distinct values.
        if ( op.equals("is") || op.equals("like") || rhs.contains("*") || rhs.contains("[") || rhs.contains("]") ) {
            lhs = lhs.replaceAll("_ns_", "|");
            
            String[] parts = rhs.split("_ns_");
            StringBuilder r = new StringBuilder();
            for (int i = 0; i < parts.length; i++) {
            	
            	String p = parts[i]; // Don't use quote pattern... Pattern.quote(parts[i]);
                r.append(p);
                if ( i < parts.length - 1 ) {
                    r.append("|");
                }
            }
            
            rhs = r.toString();
            
        }
        return lhs+getOpAsSymbol()+"\""+rhs+"\"";
    }
    
}
