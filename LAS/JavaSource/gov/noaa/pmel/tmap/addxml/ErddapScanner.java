package gov.noaa.pmel.tmap.addxml;

import gov.noaa.pmel.tmap.las.proxy.LASProxy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.http.HttpException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;


public class ErddapScanner {

	protected static String url = "http://osmc.noaa.gov/erddap/tabledap/";
	protected static String id = "OSMCV4_DUO_SURFACE_TRAJECTORY";
	protected static String search = "index.json?page=1&itemsPerPage=1000";
	protected static String category_file = null;
	protected static boolean verbose = false;
	protected static String title = null;
	protected static boolean hours = false;
	protected static boolean minutes = false;
	protected static double hours_step;
	protected static String[] properties;
	protected static String[] varproperties;
	protected static String[] skip;
	protected static String[] display;
	protected static boolean separate_files = false;
	protected static String basename = "las_from_erddap";
	
	protected static boolean auto_display = false;

	protected static ErddapScannerOptions opts = new ErddapScannerOptions();
	protected static CommandLine cl;

	protected static LASProxy lasProxy = new LASProxy();
	
	static int ukx = 0;
	static int uky = 0;
	static int ukz = 0;
	static int ukt = 0;
	
	static int uktype = 0;
	
	static int total = 0;
	
	static int count_profile = 0;
	static int count_timeseries = 0;
	static int count_trajectory = 0;
	static int count_trajectoryProfile = 0;
	static int count_point = 0;
	
	static int noread = 0;

	/**
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {

		try {
			CommandLineParser parser = new GnuParser();

			cl = parser.parse(opts, args);
			properties = cl.getOptionValues("property");
			varproperties = cl.getOptionValues("varprop");
			display = cl.getOptionValues("display");
			// -d with no options
			if ( cl.hasOption("display") && display == null ) {
				auto_display = true;
			}
			skip = cl.getOptionValues("skip");
			url = cl.getOptionValue("url");
			id = cl.getOptionValue("id");
			verbose = cl.hasOption("verbose");
			title = cl.getOptionValue("title");
			hours = cl.hasOption("hours");
			if ( hours ) {
				String hsv = cl.getOptionValue("hours");
				hours_step = Double.valueOf(hsv).doubleValue();
			}
			minutes = cl.hasOption("minutes");
			category_file = cl.getOptionValue("category");
			if ( !url.endsWith("/") ) {
				url = url+"/";
			}
			if ( hours && minutes ) {
				String header = "Either choose to use hours or minutes, but not both.";
				String footer = "";
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("addDiscrete", header, opts, footer, true);
				System.exit(-1);
			}
			separate_files = cl.hasOption("files");
			
		} catch (ParseException e) {
			String header = "An error occurred with the command line processing\n";
			String footer = "";
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("addDiscrete", header, opts, footer, true);
			System.exit(-1);
		}
		String skipaxes = cl.getOptionValue("axes");
		List<String> axesToSkip = new ArrayList<String>();
		if ( skipaxes != null ) {
			// This is regex magic to split on empty strings, that follow the start of the string.
			axesToSkip = Arrays.asList(skipaxes.split("(?!^)"));
		}
		ErddapProcessor processor = new ErddapProcessor(axesToSkip);
		if ( id != null ) {
			// Process the given data set.
			processor.process(url, id, false, auto_display, false, hours, hours_step, minutes, verbose, properties, varproperties, display, skip, false);
		} else {
			List<CategoryBean> categories = new ArrayList<CategoryBean>();
			CategoryBean othercat;
			if ( category_file != null ) {
				Document doc = new Document();
				try {
					JDOMUtils.XML2JDOM(new File(category_file), doc);
				} catch (IOException e) {
					System.err.println("Error processing category file: "+e.getMessage());
				} catch (JDOMException e) {
					System.err.println("Error processing category file: "+e.getMessage());
				}
				Element r = doc.getRootElement();
				List<Element> catsE = r.getChild("category").getChildren("category");
				for (Iterator iterator = catsE.iterator(); iterator.hasNext();) {
					Element cat = (Element) iterator.next();
					CategoryBean cb = new CategoryBean();
					cb.setName(cat.getAttributeValue("name"));
					cb.setID(cat.getAttributeValue("ID"));
					Element filter = cat.getChild("filter");
					FilterBean fb = new FilterBean();
					fb.setAction(filter.getAttributeValue("action"));
					fb.setContains(filter.getAttributeValue("contains"));
					cb.addFilter(fb);
					categories.add(cb);
					if ( cb.getID().equals("other_cat") ) {
						othercat = cb;
					}
				}
			}
			String s = url+search;
			InputStream stream = null;
			try {
				stream = lasProxy.executeGetMethodAndReturnStream(s, null);
			} catch (HttpException e) {
				System.err.println("DAS processing error: "+e.getMessage());
			} catch (IOException e) {
				System.err.println("DAS processing error: "+e.getMessage());
			}
			if ( stream != null ) {
				InputStreamReader reader = new InputStreamReader(stream);
				JsonStreamParser jp = new JsonStreamParser(reader);
				JsonObject tabledap_list = (JsonObject) jp.next();
				JsonArray rows = (JsonArray) ((JsonObject) (tabledap_list.get("table"))).get("rows");
				File categoriesFile = new File("las_categories.xml");
				CategoryBean topCat = new CategoryBean();
				topCat.setID("erddap_cats");
				if ( title == null ) title = "Data from ERDDAP";
				topCat.setName(title);
				
				List<CategoryBean> cats = new ArrayList<CategoryBean>();
				
				int limit = rows.size();

				// The first one is a listing of all data sets, not the first tabledap data set.
				for (int i = 1; i < limit; i++) {
					total++;
					JsonArray row = (JsonArray) rows.get(i);
					String fullurl = row.get(2).getAsString();
					String title = row.get(7).getAsString();

					String u = fullurl.substring(0, fullurl.lastIndexOf("/"));
					String uid = fullurl.substring(fullurl.lastIndexOf("/")+1);


					boolean append = false;
					if ( i > 1 ) append = true;

					ErddapReturn r = processor.process(u, uid, append, auto_display, true, hours, hours_step, minutes, verbose, properties, varproperties, display, skip, separate_files);
					// Only include the XML if it looks like it's fully specified.
					if ( r.write ) {
						if (r.type.equals(CdmDatatype.PROFILE) ) {
							count_profile++;
						} else if ( r.type.equals(CdmDatatype.TRAJECTORYPROFILE) ) {
							count_trajectoryProfile++;
						} else if ( r.type.equals(CdmDatatype.TRAJECTORY) ) {
							count_trajectory++;
						} else if ( r.type.equals(CdmDatatype.TIMESERIES) ) {
							count_timeseries++;
						} else if ( r.type.equals(CdmDatatype.POINT) ) {
							count_point++;
						}
						CategoryBean cat = new CategoryBean();
						cat.setName(title);
						cat.setID("cat_"+uid);

						FilterBean filter = new FilterBean();
						filter.setAction("apply-dataset");
						filter.setContainstag("id-"+uid);
						cat.addFilter(filter);
						
						if ( categories.size() == 0 ) {
							cats.add(cat);
						} else {
							// Figure out where to add the new cat.
							for (Iterator iterator = categories.iterator(); iterator.hasNext();) {
								CategoryBean categoryBean = (CategoryBean) iterator.next();
								String match = ((FilterBean) categoryBean.getFilters().get(0)).getContains();
								if ( title.contains(match) ) {
									categoryBean.addCategory(cat);
								}
							}
						}

						if ( categories.size() == 0 ) {
							topCat.setCategories(cats);
						} else {
							topCat.setCategories(categories);
						}
						

					} else {
						if ( r.type != null && r.type.equals("unknown") ) uktype++;
						if ( r.type != null && r.type.equals("failed") ) noread++;
						if ( r.unknown_axis != null ) {
							if ( r.unknown_axis.equals("x") ) {
								ukx++;
							} else if (r.unknown_axis.equals("y") ) {
								uky++;
							} else if ( r.unknown_axis.equals("z") ) {
								ukz++;
							} else if ( r.unknown_axis.equals("t") ) {
								ukt++;
							}
						}
					}

				}
				Element lc = new Element("las_categories");
//				List<CategoryBean> c = topCat.getCategories();
//				for (Iterator catIt = c.iterator(); catIt.hasNext();) {
//					CategoryBean cbean = (CategoryBean) catIt.next();
//					cbean.setFilters(new ArrayList<FilterBean>());
//				}
				lc.addContent(topCat.toXml());
				processor.outputXML(categoriesFile, lc, false);
				System.out.println(total+" dataset were examined.");
				System.out.println(count_point+" point data sets were configured into LAS.");
				System.out.println(count_profile+" profile data sets were configured into LAS.");
				System.out.println(count_trajectory+" trajectory data sets were configured into LAS.");
				System.out.println(count_trajectoryProfile+" trajectory profile data sets were configured into LAS.");
				System.out.println(count_timeseries+" timeseries data sets were configured into LAS.");
				
				System.out.println(uktype + " data set were of a type that LAS cannot currently use.");
				System.out.println(noread + " data sets could not be read from the server.");
				
				System.out.println(ukx + " data sets had X limits which could not be determined.");
				System.out.println(uky + " data sets had Y limits which could not be determined.");
				System.out.println(ukz + " data sets had Z limits which could not be determined.");
				System.out.println(ukt + " data sets had T limits which could not be determined.");


			}
		}
	}
}
