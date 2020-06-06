package gov.noaa.pmel.tmap.addxml;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gov.noaa.pmel.tmap.las.proxy.LASProxy;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.apache.http.HttpException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import thredds.catalog.InvCatalog;
import thredds.catalog.InvCatalogFactory;
import thredds.catalog.InvDataset;
import thredds.catalog.ServiceType;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class ListThredds {
    // TODO cli for this
    static String searchUafErddap = "http://upwell.pfeg.noaa.gov/erddap/search/index.json?page=1&itemsPerPage=1000&searchFor=";
    /**
     * @param args
     */
    static int count;
    public static void main(String[] args) {
        AddxmlOptions options = new AddxmlOptions();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse( options, args);
            String thredds = cmd.getOptionValue("thredds");
            DateTime now = new DateTime();
            DateTimeFormatter iso = ISODateTimeFormat.dateTime();
            System.out.println("Starting scan of " + thredds + " at " + iso.print(now.getMillis()));
            InvCatalogFactory factory = new InvCatalogFactory("default", false);
            InvCatalog catalog = (InvCatalog) factory.readXML(thredds);
            StringBuilder buff = new StringBuilder();
            if (!catalog.check(buff, true)) {
                System.err.println("Invalid catalog <" + thredds + ">\n" + buff.toString());
            } else {
                List datasets = catalog.getDatasets();
                Iterator di = datasets.iterator();
                while (di.hasNext()) {
                    InvDataset td = (InvDataset) di.next();
                    listDataset(td);
                }
            }
            now = new DateTime();
            System.out.println("Ending scan of " + thredds + " at " + iso.print(now.getMillis()));
            System.out.println(count + " datasets found.");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private static void listDataset(InvDataset td) {
        if ( td.hasAccess() && td.getAccess(ServiceType.OPENDAP) != null ) {
            String turl = td.getAccess(ServiceType.OPENDAP).getStandardUrlName();
            System.out.println(turl);
            count++;
        }
        List children = td.getDatasets();
        for (int i = 0; i < children.size(); i++) {
            InvDataset child = (InvDataset) children.get(i);
            listDataset(child);
        }
    }
}
