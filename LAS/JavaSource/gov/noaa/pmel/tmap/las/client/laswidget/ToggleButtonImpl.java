package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.activity.ToggleButtonActivity;
import gov.noaa.pmel.tmap.las.client.activity.VariableSelectorActivity;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Sample implementation of {@link ToggleButton}.
 */
public class ToggleButtonImpl extends Composite implements ToggleButton {
	private ClientFactory clientFactory;
	static final String DEFAULT_DOWNTEXT = "Do the opposite";
	static final String DEFAULT_UPTEXT = "Do it";
	private Vector<ClickHandler> clickHandlers = new Vector<ClickHandler>();
	private boolean down = false;
	private Presenter listener;
	private SimplePanel panel = new SimplePanel();
	private PushButton pshbtnDown = new PushButton(DEFAULT_DOWNTEXT);
	private PushButton pshbtnUp = new PushButton(DEFAULT_UPTEXT);
	private final ToggleButton thisView = this;

	/**
	 * @wbp.parser.constructor
	 */
	public ToggleButtonImpl() {
		this(DEFAULT_UPTEXT, DEFAULT_DOWNTEXT, null);
	}

	public ToggleButtonImpl(String upText, String downText, ClickHandler handler) {
		if (clientFactory == null)
			clientFactory = GWT.create(ClientFactory.class);
		if (listener == null)
			setPresenter(new ToggleButtonActivity(clientFactory));
		if (handler != null)
			addClickHandler(handler);
		if (upText != null)
			pshbtnUp.setText(upText);
		if (downText != null)
			pshbtnDown.setText(downText);

		initWidget(panel);
		pshbtnUp.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				toggleButtonState();
				listener.pshbtnUpOnClick(event, thisView);
				thisView.onClick(event);
			}
		});
		pshbtnDown.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				toggleButtonState();
				listener.pshbtnDownOnClick(event, thisView);
				thisView.onClick(event);
			}
		});

		panel.setWidget(pshbtnUp);
		pshbtnUp.setSize("100%", "100%");
	}

	@Override
	public boolean addClickHandler(ClickHandler handler) {
		if (handler != null)
			return clickHandlers.add(handler);
		return false;
	}

	/**
	 * @return the current down state
	 */
	@Override
	public boolean isDown() {
		return down;
	}

	@Override
	public void onClick(ClickEvent event) {
		for (ClickHandler handler : clickHandlers) {
			handler.onClick(event);
		}
	}

	@Override
	public boolean removeClickHandler(ClickHandler handler) {
		return clickHandlers.remove(handler);
	}

	/**
	 * @param down
	 *            the down state to set
	 */
	@Override
	public void setDown(boolean down) {
		this.down = down;
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}

	protected void toggleButtonState() {
		// Swap buttons and set the new down state
		if (down) {
			// Then the pshbtnDown should be on the panel
			// so put pshbtnUp on the panel
			if (panel.remove(pshbtnDown)) {
				panel.add(pshbtnUp);
			}
		} else {
			// Then the pshbtnUp should be on the panel
			// so put pshbtnDown on the panel
			if (panel.remove(pshbtnUp)) {
				panel.add(pshbtnDown);
			}
		}
		down = !down;
	}

	/**
	 * @see com.google.gwt.user.client.ui.UIObject#addStyleDependentName(java.lang.String)
	 */
	@Override
	public void addStyleDependentName(String styleSuffix) {
		pshbtnUp.addStyleDependentName(styleSuffix);
		pshbtnDown.addStyleDependentName(styleSuffix);
	}

	/**
	 * @see com.google.gwt.user.client.ui.UIObject#removeStyleDependentName(java.lang.String)
	 */
	@Override
	public void removeStyleDependentName(String styleSuffix) {
		pshbtnUp.removeStyleDependentName(styleSuffix);
		pshbtnDown.removeStyleDependentName(styleSuffix);
	}
}
