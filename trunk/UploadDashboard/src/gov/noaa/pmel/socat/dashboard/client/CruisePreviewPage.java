/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.client.SocatUploadDashboard.PagesEnum;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList;
import gov.noaa.pmel.socat.dashboard.shared.DashboardServicesInterface;
import gov.noaa.pmel.socat.dashboard.shared.DashboardServicesInterfaceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
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

/**
 * Page showing various plots of cruise data.
 * These plots are to be examined by a user 
 * to catch errors prior to submitting for QC.
 * 
 * @author Karl Smith
 */
public class CruisePreviewPage extends CompositeWithUsername {

	private static final String TITLE_TEXT = "Preview Dataset";
	private static final String WELCOME_INTRO = "Logged in as ";
	private static final String LOGOUT_TEXT = "Logout";

	private static final String INTRO_HTML_PROLOGUE = 
			"Examine data plots for the dataset: ";

	private static final String DISMISS_TEXT = "Done";

	private static final String PLOT_GENERATION_FAILURE_HTML = "<b>Problems generating the plot previews</b>";

	private static final String LAT_VS_LON_ALT_TEXT = "latitude versus longitude";
	private static final String LAT_LON_VS_NUM_ALT_TEXT = "latitude, longitude versus sample number";
	private static final String DAY_YEAR_VS_NUM_ALT_TEXT = "day of year, year versus sample number";
	private static final String TIME_SERIES_ALT_TEXT = "fCO2_rec, SST, sal, lon, lat versus time";
	private static final String PRESSURES_VS_NUM_ALT_TEXT = "pressures versus sample number";
	private static final String TEMPERATURES_VS_NUM_ALT_TEXT = "temperatures versus sample number";
	private static final String XCO2_VS_NUM_ALT_TEXT = "xCO2 values versus sample number";
	private static final String SALINITY_VS_NUM_ALT_TEXT = "salinities versus sample number";
	private static final String FCO2REC_VS_NUM_ALT_TEXT = "fCO2_rec versus sample number";
	private static final String FCO2REC_VS_SST_ALT_TEXT = "fCO2_rec versus SST";
	private static final String FCO2REC_VS_SAL_ALT_TEXT = "fCO2_rec versus salinity";
	private static final String FCO2REC_VS_FCO2_ALT_TEXT = "fCO2_rec versus given fCO2";
	private static final String FCO2REC_SRC_HIST_ALT_TEXT = "histogram of fCO2_rec computation method";

	private static final String IMAGE_TYPE_SUFFIX = ".png";
	private static final String LAT_VS_LON_IMAGE_SUFFIX = "lat_vs_lon" + IMAGE_TYPE_SUFFIX;
	private static final String LAT_LON_VS_NUM_IMAGE_SUFFIX = "lat_lon_vs_num" + IMAGE_TYPE_SUFFIX;
	private static final String DAY_YEAR_VS_NUM_IMAGE_SUFFIX = "day_year_sv_num" + IMAGE_TYPE_SUFFIX;
	private static final String TIME_SERIES_IMAGE_SUFFIX = "time_series" + IMAGE_TYPE_SUFFIX;
	private static final String PRESSURES_VS_NUM_IMAGE_SUFFIX = "press_vs_num" + IMAGE_TYPE_SUFFIX;
	private static final String TEMPERATURES_VS_NUM_IMAGE_SUFFIX = "temp_vs_num" + IMAGE_TYPE_SUFFIX;
	private static final String XCO2_VS_NUM_IMAGE_SUFFIX = "xco2_vs_num" + IMAGE_TYPE_SUFFIX;
	private static final String SALINITY_VS_NUM_IMAGE_SUFFIX = "sal_vs_num" + IMAGE_TYPE_SUFFIX;
	private static final String FCO2REC_VS_NUM_IMAGE_SUFFIX = "fco2rec_vs_num" + IMAGE_TYPE_SUFFIX;
	private static final String FCO2REC_VS_SST_IMAGE_SUFFIX = "fco2rec_vs_sst" + IMAGE_TYPE_SUFFIX;
	private static final String FCO2REC_VS_SAL_IMAGE_SUFFIX = "fco2rec_vs_sal" + IMAGE_TYPE_SUFFIX;
	private static final String FCO2REC_VS_FCO2_IMAGE_SUFFIX = "fco2rec_vs_fco2" + IMAGE_TYPE_SUFFIX;
	private static final String FCO2REC_SRC_HIST_IMAGE_SUFFIX = "fco2rec_src_hist" + IMAGE_TYPE_SUFFIX;

	interface CruisePreviewPageUiBinder extends UiBinder<Widget, CruisePreviewPage> {
	}

	private static CruisePreviewPageUiBinder uiBinder = 
			GWT.create(CruisePreviewPageUiBinder.class);

	private static DashboardServicesInterfaceAsync service = 
			GWT.create(DashboardServicesInterface.class);

	@UiField InlineLabel titleLabel;
	@UiField InlineLabel userInfoLabel;
	@UiField Button logoutButton;
	@UiField HTML introHtml; 
	@UiField Button dismissButton;
	@UiField Image latVsLonImage;
	@UiField Image latLonVsNumImage;
	@UiField Image dayYearVsNumImage;
	@UiField Image timeSeriesImage;
	@UiField Image pressuresVsNumImage;
	@UiField Image temperaturesVsNumImage;
	@UiField Image xco2sVsNumImage;
	@UiField Image salinitiesVsNumImage;
	@UiField Image fco2recVsNumImage;
	@UiField Image fco2recVsSstImage;
	@UiField Image fco2recVsSalImage;
	@UiField Image fco2recVsFco2Image;
	@UiField Image fco2recSrcHistogramImage;

	String expocode;
	AsyncCallback<Boolean> checkStatusCallback;

	// The singleton instance of this page
	private static CruisePreviewPage singleton;

	public CruisePreviewPage() {
		initWidget(uiBinder.createAndBindUi(this));
		singleton = this;

		setUsername(null);
		expocode = "";
		// Callback when generating plots
		checkStatusCallback = new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean isDone) {
				if ( SocatUploadDashboard.isCurrentPage(singleton) ) {
					// Refresh this page to get the new image(s)
					singleton.resetImageUrls();
					if ( ! isDone ) {
						// More images to be generated - inquire again
						service.buildPreviewImages(getUsername(), singleton.expocode, false, checkStatusCallback);
					}
				}
			}
			@Override
			public void onFailure(Throwable ex) {
				if ( SocatUploadDashboard.isCurrentPage(singleton) ) {
					singleton.resetImageUrls();
					SocatUploadDashboard.showFailureMessage(PLOT_GENERATION_FAILURE_HTML, ex);
				}
			}
		};

		titleLabel.setText(TITLE_TEXT);
		logoutButton.setText(LOGOUT_TEXT);

		dismissButton.setText(DISMISS_TEXT);

		// Set text alternative for the images
		latVsLonImage.setAltText(LAT_VS_LON_ALT_TEXT);
		latLonVsNumImage.setAltText(LAT_LON_VS_NUM_ALT_TEXT);
		dayYearVsNumImage.setAltText(DAY_YEAR_VS_NUM_ALT_TEXT);
		timeSeriesImage.setAltText(TIME_SERIES_ALT_TEXT);
		pressuresVsNumImage.setAltText(PRESSURES_VS_NUM_ALT_TEXT);
		temperaturesVsNumImage.setAltText(TEMPERATURES_VS_NUM_ALT_TEXT);
		xco2sVsNumImage.setAltText(XCO2_VS_NUM_ALT_TEXT);
		salinitiesVsNumImage.setAltText(SALINITY_VS_NUM_ALT_TEXT);
		fco2recVsNumImage.setAltText(FCO2REC_VS_NUM_ALT_TEXT);
		fco2recVsSstImage.setAltText(FCO2REC_VS_SST_ALT_TEXT);
		fco2recVsSalImage.setAltText(FCO2REC_VS_SAL_ALT_TEXT);
		fco2recVsFco2Image.setAltText(FCO2REC_VS_FCO2_ALT_TEXT);
		fco2recSrcHistogramImage.setAltText(FCO2REC_SRC_HIST_ALT_TEXT);
	}

	/**
	 * Display the preview page in the RootLayoutPanel with data plots  
	 * for the first cruise in the given cruiseList.  
	 * Adds this page to the page history.
	 */
	static void showPage(DashboardCruiseList cruiseList) {
		if ( singleton == null )
			singleton = new CruisePreviewPage();
		SocatUploadDashboard.updateCurrentPage(singleton);
		singleton.updatePreviewPlots(cruiseList.keySet().iterator().next(), 
									 cruiseList.getUsername());
		History.newItem(PagesEnum.PREVIEW_CRUISE.name(), false);
	}

	/**
	 * Redisplays the last version of this page if the username
	 * associated with this page matches the given username.
	 */
	static void redisplayPage(String username) {
		if ( (username == null) || username.isEmpty() || 
			 (singleton == null) || ! singleton.getUsername().equals(username) ) {
			CruiseListPage.showPage();
		}
		else {
			SocatUploadDashboard.updateCurrentPage(singleton);
		}
	}

	/**
	 * Updates the this page with the plots for the indicated cruise.
	 * 
	 * @param expocode
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
			service.buildPreviewImages(getUsername(), this.expocode, true, checkStatusCallback);
		}
		// Set the URLs for the images.
		resetImageUrls();
	}

	/**
	 * Assigns the URLs to the images.  This triggers load events so the page should
	 * refresh when this is called.
	 */
	private void resetImageUrls() {
		String imagePrefix;
		if ( this.expocode.length() > 11 ) {
			imagePrefix = this.expocode.substring(0,4) + "/" + this.expocode + "_";
		}
		else {
			imagePrefix = "invalid/invalid_";
		}
		latVsLonImage.setUrl(UriUtils.fromString(imagePrefix + LAT_VS_LON_IMAGE_SUFFIX));
		latLonVsNumImage.setUrl(UriUtils.fromString(imagePrefix + LAT_LON_VS_NUM_IMAGE_SUFFIX));
		dayYearVsNumImage.setUrl(UriUtils.fromString(imagePrefix + DAY_YEAR_VS_NUM_IMAGE_SUFFIX));
		timeSeriesImage.setUrl(UriUtils.fromString(imagePrefix + TIME_SERIES_IMAGE_SUFFIX));
		pressuresVsNumImage.setUrl(UriUtils.fromString(imagePrefix + PRESSURES_VS_NUM_IMAGE_SUFFIX));
		temperaturesVsNumImage.setUrl(UriUtils.fromString(imagePrefix + TEMPERATURES_VS_NUM_IMAGE_SUFFIX));
		xco2sVsNumImage.setUrl(UriUtils.fromString(imagePrefix + XCO2_VS_NUM_IMAGE_SUFFIX));
		salinitiesVsNumImage.setUrl(UriUtils.fromString(imagePrefix + SALINITY_VS_NUM_IMAGE_SUFFIX));
		fco2recVsNumImage.setUrl(UriUtils.fromString(imagePrefix + FCO2REC_VS_NUM_IMAGE_SUFFIX));
		fco2recVsSstImage.setUrl(UriUtils.fromString(imagePrefix + FCO2REC_VS_SST_IMAGE_SUFFIX));
		fco2recVsSalImage.setUrl(UriUtils.fromString(imagePrefix + FCO2REC_VS_SAL_IMAGE_SUFFIX));
		fco2recVsFco2Image.setUrl(UriUtils.fromString(imagePrefix + FCO2REC_VS_FCO2_IMAGE_SUFFIX));
		fco2recSrcHistogramImage.setUrl(UriUtils.fromString(imagePrefix + FCO2REC_SRC_HIST_IMAGE_SUFFIX));
	}

	@UiHandler("logoutButton")
	void logoutOnClick(ClickEvent event) {
		DashboardLogoutPage.showPage();
	}

	@UiHandler("dismissButton")
	void cancelOnClick(ClickEvent event) {
		// Change to the latest cruise listing page.
		CruiseListPage.showPage();
	}

}
