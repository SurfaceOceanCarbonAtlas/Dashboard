package gov.noaa.pmel.tmap.las.client.laswidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/*
 * Part of the simple help system for showing a bit of help text from a URL.
 *
 */

public class HelpPanel extends Composite {
	
	private int popupLeft = -999;
	private int popupTop = -999;
	private String frameHeight = "525px";
	private String frameWidth = "525px";
	// new SafeHtmlBuilder().appendEscaped("?").toSafeHtml()
	Anchor helpButton = new Anchor(true);
	Anchor closeButton = new Anchor(true);
	PopupPanel mainPanel = new PopupPanel(false);
	VerticalPanel layoutPanel = new VerticalPanel();
	
	public HelpPanel() {
	    helpButton.setHTML("<b>Help</b>");
	    closeButton.setHTML("<b>Close</b>");
	    closeButton.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                setOpen(false);
                
            }
        });
	    layoutPanel.add(closeButton);
		mainPanel.add(layoutPanel);
		helpButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if ( !mainPanel.isShowing() ) { 
                    setOpen(true);
                } else {
                    setOpen(false);
                }       
            }
		    
		});
		
		helpButton.setTitle("Help");
		initWidget(helpButton);
	}
	
	public void setHelpHtml(String html) {
		HTML help = new HTML(html);
		layoutPanel.clear();
		layoutPanel.add(closeButton);
		layoutPanel.add(help);
	}
	public void setHelpURL(String url) {
		Frame help = new Frame(GWT.getModuleBaseURL()+url);
		help.setHeight(frameHeight);
		help.setWidth(frameWidth);
		layoutPanel.clear();
		layoutPanel.add(closeButton);
		layoutPanel.add(help);
	}
	public void setOpen(boolean open) {
		if ( open ) {
			
			if ( popupTop == -999 || popupLeft == -999 ) {
				mainPanel.setPopupPosition(helpButton.getAbsoluteLeft(), helpButton.getAbsoluteTop() + 32 );
			} else {
				mainPanel.setPopupPosition(popupLeft, popupTop);
			}
			mainPanel.show();
		} else {
			
			mainPanel.hide();
		}
	}
	public void setPopupTop( int top ) {
		popupTop = top;
	}
	public void setPopupLeft( int left ) {
		popupLeft = left;
	}
	public void setPopupWidth(String width) {
		mainPanel.setWidth(width);
		frameWidth = width;
	}
	public void setPopupHeight(String height) {
		mainPanel.setHeight(height);
		frameHeight = height;
	}
}
