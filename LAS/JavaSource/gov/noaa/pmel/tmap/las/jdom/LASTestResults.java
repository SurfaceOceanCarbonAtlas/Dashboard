package gov.noaa.pmel.tmap.las.jdom;

import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.jdom.LASDocument;
import gov.noaa.pmel.tmap.las.client.lastest.TestConstants;
import gov.noaa.pmel.tmap.las.client.serializable.TestDataset;
import gov.noaa.pmel.tmap.las.client.serializable.TestResult;
import gov.noaa.pmel.tmap.las.client.serializable.TestSerializable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

public class LASTestResults extends LASDocument {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -854538547489836923L;
	
	public static final String KEY_NAME = "name";
	public static final String KEY_ID = "id";
	public static final String KEY_DATE = "date";
	public static final String KEY_URL = "url";
	public static final String KEY_STATUS = "status";
	public static final String KEY_VIEW = "view";
	public static final String KEY_PRODUCT = "product";
	public static final String KEY_TIME_STEP = "time_step";
	
	public LASTestResults() {
		super();
		setRootElement(new Element("tests"));
	}
	public void putTest(String name, long date) {
		Element existingTest = findTest(name);
		if ( existingTest != null ) {
			getRootElement().removeContent(existingTest);
		}
		Element test = new Element("test");
		test.setAttribute(KEY_NAME, name);
		test.setAttribute(KEY_DATE, Long.toString(date));
		getRootElement().addContent(test);
	}
	public void putDataset(String testName, String name, String dsID) throws LASException {
		Element existingTest = findTest(testName);
		if ( existingTest != null ) {
		    removeDataset(existingTest, dsID);
		} else {
			throw new LASException("Illegal state.  Attempt to add a data set to a test that does not exist.  Create the test first.");
		}
		Element dataset = new Element("dataset");
		dataset.setAttribute(KEY_NAME, name);
		dataset.setAttribute(KEY_ID, dsID);
		existingTest.addContent(dataset);
	}
	public void addProductResult(String testName, String dsID, String url, String view, String product, String time_step, String status) {
		Element dataset = findOrAddDataset(testName, dsID);
		Element resultE = new Element("result");
		resultE.setAttribute(KEY_URL, url);
		resultE.setAttribute(KEY_STATUS, status);
		resultE.setAttribute(KEY_VIEW, view);
		resultE.setAttribute(KEY_PRODUCT, product);
		resultE.setAttribute(KEY_TIME_STEP, time_step);
		dataset.addContent(resultE);
	}
	public void addResult(String testName, String dsID, String url, String status) {	
		Element dataset = findOrAddDataset(testName, dsID);
		Element resultE = new Element("result");
		resultE.setAttribute(KEY_URL, url);
		resultE.setAttribute(KEY_STATUS, status);
		dataset.addContent(resultE);
	}
	private Element findOrAddDataset(String testName, String dsID) {
		Element mytest = findTest(testName);
		if ( mytest == null ) {
			mytest = new Element("test");
			mytest.setAttribute(KEY_NAME, testName);
			addContent(mytest);
		}
		Element dataset = findDataset(mytest, dsID);
		if ( dataset == null ) {
			dataset = new Element("dataset");
			mytest.addContent(dataset);
		}
		return dataset;
	}
	private Element findDataset(Element test, String dsID) {
		Element mydataset = null;
		List datasets = test.getChildren("dataset");
		for (Iterator dsIt = datasets.iterator(); dsIt.hasNext();) {
			Element dataset = (Element) dsIt.next();
			if ( dataset.getAttributeValue(KEY_ID).equals(dsID) ) {
				mydataset = dataset;
			}
		}
		return mydataset;
	}
	private Element findTest (String name) {
		Element mytest = null;
		List tests = getRootElement().getChildren("test");
		for (Iterator testsIt = tests.iterator(); testsIt.hasNext();) {
			Element test = (Element) testsIt.next();
			if ( test.getAttributeValue(KEY_NAME).equals(name) ) {
				mytest = test;
			}
		}
		return mytest;
	}
	private void removeDataset(Element test, String dsID) {
		Element existingDataset = null;
		List datasets = test.getChildren("dataset");
		for (Iterator dsIt = datasets.iterator(); dsIt.hasNext();) {
			Element dataset = (Element) dsIt.next();
			if ( dataset.getAttributeValue(KEY_ID).equals(dsID) ) {
				existingDataset = dataset;
			}
		}
		if ( existingDataset != null ) {
			test.removeContent(existingDataset);
		}
	}
	public int getTestsWithFailedResultSize() {
		return getTestsWithFailedResult().length;
	}
	public TestSerializable[] getTestsWithFailedResult() {
		TestSerializable[] all = getTests();
		ArrayList<TestSerializable> failed = new ArrayList<TestSerializable>();
		for ( int i = 0; i < all.length; i++ ) {
			if ( all[i].hasFailedResult() ) {
				failed.add(all[i]);
			}
		}
		TestSerializable[] fails = new TestSerializable[failed.size()];
		return failed.toArray(fails);
	}
	public TestSerializable getTest(String test) {
		TestSerializable[] tests = getTests();
		TestSerializable wireTest = null;
		for (int i = 0; i < tests.length; i++) {
			if ( tests[i].getName().equals(TestConstants.TEST_DIRECT_OPENDAP) && test.equals(TestConstants.KEY_TEST_DIRECT_OPENDAP) ) {
				wireTest = tests[i];
				wireTest.setID(TestConstants.KEY_TEST_DIRECT_OPENDAP);
			} else if ( tests[i].getName().equals(TestConstants.TEST_F_TDS_OPENDAP) && test.equals(TestConstants.KEY_TEST_F_TDS_OPENDAP) ) {
				wireTest = tests[i];
				wireTest.setID(TestConstants.KEY_TEST_F_TDS_OPENDAP);
			} else if ( tests[i].getName().equals(TestConstants.TEST_PRODUCT_RESPONSE) && test.equals(TestConstants.KEY_TEST_PRODUCT) ) {
				wireTest = tests[i];
				wireTest.setID(TestConstants.KEY_TEST_PRODUCT);
			}
		}
		return wireTest;
	}
	public TestSerializable[] getTests() {
		List testsList = getRootElement().getChildren("test");
		TestSerializable[] tests = new TestSerializable[testsList.size()];
		int i = 0;
		
		for (Iterator testsIt = testsList.iterator(); testsIt.hasNext();) {
			Element testE = (Element) testsIt.next();
			TestSerializable test = new TestSerializable();
			String name = testE.getAttributeValue(KEY_NAME);
			String date = testE.getAttributeValue(KEY_DATE);
			test.setName(name);		
			test.setDate(date);
			if ( name.equals(TestConstants.TEST_DIRECT_OPENDAP) ) {
				test.setID(TestConstants.KEY_TEST_DIRECT_OPENDAP);
			} else if ( name.equals(TestConstants.TEST_F_TDS_OPENDAP) ) {
				test.setID(TestConstants.KEY_TEST_F_TDS_OPENDAP);
			} else if ( name.equals(TestConstants.TEST_PRODUCT_RESPONSE) ) {
				test.setID(TestConstants.KEY_TEST_PRODUCT);
			}
			List datasetList = testE.getChildren("dataset");
			TestDataset[] testDatasets = new TestDataset[datasetList.size()];
			// Loop up the results.
			int j = 0;
			for (Iterator resultIt = datasetList.iterator(); resultIt.hasNext();) {
				Element dataset = (Element) resultIt.next();
				TestDataset testDataset = new TestDataset();
				testDataset.setName(dataset.getAttributeValue(KEY_NAME));
				testDataset.setID(dataset.getAttributeValue(KEY_ID));
				List resultsList = dataset.getChildren("result");
				TestResult[] testResults = new TestResult[resultsList.size()];
				int k = 0;
				for (Iterator resultsIt = resultsList.iterator(); resultsIt.hasNext();) {
					Element result = (Element) resultsIt.next();
					TestResult testResult = new TestResult();
					String url = result.getAttributeValue(KEY_URL);
					String status = result.getAttributeValue(KEY_STATUS);
					String view = result.getAttributeValue(KEY_VIEW);
					String product = result.getAttributeValue(KEY_PRODUCT);
					String time_step = result.getAttributeValue(KEY_TIME_STEP);
					if ( url != null ) {
						testResult.setUrl(url);
					}
					if ( status != null ) {
						testResult.setStatus(status);
					}
					if ( view != null ) {
						testResult.setView(view);
					}
					if ( product !=  null ) {
						testResult.setProduct(product);
					}
					if ( time_step != null ) {
						testResult.setTime(time_step);
					}
					testResults[k] = testResult;
					k++;
				}
				testDataset.setResults(testResults);
				testDatasets[j] = testDataset;
				j++;
			}
			// set the results.
			test.setTestDatasets(testDatasets);
			tests[i] = test;
			i++;
		}
		return tests;
	}
	
	public int size() {
		return getTests().length;
	}
}
