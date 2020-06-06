package gov.noaa.pmel.tmap.las.client.laswidget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
/**
 * A popup panel with a close button that can take a message.  Like an Alert, but not as ugly.
 * @author rhs
 *
 */
public class MessagePanel extends DialogBox {
	
	
	final Button messageButton;
	String messageText = "Empty message.";
	public MessagePanel() {
	    setModal(false);
	    setAutoHideEnabled(true);
	    setAutoHideOnHistoryEventsEnabled(true);
		messageButton = new Button("Close");
		messageButton.addClickHandler(closeHandler);
		setText(messageText);
        setWidget(messageButton);
	}
	public void show(int left, int top, String text) {
		this.setPopupPosition(left, top);
		messageText = text;
		this.setText(messageText);
		this.show();
	}
	
	private ClickHandler closeHandler = new ClickHandler() {
        
        @Override
        public void onClick(ClickEvent arg0) {
            hide();
        }
    };
	
}
