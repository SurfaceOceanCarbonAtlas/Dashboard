package gov.noaa.pmel.tmap.las.jdom;

import org.jdom.Element;
import org.jdom.JDOMException;

import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.jdom.LASDocument;

public class LASDatabaseBackendConfig extends LASDocument {

    public String getDriver(String db_name) throws LASException, JDOMException {
        Element database = getElementByXPath("/databases/database[@name='"+db_name+"']");
        if ( database == null ) {
            throw new LASException("No database definition found for database "+db_name+" in database config.");
        }
        return database.getAttributeValue("driver");
    }
    public String getType(String db_name) throws LASException, JDOMException {
        Element database = getElementByXPath("/databases/database[@name='"+db_name+"']");
        if ( database == null ) {
            throw new LASException("No database definition found for database "+db_name+" in database config.");
        }
        return database.getAttributeValue("type");
    }
    public String getHost(String db_name) throws LASException, JDOMException {
        Element database = getElementByXPath("/databases/database[@name='"+db_name+"']");
        if ( database == null ) {
            throw new LASException("No database definition found for database "+db_name+" in database config.");
        }
        return database.getAttributeValue("host");
    }
    public String getPort(String db_name) throws LASException, JDOMException {
        Element database = getElementByXPath("/databases/database[@name='"+db_name+"']");
        if ( database == null ) {
            throw new LASException("No database definition found for database "+db_name+" in database config.");
        }
        return database.getAttributeValue("port"); 
    }
    public String getUser(String db_name) throws LASException, JDOMException {
        Element database = getElementByXPath("/databases/database[@name='"+db_name+"']");
        if ( database == null ) {
            throw new LASException("No database definition found for database "+db_name+" in database config.");
        }
        return database.getAttributeValue("user");
    }
    public String getPassword(String db_name) throws LASException, JDOMException {
        Element database = getElementByXPath("/databases/database[@name='"+db_name+"']");
        if ( database == null ) {
            throw new LASException("No database definition found for database "+db_name+" in database config.");
        }
        return database.getAttributeValue("password");
    }
    public String getConnectionURL(String db_name) throws LASException, JDOMException {
        Element database = getElementByXPath("/databases/database[@name='"+db_name+"']");
        if ( database == null ) {
            throw new LASException("No database definition found for database "+db_name+" in database config.");
        }
        return database.getAttributeValue("connectionURL");
    }

}
