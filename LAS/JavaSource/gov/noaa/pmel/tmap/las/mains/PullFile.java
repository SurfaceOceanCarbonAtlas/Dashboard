package gov.noaa.pmel.tmap.las.mains;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.HttpException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import gov.noaa.pmel.tmap.las.proxy.LASProxy;

public class PullFile {

    /**
     * @param args
     */
    public static void main(String[] args) {
        LASProxy lasProxy = new LASProxy();
        if (args.length <= 0 ) {
            System.out.println("Add URL to grab to arguments.");
        }
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();

        for (int i = 0; i < args.length; i++) {
            String dsUrl = args[i];
            
            try {
               
                if (dsUrl.startsWith("'") ) {
                    dsUrl = dsUrl.substring(1);
                }
                if ( dsUrl.endsWith("'") ) {
                    dsUrl = dsUrl.substring(0, dsUrl.length()-1);
                }
                File out = new File("out"+String.valueOf(i));
                DateTime dt = new DateTime();
                System.out.println("TableDapTool starting file pull for the only file at "+fmt.print(dt));
                String b = "http://dunkel.pmel.noaa.gov:8660/erddap/tabledap/dsg_files_badval_7f9a_0653_3fc1.ncCF";
                String q = "traj1,QC_flag,cruise_id,cruise_name,vessel_name,PIs,DOI,obs1,fCO2_recomputed,pressure,pressure_ncep_slp,pressure_equilibrium,salinity,salinity_woa5,temperature,temperature_equilibrium,ETOPO2_depth,day_of_year,WOCE_flag,fCO2_source,region_id,data_id,tmonth,lon360&time>=1968-11-16T22:00:00Z&time<=1988-01-01T00:00:00Z&orderBy(\"cruise_expocode,time\")";
                q = URLEncoder.encode(q.toString(), "UTF-8").replaceAll("\\+", "%20");
                lasProxy.executeGetMethodAndSaveResult(b + "?" + q, out, null);
                dt = new DateTime();
                System.out.println("TableDapTool finishing file pull for the only file at "+fmt.print(dt));
            } catch (HttpException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
