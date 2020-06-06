package gov.noaa.pmel.tmap.las.client.laswidget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View base interface. Extends IsWidget so a view impl can easily provide its
 * container widget.
 */
public interface ToggleButton extends IsWidget {

	public interface Presenter {

		void pshbtnDownOnClick(ClickEvent event, ToggleButton clickHandler);

		void pshbtnUpOnClick(ClickEvent event, ToggleButton clickHandler);
	}

	boolean addClickHandler(ClickHandler handler);

	void addStyleDependentName(String string);

	boolean isDown();

	void onClick(ClickEvent event);

	boolean removeClickHandler(ClickHandler handler);

	void setDown(boolean b);

	void setPresenter(Presenter listener);

	void setTitle(String string);

}
