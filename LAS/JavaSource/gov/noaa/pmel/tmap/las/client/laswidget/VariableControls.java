package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.event.ComparisonModeChangeEvent;
import gov.noaa.pmel.tmap.las.client.event.RemoveVariableEvent;
import gov.noaa.pmel.tmap.las.client.event.VariablePluralityEvent;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class VariableControls extends Composite {

    ClientFactory clientFactory = GWT.create(ClientFactory.class);
    EventBus eventBus = clientFactory.getEventBus();
    
    String id;
    
    FlowPanel layout = new FlowPanel();
    
    public VariableControls(String id) {
        this.id = id;
        eventBus.addHandler(VariablePluralityEvent.TYPE, vpEventHandler);
        eventBus.addHandler(RemoveVariableEvent.TYPE, rmVarEventHandler);
        eventBus.addHandler(ComparisonModeChangeEvent.TYPE, compareEventHandler);
        initWidget(layout);
    }
    private void setAddRemove(boolean fire) {
        if ( layout.getWidgetCount() > 1 ) {
            UserListBox last = getLatestListBox();
            last.setAddButtonEnabled(true);
            last.setAddButtonVisible(true);
            last.setRemoveButtonVisible(true);
            // Notify the rest of the app we're still using multiiple variables (perhaps unnecessarily).
            if ( fire) eventBus.fireEventFromSource(new VariablePluralityEvent(true), VariableControls.this);
        } else {
            UserListBox last = getLatestListBox();
            if ( last.getVariables().size() > 1) {
                last.setAddButtonEnabled(true);
                last.setAddButtonVisible(true);
            } else {
                last.setAddButtonEnabled(false);
                last.setAddButtonVisible(false);
            }
            last.addVectors();

            // Notify the rest of the app we're down to one variable.
            if ( fire ) eventBus.fireEventFromSource(new VariablePluralityEvent(false), VariableControls.this);
        }
    }
    private RemoveVariableEvent.Handler rmVarEventHandler = new RemoveVariableEvent.Handler() {
        
        @Override
        public void onRemove(RemoveVariableEvent event) {
            Object source = event.getSource();
            if ( source instanceof UserListBox ) {
                UserListBox rm = (UserListBox) source;
                layout.remove(rm);
                setAddRemove(true);
                
            }
        }
    };
    private ComparisonModeChangeEvent.Handler compareEventHandler = new ComparisonModeChangeEvent.Handler() {

        @Override
        public void onComparisonModeChange(ComparisonModeChangeEvent event) {
            
            if ( event.isComparing() ) {
                removeListBoxesExceptFirst();
                UserListBox box = getLatestListBox();
                box.setAddButtonVisible(false);
            } else {
                UserListBox box = getLatestListBox();
                box.setAddButtonEnabled(true);
                box.setAddButtonVisible(layout.getWidgetCount() < box.getVariables().size());
            }
            
        }
        
    };
    private VariablePluralityEvent.Handler vpEventHandler = new VariablePluralityEvent.Handler() {

        @Override
        public void onPluralityChange(VariablePluralityEvent event) {
            Object s = event.getSource();
            if ( s instanceof UserListBox ) {
                UserListBox source = (UserListBox) s;
                // Create a new list box based on the existing one.
                if ( layout.getWidgetCount() < source.getVariables().size() ) {
                    source.setRemoveButtonVisible(false);
                    UserListBox newlist = new UserListBox(id+"."+String.valueOf(layout.getWidgetCount()+1), false);
                    List<VariableSerializable> v = new ArrayList<VariableSerializable>();
                    v.addAll(source.getVariables());
                    newlist.setVariables(v);
                    // Remove the vectors from this one before adding it.
                    newlist.removeVectors();
                    // Now go back and remove the vectors from any existing boxes, and resetting the selected ID
                    for (int i = 0; i < layout.getWidgetCount(); i++ ) {
                        UserListBox box = (UserListBox) layout.getWidget(i);
                        int selected = box.getSelectedIndex();
                        VariableSerializable var = box.getVariables().get(selected);
                        box.removeVectors();
                        for ( int j = 0; j < box.getItemCount(); j++) {
                            VariableSerializable ov = box.getVariables().get(j);
                            if ( var.getID().equals(ov.getID()) && var.getDSID().equals(ov.getDSID()) ) {
                                box.setSelectedIndex(j);
                                int ni = (j+1)%(box.getItemCount());
                                newlist.setSelectedIndex(ni);
                            }
                        }
                    }
                    layout.add(newlist);
                    if ( layout.getWidgetCount() > 1 ) {
                        newlist.setRemoveButtonVisible(true);
                        eventBus.fireEventFromSource(new VariablePluralityEvent(true), VariableControls.this);
                    } else {
                        eventBus.fireEventFromSource(new VariablePluralityEvent(false), VariableControls.this);
                    }
                    
                }
            }
        }
        
    };
    public void setVariables(List<VariableSerializable> variables) {
        layout.clear();
//        this.variables = variables;
        UserListBox list = new UserListBox(id, true);
        list.setVariables(variables);
        layout.add(list);
        setAddRemove(false);
    }
    public void setVariables(List<VariableSerializable> variables, VariableSerializable var) {
        setVariables(variables);
        UserListBox list = (UserListBox) layout.getWidget(layout.getWidgetCount() - 1);
        list.setVariable(var);
    }
    public void addUserListBox(List<VariableSerializable> variables, int index) {
        int next = layout.getWidgetCount() + 1;
        UserListBox list = new UserListBox(id+"."+next, false);
        list.setVariables(variables);
        list.setSelectedIndex(index);
        layout.add(list);
    }
    public void setMinMaxNumberOfVariables(int min, int max) {
        int size = layout.getWidgetCount();
        if ( min == 1 && max == 1 ) {
            for (int i = layout.getWidgetCount() - 1; i >= 1 ; i--) {
                layout.remove(i);
            }
        } else {
            if ( size < min ) {
                Window.alert("This operation requires more variables.  Use the + button on the variables drop down to add variables.");
            }
            if ( size > max ) {
                for (int i = size - 1; i <= max ; i--) {
                    layout.remove(i);
                }
            }
        }
        setAddRemove(false);
    }

    public List<UserListBox> getListBoxes() {
        List<UserListBox> boxes = new ArrayList<UserListBox>();
        for ( int i = 0; i < layout.getWidgetCount(); i++ ) {
            UserListBox box = (UserListBox) layout.getWidget(i);
            boxes.add(box);
        }
        return boxes;
    }

    public void removeListBoxesExceptFirst() {
        for ( int i = 1; i < layout.getWidgetCount(); i++ ) {
            layout.remove(i);
        }
        
    }
    public void setVariable(VariableSerializable variable) {
        if ( layout.getWidgetCount() > 0 ) {
            UserListBox box = (UserListBox) layout.getWidget(0);
            box.setVariable(variable);
        }
    }
    public UserListBox getFirstListBox() {
        if ( layout.getWidgetCount() > 0 ) {
            return (UserListBox) layout.getWidget(0);
        } else {
            return null;
        }
    }
    public UserListBox getLatestListBox() {
        if ( layout.getWidgetCount() > 0 ) {
            return (UserListBox) layout.getWidget(layout.getWidgetCount() - 1);
        } else {
            return null;
        }
    }
//    public List<VariableSerializable> getVariables() {
//       return variables;
//        
//    }
}
