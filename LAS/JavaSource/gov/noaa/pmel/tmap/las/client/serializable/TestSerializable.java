/**
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development. 
 */
package gov.noaa.pmel.tmap.las.client.serializable;



import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author rhs
 *
 */
public class TestSerializable extends Serializable implements IsSerializable {

     TestDataset[] testDatasets;
     String date;

 	/**
 	 * 
 	 */
 	public TestSerializable() {
 		
 	}

	public TestDataset[] getTestDatasets() {
		return testDatasets;
	}

	public void setTestDatasets(TestDataset[] testDatasets) {
		this.testDatasets = testDatasets;
	}
    
	public int size() { 
		return this.testDatasets.length;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDateString() {
		long datemilli = Long.valueOf(date);
		Date time = new Date(datemilli);
		return time.toString();
	}
	
	public boolean hasFailedResult() {
		TestDataset[] datasets = getTestDatasets();
		for (int i = 0; i < datasets.length; i++) {
			if ( datasets[i].hasFailedResult() ) return true;
		}
		return false;
	}
	public TestDataset[] getDatasetsWithFailedResult() {
		TestDataset[] datasets = getTestDatasets();
		ArrayList<TestDataset> failed = new ArrayList<TestDataset>();
		for (int i = 0; i < datasets.length; i++ ) {
			if ( datasets[i].hasFailedResult() ) {
				failed.add(datasets[i]);
			}
		}
		TestDataset[] fails = new TestDataset[failed.size()];
		return failed.toArray(fails);
	}
	public int getDatasetsWithFailedResultSize() {
		return getDatasetsWithFailedResult().length;
	}
}
