/**
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development. 
 */
package gov.noaa.pmel.tmap.las.jdom;

import gov.noaa.pmel.tmap.jdom.LASDocument;
import gov.noaa.pmel.tmap.las.util.Result;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.sql.rowset.WebRowSet;

import oracle.jdbc.rowset.OracleWebRowSet;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.transform.XSLTransformException;
import org.jdom.transform.XSLTransformer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONException;
import org.json.XML;

import com.sun.rowset.WebRowSetImpl;

/**
 * This class is the container for the response from the backend service.
 * @author Roland Schweitzer
 *
 */
public class LASBackendResponse extends LASDocument {
    /*
	 * Any number that uniquely identifies the version of this class' code.  
	 * The Eclipse IDE will generate it automatically for you.  We do not depend on this
	 * since we do not serialize our code across the wire.
	 */
    private static final long serialVersionUID = -8183503354778875380L;
    /**
     * Default constructor that builds an empty document with just a root element.
     */
    public LASBackendResponse () {
        this.setRootElement(new Element("backend_response"));
    }
    /**
     * A convenience method to copy the collection of expected results from the request XML to the
     * response XML verifying the existence of each result before adding it to the response.
     * @param lasBackendRequest - the request object from which the results will be copied
     * @throws JDOMException
     */
    public void addResponseFromRequest(LASBackendRequest lasBackendRequest) throws JDOMException {
        // There is no guarantee that the Ferret script (or whatever processes makes each result)
        // actually created each result so this must be verified explicitly for each result.
        Element thisResponse = new Element("response");
        setDate(thisResponse);
        this.getRootElement().addContent(thisResponse);
        Element response = lasBackendRequest.getElementByXPath("/backend_request/response");        
        List results = response.getChildren("result");
        for (Iterator resIt = results.iterator(); resIt.hasNext();) {
            Element result = (Element) resIt.next();
            File res = new File(result.getAttributeValue("file"));
            // TODO Add check for existence of URL if file is null or "".
            if ( res.exists() ) {
                thisResponse.addContent((Element)result.clone());
            }
        }
    }
   
    /**
     * Get a URL reference to a result, the most common method you need to add a result to an HTML page in an &lt;img&gt; or &lt;iframe&gt; tag.
     * @param ID - the ID of the result you want.
     * @return url - a String with a reference to the result as a URL.
     */
    public String getResult(String ID) {
        String openid = getRootElement().getAttributeValue("openid");
        String appendid = "";
        if ( openid != null && !openid.equals("") ) {
        	appendid = "?openid="+openid;
        }
        Element result = getResultElement(ID);
        if ( result != null ) {
            String type = result.getAttributeValue("type");
            String rID = result.getAttributeValue("ID");
            if (ID.equals(rID)) {
                if (type.equals("error")) {
                    String text = result.getText();
                    return text;
                    //return result.getText();
                } else {
                    return result.getAttributeValue("url")+appendid;
                }
            }
        }

        return "";    
    }
    /**
     * Find out if a result is from a remote service
     * @param ID the id of the result to check
     * @return true if the service is remote (and therefore you must access the result via a URL); false if it's local
     */
    public boolean isResultRemote(String ID) {
        Element result = getResultElement(ID);
        if ( result != null ) {
            String remote = result.getAttributeValue("remote");
            if ( remote != null && remote.equals("true") ) {
                return true;
            }
        }
        return false;
    }
    /**
     * Private helper method that the gets the result by ID.
     * @param ID - the ID of the result element you want.
     * @return result - the result element with the matching ID.  null if no matching element was found
     */
    private Element getResultElement(String ID) {
        List responses = this.getRootElement().getChildren("response");
        Element result = null;
        if (responses.size() == 0) {
            return null;
        }
        for (Iterator respIt = responses.iterator(); respIt.hasNext();) {
            Element resp = (Element) respIt.next();
            List results = resp.getChildren("result");
            for (Iterator resIt = results.iterator(); resIt.hasNext();) {
                result = (Element) resIt.next();
                String type = result.getAttributeValue("type");
                String rID = result.getAttributeValue("ID");
                if (ID.equals(rID)) {
                    return result;
                }
            }
        }
        
        return null;    
    }
    
    /**
     * Check to see if a result can be streamed.
     * @param ID - the ID of the result to check
     * @return true if this result is streamable; false if it is not
     */
    public boolean isStreamable(String ID) {
        List responses = this.getRootElement().getChildren("response");
        if (responses.size() == 0) {
            return false;
        }
        for (Iterator respIt = responses.iterator(); respIt.hasNext();) {
            Element resp = (Element) respIt.next();
            List results = resp.getChildren("result");
            for (Iterator resIt = results.iterator(); resIt.hasNext();) {
                Element result = (Element) resIt.next();
                String type = result.getAttributeValue("type");
                String rID = result.getAttributeValue("ID");
                String stream = result.getAttributeValue("streamable");
                if (type.equals("error")) {
                    // Nothing to do...
                    return false;
                }
                if (stream != null && Boolean.valueOf(stream).booleanValue() && rID.equals(ID)) {
                   return true;
                }
            }
        }
        
        return false;
    }
    /**
     * @deprecated
     * @return The ID of the first result that is streamable.
     */
    public String getStreamedID() {
        List responses = this.getRootElement().getChildren("response");
        if (responses.size() == 0) {
            return "";
        }
        for (Iterator respIt = responses.iterator(); respIt.hasNext();) {
            Element resp = (Element) respIt.next();
            List results = resp.getChildren("result");
            for (Iterator resIt = results.iterator(); resIt.hasNext();) {
                Element result = (Element) resIt.next();
                String type = result.getAttributeValue("type");
                String rID = result.getAttributeValue("ID");
                String stream = result.getAttributeValue("streamable");
                if (type.equals("error")) {
                    // Nothing to do...
                    return "";
                }
                if (stream != null && Boolean.valueOf(stream).booleanValue()) {
                   return rID;
                }
            }
        }
        
        return "";
    }
    /**
     * Get the MIME type of this result
     * Before the product server can stream the result back it has to know it's mime time which is part of the operation configuration.
     * @param ID -- The ID of the result you want.
     * @return the test string that identifies the MIME type of this result
     */
    public String getStreamedMimeType(String ID) {
        List responses = this.getRootElement().getChildren("response");
        if (responses.size() == 0) {
            return "";
        }
        for (Iterator respIt = responses.iterator(); respIt.hasNext();) {
            Element resp = (Element) respIt.next();
            List results = resp.getChildren("result");
            for (Iterator resIt = results.iterator(); resIt.hasNext();) {
                Element result = (Element) resIt.next();
                String type = result.getAttributeValue("type");
                String mime_type = result.getAttributeValue("mime_type");
                String stream = result.getAttributeValue("streamable");
                String rId = result.getAttributeValue("ID");
                if (type.equals("error")) {
                    // Nothing to do...
                    return "";
                }
                if (stream != null && Boolean.valueOf(stream).booleanValue() && ID.equals(rId)) {
                   return mime_type;
                }
            }
        }
        
        return "";
    }
    /**
     * Check to see if this reponse has a result that is "streamable"
     * @return true if it has a streamable result; false if it does not
     */
    public boolean hasStreamedResult() {
        List responses = this.getRootElement().getChildren("response");
        if (responses.size() == 0) {
            return false;
        }
        for (Iterator respIt = responses.iterator(); respIt.hasNext();) {
            Element resp = (Element) respIt.next();
            List results = resp.getChildren("result");
            for (Iterator resIt = results.iterator(); resIt.hasNext();) {
                Element result = (Element) resIt.next();
                String type = result.getAttributeValue("type");
                String stream = result.getAttributeValue("streamable");
                if (type.equals("error")) {
                    // Nothing to do...
                    return false;
                }
                if (stream != null && Boolean.valueOf(stream).booleanValue()) {
                   return true;
                }
            }
        }
        
        return false;
    }
    public List<Result> getResults() {
    	List<Result> all_results = new ArrayList<Result>();
    	 List responses = this.getRootElement().getChildren("response");
         if (responses == null || responses.size() == 0) {
             return null;
         }
         for (Iterator respIt = responses.iterator(); respIt.hasNext();) {
             Element resp = (Element) respIt.next();
             List results = resp.getChildren("result");
             for (Iterator resIt = results.iterator(); resIt.hasNext();) {
                 Element resultE = (Element) resIt.next();
                 Result result = new Result((Element) resultE.clone());
                 all_results.add(result);
             }
         }
    	return all_results;
    }
    /**
     * Helper function that gets the result element by result type.
     * @param in_type
     * @return resutlt - the result element.
     */
    public Element getResultByType(String in_type) {
        
        List responses = this.getRootElement().getChildren("response");
        if (responses == null || responses.size() == 0) {
            return null;
        }
        for (Iterator respIt = responses.iterator(); respIt.hasNext();) {
            Element resp = (Element) respIt.next();
            List results = resp.getChildren("result");
            for (Iterator resIt = results.iterator(); resIt.hasNext();) {
                Element result = (Element) resIt.next();
                String type = result.getAttributeValue("type");
                if (in_type.equals(type)) {
                    return result;
                }
            }
        }
        
        return null;    
    }
    /**
     * Return a file path to the first result of a particular type.
     * @param in_type
     * @return the full path name of the file that holds the first result of this type
     */
    public String getResultAsFileByType(String in_type) {
        Element result = getResultByType(in_type);
        if (result != null) {
            String type = result.getAttributeValue("type");

            if (type.equals("error")) {
                // Nothing to do...
                return result.getText();
            } else {
                return result.getAttributeValue("file");
            }
        } else {
            return "";
        }
    }
    public void addResult(Element result) {
    	Element response = this.getRootElement().getChild("response");
    	if ( response == null ) {
    		response = new Element("response");
    		setDate(response);
    		getRootElement().addContent(response);
    	}
    	response.addContent(result);
    }
    /**
     * Remove a result.  This is used to eliminate results that did not get created by the backend service for whatever reason.
     * @param ID - the ID of the result to be removed
     */
    public void removeResult(String ID) {
        List responses = this.getRootElement().getChildren("response");
        for (Iterator respIt = responses.iterator(); respIt.hasNext();) {
            Element resp = (Element) respIt.next();
            List results = resp.getChildren("result");
            for (Iterator resIt = results.iterator(); resIt.hasNext();) {
                Element result = (Element) resIt.next();
                String rID = result.getAttributeValue("ID");
                if (ID.equals(rID)) {
                    this.removeContent(result);
                }
            }
        }
    }
    /**
     * Get an ArrayList of result file paths.
     * @return files - an ArrayList with all the results referenced by file path
     */
    public ArrayList<String> getResultsAsFiles() {
        ArrayList<String> files = new ArrayList<String>();
        List responses = this.getRootElement().getChildren("response");
        for (Iterator respIt = responses.iterator(); respIt.hasNext();) {
            Element resp = (Element) respIt.next();
            List results = resp.getChildren("result");
            for (Iterator resIt = results.iterator(); resIt.hasNext();) {
                Element result = (Element) resIt.next();
                String type = result.getAttributeValue("type");
                if (!type.equals("error")) {   
                    files.add(result.getAttributeValue("file"));
                }       
            }
        }
        return files;
    }
    /**
     * Get a HashSet of unique cache keys associated with this response.
     * 
     */
    public HashSet<String> getCacheKeys() {
    	HashSet<String> keys = new HashSet<String>();
    	List responses = this.getRootElement().getChildren("response");
        for (Iterator respIt = responses.iterator(); respIt.hasNext();) {
            Element resp = (Element) respIt.next();
            List results = resp.getChildren("result");
            for (Iterator resIt = results.iterator(); resIt.hasNext();) {
                Element result = (Element) resIt.next();
                String type = result.getAttributeValue("type");
                if (!type.equals("error")) {   
                    keys.add(result.getAttributeValue("key"));
                }       
            }
        }
        return keys;
    }
    /**
     * Get the full path name of a result by ID
     * @param ID the ID of the result you want.
     * @return the path to the file that contains the result
     */
    public String getResultAsFile(String ID) {
        List responses = this.getRootElement().getChildren("response");
        if (responses.size() == 0) {
            return "No responses found.";
        }
        for (Iterator respIt = responses.iterator(); respIt.hasNext();) {
            Element resp = (Element) respIt.next();
            List results = resp.getChildren("result");
            for (Iterator resIt = results.iterator(); resIt.hasNext();) {
                Element result = (Element) resIt.next();
                String type = result.getAttributeValue("type");
                String rID = result.getAttributeValue("ID");
                if (ID.equals(rID)) {
                    if (type.equals("error")) {
                        // Nothing to do...
                        return result.getText();
                    } else {
                       return result.getAttributeValue("file");
                    }
                }
            }
        }
        
        return "";    
    }
    /**
     * Get the contents of a result that an XML file as a String.
     * 
     * @param ID - the ID of the result you want
     * @return the contents of a XML result as a String
     * @throws JDOMException 
     * @throws IOException 
     */
    public String getResultAsString(String ID) throws JDOMException, IOException {
        //TODO Figure out what to do with other types (like txt is pretty obvious)
        Element result = getElementByXPath("/backend_response/response/result[@ID='"+ID+"']");
        if (result == null) {
            return "No result found with ID="+ID;
        }
        String type = result.getAttributeValue("type");
        if (type.equals("xml") || type.equals("webrowset")) {
           LASDocument doc = new LASDocument();
           JDOMUtils.XML2JDOM(new File(getResultAsFile(ID)), doc);
           return doc.toString();
        } else {
            return "Don't know how to return file of type: "+type;
        }
    }

    /**
     * Get the contents of a result that an XML file as a encoded String.
     *
     * @param ID - the ID of the result you want
     * @return the contents of a XML result as a String
     * @throws JDOMException
     * @throws IOException
     */
//jli
    public String getResultAsEncodedString(String ID) throws JDOMException, IOException {
        //TODO Figure out what to do with other types (like txt is pretty obvious)
        Element result = getElementByXPath("/backend_response/response/result[@ID='"+ID+"']");
        if (result == null) {
            return "No result found with ID="+ID;
        }
        String type = result.getAttributeValue("type");
        if (type.equals("xml")) {
           LASDocument doc = new LASDocument();
           JDOMUtils.XML2JDOM(new File(getResultAsFile(ID)), doc);
           return doc.toEncodedURLString();
        } else {
            return "Don't know how to return file of type: "+type;
        }
    }


    /**
     * Get the contents of a result that an XML file as a JavaScript Safe String.
     *
     * @param ID - the ID of the result you want
     * @return the contents of a XML result as a String
     * @throws JDOMException
     * @throws IOException
     */
    public String getResultAsJavaScriptSafeString(String ID) throws JDOMException, IOException {
        //TODO Figure out what to do with other types (like txt is pretty obvious)
        Element result = getElementByXPath("/backend_response/response/result[@ID='"+ID+"']");
        if (result == null) {
            return "No result found with ID="+ID;
        }
        String type = result.getAttributeValue("type");
        if (type.equals("xml")) {
           LASDocument doc = new LASDocument();
           JDOMUtils.XML2JDOM(new File(getResultAsFile(ID)), doc);
           return doc.toJavaScriptSafeString();
        } else {
            return "Don't know how to return file of type: "+type;
        }
    }

    /**
     * Get the contents of an XML result transformed by an XSLT stylesheet.
     * 
     * @param ID          the ID of the result you want to transform
     * @param stylesheet  the name of the stylesheet (in resources/productserver/stylesheets) that will be used to transform the XML
     * @return            a String with the result of the XSLT transformation 
     * @throws JDOMException 
     * @throws IOException 
     */
    public String getResultTransformedByXSL(String ID, String stylesheet) throws IOException, JDOMException {
        if ( !stylesheet.endsWith(".xsl")) {
            stylesheet = stylesheet+".xsl";
        }
        String xslFile = JDOMUtils.getResourcePath(this, "resources/productserver/stylesheets/"+stylesheet);
        XSLTransformer transformer = new XSLTransformer(xslFile);
        LASDocument rowset = new LASDocument();
        JDOMUtils.XML2JDOM(new File(getResultAsFile(ID)), rowset);        
        LASDocument transformedRowset = new LASDocument(transformer.transform(rowset));
        return transformedRowset.toString();
    }
    public String getTextResultTransformedByXSL(String ID, String stylesheet) throws IOException, JDOMException {
    	if ( !stylesheet.endsWith(".xsl")) {
            stylesheet = stylesheet+".xsl";
        }
        String xslFile = JDOMUtils.getResourcePath(this, "resources/productserver/stylesheets/"+stylesheet);
        LASDocument rowset = new LASDocument();
        JDOMUtils.XML2JDOM(new File(getResultAsFile(ID)), rowset);       
        XSLTransformer transformer = new XSLTransformer(xslFile);
        return transformer.transform(rowset).getRootElement().getText();
    }
    /**
     * Get the contents of an XML file transformed by an XSLT stylesheet.
     *
     * @param PATH          the full i local path of the file you want to transform
     * @param stylesheet  the name of the stylesheet (in resources/productserver/stylesheets) that will be used to transform the XML
     * @return            a String with the result of the XSLT transformation
     * @throws JDOMException
     * @throws IOException
     */
    public String getFileTransformedByXSL(String PATH, String stylesheet) throws IOException, JDOMException {
        if ( !stylesheet.endsWith(".xsl")) {
            stylesheet = stylesheet+".xsl";
        }
        String xslFile = JDOMUtils.getResourcePath(this, "resources/productserver/stylesheets/"+stylesheet);
        XSLTransformer transformer = new XSLTransformer(xslFile);
        LASDocument rowset = new LASDocument();
        JDOMUtils.XML2JDOM(new File(PATH), rowset);
        LASDocument transformedRowset = new LASDocument(transformer.transform(rowset));
        return transformedRowset.toString();
    }

 
    
    /**
     * Returns a WebRowSet implementation to match the database type for a given id
     * @param ID  -- the result id of the webrowset.  It had better be a result of type webrowset
     * @param db_type  -- the database implementation, either "mysql" or "oracle"
     * @return -- the webrowset object
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public WebRowSet getResultAsWebRowSet(String ID, String db_type) throws SQLException, FileNotFoundException, IOException {
    	WebRowSet rowset = null;
    	FileInputStream stream = null;
    	try {
			stream = new FileInputStream(new File(getResultAsFile(ID)));
			if ( db_type.equals("oracle") ) {
				rowset = new OracleWebRowSet();
				System.setProperty("javax.xml.parsers.SAXParserFactory","com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
			} else {
				rowset = new WebRowSetImpl();
			}
			if ( getResultElement(ID).getAttributeValue("type").equals("webrowset") ) {
			   rowset.readXml(stream);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if ( stream != null ) {
				stream.close();
			}
		}

    	return rowset;
    }

    /**
     * A convenience method that is equivalent to getResultTransformedByXSL(ID, "webrowsetToTable")
     * @param ID         the ID of the result you want to transform
     * @return           a String with the result of the transform
     * @throws JDOMException 
     * @throws IOException 
     */
    public String getResultAsTable(String ID) throws IOException, JDOMException {
        return getResultTransformedByXSL(ID, "webrowsetToTable.xsl");
    }
    /**
     * Take the suggested file names on the local server and construct URLs for those results
     * @param serverURL the URL of the local server
     */
    public void mapResultsToURL(String serverURL) {
        List responses = this.getRootElement().getChildren("response");
        for (Iterator respIt = responses.iterator(); respIt.hasNext();) {
            Element resp = (Element) respIt.next();
            List results = resp.getChildren("result");
            for (Iterator resIt = results.iterator(); resIt.hasNext();) {
                Element result = (Element) resIt.next();    
                String type = result.getAttributeValue("type");
                if (!type.equals("error")) {
                    String url = result.getAttributeValue("url");
                    // Replace the URL value if it's not already set by the backend service.               
                    if ( url == null || url.equals("") ) {
                        String newurl = result.getAttributeValue("file");
                        newurl = newurl.substring(newurl.lastIndexOf("/output/"), newurl.length());
                        newurl = serverURL + newurl;
                        result.setAttribute("url", newurl);
                    }
                }
            }
        }
    }
    /**
     * Take an LASResponse and merge its results with this one.  Use for building a
     * single response from a compound product.
     * @param lasResponse the response to be merged
     */
    public void merge(LASBackendResponse lasResponse) {
        
        // There should only be one in this docuemnt.
        Element response = (Element)lasResponse.getRootElement().getChild("response").clone();
        // Add it to the compound response...
        this.getRootElement().addContent(response);
        
    }
    /**
     * Set an id for this response
     * @param key the ID (usually the cache key)
     */
    public void setID(String key) {
        Element response = this.getRootElement().getChild("response");
        if ( response != null ) {
           response.setAttribute("ID", key);
        }
    }
    /**
     * Pull out the first error result from the response.
     * @return the error message for this error result
     */
    public String getError() {
        List responses = this.getRootElement().getChildren("response");
        for (Iterator respoIt = responses.iterator(); respoIt.hasNext();) {
            Element response = (Element) respoIt.next();
            List results = response.getChildren("result");
            for (Iterator resIt = results.iterator(); resIt.hasNext();) {
                Element result = (Element) resIt.next();
                if (result.getAttributeValue("type").equals("error")) {
                    return result.getText();
                }
            }
        }
        return "";
    }
    /**
     * See if this response contains an error result.
     * @return true if response has an error; false if not
     */
    public boolean hasError() {
        String error = getError();
        if ( error.equals("")) {
            return false;
        } else {
            return true;
        }
    }
    /**
     * Set an error for this response, which will remove all other results.
     * @param message the message for the error being set.
     */
    public void setError(String message) {       
        Element backend_response = getRootElement();
        Element debug = null;
        if ( backend_response == null ) {
            backend_response = new Element("backend_response");
            setRootElement(backend_response);
        } else {
            Element bug = getResultByType("debug");
            if ( bug != null ) {
                debug = (Element) bug.clone();
            }
        }
        backend_response.removeContent();
        Element response = new Element("response");
        response.setAttribute("ID", "error_response");
        Element result = new Element("result");
        result.setAttribute("type","error");
        result.setText(message);
        response.setContent(result);     
        if ( debug != null ) {
            response.addContent(debug);
        }
        setDate(response);
        backend_response.setContent(response);        
    }
    /**
     * Add an error result to the response.
     * @param message the error message to add
     */
    public void addError(String message) {
        Element response = getRootElement().getChild("response");
        Element result = new Element("result");
        result.setAttribute("type","error");
        result.setText(message);
        response.addContent(result);
    }
    /**
     * Turn this response into an error response using the message as the error message.
     * @param ID The ID of the error result
     * @param message the message for this error
     */
    public void setError(String ID, String message) {  
        Element backend_response = getRootElement();
        Element debug = null;
        if ( backend_response == null ) {
            backend_response = new Element("backend_response");
            setRootElement(backend_response);
        } else {
            Element bug = getResultByType("debug");
            if ( bug != null ) {
                debug = (Element) bug.clone();
            }
        }
        backend_response.removeContent();
        Element response = new Element("response");
        response.setAttribute("ID", "error_response");
        Element result = new Element("result");
        result.setAttribute("ID",ID);
        result.setAttribute("type","error");
        result.setText(message);
        response.setContent(result);  
        if ( debug != null ) {
            response.addContent(debug);
        }
        setDate(response);
        backend_response.setContent(response);        
    }
    /**
     * Add an error result to the response giving it a particular ID
     * @param ID the ID of the error result
     * @param message the error message
     */
    public void addError(String ID, String message) {
        Element response = getRootElement().getChild("response");
        Element result = new Element("result");
        result.setAttribute("ID", ID);
        result.setAttribute("type","error");
        result.setText(message);
        response.addContent(result);
    }
    /**
     * Add the date time/stamp to the response element.
     * @param response
     */
    private void setDate(Element response) {
        DateTime now = new DateTime();
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        Element date = new Element("date");
        date.setText(fmt.print(now));
        response.addContent(date);
    }
    /**
     * Get the date from the response.  All responses should be tagged with a
     * date/time stamp when they are created.
     * @return the data/time string
     */
    public String getDate() {
        Element root = getRootElement();
        List responses = root.getChildren("response");
        if (responses.size() == 0) {
            return "";
        } else if (responses.size() == 1) {
            Element response = root.getChild("response");
            if ( response != null ) {
               Element date = response.getChild("date");
               if ( date != null ) {
                   return date.getTextNormalize();
               } else {
                   return "";
               }
            } else {
                return "";
            }
        }else {
            String dateString = "";
            for (int i = 0; i < responses.size(); i++) {
                Element response = (Element) responses.get(i);
                Element date = response.getChild("date");
                if ( date != null ) {
                   dateString = "Response "+i+" generated at "+date.getTextNormalize() + "\n";
                }
            }
            return dateString;
        }
    }
    /**
     * Set this as an error response using the Java Exception for a detailed message.
     * @param message The LAS message
     * @param e the Exception from which to get the details of the failure.
     */
    public void setError(String message, Exception e) {
        Element backend_response = getRootElement();
        Element debug = null;
        if ( backend_response == null ) {
            backend_response = new Element("backend_response");
            setRootElement(backend_response);
        } else {
            Element bug = getResultByType("debug");
            if ( bug != null ) {
                debug = (Element) bug.clone();
            }
        }
        backend_response.removeContent();
        Element response = new Element("response");
        Element result = new Element("result");
        result.setAttribute("type","error");
        result.setAttribute("ID", "las_message");
        result.setText(message);
        response.setContent(result);
        Element result_exception = new Element("result");
        result_exception.setAttribute("ID", "exception_message");
        result_exception.setAttribute("type", "error");
        result_exception.setText(e.getMessage());
        response.addContent(result_exception);
        setDate(response);
        
        if ( debug != null ) {
            response.addContent(debug);
        }
        backend_response.setContent(response); 
    }
    
    /**
     * Get a remote result by type
     * @param in_type the result type that we are checking
     * @return true if the result was supplied by a remote service
     */
    public boolean isResultByTypeRemote(String in_type) {
        Element result = getResultByType(in_type);
        if ( result == null ) {
            return false;
        }
        String remote = result.getAttributeValue("remote");
        if ( remote != null && remote.equals("true")) {
            return true;
        }
        return false;
    }
    /**
     * Make a new LASBackendResponse without the exception_message.
     * @return LASBackendResponse - the shortened LASBackendResponse
     */
    public LASBackendResponse brief() {
        LASBackendResponse brief = new LASBackendResponse();
        Element brief_response = new Element("response");
        List responses = this.getRootElement().getChildren("response");
        for (Iterator respIt = responses.iterator(); respIt.hasNext();) {
            Element response = (Element) respIt.next();
            List results = response.getChildren("result");
            for (Iterator resIt = results.iterator(); resIt.hasNext();) {
                Element result = (Element) resIt.next();
                if ( !result.getAttributeValue("ID").equals("exception_message")) {
                    brief_response.addContent((Element)result.clone());
                }
            }
        }
        brief.getRootElement().addContent(brief_response);
        return brief;
    }
	public void setOpenId(String openid) {
		
		getRootElement().setAttribute("openid", openid);
		
	}
	public void addMapScale(LASMapScale lasMapScale) {
		Element result = getResultByType("map_scale");
		result.addContent((Element)lasMapScale.getRootElement().clone());	
	}
	public void makeResult(String absolutePath) {
		Element result = new Element("result");
		result.setAttribute("file", absolutePath);
		result.setAttribute("ID", absolutePath);
		result.setAttribute("type", "ClimateAnalysisPlot");
		addResult(result);
	}
}

