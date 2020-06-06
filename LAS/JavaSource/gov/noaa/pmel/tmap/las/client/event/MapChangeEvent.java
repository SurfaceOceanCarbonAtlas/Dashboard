package gov.noaa.pmel.tmap.las.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * This event should be fired when a reference map changes so other objects
 * that are managing maps can update themselves for the axes they are
 * responsible for keeping up-to-date.
 * 
 * @author rhs
 * 
 */
public class MapChangeEvent extends GwtEvent<MapChangeEvent.Handler> {

    public static final Type<MapChangeEvent.Handler> TYPE = new Type<MapChangeEvent.Handler>();
    
    double xlo;
    double xhi;
    double ylo;
    double yhi;

    public MapChangeEvent(double ylo, double yhi, double xlo, double xhi) {
        super();
        this.xlo = xlo;
        this.xhi = xhi;
        this.ylo = ylo;
        this.yhi = yhi;
    }
    @Override
    protected void dispatch(MapChangeEvent.Handler handler) {
        handler.onMapSelectionChange(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<Handler> getAssociatedType() {
        return TYPE;
    }

    public interface Handler extends EventHandler {
        public void onMapSelectionChange(MapChangeEvent event);
    }

    public double getXlo() {
        return xlo;
    }
    public double getXhi() {
        return xhi;
    }
    public double getYlo() {
        return ylo;
    }
    public double getYhi() {
        return yhi;
    }
    
}
