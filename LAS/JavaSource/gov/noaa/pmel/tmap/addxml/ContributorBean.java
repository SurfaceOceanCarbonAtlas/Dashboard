package gov.noaa.pmel.tmap.addxml;

import org.jdom.*;

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
public class ContributorBean {
  private String name;
  private String url;
  private String role;

  public ContributorBean() {
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }

  public String getRole() {
    return role;
  }

  /**
   * toXml
   *
   * @return Element
   */
  public Element toXml() {
    Element contributor = new Element("contributor");
    contributor.setAttribute("url", url);
    contributor.setAttribute("name", name);
    contributor.setAttribute("role", role);
    return contributor;
  }
}
