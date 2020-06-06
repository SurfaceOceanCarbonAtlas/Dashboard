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
public class ArangeBean extends LasBean {
  private String start;
  private String step;
  private String size;
  private String end;
  

  public ArangeBean() {
  }

  public void setStart(String start) {
    this.start = start;
  }

  public void setStep(String step) {
    this.step = step;
  }

  public void setSize(String size) {
    this.size = size;
  }
  
  public void setEnd(String end) {
	  this.end = end;
  }

  public String getStart() {
    return start;
  }

  public String getStep() {
    return step;
  }

  public String getSize() {
    return size;
  }
  
  public String getEnd() {
	  return end;
  }

  public String toString() {
     return "start="+start+" step="+step+" size="+size;
  }

@Override
public boolean equals(LasBean bean) {
	ArangeBean b = null;
	if ( !( bean instanceof ArangeBean ) ) {
		return false;
	} else { 
		b = (ArangeBean) bean;
	}
	if ( !size.equals(b.getSize() )) {
		return false;
	}
	if ( !step.equals(b.getStep() )) {
		return false;
	}
	if ( !start.equals(b.getStart() )) {
		return false;
	}
	return true;
}
}
