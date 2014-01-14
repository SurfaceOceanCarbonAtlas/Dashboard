package gov.noaa.pmel.socat.dashboard.nc;

import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;

import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.NetcdfFileWriter.Version;
import ucar.nc2.Variable;
import ucar.nc2.dt.grid.NetcdfCFWriter;


public class CruiseSdgncFile {
    
    SocatMetadata metadata;
    List<SocatCruiseData> data;
    NetcdfFileWriter ncfile;

    public CruiseSdgncFile(SocatMetadata metadata, List<SocatCruiseData> data) {
        this.metadata = metadata;
        this.data = data;
    }
    public void create(String sdgFilename) throws IOException {

        ncfile = NetcdfFileWriter.createNew(Version.netcdf3, sdgFilename);


        Dimension d = ncfile.addDimension(null, "rows", data.size());
        List<Dimension> dims = new ArrayList<Dimension>();
        dims.add(d);
        if ( data.size() > 0 ) {
            Class<?> d0 = data.get(0).getClass();
            Field[] fields = d0.getDeclaredFields();

            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                String name = f.getName();
                Class<?> type = f.getType();
                if ( !name.equals("serialVersionUID")) {
                    
                   Variable var = null;
                    
                    
                    if ( type.equals(Long.class) || type.equals(Long.TYPE) ) {
                        var = ncfile.addVariable(null, name, DataType.DOUBLE, dims);
                    } else if ( type.equals(Double.class) || type.equals(Double.TYPE) ) {
                        var = ncfile.addVariable(null, name, DataType.DOUBLE, dims);
                    } else if ( type.equals(Integer.class) || type.equals(Integer.TYPE) ) {
                        var = ncfile.addVariable(null, name, DataType.INT, dims);
                    } else if ( type.equals(String.class) ) {
                        // Skip for now.
                    }
                    if ( var != null ) {
                        List<String> units = Constants.UNITS.get(name);
                        if ( units != null ) {
                            ncfile.addVariableAttribute(var, new Attribute("units", units));
                        }
                        String description = Constants.DESCRIPTION.get(name);
                        if ( description != null ) {
                            ncfile.addVariableAttribute(var, new Attribute("long_name", description));
                        }
                    }
                   
                    
                    
                    
                }
            }
            ncfile.create();
        }
    }
    public void write(String sdgFilename) throws IOException {
       
        
    }
    public void close() {
        try {
            ncfile.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
