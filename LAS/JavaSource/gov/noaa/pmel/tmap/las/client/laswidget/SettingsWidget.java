package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.serializable.GridSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.OperationSerializable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
/**
 * A widget that contains a data picker, plot options and reset buttons for output panels that want to have
 * their own controls exposed.
 * @author rhs
 *
 */
public class SettingsWidget extends Composite {

	/*
	 * Objects common to any layout.
	 */
	protected OperationsWidget operations;
	protected PushButton closeButton;
	
	protected HorizontalPanel buttonBar;
	
	protected DatasetButton datasetButton;
	protected OptionsButton optionsButton;
	protected String operationID;
	protected String optionID;
	boolean usePanel = false;

	protected PushButton settingsButton;
	protected PopupPanel settingsPopup;
	

	protected FlexTable settingsLayout = new FlexTable();

	public SettingsWidget(String title, String panelID, String operationID, String optionID) {
		this.operationID = operationID;
		this.optionID = optionID;
		
		closeButton = new PushButton("Close");
		closeButton.addStyleDependentName("SMALLER");
		closeButton.setTitle("Close settings panel for "+panelID);
		closeButton.addClickListener(closeClick);
		buttonBar = new HorizontalPanel();
		buttonBar.add(closeButton);
		
		datasetButton = new DatasetButton();		
		optionsButton = new OptionsButton(optionID, 300);
		datasetButton.setOffset(0);

		operations = new OperationsWidget(title);
		operations.addClickHandler(operationsClickHandler);
	
		settingsButton = new PushButton (title);
		settingsButton.addStyleDependentName("SMALLER");
		settingsButton.addClickListener(settingsButtonClick);
		settingsPopup = new PopupPanel(false);

		buttonBar.add(datasetButton);
		buttonBar.add(optionsButton);

		settingsLayout = new FlexTable();
		settingsLayout.setWidget(0, 0, buttonBar);
		settingsLayout.setWidget(1, 0, operations);


		settingsPopup.add(settingsLayout);
		settingsButton.setWidth("65px");
		initWidget(settingsButton);	

	}

	
	public void addClickListener(ClickListener listener) {
		closeButton.addClickListener(listener);
	}

	public ClickHandler operationsClickHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {	
			//TODO  Need to be able to register an handler from the parent to get the map tool right.
			optionsButton.setOptions(getCurrentOperation().getOptionsID());
		}
	};

	public void addCloseClickListener(ClickListener close) {
		closeButton.addClickListener(close);
	}

	public void setOperations(String intervals, String dsID, String varID, String opID, String view) {
		operations.setOperations(intervals, dsID, varID, opID, view);
	}
	
	public void setOperations(GridSerializable grid, String opID, String view, OperationSerializable[] ops) {
		operations.setOperations(grid, opID, view, ops);
	}

	public boolean isUsePanelSettings() {
		return usePanel;
	}

	public void setUsePanel(boolean b) {
		usePanel = b;
	}

	public void addOptionsOkClickListener(ClickListener listener) {
	//	optionsButton.addOkClickListener(listener);
	}

	public Map<String, String> getOptions() {
		return optionsButton.getState();
	}

	public OperationSerializable getCurrentOperation() {
		return operations.getCurrentOperation();
	}

	public String getCurrentOperationView() {
		return operations.getCurrentView();
	}

	public OperationsWidget getOperationsWidget() {
		return operations;
	}

	public void addOperationClickHandler(ClickHandler operationsClickHandler) {
		operations.addClickHandler(operationsClickHandler);
	}

	public void setOperation(String id, String view) {
		operations.setOperation(id, view);		
	}
	protected ClickListener closeClick = new ClickListener() {
		public void onClick(Widget sender) {
			settingsPopup.hide();
		}
	};
	protected ClickListener settingsButtonClick = new ClickListener() {
		public void onClick(Widget sender) {
			// The parent is the button and it's parent is the panel, so we're putting this in the upper left of the panel.
			settingsPopup.setPopupPosition(settingsButton.getParent().getParent().getAbsoluteLeft(), settingsButton.getParent().getParent().getAbsoluteTop());
			settingsPopup.show();	
		}		
	};
   
	public String getHistoryToken() {
		StringBuilder token = new StringBuilder();
		
		if ( getCurrentOperation() != null ) {
			token.append(";operation_id="+getCurrentOperation().getID());
			token.append(";view="+getCurrentOperationView());
		}
		Map<String, String> options = getOptions();
		for (Iterator opIt = options.keySet().iterator(); opIt.hasNext();) {
			String name = (String) opIt.next();
			String value = options.get(name);
			if ( !value.equalsIgnoreCase("default") ) {
				token.append(";ferret_"+name+"="+value);
			}
		}		
		return token.toString();
	}
	public void setFromHistoryToken(Map<String, String> tokenMap, Map<String, String> optionsMap) {
		setOperation(tokenMap.get("operation_id"), tokenMap.get("view"));
		if ( optionsMap.size() >= 1 ) {
			optionsButton.setState(optionsMap);
		}
	}

	public void setOperationsMenu(OperationsMenu operationsMenu) {
		operations.setOperationsMenu(operationsMenu);
	}

	public void addComparisonAxisSelector(List<String> ortho) {
		// TODO Auto-generated method stub
		
	}
//	public HandlerRegistration addSelectionHandler(SelectionHandler<TreeItem> handler) {
//		return datasetButton.addSelectionHandler(handler);
//	}

	public void addOpenHandler(ClickHandler handler) {
		settingsButton.addClickHandler(handler);
	}
	public void addCloseHandler(ClickHandler handler) {
		closeButton.addClickHandler(handler);
	}
}