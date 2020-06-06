package gov.noaa.pmel.tmap.las.client.laswidget;

//import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * View base interface. Extends IsWidget so a view impl can easily provide its
 * container widget.
 */
public interface ActionsMenuBar extends IsWidget {

    public interface Presenter {
        /**
         * Navigate to a new Place in the browser.
         */
        // void goTo(Place place);
    }

    MenuItem getAnimateItem();

    MenuItem getDisplayItem();

    MenuItem getExportOtherItem();

    MenuItem getGoogleEarthItem();

    public String getName();

//    MenuItem getOptionalCalcsItem();

    MenuItem getSaveAsItem();

    MenuItem getShowValuesItem();

    MenuItem getUpdateItem();

    void setName(String helloName);

    void setPresenter(Presenter listener);
}
