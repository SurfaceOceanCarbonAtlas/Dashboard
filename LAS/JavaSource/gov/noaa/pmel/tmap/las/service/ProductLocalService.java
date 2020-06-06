/**
 * 
 */
package gov.noaa.pmel.tmap.las.service;

import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.jdom.LASDocument;
import gov.noaa.pmel.tmap.las.jdom.LASBackendRequest;
import gov.noaa.pmel.tmap.las.jdom.LASBackendResponse;
import gov.noaa.pmel.tmap.las.jdom.LASConfig;
import gov.noaa.pmel.tmap.las.jdom.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gov.noaa.pmel.tmap.las.util.Category;
import gov.noaa.pmel.tmap.las.util.Variable;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;

/**
 * @author Roland Schweitzer
 *
 */
public class ProductLocalService extends ProductService {
    private final static Logger log = LoggerFactory.getLogger(ProductLocalService.class.getName());
    
    /**
     * @param lasBackendRequest
     * @param serverURL
     * @param methodName
     * @param outputFileName
     * @throws IOException 
     */
    public ProductLocalService(LASBackendRequest lasBackendRequest, String serverURL,
            String methodName, String outputFileName) throws LASException, IOException {
        super(lasBackendRequest, serverURL, methodName, outputFileName);
    }

    public void getTHREDDS(LASBackendRequest lasBackendRequest, LASConfig lasConfig, ServerConfig serverConfig) throws JDOMException, URISyntaxException, UnsupportedEncodingException, LASException {
        
        LASBackendResponse lasBackendResponse = new LASBackendResponse();
        String fdsServerURL = lasConfig.getServerURL().replace("ProductServer.do", "fds/data/");
        URI base = new URI(fdsServerURL);
        LASDocument threddsDoc = new LASDocument();
        Namespace tns = Namespace.getNamespace("http://www.unidata.ucar.edu/namespaces/thredds/InvCatalog/v1.0");
        Namespace xlink = Namespace.getNamespace("xlink", "http://www.w3.org/1999/xlink");
        Element catalog = new Element("catalog", tns);
        catalog.addNamespaceDeclaration(xlink);
        threddsDoc.setRootElement(catalog);
        String title = lasConfig.getTitle();
        String name;
        if ( title != null && !title.equals("")) {
            name = "LAS THREDDS Catalog for "+title;
        } else {
            name = "LAS THREDDS Catalog";
        }
        catalog.setAttribute("name",name);
        
        catalog.setAttribute("version","1.0.1");
        
        Element service = new Element("service", tns);
        service.setAttribute("base",fdsServerURL);
        service.setAttribute("name", "fds");
        service.setAttribute("serviceType", "OpenDAP");
        catalog.addContent(service);
        
        ArrayList lasUIs = lasConfig.getUIs();
        
        ArrayList<Category> datasets = lasConfig.getCategories(null);
        for (Iterator dsIt = datasets.iterator(); dsIt.hasNext();) {
            
            Category dataset = (Category) dsIt.next();
            
            Element metadata = new Element("metadata", tns);
            metadata.setAttribute("inherited","true");
            
            Element dataType = new Element("dataType", tns);
            dataType.setText("GRID");
            metadata.addContent(dataType);
            
            Element serviceName = new Element("serviceName", tns);
            serviceName.setText("fds");
            metadata.addContent(serviceName);
       
            Element ds = new Element("dataset", tns);
            ds.addContent(metadata);
            ds.setAttribute("name", dataset.getName());
            ds.setAttribute("ID", dataset.getID());
            
            // Set a "doc" link to the "variables" page for this dataset.
            /*  After all this, there is no code in the LAS UI to enter
             * at the data set level (coulda fooled me) so this is put to
             * rest for the time being.
            Element doc = new Element("documentation", tns);
            String dstitle = URLEncoder.encode(dataset.getName(),"UTF-8");
            for (Iterator uiIt = lasUIs.iterator(); uiIt.hasNext();) {
                
                String url = (String) uiIt.next();
                if (url.endsWith("/")) {
                    url = url.substring(0,url.length()-1);
                }
                doc.setAttribute("href",url+"/dataset?"+"title="+dstitle, xlink);
                doc.setAttribute("title","LAS Access to the "+dataset.getName()+" data set.", xlink);
                ds.addContent(doc);
            }
            Need the declarations for doc and dstitle...
            */
            String dstitle;
            Element doc;
            
            String urlPath = dataset.getID();
            
            ArrayList<Variable> variables = lasConfig.getVariables(dataset.getID());
            if ( variables.size() > 0) {
                for (Iterator varIt = variables.iterator(); varIt.hasNext();) {
                    Variable variable = (Variable) varIt.next();
                    Element var = new Element("dataset", tns);
                    var.setAttribute("name", variable.getName());
                    var.setAttribute("ID", dataset.getID()+"_"+variable.getID());
                    for (Iterator uiIt = lasUIs.iterator(); uiIt.hasNext();) {
                        
                        String url = (String) uiIt.next();
                        if (url.endsWith("/")) {
                            url = url.substring(0,url.length()-1);
                        }


                        doc = new Element("documentation", tns);
                        dstitle = URLEncoder.encode(dataset.getName(),"UTF-8");
                        String varname = URLEncoder.encode(variable.getName(), "UTF-8");
                        doc.setAttribute("href",url+"/constrain?"+"title="+dstitle+"&varname="+varname, xlink);
                        doc.setAttribute("title","LAS Access to "+variable.getName()+" of the "+dataset.getName()+" data set.", xlink);
                        var.addContent(doc);
                    }

                    if ( !lasConfig.isRegular(dataset.getID(), variable.getID())) {
                        var.setAttribute("dataType", "Station");
                    } else {
                        var.setAttribute("urlPath",urlPath+"/"+variable.getID());
                    }

                    ds.addContent(var);
                }
                catalog.addContent(ds);
            }
        }

        String catalog_file = lasBackendRequest.getResultAsFile("thredds");
        threddsDoc.write(catalog_file);
        lasBackendResponse.addResponseFromRequest(lasBackendRequest);
        setResponseXML(lasBackendResponse.toString());
        
        return;
 
    }
    public void fiveMinutes(LASBackendRequest lasBackendRequest) throws JDOMException, IOException, Exception {
         
        LASBackendResponse lasBackendResponse = new LASBackendResponse();
        
        
        String cancel = lasBackendRequest.getResultAsFile("cancel");
        File cf = null;
        if ( cancel != null && !cancel.equals("") ) {
            cf = new File(cancel);
        }
        
        // If it's the cancel job, set the file and return.
        if ( lasBackendRequest.isCancelRequest() && cf != null ) {
            lasBackendResponse.setError("Java backend request canceled.");
           
            try {
                cf.createNewFile();
            } catch (Exception e) {
                lasBackendResponse.setError("fiveMinutes backend failed to cancel request. ", e);
            }
            log.debug("Java backend request canceled: "+lasBackendRequest.toCompactString());
            setResponseXML(lasBackendResponse.toString());
            return;
        }
        // If it's not the cancel job, sleep for 30 then look for the cancel job.  Repeat for 5 minutes.
        for ( int i = 0; i < 10; i++ ) {
            try {
                Thread.currentThread().sleep(1000*30);
            } catch (InterruptedException e) {
                lasBackendResponse.setError("fiveMinutes had trouble sleeping. ", e);
            }
            if ( cf != null && cf.exists() ) {
                lasBackendResponse.setError("Java backend request canceled.", "Returning cancel response.");
                if ( !cf.delete() ) {
                    lasBackendResponse.setError("Could not remove cancel file.", "Remove from cache");
                }
                setResponseXML(lasBackendResponse.toString());
                return;
            }
        }
        
        lasBackendResponse.addResponseFromRequest(lasBackendRequest);
        setResponseXML(lasBackendResponse.toString());
        return;    
    }
}
