/**
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development. 
 */
package gov.noaa.pmel.tmap.iosp;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;

import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import org.jdom.Element;
/**
 * A collection of helper methods for managing JDOM documents.
 * @author Roland Schweitzer
 *
 */
public class JDOMUtils {
    /**
     * Generates a Document from a XML input stream
     * 
     * @param is the input xml stream
     * @throws Exception if something goes wrong during the parsing process
     */
    public static void XML2JDOM(Reader is, Document doc) throws Exception {
        Document tdoc = new Document();
        SAXBuilder builder = new SAXBuilder();
        builder.setExpandEntities(false);
        builder.setEntityResolver(new MyResolver());
        tdoc = builder.build(is);
        Element root = tdoc.getRootElement();
        Element new_root = (Element) root.clone();
        doc.setRootElement(new_root);
    }
    
    /**
     * Generates a Document from a XML string.
     * 
     * @param xml the input xml string
     * @throws Exception if something goes wrong during the parsing process
     */
    public static void XML2JDOM(String xml, Document doc) throws Exception {
        StringReader sr = new StringReader(xml);
        XML2JDOM(sr, doc);
    }
    
    /**
     * Generates a Document from a XML file
     * 
     * @param file the input xml file handle
     * @throws Exception if something goes wrong during the parsing process
     */
    public static void XML2JDOM(File file, Document doc) throws Exception  {
        Document tdoc = new Document();
        SAXBuilder builder = new SAXBuilder();
        // builder.setExpandEntities(false);
        builder.setEntityResolver(new MyResolver());
        tdoc = builder.build(file);
        Element root = tdoc.getRootElement();
        Element new_root = (Element) root.clone();
        doc.setRootElement(new_root);
    }
    
    /**
     * Make an MD5 Hash from a particular string.
     * @param str - the string to be hashed.
     * @return md5 - the resulting hash
     * @throws UnsupportedEncodingException
     */
    public static String MD5Encode(String str) throws UnsupportedEncodingException {
        String returnVal = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte mdArr[] = md.digest(str.getBytes("UTF-16"));
            returnVal = toHexString(mdArr);
        } catch (Exception e) {
            returnVal = URLEncoder.encode(str,"UTF-8").replaceAll("\\*", "x");
        }
        return returnVal;
    }
    /**
     * Makes a nice String with the bytes of the MD5 Hash.
     * @param bytes the bytes to be converted.
     * @return hash - the resulting String.
     */
    protected static String toHexString(byte bytes[]) {
        char chars[] = new char[bytes.length * 2];
        
        for (int i = 0; i < bytes.length; ++i) {
            chars[2 * i] = HEXCODE[(bytes[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEXCODE[bytes[i] & 0x0F];
        }
        return new String(chars);
    }
    
    protected static final char HEXCODE[] = { '0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };   
    /**
     * Finds objects in the classpath.
     * @param object
     * @param resource
     * @return the fully qualified path name of the requested object.
     */
    public static String getResourcePath(Object object, String resource) {

        if ( resource.startsWith("/") ) {
           return resource;
        }
        
        URL resourceURL = object.getClass().getClassLoader().getResource(resource);
        
        if ( resourceURL != null ) {
            try {
                return resourceURL.toURI().getPath();
                
            } catch (URISyntaxException e) {
                return null; 
            }
            
        } else {
            return null;
        }
    }
    public static String decode(String encoded) throws UnsupportedEncodingException {
    	 String temp = URLDecoder.decode(encoded.toString(), "UTF-8");
    	 if ( temp.length() == encoded.length() ) {
    		 // Decoding was not necessary since it didn't get smaller to return the original.
    		 return encoded;
    	 } else {
    		 return temp;
    	 }
    }
    public static String decode(String encoded, String encoding) throws UnsupportedEncodingException {
    	 String temp = URLDecoder.decode(encoded.toString(), encoding);
    	 if ( temp.length() == encoded.length() ) {
    		 // Decoding was not necessary since it didn't get smaller to return the original.
    		 return encoded;
    	 } else {
    		 return temp;
    	 }
    }
}
