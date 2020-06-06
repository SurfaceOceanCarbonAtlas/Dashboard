package gov.noaa.pmel.tmap.las.test;


import gov.noaa.pmel.tmap.addxml.JDOMUtils;
import gov.noaa.pmel.tmap.jdom.LASDocument;
import gov.noaa.pmel.tmap.las.client.lastest.TestConstants;
import gov.noaa.pmel.tmap.las.jdom.LASConfig;
import gov.noaa.pmel.tmap.las.jdom.LASTestResults;
import gov.noaa.pmel.tmap.las.proxy.LASProxy;
import gov.noaa.pmel.tmap.las.util.Category;
import gov.noaa.pmel.tmap.las.util.Dataset;
import gov.noaa.pmel.tmap.las.util.Variable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import dods.dap.DConnect;
import dods.dap.DDS;
import dods.dap.DDSException;
import dods.dap.DODSException;
import dods.dap.parser.ParseException;

/**
 * This classes tests the remote OPeNDAP URLs used by this LAS server
 * and the F-TDS URLs provided by this LAS server
 *
 * @author Jing Yang Li
 * 
 */
public class LASDatasetTester{

    private LASTestOptions lto;
    private LASConfig lasConfig;
    private static LASProxy lasProxy = new LASProxy();
    
    public LASDatasetTester(LASConfig lasConfig, LASTestOptions l){
        this.lasConfig = lasConfig;
        lto = l;
    }

    /**
     * Test remote OPeNDAP URLs
     *
     */
    public void testDataset(boolean web_output, ArrayList<Dataset> datasets){

    	String test_output_file = null;
    	LASTestResults testResults = new LASTestResults();
    	

    	try{
    		if ( web_output ) {
    			test_output_file = lasConfig.getOutputDir()+File.separator+TestConstants.TEST_RESULTS_FILE;
    			File c = new File(test_output_file);
    			if ( c.exists() ) {
    				JDOMUtils.XML2JDOM(new File(test_output_file), testResults);
    			}
    			Date now = new Date();
    			testResults.putTest(TestConstants.TEST_DIRECT_OPENDAP, now.getTime());
    			
    		} 

    		
    		



    		for (Iterator dsIt = datasets.iterator(); dsIt.hasNext();) {
    			Dataset dataset = (Dataset) dsIt.next();
    			if ( dataset.getVariables().size() > 0 ) {
    				Variable var = dataset.getVariables().get(0);

    				

    				//get the data URL
    				String durl = dataset.getAttributesAsMap().get("url");
    				String varURL = var.getAttributesAsMap().get("url");
    				String dsURL = LASConfig.combinedURL(durl, varURL);

    				//if url is in format of ....xyz.nc#var
    				if(dsURL.contains("#")){
    					String[] tmp = dsURL.split("#");
    					dsURL = tmp[0];
    				}

    				//get dds for a remote dataset
    				if(dsURL != null && dsURL.contains("http")){
    					
    					String ds = lto.getDataset();
    					if( (ds == null) || ((ds != null)&&(dsURL.contains(ds))) ) {
    						// Only add it to the test results if we're going to test it.
    						if ( web_output ) {
            					testResults.putDataset(TestConstants.TEST_DIRECT_OPENDAP, dataset.getName(), dataset.getID());
            				}
    						if ( !web_output ) {
    							System.out.println("---- Check dataset: " + dsURL);
    						}
    						String status = "passed";
    						if ( !getDDS(dsURL) ) {
    							status = "failed";
    						}
    						if ( web_output ) {
    							testResults.addResult(TestConstants.TEST_DIRECT_OPENDAP, dataset.getID(), dsURL, status);
    						} else {
    							if ( status.equals("passed") ) {
    								System.out.println("OK!");
    							}
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
     * Test F-TDS URLs
     *
     */
    public void testFTDS(boolean web_output){
        String dsID;
        String varID;
        String varpath;

        ArrayList<Dataset> datasets = new ArrayList<Dataset>();
        ArrayList<Variable> variables = new ArrayList<Variable>();

        String test_output_file = null;
    	LASTestResults testResults = new LASTestResults();
        
        try{
            //get datasets
            Element datasetsE = lasConfig.getDatasetsAsElement();
            List datasetElements = datasetsE.getChildren("dataset");

            if ( web_output ) {
                test_output_file = lasConfig.getOutputDir()+File.separator+TestConstants.TEST_RESULTS_FILE;
                File c = new File(test_output_file);
                if ( c.exists() ) {
                    JDOMUtils.XML2JDOM(c, testResults);
                }
                Date date = new Date();
                testResults.putTest(TestConstants.TEST_F_TDS_OPENDAP, date.getTime());
            }

            //loop over each dataset
            for(Iterator dsIt = datasetElements.iterator(); dsIt.hasNext();){
                Element datasetE = (Element) dsIt.next();
                dsID = datasetE.getAttributeValue("ID");

                if ( web_output) 
                    testResults.putDataset(TestConstants.TEST_F_TDS_OPENDAP, datasetE.getAttributeValue("name"), dsID);
                
                //get first variable of this dataset
                variables = lasConfig.getVariables(dsID);
                varID = variables.get(0).getID();

                //build XPath for this variable
                varpath = "/lasdata/datasets/dataset[@ID='"+dsID+"']/variables/variable[@ID='"+varID+"']";

                //test FTDS URLs
                String ftdsURL= lasConfig.getDataAccessURL(varpath, true);
                
                if(ftdsURL != null && ! ftdsURL.equals("") && ftdsURL.contains("http")){ 
                	
                	if ( !web_output ) {
					    System.out.println("---- Check dataset: " + ftdsURL);
                	}
                	String status = "passed";
                	if ( !getDDS(ftdsURL) ) {
						status = "failed";
					}
                	if ( web_output ) {
						testResults.addResult(TestConstants.TEST_F_TDS_OPENDAP, dsID, ftdsURL, status);
                	} else {
                		if ( status.equals("passed") ) {
							System.out.println("OK!");
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
     * Get URL of product server, which is specfied in las.xml
     * @param las_config JDOM object LASConfig for this installation
     *
     */
    public String getProductServerURL(LASConfig las_config){
        String productServerURL = null;
        Element operations =  las_config.getRootElement().getChild("operations");
        productServerURL = operations.getAttributeValue("url");
        return productServerURL;
    }

    /**
     * Get DDS for a dataset and print it on console
     * @param url URL of the dataset
     *
     */
    public boolean getDDS(String url) throws Exception{
       
        boolean printDDS = lto.showDDS();

        
        try{
            DConnect dc = new DConnect(url);
            DDS mydds = dc.getDDS();
            
            if(printDDS){
                mydds.print(System.out);
            }

            return true;

        }catch (MalformedURLException e){
            //java.net.MalformedURLException - if the URL given to the constructor has an error
            System.out.println("The URL given to the constructor has an error.");
            //e.printStackTrace();
            System.out.println(e.getMessage());
            return false;

        }catch (IOException e){
            //java.io.IOException - if an error connecting to the remote server
            System.out.println("An error occurs when connecting to the remote server.");
            System.out.println(e.getMessage());

        }catch (ParseException e){
            //dods.dap.parser.ParseException - if the DDS parser returned an error
            System.out.println("The DDS parser returned an error.");
            System.out.println(e.getMessage());
            return false;

        }catch (DDSException e){
            //dods.dap.DDSException - on an error constructing the DDS
            System.out.println("An error occurs when constructing the DDS.");
            System.out.println(e.getErrorMessage());
            return false;

        }catch (DODSException e){
            //dods.dap.DODSException - if an error returned by the remote server
            System.out.println("An error was returned by the remote server.");
            System.out.println(e.getErrorMessage());
            return false;
        }catch (Exception e){
            System.out.println("An error occurs when connecting to the data server.");
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
