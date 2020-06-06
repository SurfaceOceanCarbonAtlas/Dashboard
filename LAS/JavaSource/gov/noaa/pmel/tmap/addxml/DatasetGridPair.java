package gov.noaa.pmel.tmap.addxml;

import thredds.catalog.InvDatasetImpl;
import ucar.nc2.dt.GridDataset;

public class DatasetGridPair {
	private InvDatasetImpl dataset;
	private GridDataset grid;
	public DatasetGridPair(InvDatasetImpl dataset, GridDataset grid) {
		this.dataset = dataset;
		this.grid = grid;
	}
	public InvDatasetImpl getDataset() {
		return dataset;
	}
	public void setDataset(InvDatasetImpl dataset) {
		this.dataset = dataset;
	}
	public GridDataset getGrid() {
		return grid;
	}
	public void setGrid(GridDataset grid) {
		this.grid = grid;
	}

}
