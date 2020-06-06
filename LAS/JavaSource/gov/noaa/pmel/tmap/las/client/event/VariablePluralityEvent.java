package gov.noaa.pmel.tmap.las.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event for informing objects, that have a member that implements (or implement
 * themselves) the {@link VariablePluralityEvent.Handler} interface, that a
 * widget is has changed from using only one {@link VariableSerializable} to
 * using multiple {@link VariableSerializable}s or vice-versa.
 * 
 * @author weusijana
 * 
 */
public class VariablePluralityEvent extends GwtEvent<VariablePluralityEvent.Handler> {

    public interface Handler extends EventHandler {
        void onPluralityChange(VariablePluralityEvent event);
    }

    public static final Type<VariablePluralityEvent.Handler> TYPE = new Type<VariablePluralityEvent.Handler>();

    private boolean plural;

    /**
     * @return true if the source has changed to using multiple
     *         {@link VariableSerializable}s, false if the source has changed to
     *         using only one {@link VariableSerializable}.
     */
    public boolean isPlural() {
        return plural;
    }

    /**
     * @param plural
     *            Set to true if the source has changed to using multiple
     *            {@link VariableSerializable}s, set to false if the source has
     *            changed to using only one {@link VariableSerializable}.
     */
    public void setPlural(boolean plural) {
        this.plural = plural;
    }

    /**
     * @param plural
     *            Pass in true if the source has changed to using multiple
     *            {@link VariableSerializable}s, pass in false if the source has
     *            changed to using only one {@link VariableSerializable}.
     */
    public VariablePluralityEvent(boolean plural) {
        setPlural(plural);
    }

    @Override
    protected void dispatch(VariablePluralityEvent.Handler handler) {
        handler.onPluralityChange(this);
    }

    @Override
    public final Type<VariablePluralityEvent.Handler> getAssociatedType() {
        return TYPE;
    }
}
