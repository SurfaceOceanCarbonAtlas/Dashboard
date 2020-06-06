package gov.noaa.pmel.tmap.addxml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class MyResolver implements EntityResolver {

    public MyResolver() {
        super();
        // TODO Auto-generated constructor stub
    }
    public InputSource resolveEntity(String publicId, String systemId) 
            throws SAXException, IOException {
        // This method is called whenever an external entity is accessed
        // for the first time.
        
        
            try {
                // Wrap the systemId in a URI object to make it convenient
                // to extract the components of the systemId
                URI uri = new URI(systemId);
                
                // Check if external source is a file
                if ("file".equals(uri.getScheme())) {
                    String filename = uri.getSchemeSpecificPart();
                    File file = new File(filename);
                    if (!file.exists()) {
                        return new InputSource(new StringReader(""));
                    } else {
                        return new InputSource(new FileReader(filename));
                    }
                }
            } catch (URISyntaxException e) {
            } catch (IOException e) {
            }
            
            // Returning null causes the caller to try accessing the systemid
            return null;
    }

}
