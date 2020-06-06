package gov.noaa.pmel.tmap.las.client.activity;

//import gov.noaa.pmel.tmap.las.client.laswidget.%placeName%;
import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.laswidget.LASAnnotationsButtonPanel;
import gov.noaa.pmel.tmap.las.client.laswidget.LASAnnotationsButtonPanelImpl;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * Activities are started and stopped by an ActivityManager associated with a container Widget.
 */
public class LASAnnotationsButtonPanelActivity extends AbstractActivity implements LASAnnotationsButtonPanel.Presenter {

	private String name;
    private ClientFactory clientFactory;

	// Don't allow this Activity to be constructed without a name ID set
    @SuppressWarnings("unused")
    private  LASAnnotationsButtonPanelActivity() {
//		this.name = place.getName();
	}

    public LASAnnotationsButtonPanelActivity(ClientFactory clientFactory, String id) {
        this.clientFactory = clientFactory;
        this.name = id;
    }

    @Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		LASAnnotationsButtonPanel view = init(name);
		containerWidget.setWidget(view.asWidget());
	}

    /**
     * @return view
     */
    public LASAnnotationsButtonPanel init(String id) {
        LASAnnotationsButtonPanel view = new LASAnnotationsButtonPanelImpl(id);
		view.setPresenter(this);
        return view;
    }

	@Override
	public String mayStop() {
		return "Please hold on. This activity is stopping.";
	}

	/**
	 * @see LASAnnotationsButtonPanel.Presenter#goTo(Place)
	 */
//	public void goTo(Place place) {
//	}
}
