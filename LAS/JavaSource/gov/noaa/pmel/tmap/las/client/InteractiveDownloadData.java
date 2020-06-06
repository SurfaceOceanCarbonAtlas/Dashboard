package gov.noaa.pmel.tmap.las.client;

import gov.noaa.pmel.tmap.las.client.activity.InteractiveDownloadDataViewActivity;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * A dialog window that handles interactively saving the users data. The data
 * region has already been selected. Users can choose among various data formats
 * and time periods. This is a GWT entry point class that uses
 * <code>onModuleLoad()</code>.
 * 
 * @author weusijana
 */
// TODO: Write tests (at least integration tests) using gwt-test-utils without Gin and Guice
public class InteractiveDownloadData implements EntryPoint {

	private InteractiveDownloadDataViewActivity presenter;
	// GUI objects
	private RootPanel rootPanel;

	public void onModuleLoad() {
		rootPanel = RootPanel.get();
		rootPanel.setSize("400", "700");
		ClientFactory clientFactory = GWT.create(ClientFactory.class);
		EventBus eventBus = clientFactory.getEventBus();
		presenter = new InteractiveDownloadDataViewActivity(clientFactory);
		// view = clientFactory.getView();
		// RootPanel.get().add(view);
		// view.setPresenter(presenter);
		ScrollPanel scrollPanel = new ScrollPanel();
		// scrollPanel.setWidget(view.asWidget());
		rootPanel.add(scrollPanel);
		presenter.start(scrollPanel, eventBus);
	}

}