/**
 * 
 */
package gov.noaa.pmel.tmap.las.client.util;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.RequestController;
import gov.noaa.pmel.tmap.las.client.event.LASResponseEvent;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

/**
 * Publishes {@link LASResponseEvent}s for multiple listeners when
 * onResponseReceived or onError is called.
 * 
 * @author weusijana
 * 
 */
public class MultiCallback implements RequestCallback {
	private RequestController controller;
	private String url = "";

	/**
	 * @param requestCallbackObjectName
	 * @param callbackPairs
	 */
	public MultiCallback(RequestController controller, String url,
			String firstSourceName, String firstCallbackName) {
		super();
		logger.setLevel(Level.OFF);
		this.controller = controller;
		this.url = url;
		this.add(firstSourceName, firstCallbackName);
	}

	public MultiCallback(RequestController controller) {
		super();
		logger.setLevel(Level.OFF);
		this.controller = controller;
	}

	private final Logger logger = Logger.getLogger(MultiCallback.class
			.getName());
	private ClientFactory clientFactory = GWT.create(ClientFactory.class);
	private EventBus eventBus = clientFactory.getEventBus();
	private ArrayList<ArrayList<String>> callbackPairs = new ArrayList<ArrayList<String>>();

	@Override
	public void onResponseReceived(Request request, Response response) {
		controller.done(this);
		for (ArrayList<String> callbackPair : callbackPairs) {
			LASResponseEvent lasResponseEvent = new LASResponseEvent(request,
					response, callbackPair.get(0), callbackPair.get(1));
			logger.info("MultiCallback is firing lasResponseEvent:"
					+ lasResponseEvent + "for callbackPair:" + callbackPair
					+ " with response:" + response);
			eventBus.fireEventFromSource(lasResponseEvent, this);
		}
	}

	@Override
	public void onError(Request request, Throwable exception) {
		controller.done(this);
		for (ArrayList<String> callbackPair : callbackPairs) {
			LASResponseEvent lasResponseEvent = new LASResponseEvent(request,
					exception, callbackPair.get(0), callbackPair.get(1));
			logger.info("MultiCallback is firing lasResponseEvent:"
					+ lasResponseEvent + "for callbackObjectName:"
					+ callbackPair + " with exception:" + exception);
			eventBus.fireEventFromSource(lasResponseEvent, this);
		}
	}

	/**
	 * @return
	 * @see java.util.ArrayList#size()
	 */
	public int size() {
		return callbackPairs.size();
	}

	/**
	 * @param pair
	 * @return
	 * @see java.util.ArrayList#contains(java.lang.Object)
	 */
	public boolean contains(ArrayList<String> pair) {
		return callbackPairs.contains(pair);
	}

	/**
	 * @param sourceName
	 *            name of the object that made the request
	 * @param callbackObjectName
	 *            name of the object to handle the response inside the source
	 *            object
	 * @return
	 * @see java.util.ArrayList#add(java.lang.Object)
	 */
	public boolean add(String sourceName, String callbackObjectName) {
		ArrayList<String> callbackPair = new ArrayList<String>(2);
		callbackPair.add(0, sourceName);
		callbackPair.add(1, callbackObjectName);

		// Don't bother adding duplicates
		if (this.callbackPairs.contains(callbackPair))
			return false;

		return this.callbackPairs.add(callbackPair);
	}

	/**
	 * @return
	 * @see java.util.AbstractCollection#toString()
	 */
	@Override
	public String toString() {
		return callbackPairs.toString();
	}

	/**
	 * @param pair
	 * @return
	 * @see java.util.ArrayList#remove(java.lang.Object)
	 */
	public boolean remove(ArrayList<String> pair) {
		return callbackPairs.remove(pair);
	}

	/**
	 * 
	 * @see java.util.ArrayList#clear()
	 */
	public void clear() {
		callbackPairs.clear();
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

}
