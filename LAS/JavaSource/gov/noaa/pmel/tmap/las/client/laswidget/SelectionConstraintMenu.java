package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.event.AddSelectionConstraintEvent;
import gov.noaa.pmel.tmap.las.client.serializable.ConstraintSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.ERDDAPConstraint;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;
import gov.noaa.pmel.tmap.las.client.util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SelectionConstraintMenu extends Composite {
    
    
    VerticalPanel mainPanel  = new VerticalPanel();

    // Current selection values
    ListBox valuesList = new ListBox();
    
    // Use a message if there is only one.
    Label onlyOne = new Label("There is only one value.");
    
    // The data set for this group.
    String dsid;
    
    // The Key that drives all the selections for this group.
    String key;
 
    ClientFactory clientFactory = GWT.create(ClientFactory.class);
    EventBus eventBus = clientFactory.getEventBus();
    
    // The name of the currently selected variable from the radio buttons group.
    String currentVariable;
    
    // The constraint and the variables it needs
    ERDDAPConstraint constraint;
    
    Map<String, String> currentValues;
    
    TextBox filter = new TextBox();
    Label filterLabel = new Label("Filter: ");
    
    HorizontalPanel filterPanel = new HorizontalPanel();
    
    boolean adjust = true;
    boolean notify = false;

    List<ConstraintSerializable> constraints = new ArrayList<ConstraintSerializable>();
    
    protected AsyncCallback<Map<String, String>> outerSequenceValuesCallback = new AsyncCallback<Map<String,String>>() {

    	@Override
    	public void onFailure(Throwable caught) {
    		Window.alert("Unable to get variable values from the server. "+caught.getMessage());
    	}

    	@Override
    	public void onSuccess(Map<String, String> result) {
    		currentValues = result;
    		valuesList.clear();
    		if ( result.keySet().size() == 1 ) {
    			valuesList.setVisibleItemCount(2);
    			valuesList.addItem(Constants.ONLYONE);
    			valuesList.addItem(Constants.ONLYONE2);
    			if ( notify ) {
    				String key_value = result.keySet().iterator().next();
    				String value = (String) result.get(key_value);
    				String variable = getShortName(currentVariable);
    				eventBus.fireEventFromSource(new AddSelectionConstraintEvent(variable, value, key, key_value, "eq"), SelectionConstraintMenu.this);
    			}
    		} else {
    			if ( adjust ) {
    				valuesList.setVisibleItemCount(Math.min(result.keySet().size(), 10));
    			}
    			for (Iterator rIt = result.keySet().iterator(); rIt.hasNext();) {
    				String key_value = (String) rIt.next();
    				String value = (String) result.get(key_value);
    				valuesList.addItem(value, key_value);
    			}
    			if (notify) fireChoice();
    		}
    	}
    };
    
    
    public SelectionConstraintMenu() {
        valuesList.setVisibleItemCount(10);
       
        valuesList.addChangeHandler(new ChangeHandler(){

            @Override
            public void onChange(ChangeEvent event) {
                fireChoice();
            }
        });
        
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
        
        valuesList.addItem(Constants.PICK);
        valuesList.addItem(Constants.APPEAR);
        filterPanel.add(filterLabel);
        filterPanel.add(filter);
        mainPanel.add(filterPanel);
        
        valuesList.setWidth(Constants.CONTROLS_WIDTH-6+"px");
        valuesList.addStyleDependentName("PADDING");
        mainPanel.add(valuesList);
        initWidget(mainPanel);
    }
    public void fireChoice() {
        int index = valuesList.getSelectedIndex();
        String key_value = valuesList.getValue(index);
        String value = valuesList.getItemText(index);
        String variable = getShortName(currentVariable);
        // Don't send an event if they click on the instructions.
        if (  !value.equals(Constants.PICK) && !value.equals(Constants.APPEAR) && !value.equals(Constants.ONLYONE) && !value.equals(Constants.ONLYONE2) ) {
        	eventBus.fireEventFromSource(new AddSelectionConstraintEvent(variable, value, key, key_value, "eq"), SelectionConstraintMenu.this);
        }
    }
    public ConstraintSerializable getConstraint() {
        int index = valuesList.getSelectedIndex();
        String key_value = valuesList.getValue(index);
        String value = valuesList.getItemText(index);
        String variable = getShortName(currentVariable);
        String label = valuesList.getItemText(index);
        return new ConstraintSerializable(Constants.TEXT_CONSTRAINT, null, null, key, "eq", value, key+"_"+value, label);
    }
    public void setAdjust(boolean adjust) {
        this.adjust = adjust;
    }
    public String getShortName(String name) {
        List<VariableSerializable> variables = constraint.getVariables();
        for (Iterator varIt = variables.iterator(); varIt.hasNext();) {
            VariableSerializable variableSerializable = (VariableSerializable) varIt.next();
            if ( variableSerializable.getName().equals(name) ) {
                return variableSerializable.getShortname();
            }
        }
        return null;
    }
    public void clearSelection() {
        int index = valuesList.getSelectedIndex();
        if ( index >= 0 ) {
            valuesList.setItemSelected(index, false);
        }
    }

    public void load(String id, String shortname) {
        filter.setText("");
        valuesList.clear();
        valuesList.addItem(Constants.LOADING);
        currentVariable = shortname;
        Util.getRPCService().getERDDAPOuterSequenceValues(dsid, id, key, constraint, constraints, outerSequenceValuesCallback);
        
    }

    public void setVisibleItemCount(int i) {
        valuesList.setVisibleItemCount(i);
        
    }

    public void init(ERDDAPConstraint constraint, String dsid, String key) {
        this.constraint = constraint;
        this.dsid = dsid;
        this.key = key;
        
    }
    public void setNotify(boolean notify) {
        this.notify = notify;
    }
    public void setConstraints(List<ConstraintSerializable> constraints) {
        this.constraints = constraints;     
    }
}
