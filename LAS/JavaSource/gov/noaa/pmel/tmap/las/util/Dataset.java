/**
 * 
 */
package gov.noaa.pmel.tmap.las.util;

import gov.noaa.pmel.tmap.las.client.serializable.DatasetSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

/**
 * @author Roland Schweitzer
 *
 */
public class Dataset extends Container implements DatasetInterface {
    public Dataset(Element dataset) {
        super(dataset);
    }
    /* (non-Javadoc)
     * @see gov.noaa.pmel.tmap.las.util.DatasetInterface#getXPath()
     */
    public String getXPath() {
        return "/lasdata/datasets/dataset@[ID='"+getID()+"']";
    }
    public String getDoc() {
    	return element.getAttributeValue("doc");
    }
    public String getCATID() {
        return element.getAttributeValue("catid");
    }
    public void setCATID(String catid) {
        element.setAttribute("catid", catid);
    }
    public void setVariables(ArrayList<Variable> variables) {
    	element.removeChild("variables");
    	Element varsE = new Element("variables");
    	for (Iterator varIt = variables.iterator(); varIt.hasNext();) {
    		Variable var = (Variable) varIt.next();
    		varsE.addContent(var.getElement());
    	}
    	element.addContent(varsE);
    }
    public ArrayList<Variable> getVariables() {
    	ArrayList<Variable> vars = new ArrayList<Variable>();
    	Element variablesElement = element.getChild("variables");
    	if (variablesElement != null){
    		List variables = variablesElement.getChildren("variable");
    		if ( variables != null ) {
    			for (Iterator varIt = variables.iterator(); varIt.hasNext();) {
    				Element var = (Element) varIt.next();
    				Variable v = new Variable(var, getCATID(), getID(), getName());
    				vars.add(v);
    			}
    		}
    	}
    	return vars;
    }
    public VariableSerializable[] getVariablesSerializable() {
    	List variables = element.getChild("variables").getChildren("variable");
    	VariableSerializable[] vs = new VariableSerializable[variables.size()];
    	int i = 0;
    	for (Iterator varIt = variables.iterator(); varIt.hasNext();) {
			Element var = (Element) varIt.next();
			Variable v = new Variable(var, getCATID(), getID(), getName());
			vs[i] = v.getVariableSerializable();
		    i++;	
		}
    	return vs;
    }
	public void addVariable(Variable variable) {
		Element variables = element.getChild("variables");
		variables.addContent((Element) variable.getElement().clone());
	}
	public DatasetSerializable getDatasetSerializable() {
		DatasetSerializable wire_ds = new DatasetSerializable();
		wire_ds.setName(getName());
		wire_ds.setID(getID());
		wire_ds.setVariablesSerializable(getVariablesSerializable());
		return wire_ds;
	}
	public Variable getVariable(String varid) {
		ArrayList<Variable> variables = getVariables();
		for (Iterator varId = variables.iterator(); varId.hasNext();) {
			Variable variable = (Variable) varId.next();
			if ( variable.getID().equals(varid) ) {
				variable.setDSID(getID());
				variable.setDSName(getName());
				variable.setCATID(getCATID());
				return variable;
			}
		}
		return null;
	}
}
