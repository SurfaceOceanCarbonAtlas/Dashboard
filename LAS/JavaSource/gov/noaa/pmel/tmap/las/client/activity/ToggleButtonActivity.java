package gov.noaa.pmel.tmap.las.client.activity;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.laswidget.ToggleButton;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * Activities are started and stopped by an ActivityManager associated with a
 * container Widget.
 */
public class ToggleButtonActivity extends AbstractActivity implements
		ToggleButton.Presenter {
	private ClientFactory clientFactory;

	/**
	 * 
	 */
	public ToggleButtonActivity() {
		super();
		clientFactory = GWT.create(ClientFactory.class);
	}

	/**
	 * 
	 */
	public ToggleButtonActivity(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	/**
	 * @return view
	 */
	public ToggleButton init() {
		ToggleButton view = clientFactory.getToggleButton("", "", null);
		view.setPresenter(this);
		return view;
	}

	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		ToggleButton view = init();
		containerWidget.setWidget(view.asWidget());
	}

	@Override
	public String mayStop() {
		return "Please hold on. This activity is stopping.";
	}

	/**
	 * Called just after the "up" state button was clicked and the state has
	 * been toggled and before added handlers are used to fire ClickEvents.
	 * 
	 * @see gov.noaa.pmel.tmap.las.client.laswidget.ToggleButton.Presenter#pshbtnUpOnClick(com.google.gwt.event.dom.client.ClickEvent,
	 *      gov.noaa.pmel.tmap.las.client.laswidget.ToggleButton)
	 */
	@Override
	public void pshbtnUpOnClick(ClickEvent event, ToggleButton view) {
	}

	/**
	 * Called just after the "down" state button was clicked and the state has
	 * been toggled and before added handlers are used to fire ClickEvents.
	 * 
	 * @see gov.noaa.pmel.tmap.las.client.laswidget.ToggleButton.Presenter#pshbtnDownOnClick(com.google.gwt.event.dom.client.ClickEvent,
	 *      gov.noaa.pmel.tmap.las.client.laswidget.ToggleButton)
	 */
	@Override
	public void pshbtnDownOnClick(ClickEvent event, ToggleButton view) {
	}
}
