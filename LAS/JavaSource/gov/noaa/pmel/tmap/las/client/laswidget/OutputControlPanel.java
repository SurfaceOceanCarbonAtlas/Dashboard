package gov.noaa.pmel.tmap.las.client.laswidget;

import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * View base interface for control panels for {@link OutputPanel}s (plots and
 * charts). Extends IsWidget so a view impl can easily provide its container
 * widget.
 */
public interface OutputControlPanel extends IsWidget, HasName {

    public interface Presenter {

        void onDisplayButtonClick(String imageURL);

        /**
         * Navigate to a new Place in the browser.
         */
        // void goTo(Place place);
    }

    CellFormatter getCellFormatter();

    DatasetButton getDatasetButton();

    public PushButton getDisplayButton();

    @Override
    public String getName();

    public VariableControlsOldAndComplicated getVariableControls();

    public Widget getWidget(int row, int column);

    void remove(Widget widget);

    void removeCell(int row, int col);

    void setImageURL(String imageURL);

    @Override
    void setName(String helloName);

    void setPresenter(Presenter listener);

    void setStyleName(String style);

    void setVisible(boolean visible);

    void setWidget(int row, int column, Widget widget);
}
