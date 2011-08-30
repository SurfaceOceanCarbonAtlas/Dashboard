package gov.noaa.pmel.tmap.catalogcleaner.data;

import gov.noaa.pmel.tmap.catalogcleaner.Datavalue;
public class DatasetService extends Service {
	private int parentId;

	public int getParentId(){
		return this.parentId;
	}

	public int getChildId(){
		return this.serviceId;
	}

	public DatasetService(int parentId, int serviceId){
		super(serviceId);
		this.parentId = parentId;
	}
	public DatasetService(int parentId, int serviceId, Datavalue base, Datavalue desc, Datavalue name, Datavalue suffix, Datavalue serviceType, Datavalue status){
		super(serviceId, base, desc, name, suffix, serviceType, status);
		this.parentId = parentId;
	}
}

