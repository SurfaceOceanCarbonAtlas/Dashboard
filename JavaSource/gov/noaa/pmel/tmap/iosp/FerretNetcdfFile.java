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

import java.io.IOException;

import org.apache.log4j.Logger;

import ucar.nc2.NetcdfFile;
import ucar.unidata.io.RandomAccessFile;

/**
 * This is where the construction of the Ferret data object derived from 
 * the HTTP Servlet Request object actually happens.  
 * @author Roland Schweitzer
 *
 */
public class FerretNetcdfFile extends NetcdfFile {

    static private Logger log = Logger.getLogger(FerretNetcdfFile.class.getName());

    /**
     * Takes the HTTP Servlet Request which contains an _expr_ and turns it into a Ferret script.
     * @param request which contains the URL that must be cracked and turned into a Ferret script.
     * @throws IOException if it has trouble writing the script.
     */
    public FerretNetcdfFile(RandomAccessFile raf, String location) throws IOException {
        super(new FerretIOServiceProvider(), raf, location, null);  
        log.debug("Constructed new netCDF file at: "+location);
        finish();
    }
}
