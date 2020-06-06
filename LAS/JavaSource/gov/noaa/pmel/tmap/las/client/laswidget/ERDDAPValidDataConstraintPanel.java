package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.event.VariableConstraintEvent;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ERDDAPValidDataConstraintPanel extends Composite {

    HorizontalPanel row2 = new HorizontalPanel();


    PushButton apply = new PushButton("Apply");

    VariableSerializable variable;



    FlowPanel mainPanel = new FlowPanel();
    FlexTable layout = new FlexTable();
    VariableListBox variablesListBox = new VariableListBox(false);
    ClientFactory clientFactory = GWT.create(ClientFactory.class);
    EventBus eventBus = clientFactory.getEventBus();

    public ERDDAPValidDataConstraintPanel() {
        variablesListBox.setVisibleItemCount(1);
        variablesListBox.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                int index = variablesListBox.getSelectedIndex();
                variable = variablesListBox.getVariable(index);
                eventBus.fireEventFromSource(new VariableConstraintEvent(variable.getDSID(), variable.getID(), "", "ne", variable.getShortname(), "", "ne", false), ERDDAPValidDataConstraintPanel.this);
            }

        });


        apply.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                fireEvent();
            }
            
        });
        layout.setWidget(0, 0, apply);
       
        

        row2.add(variablesListBox);

        layout.setWidget(1, 0, row2);

        
        layout.getFlexCellFormatter().setRowSpan(0, 0, 3);
        
        mainPanel.add(layout);

        initWidget(mainPanel);
    }
    public void setVariable(VariableSerializable variable) {
        this.variable = variable;
    }
    public void setVariables(List<VariableSerializable> variables) {
        Vector vs = new Vector();
        for (Iterator varIt = variables.iterator(); varIt.hasNext();) {
            VariableSerializable variableSerializable = (VariableSerializable) varIt.next();
            // Filter out the subset variables since they don't have range values 
            if ( variableSerializable.getAttributes().get("subset_variable") == null || !variableSerializable.getAttributes().get("subset_variable").equals("true") ) {
                variablesListBox.addItem(variableSerializable);          
            }
        }
        int index = variablesListBox.getSelectedIndex();
        variable = variablesListBox.getVariable(index);
    }
    public void setVariables(VariableSerializable[] variables) {
        variablesListBox.clear();
        Vector vs = new Vector();
        for (int i = 0; i < variables.length; i++) {
            // Filter subset variable which do not have a range...
            if ( variables[i].getAttributes().get("subset_variable") == null || !variables[i].getAttributes().get("subset_variable").equals("true") ) {
                variablesListBox.addItem(variables[i]);
            }
        }
        int index = variablesListBox.getSelectedIndex();
        variable = variablesListBox.getVariable(index);
    }

    private void fireEvent() {

        String op1 = "ne";
        String op2 = "ne";
            
        eventBus.fireEventFromSource(new VariableConstraintEvent(variable.getDSID(), variable.getID(), "NaN", op1, variable.getShortname(), "NaN", op2, true), ERDDAPValidDataConstraintPanel.this);
    }
    public void clearSelection() {
        variablesListBox.clearSelection();
    }
}
