
package gov.noaa.pmel.tmap.las.client;

//import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

/**
 * View base interface.
 * Extends IsWidget so a view impl can easily provide its container widget.
 */
public interface InteractiveDownloadDataView extends IsWidget {
  
	public interface Presenter {

		/**
		 * Navigate to a new Place in the browser.
		 */
//		void goTo(Place place);

		void saveAction();
		void updateUI();
	}

	FlowPanel getAxisPanel();

	ListBox getDataFormatComboBox();

	FlowPanel getDateTimePanel();

	Button getSaveButton();

	Label getSelectedRegionLatitude();

	Label getSelectedRegionLongitude();

	void setName(String helloName);

	void setPresenter(Presenter listener);

}
