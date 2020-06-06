package gov.noaa.pmel.tmap.las.client;

import gov.noaa.pmel.tmap.las.client.laswidget.ActionsMenuBar;
import gov.noaa.pmel.tmap.las.client.laswidget.MultiVariableSelector;
import gov.noaa.pmel.tmap.las.client.laswidget.OutputControlPanel;
import gov.noaa.pmel.tmap.las.client.laswidget.ToggleButton;
import gov.noaa.pmel.tmap.las.client.laswidget.VariableControlsOldAndComplicated;
import gov.noaa.pmel.tmap.las.client.laswidget.VariableMetadataView;
import gov.noaa.pmel.tmap.las.client.laswidget.VariableSelector;
import gov.noaa.pmel.tmap.las.client.rpc.RPCManager;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;

//import com.google.gwt.place.shared.PlaceController;

/**
 * ClientFactory helpful to use a factory or dependency injection framework like
 * GIN to obtain references to objects needed throughout your application like
 * the {@link EventBus}, {@link PlaceController} and views.
 */
public interface ClientFactory {

	EventBus getEventBus();
	
	RPCManager getRPCManager();

	// PlaceController getPlaceController();
	public InteractiveDownloadDataView getInteractiveDownloadDataView();

	MultiVariableSelector getMultiVariableSelector(String id);

	OutputControlPanel getOutputControlPanel(String id);

	VariableControlsOldAndComplicated getVariableControls(String id);

	VariableMetadataView getVariableMetadataView(String id);

	VariableSelector getVariableSelectorView(String id);

	ActionsMenuBar getActionsMenuBar(String id);

	ToggleButton getToggleButton(String upText, String downText,
			ClickHandler handler);
}
