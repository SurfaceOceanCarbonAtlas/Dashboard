package gov.noaa.pmel.tmap.las.jdom;

import gov.noaa.pmel.tmap.jdom.LASDocument;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import org.jdom.Element;

public class LASRegionIndex extends LASDocument {

    private static Logger log = LoggerFactory.getLogger(LASRegionIndex.class.getName());
    private static final long serialVersionUID = 703675281298155235L;
    /**
     * 
     */
    
    public LASRegionIndex() {
        super();
    }
    
    public LASRegionIndex(File index) throws IOException {
        super();
        BufferedReader indexReader;

        FileReader f = new FileReader(index);
        indexReader = new BufferedReader(f);

        ArrayList<HashMap> sections = new ArrayList<HashMap>();

        HashMap<String, String> original;
		try {
			String line = indexReader.readLine();
			HashMap<String, String> section = new HashMap<String, String>();
			original = new HashMap<String, String>();
			while ( line != null ) {
			    /* The Ferret output looks like this:
			         "data region" " "
			         "original_region"
			         "original_x_lo" "-180.0"
			         "original_x_hi" "180.0"
			         "original_y_lo" "-89.0"
			         "original_y_hi" "89.0"
			         "original_z_lo" " "
			         "original_z_hi" " "
			         "original_t_lo" "15-Jan"
			         "original_t_hi" "15-Jan"
			         "original_k_lo" " "
			         "original_k_hi" " "
			         "original_l_lo" "1"
			         "original_l_hi" "1"
			         "end original_region"
			         "section" "1"
			         "x_lo"    "-181"
			         "x_hi"    "19"
			         "i_lo"    "-100"
			         "i_hi"    "0"
			         "y_lo"    "-89"
			         "y_hi"    "89"
			         "j_lo"    "1"
			         "j_hi"    "90"
			         "z_lo"    " "
			         "z_hi"    " "
			         "k_lo"    " "
			         "k_hi"    " "
			         "t_lo"    "16-JAN"
			         "t_hi"    "16-JAN"
			         "l_lo"    "1"
			         "l_hi"    "1"
			         "end section"
			         "section" "2"
			         "x_lo"    "59"
			         "x_hi"    "179"
			         "i_lo"    "20"
			         "i_hi"    "80"
			         "y_lo"    "-89"
			         "y_hi"    "89"
			         "j_lo"    "1"
			         "j_hi"    "90"
			         "z_lo"    " "
			         "z_hi"    " "
			         "k_lo"    " "
			         "k_hi"    " "
			         "t_lo"    "16-JAN"
			         "t_hi"    "16-JAN"
			         "l_lo"    "1"
			         "l_hi"    "1"
			         "end section"
			         "end region" 
			     */
			    if ( line.contains("\"section\"") ) {
			        if (section.size() == 0) {
			            sections.add(section);
			        }
			        else if ( section.size() > 0 ) {                  
			            section = new HashMap<String, String>();
			            sections.add(section);
			        }
			    } else if ( (line.contains("hi") || line.contains("lo")) && !line.contains("original") ) {
			        // Split on the " and use the parts.  See JavaDoc on split for explanation...
			        String[] parts = line.split("\"");
			        section.put(parts[1], parts[3]);
			    } else if ( (line.contains("hi") || line.contains("lo")) && line.contains("original") ) {
			        String[] parts = line.split("\"");
			        original.put(parts[1], parts[3]);
			    }
			    line = indexReader.readLine();
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if ( indexReader != null )
				indexReader.close();
		}
        Element region = new Element("region");

        this.setRootElement(region);
        
        Element originalE = new Element("original");
        for (Iterator oIt = original.keySet().iterator(); oIt.hasNext();) {
            String key = (String) oIt.next();
            String value = (String) original.get(key);
            Element keyE = new Element(key);
            keyE.setText(value);
            originalE.addContent(keyE);
        }
        
        region.addContent(originalE);

        log.debug("Found "+sections.size()+" sections.");

        int sectionIndex = 0;
        int isec = 0;
        for (Iterator secIt = sections.iterator(); secIt.hasNext();) {
            sectionIndex++;
            Element sectionE = new Element("section");
            sectionE.setAttribute("index", String.valueOf(sectionIndex));
            HashMap sectionHM = (HashMap) secIt.next();
            isec = sectionIndex + 1;
            log.debug("Adding section "+isec+".");
            for (Iterator keys = sectionHM.keySet().iterator(); keys.hasNext();) {
                String key = (String) keys.next();
                String value = (String) sectionHM.get(key);
                Element keyE = new Element(key);
                keyE.setText(value);
                sectionE.addContent(keyE);
            }
            region.addContent(sectionE);
        }
    }
    public String getStartCount(int section_index) {
    	String start = "[";
    	String count = "[";
    	ArrayList sections = getSections();
        HashMap section = (HashMap) sections.get(section_index);
        if ( section.containsKey("t") ) {
        	int tlo = Integer.valueOf((String)((HashMap)section.get("t")).get("index_lo")).intValue();
        	int thi = Integer.valueOf((String)((HashMap)section.get("t")).get("index_hi"));
        	int c = (thi - tlo) + 1;
        	start = start+tlo;
        	count = count + c;
        }
        if ( section.containsKey("z") ) {
        	int zlo = Integer.valueOf((String)((HashMap)section.get("z")).get("index_lo")).intValue();
        	int zhi = Integer.valueOf((String)((HashMap)section.get("z")).get("index_hi")).intValue();
        	int c = (zhi -zlo) + 1;
        	if ( start.equals("[") ) {
        		start = start + zlo;
        	} else {
        		start = start + "," + zlo;
        	}
        	if ( count.equals("[") ) {
        		count = count + c;
        	} else {
        		count = count + "," + c;
        	}
        }
        if ( section.containsKey("y") ) {
        	int ylo = Integer.valueOf((String)((HashMap)section.get("y")).get("index_lo")).intValue();
        	int yhi = Integer.valueOf((String)((HashMap)section.get("y")).get("index_hi")).intValue();
        	int c = (yhi - ylo ) + 1;
        	if ( start.equals("[") ) {
        		start = start + ylo;
        	} else {
        		start = start + "," + ylo;
        	}
        	if ( count.equals("[") ) {
        		count = count + c;
        	} else {
        		count = count + "," + c;
        	}
        }
        if ( section.containsKey("x") ) {
        	int xlo = Integer.valueOf((String)((HashMap)section.get("x")).get("index_lo")).intValue();
        	int xhi = Integer.valueOf((String)((HashMap)section.get("x")).get("index_hi")).intValue();
        	int c = (xhi - xlo) + 1;
        	if ( start.equals("[") ) {
        		start = start + xlo;
        	} else {
        		start = start + "," + xlo;
        	}
        	if ( count.equals("[") ) {
        		count = count + c;
        	} else {
        		count = count + "," + c;
        	}
        }
        start = start + "]";
        count = count + "]";
        return start + "," + count;
    }
    public String getOPeNDAPConstraint(int section_index) {
        String constraint = "";
        ArrayList sections = getSections();
        HashMap section = (HashMap) sections.get(section_index);
        if ( section.containsKey("t") ) {
        	int tlo = Integer.valueOf((String)((HashMap)section.get("t")).get("index_lo")).intValue();
        	int thi = Integer.valueOf((String)((HashMap)section.get("t")).get("index_hi"));
        	tlo = tlo - 1;
        	thi = thi - 1;
            constraint = constraint+"["+tlo+":1:"+thi+"]";
        }
        if ( section.containsKey("z") ) {
        	int zlo = Integer.valueOf((String)((HashMap)section.get("z")).get("index_lo")).intValue();
        	int zhi = Integer.valueOf((String)((HashMap)section.get("z")).get("index_hi")).intValue();
        	zlo = zlo - 1;
        	zhi = zhi - 1;
            constraint = constraint+"["+zlo+":1:"+zhi+"]";
        }
        if ( section.containsKey("y") ) {
        	int ylo = Integer.valueOf((String)((HashMap)section.get("y")).get("index_lo")).intValue();
        	int yhi = Integer.valueOf((String)((HashMap)section.get("y")).get("index_hi")).intValue();
        	ylo = ylo - 1;
        	yhi = yhi - 1;
            constraint = constraint+"["+ylo+":1:"+yhi+"]";
        }
        if ( section.containsKey("x") ) {
        	int xlo = Integer.valueOf((String)((HashMap)section.get("x")).get("index_lo")).intValue();
        	int xhi = Integer.valueOf((String)((HashMap)section.get("x")).get("index_hi")).intValue();
        	xlo = xlo - 1;
        	xhi = xhi - 1;
            constraint = constraint+"["+xlo+":1:"+xhi+"]";
        }
        return constraint;
    }
    public String getRangesCovered(int section_index) {
        String constraint = "% Region covered by this URL ";
        ArrayList sections = getSections();
        HashMap section = (HashMap) sections.get(section_index);
        if ( section.containsKey("t") ) {
            constraint = constraint+"t="+((HashMap)section.get("t")).get("lo")+":"+((HashMap)section.get("t")).get("hi")+" ";
        }
        if ( section.containsKey("z") ) {
            constraint = constraint+"z="+((HashMap)section.get("z")).get("lo")+":"+((HashMap)section.get("z")).get("hi")+" ";
        }
        if ( section.containsKey("y") ) {
            constraint = constraint+"y="+((HashMap)section.get("y")).get("lo")+":"+((HashMap)section.get("y")).get("hi")+" ";
        }
        if ( section.containsKey("x") ) {
            constraint = constraint+"x="+((HashMap)section.get("x")).get("lo")+":"+((HashMap)section.get("x")).get("hi")+" ";
        }
        return constraint;
    }
    public String getFerretRegion() {
        
        HashMap<String, HashMap<String,String>> original = getOriginalRegion();
        
        HashMap<String, String> extent = (HashMap<String, String>)original.get("x");
        
        String xlo = null;
        String xhi = null;
        String ylo = null;
        String yhi = null;
        String zlo = null;
        String zhi = null;
        String tlo = null;
        String thi = null;
        
        if ( extent != null ) {
            xlo = (String)extent.get("lo");
            xhi = (String)extent.get("hi");
        }
        
        extent = (HashMap<String, String>)original.get("y");
        
        if ( extent != null ) {
            ylo = (String)extent.get("lo");
            yhi = (String)extent.get("hi");
        }
        
        extent = (HashMap<String, String>)original.get("z");
        
        if ( extent != null ) {
            zlo = (String)extent.get("lo");
            zhi = (String)extent.get("hi");
        }

        extent = (HashMap<String, String>)original.get("t");
        
        if (extent != null ) {
            tlo = (String)extent.get("lo");
            thi = (String)extent.get("hi");
        }
        
        
        
        String region = "SET REGION";
        if ( xlo != null && !xlo.equals("") && !xlo.equals(" ") && !xlo.equals("  ")) {
            region = region+"/x=\""+xlo+"\":\""+xhi+"\"";
        }
        if ( ylo != null && !ylo.equals("") && !ylo.equals(" ") && !ylo.equals("  ")) {
            region = region+"/y=\""+ylo+"\":\""+yhi+"\"";
        }
        if ( zlo != null && !zlo.equals("") && !zlo.equals(" ") && !zlo.equals("  ")) {
            region = region+"/z=\""+zlo+"\":\""+zhi+"\"";
        }
        if ( tlo != null && !tlo.equals("") && !tlo.equals(" ") && !tlo.equals("  ")) {
            region = region+"/t=\""+tlo+"\":\""+thi+"\"";
        }
        
        return region;
        
    }
    
    public HashMap<String, HashMap<String, String>> getOriginalRegion() {
        HashMap<String, HashMap<String, String>> original = new HashMap<String, HashMap<String, String>>();
        Element originalE = this.getRootElement().getChild("original");
        HashMap<String, String> x = new HashMap<String, String>();
        HashMap<String, String> y = new HashMap<String, String>();
        HashMap<String, String> z = new HashMap<String, String>();
        HashMap<String, String> t = new HashMap<String, String>();
        List extents = originalE.getChildren();
        for (Iterator exIt = extents.iterator(); exIt.hasNext();) {
            Element extent = (Element) exIt.next();
            String name = extent.getName();
            String value = extent.getTextNormalize();
            if ( name.contains("x_lo") || name.contains("x_hi") ) {              
                if ( value != null && !value.equals("") && !value.equals(" ") && !value.equals("  ")) {
                    if ( name.equals("original_x_lo")) {
                        x.put("lo", value);
                    }
                    if ( name.equals("original_x_hi")) {
                        x.put("hi", value);
                    }
                }
                if ( x.size() > 0 ) {
                   original.put("x", x);
                }
            }
            if ( name.contains("y_lo") || name.contains("y_hi")) {                
                if ( value != null && !value.equals("") && !value.equals(" ") && !value.equals("  ")) {
                    if ( name.equals("original_y_lo")) {
                        y.put("lo", value);
                    }
                    if ( name.equals("original_y_hi")) {
                        y.put("hi", value);
                    }
                }
                if ( y.size() > 0 ) {
                    original.put("y", y);
                }                    
            }
            if ( name.contains("z_lo") || name.contains("z_hi") || name.contains("k_lo") || name.contains("k_hi")) {                
                if ( value != null && !value.equals("") && !value.equals(" ") && !value.equals("  ")) {
                    if ( name.equals("original_z_lo")) {
                        z.put("lo", value);
                    }
                    if ( name.equals("original_z_hi")) {
                        z.put("hi", value);
                    }
                    if ( name.equals("original_k_lo") ) {
                        z.put("index_lo", value);
                    }
                    if ( name.equals("original_k_hi") ) {
                        z.put("index_hi", value);
                    }
                }
                if ( z.size() > 0 ) {
                   original.put("z", z);
                }
            }
            if ( name.contains("t_lo") || name.contains("t_hi") || name.contains("l_lo") || name.contains("l_hi") ) {               
                if ( value != null && !value.equals("") && !value.equals(" ") && !value.equals("  ")) {
                    if ( name.contains("original_t_lo")) {
                        t.put("lo", value);
                    }
                    if ( name.contains("original_t_hi")) {
                        t.put("hi", value);
                    }
                    if ( name.equals("original_l_lo") ) {
                        t.put("index_lo", value);
                    }
                    if ( name.equals("original_l_hi") ) {
                        t.put("index_hi", value);
                    }
                }
                if ( t.size() > 0 ) {
                    original.put("t", t);
                }                    
            }
        }    
        return original;
    }
    
    public ArrayList<HashMap> getSections() {
        ArrayList<HashMap> sections = new ArrayList<HashMap>();
        List sectionsElementList = this.getRootElement().getChildren("section");       
        for (Iterator secIt = sectionsElementList.iterator(); secIt.hasNext();) {
            HashMap<String, String> x = new HashMap<String, String>();
            HashMap<String, String> y = new HashMap<String, String>();
            HashMap<String, String> z = new HashMap<String, String>();
            HashMap<String, String> t = new HashMap<String, String>();
            Element sect = (Element) secIt.next();
            HashMap<String, HashMap> section = new HashMap<String, HashMap>();
            List extents = sect.getChildren();
            for (Iterator exIt = extents.iterator(); exIt.hasNext();) {
                Element extent = (Element) exIt.next();
                String name = extent.getName();
                String value = extent.getTextNormalize();
                if ( name.startsWith("x") || name.startsWith("i")) {
                    
                    if ( value != null && !value.equals("") && !value.equals(" ") && !value.equals("  ")) {
                        if ( name.equals("x_lo")) {
                            x.put("lo", value);
                        }
                        if ( name.equals("x_hi")) {
                            x.put("hi", value);
                        }
                        if ( name.equals("i_lo") ) {
                            x.put("index_lo", value);
                        }
                        if ( name.equals("i_hi") ) {
                            x.put("index_hi", value);
                        }
                    }
                    if ( x.size() > 0 ) {
                       section.put("x", x);
                    }
                }
                if ( name.startsWith("y") || name.startsWith("j")) {
                    
                    if ( value != null && !value.equals("") && !value.equals(" ") && !value.equals("  ")) {
                        if ( name.equals("y_lo")) {
                            y.put("lo", value);
                        }
                        if ( name.equals("y_hi")) {
                            y.put("hi", value);
                        }
                        if ( name.equals("j_lo") ) {
                            y.put("index_lo", value);
                        }
                        if ( name.equals("j_hi") ) {
                            y.put("index_hi", value);
                        }
                    }
                    if ( y.size() > 0 ) {
                        section.put("y", y);
                    }                    
                }
                if ( name.startsWith("z") || name.startsWith("k")) {
                    
                    if ( value != null && !value.equals("") && !value.equals(" ") && !value.equals("  ")) {
                        if ( name.equals("z_lo")) {
                            z.put("lo", value);
                        }
                        if ( name.equals("z_hi")) {
                            z.put("hi", value);
                        }
                        if ( name.equals("k_lo") ) {
                            z.put("index_lo", value);
                        }
                        if ( name.equals("k_hi") ) {
                            z.put("index_hi", value);
                        }
                    }
                    if ( z.size() > 0 ) {
                       section.put("z", z);
                    }
                }
                if ( name.startsWith("t") || name.startsWith("l") ) {
                    
                    if ( value != null && !value.equals("") && !value.equals(" ") && !value.equals("  ")) {
                        if ( name.equals("t_lo")) {
                            t.put("lo", value);
                        }
                        if ( name.equals("t_hi")) {
                            t.put("hi", value);
                        }
                        if ( name.equals("l_lo") ) {
                            t.put("index_lo", value);
                        }
                        if ( name.equals("l_hi") ) {
                            t.put("index_hi", value);
                        }
                    }
                    if ( t.size() > 0 ) {
                        section.put("t", t);
                    }                    
                }
            }
            sections.add(section);
        }
        return sections;
    }
    public int getNumberOfSections() {
        List sections = this.getRootElement().getChildren("section");
        return sections.size();
    }
    
    // Probably won't use these since we need to loop the sections.
    public String getX_lo(int section) {
        List sections = this.getRootElement().getChildren("section");
        Element sectionE = (Element) sections.get(section);
        if ( sectionE != null ) {
        	return sectionE.getChildTextTrim("x_lo");
        } else {
        	return "";
        }
    }
    public String getX_hi(int section) {
        List sections = this.getRootElement().getChildren("section");
        Element sectionE = (Element)sections.get(section);
        if ( sectionE != null ) {
        	return sectionE.getChildTextTrim("x_hi");
        } else {
        	return "";
        }
    }
    
    public String getY_lo(int section) {
        List sections = this.getRootElement().getChildren("section");
        Element sectionE = (Element) sections.get(section);
        if ( sectionE != null ) {
        	return sectionE.getChildTextTrim("y_lo");
        } else {
        	return "";
        }
    }
    public String getY_hi(int section) {
        List sections = this.getRootElement().getChildren("section");
        Element sectionE = (Element) sections.get(section);
        if ( sectionE != null ) {
        	return sectionE.getChildTextTrim("y_hi");
        } else {
        	return "";
        }
    }
    
    public String getZ_lo(int section) {
        List sections = this.getRootElement().getChildren("section");
        Element sectionE = (Element) sections.get(section);
        if ( sectionE != null ) {
        	return sectionE.getChildTextTrim("z_lo");
        } else {
        	return "";
        }
    }
    public String getZ_hi(int section) {
        List sections = this.getRootElement().getChildren("section");
        Element sectionE = (Element) sections.get(section);
        if ( sectionE != null ) {
        	return sectionE.getChildTextTrim("z_hi");
        } else {
        	return "";
        }
    }
    
    public String getT_lo(int section) {
        List sections = this.getRootElement().getChildren("section");
        Element sectionE = (Element) sections.get(section);
        if ( sectionE != null ) {
        	return  sectionE.getChildTextTrim("t_lo");
        } else {
        	return "";
        }
    }
    public String getT_hi(int section) {
        List sections = this.getRootElement().getChildren("section");
        Element sectionE = (Element) sections.get(section);
        if ( sectionE != null ) {
        	return sectionE.getChildTextTrim("t_hi");
        } else {
        	return "";
        }
    }
}
