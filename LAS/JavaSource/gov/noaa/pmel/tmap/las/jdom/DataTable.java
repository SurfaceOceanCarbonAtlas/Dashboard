package gov.noaa.pmel.tmap.las.jdom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DataTable {
    List<String> headers = new ArrayList<String>();
    List<String> preamble = new ArrayList<String>();
    Map<String, List<String>> data = new HashMap<String, List<String>>();
    public DataTable() {
    }
    public DataTable(File file) throws IOException {
       read(file);
    }
   
    public void read(File chart) throws IOException {
        String[] headers;
        Map<String, List<String>> data = new HashMap<String, List<String>>();

        BufferedReader reader = new BufferedReader(new FileReader(chart));
        String line = reader.readLine();
        boolean columnStart = false;
        boolean columnEnd = false;
        boolean headersStart = false;
        boolean dataStart = false;
        int cols = 0;
        try {
			while ( line != null ) {
			    if ( line.contains("Column ") ) {
			        cols++;
			        columnStart = true;
			    } else if ( line.contains("Total ") ) {
			        // add the total to the preamble below
			    } else if ( line.trim().length() == 0 ) {
			        // Skip any line that is all white space...
			    } else {
			        if ( columnStart ) {
			            columnEnd = true;
			        }
			    }

			    
			    if ( columnStart && columnEnd && !headersStart) {
			        headersStart = true;
			        headers = line.trim().split(",");
			        List<String> h = new ArrayList<String>();
			        for (int i = 0; i < headers.length; i++) {
			            String value = headers[i];
			            if ( value.startsWith("\"")) {
			                value = value.substring(1, value.length());
			            }
			            if ( value.endsWith("\"") ) {
			                value = value.substring(0, value.length()-1);
			            }
			            h.add(value);
			        }
			        setHeaders(h);
			    } else if ( !headersStart && !columnEnd ) {
			        preamble.add(line.trim());
			    }
			    if (headersStart) {
			        if ( dataStart ) {
			            if ( line.contains("---- M") ) line = reader.readLine(); // Skip some debug output for now...
			            String[] values = line.trim().split(",");
			            for (int i = 0; i < getHeaders().size(); i++ ) {
			                String header = getHeaders().get(i);
			                List<String> datalist = data.get(header);
			                if ( datalist == null ) {
			                    datalist = new ArrayList<String>();
			                    data.put(header, datalist);
			                }
			                String value = values[i];
			                if ( value.startsWith("\"")) {
			                    value = value.substring(1, value.length());
			                }
			                if ( value.endsWith("\"") ) {
			                    value = value.substring(0, value.length()-1);
			                }
			                datalist.add(value);
			            }
			        }
			        dataStart = true;
			    }
			    line = reader.readLine();
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if ( reader != null )
				reader.close();
		}
        setData(data);
    }
    public List<String> getPreamble() {
        return preamble;
    }
    public void setPreamble(List<String> preamble) {
        this.preamble = preamble;
    }
    public List<String> getHeaders() {
        return headers;
    }
    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }
    public Map<String, List<String>> getData() {
        return data;
    }
    public void setData(Map<String, List<String>> data) {
        this.data = data;
    }
}
