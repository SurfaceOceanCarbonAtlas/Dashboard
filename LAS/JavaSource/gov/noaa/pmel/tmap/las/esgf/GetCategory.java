package gov.noaa.pmel.tmap.las.esgf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.http.HttpException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import thredds.catalog.InvCatalog;
import thredds.catalog.InvCatalogFactory;
import gov.noaa.pmel.tmap.addxml.ADDXMLProcessor;
import gov.noaa.pmel.tmap.addxml.AxisBean;
import gov.noaa.pmel.tmap.addxml.DatasetBean;
import gov.noaa.pmel.tmap.addxml.DatasetsGridsAxesBean;
import gov.noaa.pmel.tmap.addxml.GridBean;
import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.proxy.LASProxy;
import gov.noaa.pmel.tmap.las.util.Constants;

public class GetCategory {

    /**
     * @param args
     */
    public static void main(String[] args) {
        /* 
         * catid=55B48299F30BC9828F9E93A18AFAA2FC_ns_cmip5.output1.NOAA-GFDL.GFDL-CM2p1.decadal1961.mon.atmos.Amon.r6i1p1.v20110601
         * catid=55B48299F30BC9828F9E93A18AFAA2FC_ns_cmip5.output1.NOAA-GFDL.GFDL-CM2p1.decadal1961.mon.atmos.Amon.r6i1p1.uas.20110601.aggregation
         * catid=55B48299F30BC9828F9E93A18AFAA2FC_ns_cmip5.output1.NOAA-GFDL.GFDL-CM2p1.decadal1961.mon.atmos.Amon.r6i1p1.uas.20110601.aggregation.1
         *
         */
        
        XMLOutputter xmlout = new XMLOutputter(Format.getPrettyFormat());
        
        String id = "55B48299F30BC9828F9E93A18AFAA2FC_ns_cmip5.output1.NOAA-GFDL.GFDL-CM2p1.decadal1961.mon.atmos.Amon.r6i1p1.uas.20110601.aggregation";
        LASProxy lasProxy= new LASProxy();
         
        String[] parts = id.split(Constants.NAME_SPACE_SPARATOR);
        
        String master_id = parts[1];

        if ( master_id.endsWith("aggregation") ) {
            // Hack off three
            master_id = master_id.substring(0, master_id.lastIndexOf("."));
            master_id = master_id.substring(0, master_id.lastIndexOf("."));
            master_id = master_id.substring(0, master_id.lastIndexOf("."));
        } else if ( master_id.contains("aggregation") ) {
            // Hack off four
            master_id = master_id.substring(0, master_id.lastIndexOf("."));
            master_id = master_id.substring(0, master_id.lastIndexOf("."));
            master_id = master_id.substring(0, master_id.lastIndexOf("."));
            master_id = master_id.substring(0, master_id.lastIndexOf("."));
        } 
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            lasProxy.executeGetMethodAndStreamResult("http://pcmdi9.llnl.gov/esg-search/search?master_id="+master_id, stream);
            Document doc = new Document();
            
            JDOMUtils.XML2JDOM(stream.toString(), doc);
            Element root = doc.getRootElement();
            Element result = root.getChild("result");
            String catalog = null;
            String LAS = null;
            Set<String> time_freqs = new HashSet<String>();
            if ( result != null ) {
                String number = result.getAttributeValue("numFound");
                if ( !number.equals("0") ) {
                    List<Element> results = result.getChildren("doc");
                    Element solrDoc = results.get(0);
                    if ( solrDoc != null ) {
                        List<Element> arrays = solrDoc.getChildren("arr");
                        for ( Iterator arrE = arrays.iterator(); arrE.hasNext(); ) {
                            Element arr = (Element) arrE.next();
                            if ( arr.getAttributeValue("name").equals("url")) {
                                List<Element> strs = arr.getChildren("str");
                                for ( Iterator strIt = strs.iterator(); strIt.hasNext(); ) {
                                    Element str = (Element) strIt.next();
                                    String txt = str.getTextTrim();
                                    if ( txt.contains("|Catalog") ) {
                                        catalog = txt.substring(0, txt.indexOf("#"));
                                        System.out.println(catalog);
                                    }
                                    if ( txt.contains("|LAS") ) {
                                        LAS = txt.substring(0, txt.indexOf("|"));
                                    }
                                }
                            } else if ( arr.getAttributeValue("name").equals("time_frequency") ) {
                                List<Element> strs = arr.getChildren("str");
                                for ( Iterator strIt = strs.iterator(); strIt.hasNext(); ) {
                                    Element str = (Element) strIt.next();
                                    String txt = str.getTextTrim();
                                    time_freqs.add(txt);
                                }
                            }
                        }
                    }
                }                
            }
            InvCatalogFactory factory = new InvCatalogFactory("default", false);
            InvCatalog invCatalog = (InvCatalog) factory.readXML(catalog);
            List<DatasetsGridsAxesBean> dagbs = ADDXMLProcessor.processESGDatasets(time_freqs, invCatalog);
            for ( Iterator dagbIt = dagbs.iterator(); dagbIt.hasNext(); ) {
                DatasetsGridsAxesBean dagb = (DatasetsGridsAxesBean) dagbIt.next();
                //TODO we need to convert7
                List<DatasetBean> datasets = dagb.getDatasets();
                for ( Iterator dsIt = datasets.iterator(); dsIt.hasNext(); ) {
                    DatasetBean db = (DatasetBean) dsIt.next();
                    xmlout.output(db.toXml(), System.out);
                }
                
                List<GridBean> grids = dagb.getGrids();
                for ( Iterator gIt = grids.iterator(); gIt.hasNext(); ) {
                    GridBean gb = (GridBean) gIt.next();
                    xmlout.output(gb.toXml(), System.out);
                }
                
                List<AxisBean> axes = dagb.getAxes();
                for ( Iterator aIt = axes.iterator(); aIt.hasNext(); ) {
                    AxisBean ab = (AxisBean) aIt.next();
                    xmlout.output(ab.toXml(), System.out);
                }
                
            }
            int i = 0;
        } catch ( HttpException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch ( JDOMException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
