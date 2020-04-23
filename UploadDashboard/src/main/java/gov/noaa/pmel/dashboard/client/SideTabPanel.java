package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;

import java.util.ArrayList;

public class SideTabPanel extends ResizeComposite {
    interface SidebarPanelUiBinder extends UiBinder<DockLayoutPanel,SideTabPanel> {
    }

    private static final SidebarPanelUiBinder sidebarPanelUiBinder = GWT.create(SidebarPanelUiBinder.class);

    @UiField
    Button addButton;
    @UiField
    Button removeButton;
    @UiField
    ListBox valsList;
    @UiField
    FlowPanel hiddenPanel;
    @UiField
    ScrollPanel mainPanel;

    private final ArrayList<Composite> panelsList;
    private final ArrayList<TextBox> headersList;
    private final ArrayList<HandlerRegistration> registrationsList;
    private int selectedIndex;

    /**
     * A DockLayoutPanel with only a west column and a center area.  The west column contains an add {@link Button},
     * a remove {@link Button}, and a {@link ListBox} showing headers of available panels to display.  The center
     * area contains a {@link ScrollPanel} for displaying the currently selected panel.  Panels are made available
     * for display by calling {@link #addPanel(Composite, TextBox)} with the panel to display and a TextBox
     * (never displayed) that acts only to provide updatable String values for the selection list.
     * <p>
     * No actions or labels are applied to the add or remove button.  After creating this panel, the
     * {@link #getAddButton()} and {@link #getRemoveButton()} methods can be used to access these buttons to
     * assign labels and event handlers.
     */
    public SideTabPanel() {
        initWidget(sidebarPanelUiBinder.createAndBindUi(this));

        panelsList = new ArrayList<Composite>();
        headersList = new ArrayList<TextBox>();
        registrationsList = new ArrayList<HandlerRegistration>();
        selectedIndex = -1;
    }

    /**
     * @return the "add" button shown at the top of the west column
     */
    public Button getAddButton() {
        return addButton;
    }

    /**
     * @return the "remove" button shown just below the "add" button at the top of the west column
     */
    public Button getRemoveButton() {
        return removeButton;
    }

    /**
     * Remove all panels and entries in the selection list.
     */
    public void clear() {
        for (HandlerRegistration reg : registrationsList) {
            reg.removeHandler();
        }
        registrationsList.clear();
        headersList.clear();
        panelsList.clear();
        valsList.clear();
        hiddenPanel.clear();
        mainPanel.clear();
        selectedIndex = -1;
    }

    /**
     * Adds a panel and then displays it.
     *
     * @param panel
     *         panel to add to the center scroll panel
     * @param header
     *         text box used for providing the selection list text for this panel
     */
    public void addPanel(Composite panel, TextBox header) {
        selectedIndex = panelsList.size();
        panelsList.add(panel);
        mainPanel.setWidget(panel);
        // Add the header TextBox as an invisible element just to make sure it is in the event chain
        header.setVisible(false);
        hiddenPanel.add(header);
        headersList.add(header);
        valsList.addItem(header.getText());
        valsList.setSelectedIndex(selectedIndex);
        registrationsList.add(header.addValueChangeHandler(
                new ValueChangeHandler<String>() {
                    @Override
                    public void onValueChange(ValueChangeEvent event) {
                        updateValsList();
                    }
                }
        ));
    }

    /**
     * Removes the panel and selection list entry associated with the given index.
     * If the panel removed was currently being displayed, the next panel in the list,
     * or the last panel if there is no next panel, is shown
     *
     * @param index
     *         remove the panel at this index in the list; does nothing if an invalid value
     */
    public void removePanel(int index) {
        if ( (index < 0) || (index >= panelsList.size()) )
            return;
        HandlerRegistration reg = registrationsList.remove(index);
        reg.removeHandler();
        TextBox header = headersList.remove(index);
        hiddenPanel.remove(header);
        panelsList.remove(index);
        valsList.removeItem(index);
        if ( selectedIndex == index ) {
            if ( selectedIndex == panelsList.size() ) {
                selectedIndex--;
            }
            if ( selectedIndex >= 0 )
                mainPanel.setWidget(panelsList.get(selectedIndex));
            else
                mainPanel.clear();
        }
        else if ( index < selectedIndex )
            selectedIndex--;
    }

    /**
     * @param index
     *         show the panel associated with the given index; does nothing if an invalid value
     *         or if the indicated panel is already being displayed.
     */
    public void showSelectedIndex(int index) {
        if ( (index < 0) || (index == selectedIndex) || (index >= panelsList.size()) )
            return;
        selectedIndex = index;
        mainPanel.setWidget(panelsList.get(index));
        valsList.setSelectedIndex(index);
    }

    /**
     * @return the index of the currently selected/displayed panel, or
     *         -1 if no panel is being selected/being displayed
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    @UiHandler("valsList")
    void valsListOnChange(ChangeEvent event) {
        showSelectedIndex(valsList.getSelectedIndex());
    }

    /**
     * Updates the text items in the selection list with the values, if different, given in the associated text boxes.
     */
    private void updateValsList() {
        for (int k = 0; k < headersList.size(); k++) {
            String text = headersList.get(k).getText();
            String value = valsList.getItemText(k);
            if ( !value.equals(text) )
                valsList.setItemText(k, text);
        }
    }

}
