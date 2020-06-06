/**
 * 
 */
package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.event.MapChangeEvent;
import gov.noaa.pmel.tmap.las.client.event.UpdateFinishedEvent;
import gov.noaa.pmel.tmap.las.client.event.VariableSelectionChangeEvent;
import gov.noaa.pmel.tmap.las.client.event.WidgetSelectionChangeEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;

/**
 * A button which will alert the user that it needs to be pushed.
 * @author rhs
 *
 */
public class AlertButton extends Composite {

    private String alertStyle;
    private PushButton button;
    private EventBus eventBus;
    private HorizontalPanel panel;
    private CheckBox checkBox;
    /**
     * @param title The text that will appear on the button face.
     * @param alertStyle the dependent style suffix that will be appended to the default GWT style when the user is to be alerted.
     */
    public AlertButton(String title, String alertStyle) {
        button = new PushButton(title);
        button.addStyleDependentName("SMALLER");
        panel = new HorizontalPanel();
        panel.setBorderWidth(1);
        checkBox = new CheckBox();
        panel.add(button);
        button.setSize("66", "21");
        panel.add(checkBox);
        checkBox.setSize("20", "20");
        ClientFactory cf = GWT.create(ClientFactory.class);
        eventBus = cf.getEventBus();
        this.alertStyle = alertStyle;
        eventBus.addHandler(WidgetSelectionChangeEvent.TYPE, updateNeededEventHandler);
        eventBus.addHandler(VariableSelectionChangeEvent.TYPE, variableChangedEventHandler);
        eventBus.addHandler(UpdateFinishedEvent.TYPE, updateFinishedHandler);
        eventBus.addHandler(MapChangeEvent.TYPE, mapChangeHandler);
        initWidget(panel);
        panel.setSize("90", "23");
        this.ensureDebugId("AlertButton");
    }
    WidgetSelectionChangeEvent.Handler updateNeededEventHandler = new WidgetSelectionChangeEvent.Handler() {

        @Override
        public void onAxisSelectionChange(WidgetSelectionChangeEvent event) {
            button.addStyleDependentName(alertStyle);            
        }
        
    };
    VariableSelectionChangeEvent.Handler variableChangedEventHandler = new VariableSelectionChangeEvent.Handler() {

		@Override
		public void onVariableChange(VariableSelectionChangeEvent event) {
            button.addStyleDependentName(alertStyle);            			
		}
    	
    };
    private MapChangeEvent.Handler mapChangeHandler = new MapChangeEvent.Handler() {

        @Override
        public void onMapSelectionChange(MapChangeEvent event) {
            button.addStyleDependentName(alertStyle);
        }
    };
    UpdateFinishedEvent.Handler updateFinishedHandler = new UpdateFinishedEvent.Handler() {
        @Override
        public void onUpdateFinished(UpdateFinishedEvent event) {
            removeStyleDependentName(alertStyle);
        }
    };
    @Override
    public void removeStyleDependentName(String name) {
        button.removeStyleDependentName(name);
    }
    public void setText(String text) {
        button.setText(text);
    }
    public void addClickHandler(ClickHandler handler) {
        button.addClickHandler(handler);
    }
    public boolean getCheckBoxValue() {
        return checkBox.getValue();
    }
}
