package gov.noaa.pmel.tmap.las.client.serializable;

import java.util.ArrayList;
import java.util.Locale;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TestDataset extends Serializable implements IsSerializable {
	TestResult[] results;

	public TestResult[] getResults() {
		return results;
	}

	public void setResults(TestResult[] results) {
		this.results = results;
	}
    
	public int size() { 
		return results.length;
	}
	
	public TestResult[] getFailedResults() {
		ArrayList<TestResult> failedResults = new ArrayList<TestResult>();
		TestResult[] results = getResults();
		for (int i = 0; i < results.length; i++) {
			if ( results[i].getStatus().toLowerCase(Locale.ENGLISH).contains("failed")) {
				failedResults.add(results[i]);
			}
		}
		TestResult[] failures = new TestResult[failedResults.size()];
		return (TestResult[]) failedResults.toArray(failures);
	}
	
	public boolean hasFailedResult() {
		return getFailedResults().length > 0;
	}
	
	public int getFailedSize() {
		return getFailedResults().length;
	}
	
}
