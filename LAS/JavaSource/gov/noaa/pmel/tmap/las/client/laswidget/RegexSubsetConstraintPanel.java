package gov.noaa.pmel.tmap.las.client.laswidget;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.event.AddSelectionConstraintEvent;
import gov.noaa.pmel.tmap.las.client.event.VariableSelectionChangeEvent;
import gov.noaa.pmel.tmap.las.client.serializable.ERDDAPConstraint;
import gov.noaa.pmel.tmap.las.client.serializable.ERDDAPConstraintGroup;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class RegexSubsetConstraintPanel extends Composite {
    PushButton apply = new PushButton("Apply");
    Label regexLabel = new Label("Text to match: ");
    TextBox regex = new TextBox();
    HorizontalPanel regexPanel = new HorizontalPanel();
    VerticalPanel mainPanel = new VerticalPanel();
    VariableListBox variablesListBox = new VariableListBox(false);
    ClientFactory clientFactory = GWT.create(ClientFactory.class);
    EventBus eventBus = clientFactory.getEventBus();
    Map<String, VariableSerializable> keys = new HashMap<String, VariableSerializable>();
    public RegexSubsetConstraintPanel() {
        apply.addStyleDependentName("SMALLER");
        apply.setWidth("30px");
        regexPanel.add(regexLabel);
        regexPanel.add(regex);
        mainPanel.add(apply);
        mainPanel.add(variablesListBox);
        mainPanel.add(regexPanel);
        apply.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                String ex = regex.getText();
                if ( !ex.contains("*") || !ex.contains("]") || !ex.contains("[") ) {
                    ex = "(?i).*"+ex+".*";
                }
                VariableSerializable cv = variablesListBox.getVariable(variablesListBox.getSelectedIndex());
                eventBus.fireEventFromSource(new AddSelectionConstraintEvent(cv.getShortname(), ex, cv.getShortname(), ex, "like"), RegexSubsetConstraintPanel.this);
            }
        });
        initWidget(mainPanel);
        variablesListBox.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                eventBus.fireEventFromSource(new VariableSelectionChangeEvent(), RegexSubsetConstraintPanel.this);               
            }
            
        });
        eventBus.addHandler(VariableSelectionChangeEvent.TYPE, new VariableSelectionChangeEvent.Handler() {
            
            @Override
            public void onVariableChange(VariableSelectionChangeEvent event) {
                Widget source = (Widget) event.getSource();
                if (source instanceof RegexSubsetConstraintPanel ) {
                    regex.setText("");
                }
            }
        });
    }
    public void init(ERDDAPConstraintGroup constraintGroup) {
        List<ERDDAPConstraint>constraintGroups = constraintGroup.getConstraints();
        for (Iterator cgIt = constraintGroups.iterator(); cgIt.hasNext();) {
            ERDDAPConstraint erddapConstraint = (ERDDAPConstraint) cgIt.next();
            String key = erddapConstraint.getKey();
            VariableSerializable variable = erddapConstraint.getVariables().get(0);
            variablesListBox.addItem(variable);
            keys.put(key, variable);
        }
    }
}
