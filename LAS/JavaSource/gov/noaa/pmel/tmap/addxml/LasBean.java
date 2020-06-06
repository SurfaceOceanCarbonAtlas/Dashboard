package gov.noaa.pmel.tmap.addxml;

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
public abstract class LasBean {
  private String element;
  public LasBean() {
  }

  public void setElement(String element) {
    this.element = element;
  }

  public String getElement() {
    return element;
  }
  
  public abstract boolean equals(LasBean bean);
}
