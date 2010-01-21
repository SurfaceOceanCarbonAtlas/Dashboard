package gov.noaa.pmel.tmap.addxml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.filter.Filter;

import thredds.catalog.InvAccess;
import thredds.catalog.InvCatalog;
import thredds.catalog.InvCatalogConvertIF;
import thredds.catalog.InvCatalogFactory;
import thredds.catalog.InvCatalogImpl;
import thredds.catalog.InvDatasetImpl;
import thredds.catalog.ServiceType;

public class Cleaner {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,S");
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		InvCatalogFactory factory = new InvCatalogFactory("default", true);
		if ( args[0] == null || args[0].equals("") ) {
			error("Cleaner catalog.xml true|false (file to clean and whether to make aggregation ncML.", 0);
		}

		File source = new File(args[0]);
		Document uaf = new Document();

		boolean aggregations = false;
		if ( args.length > 1 ) {
			if ( args[1] != null && (args[1].equals("true") || args[1].equals("false")) ) {
				aggregations = Boolean.valueOf(args[1]);
			}
		}

		try {
			JDOMUtils.XML2JDOM(source, uaf);
		} catch (Exception e) {
			error("Trouble reading source catalog: " + e.getMessage(), 0);
		}

		Namespace xlink = Namespace.getNamespace("http://www.w3.org/1999/xlink");

		for (Iterator catalogRefs = uaf.getDescendants(new CatalogRefFilter()); catalogRefs.hasNext();) {
			Element catalogRef = (Element) catalogRefs.next();

			String data = catalogRef.getAttributeValue("href", xlink);

			InvCatalog catalog;
			try {
				catalog = (InvCatalog) factory.readXML(data);

				StringBuilder buff = new StringBuilder();
				if (!catalog.check(buff, false)) {
					error("Invalid catalog " + data + "\n" + buff.toString(), 1);

				} else {
					info("Cleaning: "+data, 0);
					CatalogCleaner cleaner = null;
					try {
						cleaner = new CatalogCleaner(catalog, aggregations);
					} catch (UnsupportedEncodingException e) {

						e.printStackTrace();
					} catch (URISyntaxException e) {

						e.printStackTrace();
					}
					InvCatalogImpl clean = null;
					if ( cleaner != null ) {
						try {
							clean = cleaner.cleanCatalog();
						} catch (Exception e) {

							e.printStackTrace();
						}
					}

					if ( clean != null ) {

						try {

							// Write to a file...
							String f = JDOMUtils.MD5Encode(data)+".xml";
							File file = new File(f);
							FileOutputStream out = new FileOutputStream(file);
							factory.writeXML(clean, out, true);
							out.close();
						} catch (IOException e) {

							e.printStackTrace();
						}
					}
				}

			} catch (Exception e) {
				error("Could not read catalog " + data + "\n" + e.getLocalizedMessage(), 1);
			}
		}
	}
	public static void info(String message, int level) {
		out(message, System.out, level);   	
	}
	public static void error(String message, int level) {
		out(message, System.err, level);
	}
	private static void out(String message, PrintStream stream, int level) {
		if ( level == 0 ) {
			stream.println(dateFormat.format(new Date())+" "+message);
		} else if ( level == 1 ) {
			stream.println(dateFormat.format(new Date())+"\t ... "+message);
		} else if ( level >= 1 ) {
			stream.println(dateFormat.format(new Date())+"\t\t ... "+message);   	
		}
	}
}
