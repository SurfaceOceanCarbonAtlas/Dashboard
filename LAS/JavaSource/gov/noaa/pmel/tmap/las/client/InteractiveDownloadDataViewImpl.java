package gov.noaa.pmel.tmap.las.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implementation of {@link InteractiveDownloadDataView}.
 */
public class InteractiveDownloadDataViewImpl extends Composite implements
		InteractiveDownloadDataView {

	interface Binder extends UiBinder<Widget, InteractiveDownloadDataViewImpl> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	FlowPanel iddPanel;

	@UiField
	FlowPanel axisPanel;

	@UiField
	ListBox dataFormatComboBox;

	@UiField
	FlowPanel dateTimePanel;

	@UiField
	HTML lblDownloadData;

	@UiField
	HTML lblSelectAData;

	@UiField
	HTML lblSelectDepth;

	@UiField
	HTML lblSelectedRegion;

	@UiField
	HTML lblSelectTime;

	private Presenter listener;

	private String name = "InteractiveDownloadData";

	@UiField
	Button saveButton;

	@UiField
	Label selectedRegionLatitude;

	@UiField
	Label selectedRegionLongitude;

	public InteractiveDownloadDataViewImpl() {
		initWidget(binder.createAndBindUi(this));
	}

	/**
	 * @return the axisPanel
	 */
	@Override
	public FlowPanel getAxisPanel() {
		return axisPanel;
	}

	/**
	 * @return the dataFormatComboBox
	 */
	@Override
	public ListBox getDataFormatComboBox() {
		return dataFormatComboBox;
	}

	/**
	 * @return the dateTimePanel
	 */
	@Override
	public FlowPanel getDateTimePanel() {
		return dateTimePanel;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the saveButton
	 */
	@Override
	public Button getSaveButton() {
		return saveButton;
	}

	/**
	 * @return the selectedRegionLatitude
	 */
	@Override
	public Label getSelectedRegionLatitude() {
		return selectedRegionLatitude;
	}

	/**
	 * @return the selectedRegionLongitude
	 */
	@Override
	public Label getSelectedRegionLongitude() {
		return selectedRegionLongitude;
	}

	@UiHandler("dataFormatComboBox")
	void onDataFormatComboBoxChange(ChangeEvent event) {
		listener.updateUI();
	}

	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		listener.saveAction();
	}

	/**
	 * @param axisPanel
	 *            the axisPanel to set
	 */
	public void setAxisPanel(FlowPanel axisPanel) {
		this.axisPanel = axisPanel;
	}

	/**
	 * @param dataFormatComboBox
	 *            the dataFormatComboBox to set
	 */
	public void setDataFormatComboBox(ListBox dataFormatComboBox) {
		this.dataFormatComboBox = dataFormatComboBox;
	}

	/**
	 * @param dateTimePanel
	 *            the dateTimePanel to set
	 */
	public void setDateTimePanel(FlowPanel dateTimePanel) {
		this.dateTimePanel = dateTimePanel;
	}

	@Override
	public void setName(String name) {
		// button.setHTML(name);
		this.name = name;
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}

	/**
	 * @param saveButton
	 *            the saveButton to set
	 */
	public void setSaveButton(Button saveButton) {
		this.saveButton = saveButton;
	}

	/**
	 * @param selectedRegionLatitude
	 *            the selectedRegionLatitude to set
	 */
	public void setSelectedRegionLatitude(Label selectedRegionLatitude) {
		this.selectedRegionLatitude = selectedRegionLatitude;
	}

	/**
	 * @param selectedRegionLongitude
	 *            the selectedRegionLongitude to set
	 */
	public void setSelectedRegionLongitude(Label selectedRegionLongitude) {
		this.selectedRegionLongitude = selectedRegionLongitude;
	}

}
