package gov.noaa.pmel.tmap.las.client.laswidget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PopupTextPanel extends Composite {
	Label title;
	PopupPanel panel = new PopupPanel(true);
	VerticalPanel innerPanel = new VerticalPanel();
	TextBox textbox = new TextBox();
	PushButton close = new PushButton("Close");
	public PopupTextPanel(String titleText) {
		title = new Label(titleText);
		innerPanel.add(title);
		innerPanel.add(textbox);
		close.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				
				panel.hide();
				
			}
			
		});
		innerPanel.add(close);
		panel.add(innerPanel);
	}
	public void show(int left, int top) {
		
		panel.setPopupPosition(left, top);
		panel.show();
		
	}
	public void addCloseHandler(ClickHandler closeHandler) {
		
		close.addClickHandler(closeHandler);
		
	}
	
	public String getText() {
		return textbox.getText().trim();
	}

}
