/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * An question embedded within a PopupPanel.
 * 
 * @author Karl Smith
 */
public class DashboardAskPopup extends Composite {

	interface DashboardAskPopupUiBinder extends UiBinder<Widget, DashboardAskPopup> {
	}

	private static DashboardAskPopupUiBinder uiBinder = 
			GWT.create(DashboardAskPopupUiBinder.class);

	@UiField HTML askHtml;
	@UiField Button yesButton;
	@UiField Button noButton;

	private PopupPanel parentPanel;
	Boolean answer;
	HandlerRegistration askHandler;

	/**
	 * Widget for asking a question in a PopupPanel 
	 * that is modal and does not auto-hide.
	 * 
	 * @param yesText
	 * 		text for the yes button
	 * @param noText
	 * 		text for the no button
	 * @param callback
	 * 		calls the onSuccess method of this callback with the answer: 
	 * 		true for the yes button, false for the no button, or null if 
	 * 		the window was (somehow) closed without pressing either the
	 * 		yes or no button.  The onFailure method of this callback is
	 * 		never called.
	 */
	public DashboardAskPopup(String yesText, String noText,	
			final AsyncCallback<Boolean> callback) {
		initWidget(uiBinder.createAndBindUi(this));

		parentPanel = new PopupPanel(false, true);
		parentPanel.setWidget(this);

		yesButton.setText(yesText);

		noButton.setText(noText);

		answer = null;

		// Handler to make the callback on window closing 
		askHandler = parentPanel.addCloseHandler(
				new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				// Make the appropriate call
				callback.onSuccess(answer);
			}
		});
	}

	/**
	 * Assigns the question asked in this PopupPanel and 
	 * shows the PopupPanel centered in the browser window.
	 * The no button is given the focus.
	 * 
	 * @param htmlQuestion
	 * 		the unchecked HTML question to display.
	 * 		For safety, use only known (static) HTML.
	 */
	void askQuestion(String htmlQuestion) {
		noButton.setFocus(true);
		askHtml.setHTML(htmlQuestion);
		parentPanel.center();
	}

	@UiHandler("yesButton")
	void yesOnClick(ClickEvent e) {
		answer = true;
		parentPanel.hide();
	}

	@UiHandler("noButton")
	void noOnClick(ClickEvent e) {
		answer = false;
		parentPanel.hide();
	}

}
