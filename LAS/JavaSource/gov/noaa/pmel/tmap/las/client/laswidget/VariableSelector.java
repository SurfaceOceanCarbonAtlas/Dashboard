package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;

import java.util.Vector;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View base interface. Extends IsWidget so a view impl can easily provide its
 * container widget.
 */
public interface VariableSelector extends IsWidget, HasName {

    public interface Presenter {

        UserListBox addUserListBox(UserListBox source, VariableSelector view);
        
        VariableSelector init(String id);
        
        void itemCountUpdated(int oldItemCount, int newItemCount, VariableSelector view);

        void onAddButtonClick(ClickEvent event, UserListBox source, VariableSelector view);

        void onChange(ChangeEvent event, UserListBox newListBox);

        void onChange(ChangeEvent event, VariableSelector view);

        void onRemoveButtonClick(ClickEvent event, UserListBox source, VariableSelector view);

    }

    void addListBox(UserListBox newListBox);

    UserListBox addUserListBox(UserListBox source, VariableSelector view);
    
    UserListBox getFirstListBox();

    int getItemCount();

    UserListBox getLatestListBox();

    Vector<UserListBox> getListBoxes();

    UserListBox initUserListBox(String id, boolean addChangeHandler);

    boolean isComparing();

    void removeExtraListBoxes(boolean update_count);

    void removeListBox(UserListBox oldListBox);

    void removeListBoxesExceptFirst();
    
    void setComparing(boolean comparing);

    void setPresenter(Presenter listener);

    void setVariableMetadataView(VariableMetadataView variableMetadataView);

	void setVariables(Vector<VariableSerializable> variables, int selected);

}
