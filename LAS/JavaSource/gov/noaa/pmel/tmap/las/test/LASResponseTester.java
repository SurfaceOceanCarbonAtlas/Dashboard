package gov.noaa.pmel.tmap.las.test;


import gov.noaa.pmel.tmap.addxml.JDOMUtils;
import gov.noaa.pmel.tmap.las.client.lastest.TestConstants;
import gov.noaa.pmel.tmap.las.jdom.LASConfig;
import gov.noaa.pmel.tmap.las.jdom.LASTestResults;
import gov.noaa.pmel.tmap.las.jdom.LASUIRequest;
import gov.noaa.pmel.tmap.las.ui.state.OptionBean;
import gov.noaa.pmel.tmap.las.util.Dataset;
import gov.noaa.pmel.tmap.las.util.Grid;
import gov.noaa.pmel.tmap.las.util.Variable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.jdom.Element;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import dods.dap.DConnect;
import dods.dap.DDS;
import dods.dap.DDSException;
import dods.dap.DODSException;
import dods.dap.parser.ParseException;

/**
 * This class tests the responses form a LAS product server
 * @author Jing Yang Li
 *
 */
public class LASResponseTester{

	private LASConfig lasConfig;
	private LASTestOptions lto;

	//public LASResponseTester(LASConfig config, LASDocument operations, LASDocument options, LASTestOptions l){
	/**
	 * Constructor
	 */
	public LASResponseTester(LASConfig config, LASTestOptions l){
		lasConfig = config;
		lto = l;
	}

	/**
	 * Test whether responses from product server are correct
	 *
	 */
	public void testResponse(boolean web_output, ArrayList<Dataset> datasets) {
		String dsID;
		String varID;
		String varpath;

		
		ArrayList<Variable> variables = new ArrayList<Variable>();

		String productServerURL;

		String test_output_file = null;
		LASTestResults testResults = new LASTestResults();

		try{
			if ( web_output ) {
				test_output_file = lasConfig.getOutputDir()+File.separator+TestConstants.TEST_RESULTS_FILE;
				File c = new File(test_output_file);
				if ( c.exists() ) {
					JDOMUtils.XML2JDOM(new File(test_output_file), testResults);
				}
				Date date = new Date();
				testResults.putTest(TestConstants.TEST_PRODUCT_RESPONSE, date.getTime());
			}

			productServerURL = lto.getLAS()+"ProductServer.do";

			
            String dregex = lto.getDregex();
            String vregex = lto.getVregex();
                
			//loop over each dataset
			for(Iterator dsIt = datasets.iterator(); dsIt.hasNext();){
				Dataset ds = (Dataset)dsIt.next();
				boolean dmatch = true;
				if ( dregex != null ) {
				    dmatch = Pattern.matches(dregex, ds.getID());
				}
				if ( dmatch ) {
				    if ( web_output ) {
				        testResults.putDataset(TestConstants.TEST_PRODUCT_RESPONSE, ds.getName(), ds.getID());
				    }
				    //get first variable of this dataset
				    variables = ds.getVariables();
				    boolean allVar = lto.allVariable() || vregex != null;

				    if(!allVar){
				        if ( variables != null && variables.size() > 0 ) {
				        	Variable firstVar = null;
				        	// We cannot test subset variables for DSG data.
				        	for ( int i = 0; i < variables.size(); i++ ) {
				        		
				        		Variable candidate = variables.get(i);
				        		
				        		// find the first non-subset variable, and not time, lat or lon since these require
				        		// special treatment to extract from the server properly
				        		if ( candidate.getAttributesAsMap().get("subset_variable") == null &&
				        			 !candidate.getName().toLowerCase(Locale.ENGLISH).contains("time") && 
				        			 !candidate.getName().toLowerCase(Locale.ENGLISH).contains("lat") &&
				        			 !candidate.getName().toLowerCase(Locale.ENGLISH).contains("lon") ) {
				        			firstVar = candidate;
				        			break;
				        		}
				        	}
				        	if ( firstVar != null ) {
				        		varLASRequest(ds, firstVar, web_output, testResults);
				        	}
				        }
				    }else{
				        for(Iterator varIt = variables.iterator(); varIt.hasNext();){
				            boolean vmatch = true;
				            Variable theVar = (Variable)varIt.next();
				            if ( vregex != null ) {
				                vmatch = Pattern.matches(vregex, theVar.getID());
				            }
				            if ( vmatch ) {
				                varLASRequest(ds, theVar, web_output, testResults);
				            }
				        }
				    }
				}
			}
			if ( web_output ) {
			    testResults.write(test_output_file);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Test the LAS response for a variable
	 * @param dsE the dataset element
	 * @param theVar the variable to test
	 */
	public void varLASRequest(Dataset dataset, Variable theVar, boolean web_output, LASTestResults testResults) {

	    String dsoURL = dataset.getAttributeValue("url");
		String varURL = theVar.getAttributeValue("url");


		try{
			//get the data URL
			String dsURL = LASConfig.combinedURL(dsoURL, varURL);
			String userds = lto.getDataset();

			//if url is in format of ....xyz.nc#var
			if(dsURL.contains("#")){
				String[] tmp = dsURL.split("#");
				dsURL = tmp[0];
			}

			boolean isAvailable = false;
			//check if a remote dataset is available

			if(dsURL == null && dsURL == ""){
				if ( web_output ) {
					testResults.addProductResult(TestConstants.TEST_PRODUCT_RESPONSE, dataset.getID(), dsURL, "n/a", "n/a", "n/a", "failed - dataset invalid");
				} else {
					System.out.println("The dataset URL is not valid.");
				}
			}

			//check if a dataset is available
			/*
            if( userds == null || userds==""){
                if(dsURL.contains("http")){
                    isAvailable = isAvailable(dsURL); //remote dataset
                }else{
                    isAvailable = true; //local dataset
                }
            }else{
                if(dsURL.contains(userds)){
                    isAvailable = isAvailable(dsURL);
                }
            }
			 */
			//if(isAvailable){
			if( userds == null || userds=="" || dsURL.contains(userds) ){
				if ( !web_output ) {
					System.out.println("---- Dataset Name: "+ dataset.getName() + " with ID="+dataset.getID());
					System.out.println("     -- Dataset  URL: "+ dsURL);   
				}
				if(dsURL.contains("http") && !dsURL.contains("tabledap") ){
					isAvailable = isAvailable(dsURL); //remote dataset
				}else{
					isAvailable = true; //local dataset
				}

				if(isAvailable){        
					if ( !web_output ) {
						System.out.println("        Variable: "+ theVar.getName());
					}
					
					

					boolean hasZ = theVar.getGrid().hasZ();
					boolean hasT = theVar.getGrid().hasT();
					String v = lto.getView();

					//by default, test each view           
					if(v == null || v.equals("")){                

						//1D plots             
						if ( !web_output ) {
							System.out.print("         -- test x line plot: ");
						}
						makeLASRequest("x", theVar, web_output, testResults);       
						if ( !web_output ) {
							System.out.print("         -- test y line plot: ");
						}
						makeLASRequest("y", theVar, web_output, testResults);                
						if(hasZ){      
							if ( !web_output ) {
								System.out.print("         -- test z line plot: ");
							}
							makeLASRequest("z", theVar, web_output, testResults);                
						}

						if(hasT){     
							if ( !web_output ) {
								System.out.print("         -- test time series: ");
							}
							makeLASRequest("t", theVar, web_output, testResults);
						}

						//2D plots  
						if ( !web_output ) {
							System.out.print("         -- test XY 2D  plot: ");
						}
						makeLASRequest("xy", theVar, web_output, testResults);                
						if(hasZ){   
							if ( !web_output ) {
								System.out.print("         -- test XZ 2D  plot: ");
							}
							makeLASRequest("xz", theVar, web_output, testResults);      
							
							if ( !web_output ) {
								System.out.print("         -- test YZ 2D  plot: ");
							}
							makeLASRequest("yz", theVar, web_output, testResults);

							if(hasT){
								if ( !web_output ) {
									System.out.print("         -- test ZT 2D  plot: ");
								}
								makeLASRequest("zt", theVar, web_output, testResults);
							}
						}

						if(hasT){  
							if ( !web_output ) {
								System.out.print("         -- test XT 2D  plot: ");
							}
							makeLASRequest("xt",theVar, web_output, testResults);
							if ( !web_output ) {
								System.out.print("         -- test YT 2D  plot: ");
							}
							makeLASRequest("yt", theVar, web_output, testResults);       				
						}

					}else{ 

						//only make request for the plot defined by command-line parameter
						if( v.contains("z") && (!hasZ) ){
							if ( !web_output) System.out.println("       -- No Z axis!");
						}else if( v.contains("t") && (!hasT) ){    	    	
							if ( !web_output) System.out.println("       -- No T axis!");
						}else{               	            	    
							if ( !web_output ) System.out.print("        -- test "+v+" plot: ");
							makeLASRequest(v, theVar, web_output, testResults);                
						}            
					}
				}else{
					if ( web_output ) {
						testResults.addProductResult(TestConstants.TEST_PRODUCT_RESPONSE, dataset.getID(), dsURL, "n/a", "n/a", "n/a", "failed - dataset unavailable");
					} else {
						System.out.println("        ******** WARNING ******** The dataset is not available");
					}
				}
				if ( !web_output ) {
					System.out.println("");
				}
			}	    
			/*    
            }else{
                if( userds == null || userds==""){
                    System.out.println("---- Dataset Name: "+ dsName);
                    System.out.println("     -- Dataset  URL: "+ dsURL);
                    System.out.println("        ******** WARNING ******** The dataset is not available");
                }else{
                    if(dsURL.contains(userds)){
                        System.out.println("---- Dataset Name: "+ dsName);
                        System.out.println("     -- Dataset  URL: "+ dsURL);
                        System.out.println("     ******** WARNING ******** The dataset is not available");
                    }
                }
            }
			 */
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Check if a dataset with the given URL is available
	 * @param url the URL of the dataset
	 * @return isAvailable
	 */
	public boolean isAvailable(String url) throws Exception{
		boolean isAvailable = false;
		try{
			DConnect dc = new DConnect(url);
			DDS mydds = dc.getDDS();

			if(mydds != null){
				isAvailable = true;
			}else{
				isAvailable = false;
			}
		}catch (MalformedURLException e){
			//java.net.MalformedURLException - if the URL given to the constructor has an error
			isAvailable = false;
		}catch (IOException e){
			//java.io.IOException - if an error connecting to the remote server
			isAvailable = false;
		}catch (ParseException e){
			//dods.dap.parser.ParseException - if the DDS parser returned an error
			isAvailable = false;
		}catch (DDSException e){
			//dods.dap.DDSException - on an error constructing the DDS
			isAvailable = false;
		}catch (DODSException e){
			//dods.dap.DODSException - if an error returned by the remote server
			isAvailable = false;
		}catch (Exception e){
			//e.printStackTrace(); 
			isAvailable = false;
		}

		return isAvailable;
	}    
	/**
	 * Test LAS request for 1D and 2D plots
	 * @param viewtype view type of the plot
	 * @param dsID dataset ID
	 * @param varID variable ID
	 */
	public void makeLASRequest(String viewtype, Variable variable, boolean web_output, LASTestResults testResults) {
		String requestURL ="";
		String serverURL;

		LASUIRequest lr = new LASUIRequest();

		lr = buildLASUIRequest(viewtype,variable);

		try{
			serverURL = lto.getLAS()+"ProductServer.do";
			requestURL = serverURL+"?xml=" + lr.toEncodedURLString()+"&debug=true"; 
		} catch (Exception e){
			e.printStackTrace();
		}

		
		boolean inProgress = true;
		int noProgress = 0;
		InputStream instream = null;
		while(inProgress && (noProgress < 2)){
			try{
				//send request to ProductServer
				URL url = new URL(requestURL);
				URLConnection conn = url.openConnection();
				conn.connect();
				instream = conn.getInputStream();
				String contentType = conn.getContentType();
				char[] buf = new char[4096];
				BufferedReader is = new BufferedReader(new InputStreamReader(instream));
				StringBuffer sbuf = new StringBuffer();
				int length = is.read(buf, 0, 4096);

				while (length >=0 ){
					sbuf.append(buf, 0, length);
					length = is.read(buf, 0, 4096);
				}
				int size = sbuf.length();

				if(sbuf.toString().contains("error")){
					String debugFile = extractDebugFile(sbuf.toString());
					if ( web_output ) {
						testResults.addProductResult(TestConstants.TEST_PRODUCT_RESPONSE, variable.getDSID(), requestURL, viewtype, lr.getOperation(), lr.getThi(), "failed");
					} else {
						System.out.println("              ---------- ERROR !!!");
					}
					if(debugFile !=null && debugFile !=""){
						if ( !web_output ) System.out.println("        The debug file is "+ debugFile);
					}
					//print the whole error message
					if(lto.isVerbose() || debugFile == null || debugFile == ""){
						if (!web_output) System.out.println(sbuf.toString());
					} 
					inProgress = false;
					if(lto.exitFirst()){System.exit(-1);}
				}else if(sbuf.toString().contains("Progress") || sbuf.toString().contains("progress")){
					inProgress = true;
					noProgress++;
					if ( !web_output) {
						System.out.println("The response is:");
						System.out.println(sbuf.toString());
					}
				}else{//correct response (hope so!)
					inProgress = false;
					if ( web_output ) {
						testResults.addProductResult(TestConstants.TEST_PRODUCT_RESPONSE, variable.getDSID(), requestURL, viewtype, lr.getOperation(), lr.getThi(), "passed");
					} else {
						System.out.println("              ---------- PASS!");
					}
				}
			}catch (Exception e){
				if ( !web_output ) System.out.println("error in making request to product server");
				e.printStackTrace();
				break;
			} finally {
				if ( instream != null ) {
					try {
						instream.close();
					} catch (IOException e) {
						System.err.println("Error closing stream.  "+e.getMessage());
					}
				}
			}
		}
		//if the request was sent more than 3 times; kill it
		//it may be taking too long or there are errors (e.g. Ferret script hangs)
		if(inProgress && (noProgress == 2)){
			if ( !web_output) System.out.println("----------------------------cancel request");
			//send cancel request to ProductServer
			try{
				URL url = new URL(requestURL+"&cancel=Cancel");
				URLConnection conn = url.openConnection();
				conn.connect();
				if ( web_output ) {
					testResults.addProductResult(TestConstants.TEST_PRODUCT_RESPONSE, variable.getDSID(), requestURL, viewtype, lr.getOperation(), lr.getThi(), "canceled");
				} else {
					System.out.println("The request either takes too long or may have error --- cancel it!");
				}
			}catch (IOException e){
				System.out.println("error in canceling request");
			}
		}
	}

	/**
	 * Extract the debug file from the error message
	 * @param errMsg the error message returned from LAS
	 */
	private String extractDebugFile(String errMsg){
		//System.out.println(errMsg);
		String debugFile="";
		int i1 = errMsg.indexOf("debug.txt");
		if(i1 > 0){
			String s1 = errMsg.substring(0,i1+9);
			int i2 = s1.lastIndexOf("http");
			if(i2>0){debugFile = s1.substring(i2);}
		} 
		return debugFile;
	}

	/**
	 * Build a LASUIRequest for 1D and 2D plots
	 * @param viewtype view type of the plot
	 * @param dsID dataset ID
	 * @param varID variable ID
	 */
	public LASUIRequest buildLASUIRequest(String viewtype, Variable variable){
		ArrayList<OptionBean> options = null;

		LASUIRequest lr = new LASUIRequest();
		lr.addVariable(variable.getDSID(),variable.getID());

		if(viewtype.length() == 1){
			lr.setOperation("Plot_1D");
			//options = extractOptions("Options_1D");
			options = makeOptions(viewtype);
			lr.setOptions("ferret", options);
			lr.setProperty("ferret","line_or_sym","default");
			lr.setProperty("ferret","line_color","default");
			lr.setProperty("ferret","line_thickness","default");
		}else if(viewtype.length() == 2){
			if(viewtype.equals("xy")){
				lr.setOperation("Plot_2D_XY");
			}else{
				lr.setOperation("Plot_2D");
			}
			//options = extractOptions("Options_2D");
			options = makeOptions(viewtype);
			lr.setOptions("ferret", options);
			lr.setProperty("ferret","palette","default");
		} else if ( viewtype.length() == 3 && variable.isDiscrete() ) {
			String gt = variable.getGridType();
			// TODO other discrete types.
			if ( gt != null ) {
				options = makeOptions(viewtype);
				lr.setOptions("ferret", options);
				if ( gt.equals("timeseries") ) {
					lr.setOperation("Timeseries_interactive_plot");
				} else if (gt.equals("profile") ) {
					lr.setOperation("Profile_interactive_plot");
				} else if ( gt.equals("trajectory") ) {
					lr.setOperation("Trajectory_interactive_plot");
				}
				
			}
			
		}
		Date now = new Date();
		String timestamp = String.valueOf(now.getTime());
		lr.setProperty("ferret", "annotations", "file");
		lr.setProperty("result","annotations_ID", "annotations");
		lr.setProperty("result","annotations_filename", timestamp+"_annotations.xml");
		lr.setProperty("result","annotations_type","annotations");
		lr.setProperty("data_0", "dataset_name", "Test data set");


		HashMap<String, HashMap<String,String[]>> region = new HashMap<String, HashMap<String,String[]>>();

		region = makeRegion(viewtype, variable);
		if(region != null){lr.setRegion(region);}

		return lr;
	}

	/**
	 * Make a region for a plot
	 * @param viewtype view type of the plot
	 * @param dsID dataset ID
	 * @param varID variable ID
	 */
	public ArrayList<OptionBean> makeOptions(String viewtype){
		ArrayList<OptionBean> options = new ArrayList<OptionBean>();

		OptionBean opb1 = new OptionBean();
		opb1.setWidget_name("interpolate_data");
		opb1.setValue("false");
		options.add(opb1);

		OptionBean opb2 = new OptionBean();
		opb2.setWidget_name("image_format");
		opb2.setValue("default");
		options.add(opb2);

		OptionBean opb3 = new OptionBean();
		opb3.setWidget_name("size");
		opb3.setValue(".5");
		options.add(opb3);

		OptionBean opb4 = new OptionBean();
		opb4.setWidget_name("use_ref_map");
		opb4.setValue("default");
		options.add(opb4);

		OptionBean opb5 = new OptionBean();
		opb5.setWidget_name("use_graticules");
		opb5.setValue("default");
		options.add(opb5);

		OptionBean opb6 = new OptionBean();
		opb6.setWidget_name("no_margins");
		opb6.setValue("default");
		options.add(opb6);

		OptionBean opb7 = new OptionBean();
		opb7.setWidget_name("deg_min_sec");
		opb7.setValue("default");
		options.add(opb7);

		OptionBean opb8 = new OptionBean();
		opb8.setWidget_name("view");
		opb8.setValue(viewtype);
		options.add(opb8);

		return options;
	} 
	/**
	 * Make a region for a plot
	 * @param viewtype view type of the plot
	 * @param dsID dataset ID
	 * @param varID variable ID
	 */
	public HashMap<String, HashMap<String,String[]>> makeRegion(String viewtype, Variable variable){

		Grid grid = variable.getGrid();
		String xLo = "";
		String xHi = "";
		String yLo = "";
		String yHi = "";
		String zLo = "";
		String zHi = "";
		String tLo = "";
		String tHi = "";
		boolean hasZ = false;
		boolean hasT = false;
		DateTime lodt=null;
		DateTime hidt=null;

		HashMap<String, HashMap<String,String[]>> region = new HashMap<String, HashMap<String,String[]>>();
		HashMap<String,String[]> points = new HashMap<String,String[]>();
		HashMap<String,String[]> intervals = new HashMap<String,String[]>();

		

		try{
			//get the end points of each axis
			xLo = grid.getAxis("x").getLo();
			xHi = grid.getAxis("x").getHi();
			yLo = grid.getAxis("y") .getLo();
			yHi = grid.getAxis("y").getHi();
			if(grid.hasZ()){
				hasZ = true;
				zLo = grid.getAxis("z").getLo();
				zHi = grid.getAxis("z").getHi();
			}
			//time axis
			if(grid.hasT()){
				hasT = true;

				//get time format for this variable defined in dataset configuration file
				tLo = grid.getTime().getLo();
				LASDateFormat ldf = new LASDateFormat(tLo);
				String tFormat = ldf.getDateFormat();

				DateTimeFormatter fmt = DateTimeFormat.forPattern(tFormat).withZone(DateTimeZone.UTC);
				DateTimeFormatter ferretfmt = DateTimeFormat.forPattern("dd-MMM-yyyy HH:mm:ss").withZone(DateTimeZone.UTC);

				lodt = fmt.parseDateTime(tLo);   
				tLo = lodt.toString(ferretfmt);

                  
					tHi = grid.getTime().getHi();
					hidt= fmt.parseDateTime(tHi); 
					tHi = hidt.toString(ferretfmt);
				
			}
		}catch (Exception e){
			e.printStackTrace();
		}

		//make a x interval or point
		if(viewtype.contains("x")){
			//make a small x interval in case it's a large dataset
			String[] x = new String[2];
			double lox = Double.valueOf(xLo).doubleValue();
			double hix = Double.valueOf(xHi).doubleValue();
			double dx = hix - lox;
			x[0] = xLo;
			x[1] = Double.toString(lox+(dx/4));
			intervals.put("x",x);
		}else{
			//make a x point
			String[] x = new String[1];
			x[0] =  xLo;
			points.put("x",x);
		}

		//make a y interval or point
		if(viewtype.contains("y")){
			//make a small y interval in case it's a large dataset
			String[] y = new String[2];
			double loy = Double.valueOf(yLo).doubleValue();
			double hiy = Double.valueOf(yHi).doubleValue();
			double dy = hiy - loy;
			y[0] =  yLo;
			y[1] =  Double.toString(loy+(dy/4));
			intervals.put("y",y);
		}else{
			//make a y point
			String[] y = new String[1];
			y[0] =  yLo;
			points.put("y",y);
		}

		//make a z interval or point
		if(hasZ){
			if(viewtype.contains("z") || variable.isDiscrete() ){
				//make a z interval
				String[] z = new String[2];
				z[0] =  zLo;
				z[1] =  zHi;
				intervals.put("z",z);
			}else{
				//make a z point
				String[] z = new String[1];
				z[0] =  zLo;
				points.put("z",z);
			}
		}

		//make a t interval or point
		if(hasT){
			if(viewtype.contains("t")){
				//make a t interval
				String[] t = new String[2];
				t[0] =  tLo;
				t[1] =  tHi;
				intervals.put("t",t);
			}else{
				//make a t point
				String[] t = new String[1];
				t[0] = tLo;
				points.put("t",t);
			}
		}

		region.put("intervals", intervals);
		region.put("points", points);

		return region;
	}

	public LASTimeAxis getLASTimeAxis(String varpath){
		LASTimeAxis lta = new LASTimeAxis();

		String timeStyle = null;
		try{
			if (!varpath.contains("@ID")) {
				String[] parts = varpath.split("/");
				// Throw away index 0 since the string has a leading "/".
				varpath = "/"+parts[1]+"/"+parts[2]+"/dataset[@ID='"+parts[3]+"']/"+parts[4]+"/variable[@ID='"+parts[5]+"']";
			}
			Element variable = lasConfig.getElementByXPath(varpath);
			if (variable == null) {
				return null;
			}
			String gridID = variable.getChild("grid").getAttributeValue("IDREF");
			Element grid = lasConfig.getElementByXPath("/lasdata/grids/grid[@ID='"+gridID+"']");
			if (grid == null) {
				//System.out.println("grid");
				return null;
			}
			List axes = grid.getChildren("axis");
			for (Iterator axIt = axes.iterator(); axIt.hasNext();) {
				Element axis = (Element) axIt.next();
				String axisID = axis.getAttributeValue("IDREF");
				axis = lasConfig.getElementByXPath("/lasdata/axes/axis[@ID='"+axisID+"']");
				String t = axis.getAttributeValue("type");

				if ( t.equals("t") ) {
					lta.setUnit(axis.getAttributeValue("units"));
					Element arange = axis.getChild("arange");
					if (arange == null) {
						List v = axis.getChildren("v");
						if(v != null){
							timeStyle = "v";
							lta.setStyle("v");
						}
					} else {
						timeStyle = "arange";
						lta.setStyle("arange");
						double size = Double.valueOf(arange.getAttributeValue("size")).doubleValue();
						double step = Double.valueOf(arange.getAttributeValue("step")).doubleValue();
						lta.setSize(size);
						lta.setStep(step);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();    
		}
		return lta;
	}

}
