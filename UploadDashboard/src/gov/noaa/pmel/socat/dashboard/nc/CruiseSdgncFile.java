package gov.noaa.pmel.socat.dashboard.nc;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.james.mime4j.io.MaxLineLimitException;

import ucar.ma2.Array;
import ucar.ma2.ArrayChar;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayString;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.NetcdfFileWriter.Version;
import ucar.nc2.Variable;
import ucar.nc2.time.Calendar;
import ucar.nc2.time.CalendarDate;


public class CruiseSdgncFile {
    
    SocatMetadata metadata;
    List<SocatCruiseData> data;
    NetcdfFileWriter ncfile;
    String version = "CruiseSdgncFile 0.1";

    public CruiseSdgncFile(SocatMetadata metadata, List<SocatCruiseData> data) {
        this.metadata = metadata;
        this.data = data;
    }
    public void create(String sdgFilename) throws IOException {

        ncfile = NetcdfFileWriter.createNew(Version.netcdf3, sdgFilename);

        // According to the CF standard if a file only has one trajectory, the the trajectory dimension is not necessary.
        // However, who knows what would break downstream from this process without it...
        
        Dimension traj = ncfile.addDimension(null, "trajectory", 1);
        
        // There will be 9 trajectory variables of type character from the metadata.
        // Which is the longest?
        int maxchar = metadata.getMaxStringLength();
        Dimension stringlen = ncfile.addDimension(null, "string_lenght", maxchar);
        List<Dimension> trajdims = new ArrayList<Dimension>();
        trajdims.add(traj);
        trajdims.add(stringlen);

        Dimension d = ncfile.addDimension(null, "obs", data.size());
        List<Dimension> dims = new ArrayList<Dimension>();
        dims.add(d);
        
        // Make character netCDF variables of all the string metadata.
        Field[] metafields = metadata.getClass().getDeclaredFields();
        for (int i = 0; i < metafields.length; i++) {
            Field f = metafields[i];
            if ( f.getType().equals(String.class) && !Modifier.isStatic(f.getModifiers()) ) {
                try {
                    Variable var = ncfile.addVariable(null, f.getName(), DataType.CHAR, trajdims);
                    if ( f.getName().equals("expocode")) {
                        ncfile.addVariableAttribute(var, new Attribute("cf_role", "trajectory_id"));
                    }
                } catch (IllegalArgumentException e) {
                    // Carry on.  We'll just do without this one
                }
            }
        }
        
        
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
                        ncfile.addVariableAttribute(var, new Attribute("missing_value", Double.NaN));
                    }
                   
                    
                    
                    
                }
            }
            
            Variable var = ncfile.addVariable(null, "time", DataType.DOUBLE, dims);
            ncfile.addVariableAttribute(var, new Attribute("units", "seconds since 1970-01-01 00:00"));
            
            ncfile.addGroupAttribute(null, new Attribute("History", version));
            ncfile.addGroupAttribute(null, new Attribute("featureType", "Trajectory"));
            ncfile.addGroupAttribute(null, new Attribute("Convenstions", "CF-1.6"));
            ncfile.addGroupAttribute(null, new Attribute("EastMostLongitude", metadata.getEastmostLongitude()));
            ncfile.addGroupAttribute(null, new Attribute("WestMostLongitude", metadata.getWestmostLongitude()));
            ncfile.addGroupAttribute(null, new Attribute("NorthMostLatitude", metadata.getNorthmostLatitude()));
            ncfile.addGroupAttribute(null, new Attribute("SouthMostLatitude", metadata.getSouthmostLatitude()));
            
            CalendarDate start = CalendarDate.of(metadata.getBeginTime());
            CalendarDate end = CalendarDate.of(metadata.getEndTime());
            
            ncfile.addGroupAttribute(null, new Attribute("time_coverage_start", start.toString()));
            ncfile.addGroupAttribute(null, new Attribute("time_converage_end", end.toString()));

            ncfile.create();
            // The header has been created.  Now let's fill it up.
    
            for (int i = 0; i < metafields.length; i++) {
                Field f = metafields[i];
                if ( f.getType().equals(String.class) && !Modifier.isStatic(f.getModifiers()) ) {
                    try {
                        var = null;
                        String s = (String) f.get(metadata);
                        var = ncfile.findVariable(f.getName());
                        if ( var != null ) {
                            ArrayChar.D2 values = new ArrayChar.D2(1, maxchar);
                            values.setString(0, s);
                            ncfile.write(var, values);
                        }
                    } catch (IllegalArgumentException e) {
                        
                    
                    } catch (IllegalAccessException e) {
                        
                    } catch (InvalidRangeException e) {
                       
                    }
                }
            }
            
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                String name = f.getName();
                Class<?> type = f.getType();
                if ( !name.equals("serialVersionUID")) {
                    var = null;
                    if ( type.equals(Long.class) || type.equals(Long.TYPE) ) {
                        var = ncfile.findVariable(name);
                        if ( var != null ) {
                            ArrayDouble.D1 dvar = new ArrayDouble.D1(data.size());
                            for (int index = 0; index < data.size(); index++) {
                                SocatCruiseData datarow = (SocatCruiseData) data.get(index);
                                try {
                                    Long value = (Long) datarow.getClass().getDeclaredField(name).get(datarow);
                                    Double dvalue = new Double(value);
                                    dvar.set(index, dvalue);
                                } catch (IllegalArgumentException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (NoSuchFieldException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (SecurityException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                            try {
                                ncfile.write(var, dvar);
                            } catch (InvalidRangeException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    } else if ( type.equals(Double.class) || type.equals(Double.TYPE) ) {
                        var = ncfile.findVariable(name);
                        if ( var != null ) {
                            ArrayDouble.D1 dvar = new ArrayDouble.D1(data.size());
                            for (int index = 0; index < data.size(); index++) {
                                SocatCruiseData datarow = (SocatCruiseData) data.get(index);
                                try {
                                    Double dvalue = (Double) datarow.getClass().getDeclaredField(name).get(datarow);
                                    dvar.set(index, dvalue);
                                } catch (IllegalArgumentException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (NoSuchFieldException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (SecurityException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                            try {
                                ncfile.write(var, dvar);
                            } catch (InvalidRangeException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    } else if ( type.equals(Integer.class) || type.equals(Integer.TYPE) ) {
                        var = ncfile.findVariable(name);
                        if ( var != null ) {
                            ArrayInt.D1 dvar = new ArrayInt.D1(data.size());
                            for (int index = 0; index < data.size(); index++) {
                                SocatCruiseData datarow = (SocatCruiseData) data.get(index);
                                try {
                                    Integer dvalue = (Integer) datarow.getClass().getDeclaredField(name).get(datarow);
                                    dvar.set(index, dvalue);
                                } catch (IllegalArgumentException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (NoSuchFieldException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (SecurityException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                            try {
                                ncfile.write(var, dvar);
                            } catch (InvalidRangeException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    } else if ( type.equals(String.class) ) {
                        // Skip for now.
                    }
                    
                }
            }
            var = ncfile.findVariable("time");
            if ( var != null ) {
                ArrayDouble.D1 values = new ArrayDouble.D1(data.size());
                for (int index = 0; index < data.size(); index++) {
                    SocatCruiseData datarow = (SocatCruiseData) data.get(index);
                    Integer year = datarow.getYear();
                    Integer month = datarow.getMonth();
                    Integer day = datarow.getDay();
                    Integer hour = datarow.getHour();
                    Integer minute = datarow.getMinute();
                    Double second = datarow.getSecond();
                    Integer sec = (int)Math.round(second);
                    CalendarDate date = CalendarDate.of(Calendar.proleptic_gregorian, year, month, day, hour, minute, sec);
                    CalendarDate base = CalendarDate.of(Calendar.proleptic_gregorian, 1970, 1, 1, 0, 0, 0);
                    long lvalue = date.getDifferenceInMsecs(base);
                    double value = (double) lvalue;
                    values.set(index, value);
                }
                try {
                    ncfile.write(var, values);
                } catch (InvalidRangeException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
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
