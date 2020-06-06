package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;

import java.util.Vector;

import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * View base interface. Extends IsWidget so a view impl can easily provide its
 * container widget.
 */
public interface MultiVariableSelector extends IsWidget {

    public interface Presenter {
    }

    String getName();

    SelectionHandler<TreeItem> getSelectionHandler();

    Vector<VariableSerializable> getVariables();

    void setName(String id);

    void setPresenter(Presenter listener);

    void setSelectionHandler(SelectionHandler<TreeItem> treeItemSelectionEventHandler);

    void setVariables(Vector<VariableSerializable> variables, int selected);
    
    VariableSelector getVariableSelector();
    
}
