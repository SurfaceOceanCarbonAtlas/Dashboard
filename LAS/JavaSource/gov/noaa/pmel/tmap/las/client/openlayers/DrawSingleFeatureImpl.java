
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
 */package gov.noaa.pmel.tmap.las.client.openlayers;

import gov.noaa.pmel.tmap.las.client.openlayers.DrawSingleFeature.FeatureAddedListener;

import org.gwtopenmaps.openlayers.client.util.JSObject;

/**
 *
 * @author Erdem Gunay
 * @author Edwin Commandeur - Atlis EJS
 *
 */
class DrawSingleFeatureImpl {

	public static native JSObject create(JSObject layer, JSObject handler)/*-{
		return new $wnd.OpenLayers.Control.DrawSingleFeature(layer, handler);
	}-*/;

	public static native JSObject create(JSObject layer, JSObject handler, JSObject options)/*-{
		return new $wnd.OpenLayers.Control.DrawSingleFeature(layer, handler, options);
	}-*/;

	public static native JSObject createFeatureAddedCallback(FeatureAddedListener listener)/*-{
		var callback = function(obj){
			var vectorFeatureObj = @org.gwtopenmaps.openlayers.client.feature.VectorFeature::narrowToVectorFeature(Lorg/gwtopenmaps/openlayers/client/util/JSObject;)(obj);
			listener.@gov.noaa.pmel.tmap.las.client.openlayers.DrawSingleFeature.FeatureAddedListener::onFeatureAdded(Lorg/gwtopenmaps/openlayers/client/feature/VectorFeature;)(vectorFeatureObj);
		}
		return callback;
	}-*/;

}
