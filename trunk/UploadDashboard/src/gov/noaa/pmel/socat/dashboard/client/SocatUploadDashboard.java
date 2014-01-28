package gov.noaa.pmel.socat.dashboard.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.UIObject;

public class SocatUploadDashboard implements EntryPoint, ValueChangeHandler<String> {

	/**
	 * Enumerated type to specify pages for browser history.
	 */
	public enum PagesEnum {
		/** History tag for DashboardLoginPage */
		LOGIN,
		/** History tag for CruiseListPage */
		CRUISE_LIST,
		/** History tag for CruiseUploadPage */
		CRUISE_UPLOAD,
		/** History tag for DataColumnSpecsPage */
		DATA_COLUMN_SPECS,
		/** History tag for AddlDocsManagerPage */
		ADDL_DOCS_MANAGER,
		/** History tag for AddlDocsUploadPage */
		ADDL_DOCS_UPLOAD,
		/** History tag for AddToSocatPage */
		ADD_TO_SOCAT,
		/** History tag for DashboardLogoutPage */
		LOGOUT
	}

	// Column widths in em's
	static double CHECKBOX_COLUMN_WIDTH = 2.5;
	static double NARROW_COLUMN_WIDTH = 5.0;
	static double NORMAL_COLUMN_WIDTH = 9.0;
	static double FILENAME_COLUMN_WIDTH = 12.0;

	// Singleton instance of this object
	private static SocatUploadDashboard singleton = null;

	// Keep a record of the currently displayed page
	private Composite currentPage;
	// PopupPanel for displaying messages 
	private DashboardInfoPopup msgPopup;

	/**
	 * Create the manager for the SocatUploadDashboard pages.
	 * Do not use this constructor; instead use the static
	 * methods provided to display pages and messages.
	 */
	SocatUploadDashboard() {
		// Just in case this gets called more than once, 
		// remove any recorded page in the previous instantiation
		if ( (singleton != null) && (singleton.currentPage != null) ) {
			RootLayoutPanel.get().remove(singleton.currentPage);
			singleton.currentPage = null;
		}
		currentPage = null;
		msgPopup = null;
		// Make sure singleton is assign to this instance since 
		// this constructor is probably called from GWT.
		singleton = this;
	}

	/**
	 * Shows the message in a popup panel centered on the page.
	 * 
	 * @param htmlMsg
	 * 		unchecked HTML message to show.
	 */
	public static void showMessage(String htmlMsg) {
		if ( singleton == null )
			singleton = new SocatUploadDashboard();
		if ( singleton.msgPopup == null )
			singleton.msgPopup = new DashboardInfoPopup();
		singleton.msgPopup.setInfoMessage(htmlMsg);
		singleton.msgPopup.showCentered();
	}

	/**
	 * Shows the message in a popup panel relative to the given UI obect. 
	 * See {@link PopupPanel#showRelativeTo(UIObject)}. 
	 * 
	 * @param htmlMsg
	 * 		unchecked HTML message to show.
	 * @param obj
	 * 		show the message relative to this object
	 * 		(usually underneath, left-aligned)
	 */
	public static void showMessageAt(String htmlMsg, UIObject obj) {
		if ( singleton == null )
			singleton = new SocatUploadDashboard();
		if ( singleton.msgPopup == null )
			singleton.msgPopup = new DashboardInfoPopup();
		singleton.msgPopup.setInfoMessage(htmlMsg);
		singleton.msgPopup.showRelativeTo(obj);
	}

	/**
	 * Shows an error message, along with the message from an
	 * exception, in a popup panel centered on the page.
	 * 
	 * @param htmlMsg
	 * 		unchecked HTML message to show before the exception message
	 * @param ex
	 * 		exception whose message is to be shown
	 */
	public static void showFailureMessage(String htmlMsg, Throwable ex) {
		String exceptMsg = ex.getMessage();
		if ( exceptMsg == null )
			exceptMsg = htmlMsg;
		else if ( exceptMsg.contains("</pre>") )
			exceptMsg = htmlMsg + "<br /><pre>" + 
					SafeHtmlUtils.htmlEscape(exceptMsg) + "</pre>";
		else
			exceptMsg = htmlMsg + "<br /><pre>" + exceptMsg + "</pre>";
		SocatUploadDashboard.showMessage(exceptMsg);
	}

	/**
	 * Updates the displayed page by removing any page 
	 * currently being shown and adding the given page.
	 * 
	 * @param newPage
	 * 		new page to be shown; if null, not page is shown
	 */
	public static void updateCurrentPage(Composite newPage) {
		if ( singleton == null )
			singleton = new SocatUploadDashboard();
		if ( singleton.currentPage != null )
			RootLayoutPanel.get().remove(singleton.currentPage);
		singleton.currentPage = newPage;
		if ( singleton.currentPage != null )
			RootLayoutPanel.get().add(singleton.currentPage);
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
			DashboardLoginPage.showPage(true);
		}
		else if ( token.equals(PagesEnum.LOGIN.name()) ) {
			// Login page from history
			DashboardLoginPage.showPage(false);
		}
		else if ( token.equals(PagesEnum.CRUISE_LIST.name()) ) {
			// Cruise list page from history
			CruiseListPage.redisplayPage(false);
		}
		else if ( token.equals(PagesEnum.CRUISE_UPLOAD.name()) ) {
			// Cruise upload page from history
			CruiseUploadPage.redisplayPage(false);
		}
		else if ( token.equals(PagesEnum.DATA_COLUMN_SPECS.name()) ) {
			// Data column specs page from history
			DataColumnSpecsPage.redisplayPage(false);
		}
		else if ( token.equals(PagesEnum.ADDL_DOCS_MANAGER.name()) ) {
			// Metadata manager page from history
			AddlDocsManagerPage.redisplayPage(false);
		}
		else if ( token.equals(PagesEnum.ADDL_DOCS_UPLOAD.name()) ) {
			// Metadata upload page from history
			AddlDocsUploadPage.redisplayPage(false);
		}
		else if ( token.equals(PagesEnum.ADD_TO_SOCAT.name()) ) {
			// Add to SOCAT page from history
			AddToSocatPage.redisplayPage(false);
		}
		else if ( token.equals(PagesEnum.LOGOUT.name()) ) {
			// Logout page from history
			DashboardLogoutPage.redisplayPage(false);
		}
		else {
			// Unknown page from the history; instead show the login page 
			DashboardLoginPage.showPage(true);
		}
	}

}
