package gov.noaa.pmel.tmap.las.client;

import gov.noaa.pmel.tmap.las.client.laswidget.ActionsMenuBar;
import gov.noaa.pmel.tmap.las.client.laswidget.ActionsMenuBarImpl;
import gov.noaa.pmel.tmap.las.client.laswidget.MultiVariableSelector;
import gov.noaa.pmel.tmap.las.client.laswidget.MultiVariableSelectorImpl;
import gov.noaa.pmel.tmap.las.client.laswidget.OutputControlPanel;
import gov.noaa.pmel.tmap.las.client.laswidget.OutputControlPanelImpl;
import gov.noaa.pmel.tmap.las.client.laswidget.ToggleButton;
import gov.noaa.pmel.tmap.las.client.laswidget.ToggleButtonImpl;
import gov.noaa.pmel.tmap.las.client.laswidget.VariableControlsOldAndComplicated;
import gov.noaa.pmel.tmap.las.client.laswidget.VariableControlsImpl;
import gov.noaa.pmel.tmap.las.client.laswidget.VariableMetadataView;
import gov.noaa.pmel.tmap.las.client.laswidget.VariableMetadataViewImpl;
import gov.noaa.pmel.tmap.las.client.laswidget.VariableSelector;
import gov.noaa.pmel.tmap.las.client.laswidget.VariableSelectorImpl;
import gov.noaa.pmel.tmap.las.client.rpc.RPCManager;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

//import com.google.gwt.place.shared.PlaceController;

/**
 * Sample implementation of {@link ClientFactory}.
 */
public class ClientFactoryImpl implements ClientFactory {

	private static final EventBus eventBus = new SimpleEventBus();
	// private static final PlaceController placeController = new
	// PlaceController(eventBus);
	private static final InteractiveDownloadDataView interactiveDownloadDataView = new InteractiveDownloadDataViewImpl();
	
    private static final RPCManager rpcManager = new RPCManager();
	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	// @Override
	// public PlaceController getPlaceController() {
	// return placeController;
	// }

	@Override
	public InteractiveDownloadDataView getInteractiveDownloadDataView() {
		return interactiveDownloadDataView;
	}

	@Override
	public OutputControlPanel getOutputControlPanel(String id) {
		return new OutputControlPanelImpl(id);
	}

	@Override
	public VariableControlsOldAndComplicated getVariableControls(String id) {
		return new VariableControlsImpl(id);
	}

	@Override
	public MultiVariableSelector getMultiVariableSelector(String id) {
		return new MultiVariableSelectorImpl(id);
	}

	@Override
	public VariableMetadataView getVariableMetadataView(String id) {
		return new VariableMetadataViewImpl(id);
	}

	@Override
	public VariableSelector getVariableSelectorView(String id) {
		return new VariableSelectorImpl(id);
	}

	@Override
	public ActionsMenuBar getActionsMenuBar(String id) {
		return new ActionsMenuBarImpl(id);
	}

	/**
	 * @see gov.noaa.pmel.tmap.las.client.ClientFactory#getToggleButton()
	 */
	@Override
	public ToggleButton getToggleButton(String upText, String downText,
			ClickHandler handler) {
		return new ToggleButtonImpl(upText, downText, handler);
	}

    @Override
    public RPCManager getRPCManager() {
        return rpcManager;
    }

}
