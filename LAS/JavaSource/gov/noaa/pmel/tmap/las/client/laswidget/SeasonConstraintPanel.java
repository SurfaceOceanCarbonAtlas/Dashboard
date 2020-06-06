package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.event.AddSelectionConstraintEvent;
import gov.noaa.pmel.tmap.las.client.event.RemoveSelectionConstraintEvent;
import gov.noaa.pmel.tmap.las.client.serializable.ERDDAPConstraintGroup;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ToggleButton;

public class SeasonConstraintPanel extends Composite {
    ClientFactory clientFactory = GWT.create(ClientFactory.class);
    EventBus eventBus = clientFactory.getEventBus();

    List<ToggleButton> buttons = new ArrayList<ToggleButton>();

    FlowPanel mainPanel = new FlowPanel();
    FlexTable circle = new FlexTable();
    FlowPanel firstRow = new FlowPanel();
    FlowPanel secondRow = new FlowPanel();
    FlowPanel thirdRow = new FlowPanel();

    ToggleButton jan = new ToggleButton("Jan");
    ToggleButton feb = new ToggleButton("Feb");
    ToggleButton mar = new ToggleButton("Mar");
    ToggleButton apr = new ToggleButton("Apr");

    ToggleButton may = new ToggleButton("May");
    ToggleButton jun = new ToggleButton("Jun");
    ToggleButton jul = new ToggleButton("Jul");
    ToggleButton aug = new ToggleButton("Aug");

    ToggleButton sep = new ToggleButton("Sep");
    ToggleButton oct = new ToggleButton("Oct");
    ToggleButton nov = new ToggleButton("Nov");
    ToggleButton dec = new ToggleButton("Dec");

    VariableSerializable var;
    HTML hint = new HTML("Select <i>any</i> sequence of consecutive months, e.g. DJF.");

    List<String> months = new ArrayList<String>();

    public SeasonConstraintPanel() {

        months.add("Jan");
        months.add("Feb");
        months.add("Mar");
        months.add("Apr");
        months.add("May");
        months.add("Jun");
        months.add("Jul");
        months.add("Aug");
        months.add("Sep");
        months.add("Oct");
        months.add("Nov");
        months.add("Dec");

        jan.addStyleName("IN-LINE-BUTTON");
        feb.addStyleName("IN-LINE-BUTTON");
        mar.addStyleName("IN-LINE-BUTTON");
        apr.addStyleName("IN-LINE-BUTTON");
        may.addStyleName("IN-LINE-BUTTON");
        jun.addStyleName("IN-LINE-BUTTON");
        jul.addStyleName("IN-LINE-BUTTON");
        aug.addStyleName("IN-LINE-BUTTON");
        sep.addStyleName("IN-LINE-BUTTON");
        oct.addStyleName("IN-LINE-BUTTON");
        nov.addStyleName("IN-LINE-BUTTON");
        dec.addStyleName("IN-LINE-BUTTON");

        buttons.add(jan);
        buttons.add(feb);
        buttons.add(mar);
        buttons.add(apr);
        buttons.add(may);
        buttons.add(jun);
        buttons.add(jul);
        buttons.add(aug);
        buttons.add(sep);
        buttons.add(oct);
        buttons.add(nov);
        buttons.add(dec);

        jan.addClickHandler(monthClickHandler);
        feb.addClickHandler(monthClickHandler);
        mar.addClickHandler(monthClickHandler);
        apr.addClickHandler(monthClickHandler);
        may.addClickHandler(monthClickHandler);
        jun.addClickHandler(monthClickHandler);
        jul.addClickHandler(monthClickHandler);
        aug.addClickHandler(monthClickHandler);
        sep.addClickHandler(monthClickHandler);
        oct.addClickHandler(monthClickHandler);
        nov.addClickHandler(monthClickHandler);
        dec.addClickHandler(monthClickHandler);


        circle.setWidget(0, 0, jan);
        circle.setWidget(0, 1, feb);
        circle.setWidget(0, 2, mar);
        circle.setWidget(0, 3, apr);
        circle.setWidget(1, 3, may);
        circle.setWidget(2, 3, jun);
        circle.setWidget(3, 3, jul);
        circle.setWidget(3, 2, aug);
        circle.setWidget(3, 1, sep);
        circle.setWidget(3, 0, oct);
        circle.setWidget(2, 0, nov);
        circle.setWidget(1, 0, dec);

        mainPanel.add(hint);
        mainPanel.add(circle);

        initWidget(mainPanel);
        eventBus.addHandler(RemoveSelectionConstraintEvent.TYPE, new RemoveSelectionConstraintEvent.Handler() {

            @Override
            public void onRemove(RemoveSelectionConstraintEvent event) {
                Object source = event.getSource();
                if ( source instanceof TextConstraintAnchor ) {
                    String month = event.getKeyValue();
                    int m = months.indexOf(month);
                    // TODO This text anchor might be for some other variable.  Can we do a better job of differentiating these?
                    if ( m >= 0 && m <=11 ) {
                        buttons.get(m).setDown(false);
                        disable(m, false);
                    }
                }

            }
        });


    }
    public void init(ERDDAPConstraintGroup constraintGroup) {
        var = constraintGroup.getConstraints().get(0).getVariables().get(0);
    }

    private ClickHandler monthClickHandler = new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
            ToggleButton button = (ToggleButton) event.getSource();
            int index = buttons.indexOf(button);
            if ( button.isDown() ) {
                enable(index);
            } else {
                disable(index, true);
            }
        }

    };
    private void enable(int index) {

        for ( int i = index+1; i < index + 12; i++ ) {
            int current = i % 12;
            int next = (i+1) % 12;
            int prev = (i-1) % 12;
            if ( !buttons.get(current).isDown() ) {
                if ( buttons.get(next).isDown() || buttons.get(prev).isDown() ) {
                    buttons.get(current).setEnabled(true);
                } else {
                    buttons.get(current).setEnabled(false);
                }
            } 
        }

        for ( int i = 0; i < 12; i++ ) {
            if ( buttons.get(i).isDown() ) {
                eventBus.fireEventFromSource(new AddSelectionConstraintEvent(var.getShortname(), months.get(i), var.getShortname(), months.get(i),"is"), this);
            }
        }
        //eventBus.fireEventFromSource(new AddVariableConstraintEvent(var.getDSID(), var.getShortname(), String.valueOf(start), "ge", var.getShortname(), String.valueOf(end), "le", true), this);
    }
    private void disable(int index, boolean fire) {
        if ( fire ) {
            eventBus.fireEventFromSource(new RemoveSelectionConstraintEvent(var.getShortname(), months.get(index), var.getShortname(), months.get(index)), this);
        }
        // Special case of clicking the start of the sequence.  It won't make an orphan to just turn it off.
        int prev;
        if ( index == 0 ) {
            prev = 11;
        } else {
            prev   = ( index - 1 ) % 12;
        }
        if ( buttons.get(prev).isEnabled() && !buttons.get(prev).isDown() ) {
            buttons.get(prev).setEnabled(false);
            anydown();
            return;
        }


        boolean foundSecondEnabled = false;
        boolean foundFirstEnabled = false;
        for ( int i = index+1; i < index + 12; i++ ) {
            int current = i % 12;

            if ( !foundFirstEnabled && buttons.get(current).isEnabled() && !buttons.get(current).isDown() ) {
                foundFirstEnabled = true;            
            } else if ( buttons.get(current).isEnabled() && !buttons.get(current).isDown() ) {
                foundSecondEnabled = true;
            }
            if (  !foundSecondEnabled ) {
                eventBus.fireEventFromSource(new RemoveSelectionConstraintEvent(var.getShortname(), months.get(current), var.getShortname(), months.get(current)), this);
                buttons.get(current).setDown(false);
                buttons.get(current).setEnabled(false);
            }
        }
        anydown();

    }
    private void anydown() {
        // If no button is down then everybody has to be enabled.
        boolean anydown = false;
        for (int i = 0; i < buttons.size(); i++) {
            anydown = anydown || buttons.get(i).isDown();
        }
        if ( !anydown ) {
            for (int i = 0; i < buttons.size(); i++) {
                buttons.get(i).setEnabled(true);
            }
        }
    }
}
