package gov.noaa.pmel.tmap.las.client.laswidget;

import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * View base interface. Extends IsWidget so a view impl can easily provide its
 * container widget.
 */
public interface VariableMetadataView extends IsWidget {

    public interface Presenter {

        void onBreadcrumbValueChange(ValueChangeEvent<String> event, VariableMetadataView view);

        void openInfo(String dsid);

    }

    TextBox getBreadcrumbs();

    String getName();

    SelectionHandler<TreeItem> getSelectionHandler();

    boolean isOnComparePanel();

    void setDSID(String dsid);

    void setName(String helloName);

    void setOnComparePanel(boolean isOnComparePanel);

    void setPresenter(Presenter listener);

    void setSelectionHandler(SelectionHandler<TreeItem> updateBreadcrumbsTreeItemSelectionHandler);
}
