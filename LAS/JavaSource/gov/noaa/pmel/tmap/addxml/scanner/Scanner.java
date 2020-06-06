package gov.noaa.pmel.tmap.addxml.scanner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import gov.noaa.pmel.tmap.addxml.ADDXMLProcessor;
import gov.noaa.pmel.tmap.addxml.CatalogRefHandler;
import gov.noaa.pmel.tmap.addxml.CategoryBean;
import gov.noaa.pmel.tmap.addxml.FilterBean;
import gov.noaa.pmel.tmap.addxml.addXML;
import gov.noaa.pmel.tmap.jdom.LASDocument;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.jdom.Document;
import org.jdom.Element;
import org.xml.sax.SAXException;

import thredds.catalog.InvCatalog;
import thredds.catalog.InvCatalogFactory;


public class Scanner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ScannerOptions scannerOptions = new ScannerOptions();
		InvCatalogFactory catfactory = new InvCatalogFactory("default", false);
		String catalogURL = "";
	    String catalogName = "";
	    try {
	    	CommandLineParser parser = new GnuParser();
	        CommandLine line = parser.parse( scannerOptions, args );
	        String scan = line.getOptionValue("scan");
	        String regex = line.getOptionValue("regex");
	        boolean esg = line.hasOption("esg");
	        String catoption = line.getOptionValue("catregex");
	        
	        boolean noregex = false;
	        if ( regex == null || regex.equals("") ) noregex = true;
	        if ( scan == null ) {
	        	HelpFormatter formatter = new HelpFormatter();
	        	formatter.printHelp("scanner", scannerOptions);
	        	System.exit(1);
	        }
		    String host = scan.substring(scan.indexOf("http://")+7);
		    String base = "http://"+host.substring(0, host.lastIndexOf("/"));
			SAXParserFactory factory = SAXParserFactory.newInstance();
			CatalogRefHandler esgCatalogHandler = new CatalogRefHandler();
			SAXParser xmlparser = null;	
			try {
				factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
				factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
				xmlparser = factory.newSAXParser();
				xmlparser.parse(scan, esgCatalogHandler);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Map<String, String> catalogs = esgCatalogHandler.getCatalogs();
			ADDXMLProcessor addxml = new ADDXMLProcessor();
			addxml.setVerbose(true);
			HashMap<String, String> addxmlOptions = new HashMap<String, String>();
			addxmlOptions.put("force", "t");
			addxmlOptions.put("oneDataset", "true");
			addxmlOptions.put("category", "true");
			addxmlOptions.put("esg", String.valueOf(esg));
			Map<String, CategoryBean> cats = new HashMap<String, CategoryBean>();
			String[] catregex = new String[0];
			if ( catoption != null ) {
				catregex = catoption.split(",");
				for (int i = 0; i < catregex.length; i++) {
					CategoryBean cb = new CategoryBean();
					cb.setName(catregex[i]);
					cats.put(catregex[i], cb);
				}
			}
			addxml.setOptions(addxmlOptions);
			for (Iterator catIt = catalogs.keySet().iterator(); catIt.hasNext();) {
				catalogName = (String) catIt.next();
				catalogURL = catalogs.get(catalogName);
				String filename;
				if ( !catalogURL.startsWith("http://") ) {
					filename = "LAS-"+catalogURL.replaceAll("/", "-");
					catalogURL = base + "/" + catalogURL;
				} else {
					filename = "LAS-"+catalogURL.substring(catalogURL.indexOf("http://"+7)).replaceAll("/", "-");
				}
				System.out.println("Scanning "+catalogURL);
				
				File outfile = new File(filename);
				if ( !outfile.exists() ) {
					if ( noregex || Pattern.matches(regex, catalogURL)) {
						InvCatalog catalog = (InvCatalog) catfactory.readXML(catalogURL);
						LASDocument las = (LASDocument) addxml.createXMLfromTHREDDSCatalog(catalog);
						List childs = las.getRootElement().getChild("datasets").getChildren();
						if ( childs.size() > 0 ) {
							Element d  = (Element) childs.get(0);
							for ( int i = 0; i < catregex.length; i++ ) {
								if ( Pattern.matches(".*"+catregex[i]+".*", d.getName())) {
									CategoryBean c = new CategoryBean();
									c.setName(d.getAttributeValue("name"));
									FilterBean f = new FilterBean();
									f.setAction("apply-dataset");
									f.setContainstag(d.getName());
									c.addFilter(f);
									cats.get(catregex[i]).addCategory(c);
								}
							}
						}
						// I just want the datasets, grids and axes stubs written to a file...
						File file = new File(filename);
						las.writeElement("datasets", file, false);
						las.writeElement("grids", file, true);
						las.writeElement("axes", file, true);
					}
				}
				
			}
			Element lc = new Element("las_categories");
			for (Iterator catsIt = cats.keySet().iterator(); catsIt.hasNext();) {
				String key = (String) catsIt.next();
				CategoryBean c = cats.get(key);
				lc.addContent(c.toXml());
			}
			ADDXMLProcessor.outputXML("LAS-Categories.xml", lc, true);
	    } catch( ParseException exp ) {
	        System.err.println( "Argument parsing failed.  Reason: " + exp.getMessage() );
	    } catch (ParserConfigurationException e) {
			System.err.println("Catalog parsing failed for: "+catalogURL);
		} catch (SAXException e) {
			System.err.println("Catalog parsing failed for: "+catalogURL);
		}
	    
	}
}
