/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import org.moxieapps.gwt.uploader.client.File;
import org.moxieapps.gwt.uploader.client.events.UploadProgressEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Represents an entry for a file to be uploaded, 
 * that is uploading, or that has been uploaded.
 * 
 * @author Karl Smith
 */
public class FileUploadEntry extends Composite {

	interface FileUploadEntryUiBinder extends UiBinder<Widget, FileUploadEntry> {
	}

	private static FileUploadEntryUiBinder uiBinder = 
			GWT.create(FileUploadEntryUiBinder.class);

	@UiField Button statusButton;
	@UiField InlineLabel nameLabel;

	File uploadFile;
	HandlerRegistration clickRegistration;
	boolean inProgress;

	/**
	 * Creates an file display widget for the given File.
	 * 
	 * @param uploadFile
	 * 		file to be displayed
	 */
	FileUploadEntry(File uploadFile) {
		initWidget(uiBinder.createAndBindUi(this));

		this.uploadFile = uploadFile;
		inProgress = false;
		statusButton.setText("Remove");
		nameLabel.setText(uploadFile.getName());
		clickRegistration = statusButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Remove the click handler 
				if ( clickRegistration != null ) {
					clickRegistration.removeHandler();
					clickRegistration = null;
				}
				// Remove this entry from the list on the parent page
				if ( FileUploadEntry.this.uploadFile != null ) {
					CruiseUploadPage.removeUploadFile(
							FileUploadEntry.this.uploadFile, inProgress);
					FileUploadEntry.this.uploadFile = null;
				}
			}
		});
	}

	/**
	 * Resets the label in the status button to show the upload has started.
	 */
	void showUploadStarted() {
		inProgress = true;
		statusButton.setText(NumberFormat.getPercentFormat().format(0.0));
	}

	/**
	 * Resets the label in the status button to show the upload progress.
	 * 
	 * @param evnt
	 * 		get the progress from this event
	 */
	void showProgress(UploadProgressEvent evnt) {
		inProgress = true;
		statusButton.setText(NumberFormat.getPercentFormat().format(
				evnt.getBytesComplete() / (double) evnt.getBytesTotal()));
	}

	/**
	 * Reset the label in the status button to show the upload is complete.
	 */
	void showUploadDone() {
		inProgress = false;
		statusButton.setText("Done");
	}

	/**
	 * Reset the label in the status button to show the upload failed.
	 */
	void showUploadFailed() {
		inProgress = false;
		statusButton.setText("Failed");
	}

}
