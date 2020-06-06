package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.ui.AutoCloseMenuBar;

import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;

/**
 * Implementation of {@link HelpMenuBar}.
 */
public class HelpMenuBarImpl extends Composite implements HelpMenuBar {

//    interface Binder extends UiBinder<Widget, HelpMenuBarImpl> {
//    }
//
//    private static final Binder binder = GWT.create(Binder.class);
    @UiField
    MenuItem aboutItem;
    
    @UiField
    MenuItem helpMenu;
    
    @UiField
    AutoCloseMenuBar helpMenuBar;
    
    @UiField
    MenuBar helpSubMenuBar;
    
    private Presenter listener;

    @UiField
    MenuItem onlineDocsItem;

    @UiField
    MenuItem videoTutorialsItem;
    
    private MenuItemSeparator separator;

    public HelpMenuBarImpl() {
    	
    	helpMenuBar = new AutoCloseMenuBar(false);
    	helpMenuBar.setSize("100%", "100%");
    	helpMenuBar.setAnimationEnabled(true);
    	initWidget(helpMenuBar);
    	helpSubMenuBar = new AutoCloseMenuBar(true);
    	helpSubMenuBar.setAnimationEnabled(true);
    	
    	helpMenu = new MenuItem("Help", false, helpSubMenuBar);
    	helpMenu.setSize("100%", "100%");
    	
    	onlineDocsItem = new MenuItem("Online Documentation", false, (Command) null);
    	helpSubMenuBar.addItem(onlineDocsItem);
    	
    	videoTutorialsItem = new MenuItem("Video Tutorials", false, (Command) null);
    	helpSubMenuBar.addItem(videoTutorialsItem);
    	
    	separator = new MenuItemSeparator();
    	helpSubMenuBar.addSeparator(separator);
    	
    	aboutItem = new MenuItem("About", false, (Command) null);
    	helpSubMenuBar.addItem(aboutItem);
    	
    	helpMenuBar.addItem(helpMenu);
//        initWidget(binder.createAndBindUi(this));
        helpMenu.ensureDebugId("helpMenu");
    }

    /**
     * @return the aboutItem
     */
    @Override
    public MenuItem getAboutItem() {
        return aboutItem;
    }

    /**
     * @return the onlineDocsItem
     */
    @Override
    public MenuItem getOnlineDocsItem() {
        return onlineDocsItem;
    }

    /**
     * @return the videoTutorialsItem
     */
    @Override
    public MenuItem getVideoTutorialsItem() {
        return videoTutorialsItem;
    }

    @Override
    public void setName(String name) {
        // button.setHTML(name);
        helpMenu.setText(name);
    }

    @Override
    public void setPresenter(Presenter listener) {
        this.listener = listener;
    }

    /**
     * @see com.google.gwt.user.client.ui.UIObject#setSize(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void setSize(String width, String height) {
        super.setSize(width, height);
    }
}
