package gov.noaa.pmel.tmap.las.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gov.noaa.pmel.tmap.las.client.serializable.AnalysisAxisSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.AnalysisSerializable;

import org.jdom.Element;

public class Analysis extends Container implements AnalysisInterface {
	/*
	  <analysis label="Average AIR TEMPERATURE">
          <axis type="y" lo="-89" hi="89" op="ave"/>
      </analysis>
	 
	 */
	
	public Analysis(Element element) {
		super(element);
	}


	@Override
	public String getLabel() {
		return element.getAttributeValue("label");
	}



	@Override
	public void setLabel(String label) {
		element.setAttribute("label", label);
	}

	public AnalysisSerializable getAnalysisSerializable() {
		AnalysisSerializable analysis = new AnalysisSerializable();
		analysis.setLabel(getLabel());
		List<AnalysisAxisSerializable> wireAxes = new ArrayList<AnalysisAxisSerializable>();
		List<Element> axes = element.getChildren("axis");
		for (Iterator axIt = axes.iterator(); axIt.hasNext();) {
			Element aE = (Element) axIt.next();
			AnalysisAxis a = new AnalysisAxis(aE);
			wireAxes.add(a.getAnalysisAxisSerializable());
		}
		analysis.setAxes(wireAxes);
		return analysis;
	}
}
