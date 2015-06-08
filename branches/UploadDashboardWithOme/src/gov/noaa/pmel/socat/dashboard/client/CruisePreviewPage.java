/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import gov.noaa.pmel.socat.dashboard.client.SocatUploadDashboard.PagesEnum;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList;
import gov.noaa.pmel.socat.dashboard.shared.DashboardServicesInterface;
import gov.noaa.pmel.socat.dashboard.shared.DashboardServicesInterfaceAsync;

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
			"Plots of the dataset: ";

	private static final String REFRESH_TEXT = "Refresh plots";
	private static final String DISMISS_TEXT = "Done";

	private static final String PLOT_GENERATION_FAILURE_HTML = "<b>Problems generating the plot previews</b>";

	private static final String LAT_VS_LON_TAB_TEXT = "lat vs lon";
	private static final String LAT_LON_TAB_TEXT = "lat, lon";
	private static final String SAMPLE_VS_TIME_TAB_TEXT = "sample num vs time";
	private static final String TIME_SERIES_TAB_TEXT = "time series";
	private static final String PRESSURES_TAB_TEXT = "pressures";
	private static final String TEMPERATURES_TAB_TEXT = "temperatures";
	private static final String SALINITIES_TAB_TEXT = "salinities";
	private static final String XCO2S_TAB_TEXT = "xCO<sub>2</sub>s";
	private static final String DT_XCO2_FCO2_TAB_TEXT = "dT, xCO<sub>2</sub>, fCO<sub>2</sub>";
	private static final String REC_FCO2_VS_TIME_TAB_TEXT = "rec fCO<sub>2</sub> vs time";
	private static final String REC_FCO2_VS_SST_TAB_TEXT = "rec fCO<sub>2</sub> vs SST";
	private static final String REC_FCO2_VS_SAL_TAB_TEXT = "rec fCO<sub>2</sub> vs sal";
	private static final String REC_FCO2_DELTA_TAB_TEXT = "rec fCO<sub>2</sub> - CO<sub>2</sub>";
	private static final String REC_FCO2_SOURCES_TAB_TEXT = "rec fCO<sub>2</sub> sources";

	private static final String LAT_VS_LON_ALT_TEXT = "latitude versus longitude";
	private static final String LAT_LON_ALT_TEXT = "latitude, longitude versus time";
	private static final String SAMPLE_VS_TIME_ALT_TEXT = "sample number (row number) versus time";
	private static final String TIME_SERIES_ALT_TEXT = "recommended fCO2, temperature, salinity, longitude, and latitude versus time";
	private static final String PRESSURES_ALT_TEXT = "pressures versus time";
	private static final String TEMPERATURES_ALT_TEXT = "temperatures versus time";
	private static final String SALINITIES_ALT_TEXT = "salinities versus time";
	private static final String XCO2S_ALT_TEXT = "xCO2 values versus time";
	private static final String DT_XCO2_FCO2_ALT_TEXT = "Teq minus SST, xCO2 @ Teq dry, and rec fCO2 versus time";
	private static final String REC_FCO2_VS_TIME_ALT_TEXT = "recommended fCO2 versus time";
	private static final String REC_CO2_VS_SST_ALT_TEXT = "recommended fCO2 versus temperature";
	private static final String REC_FCO2_VS_SAL_ALT_TEXT = "recommended fCO2 versus salinity";
	private static final String REC_FCO2_DELTA_ALT_TEXT = "recommended fCO2 minus reported CO2";
	private static final String REC_FCO2_SOURCES_ALT_TEXT = "histogram of source types of recommended fCO2";

	public static final String LAT_VS_LON_IMAGE_NAME = "lat_vs_lon";
	public static final String LAT_LON_IMAGE_NAME = "lat_lon";
	public static final String SAMPLE_VS_TIME_IMAGE_NAME = "sample_vs_time";
	public static final String TIME_SERIES_IMAGE_NAME = "time_series";
	public static final String PRESSURES_IMAGE_NAME = "pressures";
	public static final String TEMPERATURES_IMAGE_NAME = "temperatures";
	public static final String SALINITIES_IMAGE_NAME = "salinities";
	public static final String XCO2S_IMAGE_NAME = "xco2s";
	public static final String DT_XCO2_FCO2_IMAGE_NAME = "delta_temp_xco2_fco2";
	public static final String REC_FCO2_VS_TIME_IMAGE_NAME = "rec_fco2_vs_time";
	public static final String REC_FCO2_VS_SST_IMAGE_NAME = "rec_fco2_vs_sst";
	public static final String REC_FCO2_VS_SAL_IMAGE_NAME = "rec_fco2_vs_sal";
	public static final String REC_FCO2_DELTA_IMAGE_NAME = "rec_fco2_delta";
	public static final String REC_FCO2_SOURCES_IMAGE_NAME = "rec_fco2_sources";

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
	@UiField Button refreshButton;
	@UiField Button dismissButton;

	@UiField HTML latVsLonHtml;
	@UiField HTML latLonHtml;
	@UiField HTML sampleVsTimeHtml;
	@UiField HTML timeSeriesHtml;
	@UiField HTML pressuresHtml;
	@UiField HTML temperaturesHtml;
	@UiField HTML salinitiesHtml;
	@UiField HTML xco2sHtml;
	@UiField HTML dtXco2Fco2Html;
	@UiField HTML recFco2VsTimeHtml;
	@UiField HTML recFco2VsSstHtml;
	@UiField HTML recFco2VsSalHtml;
	@UiField HTML recFco2DeltaHtml;
	@UiField HTML recFco2SourcesHtml;

	@UiField Image latVsLonImage;
	@UiField Image latLonImage;
	@UiField Image sampleVsTimeImage;
	@UiField Image timeSeriesImage;
	@UiField Image pressuresImage;
	@UiField Image temperaturesImage;
	@UiField Image salinitiesImage;
	@UiField Image xco2sImage;
	@UiField Image dtXco2Fco2Image;
	@UiField Image recFco2VsTimeImage;
	@UiField Image recFco2VsSstImage;
	@UiField Image recFco2VsSalImage;
	@UiField Image recFco2DeltaImage;
	@UiField Image recFco2SourcesImage;

	String expocode;
	String timetag;
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
					if ( isDone ) {
						SocatUploadDashboard.showAutoCursor();
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
				if ( SocatUploadDashboard.isCurrentPage(singleton) ) {
					SocatUploadDashboard.showAutoCursor();
					singleton.resetImageUrls();
					SocatUploadDashboard.showFailureMessage(PLOT_GENERATION_FAILURE_HTML, ex);
				}
			}
		};

		titleLabel.setText(TITLE_TEXT);
		logoutButton.setText(LOGOUT_TEXT);

		refreshButton.setText(REFRESH_TEXT);
		dismissButton.setText(DISMISS_TEXT);

		// Set the HTML for the tabs
		latVsLonHtml.setHTML(LAT_VS_LON_TAB_TEXT);
		latLonHtml.setHTML(LAT_LON_TAB_TEXT);
		sampleVsTimeHtml.setHTML(SAMPLE_VS_TIME_TAB_TEXT);
		timeSeriesHtml.setHTML(TIME_SERIES_TAB_TEXT);
		pressuresHtml.setHTML(PRESSURES_TAB_TEXT);
		temperaturesHtml.setHTML(TEMPERATURES_TAB_TEXT);
		salinitiesHtml.setHTML(SALINITIES_TAB_TEXT);
		xco2sHtml.setHTML(XCO2S_TAB_TEXT);
		dtXco2Fco2Html.setHTML(DT_XCO2_FCO2_TAB_TEXT);
		recFco2VsTimeHtml.setHTML(REC_FCO2_VS_TIME_TAB_TEXT);
		recFco2VsSstHtml.setHTML(REC_FCO2_VS_SST_TAB_TEXT);
		recFco2VsSalHtml.setHTML(REC_FCO2_VS_SAL_TAB_TEXT);
		recFco2DeltaHtml.setHTML(REC_FCO2_DELTA_TAB_TEXT);
		recFco2SourcesHtml.setHTML(REC_FCO2_SOURCES_TAB_TEXT);

		// Set hover helps for the tabs
		latVsLonHtml.setTitle(LAT_VS_LON_ALT_TEXT);
		latLonHtml.setTitle(LAT_LON_ALT_TEXT);
		sampleVsTimeHtml.setTitle(SAMPLE_VS_TIME_ALT_TEXT);
		timeSeriesHtml.setTitle(TIME_SERIES_ALT_TEXT);
		pressuresHtml.setTitle(PRESSURES_ALT_TEXT);
		temperaturesHtml.setTitle(TEMPERATURES_ALT_TEXT);
		salinitiesHtml.setTitle(SALINITIES_ALT_TEXT);
		xco2sHtml.setTitle(XCO2S_ALT_TEXT);
		dtXco2Fco2Html.setTitle(DT_XCO2_FCO2_ALT_TEXT);
		recFco2VsTimeHtml.setTitle(REC_FCO2_VS_TIME_ALT_TEXT);
		recFco2VsSstHtml.setTitle(REC_CO2_VS_SST_ALT_TEXT);
		recFco2VsSalHtml.setTitle(REC_FCO2_VS_SAL_ALT_TEXT);
		recFco2DeltaHtml.setTitle(REC_FCO2_DELTA_ALT_TEXT);
		recFco2SourcesHtml.setTitle(REC_FCO2_SOURCES_ALT_TEXT);

		// Set text alternative for the images
		latVsLonImage.setAltText(LAT_VS_LON_ALT_TEXT);
		latLonImage.setAltText(LAT_LON_ALT_TEXT);
		sampleVsTimeImage.setAltText(SAMPLE_VS_TIME_ALT_TEXT);
		timeSeriesImage.setAltText(TIME_SERIES_ALT_TEXT);
		pressuresImage.setAltText(PRESSURES_ALT_TEXT);
		temperaturesImage.setAltText(TEMPERATURES_ALT_TEXT);
		salinitiesImage.setAltText(SALINITIES_ALT_TEXT);
		xco2sImage.setAltText(XCO2S_ALT_TEXT);
		dtXco2Fco2Image.setAltText(DT_XCO2_FCO2_ALT_TEXT);
		recFco2VsTimeImage.setAltText(REC_FCO2_VS_TIME_ALT_TEXT);
		recFco2VsSstImage.setAltText(REC_CO2_VS_SST_ALT_TEXT);
		recFco2VsSalImage.setAltText(REC_FCO2_VS_SAL_ALT_TEXT);
		recFco2DeltaImage.setAltText(REC_FCO2_DELTA_ALT_TEXT);
		recFco2SourcesImage.setAltText(REC_FCO2_SOURCES_ALT_TEXT);

		// Set hover helps for the images
		latVsLonImage.setTitle(LAT_VS_LON_ALT_TEXT);
		latLonImage.setTitle(LAT_LON_ALT_TEXT);
		sampleVsTimeImage.setTitle(SAMPLE_VS_TIME_ALT_TEXT);
		timeSeriesImage.setTitle(TIME_SERIES_ALT_TEXT);
		pressuresImage.setTitle(PRESSURES_ALT_TEXT);
		temperaturesImage.setTitle(TEMPERATURES_ALT_TEXT);
		salinitiesImage.setTitle(SALINITIES_ALT_TEXT);
		xco2sImage.setTitle(XCO2S_ALT_TEXT);
		dtXco2Fco2Image.setTitle(DT_XCO2_FCO2_ALT_TEXT);
		recFco2VsTimeImage.setTitle(REC_FCO2_VS_TIME_ALT_TEXT);
		recFco2VsSstImage.setTitle(REC_CO2_VS_SST_ALT_TEXT);
		recFco2VsSalImage.setTitle(REC_FCO2_VS_SAL_ALT_TEXT);
		recFco2DeltaImage.setTitle(REC_FCO2_DELTA_ALT_TEXT);
		recFco2SourcesImage.setTitle(REC_FCO2_SOURCES_ALT_TEXT);
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
			SocatUploadDashboard.showWaitCursor();
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
		timeSeriesImage.setUrl(UriUtils.fromString(imagePrefix + TIME_SERIES_IMAGE_NAME + imageSuffix));
		pressuresImage.setUrl(UriUtils.fromString(imagePrefix + PRESSURES_IMAGE_NAME + imageSuffix));
		temperaturesImage.setUrl(UriUtils.fromString(imagePrefix + TEMPERATURES_IMAGE_NAME + imageSuffix));
		salinitiesImage.setUrl(UriUtils.fromString(imagePrefix + SALINITIES_IMAGE_NAME + imageSuffix));
		xco2sImage.setUrl(UriUtils.fromString(imagePrefix + XCO2S_IMAGE_NAME + imageSuffix));
		dtXco2Fco2Image.setUrl(UriUtils.fromString(imagePrefix + DT_XCO2_FCO2_IMAGE_NAME + imageSuffix));
		recFco2VsTimeImage.setUrl(UriUtils.fromString(imagePrefix + REC_FCO2_VS_TIME_IMAGE_NAME + imageSuffix));
		recFco2VsSstImage.setUrl(UriUtils.fromString(imagePrefix + REC_FCO2_VS_SST_IMAGE_NAME + imageSuffix));
		recFco2VsSalImage.setUrl(UriUtils.fromString(imagePrefix + REC_FCO2_VS_SAL_IMAGE_NAME + imageSuffix));
		recFco2DeltaImage.setUrl(UriUtils.fromString(imagePrefix + REC_FCO2_DELTA_IMAGE_NAME + imageSuffix));
		recFco2SourcesImage.setUrl(UriUtils.fromString(imagePrefix + REC_FCO2_SOURCES_IMAGE_NAME + imageSuffix));
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
		CruiseListPage.showPage();
	}

}
