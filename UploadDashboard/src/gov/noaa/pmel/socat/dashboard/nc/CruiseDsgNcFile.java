package gov.noaa.pmel.socat.dashboard.nc;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import ucar.ma2.ArrayChar;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayInt;
import ucar.ma2.DataType;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.NetcdfFileWriter.Version;
import ucar.nc2.Variable;
import ucar.nc2.time.Calendar;
import ucar.nc2.time.CalendarDate;


public class CruiseDsgNcFile {

    SocatMetadata metadata;
    List<SocatCruiseData> data;
    NetcdfFileWriter ncfile;
    String version = "CruiseDsgNcFile 0.2";

    public CruiseDsgNcFile(SocatMetadata metadata, List<SocatCruiseData> data) {
        this.metadata = metadata;
        this.data = data;
    }
    public void create(String sdgFilename) throws Exception {

        ncfile = NetcdfFileWriter.createNew(Version.netcdf3, sdgFilename);

        // According to the CF standard if a file only has one trajectory, the the trajectory dimension is not necessary.
        // However, who knows what would break downstream from this process without it...

        Dimension traj = ncfile.addDimension(null, "trajectory", 1);

        // There will be 9 trajectory variables of type character from the metadata.
        // Which is the longest?
        int maxchar = metadata.getMaxStringLength();
        Dimension stringlen = ncfile.addDimension(null, "string_length", maxchar);
        List<Dimension> trajdimsChar = new ArrayList<Dimension>();
        trajdimsChar.add(traj);
        trajdimsChar.add(stringlen);

        List<Dimension> trajdims = new ArrayList<Dimension>();
        trajdims.add(traj);

        Dimension d = ncfile.addDimension(null, "obs", data.size());
        List<Dimension> dims = new ArrayList<Dimension>();
        dims.add(d);

        // Make character netCDF variables of all the string metadata.
        Field[] metafields = metadata.getClass().getDeclaredFields();
        for (int i = 0; i < metafields.length; i++) {
            Field f = metafields[i];
            if ( f.getType().equals(String.class) && !Modifier.isStatic(f.getModifiers()) ) {

                Variable var = ncfile.addVariable(null, f.getName(), DataType.CHAR, trajdimsChar);
                if ( f.getName().equals("expocode")) {
                    ncfile.addVariableAttribute(var, new Attribute("cf_role", "trajectory_id"));
                }

            }
        }
        Variable var = ncfile.addVariable(null, "rowSize", DataType.DOUBLE, trajdims);
        ncfile.addVariableAttribute(var, new Attribute("sample_dimension", "obs"));
        ncfile.addVariableAttribute(var, new Attribute("long_name", "Number of Observations"));

        if ( data.size() > 0 ) {
            Class<?> d0 = data.get(0).getClass();
            Field[] fields = d0.getDeclaredFields();

            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                String name = f.getName();
                Class<?> type = f.getType();
                if ( !Modifier.isStatic(f.getModifiers()) ) {

                    var = null;
                    Number missVal = SocatCruiseData.FP_MISSING_VALUE;
                    if ( type.equals(Long.class) || type.equals(Long.TYPE) ) {
                        var = ncfile.addVariable(null, Constants.SHORT_NAME.get(name), DataType.DOUBLE, dims);
                        missVal = Double.valueOf(SocatCruiseData.INT_MISSING_VALUE);
                    } else if ( type.equals(Double.class) || type.equals(Double.TYPE) ) {
                        var = ncfile.addVariable(null, Constants.SHORT_NAME.get(name), DataType.DOUBLE, dims);
                        missVal = SocatCruiseData.FP_MISSING_VALUE;
                    } else if ( type.equals(Integer.class) || type.equals(Integer.TYPE) ) {
                        var = ncfile.addVariable(null, Constants.SHORT_NAME.get(name), DataType.INT, dims);
                        missVal = SocatCruiseData.INT_MISSING_VALUE;
                    } else if ( type.equals(String.class) ) {
                        // Skip for now.
                    }
                    if ( var != null ) {
                        String units = Constants.UNITS.get(name);
                        if ( units != null ) {
                            ncfile.addVariableAttribute(var, new Attribute("units", units));
                        }
                        String description = Constants.LONG_NAME.get(name);
                        if ( description != null ) {
                            ncfile.addVariableAttribute(var, new Attribute("long_name", description));
                        }
                        ncfile.addVariableAttribute(var, new Attribute("missing_value", missVal));
                        ncfile.addVariableAttribute(var, new Attribute("_FillValue", missVal));
                    }
                }
            }

            var = ncfile.addVariable(null, "time", DataType.DOUBLE, dims);
            ncfile.addVariableAttribute(var, new Attribute("units", "seconds since 1970-01-01 00:00"));

            ncfile.addGroupAttribute(null, new Attribute("History", version));
            ncfile.addGroupAttribute(null, new Attribute("featureType", "Trajectory"));
            ncfile.addGroupAttribute(null, new Attribute("Convenstions", "CF-1.6"));
            if ( ! metadata.getEastmostLongitude().isNaN() )
            	ncfile.addGroupAttribute(null, new Attribute("EastMostLongitude", metadata.getEastmostLongitude()));
            if ( ! metadata.getWestmostLongitude().isNaN() )
            	ncfile.addGroupAttribute(null, new Attribute("WestMostLongitude", metadata.getWestmostLongitude()));
            if ( ! metadata.getNorthmostLatitude().isNaN() )
            	ncfile.addGroupAttribute(null, new Attribute("NorthMostLatitude", metadata.getNorthmostLatitude()));
            if ( ! metadata.getSouthmostLatitude().isNaN() )
            	ncfile.addGroupAttribute(null, new Attribute("SouthMostLatitude", metadata.getSouthmostLatitude()));
            if ( ! metadata.getBeginTime().equals(SocatMetadata.DATE_MISSING_VALUE) ) {
            	CalendarDate start = CalendarDate.of(metadata.getBeginTime());
            	ncfile.addGroupAttribute(null, new Attribute("time_coverage_start", start.toString()));
            }
            if ( ! metadata.getEndTime().equals(SocatMetadata.DATE_MISSING_VALUE) ) {
            	CalendarDate end = CalendarDate.of(metadata.getEndTime());
            	ncfile.addGroupAttribute(null, new Attribute("time_converage_end", end.toString()));
            }

            ncfile.create();
            // The header has been created.  Now let's fill it up.

            for (int i = 0; i < metafields.length; i++) {
                Field f = metafields[i];
                if ( f.getType().equals(String.class) && !Modifier.isStatic(f.getModifiers()) ) {

                    var = null;
                    String s = (String) f.get(metadata);
                    var = ncfile.findVariable(f.getName());
                    if ( var != null ) {
                        ArrayChar.D2 values = new ArrayChar.D2(1, maxchar);
                        values.setString(0, s);
                        ncfile.write(var, values);
                    }

                }
            }
            ArrayDouble.D1 obscount = new ArrayDouble.D1(1);
            obscount.set(0, (double)data.size());
            var = ncfile.findVariable("rowSize");
            if ( var != null ) {
                ncfile.write(var, obscount);

            }
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                String name = f.getName();
                Class<?> type = f.getType();
                if ( !Modifier.isStatic(f.getModifiers()) ) {
                    var = null;
                    if ( type.equals(Long.class) || type.equals(Long.TYPE) ) {
                        var = ncfile.findVariable(Constants.SHORT_NAME.get(name));
                        if ( var != null ) {
                            ArrayDouble.D1 dvar = new ArrayDouble.D1(data.size());
                            for (int index = 0; index < data.size(); index++) {
                                SocatCruiseData datarow = (SocatCruiseData) data.get(index);
                                Long value = (Long) datarow.getClass().getDeclaredField(name).get(datarow);
                                Double dvalue = new Double(value);
                                dvar.set(index, dvalue);

                            }
                            ncfile.write(var, dvar);

                        }
                    } else if ( type.equals(Double.class) || type.equals(Double.TYPE) ) {
                        var = ncfile.findVariable(Constants.SHORT_NAME.get(name));
                        if ( var != null ) {
                            ArrayDouble.D1 dvar = new ArrayDouble.D1(data.size());
                            for (int index = 0; index < data.size(); index++) {
                                SocatCruiseData datarow = (SocatCruiseData) data.get(index);
                                Double dvalue = (Double) datarow.getClass().getDeclaredField(name).get(datarow);
                                dvar.set(index, dvalue);

                            }
                            ncfile.write(var, dvar);

                        }
                    } else if ( type.equals(Integer.class) || type.equals(Integer.TYPE) ) {
                        var = ncfile.findVariable(Constants.SHORT_NAME.get(name));
                        if ( var != null ) {
                            ArrayInt.D1 dvar = new ArrayInt.D1(data.size());
                            for (int index = 0; index < data.size(); index++) {
                                SocatCruiseData datarow = (SocatCruiseData) data.get(index);

                                Integer dvalue = (Integer) datarow.getClass().getDeclaredField(name).get(datarow);
                                dvar.set(index, dvalue);

                            }
                            ncfile.write(var, dvar);
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
                    Integer sec;
                    if ( second.isNaN() )
                    	sec = 0;
                    else
                    	sec = (int)Math.round(second);
                    CalendarDate date = CalendarDate.of(Calendar.proleptic_gregorian, year, month, day, hour, minute, sec);
                    CalendarDate base = CalendarDate.of(Calendar.proleptic_gregorian, 1970, 1, 1, 0, 0, 0);
                    long lvalue = date.getDifferenceInMsecs(base);
                    double value = (double) lvalue / 1000.0;
                    values.set(index, value);
                }
                ncfile.write(var, values);

            }
        }
        ncfile.close();
    }
}
