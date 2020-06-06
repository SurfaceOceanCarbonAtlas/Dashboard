package gov.noaa.pmel.tmap.las.client.laswidget;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class PrintPanel extends Composite {
	String url;
	SimplePanel panel = new SimplePanel();
	public PrintPanel(String url) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		initWidget(panel);
		try {
			builder.sendRequest(null, fillPanelCallback);
		} catch (RequestException e) {
			HTMLPanel html = new HTMLPanel("<b>Unable to retrieve image.</b>");		
			panel.add(html);
		}
	}
    RequestCallback fillPanelCallback = new RequestCallback() {

		@Override
		public void onError(Request request, Throwable exception) {
			HTMLPanel html = new HTMLPanel("<b>Unable to retrieve image.</b>");
			panel.add(html);
		}

		@Override
		public void onResponseReceived(Request request, Response response) {
			HTMLPanel html = new HTMLPanel(UriUtils.fromString(response.getText()).asString());
			panel.add(html);
		}
    	
    };
}
