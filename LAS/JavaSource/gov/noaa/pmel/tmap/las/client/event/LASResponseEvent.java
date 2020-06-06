/**
 * 
 */
package gov.noaa.pmel.tmap.las.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;

/**
 * A {@link GwtEvent} that informs listeners of a LAS Response.
 * Listeners must have a class that implements (or implement themselves) the
 * {@link LASResponseEvent.Handler} interface.
 * 
 * @author weusijana
 * 
 */
public class LASResponseEvent extends GwtEvent<LASResponseEvent.Handler> {

    public interface Handler extends EventHandler {
        void onResponse(LASResponseEvent event);
    }

    public static final Type<LASResponseEvent.Handler> TYPE = new Type<LASResponseEvent.Handler>();

	private String callbackObjectName;

    private String callerObjectName;

	private Throwable exception;

	private boolean isResponseReceived = true;
	
	private Request request;
	
	private Response response;

    public LASResponseEvent(Request request, Response response,
			String callerObjectName, String callbackObjectName) {
		this.request = request;
		this.response = response;
		this.callerObjectName = callerObjectName;
        this.callbackObjectName = callbackObjectName;
        this.setResponseReceived(true);
	}

	public LASResponseEvent(Request request, Throwable exception,
			String callerObjectName, String callbackObjectName) {
		this.request = request;
		this.exception = exception;
        this.callbackObjectName = callbackObjectName;
        this.setResponseReceived(false);
	}

	@Override
    protected void dispatch(LASResponseEvent.Handler handler) {
        handler.onResponse(this);
    }

    @Override
    public final Type<LASResponseEvent.Handler> getAssociatedType() {
        return TYPE;
    }

	/**
	 * @return the callbackObjectName
	 */
	public String getCallbackObjectName() {
		return callbackObjectName;
	}

	/**
	 * @return the callerObjectName
	 */
	public String getCallerObjectName() {
		return callerObjectName;
	}

	/**
	 * @return the exception
	 */
	public Throwable getException() {
		return exception;
	}

	/**
	 * @return the request
	 */
	public Request getRequest() {
		return request;
	}

	/**
	 * @return the response
	 */
	public Response getResponse() {
		return response;
	}

	/**
	 * @return the isResponseReceived
	 */
	public boolean isResponseReceived() {
		return isResponseReceived;
	}

	/**
	 * @param callbackObjectName the name of the callback object
	 */
	void setCallbackObjectName(String callbackObjectName) {
		this.callbackObjectName = callbackObjectName;
	}

	/**
	 * @param callerObjectName the callerObjectName to set
	 */
	void setCallerObjectName(String callerObjectName) {
		this.callerObjectName = callerObjectName;
	}

	/**
	 * @param exception the exception to set
	 */
	void setException(Throwable exception) {
		this.exception = exception;
	}

	/**
	 * @param request the request to set
	 */
	void setRequest(Request request) {
		this.request = request;
	}

	/**
	 * @param response the response to set
	 */
	void setResponse(Response response) {
		this.response = response;
	}

	/**
	 * @param isResponseReceived the isResponseReceived to set
	 */
	void setResponseReceived(boolean isResponseReceived) {
		this.isResponseReceived = isResponseReceived;
	}

}
