package gov.noaa.pmel.tmap.las.client.activity;

//import gov.noaa.pmel.tmap.las.client.laswidget.%placeName%;
import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.ui.AutoCloseMenuBar;
import gov.noaa.pmel.tmap.las.client.util.Util;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Activities are started and stopped by an ActivityManager associated with a
 * container Widget.
 */
public class HelpMenuBar extends Composite {

    MenuItem aboutItem;
    MenuItem helpMenu;
    MenuItem onlineDocsItem;
    MenuItem videoTutorialsItem;
 
    AutoCloseMenuBar helpMenuBar;
    MenuItemSeparator separator;
   
    MenuBar helpSubMenuBar;
	public HelpMenuBar() {
	    
		
		helpMenuBar = new AutoCloseMenuBar(false);
    	helpMenuBar.setSize("100%", "100%");
    	helpMenuBar.setAnimationEnabled(true);
    	
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
		
      initWidget(helpMenuBar);

        // Make menu commands
        Command aboutCmd = new Command() {
            @Override
            public void execute() {
                Window.open(Util.getProductServer(), "_blank", "scrollbars=1");
            }
        };
        aboutItem.setCommand(aboutCmd);

        Command tutorialsCmd = new Command() {
            @Override
            public void execute() {
                Window.open("https://ferret.pmel.noaa.gov/LAS/documentation/introduction/using-the-las-user-interface#videos", "_blank", "scrollbars=1");
            }
        };
        videoTutorialsItem.setCommand(tutorialsCmd);

        Command docsCmd = new Command() {
            @Override
            public void execute() {
                Window.open("https://ferret.pmel.noaa.gov/LAS/documentation/end-user-documentation", "_blank", "scrollbars=1");
            }
        };
        onlineDocsItem.setCommand(docsCmd);
        onlineDocsItem.setSize("100%", "100%");
        
	
	}
	
	

}
