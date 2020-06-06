package gov.noaa.pmel.tmap.las.client.activity;

//import gov.noaa.pmel.tmap.las.client.laswidget.%placeName%;
import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.laswidget.ActionsMenuBar;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
//import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * Activities are started and stopped by an ActivityManager associated with a
 * container Widget.
 */
public class ActionsMenuBarActivity extends AbstractActivity implements ActionsMenuBar.Presenter {

    private ClientFactory clientFactory;
    private String name;
    private EventBus eventBus;

    private ActionsMenuBarActivity() {
        clientFactory = GWT.create(ClientFactory.class);
    }

    public ActionsMenuBarActivity(ClientFactory clientFactory, String id) {
        this.clientFactory = clientFactory;
        this.name = id;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        this.eventBus = eventBus;
        ActionsMenuBar view = init(name);

        containerWidget.setWidget(view.asWidget());
    }

    /**
     * @return an initialized ActionsMenuBar
     */
    public ActionsMenuBar init(String id) {
        if ( eventBus == null )
            eventBus = clientFactory.getEventBus();
        ActionsMenuBar view = clientFactory.getActionsMenuBar(id);
        view.setPresenter(this);
        // Make menu commands
        view.getSaveAsItem().setCommand(new Command() {
            @Override
            public void execute() {
                // TODO Auto-generated method stub
                // Window.open("http://ferret.pmel.noaa.gov/LAS/home/documentation/introduction/using-the-las-user-interface/",
                // "_blank", "");

            }
        });
        return view;
    }

}
