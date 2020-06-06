package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.serializable.OperationSerializable;

import com.google.gwt.user.client.ui.RadioButton;
/**
 * A radio button used to build the operations selection interface since it keeps track of the
 * operation that it is associated with.
 * @author rhs
 *
 */
public class OperationRadioButton extends RadioButton {
    OperationSerializable operation;
    String view;
	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public OperationRadioButton(String name, String label, boolean asHTML) {
		super(name, label, asHTML);
	}

	public OperationRadioButton(String name, String label) {
		super(name, label);
	}

	public OperationRadioButton(String name) {
		super(name);
	}
    public OperationSerializable getOperation() {
    	return operation;
    }
    public void setOperation(OperationSerializable operation) {
    	this.operation = operation;
    }
}
