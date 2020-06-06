package gov.noaa.pmel.tmap.las.client.laswidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implementation of {@link ActionsMenuBar}.
 */
public class ActionsMenuBarImpl extends Composite implements ActionsMenuBar, HasName {

    interface Binder extends UiBinder<Widget, ActionsMenuBarImpl> {
    }

    private static final Binder binder = GWT.create(Binder.class);

    @UiField
    MenuItem actionsMenu;

    @UiField
    MenuBar actionsMenuBar;

    @UiField
    MenuBar actionsSubMenuBar;

    @UiField
    MenuItem animateItem;

    @UiField
    MenuItem displayItem;

    @UiField
    MenuItem exportOtherItem;

    @UiField
    MenuItem googleEarthItem;

    private Presenter listener;

    private String name;

//    @UiField
//    MenuItem optionalCalcsItem;

    @UiField
    MenuItem saveAsItem;

    @UiField
    MenuItem showValuesItem;
    
    @UiField
    MenuItem updateItem;

    private ActionsMenuBarImpl() {
        this(null);
    }

    public ActionsMenuBarImpl(String id) {
        setName(id);
        initWidget(binder.createAndBindUi(this));
    }

    /**
     * @return the animateItem
     */
    @Override
    public MenuItem getAnimateItem() {
        return animateItem;
    }

    /**
     * @return the displayItem
     */
    @Override
    public MenuItem getDisplayItem() {
        return displayItem;
    }

    /**
     * @return the exportToItem
     */
    @Override
    public MenuItem getExportOtherItem() {
        return exportOtherItem;
    }

    /**
     * @return the googleEarthItem
     */
    @Override
    public MenuItem getGoogleEarthItem() {
        return googleEarthItem;
    }

    /**
     * @return the name that should match the {@link OutputPanel}'s ID this
     *         {@link Widget} is associated with.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @return the optionalCalcsItem
     */
//    @Override
//    public MenuItem getOptionalCalcsItem() {
//        return optionalCalcsItem;
//    }

    /**
     * @return the saveAsItem
     */
    @Override
    public MenuItem getSaveAsItem() {
        return saveAsItem;
    }

    /**
     * @return the showValuesItem
     */
    @Override
    public MenuItem getShowValuesItem() {
        return showValuesItem;
    }

    /**
     * @return the updateItem
     */
    @Override
    public MenuItem getUpdateItem() {
        return updateItem;
    }

    /**
     * Sets the ID that should match the {@link OutputPanel}'s ID this
     * {@link Widget} is associated with.
     * 
     * @see gov.noaa.pmel.tmap.las.client.laswidget.ActionsMenuBar#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setPresenter(Presenter listener) {
        this.listener = listener;
    }
}
