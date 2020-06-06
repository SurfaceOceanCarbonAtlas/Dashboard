package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.map.OLMapWidget;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class VariableConstraintLayout extends Composite {
	VerticalPanel widgetPanel = new VerticalPanel();
	HorizontalPanel topRow = new HorizontalPanel();
	FlexTable layout = new FlexTable();

	VariableListBox constraintVariables = new VariableListBox();
	List<VariableConstraintWidget> widgets = new ArrayList<VariableConstraintWidget>();
	VariableConstraintWidget xConstraintWidget;
	VariableConstraintWidget yConstraintWidget;
	boolean xOn = false;
	boolean yOn = false;
	public VariableConstraintLayout(String title, boolean show_variables) {
//		help.setPopupWidth("550px");
//		help.setPopupHeight("550px");
//		help.setHelpURL("../css/constraint_help.html");
//		topRow.add(help);
		topRow.add(new HTML("<b>&nbsp;&nbsp;"+title+"&nbsp;&nbsp;</b>"));		
		if ( show_variables ) {
			topRow.add(constraintVariables);
		}
		widgetPanel.add(topRow);
		widgetPanel.add(layout);
		initWidget(widgetPanel);
	}
	public void addWidget(VariableConstraintWidget widget) {
		widgets.add(widget);
		int row = widgets.size() + 2;
		int index = widgets.size() + 1;
		layoutWidget(widget, index, row);
	}
	public void addWidgetForX(VariableConstraintWidget widget) {
		int row = 1;
		int index = 1; 
		layoutWidget(widget, index, row);
	}
	public void addWidgetForY(VariableConstraintWidget widget) {
		int row = 0;
		int index = 0;
		layoutWidget(widget, index, row);
	}
	private void layoutWidget(VariableConstraintWidget widget, int index, int row) {
		widget.getApply().getElement().setId("apply-"+index);
		widget.getMaxTextBox().getElement().setId("max-"+index);
		widget.getMinTextBox().getElement().setId("min-"+index);
		layout.setWidget(row, 0, widget);
	}
	public void setApply(int index, boolean value) {
		widgets.get(index).setApply(value);
	}
	public List<VariableConstraintWidget> getWidgets() {
		return widgets;
	}
	public void setWidgets(List<VariableConstraintWidget> list) {
		widgets = list;
	}
	public void removeWidget(VariableConstraintWidget vcw) {
		int index = -1;
		int i = 0;
		for (Iterator vcwIt = widgets.iterator(); vcwIt.hasNext();) {
			VariableConstraintWidget vc = (VariableConstraintWidget) vcwIt.next();
			if ( vc.getVariable().getID().equals(vcw.getVariable().getID())) index = i;
			i++;
		}
		if ( index >= 0 ) {
			layout.removeRow(index+3);
			widgets.remove(index);
		}
	}
	public void addItem(VariableSerializable var) {
		constraintVariables.addItem(var);
	}
	public void setHeader(String header) {
		constraintVariables.setHeader(header);
	}
	public void removeAll() {
		for (int i = 0; i < widgets.size(); i++) {
			layout.removeRow(i + 3);
		}
	}
	public void restore() {
		constraintVariables.restore();
	}
	public void removeItem(VariableSerializable variable) {
		constraintVariables.removeItem(variable);
	}
	public void addChangeHandler(ChangeHandler changeHandler) {
		constraintVariables.addChangeHandler(changeHandler);
	}
	public int getSelectedIndex() {
		return constraintVariables.getSelectedIndex();
	}
	public String getValue(int index) {
		return constraintVariables.getValue(index);
	}
	public VariableListBox getConstraintVariables() {
	    return constraintVariables;
	}
	public void reset() {
		removeAll();
		setWidgets(new ArrayList<VariableConstraintWidget>());
		restore();		
	}
}
