package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class VariableConstraintWidget extends Composite {
	FlexTable layout = new FlexTable();
	VariableSerializable xVariable;
	CheckBox xApply = new CheckBox("Apply");
	HTML xLabel = new HTML("variable");
	Label greaterThan = new Label("<");
	Label lessThanEqual = new Label("<=");
	TextBox minTextBox = new TextBox();
	TextBox maxTextBox = new TextBox();
	boolean removeable = false;
	PushButton xRemove = new PushButton("Remove");
	boolean active = true;
	public VariableConstraintWidget(boolean removable) {
		this.removeable = removable;
		init();
		initWidget(layout);
	}
	/**
	 * @wbp.parser.constructor
	 */
	public VariableConstraintWidget() {
		init();
		initWidget(layout);
	}
	private void init() {
		xRemove.addStyleDependentName("SMALLER");
		xLabel.setWidth("100px");
		minTextBox.setWidth("80px");
		maxTextBox.setWidth("80px");
		layout.setWidget(0, 0, xApply);
		layout.getCellFormatter().setWordWrap(0, 0, false);
		layout.setWidget(0, 1, minTextBox);
        layout.getCellFormatter().setWordWrap(0, 1, false);
		layout.setWidget(0, 2, greaterThan);
        layout.getCellFormatter().setWordWrap(0, 2, false);
		layout.setWidget(0, 3, xLabel);
        layout.getCellFormatter().setWordWrap(0, 3, false);
		layout.setWidget(0, 4, lessThanEqual);
        layout.getCellFormatter().setWordWrap(0, 4, false);
		layout.setWidget(0, 5, maxTextBox);
        layout.getCellFormatter().setWordWrap(0, 5, false);
		xLabel.addStyleName("hideextra");
		if ( removeable ) {
			layout.setWidget(0, 6, xRemove);
	        layout.getCellFormatter().setWordWrap(0, 6, false);
		}
	}
	public void setConstraint(String min, String max) {
		minTextBox.setValue(min);
		maxTextBox.setValue(max);
	}
	public void setVariable(VariableSerializable var) {
	    this.xVariable = var;
	    if ( xVariable.getShortname() != null && !xVariable.getShortname().equals("") ) {
	        xLabel.setText(xVariable.getShortname());
	    } else {
	        xLabel.setText(xVariable.getName());
	    }
	    xLabel.setTitle(xVariable.getName());
	    xRemove.getElement().setId("other-"+var.getID());
	}
    /**
     * Add a change handler to both the min and max text boxes of this constraint widget.
     * @param constraintChange
     */
	public void addChangeHandler(ChangeHandler constraintChange) {
		minTextBox.addChangeHandler(constraintChange);
		maxTextBox.addChangeHandler(constraintChange);
	}
	public void addApplyHandler(ClickHandler applyHandler) {
		xApply.addClickHandler(applyHandler);
	}
	public String getMin() {
		return minTextBox.getText();
	}
	public String getMax() {
		return maxTextBox.getText();
	}
	public void setMin(String min) {
		minTextBox.setText(min);
	}
	public void setMax(String max) {
		maxTextBox.setText(max);
	}
	public CheckBox getApply() {
		return xApply;
	}
	public Label getLabel() {
		return xLabel;
	}
	public TextBox getMinTextBox() {
		return minTextBox;
	}
	public TextBox getMaxTextBox() {
		return maxTextBox;
	}
	public PushButton getRemoveButton() {
		return xRemove;
	}
	public void setApply(boolean value) {
		xApply.setValue(value);
	}
	public VariableSerializable getVariable() {
		return xVariable;
	}
	public boolean isRemoveable() {
		return removeable;
	}
	public void addRemoveHandler(ClickHandler clickHandler) {
		xRemove.addClickHandler(clickHandler);	
	}
	public void setActive(boolean a) {
		this.active = a;
	}
	public boolean isActive() {
		return this.active;
	}	
	public void setVisible(boolean visible) {
		xApply.setVisible(visible);
		xLabel.setVisible(visible);
		minTextBox.setVisible(visible);
		maxTextBox.setVisible(visible);
	    xRemove.setVisible(visible);
	    lessThanEqual.setVisible(visible);
	    greaterThan.setVisible(visible);
	}
	public void setDebugId(String id) {
		xApply.ensureDebugId(id+"-xApply");
		minTextBox.ensureDebugId(id+"-minTextBox");
		maxTextBox.ensureDebugId(id+"-maxTextBox");
	}
}
