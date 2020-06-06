package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.AppConstants;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.WidgetCollection;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

/**
 * Default implementation of {@link VariableSelector}.
 */
// TODO: Move non-view logic to the activity presenter/listener
public class VariableSelectorImpl extends VerticalPanel implements HasName,
		VariableSelector {
	private static final AppConstants CONSTANTS = GWT
			.create(AppConstants.class);
	private boolean comparing = false;
	private int itemCount = 0;
	// private UserListBox nextToLastAddedListBox;
	// private UserListBox lastAddedListBox;
	private Presenter listener;
	private String name;
	private UserListBox variablesListBox;
	private VariableMetadataView variableMetadataView;
	private final Logger logger = Logger.getLogger(VariableSelectorImpl.class
			.getName());

	private VariableSelectorImpl() {
		this(null);
	}

	/**
	 * @wbp.parser.constructor
	 */
	public VariableSelectorImpl(String id) {
		logger.setLevel(Level.OFF);
		setName(id);
		setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		setComparing(!isOnComparePanel());
		variablesListBox = initUserListBox(id, true);
		addListBox(variablesListBox);
	}

	@Override
	public void addListBox(UserListBox newListBox) {
		int oldItemCount = itemCount;
		add(newListBox);
		// nextToLastAddedListBox = lastAddedListBox;
		// lastAddedListBox = newListBox;
		if (itemCount == 0) {
			// Since we just added the first UserListBox, give it a dataset info
			// widget if it exists
			if (variableMetadataView != null) {
				variablesListBox.addDataSetInfoWidget(variableMetadataView);
			}
		} else {
			newListBox.removeVectors();
			newListBox.setSelectedIndex(-1);
			for (int b = 0; b < itemCount; b++) {
				UserListBox box = (UserListBox) getWidget(b);
				String svarid = box.getUserObject(box.getSelectedIndex())
						.getID();
				box.removeVectors();

				int si = 0;
				for (int c = 0; c < box.getVariables().size(); c++) {
					String nid = box.getVariables().get(c).getID();
					if (nid.equals(svarid))
						si = c;
				}
				box.setSelectedIndex(si);
			}
		}
		this.itemCount++;
		if (listener != null)
			listener.itemCountUpdated(oldItemCount, itemCount, this);
	}

	@Override
	public Widget asWidget() {
		return super.asWidget();
	}

	@Override
	public int getItemCount() {
		return itemCount;
	}

	/**
	 * @return The latest remaining (bottom most) ListBox
	 */
	@Override
	public UserListBox getLatestListBox() {
		UserListBox latestListBox = null;
		WidgetCollection children = this.getChildren();
		for (int index = children.size() - 1; index >= 0; index--) {
			Widget widget = children.get(index);
			if (widget instanceof UserListBox) {
				latestListBox = (UserListBox) widget;
				index = -1; // break out of for-loop
			}
		}
		return latestListBox;
	}

	@Override
	public Vector<UserListBox> getListBoxes() {
		Vector<UserListBox> listBoxes = new Vector<UserListBox>();
		WidgetCollection children = this.getChildren();
		for (int index = 0; index < children.size(); index++) {
			Widget widget = children.get(index);
			if (widget instanceof UserListBox) {
				listBoxes.add((UserListBox) widget);
			}
		}
		return listBoxes;
	}

	/**)
	 * @see com.google.gwt.user.client.ui.HasName#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param id
	 * @param view
	 */
	@Override
	public UserListBox initUserListBox(String id, boolean addChangeHandler) {
		final VariableSelector view = this;
		final UserListBox newListBox = new UserListBox(id, false);
//		if (addChangeHandler) {
//			newListBox.addChangeHandler(new ChangeHandler() {
//				@Override
//				public void onChange(ChangeEvent event) {
//					listener.onChange(event, view);
//					listener.onChange(event, newListBox);
//				}
//			});
//		} else {
//			newListBox.addChangeHandler(new ChangeHandler() {
//				@Override
//				public void onChange(ChangeEvent event) {
//					listener.onChange(event, view);
//				}
//			});
//		}
		newListBox.addAddButtonClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				listener.onAddButtonClick(event, newListBox, view);
			}
		});
		newListBox.addRemoveButtonClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				listener.onRemoveButtonClick(event, newListBox, view);
			}
		});
		// newListBox.addItem("Variables", "varid");
		newListBox.setAddButtonVisible(false);
		newListBox.setMinItemsForAddButtonToBeVisible(-1);
		return newListBox;
	}

	@Override
	public boolean isComparing() {
		return comparing;
	}

	/**
	 * @return
	 */
	boolean isOnComparePanel() {
		return CONSTANTS.comparePanelName().equalsIgnoreCase(getName());
	}

	@Override
	public void removeListBox(UserListBox oldListBox) {
		int oldItemCount = itemCount;
		boolean removed = remove(oldListBox);
		if (removed) {
			this.itemCount--;
			if (listener != null)
				listener.itemCountUpdated(oldItemCount, itemCount, this);
			// Show the latest remaining list box's remove button
			UserListBox latestListBox = getLatestListBox();
			if (itemCount == 1) {
				latestListBox.addVectors();
			}
			if (latestListBox != null) {
				latestListBox.setRemoveButtonVisible(itemCount > 1);
				List<VariableSerializable> variables = latestListBox
						.getVariables();
				if (variables != null) {
					latestListBox
							.setAddButtonVisible(variables.size() > itemCount);
				}
				final VariableSelector view = this;
				listener.onChange(new ChangeEvent() {
				}, view);
			}
		} else {
			logger.severe("FAILED to remove oldListBox:");
			try {
				logger.severe(oldListBox.getName(oldListBox.getSelectedIndex()));
			} catch (Exception e) {
				logger.log(Level.WARNING, e.getLocalizedMessage(), e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Need to be able to remove the list boxes without firing any events except
	 * to fire one at the end.
	 */
	@Override
	public void removeExtraListBoxes(boolean update_count) {
		int oldItemCount = itemCount;
		// Start by removing all but the first.
		List<UserListBox> removeme = new ArrayList<UserListBox>();
		for (int i = 1; i < oldItemCount; i++) {
			Widget w = this.getWidget(i);
			if (w instanceof UserListBox) {
				removeme.add((UserListBox) w);
			}
		}
		for (Iterator rIt = removeme.iterator(); rIt.hasNext();) {
			UserListBox userListBox = (UserListBox) rIt.next();
			remove(userListBox);
		}
		itemCount = 1;
		UserListBox latestListBox = getLatestListBox();
		latestListBox.addVectors();
		if (latestListBox != null) {
			latestListBox.setRemoveButtonVisible(false);
			List<VariableSerializable> variables = latestListBox
					.getVariables();
			if (variables != null) {
				latestListBox.setAddButtonVisible(variables.size() > itemCount);
			}
			if(isComparing()){
				latestListBox.setAddButtonVisible(false);
			}
		}
		if (listener != null && update_count)
			listener.itemCountUpdated(oldItemCount, itemCount, this);
	}

	@Override
	public void removeListBoxesExceptFirst() {
		logger.info("removeListBoxesExceptFirst() called with this.getChildren().size():"
				+ this.getChildren().size());
		for (Widget w : this.getChildren()) {
			logger.info("Widget w:" + w.getClass().getName());
			if (w instanceof UserListBox) {
				UserListBox listBox = (UserListBox) w;
				if (listBox != null) {
					try {
						int selectedIndex = listBox.getSelectedIndex();
						if (selectedIndex >= 0) {
							logger.info("listBox:"
									+ listBox.getName(selectedIndex));
						} else {
							logger.warning("listBox(Nothing Selected):"+listBox.toString());
						}
					} catch (Exception e) {
						if (e != null) {
							logger.log(Level.WARNING,
									"problem logging listBox", e);
							e.printStackTrace();
						}
					}
				}
				if (listBox != variablesListBox) {
					logger.info("removing...");
					removeListBox(listBox);
				}
			}
		}
		logger.info("this.getChildren().size():" + this.getChildren().size());
		// nextToLastAddedListBox = null;
		// lastAddedListBox = variablesListBox;
	}

	@Override
	public void setComparing(boolean comparing) {
		this.comparing = comparing;
	}

	/**
	 * @see com.google.gwt.user.client.ui.HasName#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}

	@Override
	public void setVariables(Vector<VariableSerializable> variables,
			int selected) {
		removeListBoxesExceptFirst();
		variablesListBox.setVariables(variables);
		variablesListBox.setSelectedIndex(selected);
		// When multiple variables are in the data set, and the app is not in
		// comparing mode,
		// allow adding variables to the comparePanel ONLY.
		if (!isComparing() && isOnComparePanel() && (variables != null)
				&& (variables.size() > 1)) {
			// variablesListBox.setMinItemsForAddButtonToBeVisible(0); //
			// instead of -1.

			// Avoid giving the user the ability to add more variableListBoxes
			// than there are variables in the current data set.
			variablesListBox.setAddButtonVisible(variables.size() > itemCount);
		} else if(isComparing()){
			variablesListBox.setAddButtonVisible(false);
		}
	}

	@Override
	public void setVariableMetadataView(
			VariableMetadataView variableMetadataView) {
		this.variableMetadataView = variableMetadataView;
		if (variablesListBox != null) {
			variablesListBox.addDataSetInfoWidget(variableMetadataView);
		}
	}

	@Override
	public UserListBox addUserListBox(UserListBox source, VariableSelector view) {
		return this.listener.addUserListBox(source, view);
	}

	/**
	 * @return the first (top-most) UserListBox
	 */
	@Override
	public UserListBox getFirstListBox() {
		return variablesListBox;
	}

	/**
	 * @param variablesListBox
	 *            the first (top-most) UserListBox to set
	 */
	public void setFirstListBox(UserListBox variablesListBox) {
		this.variablesListBox = variablesListBox;
	}

}
