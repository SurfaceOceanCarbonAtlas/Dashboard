package gov.noaa.pmel.tmap.addxml;

/*
 * This is a totally bogus EntityResolver whose sole purpose is to allow
 * us to run through a document and totally ignore any external entities.
 * This only works because we have expandExternalEntites set to false
 * in the builder.
 *
 */
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import java.io.ByteArrayInputStream;

public class MyEntityResolver
    implements EntityResolver {
  public InputSource resolveEntity(String publicId, String systemId) {
    String input="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    return new InputSource(new ByteArrayInputStream(input.getBytes()));
  }
}
