package gov.noaa.pmel.tmap.las.client.laswidget;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ToggleButton;

/**
 * View base interface.
 * Extends IsWidget so a view impl can easily provide its container widget.
 */
public interface LASAnnotationsButtonPanel extends IsWidget {
  
	public interface Presenter {
		/**
		 * Navigate to a new Place in the browser.
		 */
		//void goTo(Place place);
	}

	void addClickHandler(ClickHandler clickHandler);

	void hide();

    void setAnnotations(String xml);

    void setAnnotationsHTML(String html);

    void setAnnotationsHTMLURL(String url);

    void setButtonDown(boolean down);

    void setError(String html);

    void setName(String helloName);

    void setPopupWidth(String width);

    void setPresenter(Presenter listener);

    void setTitle(String title);
}
