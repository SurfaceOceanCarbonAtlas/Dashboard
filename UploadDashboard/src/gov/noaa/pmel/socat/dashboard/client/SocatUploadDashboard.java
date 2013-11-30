package gov.noaa.pmel.socat.dashboard.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootLayoutPanel;

public class SocatUploadDashboard 
					implements EntryPoint, ValueChangeHandler<String> {

	/**
	 * Enumerated type to specify pages for browser history.
	 */
	public enum PagesEnum {
		/** History tag for DashboardLoginPage */
		LOGIN,
		/** History tag for DashboardLogoutPage */
		LOGOUT,
		/** History tag for DashboardCruiseListPage */
		CRUISE_LIST,
		/** History tag for DashboardCruiseUploadPage */
		CRUISE_UPLOAD,
		/** History tag for CruiseDataColumnSpecsPage */
		DATA_COLUMN_SPECS,
		/** History tag for DashboardMetadataListPage */
		METADATA_LIST,
		/** History tag for DashboardMetadataUploadPage */
		METADATA_UPLOAD,
	}

	// Singleton instance of this object
	private static SocatUploadDashboard singleton = null;

	// Keep a record of the currently displayed page
	private Composite currentPage = null;

	/**
	 * Create the manager for the SocatUploadDashboard pages.
	 * Do not use this constructor; instead use the static
	 * get() method to obtain the singleton instance of this
	 * class.
	 */
	private SocatUploadDashboard() {
		// Just in case this gets called more than once, 
		// remove any recorded page in the previous instantiation
		if ( (singleton != null) && (singleton.currentPage != null) ) {
			RootLayoutPanel.get().remove(singleton.currentPage);
			singleton.currentPage = null;
		}
		currentPage = null;
		// Make sure singleton is assign to this instance since 
		// this constructor is probably called from GWT.
		singleton = this;
	}

	/**
	 * @return
	 * 		the singleton instance of the SocatUploadDashboard
	 * 		page manager.
	 */
	public static SocatUploadDashboard get() {
		if ( singleton == null )
			singleton = new SocatUploadDashboard();
		return singleton;
	}

	/**
	 * Updates the displayed page by removing any page 
	 * currently being shown and adding the given page.
	 * 
	 * @param newPage
	 * 		new page to be shown; if null, not page is shown
	 */
	public void updateCurrentPage(Composite newPage) {
		if ( currentPage != null )
			RootLayoutPanel.get().remove(currentPage);
		currentPage = newPage;
		if ( currentPage != null )
			RootLayoutPanel.get().add(currentPage);
	}

	@Override
	public void onModuleLoad() {
		// setup history management
		History.addValueChangeHandler(this);
		// show the appropriate page - if new, then the login page
		History.fireCurrentHistoryState();
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		String token = event.getValue();
		if ( token != null )
			token = token.trim();
		if ( (token == null) || token.isEmpty() ) {
			// Initial history setup; show the login page
			DashboardLoginPage.showPage();
		}
		else if ( token.equals(PagesEnum.LOGIN.name()) ) {
			// Login page from history
			DashboardLoginPage.redisplayPage();
		}
		else if ( token.equals(PagesEnum.LOGOUT.name()) ) {
			// Logout page from history
			DashboardLogoutPage.redisplayPage();
		}
		else if ( token.equals(PagesEnum.CRUISE_LIST.name()) ) {
			// Cruise list page from history
			DashboardCruiseListPage.redisplayPage();
		}
		else if ( token.equals(PagesEnum.CRUISE_UPLOAD.name()) ) {
			// Cruise upload page from history
			DashboardCruiseUploadPage.redisplayPage();
		}
		else if ( token.equals(PagesEnum.DATA_COLUMN_SPECS.name()) ) {
			// Data column specs page from history
			CruiseDataColumnSpecsPage.redisplayPage();
		}
		else if ( token.equals(PagesEnum.METADATA_LIST.name()) ) {
			// Metadata list page from history
			DashboardMetadataListPage.redisplayPage();
		}
		else if ( token.equals(PagesEnum.METADATA_UPLOAD.name()) ) {
			// Metadata upload page from history
			DashboardMetadataUploadPage.redisplayPage();
		}
		else {
			// Unknown page from the history; instead show the login page 
			DashboardLoginPage.showPage();
		}
	}

}
