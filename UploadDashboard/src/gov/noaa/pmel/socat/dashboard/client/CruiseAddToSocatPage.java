/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Karl Smith
 *
 */
public class CruiseAddToSocatPage extends Composite {

	private static AddCruiseToSocatPageUiBinder uiBinder = 
			GWT.create(AddCruiseToSocatPageUiBinder.class);

	interface AddCruiseToSocatPageUiBinder 
			extends UiBinder<Widget, CruiseAddToSocatPage> {
	}

	public CruiseAddToSocatPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
