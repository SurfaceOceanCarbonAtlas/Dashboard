/**
 * 
 */
package gov.noaa.pmel.tmap.las.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * A {@link GwtEvent} that informs listeners of a {@link String} value change.
 * Listeners must have a class that implements (or implement themselves) the
 * {@link StringValueChangeEvent.Handler} interface.
 * 
 * @author weusijana
 * 
 */
public class StringValueChangeEvent extends GwtEvent<StringValueChangeEvent.Handler> {

    public interface Handler extends EventHandler {
        void onValueChange(StringValueChangeEvent event);
    }

    public static final Type<StringValueChangeEvent.Handler> TYPE = new Type<StringValueChangeEvent.Handler>();

    private String value;

    public StringValueChangeEvent(String value) {
        this.value = value;
    }

    @Override
    protected void dispatch(StringValueChangeEvent.Handler handler) {
        handler.onValueChange(this);
    }

    @Override
    public final Type<StringValueChangeEvent.Handler> getAssociatedType() {
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
