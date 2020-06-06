/**
 * 
 */
package gov.noaa.pmel.tmap.las.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.http.client.RequestBuilder;

/**
 * A {@link GwtEvent} that informs listeners of a LAS Request URL url change.
 * Listeners must have a class that implements (or implement themselves) the
 * {@link LASRequestEvent.Handler} interface.
 * 
 * If the method is not explicitly set, the requests are {@link RequestBuilder.GET} by default.
 * 
 * @author weusijana
 * 
 */
public class LASRequestEvent extends GwtEvent<LASRequestEvent.Handler> {

	public interface Handler extends EventHandler {
		void onRequest(LASRequestEvent event);
	}

	public static final Type<LASRequestEvent.Handler> TYPE = new Type<LASRequestEvent.Handler>();

	private RequestBuilder.Method method = RequestBuilder.GET;
	private RequestBuilder requestBuilder = null;
	private String requestCallbackObjectName = null;
	private String sourceName;
	private String url = null;

	public LASRequestEvent(RequestBuilder requestBuilder,
			String requestCallbackObjectName, String sourceName) {
		this.requestBuilder = requestBuilder;
		this.requestCallbackObjectName = requestCallbackObjectName;
		this.url = requestBuilder.getUrl();
		this.sourceName = sourceName;
	}

	public LASRequestEvent(String url, RequestBuilder.Method method,
			String requestCallbackObjectName, String sourceName) {
		this.url = url;
		this.method = method;
		this.requestCallbackObjectName = requestCallbackObjectName;
		this.requestBuilder = new RequestBuilder(this.method, this.url);
		this.sourceName = sourceName;
	}

	public LASRequestEvent(String url, String requestCallbackObjectName, String sourceName) {
		this.url = url;
		this.requestCallbackObjectName = requestCallbackObjectName;
		this.requestBuilder = new RequestBuilder(this.method, this.url);
		this.sourceName = sourceName;
	}

	@Override
	protected void dispatch(LASRequestEvent.Handler handler) {
		handler.onRequest(this);
	}

	@Override
	public final Type<LASRequestEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * @return the method
	 */
	public RequestBuilder.Method getMethod() {
		return method;
	}

	/**
	 * @return the requestBuilder
	 */
	public RequestBuilder getRequestBuilder() {
		return requestBuilder;
	}

	/**
	 * @return the requestCallbackObjectName
	 */
	public String getRequestCallbackObjectName() {
		return requestCallbackObjectName;
	}

	/**
	 * @return the sourceName
	 */
	public String getSourceName() {
		return sourceName;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param method
	 *            the method to set
	 */
	void setMethod(RequestBuilder.Method method) {
		this.method = method;
		this.requestBuilder = new RequestBuilder(this.method, this.url);
	}

	/**
	 * @param requestBuilder
	 *            the requestBuilder to set
	 */
	void setRequestBuilder(RequestBuilder requestBuilder) {
		this.requestBuilder = requestBuilder;
		this.url = this.requestBuilder.getUrl();
		String httpMethod = this.requestBuilder.getHTTPMethod();
		if (RequestBuilder.GET.toString().equalsIgnoreCase(httpMethod)) {
			this.method = RequestBuilder.GET;
		} else if (RequestBuilder.POST.toString().equalsIgnoreCase(httpMethod)) {
			this.method = RequestBuilder.POST;
		} else if (RequestBuilder.DELETE.toString().equalsIgnoreCase(httpMethod)) {
			this.method = RequestBuilder.DELETE;
		} else if (RequestBuilder.HEAD.toString().equalsIgnoreCase(httpMethod)) {
			this.method = RequestBuilder.HEAD;
		} else if (RequestBuilder.PUT.toString().equalsIgnoreCase(httpMethod)) {
			this.method = RequestBuilder.PUT;
		}
	}

	/**
	 * @param requestCallbackObjectName
	 *            the requestCallbackObjectName to set
	 */
	void setRequestCallbackObjectName(String requestCallbackObjectName) {
		this.requestCallbackObjectName = requestCallbackObjectName;
	}

	/**
	 * @param sourceName the sourceName to set
	 */
	void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	void setUrl(String url) {
		this.url = url;
		this.requestBuilder = new RequestBuilder(this.method, this.url);
	}
}
