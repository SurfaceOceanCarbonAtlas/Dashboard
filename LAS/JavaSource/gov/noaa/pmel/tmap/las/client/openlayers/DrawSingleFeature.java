/**
 * This software was developed by Roland Schweitzer of Weathertop Consulting, LLC 
 * (http://www.weathertopconsulting.com/) as part of work performed for
 * NOAA Contracts AB113R-04-RP-0068 and AB113R-09-CN-0182.  
 * 
 * The NOAA licensing terms are explained below.
 * 
 * 
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development. 
 */
package gov.noaa.pmel.tmap.las.client.openlayers;

import org.gwtopenmaps.openlayers.client.control.Control;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.handler.Handler;
import org.gwtopenmaps.openlayers.client.layer.Vector;
import org.gwtopenmaps.openlayers.client.util.JSObject;

/**
 *
 *  Don't be suprised by the following:
 *  Upon activating the DrawFeature tool it creates an extra layer
 *  that is used by the handler (the name of this layer reflects this).
 *
 * (tested in OpenLayers 2.7)
 *
 * @author Erdem Gunay
 * @author Edwin Commandeur - Atlis EJS
 *
 */
public class DrawSingleFeature extends Control {

	public interface FeatureAddedListener {
		void onFeatureAdded(VectorFeature vectorFeature);
	}

	protected DrawSingleFeature(JSObject element) {
		super(element);
	}

	/**
	 *
	 * @param layer
	 * @param handler - a PointHandler, PathHandler or PolygonHandler
	 */
	public DrawSingleFeature(Vector layer, Handler handler) {
		this(DrawSingleFeatureImpl.create(layer.getJSObject(), handler.getJSObject()));
	}

	/**
	 *
	 * @param layer
	 * @param handler - a PointHandler, PathHandler or PolygonHandler
	 * @param options - see {@link DrawSingleFeatureOptions}
	 */
	public DrawSingleFeature(Vector layer, Handler handler, DrawSingleFeatureOptions options){
		this(DrawSingleFeatureImpl.create(layer.getJSObject(), handler.getJSObject(), options.getJSObject()));
	}

}
