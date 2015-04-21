/**
 * Class: OpenLayers.Control.Geocoder
 * A wrapper around GClientGeocoder.
 */
OpenLayers.Control.Geocoder = OpenLayers.Class(OpenLayers.Control, {

    /**
     * Property: geocoder
     * {GClientGeocoder}
     */
    geocoder: null,

    /**
     * Constructor: OpenLayers.Control.Geocoder
     *
     * Parameters:
     * options - {Object} An optional object with properties to be set on the
     *     control.
     */
    initialize: function(options) {
    	
        OpenLayers.Control.prototype.initialize.apply(this, [options]);
        try {
            this.geocoder = new GClientGeocoder();
        } catch(err) {
            OpenLayers.Console.error("GClientGeocoder not available", err);
        }
    },
    
    /**
     * APIMethod: destroy
     * Clean up the control
     */
    destroy: function() {
        this.geocoder = null;
        OpenLayers.Control.prototype.destroy.apply(this, arguments);
    },
    
    /**
     * APIMethod: getLocation
     *
     * Parameters:
     * address - {String} An address string.
     * callback - {Function} A function to be called with the resulting
     *     <OpenLayers.LonLat>
     */
    getLocation: function(address, callback){
        if(!callback) {
            callback = function(lonlat) {
                alert(lonlat);
            }
        }
        
        var bound = OpenLayers.Function.bind(function(point) {
        	
            if(point){
                var lonlat = new OpenLayers.LonLat(point.lng(), point.lat());            
                lonlat.transform(
                    new OpenLayers.Projection("EPSG:4326"),
                    new OpenLayers.Projection(this.map.projection)
                );
                callback(lonlat);
            }
            else{
            	alert("Place not found");
            	}
        }, this);

        this.geocoder.getLatLng(address, bound);
    },

    CLASS_NAME: "OpenLayers.Control.Geocoder"
});
