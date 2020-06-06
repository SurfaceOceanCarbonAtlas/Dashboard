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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactory;
import org.jdom.Document;
import org.jdom.Element;

import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.ma2.IndexIterator;
import ucar.ma2.InvalidRangeException;
import ucar.ma2.Range;
import ucar.ma2.Section;
import ucar.ma2.StructureDataIterator;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.ParsedSectionSpec;
import ucar.nc2.Structure;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.iosp.IOServiceProvider;
import ucar.nc2.util.CancelTask;
import ucar.unidata.io.RandomAccessFile;

/**
 * This is the implementation of the netCDF Java IO Service Provider interface
 * that turns Ferret scripts and the variables it accesses and defines into an
 * OPeNDAP netCDF data source. The variables that are defined by the script must
 * be associated with an existing open data set. E.g.
 * 
 * use levitus_climatology let/d=levitus_climatology temp_20 =
 * temp[d=levitus_climatology,z=0:20@sum] set
 * var/title="surface heat content"/units="deg C" temp_20[d=levitus_climatology]
 * use coads_climatology let/d=coads_climatology sst_5 =
 * SST[d=coads_climatology]*5.0
 * 
 * @author Roland Schweitzer
 * 
 */
public class FerretIOServiceProvider implements IOServiceProvider {
	private final static Logger log = LoggerFactory.getLogger(FerretIOServiceProvider.class
			.getName());
	RandomAccessFile raf;
	static private final long maxHeader = 512;

	/**
	 * The default constructor, warms up the FerretTool that will be used to run
	 * Ferret.
	 * 
	 */
	public FerretIOServiceProvider() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ucar.nc2.IOServiceProvider#close()
	 */
	public void close() throws IOException {
		if (raf != null) {
			raf.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ucar.nc2.IOServiceProvider#getDetailInfo()
	 */
	public String getDetailInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ucar.nc2.IOServiceProvider#isValidFile(ucar.unidata.io.RandomAccessFile)
	 */
	public boolean isValidFile(RandomAccessFile raf) throws IOException {
	    log.debug("Checking valid file for Ferret netCDF file.");
	    final byte[] MAGIC = new byte[]{0x43, 0x44, 0x46, 0x01};

	    byte[] netcdfmagic = new byte[4];
	    try {
	        raf.read(netcdfmagic);
	        if ( netcdfmagic[0] == MAGIC[0] && netcdfmagic[1] == MAGIC[1] && netcdfmagic[2] == MAGIC[2] ) {
	            // It's a netcdf 3 file...
	            return false;
	        }
	        raf.seek(0);

	        final byte[] head = {(byte) 0x89, 'H', 'D', 'F', '\r', '\n', 0x1a, '\n'};
	        final String hdf5magic = new String(head);


	        long size = raf.length();
	        long pos = 0;
	        byte[] hm = new byte[8];
	        while (pos < size && pos < maxHeader) {
	            raf.seek(pos);
	            raf.read(hm);
	            String magic = new String(hm);
	            if (magic.equals(hdf5magic)) {
	                // This is an HDF file...
	                return false;
	            }
	            pos++;
	        }




	        byte[] b = new byte[10];
	        pos = 0;


	        while (pos < size && pos < maxHeader) {
	            raf.seek(pos);
	            raf.read(b);
	            String magic = new String(b);
	            if (FerretCommands.containsCommand(magic)) {
	                log.debug("Yes, is valid");
	                return true;
	            }
	            pos++;
	        }
	    } catch (IOException e) {
	    } // fall through
	    log.debug("No, is not valid");
	    return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ucar.nc2.IOServiceProvider#open(ucar.unidata.io.RandomAccessFile,
	 * ucar.nc2.NetcdfFile, ucar.nc2.util.CancelTask)
	 */
	public void open(RandomAccessFile raf, NetcdfFile ncfile,
			CancelTask cancelTask) throws IOException {
		this.raf = raf;
		FerretTool tool;
		try {
			tool = new FerretTool();
		} catch (Exception e) {
			throw new IOException(e.toString());
		}
		log.debug("Opening " + raf.getLocation());
		raf.seek(0);
		StringReader sr;
		try {
			byte[] b = new byte[(int) raf.length()];
			raf.read(b);
			sr = new StringReader(new String(b));
		} catch (IOException e) {
			log.debug("IO Exception reading the random access file.");
			throw e;
		}

		// Use this script to run Ferret and produce the equivalent of
		// the netCDF header by reading the XML output from Ferret and building
		// all
		// the dimensions, variables and attributes.

		// Run the FerretTool to make the XML.

		String jnl = null;

		StringBuffer inJnl = new StringBuffer();

		BufferedReader jnlBuffReader = new BufferedReader(sr);
		try {
			String line = jnlBuffReader.readLine();

			while (line != null) {
				inJnl.append(line + "\n");
				line = jnlBuffReader.readLine();
			}
		} catch (IOException e) {
		}
		jnl = inJnl.toString();

		String cacheKey = JDOMUtils.MD5Encode(jnl);
		String xmlHeader = null;
		try {
			xmlHeader = tool.run_header("header.jnl", jnl, cacheKey);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		log.debug("process the XML header in " + xmlHeader);
		// Process the XML.
		Document header = new Document();

		if ( xmlHeader.contains("/..") || xmlHeader.contains("../") ) {
			throw new IOException("Illegal file name.");
		}
		File headerFile = new File(xmlHeader);
		try {
			JDOMUtils.XML2JDOM(headerFile, header);
		} catch (Exception e) {
			headerFile
			.renameTo(new File(headerFile.getAbsoluteFile() + ".bad"));
			log.error("Error processing header file XML " + e.getMessage());
			throw new IOException("Error processing XML header file "
					+ e.getMessage());
		}
		log.debug("document built " + header.toString());

		// First build a hash that identifies the data set to which an axis
		// belongs.
		HashMap<String, String> axis_datasets = new HashMap<String, String>();
		List data = header.getRootElement().getChild("datasets").getChildren(
				"dataset");
		for (Iterator dataIt = data.iterator(); dataIt.hasNext();) {
			Element dataset = (Element) dataIt.next();
			String dataset_name = dataset.getAttributeValue("name");
			List vars = dataset.getChildren("var");
			for (Iterator varIt = vars.iterator(); varIt.hasNext();) {
				Element var = (Element) varIt.next();
				List var_axes = var.getChild("grid").getChild("axes")
						.getChildren();
				for (Iterator axisIt = var_axes.iterator(); axisIt.hasNext();) {
					Element axis = (Element) axisIt.next();
					String axisName = axis.getTextNormalize();
					axis_datasets.put(axisName, dataset_name);
				}
			}
		}
		log.debug("Finished parsing the XML file.");
		// This creates the dimensions and the coordinate variables from the
		// Ferret XML description.
		List axes = header.getRootElement().getChild("axes")
				.getChildren("axis");

		log.debug("Found " + axes.size() + " axis elements.");

		// Keep a list of all the Dimensions created.
		ArrayList<Dimension> allDims = new ArrayList<Dimension>();

		for (Iterator axisIt = axes.iterator(); axisIt.hasNext();) {
			Element axisE = (Element) axisIt.next();
			// Collect all the attributes in a HashMap

			Map<String, FerretAttribute> ferretAttributes = getAttributes(axisE);

			String length = ferretAttributes.get("length").getValue();
			String name = axisE.getAttributeValue("name");
			log.debug("Working on axis: " + name);

			if (length != null) {

				String dimS = length.trim();
				Dimension dim = new Dimension(name, Integer.valueOf(dimS).intValue(), true);
				log.debug("New dim with size: " + dim.getLength());
				List<Dimension> dims = new ArrayList<Dimension>();
				dims.add(dim);
				allDims.add(dim);
				ncfile.addDimension(null, dim);
				Variable coord = new Variable(ncfile, null, null, name);
				coord.setDimensions(dims);
				coord.setDataType(DataType.DOUBLE);
				// See comment about attributes below...
				String datasetName = axis_datasets.get(name);
				if (datasetName != null) {
					coord.addAttribute(new Attribute("dataset", datasetName));
				}
				putAttributes(coord, ferretAttributes);

				ncfile.addVariable(null, coord);
			}
		}
		// In some cases a variable has a grid with a dimension rather than an axis.  Collect the dimensions for later matching.
		Element dimE = header.getRootElement().getChild("dimensions");
		if ( dimE != null ) {
		    List dimensions = dimE.getChildren("dimension");
		    for (Iterator dimIt = dimensions.iterator(); dimIt.hasNext();) {
		        Element dimensionE = (Element) dimIt.next();
		        Map<String, FerretAttribute> ferretAttributes = getAttributes(dimensionE);
		        String length = ferretAttributes.get("length").getValue();
		        String name = dimensionE.getAttributeValue("name").trim();
		        if ( length != null ) {
		            Dimension dim = new Dimension(name, Integer.valueOf(length).intValue(), true);
		            allDims.add(dim);
		            ncfile.addDimension(null, dim);
		        }
		    }
		}
		

		log.debug("Finished with coordinate axes variables.");

		// This creates the data variables form the Ferret XML description.
		log.debug("Found " + data.size() + " 'datasets'");
		ArrayList<String> varNAMES = new ArrayList<String>();
		int ds_index = 1;
		for (Iterator dataIt = data.iterator(); dataIt.hasNext();) {
			Element dataset = (Element) dataIt.next();
			String dataset_name = dataset.getAttributeValue("name");
			List vars = dataset.getChildren("var");
			for (Iterator varIt = vars.iterator(); varIt.hasNext();) {
				Element var = (Element) varIt.next();
				String name = var.getAttributeValue("name");
				// Name conflicts should handled in the Ferret script that
				// defines the dataset.
				// This is not working... This hack will keep it from crashing
				// -- I think.
				if (!varNAMES.contains(name)) {
					varNAMES.add(name);

					Variable dataVar = new Variable(ncfile, null, null, name);
					List var_axes = var.getChild("grid").getChild("axes").getChildren();
					ArrayList<Dimension> varDims = new ArrayList<Dimension>();
					String direction = "";
					// This can now be either an <axis> or a <dimension> element.
					for (Iterator axisIt = var_axes.iterator(); axisIt.hasNext();) {
						Element axis = (Element) axisIt.next();
						String axisType = axis.getName();
						String axisName = axis.getTextNormalize();
						for (Iterator dimIt = allDims.iterator(); dimIt.hasNext();) {
							Dimension dim = (Dimension) dimIt.next();
							if (dim.getName().equals(axisName)) {
								varDims.add(dim);
								String direc = "";
								if (axisType.equals("xaxis")) {
									direc = "I";
								} else if (axisType.equals("yaxis")) {
									direc = "J";
								} else if (axisType.equals("zaxis")) {
									direc = "K";
								} else if (axisType.equals("taxis")) {
									direc = "L";
								} else if ( axisType.equals("eaxis")) {
								    direc = "M";
								}
								direction = direction + direc;
							}
						}
						
					}
					Collections.reverse(varDims);
					dataVar.setDimensions(varDims);
					dataVar.addAttribute(new Attribute("direction", direction));

					// Decide type by looking at the correct type attribute...

					/*
					 * Attributes now look like this: <attribute name="units"
					 * type="char"> <value>Deg C</value> </attribute> <attribute
					 * name="long_name" type="char"> <value>SEA SURFACE
					 * TEMPERATURE</value> </attribute> <attribute
					 * name="_FillValue" type="float"> <value>-1.E+34</value>
					 * </attribute>
					 */
					Map<String, FerretAttribute> ferretAttributes = getAttributes(var);
					putAttributes(dataVar, ferretAttributes);
					String type = ferretAttributes.get("ferret_datatype").getValue();
					if (type.equalsIgnoreCase("float")) {
						dataVar.setDataType(DataType.FLOAT);
					} else if (type.equalsIgnoreCase("double")) {
						dataVar.setDataType(DataType.DOUBLE);
					} else if (type.equalsIgnoreCase("short")) {
						dataVar.setDataType(DataType.INT);
					} else if (type.equalsIgnoreCase("int")	|| type.equals("long")) {
						dataVar.setDataType(DataType.LONG);
					} else if (type.equalsIgnoreCase("char") || type.equalsIgnoreCase("string") ) {
						dataVar.setDataType(DataType.CHAR);
					}
					// TODO finish data type setting with all the other obvious
					// types in DataType.???

					dataVar.addAttribute(new Attribute("dataset", dataset_name));
					dataVar.addAttribute(new Attribute("dataset_index", String.valueOf(ds_index)));
					
					ncfile.addVariable(null, dataVar);
				}

			}
			ds_index++;
		}

		ncfile.addAttribute(null, new Attribute("Conventions", "COARDS"));

		log.debug("parsing complete.");

		int nothing = 0;
		nothing++;
	}

	public Array readData(Variable v2, Section section) throws IOException,
			InvalidRangeException {
		FerretTool tool;
		try {
			tool = new FerretTool();
		} catch (Exception e) {
			throw new IOException(e.toString());
		}
		Array a = null;
		log.debug("Entering read for " + v2.getName() + " and "
				+ section.getRank() + " ranges.");

		String varname = v2.getName();
		String readname = v2.getName();
		boolean isCoordinateVariable = false;
		if (v2.isCoordinateVariable()) {
			readname = "COORDS";
			isCoordinateVariable = true;
		}
		Attribute dataset_attr = v2.findAttribute("dataset");
		String dataset = "";
		if (dataset_attr != null) {
			dataset = dataset_attr.getStringValue();

		}
		
		Attribute dataset_index_attr = v2.findAttribute("dataset_index");
		String dataset_index = "1";
		if ( dataset_index_attr != null ) {
			dataset_index = dataset_index_attr.getStringValue();
		}
		

		String direction = "";
		Attribute direction_attr = v2.findAttribute("direction");
		if (direction_attr != null) {
			direction = direction_attr.getStringValue();
		}

		String jnl = null;

		StringBuffer inJnl = new StringBuffer();
		raf.seek(0);
		StringReader sr;
		try {
			byte[] b = new byte[(int) raf.length()];
			raf.read(b);
			sr = new StringReader(new String(b));
		} catch (IOException e) {
			log.debug("IO Exception reading the random access file.");
			throw e;
		}
		BufferedReader jnlBuffReader = new BufferedReader(sr);
		try {
			String line = jnlBuffReader.readLine();

			while (line != null) {
				inJnl.append(line + "\n");
				line = jnlBuffReader.readLine();
			}
		} catch (IOException e) {
			log.debug("IO Exception reading the random access file.");
			throw e;
		}
		jnl = inJnl.toString();

		String cacheKey = JDOMUtils.MD5Encode(jnl);
		String filename = tool.getTempDir() + cacheKey + File.separator
				+ "data_" + varname + "_" + sectionToString(section) + ".nc";
		String temp_filename = filename + ".tmp";
		// Simplest form of caching is that the exact file we need already
		// exists.

		if ( filename.contains("/..") || filename.contains("../") ) {
			throw new IOException("Illegal data file path.");
		}
		if ( temp_filename.contains("../") || temp_filename.contains("/..") ) {
			throw new IOException("Illegal temporary file name.");
		}
		File datatemp = new File(filename);
		File temp_file = new File(temp_filename);
		// Create a range section that pulls out the whole block from the
		// temporary data file.
		ArrayList<Range> newsection = new ArrayList<Range>();

		if (!datatemp.exists()) {
			if (temp_file.exists()) {
				// Somebody else is making this file. Wait for it.
				int trys = 0;
				long interval = 2000; // Wait two seconds each time.
				long timeout = tool.ferretConfig.getTimeLimit() * 1000; // Time
																		// out
																		// in
																		// seconds,
																		// convert
																		// to
																		// millis
				int limit = (int) (timeout / interval);
				while (!datatemp.exists() && trys < limit) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						throw new IOException(
								"Interrupted waiting for temporary file "
										+ e.toString());
					}
					trys++;
				}
				if (!datatemp.exists()) {
					throw new IOException(
							"Temporary data file unavailable after wait");
				}
			} else {
				String slice;
				StringBuffer indx;
				if (isCoordinateVariable) {
					indx = new StringBuffer("go get_coord \"" + temp_file
							+ "\" " + "\"" + dataset + "\" " + varname + " "
							+ direction + " ");
				} else {
					indx = new StringBuffer("go get_datavar \"" + temp_file
							+ "\" " + dataset_index + " " + varname + " "
							+ direction + " ");
				}
				// This is the get_data order XYZT
				Section ferret_section = new Section(section);

				int range_start = ferret_section.getRank() - 1;
				if ( v2.getDataType() == DataType.CHAR && ferret_section.getRank() == 2 ) {
				    // Skip the character array length dimension
				    range_start = ferret_section.getRank() - 2;
				}
				for (int s = range_start; s >= 0; s--) {
					Range range = (Range) ferret_section.getRange(s);
					int start = range.first() + 1;
					int end = range.last() + 1;
					indx.append(start + " " + end + " " + range.stride() + " ");
				}

				// Before we go off and run Ferret see if we can get the data
				// from
				// an existing file.

				slice = jnl + "\n" + indx.toString();
				log.debug("Using jnl: " + slice);
				try {

					tool.run("data.jnl", slice, cacheKey, temp_filename,
							filename);

				} catch (Exception e) {
					throw new IOException("Unable run data extract tool "
							+ e.toString());
				}
			}
		} else {
			log.debug("Cache hit on: " + filename);
		}
		// One way or another we think we have a file. Try to use it.
		for (Iterator rangeIt = section.getRanges().iterator(); rangeIt
				.hasNext();) {
			Range range = (Range) rangeIt.next();
			Range newrange = new Range(0, range.length() - 1, 1);
			newsection.add(newrange);
		}

		log.debug("Attempting to open data file: " + filename);
		NetcdfFile nds = null;
		try {
			nds = NetcdfDataset.open(filename, null);
			log.debug("Attempting to find variable : " + readname);
			Variable v = nds.findVariable(readname);
			if (readname.equals("COORDS")) {
				List dims = v.getDimensions();
				// Should be only 1.
				if (dims.size() < 0 || dims.size() > 1) {
					log
							.error("A coordinate variable has more than one dimension.");
				}
				Dimension cdim = (Dimension) dims.get(0);
				String name = cdim.getName();
				v = nds.findVariable(name);
			}
			log.debug("Attempting to read var: " + readname + " with ranges "
					+ Range.makeSectionSpec(newsection));
			if (newsection.isEmpty()) {
				a = v.read();
			} else {
				a = v.read(newsection);
			}
			log.debug("Finished reading variable data.");
		} catch (IOException e) {
			log.error("Exception opening netCDF data file. "
					+ e.getLocalizedMessage());
			throw e;
		} finally {
			if (nds != null) {
				try {
					nds.close();
				} catch (IOException e) {
					log.error("Exception closing the netCDF data file."
							+ e.getMessage());
				}
			}
		}
		return a;
	}

	private String sectionToString(Section section) {
		if ( section != null ) {
			String name = section.toString();
			if ( name != null ) {
			    return name.replace(",", "_").replace(":","-");
			} else {
				return "";
			}
		} else {
			return "";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ucar.nc2.IOServiceProvider#readNestedData(ucar.nc2.Variable,
	 * java.util.List)
	 */
	public Array readNestedData(Variable v2, List section) throws IOException,
			InvalidRangeException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ucar.nc2.IOServiceProvider#setSpecial(java.lang.Object)
	 */
	public void setSpecial(Object special) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ucar.nc2.IOServiceProvider#sync()
	 */
	public boolean sync() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ucar.nc2.IOServiceProvider#syncExtend()
	 */
	public boolean syncExtend() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ucar.nc2.IOServiceProvider#toStringDebug(java.lang.Object)
	 */
	public String toStringDebug(Object o) {
		// TODO Auto-generated method stub
		return null;
	}

	public Array readSection(ParsedSectionSpec arg0) throws IOException,
			InvalidRangeException {
		// TODO Auto-generated method stub
		return null;
	}

	public long readToByteChannel(Variable arg0, Section arg1,
			WritableByteChannel arg2) throws IOException, InvalidRangeException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Object sendIospMessage(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getFileTypeDescription() {
		return "Ferret I/O Service Provider and Server-side Analysis";
	}

	public String getFileTypeId() {
		return "F-TDS";
	}

	public String getFileTypeVersion() {
		return "7.3";
	}

	private void putAttributes(Variable var,
			Map<String, FerretAttribute> ferretAttributes) {
		for (Iterator attIt = ferretAttributes.keySet().iterator(); attIt
				.hasNext();) {
			String aname = (String) attIt.next();
			String value = ferretAttributes.get(aname).getValue();
			String type = ferretAttributes.get(aname).getType().trim().toLowerCase(Locale.ENGLISH);

			// Will this have array types?
			if (type.equals("double")) {
				try {
					double dvalue = Double.valueOf(value).doubleValue();
					var.addAttribute(new Attribute(aname, new Double(dvalue)));
				} catch (NumberFormatException nfe) {
					var.addAttribute(new Attribute(aname, value));
				}
			} else if (type.equals("float")) {
				try {
					float fvalue = Float.valueOf(value).floatValue();
					var.addAttribute(new Attribute(aname, new Float(fvalue)));
				} catch (NumberFormatException nfe) {
					var.addAttribute(new Attribute(aname, value));
				}
			} else if (type.equals("short")) {
				int ivalue;
				try {
					ivalue = Integer.valueOf(value).intValue();
					var.addAttribute(new Attribute(aname, ivalue));
				} catch (NumberFormatException e) {
					var.addAttribute(new Attribute(aname, value));
				}

			} else if (type.equals("int") || type.equals("long")) {
				long lvalue;
				try {
					lvalue = Long.valueOf(value).longValue();
					var.addAttribute(new Attribute(aname, lvalue));
				} catch (NumberFormatException e) {
					var.addAttribute(new Attribute(aname, value));
				}
			} else {
				var.addAttribute(new Attribute(aname, value));
			}
		}
	}

	private Map<String, FerretAttribute> getAttributes(Element varE) {
		Map<String, FerretAttribute> ferretAttributes = new HashMap<String, FerretAttribute>();
		List attributeElements = varE.getChildren("attribute");
		if (attributeElements != null && attributeElements.size() > 0) {
			for (Iterator attIt = attributeElements.iterator(); attIt.hasNext();) {
				Element attribute = (Element) attIt.next();
				FerretAttribute fa = new FerretAttribute();

				// Look for old style attribute XML and use it 
				String value = null;
				String aname = null;
				String atype = null;
				if ( attribute.getChildTextNormalize("value") == null ) {					
                    value = attribute.getAttributeValue("value");
                    aname = attribute.getAttributeValue("name");
                    // All we we can do is try to treat it as a number since we don't know the type.
                    // This is an in exact science
                    try {
                    	
                    	if ( value.matches("[0-9]*") ) {
                    		int ivalue = Integer.valueOf(value).intValue();
                    		atype = "short";
                    	} else {
                    		double dvalue = Double.valueOf(value).doubleValue();
                    		atype = "double";
                    	}
                    } catch (NumberFormatException nfe) {
                    	atype = "string";
                    }
				} else {
					// This is must be the new attribute XML....
					value = attribute.getChildTextNormalize("value").trim();
					aname = attribute.getAttributeValue("name").trim();
					atype = attribute.getAttributeValue("type").trim()
					.toLowerCase(Locale.ENGLISH);
					
				}
				if ( value != null && !value.equals("") && 
					 aname != null && !aname.equals("") && 
					 atype != null && !atype.equals("") ) {
					fa.setValue(value);
					fa.setName(aname);
					fa.setType(atype);
					ferretAttributes.put(aname, fa);
				}			
			}
		}
		return ferretAttributes;
	}

	public String getDataDir() {
		try {
			return new FerretTool().getDataDir();
		} catch (Exception e) {
			return "";
		}
	}

	private class FerretAttribute {
		private String name;
		private String value;
		private String type;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}

	public static void main(String[] args) {
		/*
		 * try {
		 * log.debug("Registering gov.noaa.pmel.tmap.iosp.FerretIOServiceProvider"
		 * );NetcdfFile.registerIOProvider(
		 * "gov.noaa.pmel.tmap.iosp.FerretIOServiceProvider"); } catch
		 * (IllegalAccessException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (InstantiationException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } catch
		 * (ClassNotFoundException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		// String filename =
		// "http://porter.pmel.noaa.gov:8920/thredds/dodsC/las/coads_climatology_cdf/data_coads_climatology.jnl";
		// String filename = "/home/porter/rhs/data/coads_climatology.cdf";

		String filename = "http://porter.pmel.noaa.gov:8920/thredds/dodsC/las/NOAA_NCEP_EMC_CMB_Ocean_Analysis_ml/data__iridl.ldeo.columbia.edu_SOURCES_.NOAA_.NCEP_.EMC_.CMB_.Pacific_.monthly_dods.jnl";

		// String filename =
		// "http://strider.weathertopconsulting.com:8880/thredds/dodsC/las/coads_climatology_cdf/data_coads_climatology.jnl";
		// String expr =
		// "_expr_{levitus_climatology}{let airt_regrid=airt[d=1,t=\"15-Jan\":\"15-Dec\"@ave];let temp_regrid=temp[d=2,gxy=airt_regrid[d=1]]}";
		String inner = "http://porter.pmel.noaa.gov:8920/thredds/dodsC/las/levitus_climatology_cdf/coads_climatology_cdf.jnl";
		String ninner = "";
		
		try {
			ninner = URLEncoder.encode(inner, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String expr = "_expr_{"
				+ ninner
				+ "}{let temp_1_regrid=temp[d=1,z=5.00:75.00@ave];let sst_2_regrid=sst[d=2,t=\"15-Jan\":\"15-Mar\"@ave];let sst_2_regrid_2_regrid=sst_2_regrid[d=2,gxy=temp_1_regrid[d=1]]}";

		try {
			filename = filename + URLEncoder.encode(expr, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		filename = "http://strider.weathertopconsulting.com:8880/thredds/dodsC/las/id-2c2b44e493/data_ocean_atlas_subset.jnl";
		NetcdfDataset ncd = null;
		if (filename.startsWith("http")) {
			System.out.println(filename);
			// System.exit(0);
		}
		try {
			log.debug("Calling openDataset");
			ncd = NetcdfDataset.openDataset(filename);
			log.debug("Finished openDataset");
		} catch (IOException ioe) {
			log.error("trying to open " + filename + "  " + ioe);
		} finally {
			if (null != ncd) {
				try {
					List variables = ncd.getVariables();
					StringBuffer vars = new StringBuffer();
					for (Iterator vIt = variables.iterator(); vIt.hasNext();) {
						Variable v = (Variable) vIt.next();
						if (v.isCoordinateVariable()) {
							if (vars.length() > 0)
								vars.append(";");
							vars.append(v.getName());
						}
					}
					// NCdump.print(ncd, System.out, true, true, false, true,
					// vars.toString(), null);
					for (Iterator vIt = variables.iterator(); vIt.hasNext();) {
						Variable v = (Variable) vIt.next();
						if (v.isCoordinateVariable()) {
							log.debug("reading " + v.getName());
							int[] shape = v.getShape();
							int[] origin = new int[shape.length];
							for (int s = 0; s < shape.length; s++) {
								if (shape[s] > 7) {
									origin[s] = shape[s] / 2;
								} else {
									origin[s] = 0;
								}
								shape[s] = Math.min(3, shape[s]);
								log.debug("for dimension s=" + s
										+ " setting origin=" + origin[s]
										+ " shape=" + shape[s]);
							}
							Array a = v.read(origin, shape);
							IndexIterator it = a.getIndexIterator();
							log.debug("Iterate on the array.");
							while (it.hasNext()) {
								float val = it.getFloatNext();
								log.debug("Value for " + it.toString() + "="
										+ val);
							}
						}
					}
					ncd.close();
				} catch (IOException ioe) {
					log.error("trying to close " + filename + "  "
							+ ioe.toString());
				} catch (InvalidRangeException ire) {
					log.error("bad range: " + ire.toString());
				}
			}
		}
	}

	@Override
	public StructureDataIterator getStructureIterator(Structure arg0, int arg1)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public long readToOutputStream(Variable arg0, Section arg1, OutputStream arg2) throws IOException, InvalidRangeException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long streamToByteChannel(Variable arg0, Section arg1, WritableByteChannel arg2) throws IOException, InvalidRangeException {
        // TODO Auto-generated method stub
        return 0;
    }

	@Override
	public void reacquire() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void release() throws IOException {
		// TODO Auto-generated method stub
		
	}

}
