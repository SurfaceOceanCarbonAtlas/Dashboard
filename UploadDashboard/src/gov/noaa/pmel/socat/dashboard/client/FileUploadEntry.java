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

	/**
	 * Creates an file display widget for the given File.
	 * 
	 * @param uploadFile
	 * 		file to be displayed
	 */
	FileUploadEntry(File uploadFile) {
		initWidget(uiBinder.createAndBindUi(this));

		this.uploadFile = uploadFile;
		statusButton.setText("Remove");
		nameLabel.setText(uploadFile.getName());
		clickRegistration = statusButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if ( clickRegistration != null ) {
					clickRegistration.removeHandler();
					clickRegistration = null;
				}
				CruiseUploadPage.removeUploadFile(FileUploadEntry.this.uploadFile);
				FileUploadEntry.this.uploadFile = null;
			}
		});
	}

	/**
	 * Resets the label in the status button to show the upload has started.
	 * Also removes the click handler for the status button and the internal
	 * reference to the file passed in the constructor. 
	 */
	void showUploadStarted() {
		if ( clickRegistration != null )
			clickRegistration.removeHandler();
		clickRegistration = null;
		uploadFile = null;
		statusButton.setText(NumberFormat.getPercentFormat().format(0.0));
	}

	/**
	 * Resets the label in the status button to show the upload progress.
	 * 
	 * @param evnt
	 * 		get the progress from this event
	 */
	void showProgress(UploadProgressEvent evnt) {
		// Update the progress label
		statusButton.setText(NumberFormat.getPercentFormat().format(
				evnt.getBytesComplete() / (double) evnt.getBytesTotal()));
	}

	/**
	 * Reset the label in the status button to show the upload is complete.
	 */
	void showUploadDone() {
		statusButton.setText("Done");
	}

	/**
	 * Reset the label in the status button to show the upload failed.
	 */
	void showUploadFailed() {
		statusButton.setText("Failed");
	}

}
