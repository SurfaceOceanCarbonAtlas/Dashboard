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
import thredds.server.wms.ThreddsDataset;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class CheckERDDAP {
    // TODO cli for this
    static String searchUafErddap = "http://upwell.pfeg.noaa.gov/erddap/search/index.json?page=1&itemsPerPage=1000&searchFor=";
    static int good;
    static int bad;
    /**
     * @param args
     */
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
                    checkDataset(td);
                }
            }
            now = new DateTime();
            System.out.println("Ending scan of " + thredds + " at " + iso.print(now.getMillis()));
            System.out.println(good + " data sets found in the corresponding ERDDAP.");
            System.out.println(bad + " data sets missing from the corresponding ERDDAP");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private static void checkDataset(InvDataset td) {
        if ( td.hasAccess() && td.getAccess(ServiceType.OPENDAP) != null ) {
            String turl = td.getAccess(ServiceType.OPENDAP).getStandardUrlName();
            String url = turl;
            if ( url.startsWith("https://") ) {
                url = url.replaceAll("https://", "");
            } else if ( url.startsWith("http://") ) {
                url = url.replace("http://", "");
            }
            LASProxy lasProxy = new LASProxy();
            JsonParser jsonParser = new JsonParser();
            String indexJSON = null;
            try {
                indexJSON = lasProxy.executeGetMethodAndReturnResult(searchUafErddap+url);
            } catch (HttpException e) {
                System.out.println("No  - " + turl);
                bad++;
            } catch (IOException e) {
                System.out.println("No  - " + turl);
                bad++;
            }
            if ( indexJSON != null ) {
                JsonObject indexJO = jsonParser.parse(indexJSON).getAsJsonObject();
                JsonObject table = indexJO.get("table").getAsJsonObject();
                JsonArray rows = table.getAsJsonArray("rows");
                if ( rows.size() >= 1 ) {
                    System.out.println("Yes - " + turl);
                    good++;
                }
            }

        }
        List children = td.getDatasets();
        for (int i = 0; i < children.size(); i++) {
            InvDataset child = (InvDataset) children.get(i);
            checkDataset(child);
        }
    }
}
