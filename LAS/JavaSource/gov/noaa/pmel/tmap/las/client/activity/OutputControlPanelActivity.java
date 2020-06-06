package gov.noaa.pmel.tmap.las.client.activity;

//import gov.noaa.pmel.tmap.las.client.laswidget.%placeName%;
import gov.noaa.pmel.tmap.las.client.AppConstants;
import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.event.ComparisonModeChangeEvent;
import gov.noaa.pmel.tmap.las.client.event.ControlVisibilityEvent;
import gov.noaa.pmel.tmap.las.client.event.ControlVisibilityEvent.Handler;
import gov.noaa.pmel.tmap.las.client.event.StringValueChangeEvent;
import gov.noaa.pmel.tmap.las.client.event.StringValueChangeEvent.*;
import gov.noaa.pmel.tmap.las.client.laswidget.OutputControlPanel;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.PushButton;

/**
 * Activities are started and stopped by an ActivityManager associated with a
 * container Widget.
 */
public class OutputControlPanelActivity extends AbstractActivity implements
		OutputControlPanel.Presenter {
	private static final AppConstants CONSTANTS = GWT
			.create(AppConstants.class);

	private ClientFactory clientFactory;
	private EventBus eventBus;
	private String name;

	// Don't allow this Activity to be constructed without a name ID set
	@SuppressWarnings("unused")
	private OutputControlPanelActivity() {
		clientFactory = GWT.create(ClientFactory.class);
	}

	public OutputControlPanelActivity(ClientFactory clientFactory, String id) {
		this.clientFactory = clientFactory;
		this.name = id;
	}

	public OutputControlPanel init(String id) {
		if (eventBus == null) {
			eventBus = clientFactory.getEventBus();
		}

		final OutputControlPanel view = clientFactory.getOutputControlPanel(id);
		view.setPresenter(this);

		// Listen for StringValueChangeEvents from plotImages
		eventBus.addHandler(StringValueChangeEvent.TYPE,
				new StringValueChangeEvent.Handler() {
					@Override
					public void onValueChange(StringValueChangeEvent event) {
						Object source = event.getSource();
						if ((source != null) && (source instanceof HasName)) {
							HasName namedSource = (HasName) source;
							if (view.getName().equalsIgnoreCase(
									namedSource.getName())) {
								String imageURL = event.getValue();
								view.setImageURL(imageURL);
								view.getDisplayButton().setEnabled(
										imageURL != null);
							}
						}
					}
				});

		// Listen for ControlVisibilityEvents
		Handler controlVisibilityEventHandler = new ControlVisibilityEvent.Handler() {
			@Override
			public void onVisibilityUpdate(ControlVisibilityEvent event) {
				view.setVisible(event.isVisible());
			}
		};
		eventBus.addHandler(ControlVisibilityEvent.TYPE,
				controlVisibilityEventHandler);

		// Listen for changes in app's comparison mode.
		eventBus.addHandler(ComparisonModeChangeEvent.TYPE,
				new ComparisonModeChangeEvent.Handler() {
					@Override
					public void onComparisonModeChange(
							ComparisonModeChangeEvent event) {
						PushButton displayButton = view.getDisplayButton();
						displayButton.setVisible(event.isComparing());
					}
				});

		return view;
	}

	@Override
	public void onDisplayButtonClick(String imageURL) {
		if (imageURL != null) {
			String features = "top=100,left=100,toolbar=1,location=1,directories=1,status=1,menubar=1,scrollbars=1,resizable=1";
			Window.open(imageURL, "_blank", features);
		}
	}

	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		this.eventBus = eventBus;
		OutputControlPanel view = init(name);

		containerWidget.setWidget(view.asWidget());
	}

}
