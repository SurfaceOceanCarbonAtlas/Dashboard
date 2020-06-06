package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.event.VariableConstraintEvent;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;

public class ERDDAPVariableConstraint extends Composite {
    
    FlowPanel mainPanel = new FlowPanel();
    FlexTable layout = new FlexTable();
    HorizontalPanel row1 = new HorizontalPanel();
    HorizontalPanel row2 = new HorizontalPanel();
    HorizontalPanel row3 = new HorizontalPanel();
    TextBox lhs = new TextBox();
    TextBox rhs = new TextBox();
    HTML name = new HTML("variable");
    PushButton apply = new PushButton("Apply");
    HTML lessThan = new HTML("&lt;");
    HTML lessThanEqual = new HTML("&le;");
    
    VariableSerializable variable;
    
    ClientFactory clientFactory = GWT.create(ClientFactory.class);
    EventBus eventBus = clientFactory.getEventBus();
    
    public ERDDAPVariableConstraint(VariableSerializable variableSerializable) {
        
        this.variable = variableSerializable;
        mainPanel.setWidth(Constants.CONTROLS_WIDTH-30+"px");
        layout.setWidget(0, 0, apply);
        
        lhs.setWidth("90px");
        rhs.setWidth("90px");
        
        lhs.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                fireEvent();
            }
            
        });
        rhs.addChangeHandler( new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                fireEvent();
            }
            
        });
        apply.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {               
                fireEvent();
            }
        });
        
        mainPanel.addStyleName("allBorder");
        if ( variableSerializable.getShortname() != null ) {
            name.setText(variableSerializable.getShortname());
        } else {
            name.setText(variableSerializable.getName());
        }
        name.setTitle(variableSerializable.getName());
        
        row1.add(lhs);
        row1.add(lessThan);

        row2.add(name);
        
        row3.add(lessThanEqual);
        row3.add(rhs);
        
        layout.setWidget(0, 1, row1);
        layout.setWidget(1, 0, row2);
        layout.setWidget(2, 0, row3);
        
        layout.getFlexCellFormatter().setRowSpan(0, 0, 3);
        
        mainPanel.add(layout);
        
        initWidget(mainPanel);
        
        
    }
    private void fireEvent() {
        String lhsText = lhs.getText();
        String op1 = "gt";
        String op2 = "le";
        String rhsText = rhs.getText();              
        eventBus.fireEventFromSource(new VariableConstraintEvent(variable.getDSID(), variable.getID(), lhsText, op1, variable.getShortname(), rhsText, op2, true), ERDDAPVariableConstraint.this);
    }
    public VariableSerializable getVariable() {
        return variable;
    }
    public void clearLhs() {
        lhs.setText("");
    }
    public String getLhs() {
        return lhs.getText();
    }
    public void clearRhs() {
        rhs.setText("");
    }
    public String getRhs() {
        return rhs.getText();
    }
    public void setLhs(String text) {
        lhs.setText(text);        
    }
    public void setRhs(String text) {
        rhs.setText(text);
    }
}
