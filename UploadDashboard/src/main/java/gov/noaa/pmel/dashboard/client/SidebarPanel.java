package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;

import java.util.ArrayList;

public class SidebarPanel extends ResizeComposite {
    interface SidebarPanelUiBinder extends UiBinder<DockLayoutPanel,SidebarPanel> {
    }

    private static final SidebarPanelUiBinder sidebarPanelUiBinder = GWT.create(SidebarPanelUiBinder.class);

    @UiField
    Button addButton;
    @UiField
    Button removeButton;
    @UiField
    ListBox valsList;
    @UiField
    ScrollPanel mainPanel;

    private final ArrayList<FlowPanel> panelsList;
    private final ArrayList<TextBox> headersList;
    private final ArrayList<HandlerRegistration> registrationsList;
    private int selectedIndex;

    public SidebarPanel() {
        initWidget(sidebarPanelUiBinder.createAndBindUi(this));

        panelsList = new ArrayList<FlowPanel>();
        headersList = new ArrayList<TextBox>();
        registrationsList = new ArrayList<HandlerRegistration>();
        selectedIndex = -1;
    }

    public void clear() {
        for (HandlerRegistration reg : registrationsList) {
            reg.removeHandler();
        }
        registrationsList.clear();
        headersList.clear();
        panelsList.clear();
        valsList.clear();
        mainPanel.clear();
        selectedIndex = -1;
    }

    public void addPanel(FlowPanel panel, TextBox header) {
        selectedIndex = panelsList.size();
        panelsList.add(panel);
        mainPanel.setWidget(panel);
        headersList.add(header);
        valsList.addItem(header.getText());
        valsList.setSelectedIndex(selectedIndex);
        registrationsList.add(header.addChangeHandler(
                new ChangeHandler() {
                    @Override
                    public void onChange(ChangeEvent event) {
                        updateValsList();
                    }
                }
        ));
    }

    private void updateValsList() {
        for (int k = 0; k < headersList.size(); k++) {
            String text = headersList.get(k).getText();
            String value = valsList.getItemText(k);
            if ( !value.equals(text) )
                valsList.setItemText(k, text);
        }
    }

    public void removePanel(int index) {
        if ( (index < 0) || (index >= panelsList.size()) )
            return;
        HandlerRegistration remHandler = registrationsList.remove(index);
        remHandler.removeHandler();
        headersList.remove(index);
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

    @UiHandler("valsList")
    void valsListOnChange(ChangeEvent event) {
        showSelectedIndex(valsList.getSelectedIndex());
    }

    public void showSelectedIndex(int index) {
        if ( (index < 0) || (index == selectedIndex) || (index >= panelsList.size()) )
            return;
        selectedIndex = index;
        mainPanel.setWidget(panelsList.get(index));
        valsList.setSelectedIndex(index);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

}