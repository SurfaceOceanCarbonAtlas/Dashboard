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
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.owasp.encoder.Encode;

/**
 *The Document class is the base class for all XML documents used by the IOSP code.
 * @author Roland Schweitzer
 *
 */
public class IOSPDocument extends Document {
    
    /*
	 * Any number that that uniquely identifies the version of this class' code.  
	 * The Eclipse IDE will generate it automatically for you.  We do not depend on this
	 * since we do not serialize our code across the wire.
	 */
	private static final long serialVersionUID = 4277259190947621274L;
	
	/**
	 * The default constructor which invokes the super class org.jdom.Document.
	 */
    public IOSPDocument() {
        super();
    }
    /**
     * Turn an existing JDOM document into an IOSPDocument.
     * @param doc The document to be used.
     */
    public IOSPDocument(Document doc) {
         setContent(doc.cloneContent());
    }
    /**
     * A utility class that returns an element based on the XPath to that element.
     * @param xpathValue the XPath string of the element to be found.
     * @return the element specified by the XPath.
     * @throws JDOMException
     */
    public Element getElementByXPath(String xpathValue) throws JDOMException {
        // E.g. xpathValue="/lasdata/operations/operation[@ID='Plot']"
    	String xpv = Encode.forJava(xpathValue);
        Object jdomO = this;
        XPath xpath = XPath.newInstance(xpv);
        return (Element) xpath.selectSingleNode(jdomO);   
    }
    
    /**
     * Returns the document as a String without any extra white space.
     * @return the document as a compact string.
     */
    public String toCompactString() {
        Format format = Format.getCompactFormat();
        format.setOmitDeclaration(true);
        return "<?xml version=\"1.0\"?>"+toString(format);
    }
    /**
     * Returns the document encoded by the URLEncoder class after striping carriage returns and line feeds.
     * @return the encoded string
     * @throws UnsupportedEncodingException
     */
    public String toEncodedURLString() throws UnsupportedEncodingException {
        Format format = Format.getCompactFormat();
        format.setOmitDeclaration(true);
        String xml = toString(format);
        xml = xml.replaceAll("\\r\\n","");
        xml = xml.replaceAll("\\r","");
        xml = xml.replaceAll("\\n","");
        return URLEncoder.encode("<?xml version=\"1.0\"?>"+xml, "UTF-8");
    }
    /**
     * An attempt to return an encoded string that is usable in JavaScript variable assignments.
     * @return the encoded string
     * @throws UnsupportedEncodingException
     */
    public String toEncodedJavaScriptSafeURLString() throws UnsupportedEncodingException  {
        Format format = Format.getCompactFormat();
        format.setOmitDeclaration(true);
        String xml = toString(format);
        
        xml = xml.replaceAll("\"","\\\\\"");
        xml = xml.replaceAll("'","\\\\'");;
        xml = xml.replaceAll("\\r\\n","");
        xml = xml.replaceAll("\\r","");
        xml = xml.replaceAll("\\n","");
        return URLEncoder.encode("<?xml version=\"1.0\"?>"+xml, "UTF-8");
    }
    /**
     * Attempt to return the document as a string that can be used in a JavaScript variable assignment.
     * @return the docuement as a string
     */
    public String toJavaScriptSafeString() {
        Format format = Format.getCompactFormat();
        format.setOmitDeclaration(true);
        String xml = toString(format);
        xml = xml.replaceAll("'","\\\\'");
        xml = xml.replaceAll("\"","\\\\\"");
        xml = xml.replaceAll("\\r\\n","");
        xml = xml.replaceAll("\\r","");
        xml = xml.replaceAll("\\n","");
        return "<?xml version=\"1.0\"?>"+xml;
    }
    /**
     * Return a pretty printed string formatted according to the JDOM Format 
     * @param format the Format object that controls the pretty printing.
     * @return the pretty printed string.
     */
    public String toString(Format format) {
        StringWriter xmlout = new StringWriter();
        try {
            format.setLineSeparator(System.getProperty("line.separator"));
            XMLOutputter outputter = new XMLOutputter(format);
            outputter.output(this, xmlout);
            // Close the FileWriter
            xmlout.close();
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return xmlout.toString();
    }
    /**
     * Convert the document to a string use the JDOM default getPrettyPrint format.
     */
    public String toString() {
        Format format = Format.getPrettyFormat();
        return toString(format);
    }
    /**
     * Write the document to the file specified by the path in the input String
     * @param fileName the file path
     */
    public void write(String fileName) {
        File file = new File(fileName);
        write(file);
    }
    /**
     * Write the document to the File
     * @param file the File to write to
     */
    public void write(File file) {
    	FileWriter xmlout = null;
        try {
            xmlout = new FileWriter(file);
            org.jdom.output.Format format = org.jdom.output.Format
            .getPrettyFormat();
            format.setLineSeparator(System.getProperty("line.separator"));
            XMLOutputter outputter = new XMLOutputter(format);
            outputter.output(this, xmlout);
            // Close the FileWriter
            xmlout.close();
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
        	if ( xmlout != null ) {
        		try {
					xmlout.close();
				} catch (IOException e) {
					//
				}
        	}
        }
        
    }
    
}
