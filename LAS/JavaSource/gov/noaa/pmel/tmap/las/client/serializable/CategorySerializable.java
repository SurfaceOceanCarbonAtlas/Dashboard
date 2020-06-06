/**
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development. 
 */
package gov.noaa.pmel.tmap.las.client.serializable;



import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author rhs
 * 
 */
public class CategorySerializable extends Serializable implements IsSerializable, Comparable {
    
    boolean variableChildren;
    boolean categoryChildren;
    
    DatasetSerializable datasetSerializable;
    DatasetSerializable datasetSerializableArray[];
    
    public boolean hasMultipleDatasets() {
    	if ( datasetSerializableArray != null && datasetSerializableArray.length > 0 ) {
    		return true;
    	} else {
    		return false;
    	}
    }
	/**
	 * @return the datasetSerializableArray
	 */
	public DatasetSerializable[] getDatasetSerializableArray() {
		return datasetSerializableArray;
	}

	/**
	 * @param datasetSerializableArray the datasetSerializableArray to set
	 */
	public void setDatasetSerializableArray(
			DatasetSerializable[] datasetSerializableArray) {
		this.datasetSerializableArray = datasetSerializableArray;
	}

	/**
	 * 
	 */
	public CategorySerializable() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the variableChildren
	 */
	public boolean isVariableChildren() {
		return variableChildren;
	}

	/**
	 * @return the categoryChildren
	 */
	public boolean isCategoryChildren() {
		return categoryChildren;
	}

	/**
	 * @param variableChildren the variableChildren to set
	 */
	public void setVariableChildren(boolean variableChildren) {
		this.variableChildren = variableChildren;
	}

	/**
	 * @param categoryChildren the categoryChildren to set
	 */
	public void setCategoryChildren(boolean categoryChildren) {
		this.categoryChildren = categoryChildren;
	}

	/**
	 * @return the datasetSerializable
	 */
	public DatasetSerializable getDatasetSerializable() {
		return datasetSerializable;
	}

	/**
	 * @param datasetSerializable the datasetSerializable to set
	 */
	public void setDatasetSerializable(DatasetSerializable datasetSerializable) {
		this.datasetSerializable = datasetSerializable;
	}
	public VariableSerializable getVariable(String varID) {
		if (hasMultipleDatasets()) {
			for (int i = 0; i < datasetSerializableArray.length; i++) {
				DatasetSerializable ds = datasetSerializableArray[i];
				VariableSerializable[] vars = ds.getVariablesSerializable();
				for (int j = 0; j < vars.length; j++) {
					if ( vars[j].getID().equals(varID)) {
						return vars[j];
					}
				}
			}
		} else {
			VariableSerializable[] vars = datasetSerializable.getVariablesSerializable();
			for (int j = 0; j < vars.length; j++) {
				if ( vars[j].getID().equals(varID)) {
					return vars[j];
				}
			}
		}
		return null;
	}
	public int compareTo(Object o) {
		if ( o instanceof CategorySerializable ) {
			CategorySerializable c = (CategorySerializable) o;
			return getName().compareTo(c.getName());    	   
		}
		return 0;
	}
	public void setAttribute(String name, String value) {
		getAttributes().put(name, value);
	}
	public String getDoc() {
		String doc = getAttributes().get("doc");
		if ( doc == null || doc.equals("") ) {
			DatasetSerializable ds = getDatasetSerializable();
			if ( ds != null ) {
			    doc = ds.getAttributes().get("doc");
			    if ( doc != null && doc.equals("") ) {
			    	doc = null;
			    }
			}
		}
		return doc;
	}
    public void sortVariables() {
        if ( datasetSerializable != null ) {
            if ( datasetSerializable.getVariablesSerializable() != null && datasetSerializable.getVariablesSerializable().length > 0 ) {
                Arrays.sort(datasetSerializable.getVariablesSerializable(), new VariableNameOrSortOrderComparator());
            }
        }
    }
}
