package gov.noaa.pmel.tmap.las.client.event;

import gov.noaa.pmel.tmap.las.client.serializable.ConfigSerializable;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class ConfigReturnedEvent extends GwtEvent<ConfigReturnedEvent.Handler> {
    private ConfigSerializable configSerializable;
    public static final Type<ConfigReturnedEvent.Handler> TYPE = new Type<ConfigReturnedEvent.Handler>();
    public interface Handler extends EventHandler {
        void onConfigReturned(ConfigReturnedEvent event);
    }
    public ConfigReturnedEvent(ConfigSerializable configSerializable) {
        this.configSerializable = configSerializable;
    }
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onConfigReturned(this);
        
    }
    public ConfigSerializable getConfigSerializable() {
        return configSerializable;
    }
    public void setConfigSerializable(ConfigSerializable configSerializable) {
        this.configSerializable = configSerializable;
    }
    

}
