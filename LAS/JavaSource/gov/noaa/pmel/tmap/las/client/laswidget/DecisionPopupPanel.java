package gov.noaa.pmel.tmap.las.client.laswidget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

public class DecisionPopupPanel extends PopupPanel {

	public DecisionPopupPanel(String toolTip, String prompt,
			ClickHandler yesClickHandler, ClickHandler noClickHandler) {
		super(true);
		setAnimationEnabled(true);

		FlowPanel flowPanel = new FlowPanel();
		if ((toolTip == null) || (toolTip.equalsIgnoreCase("")))
			toolTip = "Set a Cookie?";
		flowPanel.setTitle(toolTip);
		setWidget(flowPanel);
		flowPanel.setSize("100%", "100%");

		if ((prompt == null) || (prompt.equalsIgnoreCase("")))
			prompt = "Do you want to save this setting for your browser?";
		Label lblPrompt = new Label(prompt);
		flowPanel.add(lblPrompt);

		Button btnYes = new Button("<strong>Yes</strong>");
		btnYes.addStyleDependentName("SMALLER");
		btnYes.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		btnYes.addClickHandler(yesClickHandler);
		btnYes.setFocus(true);
		btnYes.setTabIndex(0);
		flowPanel.add(btnYes);

		Button btnNo = new Button("No");
		btnNo.addStyleDependentName("SMALLER");
		btnNo.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		btnNo.addClickHandler(noClickHandler);
		btnNo.setFocus(false);
		btnNo.setTabIndex(1);
		flowPanel.add(btnNo);
	}

}
