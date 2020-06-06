package gov.noaa.pmel.tmap.las.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class GetConfigEvent extends GwtEvent<GetConfigEvent.Handler> {
    private String catid;
    private String dsid;
    private String varid;
    public static final Type<GetConfigEvent.Handler> TYPE = new Type<GetConfigEvent.Handler>();
    public interface Handler extends EventHandler {
        void onGetConfig(GetConfigEvent event);
    }
    public GetConfigEvent(String catid, String dsid, String varid) {
        this.catid = catid;
        this.dsid = dsid;
        this.varid = varid;
    }
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onGetConfig(this);
        
    }
    
    public String getCatid() {
        return catid;
    }
    public void setCatid(String catid) {
        this.catid = catid;
    }
    public String getDsid() {
        return dsid;
    }
    public void setDsid(String dsid) {
        this.dsid = dsid;
    }
    public String getVarid() {
        return varid;
    }
    public void setVarid(String varid) {
        this.varid = varid;
    }

}
