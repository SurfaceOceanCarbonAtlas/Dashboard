package gov.noaa.pmel.tmap.las.product.server;

import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.jdom.LASConfig;
import gov.noaa.pmel.tmap.las.jdom.ServerConfig;
import gov.noaa.pmel.tmap.las.util.FileListing;
import gov.noaa.pmel.tmap.las.util.NameValuePair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;

import javax.servlet.ServletContext;

import org.joda.time.DateTime;

public class ReaperTask extends TimerTask {

    ServletContext context;
    long time = 0; // Time in system millis which is the cut off for files to survive.  Any file with a modified date earlier than this time ago will be removed.
    public ReaperTask(ServletContext context, long time) {
        super();
        this.context = context;
        this.time = time;
    }
    @Override
    public void run() {
        // Go through the cache object and delete old files.
        DateTime now = new DateTime();
        DateTime then = now.minus(time);
        Cache cache = (Cache) context.getAttribute(ServerConfigPlugIn.CACHE_KEY);
        // Go through the files on the file system and delete old files, then if they are in the cache remove them...
        LASConfig lasConfig = (LASConfig) context.getAttribute(LASConfigPlugIn.LAS_CONFIG_KEY);
        String out = lasConfig.getOutputDir();
        try {
            List<File> output = FileListing.getFileListing(new File(out));
            for (Iterator iterator = output.iterator(); iterator.hasNext();) {
                File file = (File) iterator.next();
                if ( !file.getAbsolutePath().contains("lasV7") && !file.getAbsolutePath().contains("cache") ) {
                    if ( time > 0 && file.exists() && file.lastModified() < then.getMillis() ) {
                        file.delete();
                        cache.removeFile(file.getAbsolutePath());
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // This is not good, but what can you do?
        }
        ServerConfig serverConfig = (ServerConfig)context.getAttribute(ServerConfigPlugIn.SERVER_CONFIG_KEY);
        // Get the operation temp directory for each service and clean it.
        ArrayList<NameValuePair> pair = serverConfig.getServiceNamesAndURLs();
        for (Iterator pairIt = pair.iterator(); pairIt.hasNext();) {
            NameValuePair nameValuePair = (NameValuePair) pairIt.next();
            String name = nameValuePair.getName().toLowerCase(Locale.ENGLISH);
            String servicedir = JDOMUtils.getResourcePath(this, "resources/"+name+"/temp");
            try {
                if ( servicedir != null ) {
                    File servicefile = new File(servicedir);
                    if ( servicefile.exists() ) {
                        List<File> output = FileListing.getFileListing(servicefile);
                        for (Iterator iterator = output.iterator(); iterator.hasNext();) {
                            File file = (File) iterator.next();
                            if ( time > 0 && file.exists() && file.lastModified() < then.getMillis() ) {
                                file.delete();
                            }
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                // This is not good, but what can you do?
            }
        }
       
        // Get the F-TDS temp directory
        
        String ftds_dir = serverConfig.getFTDSDir().replace("data", "temp");
        try {
            List<File> output = FileListing.getFileListing(new File(ftds_dir));
            for (Iterator iterator = output.iterator(); iterator.hasNext();) {
                File file = (File) iterator.next();
                if ( time > 0 && file.exists() && file.lastModified() < then.getMillis() ) {
                    file.delete();
                }
            }
        } catch (FileNotFoundException e) {
            // This is not good, but what can you do?
        }
        
        // If this is an ESGF configuration, then remove old data sets from the config.
        if ( lasConfig.pruneCategories() ) {
            try {
                lasConfig.removeOldDatasets(then);
            } catch (Exception e) {
               // Not great, but we'll live with it...
            }     
        }
    }
    
}
