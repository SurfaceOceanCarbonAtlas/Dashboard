package gov.noaa.pmel.tmap.las.product.server;

import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.las.client.ClimateAnalysis;
import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.jdom.LASBackendRequest;
import gov.noaa.pmel.tmap.las.jdom.LASBackendResponse;
import gov.noaa.pmel.tmap.las.jdom.LASConfig;
import gov.noaa.pmel.tmap.las.jdom.LASRSSFeed;
import gov.noaa.pmel.tmap.las.jdom.ServerConfig;
import gov.noaa.pmel.tmap.las.product.request.ProductRequest;
import gov.noaa.pmel.tmap.las.service.BackendService;
import gov.noaa.pmel.tmap.las.service.ProductLocalService;
import gov.noaa.pmel.tmap.las.service.database.DatabaseBackendService;
import gov.noaa.pmel.tmap.las.service.ferret.FerretBackendService;
import gov.noaa.pmel.tmap.las.service.kml.KMLBackendService;
import gov.noaa.pmel.tmap.las.service.tabledap.TabledapBackendService;
import gov.noaa.pmel.tmap.las.util.Constants;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ProductServerRunner  extends Thread  {

    protected ProductRequest productRequest = null;
    protected LASConfig lasConfig = null;
    protected ServerConfig serverConfig = null;
    protected HttpServletRequest request = null;
    protected Cache cache = null;  
    protected String errorAction = null;    
    protected LASBackendResponse compoundResponse = null;
    protected int currentOp;
    protected int statusOp;
    protected String runningDots;
    protected boolean stillWorking;
    protected String serverURL;
    protected boolean cancel = false;
    protected HashSet<String> emails = null;
    protected LASRSSFeed lasRSSFeed = null;
    protected String JSESSIONID = null;
    protected boolean batch;
    protected boolean error;
    protected boolean previous_batch_error;
    protected long start;
    protected long current_start;
    
    private String BATCH_ERROR = "batch_error";
    private String ERROR = "error";
    private String CANCEL = "cancel";

    private static Logger log = LoggerFactory.getLogger(ProductServerRunner.class.getName());
    public ProductServerRunner (ProductRequest productRequest, LASConfig lasConfig, ServerConfig serverConfig, HttpServletRequest request, Cache cache) {
        this.productRequest = productRequest;
        this.lasConfig = lasConfig;
        this.serverConfig = serverConfig;
        this.request = request;
        this.cache = cache;
        this.currentOp = 0;
        this.statusOp= 0;
        this.emails = new HashSet<String>();
        this.runningDots = "***";
        this.start=0;

        this.error = false;
        this.batch = false;
        this.previous_batch_error = false;

        this.errorAction = null;
        this.compoundResponse = new LASBackendResponse();
        String openid = request.getParameter("openid");
        if ( openid != null && !openid.equals("") ) {
            this.compoundResponse.setOpenId(openid);
        }
        HttpSession session = request.getSession();
        try {
            serverURL = lasConfig.getBaseServerURL();
        } catch (JDOMException e) {
            setError(session, "Error getting the base server URL.", e.toString());  
        }
        
        // Set up the RSS Feed file.
        lasRSSFeed = new LASRSSFeed();
    }

    public void run() {

        log.debug("ProductServerRunner started.");
        HttpSession session = request.getSession();
        stillWorking = true;
       
        // Check the cache.
        boolean globalUseCache = productRequest.getUseCache();
        String compoundResponseFileName = lasConfig.getOutputDir()+File.separator+productRequest.getCacheKey()+"_response.xml";
        String requestFileName = lasConfig.getOutputDir()+File.separator+productRequest.getCacheKey()+"_request.xml";
        File compoundResponseFile = null;
        boolean hit = false;
        if (globalUseCache) {           
            synchronized (cache) {                 
                compoundResponseFile = cache.getFile(compoundResponseFileName, Cache.GET_CACHE);
            }
            if (compoundResponseFile != null ) {
                try {
                    JDOMUtils.XML2JDOM(compoundResponseFile, compoundResponse);
                } catch (Exception e) {
                    setError(session, "Error parsing the XML returned from the backend server.", e.toString());                
                }
            }
            /*
             * Previous attempt at this batch job produced an error.
             * Show the error to the user and ask them if they want to try again.
             */
            if ( compoundResponse.hasError() ) {
                hit = true; 
                cache.remove(compoundResponseFileName);
                previous_batch_error = true;
                errorAction = BATCH_ERROR;
                stillWorking=false;
            } else {
                hit = cache.cacheHit(compoundResponse);
                if ( hit ) {
                	String openid = request.getParameter("openid");
                    if ( openid != null && !openid.equals("") ) {
                        this.compoundResponse.setOpenId(openid);
                    }
                }
            }
        } 


        log.debug("Use cache for all requests in this product:" + globalUseCache);
        log.debug("Cache hit for all requests in this product:"  + hit);
        log.debug("Previous batch error for this request is:"  + previous_batch_error);

        // If the cache is off or there is no cache hit or there was no error on the previous job
        // (stillWorking=false) then start processing requests.  Otherwise, we're done!
        if ( !globalUseCache || !hit || !previous_batch_error) {           

            

            // Loop through the requests and send each to the
            // ProductService class to access the appropriate WebService. 

            ArrayList requestXMLList = productRequest.getRequestXML();
	    // *kob* 3/2009 - move the start definition outside of loop so that it maintains the timing for the
	    //                complete product generation, not for each individual operation
            start = System.currentTimeMillis();
            for(int index = 0; index < requestXMLList.size(); index++ ) {
            	current_start = System.currentTimeMillis();
                int request = index+1;
                log.debug("Processing request "+request+" of " + requestXMLList.size());

                currentOp = index;

                writeRSSFeed("Still working on");

                LASBackendRequest backendRequestDocument = (LASBackendRequest)requestXMLList.get(index);

                // The key is the operation ID.
                String key = productRequest.getOperationID(index);
                String backendServerURL=null;
                String methodName=null;

                try {
                    backendServerURL = serverConfig.getServerURL(productRequest.getServiceName(key));
                    if ( backendServerURL == null || backendServerURL.equals("") ) {
                        throw new LASException("Server url for service named "+productRequest.getServiceName(key)+" is null or blank.");
                    }
                    methodName = serverConfig.getMethodName(productRequest.getServiceName(key));
                    if ( serverConfig.isRemote(productRequest.getServiceName(key) ) ) {
                        backendRequestDocument.runRemote();
                    }
                } catch (LASException e) {
                    setError(session, "Product request cannot find service URL for sub-operation of ID="+key+" in productserver.xml.", e.toString());
                    break;
                }

                if (cancel) {     
                    log.debug("Request cancelled:"+backendRequestDocument.toCompactString());
                    break;
                }

                String productCacheKey=null;
                try {
                    productCacheKey = productRequest.getCacheKey(key);
                } catch (LASException e) {
                    setError(session, "Can't find cache key for this request.", e.toString());
                    break;
                }

                if (cancel) {
                    log.debug("Request cancelled:"+backendRequestDocument.toCompactString());
                    break;
                }

                String responseXML=null;
                LASBackendResponse lasResponse = new LASBackendResponse();

                String responseFileName = lasConfig.getOutputDir()+File.separator+productCacheKey+"_response.xml";
                File responseFile = null;

                if (cancel) {
                    log.debug("Request cancelled:"+backendRequestDocument.toCompactString());
                    break;
                }

                boolean use_cache=true;
                try {
                    use_cache = productRequest.getUseCache(key);
                } catch (LASException e) {
                    setError(session, "Multiple definitions for the use_cache property found.", e.toString());
                    break;
                }

                if (cancel) {
                    log.debug("Request cancelled:"+backendRequestDocument.toCompactString());
                    break;
                }
                boolean isHit;
                if ( use_cache ) {
                    log.debug("Cache use is \"on\" for this request");
                    synchronized (cache) {                 
                        responseFile = cache.getFile(responseFileName, Cache.GET_CACHE);
                    }
                    if (responseFile != null ) {
                        try {
                            JDOMUtils.XML2JDOM(responseFile, lasResponse);
                        } catch (Exception e) {
                            setError(session, "Error parsing the XML response file from the cache.", e.toString());
                            break;
                        }
                        isHit = cache.cacheHit(lasResponse);
                    } else {
                    	isHit = false;
                    }                
                } else {
                    log.debug("Cache use is \"off\" for this request.");
                    isHit = false;
                }

                if (cancel) {
                    log.debug("Request cancelled:"+backendRequestDocument.toCompactString());
                    break;
                }
                // Not in the cache or don't use the cache...
                log.debug("Cache hit check returned: "+isHit+ " for request: " + request);
                if (!isHit ) {
                    
                    if (backendServerURL != null && backendServerURL.equals("local")) {
                        log.debug("Running local service.");
                        
                        try {
                            ProductLocalService productLocalService = new ProductLocalService(
                                    backendRequestDocument, backendServerURL,
                                    methodName, productCacheKey);
                            /*
                             * The gobbledygook below is the code necessary to invoke a method in a class when
                             * the name of the method is a variable (we know the class it's productLocalService).  
                             * The result if methodName="getTHREDDS" is to invoke the following:
                             * productLocalService.getTHREDDS(lasBackendRequest, lasConfig, serverConfig);
                             * 
                             * Add the time out tester here.  Could use a bit more generality.
                             */
                            Object[] oargs = null;
                            Method method = null;
                            if ( methodName.equals("getTHREDDS")) {
                            	Class[] args = new Class[3];
                            	args[0] = backendRequestDocument.getClass();
                            	args[1] = lasConfig.getClass();
                            	args[2] = serverConfig.getClass();
                            	method = productLocalService.getClass().getMethod(methodName, args);
                            	oargs = new Object[3];
                            	oargs[0] = backendRequestDocument;
                            	oargs[1] = lasConfig;
                            	oargs[2] = serverConfig;
                            	
                            } else if ( methodName.equals("fiveMinutes") ) {
                            	Class[] args = new Class[1];
                            	args[0] = backendRequestDocument.getClass();
                            	method = productLocalService.getClass().getMethod(methodName, args);
                            	oargs = new Object[1];
                            	oargs[0] = backendRequestDocument;
                            }
                            if (method != null ) {
                               method.invoke(productLocalService, oargs);
                            }
                            
                            
                            responseXML = productLocalService.getResponseXML();
                        } catch (Exception e) {
                            setError(session, "Error was returned from the local service.", e.toString());
                            break;
                        }
                    } else {
                        log.debug("Running backend service at "+backendServerURL+".");
                        
                        try {
                        	// Run the service here rather than over a SOAP connection...
                       
                        	String serviceName = productRequest.getServiceName(key);
                        	if ( serviceName.equals(Constants.FERRET_SERVICE) ) {
                        		FerretBackendService ferretService = new FerretBackendService();
                        		responseXML = ferretService.getProduct(backendRequestDocument.toString(), productCacheKey);
                        	} else if ( serviceName.equals(Constants.DATABASE_SERVICE) ) {
                        		DatabaseBackendService dbService = new DatabaseBackendService();
                        		responseXML = dbService.getProduct(backendRequestDocument.toString(), productCacheKey);
                        	} else if ( serviceName.equals(Constants.TABLEDAP_SERVICE) ) {
                        		TabledapBackendService tdService = new TabledapBackendService();
                        		responseXML = tdService.getProduct(backendRequestDocument.toString(), productCacheKey);
                        	} else if ( serviceName.equals(Constants.KML_SERVICE) ) {
                        		KMLBackendService kmlService = new KMLBackendService();
                        		responseXML = kmlService.mapScaleToKML(backendRequestDocument.toString(), productCacheKey);                        		
                        	}
                        	
//                            ProductWebService productWebService = new ProductWebService(
//                                    backendRequestDocument, backendServerURL,
//                                    methodName, productCacheKey);
//                            productWebService.run();
//                            responseXML = productWebService.getResponseXML();
                        	
                        } catch (Exception e) {
                            setError(session, "Error was returned from the backend server.", e.toString());
                            break;
                        }
                        if ( responseXML == null ) {
                            setError(session, "Response from backend service "+backendServerURL+" was null.", "Check to see if the server is running.");
                        }
                    }                    
                    if (cancel) {
                        log.debug("Request cancelled:"+backendRequestDocument.toCompactString());
                        break;
                    }   
                    try {
                        JDOMUtils.XML2JDOM(responseXML, lasResponse);
                    } catch (Exception e) {
                        setError(session, "Error parsing the XML returned from the backend server.", e.toString());
                        break;
                    }
                    if (cancel) {
                        log.debug("Request cancelled:"+backendRequestDocument.toCompactString());
                        break;
                    }
                }
                lasResponse.setID(key);

                lasResponse.mapResultsToURL(serverURL);

                if ( lasResponse.hasError() ) {
                    setError(session, lasResponse);
                    log.debug("Found error response.  Breaking out.");
                    break;
                }
                log.debug("No error response.  Continuing on.");
                if ( use_cache ) {
                    cache.addToCache(lasResponse, responseFileName);
                }
                if (cancel) {
                    log.debug("Request cancelled:"+backendRequestDocument.toCompactString());
                    break;
                }
                if ( !hit ) {
                    // Not a cache hit so add response.  Otherwise the response is complete.
                    compoundResponse.merge(lasResponse);
                }

                try {
                    productRequest.chainResults(key, compoundResponse);
                } catch (Exception e) {
                    setError(session, "Error occured chaining the compound operation.", e.toString());
                    break;
                }
                if (cancel) {
                    log.debug("Request cancelled:"+backendRequestDocument.toCompactString());
                    break;
                }
                log.debug("Finished request " + request);
            }      
        }


        String productCacheKey = productRequest.getCacheKey();
        String responseFileName = lasConfig.getOutputDir()+File.separator+productCacheKey+"_response.xml";
        if ( productRequest.getUseCache() && !previous_batch_error && ((!batch && !error) || batch) ) {
        	synchronized (cache) {    
        		if ( !hit ) {
        			// If there was no "global" cache hit, this must be recorded.  Otherwise, it came out of the cache.
                    cache.addToCache(compoundResponse, responseFileName);
                    cache.addDocToCache(productRequest.getLasRequest(), requestFileName);
        		}
        	}
        }

        stillWorking=false;
        synchronized (this) { notify(); }
        writeRSSFeed("Finished with");
        
        // Notify batch clients that the job is finished.
        
        if (getEmails().size() > 0 ) {
            String productURL="";
            try {
                productURL = productRequest.getLasRequest().toEncodedURLString();
            } catch (UnsupportedEncodingException e) {
                setError(session, "Unable to get request URL.", "User has not been notified by of failed request.");
            }
            try {
                sendEmails(getEmails(), serverConfig.getSMTPHost(), "The LAS Product for the operation "+productRequest.getOperationNames()+" has completed sucessfully."+"\nLook at: "+serverURL+"/ProductServer.do?xml="+productURL+" to see your requested product.");
            } catch (LASException e) {
                setError(session, "Error sending email notifications.", e.toString());
            }
        }

        log.debug("ProductServerRunner finished.");
    }
    private void setError(HttpSession session, LASBackendResponse lasResponse) {
        error = true;
        compoundResponse = lasResponse;
        session.setAttribute("las_response", compoundResponse);
        errorAction = ERROR;
        stillWorking=false; 
    }

    /**
     * @param emails
     * @param string
     * 
     * This method is based largely on the java mail demo and requires
     * this notice:
     * @throws MessagingException 
     * @(#)msgsendsample.java       1.19 03/04/22
     *
     * Copyright 1996-2003 Sun Microsystems, Inc. All Rights Reserved.
     *
     * Redistribution and use in source and binary forms, with or without
     * modification, are permitted provided that the following conditions
     * are met:
     *
     * - Redistributions of source code must retain the above copyright
     *   notice, this list of conditions and the following disclaimer.
     *
     * - Redistribution in binary form must reproduce the above copyright
     *   notice, this list of conditions and the following disclaimer in the
     *   documentation and/or other materials provided with the distribution.
     *
     * Neither the name of Sun Microsystems, Inc. or the names of contributors
     * may be used to endorse or promote products derived from this software
     * without specific prior written permission.
     *
     * This software is provided "AS IS," without a warranty of any kind. ALL
     * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
     * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
     * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND
     * ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES OR LIABILITIES
     * SUFFERED BY LICENSEE AS A RESULT OF  OR RELATING TO USE, MODIFICATION
     * OR DISTRIBUTION OF THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL
     * SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
     * FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
     * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
     * ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS
     * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
     *
     * You acknowledge that Software is not designed, licensed or intended
     * for use in the design, construction, operation or maintenance of any
     * nuclear facility.
     */
    private void sendEmails(HashSet<String> emails, String smtp_host, String message) throws LASException {
        InternetAddress addresses[] = new InternetAddress[emails.size()];
        int i = 0;
        for (Iterator emIt = emails.iterator(); emIt.hasNext();) {
            String address = (String) emIt.next();
            try {
                addresses[i] = new InternetAddress(address);
            } catch (MessagingException e) {
                log.warn(handleMessagingException(e));
            }
            i++;
            log.debug("Sending mail to: "+address+".");
        }
        // create some properties and get the default Session
        Properties props = new Properties();
        props.put("mail.smtp.host", smtp_host);
        Session session = Session.getInstance(props, null);
        Message msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress("no-reply@las-site"));
            msg.setRecipients(Message.RecipientType.TO, addresses);
            msg.setSubject("LAS Product Server Notification");
            msg.setSentDate(new Date());
            // If the desired charset is known, you can use
            // setText(text, charset)
            msg.setText(message);
            Transport.send(msg);
        } catch (MessagingException e) {
            log.warn(handleMessagingException(e));
        }
        
    }
    private String handleMessagingException(Exception e) {
        StringBuffer exmess = new StringBuffer();
        do {
            if (e instanceof SendFailedException) {
                SendFailedException sfex = (SendFailedException)e;
                Address[] invalid = sfex.getInvalidAddresses();               
                if (invalid != null) {
                    exmess.append("\n    ** Invalid Addresses");
                    if (invalid != null) {
                        for (int i = 0; i < invalid.length; i++)
                            exmess.append("         " + invalid[i]);
                    }
                }
                Address[] validUnsent = sfex.getValidUnsentAddresses();
                if (validUnsent != null) {
                    exmess.append("\n    ** ValidUnsent Addresses");
                    if (validUnsent != null) {
                        for (int i = 0; i < validUnsent.length; i++)
                            exmess.append("         "+validUnsent[i]);
                    }
                }
                Address[] validSent = sfex.getValidSentAddresses();
                if (validSent != null) {
                    exmess.append("\n    ** ValidSent Addresses");
                    if (validSent != null) {
                        for (int i = 0; i < validSent.length; i++)
                            exmess.append("         "+validSent[i]);
                    }
                }
            }
            if (e instanceof MessagingException)
                e = ((MessagingException)e).getNextException();
            else
                e = null;
        } while (e != null);
        return exmess.toString();
    }

    /**
     * 
     */
    private void writeRSSFeed(String message) {

        lasRSSFeed.setChannelLink(serverURL);
        String url = "";
        try {
            lasRSSFeed.setChannelTitle(lasConfig.getTitle());
            url = url + serverURL+"/ProductServer.do?xml="+productRequest.getLasRequest().toEncodedURLString();                      
        } catch (JDOMException e) {
            log.warn("Error setting title on RSS feed.");  
        } catch (UnsupportedEncodingException e) {
            log.warn("Error encoding the product URL for the RSS feed.");
        }
        if ( JSESSIONID != null && !JSESSIONID.equals("") ) {
            url = url + "&JSESSIONID="+JSESSIONID;
        }
        DateTime now = new DateTime(new Date()).withZone(DateTimeZone.UTC);
        DateTimeFormatter format = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss z");
        String pubDateText = format.print(now);
        lasRSSFeed.addItem(message+" "+productRequest.getOperationNames(), url, pubDateText);
        LASBackendRequest currentRequest = getCurrentBackendRequest();
        currentRequest.mapResultToURL(serverURL, "rss");
        String feedFileName = currentRequest.getResultAsFile("rss");
        File feedFile = new File(feedFileName);
        lasRSSFeed.write(feedFile);

    }

    public boolean stillWorking() {
        return stillWorking;
    }

    public void setCancel(boolean cancel) throws IOException {
    	File cancelFile = getCurrentBackendRequest().getCancelFile();
    	cancelFile.createNewFile();
    	error = true;
    	compoundResponse.setError("las_message", "LAS has asked the service to stop.");
    	compoundResponse.addError("exception_message", "You may keep working.");
    	errorAction = CANCEL;
    	stillWorking=false; 
    	this.cancel = cancel;
    }
    public int getCurrentOp() {
        return currentOp;
    }

    public LASBackendRequest getCurrentBackendRequest() {
        return (LASBackendRequest) productRequest.getRequestXML().get(currentOp);
    }

    /**
     * @return Returns the compoundResponse.
     */
    public LASBackendResponse getCompoundResponse() {
        return compoundResponse;
    }

    /**
     * @param compoundResponse The compoundResponse to set.
     */
    public void setCompoundResponse(LASBackendResponse compoundResponse) {
        this.compoundResponse = compoundResponse;
    }

    /**
     * @return Returns the action.
     */
    public String getErrorAction() {
        return errorAction;
    }

    /**
     * @param action The action to set.
     */
    public void setErrorAction(String action) {
        this.errorAction = action;
    }

    public ArrayList getStatus() {
        ArrayList<String> status = new ArrayList<String>();
        long time = (System.currentTimeMillis() - current_start) / 1000;
        for (int i = 0; i<productRequest.getRequestXML().size(); i++ ) {
            if ( i < currentOp ) {
                status.add(i, "done");
            } else if ( i == currentOp ) {
                if ( currentOp == statusOp ) {
                    runningDots = runningDots+"***";
                } else {
                    statusOp = currentOp;
                    runningDots = "***";
                }
		// status.add(i, "has been running for "+time+" seconds "+runningDots);
                // *kob*  Simplify timing message to only include the actual seconds LAS has been running 
                status.add(i, time+" seconds ");
            } else {
                status.add(i, "pending");
            }
        }
        return status;
    }
    
    public long getSeconds() {
    	return (System.currentTimeMillis() - start) / 1000;
    }
    
    public String getDate() {
    	DateFormat longFerretForm = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    	return longFerretForm.format(new Date());
    }

    public long getProgressTimeout() throws LASException {

        LASBackendRequest request = (LASBackendRequest) productRequest.getRequestXML().get(currentOp);
        return request.getProgressTimeout();
    }
 
    public LASBackendRequest getCurrentRequest() throws LASException {
    	return (LASBackendRequest) productRequest.getRequestXML().get(currentOp);
    }
    public HashSet<String> getEmails() {
        return emails;
    }

    public void setEmails(HashSet<String> emails) {
        this.emails = emails;
    }

    public void setEmails(String email_list) {
        String[] addresses = email_list.split(",");
        for (int i = 0; i < addresses.length; i++) {
            if ( addresses[i].contains("@") ) {
                emails.add(addresses[i].trim());
            }
        }
    }

    public void setJESSIONID(String JSESSIONID) {
        this.JSESSIONID = JSESSIONID;
    }

    public String getJESSIONID() {
        return this.JSESSIONID;
    }

    private void setError(HttpSession session, String las_error, String exception_message) {
        error = true;
        compoundResponse.setError("las_message", las_error);
        compoundResponse.addError("exception_message", exception_message);
        session.setAttribute("las_response", compoundResponse);
        errorAction = ERROR;
        stillWorking=false; 
    }
    
    public void setBatch(boolean batch) {
        this.batch = batch;
    }
    
    public boolean getBatch() {
        return batch;
    }
    
}

