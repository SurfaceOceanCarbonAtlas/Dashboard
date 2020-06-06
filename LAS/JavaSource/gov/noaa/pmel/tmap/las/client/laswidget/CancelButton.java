package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.event.CancelEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CancelButton extends Composite {
    VerticalPanel sizePanel = new VerticalPanel();
    FlexTable interior = new FlexTable();
    PushButton cancel = new PushButton("Cancel");
    HTML batch = new HTML();
    Label message = new Label("Cancel the request pending for this panel.");
    private ClientFactory clientFactory = GWT.create(ClientFactory.class);
    private EventBus eventBus = clientFactory.getEventBus();
    public CancelButton(String panel_id) {
        final String id = panel_id;
        cancel.setWidth("75px");
        cancel.addStyleDependentName("SMALLER");
        cancel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent click) {
                eventBus.fireEvent(new CancelEvent(id));
            }
        });
        CellFormatter f = interior.getCellFormatter();
        f.setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        f.setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
        f.setVerticalAlignment(2, 0, HasVerticalAlignment.ALIGN_TOP);

        interior.setWidget(0, 0, batch);
        interior.setWidget(1, 0, message);
        interior.setWidget(2, 0, cancel);
        
        sizePanel.add(interior);
        
        initWidget(sizePanel);
    }
    public void setTime(int elapsed_time) {
        if ( elapsed_time > 0 ) {
            batch.setHTML("<br><br>Your request has been processing for " + elapsed_time + " seconds.<br>This panel will refresh automatically.");
        } else {
            batch.setHTML("<br><br>This panel will refresh automatically.");

        }
    }
}
