package gov.noaa.pmel.tmap.las.client.laswidget;

//import gov.noaa.pmel.tmap.las.client.ClientFactory;

//import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * Default implementation of {@link VariableMetadataView}.
 */
public class VariableMetadataViewImpl extends HorizontalPanel implements VariableMetadataView {
    private TextBox breadcrumbs;
    private String dsid;
    private boolean isOnComparePanel;
    private Presenter listener;
    private String name;

    private SelectionHandler<TreeItem> treeItemSelectionEventHandler;

    private VariableMetadataViewImpl() {
        this(null);
    }

    public VariableMetadataViewImpl(String id) {
        setName(id);

        PushButton infoButton = new PushButton("?");
        infoButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                listener.openInfo(dsid);
            }
        });
        Image infoImage = new Image("images/info.png");
        infoImage.setAltText("?");
        infoButton.getUpFace().setImage(infoImage);
        infoButton.getUpHoveringFace().setImage(infoImage);
        infoButton.getDownFace().setImage(infoImage);
        infoButton.getDownHoveringFace().setImage(infoImage);
        infoButton.setSize("18px", "18px");
        infoButton.setTitle("Opens meta data page for this data set");
        add(infoButton);

        breadcrumbs = new TextBox();
        breadcrumbs.setStyleName("gwt-Label");
        breadcrumbs.setReadOnly(true);
        final VariableMetadataView view = this;
        breadcrumbs.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                listener.onBreadcrumbValueChange(event, view);
            }
        });
        breadcrumbs.setVisibleLength(40);
        breadcrumbs.setVisible(false);
        add(breadcrumbs);
    }

    /**
     * @return the breadcrumbs Label
     */
    @Override
    public TextBox getBreadcrumbs() {
        return breadcrumbs;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * @return the {@link SelectionHandler}<{@link TreeItem}> Handler
     */
    @Override
    public SelectionHandler<TreeItem> getSelectionHandler() {
        return treeItemSelectionEventHandler;
    }

    /**
     * @return the isOnComparePanel
     */
    @Override
    public boolean isOnComparePanel() {
        return isOnComparePanel;
    }

    @Override
    public void setDSID(String dsid) {
        this.dsid = dsid;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    // TODO: Is this method still needed?
    @Override
    public void setOnComparePanel(boolean isOnComparePanel) {
        this.isOnComparePanel = isOnComparePanel;
    }

    @Override
    public void setPresenter(Presenter listener) {
        this.listener = listener;
    }

    @Override
    public void setSelectionHandler(SelectionHandler<TreeItem> treeItemSelectionEventHandler) {
        this.treeItemSelectionEventHandler = treeItemSelectionEventHandler;
    }

}
