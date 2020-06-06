package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * View base interface.
 * Extends IsWidget so a view impl can easily provide its container widget.
 */
public interface VariableControlsOldAndComplicated extends IsWidget {
  
	public interface Presenter {

        void update(VariableControlsOldAndComplicated variableControls);
		/**
		 * Navigate to a new Place in the browser.
		 */
//		void goTo(Place place);
	}

	MultiVariableSelector getMultiVariableSelector();

    VariableSerializable getVariable();

//    VariableMetadataView getVariableMetadataView();

    void setName(String helloName);

    void setPresenter(Presenter listener);

    void setVariable(VariableSerializable variable);
}
