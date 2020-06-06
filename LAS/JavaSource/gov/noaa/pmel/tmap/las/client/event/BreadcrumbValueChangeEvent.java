/**
 * 
 */
package gov.noaa.pmel.tmap.las.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event for updating the bread crumb views that have a class
 * that implements (or implement themselves) the
 * {@link BreadcrumbValueChangeEvent.Handler} interface, such as those on other
 * {@link OutputPanel}s.
 * 
 * @author weusijana
 * 
 */
public class BreadcrumbValueChangeEvent extends GwtEvent<BreadcrumbValueChangeEvent.Handler> {

    public interface Handler extends EventHandler {
        void onValueChange(BreadcrumbValueChangeEvent event);
    }

    public static final Type<BreadcrumbValueChangeEvent.Handler> TYPE = new Type<BreadcrumbValueChangeEvent.Handler>();

    private String value;

    public BreadcrumbValueChangeEvent(String value) {
        this.value = value;
    }

    @Override
    protected void dispatch(BreadcrumbValueChangeEvent.Handler handler) {
        handler.onValueChange(this);
    }

    @Override
    public final Type<BreadcrumbValueChangeEvent.Handler> getAssociatedType() {
        return TYPE;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
}
