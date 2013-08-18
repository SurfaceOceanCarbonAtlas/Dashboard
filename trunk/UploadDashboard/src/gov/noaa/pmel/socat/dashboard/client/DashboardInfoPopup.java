/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.Widget;

/**
 * An info message that is embedded within a PopupPanel.
 * 
 * @author Karl Smith
 */
public class DashboardInfoPopup extends Composite {

	protected static String dismissText = "Dismiss";

	interface DashboardInfoPopupUiBinder extends 
			UiBinder<Widget, DashboardInfoPopup> {
	}

	private static DashboardInfoPopupUiBinder uiBinder = 
			GWT.create(DashboardInfoPopupUiBinder.class);

	@UiField HTML infoHTML;
	@UiField Button dismissButton;

	private PopupPanel parentPanel;

	/**
	 * Creates an empty info message widget within a PopupPanel.
	 * The popup includes a dismiss button to hide it.  
	 * Sets auto-hide so clicking outside the popup will also 
	 * hide it.  
	 * Use {@link #setInfoMessage(String)} to assign 
	 * the message to be displayed.  
	 * Use {@link #showAtPosition(int, int)} to show the 
	 * popup at the specified location.
	 */
	DashboardInfoPopup() {
		initWidget(uiBinder.createAndBindUi(this));
		dismissButton.setText(dismissText);
		parentPanel = new PopupPanel();
		parentPanel.setWidget(this);
		parentPanel.setAutoHideEnabled(true);
	}

	/**
	 * @param htmlMessage
	 * 		the unchecked HTML message to display.
	 * 		For safety, use only known (static) HTML.
	 */
	void setInfoMessage(String htmlMessage) {
		infoHTML.setHTML(htmlMessage);
	}

	/**
	 * Show the popup at the specified location.
	 * 
	 * @param left
	 * 		absolute position of the left edge of the popup
	 * @param top
	 * 		absolute position of the top edge of the popup
	 */
	void showAtPosition(final int left, final int top) {
		parentPanel.setPopupPositionAndShow(new PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				parentPanel.setPopupPosition(left, top);
			}
		});
	}

	@UiHandler("dismissButton")
	void onClick(ClickEvent e) {
		parentPanel.hide();
	}

}
