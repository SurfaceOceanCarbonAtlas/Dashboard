package gov.noaa.pmel.tmap.las.client.rpc;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.event.CategoriesReturnedEvent;
import gov.noaa.pmel.tmap.las.client.event.ConfigReturnedEvent;
import gov.noaa.pmel.tmap.las.client.event.GetCategoriesEvent;
import gov.noaa.pmel.tmap.las.client.event.GetConfigEvent;
import gov.noaa.pmel.tmap.las.client.serializable.CategorySerializable;
import gov.noaa.pmel.tmap.las.client.serializable.ConfigSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.GridSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.OperationSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.OptionSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.RegionSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;
import gov.noaa.pmel.tmap.las.client.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RPCManager {
    
    ClientFactory clientFactory = GWT.create(ClientFactory.class);
    EventBus eventBus = clientFactory.getEventBus();
    
    
    private CategorySerializable categorySerializable;        // from public CategorySerializable getCategoryWithGrids(String catid, String dsid) throws RPCException;
    private CategorySerializable[] categoriesSerializable;    // from getCategories(String catid, String dsid) throws RPCException;
                                                             // or getTimeSeries() throws RPCException;
    private VariableSerializable variableSerializable;        // from getVariable(String dsid, String varid) throws RPCException;
    private GridSerializable gridSerializable;                // from getGrid(String dsID, String varID) throws RPCException;
    private OperationSerializable[] operationSerializable;    // from getOperations(String view, String dsID, String varID) throws RPCException;
                                                             // or   getOperations(String view, String[] xpath) throws RPCException;
    private OptionSerializable[] optionsSerializable;         // from getOptions(String opid) throws RPCException;
    private HashMap<String, String> propertyGroup;            // from getPropertyGroup(String name) throws RPCException;

    
    private RegionSerializable[] regionsSerializable;         // from getRegions(String dsid, String varid) throws RPCException;
    private ConfigSerializable configSerializable;            // from getConfig(String view, String dsid, String varid) throws RPCException;
    private Map<String, String> iDMap;                        // from getIDMap(String data_url) throws RPCException;
    
    private List<GwtEvent> pendingRPCEvents = new ArrayList<GwtEvent>();
    
    
    public RPCManager() {
        eventBus.addHandler(GetConfigEvent.TYPE, new GetConfigEvent.Handler() {
            
            @Override
            public void onGetConfig(GetConfigEvent event) {
                String dsid = event.getDsid();
                if ( configSerializable != null ) {
                    if (configSerializable.getDsid().equals(dsid)) {
                        eventBus.fireEventFromSource(new ConfigReturnedEvent(configSerializable), RPCManager.this);
                    } else {
                       fireGetConfig(event);
                    }
                } else {
                    fireGetConfig(event);
                }
            }
        });
        eventBus.addHandler(GetCategoriesEvent.TYPE, new GetCategoriesEvent.Handler() {
            
            @Override
            public void onGetCategories(GetCategoriesEvent event) {
                String catid = event.getCatid();
                String dsid = event.getDsid();
                if ( categoriesSerializable != null ) {
                    if ( catid.equals(categoriesSerializable[0].getID()) && dsid.equals(categoriesSerializable[0].getDatasetSerializable().getID())) {
                        eventBus.fireEventFromSource(new CategoriesReturnedEvent(categoriesSerializable), RPCManager.this);
                    } else {
                        fireGetCategories(event);
                    }
                } else {
                    fireGetCategories(event);
                }
            }
        });
    }
    private void fireGetCategories(GetCategoriesEvent event) {
        String catid = event.getCatid();
        String dsid = event.getDsid();
        pendingRPCEvents.add(event);
        Util.getRPCService().getCategories(dsid, dsid, categoryCallback);     
    }
    private void fireGetConfig(GetConfigEvent event) {
        final String c = event.getCatid();
        final String d = event.getDsid();
        final String v = event.getVarid();
        pendingRPCEvents.add(event);
        Util.getRPCService().getConfig(null, c, d, v, getConfigCallback);
    }
    private void removeStaleEvents(List<GwtEvent> pendingRPCEvents, Type type) {
        List<GwtEvent> remove = new ArrayList<GwtEvent>();
        for (Iterator eIt = pendingRPCEvents.iterator(); eIt.hasNext();) {
            GwtEvent gwtEvent = (GwtEvent) eIt.next();
            if ( gwtEvent.getAssociatedType().equals(type) ) {
                remove.add(gwtEvent);
            }
        }
        pendingRPCEvents.removeAll(remove);
    }
    private AsyncCallback<CategorySerializable[]> categoryCallback = new AsyncCallback<CategorySerializable[]>() {

        @Override
        public void onFailure(Throwable caught) {
            removeStaleEvents(pendingRPCEvents, GetCategoriesEvent.TYPE);
            eventBus.fireEventFromSource(new CategoriesReturnedEvent(new CategorySerializable[]{new CategorySerializable()}), RPCManager.this);
        }


        @Override
        public void onSuccess(CategorySerializable[] result) {
        	
            String catid = UriUtils.fromString(result[0].getID()).asString();
            String dsid = UriUtils.fromString(result[0].getDatasetSerializable().getID()).asString();
            removeGetCategoriesEvent(catid, dsid);
            eventBus.fireEventFromSource(new CategoriesReturnedEvent(result), RPCManager.this);
            
        }


        private void removeGetCategoriesEvent(String catid, String dsid) {
            List<GwtEvent> remove = new ArrayList<GwtEvent>();
            for (Iterator eIt = pendingRPCEvents.iterator(); eIt.hasNext();) {
                GwtEvent gwtEvent = (GwtEvent) eIt.next();
                if ( gwtEvent.getAssociatedType().equals(GetCategoriesEvent.TYPE) ) {
                    GetCategoriesEvent e = (GetCategoriesEvent) gwtEvent;
                    if ( e.getCatid().equals(catid) && e.getDsid().equals(dsid) ) {
                        remove.add(gwtEvent);
                    }
                }
                pendingRPCEvents.remove(remove);
            }
        }
    };
    private AsyncCallback<ConfigSerializable> getConfigCallback = new AsyncCallback<ConfigSerializable>() {

        @Override
        public void onFailure(Throwable caught) {
            removeStaleEvents(pendingRPCEvents, GetConfigEvent.TYPE);
            eventBus.fireEventFromSource(new ConfigReturnedEvent(new ConfigSerializable()), RPCManager.this);
            
        }

       
        
        @Override
        public void onSuccess(ConfigSerializable result) {
            configSerializable = result;
            removeConfigSerialableEvent(configSerializable.getDsid(), configSerializable.getVarid());
            eventBus.fireEventFromSource(new ConfigReturnedEvent(configSerializable), RPCManager.this);
   
            
        }
        
        private void removeConfigSerialableEvent(String dsid, String varid) {
            List<GwtEvent> remove = new ArrayList<GwtEvent>();
            for (Iterator eIt = pendingRPCEvents.iterator(); eIt.hasNext();) {
                GwtEvent gwtEvent = (GwtEvent) eIt.next();
                if ( gwtEvent.getAssociatedType().equals(GetConfigEvent.TYPE) ) {
                    GetConfigEvent e = (GetConfigEvent) gwtEvent;
                    if ( e.getDsid().equals(dsid) && e.getVarid().equals(varid) ) {
                        remove.add(gwtEvent);
                    }
                }
            }
            pendingRPCEvents.remove(remove);
        }
        
    };
}
