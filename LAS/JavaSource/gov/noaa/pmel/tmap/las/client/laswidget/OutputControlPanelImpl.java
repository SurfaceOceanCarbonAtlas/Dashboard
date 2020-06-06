package gov.noaa.pmel.tmap.las.client.laswidget;

import java.util.Vector;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.activity.VariableControlsActivity;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

/**
 * Default implementation of {@link OutputControlPanel}s which are control
 * panels for {@link OutputPanel}s (plots and charts).
 */
public class OutputControlPanelImpl extends Composite implements OutputControlPanel {

    // interface Binder extends UiBinder<Widget, OutputControlPanelImpl> {
    // }

    // private static final Binder binder = GWT.create(Binder.class);

    private DatasetButton datasetButton;
    private PushButton displayButton;
    private FlexTable flexTable;
    private String imageURL;
    private Presenter listener;
    String name;
    private VariableControlsOldAndComplicated variableControls;
    private Label opcLabel;

    // Private to prevent the instantiation without a name/ID
    private OutputControlPanelImpl() {
        this(null);
    }

    /**
     * @wbp.parser.constructor
     */
    public OutputControlPanelImpl(String outputPanelID) {
        ClientFactory clientFactory = GWT.create(ClientFactory.class);

        setName(outputPanelID);

        FlowPanel mainPanel = new FlowPanel();

        HorizontalPanel horizontalPanel_0 = new HorizontalPanel();
        horizontalPanel_0.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        horizontalPanel_0.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        horizontalPanel_0.setSpacing(5);
        mainPanel.add(horizontalPanel_0);

        datasetButton = new DatasetButton();
        datasetButton.setName(outputPanelID);
        horizontalPanel_0.add(datasetButton);

        displayButton = new PushButton("Enlarge Image");
        displayButton.setTitle("Show the plot's image at full size in a new window.");
        displayButton.addStyleDependentName("SMALLER");
        displayButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                listener.onDisplayButtonClick(getImageURL());
            }
        });
        displayButton.setEnabled(false);
        horizontalPanel_0.add(displayButton);

        flexTable = new FlexTable();
        VariableControlsActivity variableControlsPresenter = new VariableControlsActivity(clientFactory, outputPanelID);
        variableControls = variableControlsPresenter.init(outputPanelID);
        // horizontalPanel_0.add(variableControls);
        flexTable.setWidget(0, 0, variableControls);

        mainPanel.add(flexTable);

        mainPanel.setVisible(true);
        // mainPanel.setOpen(true);

        initWidget(mainPanel);
        // initWidget(binder.createAndBindUi(this));
    }

    @Override
    public CellFormatter getCellFormatter() {
        return flexTable.getCellFormatter();
    }

    @Override
    public DatasetButton getDatasetButton() {
        return datasetButton;
    }

    /**
     * @return the displayButton
     */
    @Override
    public PushButton getDisplayButton() {
        return displayButton;
    }

    /**
     * @return the imageURL
     */
    public String getImageURL() {
        return imageURL;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public VariableControlsOldAndComplicated getVariableControls() {
        return variableControls;
    }

    @Override
    public Widget getWidget(int row, int column) {
        return flexTable.getWidget(row, column);
    }

    @Override
    public void remove(Widget widget) {
        flexTable.remove(widget);
    }

    @Override
    public void removeCell(int row, int col) {
        flexTable.removeCell(row, col);
    }

    /**
     * @param displayButton
     *            the displayButton to set
     */
    public void setDisplayButton(PushButton displayButton) {
        this.displayButton = displayButton;
    }

    /**
     * @param imageURL
     *            the imageURL to set
     */
    @Override
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setPresenter(Presenter listener) {
        this.listener = listener;
    }

    @Override
    public void setWidget(int row, int column, Widget widget) {
        flexTable.setWidget(row, column, widget);
    }
//    @Override
//    public void setVisible(boolean visible) {
//        // This is kinda dumb, but we have the so many layers to try to use the "correct" MVP that we can't get stuff done.
//        Vector<VariableSerializable> vars = getVariableControls().getMultiVariableSelector().getVariables();
//        if ( vars != null && vars.size() > 0 ) {
//            if ( !vars.get(0).isDescrete() ) {
//                super.setVisible(visible);
//            } else {
//                // Only show this menu for grids.
//                super.setVisible(false);
//            }
//            
//        } else {
//            super.setVisible(visible);
//        }
//    }
}
