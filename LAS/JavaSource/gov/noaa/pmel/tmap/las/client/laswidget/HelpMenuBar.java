package gov.noaa.pmel.tmap.las.client.laswidget;

//import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * View base interface. Extends IsWidget so a view impl can easily provide its
 * container widget.
 */
public interface HelpMenuBar extends IsWidget {

    public interface Presenter {
        /**
         * Navigate to a new Place in the browser.
         */
        // void goTo(Place place);
    }

    MenuItem getAboutItem();

    MenuItem getOnlineDocsItem();

    MenuItem getVideoTutorialsItem();

    void setName(String helloName);

    void setPresenter(Presenter listener);

    void setSize(String width, String height);
}
