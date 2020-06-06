/**
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development. 
 */
package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.event.WidgetSelectionChangeEvent;
import gov.noaa.pmel.tmap.las.client.serializable.OptionSerializable;
import gov.noaa.pmel.tmap.las.client.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A widget that will pull down the options for a particular operation and will
 * return a map of the current state.
 * 
 * @author rhs
 * 
 */
public class OptionsWidget extends VerticalPanel {
	OptionSerializable[] options;
	Button ok = new Button("OK");
	Button cancel = new Button("Cancel");
	Grid layout_grid = new Grid(1, 2);
	List<Widget> widgets = new ArrayList<Widget>();
	Map<String, String> callbackState = null;
	boolean rpc = false;
	private ClientFactory clientFactory = GWT.create(ClientFactory.class);
	private EventBus eventBus = clientFactory.getEventBus();
	private boolean isEmptyOps = false;
    private boolean fireEvent;
	public OptionsWidget() {
		layout_grid.setWidget(0, 0, ok);
		layout_grid.setWidget(0, 1, cancel);
	}

	public OptionsWidget(String opID) {
		layout_grid.setWidget(0, 0, ok);
		layout_grid.setWidget(0, 1, cancel);
		Util.getRPCService().getOptions(opID, optionsCallback);
	}

	public OptionsWidget(String optionID, ClickListener okListener,
			ClickListener cancelListener) {
		layout_grid.setWidget(0, 0, ok);
		layout_grid.setWidget(0, 1, cancel);
		ok.addClickListener(okListener);
		cancel.addClickListener(cancelListener);
		Util.getRPCService().getOptions(optionID, optionsCallback);
	}

	public OptionsWidget(ClickListener okListener, ClickListener cancelListener) {
		layout_grid.setWidget(0, 0, ok);
		layout_grid.setWidget(0, 1, cancel);
		ok.addClickListener(okListener);
		cancel.addClickListener(cancelListener);
	}

	public OptionsWidget(ClickListener listener) {
		ok.addClickListener(listener);
		cancel.addClickListener(listener);
		layout_grid.setWidget(0, 0, ok);
		layout_grid.setWidget(0, 1, cancel);
	}

	public void setOptions(String id, boolean event) {
		fireEvent = event;
		callbackState = null;
		if (id != null && !id.equals("")) {
			rpc = true;
			Util.getRPCService().getOptions(id, optionsCallback);
		} else {
			// If there are no options, show the OK and Cancel buttons.
			setOptions(new OptionSerializable[0]);
		}
	}

	public void setOptions(String id, Map<String, String> options) {
		// Only happens for internal operations.
		fireEvent = true;
		callbackState = options;
		if (id != null && !id.equals("")) {
			rpc = true;
			Util.getRPCService().getOptions(id, optionsCallback);
		}
	}

	AsyncCallback optionsCallback = new AsyncCallback() {
		public void onSuccess(Object result) {
			options = (OptionSerializable[]) result;
			setOptions(options);
			isEmptyOps = false;
			rpc = false;
			if (callbackState != null) {
				restore(callbackState);
			}
			// Don't request that the plot refresh happen automatically, don't
			// force panels to update if they don't need to and don't add to the
			// history stack.
			
			// Do not fire this event for "external" operations buttons.
			if ( fireEvent ) {
				eventBus.fireEventFromSource(new WidgetSelectionChangeEvent(false, false, true), OptionsWidget.this);
			}
		}

		public void onFailure(Throwable e) {
			setOptions(new OptionSerializable[0]);
			isEmptyOps = false;
			Window.alert(e.toString());
		}
	};

	public void restore(Map<String, String> state) {
		if (rpc) {
			callbackState = state;
			return;
		}
		for (Iterator widIt = widgets.iterator(); widIt.hasNext();) {
			Widget w = (Widget) widIt.next();
			if (w instanceof TextBox) {
				TextBox t = (TextBox) w;
				String value = state.get(t.getName());
				if (value != null && !value.equals("")) {
					t.setText(value);
				}
				if (value != null && value.equals("reset")) {
					t.setText("");
				}
			} else if (w instanceof ListBox) {
				ListBox l = (ListBox) w;
				String value = state.get(l.getName());
				if (value != null && !value.equals("")) {
					int count = l.getItemCount();
					for (int i = 0; i < count; i++) {
						String item_value = l.getValue(i);
						if (item_value.equals(value)) {
							l.setSelectedIndex(i);
						}
					}
				}
			}
		}
	}

	public Map<String, String> getState() {
		Map<String, String> state = new HashMap<String, String>();
		for (Iterator widIt = widgets.iterator(); widIt.hasNext();) {
			Widget w = (Widget) widIt.next();
			if (w instanceof TextBox) {
				TextBox t = (TextBox) w;
				if (t.getText() != null && !t.getText().equals("")) {
					String text = t.getText();
					String name = t.getName();
					if ( name != null && name.equals("expression") ) {
						if ( text != null && !text.isEmpty() ) {
							text = text.replaceAll("\\+", "_ps_");
						}
					}
					state.put(name, text);
				}
			} else if (w instanceof ListBox) {
				ListBox l = (ListBox) w;
				state.put(l.getName(), l.getValue(l.getSelectedIndex()));
			}
		}
		return state;
	}

	public HandlerRegistration addOkClickHandler(ClickHandler handler) {
		ok.ensureDebugId("ok");
		return ok.addClickHandler(handler);
	}

	public void setOptions(OptionSerializable[] op) {
		options = op;
		clear();
		widgets.clear();
		for (int i = 0; i < options.length; i++) {
			OptionSerializable opt = op[i];
			Button help = new Button("help");
			final String help_html = opt.getHelp();
			help.addClickListener(new ClickListener() {

				public void onClick(Widget help) {
					PopupPanel helpup = new PopupPanel(true);
					HTML html = new HTML();
					html.setHTML(help_html);
					helpup.add(html);
					helpup.center();
				}

			});
			Grid option_layout = new Grid(1, 2);
			option_layout.setWidget(0, 0, help);

			if (opt.getType().equals("textfield")) {
				Grid box = new Grid(1, 2);
				Label label = new Label(opt.getTitle());
				TextBox textbox = new TextBox();
				textbox.setName(opt.getName());
				box.setWidget(0, 0, label);
				box.setWidget(0, 1, textbox);
				widgets.add(textbox);
				option_layout.setWidget(0, 1, box);
			} else {
				Label label = new Label(opt.getTitle());
				ListBox menu = new ListBox();
				Grid box = new Grid(1, 2);
				Map<String, String> items = opt.getMenu();
				Vector keys = new Vector(items.keySet());
				keys.remove("default");
				keys.remove("Default");
				Collections.sort(keys);
				menu.addItem("Default", "default");
				for (Iterator itemIt = keys.iterator(); itemIt.hasNext();) {
					String name = (String) itemIt.next();
					String value = (String) items.get(name);
					menu.addItem(name, value);
				}
				menu.setName(opt.getName());
				box.setWidget(0, 0, label);
				box.setWidget(0, 1, menu);
				widgets.add(menu);
				option_layout.setWidget(0, 1, box);
			}
			add(option_layout);
		}
		isEmptyOps = (options == null) ? true : (options.length <= 0);
		add(layout_grid);
	}

	public void addOkHandler(ClickHandler handler) {
		ok.addClickHandler(handler);
	}

	public void addCancelHandler(ClickHandler handler) {
		cancel.addClickHandler(handler);
	}

	/**
	 * 
	 * @see com.google.gwt.user.client.ui.Button#click()
	 */
	public void ok() {
		ok.click();
	}

	public boolean isEmptyOps() {
		return isEmptyOps;
	}

	public void hideButtons() {
		ok.setVisible(false);
		cancel.setVisible(false);
	}

	public void showButtons() {
		ok.setVisible(true);
		cancel.setVisible(true);
	}
}
