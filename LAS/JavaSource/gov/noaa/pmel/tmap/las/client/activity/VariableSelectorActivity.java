package gov.noaa.pmel.tmap.las.client.activity;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.event.ComparisonModeChangeEvent;
import gov.noaa.pmel.tmap.las.client.event.VariablePluralityEvent;
import gov.noaa.pmel.tmap.las.client.laswidget.UserListBox;
import gov.noaa.pmel.tmap.las.client.laswidget.VariableSelector;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * Activities are started and stopped by an ActivityManager associated with a
 * container Widget.
 */
public class VariableSelectorActivity extends AbstractActivity implements VariableSelector.Presenter {

    private ClientFactory clientFactory;
    private EventBus eventBus;
    private String name;

    public VariableSelectorActivity() {
        clientFactory = GWT.create(ClientFactory.class);
        eventBus = clientFactory.getEventBus();
    }

    public VariableSelectorActivity(ClientFactory clientFactory, String id) {
        this.name = id;
        this.clientFactory = clientFactory;
    }

    /**
     * @return VariableSelector view
     */
    @Override
    public VariableSelector init(String id) {
        if ( eventBus == null )
            eventBus = clientFactory.getEventBus();
        final VariableSelector view = clientFactory.getVariableSelectorView(id);
        view.setPresenter(this);
        // Listen for changes in app's comparison mode.
        eventBus.addHandler(ComparisonModeChangeEvent.TYPE, new ComparisonModeChangeEvent.Handler() {
            @Override
            public void onComparisonModeChange(ComparisonModeChangeEvent event) {
                view.setComparing(event.isComparing());
                if ( view.isComparing() ) {
                    // If comparing use only one variable and hide the
                    // latest add button.
                    view.removeListBoxesExceptFirst();
                    UserListBox latestListBox = view.getLatestListBox();
                    if ( latestListBox != null ) {
                        latestListBox.setAddButtonVisible(false);
                    }
                } else {
                    // If not comparing, show the latest add button if there
                    // are more variables in the current data set than list
                    // boxes.
                    try {
                        UserListBox latestListBox = view.getLatestListBox();
                        if ( latestListBox != null ) {
                            latestListBox.setAddButtonVisible(latestListBox.getVariables().size() > view.getItemCount());
                        }
                    } catch ( Exception e ) {
                        GWT.log(e.getLocalizedMessage(), e);
                    }
                }
            }
        });
        VariableMetadataActivity variableMetadataPresenter = new VariableMetadataActivity(clientFactory, id);
        view.setVariableMetadataView(variableMetadataPresenter.init());
        return view;
    }

    @Override
    public void itemCountUpdated(int oldItemCount, int newItemCount, VariableSelector view) {
        if ( oldItemCount != newItemCount ) {
            if ( (oldItemCount < 2) && (newItemCount > 1) ) {
                // Fire an event to inform listeners that multiple variables
                // are being used on this OutputPanel
                eventBus.fireEventFromSource(new VariablePluralityEvent(true), view);
            }
            if ( (oldItemCount > 1) && (newItemCount < 2) ) {
                // Fire an event to inform listeners that multiple
                // variables are NO LONGER being used on this OutputPanel
                // itemCount better be 1.
                eventBus.fireEventFromSource(new VariablePluralityEvent(false), view);
            }
        }
    }

    @Override
    public String mayStop() {
        return "Please hold on. This activity is stopping.";
    }

    @Override
    public void onAddButtonClick(ClickEvent event, UserListBox source, VariableSelector view) {
            addUserListBox(source, view);          
    }
    
    @Override
    public UserListBox addUserListBox(UserListBox source, VariableSelector view) {
        List<VariableSerializable> variables = source.getVariables();
        String sourceName = source.getName();
        UserListBox newListBox = view.initUserListBox(sourceName, false);
        newListBox.setVariables(variables);
        // Avoid giving the user the ability to add more variableListBoxes
        // than there are variables in the current data set.
        int itemCount = view.getItemCount();
        int size = variables.size();
        newListBox.setAddButtonVisible(size > (itemCount + 1));
        newListBox.setAddButtonEnabled(false);
        newListBox.setRemoveButtonVisible(itemCount > 0);
        newListBox.setSelectedIndex(-1);
        view.addListBox(newListBox);
        return newListBox;
    }
    @Override
    public void onChange(ChangeEvent event, UserListBox newListBox) {
        eventBus.fireEventFromSource(event, newListBox);
    }

    @Override
    public void onChange(ChangeEvent event, VariableSelector view) {
        eventBus.fireEventFromSource(new VariablePluralityEvent(view.getItemCount() > 1), view);
    }

    @Override
    public void onRemoveButtonClick(ClickEvent event, UserListBox source, VariableSelector view) {
        view.removeListBox(source);
        // TODO: Remove related variables from the view's OutputPanel
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        this.eventBus = eventBus;
        VariableSelector view = init(name);
        containerWidget.setWidget(view.asWidget());
    }

}
