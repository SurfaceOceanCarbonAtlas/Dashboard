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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactory;

import ucar.nc2.NetcdfFile;
import ucar.unidata.io.RandomAccessFile;

/**
 * This is where the construction of the Ferret data object derived from 
 * the HTTP Servlet Request object actually happens.  
 * @author Roland Schweitzer
 *
 */
public class FerretNetcdfFile extends NetcdfFile {

    private final static Logger log = LoggerFactory.getLogger(FerretNetcdfFile.class.getName());

    /**
     * Construct a new netCDF file from the RandomAccessFile (which contains the resulting Ferret script) and TDS request URI that was used to
     * create the script.
     * @param raf
     * @param location
     * @throws IOException
     */
    public FerretNetcdfFile(RandomAccessFile raf, String location) throws IOException {
        super(new FerretIOServiceProvider(), raf, location, null);  
        log.debug("Constructed new netCDF file at: "+location);
        finish();
    }
}
