package gov.noaa.pmel.tmap.addxml;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import ucar.nc2.dt.GridDataset;
import ucar.nc2.dt.GridDatatype;

public class GridDatasetComparator implements Comparator<DatasetGridPair> {

	@Override
	public int compare(DatasetGridPair pair1, DatasetGridPair pair2) {
		GridDataset gd1 = pair1.getGrid();
		GridDataset gd2 = pair2.getGrid();
		long diff = gd1.getEndDate().getTime() - gd2.getEndDate().getTime();
		 if ( diff > 0 ) {
			 return 1;
		 } else if ( diff == 0 ) {
			 return 0 ;
		 } else {
			 return -1;
		 }
	}

}
