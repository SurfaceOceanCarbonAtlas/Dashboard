/**
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development. 
 */
package gov.noaa.pmel.tmap.iosp;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
/**
 * This class implements the EntityResolver interface for JDOM.  We use this
 * when we parse &file; entities in our XML.
 * @author Roland Schweitzer
 *
 */
public class MyResolver implements EntityResolver {

    public MyResolver() {
        super();
    }
    /**
     * This method is called whenever an external entity is accessed
     * for the first time.
     */
    public InputSource resolveEntity(String publicId, String systemId) 
            throws SAXException, IOException {
        
        
        
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
