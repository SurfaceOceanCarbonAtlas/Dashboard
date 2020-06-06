package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.ListBox;

public class VariableListBox extends ListBox {
    List<VariableSerializable> tVariables = new ArrayList<VariableSerializable>();
    String header = null;
	public VariableListBox() {
	}

	public VariableListBox(boolean isMultipleSelect) {
		super(isMultipleSelect);
	}

	public VariableListBox(Element element) {
		super(element);
	}
    public void addItem(VariableSerializable v) {
    	tVariables.add(v);
    	addItem(v.getName(), v.getID());
    }
    public VariableSerializable getVariable(int i) {
    	return tVariables.get(i);
    }
    public void setHeader(String header) {
    	this.header = header;
    	addItem(header, "-");
    }
	public void setSelectedVariable(String id) {
		int index = 0;
	    int i = 0;
		for (Iterator varIt = tVariables.iterator(); varIt.hasNext();) {
			VariableSerializable var = (VariableSerializable) varIt.next();
			if ( var.getID().equals(id) ) {
				index = i;
			}
			i++;
		}
		setSelectedIndex(index);
	}
	public void removeItem(VariableSerializable variable) {
		int index = -1;
		for (int i = 0; i < getItemCount(); i++ ) {
			if ( getValue(i).equals(variable.getID()) ) index = i;
		}
		if ( index >= 0 ) {
			removeItem(index);
		}
		
	}
	public void restore() {
		clear();
		if ( header != null ) {
			addItem(header, "-");
		}
		for (Iterator varIt = tVariables.iterator(); varIt.hasNext();) {
			VariableSerializable var = (VariableSerializable) varIt.next();
			addItem(var.getName(), var.getID());
		}
	}
	public void clearSelection() {
	    int index = getSelectedIndex();
        if ( index >= 0 ) {
            setItemSelected(index, false);
        }
	}
}
