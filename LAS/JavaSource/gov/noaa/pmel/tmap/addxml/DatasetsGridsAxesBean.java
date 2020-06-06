package gov.noaa.pmel.tmap.addxml;

import java.util.*;

/**
 * <p>Title: addXML</p>
 *
 * <p>Description: Reads local or OPeNDAP netCDF files and generates LAS XML
 * configuration information.</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: NOAA/PMEL/TMAP</p>
 *
 * @author RHS
 * @version 1.0
 */
public class DatasetsGridsAxesBean {
  private List<DatasetBean> datasets;
  private List<GridBean> grids;
  private List<AxisBean> axes;
  private String error;
  
  public DatasetsGridsAxesBean() {
      datasets = new ArrayList<DatasetBean>();
      grids = new ArrayList<GridBean>();
      axes = new ArrayList<AxisBean>();
      error = null;
  }
  

  public void setDatasets(List<DatasetBean> datasets) {
    this.datasets = datasets;
  }

  public void setGrids(List<GridBean> grids) {
    this.grids = grids;
  }

  public void setAxes(List<AxisBean> axes) {
    this.axes = axes;
  }

  public void setError(String error) {
    this.error = error;
  }

  public List<DatasetBean> getDatasets() {
    return datasets;
  }

  public List<GridBean> getGrids() {
    return grids;
  }

  public List<AxisBean> getAxes() {
    return axes;
  }

  public String getError() {
    return error;
  }
}
