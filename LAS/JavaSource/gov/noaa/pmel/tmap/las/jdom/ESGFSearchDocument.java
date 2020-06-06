package gov.noaa.pmel.tmap.las.jdom;

import gov.noaa.pmel.tmap.jdom.LASDocument;
import gov.noaa.pmel.tmap.las.client.serializable.ESGFDatasetSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.FacetMember;
import gov.noaa.pmel.tmap.las.client.serializable.FacetSerializable;
import gov.noaa.pmel.tmap.las.util.Constants;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jdom.Element;
import org.jdom.filter.ElementFilter;

public class ESGFSearchDocument extends LASDocument {
    
    public int getStatus() {
        String status = getValue("int", "status");
        if ( status == null ) return -1;
        try {
            int i = Integer.valueOf(status);
            return i;
        } catch ( NumberFormatException e ) {
            return -1;
        }
        
    }

    public List<FacetSerializable> getFacets() {
        List<FacetSerializable> facets = new ArrayList<FacetSerializable>();
        List<String> facetnames = getStringValues("arr", "facet.field");
        for ( Iterator facetnameIt = facetnames.iterator(); facetnameIt.hasNext(); ) {
            String facetname = (String) facetnameIt.next();
            FacetSerializable facet = new FacetSerializable(facetname);
            facets.add(facet);
            List<FacetMember> facetsmembers = getFacetValues("lst", facetname);
            facet.setMembers(facetsmembers);
        }
        return facets;
    }
    public List<ESGFDatasetSerializable> getDatasets() {
        List<ESGFDatasetSerializable> datasets = new ArrayList<ESGFDatasetSerializable>();
        Element root = getRootElement();
        int numFound = 0;
        int start = 0;
        if (root != null ) {
            Element result = root.getChild("result");
            if ( result != null ) {
                String numFoundString = result.getAttributeValue("numFound");
                if ( numFoundString != null ) {
                    try {
                        numFound = Integer.valueOf(numFoundString);
                    } catch ( NumberFormatException e ) {
                       // wa'evz
                    }
                }
                String startString = result.getAttributeValue("start");
                if ( startString != null ) {
                    try {
                        start = Integer.valueOf(startString);
                    } catch ( NumberFormatException e) {
                        // wa'evz
                    }
                }
                List<Element> docs = result.getChildren("doc");
                int position = start;
                for ( Iterator docIt = docs.iterator(); docIt.hasNext(); ) {
                    Element doc = (Element) docIt.next();
                    String name = getValue(doc, "str", "title");
                    String idnode = getValue(doc, "str", "id");
                    String[] parts = idnode.split("\\|");
                    String id = parts[0];
                    String node = parts[1];
                    String las = "http://"+node+"/las";
                    String key = "";
                    try {
                        key = JDOMUtils.MD5Encode(las);
                    } catch (UnsupportedEncodingException e) {
                        // bummer
                    }
                    String LASID = key+Constants.NAME_SPACE_SPARATOR+id;
                    ESGFDatasetSerializable dataset = new ESGFDatasetSerializable(name, id, node, numFound, position, LASID, false);
                    datasets.add(dataset);
                    position++;
                }
            }
        }
        return datasets;

    }
    private List<String> getStringValues(String elementName, String name) {
        List<String> values = new ArrayList<String>();
        Element root = getRootElement();
        Iterator elements = root.getDescendants(new ElementFilter(elementName));
        if ( root == null ) {
            return values;
        }
        while ( elements.hasNext() ) {
            Element e = (Element) elements.next();
            String ename = e.getAttributeValue("name");
            if (ename != null && ename.equals(name) ) {
                List<Element> strs = e.getChildren("str");
                for ( Iterator strIt = strs.iterator(); strIt.hasNext(); ) {
                    Element str = (Element) strIt.next();     
                    String value = str.getTextTrim();
                    if ( value != null ) {
                        values.add(value);
                    }
                }
            }
        }
        return values;
    }
    private List<FacetMember> getFacetValues(String elementName, String name ) {
        List<FacetMember> members = new ArrayList<FacetMember>();
        Element root = getRootElement();
        Iterator elements = root.getDescendants(new ElementFilter(elementName));
        if ( root == null ) {
            return members;
        }
        while ( elements.hasNext() ) {
            Element e = (Element) elements.next();
            String ename = e.getAttributeValue("name");
            if (ename != null && ename.equals(name) ) {
                List<Element> strs = e.getChildren("int");
                for ( Iterator strIt = strs.iterator(); strIt.hasNext(); ) {
                    Element str = (Element) strIt.next();
                    String fname = str.getAttributeValue("name");
                    String count = str.getTextTrim();
                    int c = 0;
                    try {
                        c = Integer.valueOf(count);
                    } catch ( NumberFormatException e1 ) {
                       // Ok, must be 0;
                    }
                    if ( count != null ) {
                        members.add(new FacetMember(fname, c));
                    }
                }
            }
        }
        Collections.sort(members, new Comparator<FacetMember>() {

            @Override
            public int compare(FacetMember o1, FacetMember o2) {
                if ( o1 == null ) return -1;
                if ( o2 == null ) return 1;
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
            
        });
        return members;
    }
    
    private String getValue(String elementName, String name ) {
        Element root = getRootElement();
        return getValue(root, elementName, name);
    }
    private String getValue(Element element, String elementName, String name ) {
        if ( element == null ) {
            return null;
        }
        Iterator elements = element.getDescendants(new ElementFilter(elementName));
        while ( elements.hasNext() ) {
            Element e = (Element) elements.next();
            String ename = e.getAttributeValue("name");
            if (ename != null && ename.equals(name) ) {
                return e.getTextTrim();
            }
        }
        return null;
    }
}
