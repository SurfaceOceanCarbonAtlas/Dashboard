package gov.noaa.pmel.tmap.addxml;

import gov.noaa.pmel.tmap.jdom.LASDocument;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import thredds.catalog.InvAccess;
import thredds.catalog.InvCatalog;
import thredds.catalog.InvCatalogFactory;
import thredds.catalog.InvDataset;
import thredds.catalog.ServiceType;

public class ESGDriver {

	private static final Logger log = LoggerFactory.getLogger(ESGDriver.class);
	public static void main(String[] args) {
		String src = "http://tds.prototype.ucar.edu/thredds/esgcet/catalog.xml";
		String base = src.substring(0, src.lastIndexOf("/")+1);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		CatalogRefHandler esgCatalogHandler = new CatalogRefHandler();
		SAXParser parser;
		try {
			factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			parser = factory.newSAXParser();			
			parser.parse(src, esgCatalogHandler);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LASDocument document = new LASDocument(new Element("lasdata"));
		Element dsE = new Element("datasets");
		document.getRootElement().addContent(dsE);
		Element gE = new Element("grids");
		document.getRootElement().addContent(gE);
		Element axE = new Element("axes");
		document.getRootElement().addContent(axE);
		Map<String, String> catalogs = esgCatalogHandler.getCatalogs();
		LASDocument doc = new LASDocument();
		for (Iterator nameIt = catalogs.keySet().iterator(); nameIt.hasNext();) {
			String name = (String) nameIt.next();
			String url = base+catalogs.get(name);
			InvCatalogFactory catfactory = new InvCatalogFactory("default", false);
			InvCatalog catalog = (InvCatalog) catfactory.readXML(url);
			DatasetsGridsAxesBean bean = new DatasetsGridsAxesBean();
			DatasetBean ds = new DatasetBean();
			List<DatasetBean> datasets = new ArrayList<DatasetBean>();
			List<VariableBean> variables = new ArrayList<VariableBean>();
			String id = catalog.getName();
			try {
				id = "id_"+JDOMUtils.MD5Encode(catalog.getName());
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			ds.setElement(id);
			ds.setName(catalog.getName());
			for (Iterator topLevelIt = catalog.getDatasets().iterator(); topLevelIt.hasNext(); ) {
				InvDataset topDS = (InvDataset) topLevelIt.next();
				ds.setName(topDS.getName());
									
	                // These will be the catalog containers that will contain the aggregations...
					for (Iterator grandChildrenIt = topDS.getDatasets().iterator(); grandChildrenIt.hasNext(); ) {
						InvDataset grandChild = (InvDataset) grandChildrenIt.next();
						if ( grandChild.hasAccess() && (grandChild.getName().toLowerCase(Locale.ENGLISH).contains("aggregation") || grandChild.getID().toLowerCase(Locale.ENGLISH).contains("aggregation"))) {
							// We are done.
							String durl = null;
							InvAccess access = null;
							for (Iterator ait = grandChild.getAccess().iterator(); ait.hasNext(); ) {
								access = (InvAccess) ait.next();
								if (access.getService().getServiceType() == ServiceType.DODS ||
										access.getService().getServiceType() == ServiceType.OPENDAP ||
										access.getService().getServiceType() == ServiceType.NETCDF) {
									durl = access.getStandardUrlName();
//									DatasetsGridsAxesBean dgab = addXML.createBeansFromThreddsMetadata(grandChild, durl);
//									
//									for (Iterator dsit = dgab.getDatasets().iterator(); dsit.hasNext();) {
//										DatasetBean dsb = (DatasetBean) dsit.next();
//										variables.addAll(dsb.getVariables());
//									}
//									bean.getGrids().addAll(dgab.getGrids());
//									bean.getAxes().addAll(dgab.getAxes());
								}
							}
							
						} 
					}
				
			}
			ds.setVariables(variables);
			datasets.add(ds);
			bean.setDatasets(datasets);
			for (Iterator dsIt = bean.getDatasets().iterator(); dsIt.hasNext();) {
				DatasetBean dsb = (DatasetBean) dsIt.next();
				dsE.addContent(dsb.toXml());
			}
			for (Iterator axIt = bean.getAxes().iterator(); axIt.hasNext(); ) {
				AxisBean axb = (AxisBean) axIt.next();
				axE.addContent(axb.toXml());
			}
			for (Iterator gIt = bean.getGrids().iterator(); gIt.hasNext(); ) {
				GridBean gb = (GridBean) gIt.next();
				gE.addContent(gb.toXml());
			}
		}
	}
}
