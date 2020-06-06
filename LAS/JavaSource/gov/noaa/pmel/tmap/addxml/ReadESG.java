package gov.noaa.pmel.tmap.addxml;

import gov.noaa.pmel.tmap.jdom.LASDocument;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdom.Document;
import org.jdom.Element;

import thredds.catalog.CollectionType;
import thredds.catalog.InvAccess;
import thredds.catalog.InvCatalog;
import thredds.catalog.InvCatalogFactory;
import thredds.catalog.InvDataset;
import thredds.catalog.ServiceType;

public class ReadESG {
	private static final Logger log = LoggerFactory.getLogger(ReadESG.class);
	public void read(String src) {
//		addXML ax = new addXML();
		InvCatalogFactory factory = new InvCatalogFactory("default", false);
		InvCatalog catalog = (InvCatalog) factory.readXML(src);
		StringBuilder buff = new StringBuilder();
		int count = catalog.getDatasets().size();
		if (!catalog.check(buff, true)) {
			log.error("Invalid catalog <" + src + ">\n" + buff.toString());
		} else {
			
			
			for (int index = 0; index < count; index++ ) {
				factory = new InvCatalogFactory("default", false);
				catalog = (InvCatalog) factory.readXML(src);
				InvDataset invDataset = catalog.getDatasets().get(index);
				System.out.println(invDataset.getName());
				String file = "/home/rhs/NCAR/las_categories_";
				try {
					file = file+JDOMUtils.MD5Encode(invDataset.getName())+".xml";
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				Element las_categories = new Element("las_categories");
				
				
//				CategoryBean cb = new CategoryBean();
//				cb.setName(invDataset.getName());
//				cb.setID(invDataset.getID());
//				// This is the top level...
//				//cb.setContributors(getContributors(invDataset));
//				Vector topCats = new Vector();
				for (Iterator topLevelIt = invDataset.getDatasets().iterator(); topLevelIt.hasNext(); ) {
					InvDataset topDS = (InvDataset) topLevelIt.next();
//					CategoryBean topCB = new CategoryBean();
//					topCB.setName(topDS.getName());
					String id = topDS.getID();
					if ( id == null ) {
						try {
							id = "id_"+JDOMUtils.MD5Encode(topDS.getName());
						} catch (UnsupportedEncodingException e) {
							id = "id_"+String.valueOf(Math.random());
						}
					}
					System.out.println("top: "+topDS.getName()+", "+topDS.getID());
					//topCB.setID(id);
					
					
//					for (Iterator subDatasetsIt = topDS.getDatasets().iterator(); subDatasetsIt.hasNext(); ) {
//						InvDataset subDataset = (InvDataset) subDatasetsIt.next();
//						topCB.addCatID(subDataset.getID());
//						// These will be the catalog containers that will contain the aggregations...
//						for (Iterator grandChildrenIt = subDataset.getDatasets().iterator(); grandChildrenIt.hasNext(); ) {
//							InvDataset grandChild = (InvDataset) grandChildrenIt.next();
//							if ( grandChild.hasAccess() && grandChild.getName().contains("aggregation")) {
//								// We are done.
//								String url = null;
//								InvAccess access = null;
//								for (Iterator ait = grandChild.getAccess().iterator(); ait.hasNext(); ) {
//									access = (InvAccess) ait.next();
//									if (access.getService().getServiceType() == ServiceType.DODS ||
//											access.getService().getServiceType() == ServiceType.OPENDAP ||
//											access.getService().getServiceType() == ServiceType.NETCDF) {
//										url = access.getStandardUrlName();
//									}
//								}
//								if ( url != null && url.contains("aggregation") ){
//									FilterBean filter = new FilterBean();
//									filter.setAction("apply-variable");
//									String tag = grandChild.getID();
//									filter.setContainstag(tag);
//									topCB.addFilter(filter);
//									topCB.addCatID(grandChild.getID());
//								}
//							} 
//						}
//					}
//					if ( topCB.getFilters().size() > 0 ) {
//						topCats.add(topCB);
//					}
				}
//				if ( topCats.size() > 0 ) {
//					cb.setCategories(topCats);
//				}
//				las_categories.addContent(cb.toXml());
				
				
				
				
				
//				ax.processESGCategories(invDataset, las_categories);
//				LASDocument document = new LASDocument();
//				document.setRootElement(las_categories);
//				System.out.println("Writing "+file+" for "+invDataset.getName());
//				document.write(file);
//				document = null;
			}
		}
	}
}