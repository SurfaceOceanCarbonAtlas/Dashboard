/**
 *
 */
package gov.noaa.pmel.dashboard.client;

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
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * An question embedded within a PopupPanel.
 *
 * @author Karl Smith
 */
public class DashboardInputPopup extends Composite {

    interface DashboardAskPopupUiBinder extends UiBinder<Widget,DashboardInputPopup> {
    }

    private static DashboardAskPopupUiBinder uiBinder =
            GWT.create(DashboardAskPopupUiBinder.class);

    @UiField
    HTML infoHtml;
    @UiField
    InlineLabel inputTextLabel;
    @UiField
    TextBox inputTextBox;
    @UiField
    Button yesButton;
    @UiField
    Button noButton;

    private PopupPanel parentPanel;
    String answer;
    HandlerRegistration askHandler;

    /**
     * Widget for asking for input in a PopupPanel that is modal and does not auto-hide.
     *
     * @param labelText
     *         text for the label next to the input text box
     * @param yesText
     *         text for the yes button
     * @param noText
     *         text for the no button
     * @param callback
     *         calls the onSuccess method of this callback with the input provided by the user if the yes button was
     *         selected.  If the no button was selected, or if the window was (somehow) closed without pressing either
     *         the yes or no button, null is returned. The onFailure method of this callback is never called.
     */
    public DashboardInputPopup(String labelText, String yesText,
            String noText, final AsyncCallback<String> callback) {
        initWidget(uiBinder.createAndBindUi(this));

        parentPanel = new PopupPanel(false, true);
        parentPanel.setWidget(this);

        inputTextLabel.setText(labelText);
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
     * Assigns the question asked in this PopupPanel and shows the PopupPanel centered in the browser window. The no
     * button is given the focus.
     *
     * @param htmlInfo
     *         the unchecked HTML information to display. For safety, use only known (static) HTML.
     */
    void askForInput(String htmlInfo) {
        noButton.setFocus(true);
        infoHtml.setHTML(htmlInfo);
        parentPanel.center();
    }

    @UiHandler("yesButton")
    void yesOnClick(ClickEvent e) {
        answer = inputTextBox.getText();
        parentPanel.hide();
    }

    @UiHandler("noButton")
    void noOnClick(ClickEvent e) {
        answer = null;
        parentPanel.hide();
    }

}
