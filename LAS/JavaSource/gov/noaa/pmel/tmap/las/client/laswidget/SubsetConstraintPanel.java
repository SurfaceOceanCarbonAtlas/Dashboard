package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.event.AddSelectionConstraintEvent;
import gov.noaa.pmel.tmap.las.client.serializable.ConstraintSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.ERDDAPConstraint;
import gov.noaa.pmel.tmap.las.client.serializable.ERDDAPConstraintGroup;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;
import gov.noaa.pmel.tmap.las.client.util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SubsetConstraintPanel extends Composite {
    
    Map<String, String> currentValues;
    TextBox filter = new TextBox();
    Label filterLabel = new Label("Filter: ");
    
    HorizontalPanel filterPanel = new HorizontalPanel();
    
    // Current selection values
    ListBox valuesList = new ListBox();
    ScrollPanel outerScroller = new ScrollPanel();
    VerticalPanel mainPanel  = new VerticalPanel();
    HorizontalPanel rightPanel = new HorizontalPanel();
    HTML variableTitle = new HTML("<strong>Variable Range:</strong>");

    
    VerticalPanel radioButtonGroup = new VerticalPanel();  

    ClientFactory clientFactory = GWT.create(ClientFactory.class);
    EventBus eventBus = clientFactory.getEventBus();
    
    String currentVariable;
    ERDDAPConstraintGroup constraintGroup;

    String dsid;
    
    public SubsetConstraintPanel() {
        filter.addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                String f = filter.getText();
                valuesList.clear();
                if ( !f.equals("") ) {
                    for (Iterator rIt = currentValues.keySet().iterator(); rIt.hasNext();) {
                        String key_value = (String) rIt.next();
                        String value = (String) currentValues.get(key_value);
                        if ( value.toLowerCase(Locale.ENGLISH).contains(f.toLowerCase(Locale.ENGLISH)) ) {
                            valuesList.addItem(value, key_value);
                        }
                    }
                } else {
                    for (Iterator rIt = currentValues.keySet().iterator(); rIt.hasNext();) {
                        String key_value = (String) rIt.next();
                        String value = (String) currentValues.get(key_value);
                        valuesList.addItem(value, key_value);
                    }
                }
                
            }
            
        });
        valuesList.setVisibleItemCount(6);
        valuesList.setWidth(Constants.CONTROLS_WIDTH-20+"px");
        valuesList.addChangeHandler(new ChangeHandler(){

            @Override
            public void onChange(ChangeEvent event) {
                int index = valuesList.getSelectedIndex();
                if ( index >= 0 ) {
                    String key_value = valuesList.getValue(index);
                    String value = valuesList.getItemText(index);
                    String shortName = getShortName(currentVariable);
                    //  the key and the variable are always the same in this case.
                    
                    // Don't send an event if they click on the instructions.
                    if ( !value.equals(Constants.PICK) && !value.equals(Constants.APPEAR) && !value.equals(Constants.ONLYONE) && !value.equals(Constants.ONLYONE2) ) {
                    	eventBus.fireEventFromSource(new AddSelectionConstraintEvent(shortName, value, shortName, key_value, "eq"), SubsetConstraintPanel.this);
                    }
                }
            }
        });
        rightPanel.add(valuesList);
        valuesList.addItem(Constants.PICK);
        valuesList.addItem(Constants.APPEAR);
        filterPanel.add(filterLabel);
        filterPanel.add(filter);
        mainPanel.add(filterPanel);
        mainPanel.add(radioButtonGroup);
        mainPanel.add(rightPanel);
        mainPanel.setHeight("65px");
        outerScroller.add(mainPanel);
        initWidget(outerScroller);        
    }
    public void init(ERDDAPConstraintGroup constraintGroup) {
        this.constraintGroup = constraintGroup;
        radioButtonGroup.clear();
        for (Iterator conIt = constraintGroup.getConstraints().iterator(); conIt.hasNext();) {
            final ERDDAPConstraint constraint = (ERDDAPConstraint) conIt.next();
            dsid = constraintGroup.getDsid();
            final String key = constraint.getKey();
            // There should be only 1.
            VariableSerializable variable = constraint.getVariables().get(0);
           
            final String id = variable.getID();
            
            String type = constraint.getWidget();
            if (type.equals("list")) {

                RadioButton radio = new RadioButton("constraint_type", variable.getName());
                radioButtonGroup.add(radio);
                radio.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        filter.setText("");
                        RadioButton button = (RadioButton) event.getSource();
                        valuesList.clear();
                        valuesList.addItem(Constants.LOADING);
                        currentVariable = button.getText();
                                                                                         // For this part, we do not constrain on anything...
                        Util.getRPCService().getERDDAPOuterSequenceValues(dsid, id, key, constraint, new ArrayList<ConstraintSerializable>(), outerSequenceValuesCallback);
                    }

                });
            }
        }
    }
    protected AsyncCallback<Map<String, String>> outerSequenceValuesCallback = new AsyncCallback<Map<String,String>>() {

        @Override
        public void onFailure(Throwable caught) {
            Window.alert("Unable to get variable values from the server.");
        }

        @Override
        public void onSuccess(Map<String, String> result) {
            currentValues = result;
            valuesList.clear();
            valuesList.setVisibleItemCount(Math.min(result.keySet().size(), 6));
            for (Iterator rIt = result.keySet().iterator(); rIt.hasNext();) {
                String key_value = (String) rIt.next();
                String value = (String) result.get(key_value);
                valuesList.addItem(value, key_value);
            }
            
        }
    };
    private String getShortName(String variable) {
        for (Iterator conIt = constraintGroup.getConstraints().iterator(); conIt.hasNext();) {
            ERDDAPConstraint constraint = (ERDDAPConstraint) conIt.next();
            VariableSerializable vs = constraint.getVariables().get(0);
            if (vs.getName().equals(variable)) {
                return vs.getShortname();
            }
        }
        return null;
    }
//    public void addVariableConstraint(ERDDAPVariableConstraintPanel variableConstraints) {
//        mainPanel.remove(variableTitle);
//        mainPanel.remove(variableConstraints);
//        mainPanel.add(variableTitle);
//        mainPanel.add(variableConstraints);
//    }
    public void scrollToBottom() {
        outerScroller.scrollToBottom();
    }
    public void clearSelection() {
        int index = valuesList.getSelectedIndex();
        if ( index >= 0 ) {
            valuesList.setItemSelected(index, false);
        }
    }
}
