package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.serializable.ERDDAPConstraintGroup;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;

import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SelectionConstraintPanel extends Composite {
    
    
    VerticalPanel mainPanel  = new VerticalPanel();

    // Selection variables...
    VerticalPanel variablesRadioGroup = new VerticalPanel();
   
    
    ClientFactory clientFactory = GWT.create(ClientFactory.class);
    EventBus eventBus = clientFactory.getEventBus();
    
    SelectionConstraintMenu selectionConstraintMenu = new SelectionConstraintMenu();


    
    
    public SelectionConstraintPanel() {
        selectionConstraintMenu.setVisibleItemCount(10);
        mainPanel.add(variablesRadioGroup);
        mainPanel.add(selectionConstraintMenu);
        initWidget(mainPanel);
    }
    public void init(ERDDAPConstraintGroup constraintGroup) {
        variablesRadioGroup.clear();
        selectionConstraintMenu.init(constraintGroup.getConstraints().get(0), constraintGroup.getDsid(), constraintGroup.getConstraints().get(0).getKey());
        List<VariableSerializable> variables = constraintGroup.getConstraints().get(0).getVariables();
        for (Iterator varIt = variables.iterator(); varIt.hasNext();) {
            VariableSerializable variable = (VariableSerializable) varIt.next();
            final String id = variable.getID();
            String name = variable.getName();
            RadioButton button = new RadioButton("selection_variable", name);
            button.addStyleDependentName("PADDING");
            button.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    RadioButton button = (RadioButton) event.getSource();
                    if ( button.getValue() ) {
                        selectionConstraintMenu.load(id, button.getText());
                        
                    }
                }});
            variablesRadioGroup.add(button);
        }
    }
    private String getShortName(String name) {
        return selectionConstraintMenu.getShortName(name);
    }
    public void clearSelection() {
        selectionConstraintMenu.clearSelection();
    }
}
