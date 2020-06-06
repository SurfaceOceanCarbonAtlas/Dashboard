package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.serializable.OperationSerializable;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

public class OperationPushButton extends PushButton {
   
	OperationSerializable operation;

	
	public OperationPushButton() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OperationPushButton(Image upImage, ClickHandler handler) {
		super(upImage, handler);
		// TODO Auto-generated constructor stub
	}

	public OperationPushButton(Image upImage, Image downImage,
			ClickHandler handler) {
		super(upImage, downImage, handler);
		// TODO Auto-generated constructor stub
	}

	public OperationPushButton(Image upImage, Image downImage) {
		super(upImage, downImage);
		// TODO Auto-generated constructor stub
	}

	public OperationPushButton(Image upImage) {
		super(upImage);
		// TODO Auto-generated constructor stub
	}

	public OperationPushButton(String upText, ClickHandler handler) {
		super(upText, handler);
		// TODO Auto-generated constructor stub
	}

	public OperationPushButton(String upText, String downText,
			ClickHandler handler) {
		super(upText, downText, handler);
		// TODO Auto-generated constructor stub
	}

	public OperationPushButton(String upText, String downText) {
		super(upText, downText);
		// TODO Auto-generated constructor stub
	}

	public OperationPushButton(String upText) {
		super(upText);
		// TODO Auto-generated constructor stub
	}

	public OperationSerializable getOperation() {
		return operation;
	}

	public void setOperation(OperationSerializable op) {
		this.operation = op;
	}

}
