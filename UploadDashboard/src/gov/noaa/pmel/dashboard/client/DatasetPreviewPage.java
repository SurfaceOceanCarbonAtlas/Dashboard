/**
 * 
 */
package gov.noaa.pmel.dashboard.client;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

import gov.noaa.pmel.dashboard.client.UploadDashboard.PagesEnum;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetList;
import gov.noaa.pmel.dashboard.shared.DashboardServicesInterface;
import gov.noaa.pmel.dashboard.shared.DashboardServicesInterfaceAsync;

/**
 * Page showing various plots of cruise data.
 * These plots are to be examined by a user 
 * to catch errors prior to submitting for QC.
 * 
 * @author Karl Smith
 */
public class DatasetPreviewPage extends CompositeWithUsername {

	private static final String TITLE_TEXT = "Preview Dataset";
	private static final String WELCOME_INTRO = "Logged in as ";
	private static final String LOGOUT_TEXT = "Logout";

	private static final String INTRO_HTML_PROLOGUE = 
			"Plots of the dataset: ";

	private static final String REFRESH_TEXT = "Refresh plots";
	private static final String REFRESH_HOVER_HELP = "Refresh the display the generated plots";
	private static final String DISMISS_TEXT = "Done";

	private static final String PLOT_GENERATION_FAILURE_HTML = "<b>Problems generating the plot previews</b>";

	private static final String LAT_VS_LON_TAB_TEXT = "lat vs lon";
	private static final String LAT_LON_TAB_TEXT = "lat, lon";
	private static final String SAMPLE_VS_TIME_TAB_TEXT = "sample num vs time";

	private static final String REFRESH_HELP_ADDENDUM = 
			" -- if plots do not show after awhile, try pressing the '" + REFRESH_TEXT + "' button given below this image.";

	private static final String LAT_VS_LON_ALT_TEXT = "latitude versus longitude";
	private static final String LAT_LON_ALT_TEXT = "latitude, longitude versus time";
	private static final String SAMPLE_VS_TIME_ALT_TEXT = "sample number (row number) versus time";

	public static final String LAT_VS_LON_IMAGE_NAME = "lat_vs_lon";
	public static final String LAT_LON_IMAGE_NAME = "lat_lon";
	public static final String SAMPLE_VS_TIME_IMAGE_NAME = "sample_vs_time";

	interface DatasetPreviewPageUiBinder extends UiBinder<Widget, DatasetPreviewPage> {
	}

	private static DatasetPreviewPageUiBinder uiBinder = 
			GWT.create(DatasetPreviewPageUiBinder.class);

	private static DashboardServicesInterfaceAsync service = 
			GWT.create(DashboardServicesInterface.class);

	@UiField InlineLabel titleLabel;
	@UiField InlineLabel userInfoLabel;
	@UiField Button logoutButton;
	@UiField HTML introHtml;
	@UiField Button refreshButton;
	@UiField Button dismissButton;

	@UiField HTML latVsLonHtml;
	@UiField HTML latLonHtml;
	@UiField HTML sampleVsTimeHtml;

	@UiField Image latVsLonImage;
	@UiField Image latLonImage;
	@UiField Image sampleVsTimeImage;

	String expocode;
	String timetag;
	AsyncCallback<Boolean> checkStatusCallback;

	// The singleton instance of this page
	private static DatasetPreviewPage singleton;

	public DatasetPreviewPage() {
		initWidget(uiBinder.createAndBindUi(this));
		singleton = this;

		setUsername(null);
		expocode = "";
		// Callback when generating plots
		checkStatusCallback = new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean isDone) {
				if ( UploadDashboard.isCurrentPage(singleton) ) {
					if ( isDone ) {
						UploadDashboard.showAutoCursor();
					}
					// Refresh this page to get the new image(s)
					singleton.resetImageUrls();
					if ( ! isDone ) {
						// More images to be generated - inquire again
						service.buildPreviewImages(getUsername(), singleton.expocode, 
								singleton.timetag, false, checkStatusCallback);
					}
				}
			}
			@Override
			public void onFailure(Throwable ex) {
				if ( UploadDashboard.isCurrentPage(singleton) ) {
					UploadDashboard.showAutoCursor();
					singleton.resetImageUrls();
					UploadDashboard.showFailureMessage(PLOT_GENERATION_FAILURE_HTML, ex);
				}
			}
		};

		titleLabel.setText(TITLE_TEXT);
		logoutButton.setText(LOGOUT_TEXT);

		refreshButton.setText(REFRESH_TEXT);
		refreshButton.setTitle(REFRESH_HOVER_HELP);
		dismissButton.setText(DISMISS_TEXT);

		// Set the HTML for the tabs
		latVsLonHtml.setHTML(LAT_VS_LON_TAB_TEXT);
		latLonHtml.setHTML(LAT_LON_TAB_TEXT);
		sampleVsTimeHtml.setHTML(SAMPLE_VS_TIME_TAB_TEXT);

		// Set hover helps for the tabs
		latVsLonHtml.setTitle(LAT_VS_LON_ALT_TEXT);
		latLonHtml.setTitle(LAT_LON_ALT_TEXT);
		sampleVsTimeHtml.setTitle(SAMPLE_VS_TIME_ALT_TEXT);

		// Set text alternative for the images
		latVsLonImage.setAltText(LAT_VS_LON_ALT_TEXT + REFRESH_HELP_ADDENDUM);
		latLonImage.setAltText(LAT_LON_ALT_TEXT + REFRESH_HELP_ADDENDUM);
		sampleVsTimeImage.setAltText(SAMPLE_VS_TIME_ALT_TEXT + REFRESH_HELP_ADDENDUM);

		// Set hover helps for the images
		latVsLonImage.setTitle(LAT_VS_LON_ALT_TEXT);
		latLonImage.setTitle(LAT_LON_ALT_TEXT);
		sampleVsTimeImage.setTitle(SAMPLE_VS_TIME_ALT_TEXT);
	}

	/**
	 * Display the preview page in the RootLayoutPanel with data plots  
	 * for the first cruise in the given cruiseList.  
	 * Adds this page to the page history.
	 */
	static void showPage(DashboardDatasetList cruiseList) {
		if ( singleton == null )
			singleton = new DatasetPreviewPage();
		UploadDashboard.updateCurrentPage(singleton);
		singleton.updatePreviewPlots(cruiseList.keySet().iterator().next(), 
									 cruiseList.getUsername());
		History.newItem(PagesEnum.PREVIEW_DATASET.name(), false);
	}

	/**
	 * Redisplays the last version of this page if the username
	 * associated with this page matches the given username.
	 */
	static void redisplayPage(String username) {
		if ( (username == null) || username.isEmpty() || 
			 (singleton == null) || ! singleton.getUsername().equals(username) ) {
			DatasetListPage.showPage();
		}
		else {
			UploadDashboard.updateCurrentPage(singleton);
		}
	}

	/**
	 * Updates the this page with the plots for the indicated cruise.
	 * 
	 * @param dataset
	 * 		cruises to use
	 * @param username
	 * 		user requesting these plots 
	 */
	private void updatePreviewPlots(String expocode, String username) {
		// Update the username
		setUsername(username);
		userInfoLabel.setText(WELCOME_INTRO + getUsername());

		if ( expocode != null )
			this.expocode = expocode.trim().toUpperCase();
		else
			this.expocode = "";
		introHtml.setHTML(INTRO_HTML_PROLOGUE + SafeHtmlUtils.htmlEscape(this.expocode));
		if ( this.expocode.length() > 11 ) {
			// Tell the server to generate the preview plots
			UploadDashboard.showWaitCursor();
			DateTimeFormat formatter = DateTimeFormat.getFormat("MMddHHmmss");
			this.timetag = formatter.format(new Date(), TimeZone.createTimeZone(0));
			service.buildPreviewImages(getUsername(), this.expocode, 
					this.timetag, true, checkStatusCallback);
		}
		// Set the URLs for the images.
		resetImageUrls();
	}

	/**
	 * Assigns the URLs to the images.  
	 * This triggers load events so the page should refresh when this is called.
	 */
	private void resetImageUrls() {
		String imagePrefix;
		String imageSuffix;
		if ( expocode.length() > 11 ) {
			imagePrefix = "preview/plots/" + expocode.substring(0,4) + "/" + expocode + "_";
			imageSuffix = "_" + timetag + ".gif";
		}
		else {
			imagePrefix = "preview/plots/invalid_";
			imageSuffix = ".gif";
		}
		latVsLonImage.setUrl(UriUtils.fromString(imagePrefix + LAT_VS_LON_IMAGE_NAME + imageSuffix));
		latLonImage.setUrl(UriUtils.fromString(imagePrefix + LAT_LON_IMAGE_NAME + imageSuffix));
		sampleVsTimeImage.setUrl(UriUtils.fromString(imagePrefix + SAMPLE_VS_TIME_IMAGE_NAME + imageSuffix));
	}

	@UiHandler("refreshButton")
	void refreshOnClick(ClickEvent event) {
		// Reload the images by setting the URLs again
		resetImageUrls();
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		DashboardLogoutPage.showPage();
	}

	@UiHandler("dismissButton")
	void cancelOnClick(ClickEvent event) {
		// Change to the latest cruise listing page.
		DatasetListPage.showPage();
	}

}
