package gov.noaa.pmel.dashboard.dsg;

import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.DoubleDashDataType;
import gov.noaa.pmel.dashboard.datatype.IntDashDataType;
import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.datatype.SocatTypes;
import gov.noaa.pmel.dashboard.datatype.StringDashDataType;
import gov.noaa.pmel.dashboard.qc.DataLocation;
import gov.noaa.pmel.dashboard.qc.DataQCEvent;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import ucar.ma2.ArrayChar;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayInt;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.NetcdfFileWriter.Version;
import ucar.nc2.Variable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeSet;


public class DsgNcFile extends File {

    private static final long serialVersionUID = -4101244523736694568L;

    private static final String DSG_VERSION = "DsgNcFile 2.0";
    private static final String TIME_ORIGIN_ATTRIBUTE = "01-JAN-1970 00:00:00";

    private DsgMetadata metadata;
    private StdDataArray stddata;

    /**
     * See {@link java.io.File#File(java.lang.String)} The internal metadata and data array references are set null.
     */
    public DsgNcFile(String filename) {
        super(filename);
        metadata = null;
        stddata = null;
    }

    /**
     * See {@link java.io.File#File(java.io.File, java.lang.String)} The internal metadata and data array references are
     * set null.
     */
    public DsgNcFile(File parent, String child) {
        super(parent, child);
        metadata = null;
        stddata = null;
    }

    /**
     * Adds the missing_value, _FillValue, long_name, standard_name, ioos_category, and units attributes to the given
     * variables in the given NetCDF file.
     *
     * @param ncfile
     *         NetCDF file being written containing the variable
     * @param var
     *         the variables to add attributes to
     * @param missVal
     *         if not null, the value for the missing_value and _FillValue attributes
     * @param longName
     *         if not null and not {@link DashboardUtils#STRING_MISSING_VALUE}, the value for the long_name attribute
     * @param standardName
     *         if not null and not {@link DashboardUtils#STRING_MISSING_VALUE}, the value for the standard_name
     *         attribute
     * @param ioosCategory
     *         if not null and not {@link DashboardUtils#STRING_MISSING_VALUE}, the value for the ioos_category
     *         attribute
     * @param units
     *         if not null and not {@link DashboardUtils#STRING_MISSING_VALUE}, the value for the units attribute
     */
    private void addAttributes(NetcdfFileWriter ncfile, Variable var, Number missVal,
            String longName, String standardName, String ioosCategory, String units) {
        if ( missVal != null ) {
            ncfile.addVariableAttribute(var, new Attribute("missing_value", missVal));
            ncfile.addVariableAttribute(var, new Attribute("_FillValue", missVal));
        }
        if ( (longName != null) && !DashboardUtils.STRING_MISSING_VALUE.equals(longName) ) {
            ncfile.addVariableAttribute(var, new Attribute("long_name", longName));
        }
        if ( (standardName != null) && !DashboardUtils.STRING_MISSING_VALUE.equals(standardName) ) {
            ncfile.addVariableAttribute(var, new Attribute("standard_name", standardName));
        }
        if ( (ioosCategory != null) && !DashboardUtils.STRING_MISSING_VALUE.equals(ioosCategory) ) {
            ncfile.addVariableAttribute(var, new Attribute("ioos_category", ioosCategory));
        }
        if ( (units != null) && !DashboardUtils.STRING_MISSING_VALUE.equals(units) ) {
            ncfile.addVariableAttribute(var, new Attribute("units", units));
        }
    }

    /**
     * Creates this NetCDF DSG file with the given metadata and standardized user provided data.  The internal metadata
     * reference is updated to the given DsgMetadata object and the internal data array reference is updated to a new
     * standardized data array object created from the appropriate user provided data. Every data sample must have a
     * valid longitude, latitude, sample depth, and complete date and time specification, to at least the minute.  If
     * the seconds of the time is not provided, zero seconds will be used.
     *
     * @param metadata
     *         metadata for the dataset
     * @param userStdData
     *         standardized user-provided data
     * @param dataFileTypes
     *         known data types for data files
     *
     * @throws IllegalArgumentException
     *         if any argument is null,
     *         if any of the data types in userStdData is {@link DashboardServerUtils#UNKNOWN},
     *         if any sample longitude, latitude, sample depth is missing, if any sample time cannot be computed
     * @throws IOException
     *         if creating the NetCDF file throws one
     * @throws InvalidRangeException
     *         if creating the NetCDF file throws one
     */
    public void createFromUserData(DsgMetadata metadata, StdUserDataArray userStdData, KnownDataTypes dataFileTypes)
            throws IllegalArgumentException, IOException, InvalidRangeException {
        if ( metadata == null )
            throw new IllegalArgumentException("no metadata given");
        this.metadata = metadata;
        if ( userStdData == null )
            throw new IllegalArgumentException("no data given");

        // The following verifies lon and lat, computes and adds time, and
        // if not already present, year, month, day, hour, minute, and second.
        stddata = new StdDataArray(userStdData, dataFileTypes);

        createFromFileData(metadata, stddata, dataFileTypes);
    }

    /**
     * Creates this NetCDF DSG file with the given metadata and standardized data for data files.
     * The internal metadata and stddata references are updated to the given DsgMetadata and StdDataArray object.
     *
     * @param metadata
     *         metadata for the dataset; the DSG file will be created with exactly the metadata types it contains
     * @param filedata
     *         standardized data appropriate for data files
     * @param dataFileTypes
     *         all known data types for file data variables; the DSG file will be created with all these data types.
     *         Any types not given in fileData will filled with appropriate missing values.
     *
     * @throws IllegalArgumentException
     *         if either argument is null or invalid
     * @throws IOException
     *         if creating the NetCDF file throws one
     * @throws InvalidRangeException
     *         if creating the NetCDF file throws one
     */
    public void createFromFileData(DsgMetadata metadata, StdDataArray filedata, KnownDataTypes dataFileTypes)
            throws IllegalArgumentException, IOException, InvalidRangeException {
        if ( metadata == null )
            throw new IllegalArgumentException("no metadata given");
        this.metadata = metadata;
        if ( filedata == null )
            throw new IllegalArgumentException("no data given");
        this.stddata = filedata;
        if ( dataFileTypes == null )
            throw new IllegalArgumentException("dataFileTypes is null");
        TreeSet<DashDataType<?>> knownDataTypes = dataFileTypes.getKnownTypesSet();
        if ( knownDataTypes.isEmpty() )
            throw new IllegalArgumentException("no data file types given");

        NetcdfFileWriter ncfile = NetcdfFileWriter.createNew(Version.netcdf3, getPath());
        try {
            // According to the CF standard if a file only has one trajectory,
            // then the trajectory dimension is not necessary.
            // However, who knows what would break downstream from this process without it...
            Dimension traj = ncfile.addDimension(null, "trajectory", 1);

            // There will be a number of trajectory variables of type character from the metadata.
            // Which is the longest?
            int maxMetaChar = metadata.getMaxStringLength();
            Dimension metaStringLen = ncfile.addDimension(null, "metadata_string_length", maxMetaChar);
            List<Dimension> metaStringDims = new ArrayList<Dimension>();
            metaStringDims.add(traj);
            metaStringDims.add(metaStringLen);

            List<Dimension> trajDims = new ArrayList<Dimension>();
            trajDims.add(traj);

            int numSamples = stddata.getNumSamples();
            Dimension obslen = ncfile.addDimension(null, "obs", numSamples);
            List<Dimension> dataDims = new ArrayList<Dimension>();
            dataDims.add(obslen);

            int maxDataChar = stddata.getMaxStringLength();
            Dimension dataStringLen = ncfile.addDimension(null, "data_string_length", maxDataChar);
            List<Dimension> dataStringDims = new ArrayList<Dimension>();
            dataStringDims.add(obslen);
            dataStringDims.add(dataStringLen);

            ncfile.addGroupAttribute(null, new Attribute("featureType", "Trajectory"));
            ncfile.addGroupAttribute(null, new Attribute("Conventions", "CF-1.6"));
            ncfile.addGroupAttribute(null, new Attribute("history", DSG_VERSION));

            // Add the "num_obs" variable which will be assigned using the number of data points
            Variable var = ncfile.addVariable(null, "num_obs", DataType.INT, trajDims);
            ncfile.addVariableAttribute(var, new Attribute("sample_dimension", "obs"));
            ncfile.addVariableAttribute(var, new Attribute("long_name", "Number of Observations"));
            ncfile.addVariableAttribute(var, new Attribute("missing_value", DashboardUtils.INT_MISSING_VALUE));
            ncfile.addVariableAttribute(var, new Attribute("_FillValue", DashboardUtils.INT_MISSING_VALUE));

            String varName;
            // Make netCDF variables of all the metadata and data variables
            for (DashDataType<?> dtype : metadata.valuesMap.keySet()) {
                varName = dtype.getVarName();
                if ( dtype instanceof StringDashDataType ) {
                    // Metadata Strings
                    var = ncfile.addVariable(null, varName, DataType.CHAR, metaStringDims);
                    // No missing_value, _FillValue, or units for strings
                    addAttributes(ncfile, var, null, dtype.getDescription(),
                            dtype.getStandardName(), dtype.getCategoryName(), DashboardUtils.STRING_MISSING_VALUE);
                    if ( DashboardServerUtils.DATASET_ID.typeNameEquals(dtype) ) {
                        ncfile.addVariableAttribute(var, new Attribute("cf_role", "trajectory_id"));
                    }
                }
                else if ( dtype instanceof IntDashDataType ) {
                    // Metadata Integers
                    var = ncfile.addVariable(null, varName, DataType.INT, trajDims);
                    addAttributes(ncfile, var, DashboardUtils.INT_MISSING_VALUE, dtype.getDescription(),
                            dtype.getStandardName(), dtype.getCategoryName(), dtype.getFileStdUnit());
                }
                else if ( dtype instanceof DoubleDashDataType ) {
                    // Metadata Doubles
                    var = ncfile.addVariable(null, varName, DataType.DOUBLE, trajDims);
                    addAttributes(ncfile, var, DashboardUtils.FP_MISSING_VALUE, dtype.getDescription(),
                            dtype.getStandardName(), dtype.getCategoryName(), dtype.getFileStdUnit());
                    if ( DashboardServerUtils.TIME_UNITS.get(0).equals(dtype.getUnits().get(0)) ) {
                        // Additional attribute giving the time origin (although also mentioned in the units)
                        ncfile.addVariableAttribute(var, new Attribute("time_origin", TIME_ORIGIN_ATTRIBUTE));
                    }
                }
                else {
                    throw new IllegalArgumentException("unknown metadata type: " + dtype.toString());
                }
            }

            for (DashDataType<?> dtype : knownDataTypes) {
                varName = dtype.getVarName();
                if ( dtype instanceof StringDashDataType ) {
                    // Data Strings
                    var = ncfile.addVariable(null, varName, DataType.CHAR, dataStringDims);
                    // No missing_value, _FillValue, or units for characters
                    addAttributes(ncfile, var, null, dtype.getDescription(),
                            dtype.getStandardName(), dtype.getCategoryName(), DashboardUtils.STRING_MISSING_VALUE);
                }
                else if ( dtype instanceof IntDashDataType ) {
                    // Data Integers
                    var = ncfile.addVariable(null, varName, DataType.INT, dataDims);
                    addAttributes(ncfile, var, DashboardUtils.INT_MISSING_VALUE, dtype.getDescription(),
                            dtype.getStandardName(), dtype.getCategoryName(), dtype.getFileStdUnit());
                }
                else if ( dtype instanceof DoubleDashDataType ) {
                    // Data Doubles
                    var = ncfile.addVariable(null, varName, DataType.DOUBLE, dataDims);
                    addAttributes(ncfile, var, DashboardUtils.FP_MISSING_VALUE, dtype.getDescription(),
                            dtype.getStandardName(), dtype.getCategoryName(), dtype.getFileStdUnit());
                    if ( DashboardServerUtils.TIME.typeNameEquals(dtype) ) {
                        // Additional attribute giving the time origin (although also mentioned in the units)
                        ncfile.addVariableAttribute(var, new Attribute("time_origin", TIME_ORIGIN_ATTRIBUTE));
                    }
                    if ( dtype.getStandardName().endsWith("depth") ) {
                        ncfile.addVariableAttribute(var, new Attribute("positive", "down"));
                    }
                }
                else {
                    throw new IllegalArgumentException("unknown data type: " + dtype.toString());
                }
            }

            ncfile.create();

            // The header has been created.  Now let's fill it up.
            var = ncfile.findVariable("num_obs");
            if ( var == null )
                throw new RuntimeException("Unexpected failure to find ncfile variable num_obs");
            ArrayInt.D1 obscount = new ArrayInt.D1(1);
            obscount.set(0, numSamples);
            ncfile.write(var, obscount);

            for (Entry<DashDataType<?>,Object> entry : metadata.getValuesMap().entrySet()) {
                DashDataType<?> dtype = entry.getKey();
                varName = dtype.getVarName();
                var = ncfile.findVariable(varName);
                if ( var == null )
                    throw new RuntimeException("Unexpected failure to find ncfile variable " + varName);

                if ( dtype instanceof StringDashDataType ) {
                    // Metadata Strings
                    String dvalue = (String) entry.getValue();
                    if ( dvalue == null )
                        dvalue = DashboardUtils.STRING_MISSING_VALUE;
                    ArrayChar.D2 mvar = new ArrayChar.D2(1, maxMetaChar);
                    mvar.setString(0, dvalue.trim());
                    ncfile.write(var, mvar);
                }
                else if ( dtype instanceof IntDashDataType ) {
                    // Metadata Integers
                    Integer dvalue = (Integer) entry.getValue();
                    if ( dvalue == null )
                        dvalue = DashboardUtils.INT_MISSING_VALUE;
                    ArrayInt.D1 mvar = new ArrayInt.D1(1);
                    mvar.set(0, dvalue);
                    ncfile.write(var, mvar);
                }
                else if ( dtype instanceof DoubleDashDataType ) {
                    // Metadata Doubles
                    Double dvalue = (Double) entry.getValue();
                    if ( (dvalue == null) || dvalue.isNaN() || dvalue.isInfinite() )
                        dvalue = DashboardUtils.FP_MISSING_VALUE;
                    ArrayDouble.D1 mvar = new ArrayDouble.D1(1);
                    mvar.set(0, dvalue);
                    ncfile.write(var, mvar);
                }
                else {
                    // Should have been caught above
                    throw new RuntimeException("Unexpected unknown metadata type: " + dtype.toString());
                }
            }

            List<DashDataType<?>> dataTypes = stddata.getDataTypes();
            // for (int k = 0; k < stddata.getNumDataCols(); k++) {
            for (DashDataType<?> dtype : knownDataTypes) {
                varName = dtype.getVarName();
                var = ncfile.findVariable(varName);
                if ( var == null )
                    throw new RuntimeException("Unexpected failure to find ncfile variable " + varName);
                int k = dataTypes.indexOf(dtype);

                if ( dtype instanceof StringDashDataType ) {
                    // Data Stings
                    ArrayChar.D2 dvar = new ArrayChar.D2(numSamples, maxDataChar);
                    if ( k >= 0 ) {
                        for (int j = 0; j < numSamples; j++) {
                            String dvalue = (String) stddata.getStdVal(j, k);
                            if ( dvalue == null )
                                dvalue = DashboardUtils.STRING_MISSING_VALUE;
                            dvar.setString(j, dvalue.trim());
                        }
                    }
                    else {
                        for (int j = 0; j < numSamples; j++) {
                            dvar.setString(j, DashboardUtils.STRING_MISSING_VALUE);
                        }
                    }
                    ncfile.write(var, dvar);
                }
                else if ( dtype instanceof IntDashDataType ) {
                    // Data Integers
                    ArrayInt.D1 dvar = new ArrayInt.D1(numSamples);
                    if ( k >= 0 ) {
                        for (int j = 0; j < numSamples; j++) {
                            Integer dvalue = (Integer) stddata.getStdVal(j, k);
                            if ( dvalue == null )
                                dvalue = DashboardUtils.INT_MISSING_VALUE;
                            dvar.set(j, dvalue);
                        }
                    }
                    else {
                        for (int j = 0; j < numSamples; j++) {
                            dvar.set(j, DashboardUtils.INT_MISSING_VALUE);
                        }
                    }
                    ncfile.write(var, dvar);
                }
                else if ( dtype instanceof DoubleDashDataType ) {
                    // Data Doubles
                    ArrayDouble.D1 dvar = new ArrayDouble.D1(numSamples);
                    if ( k >= 0 ) {
                        for (int j = 0; j < numSamples; j++) {
                            Double dvalue = (Double) stddata.getStdVal(j, k);
                            if ( (dvalue == null) || dvalue.isNaN() || dvalue.isInfinite() )
                                dvalue = DashboardUtils.FP_MISSING_VALUE;
                            dvar.set(j, dvalue);
                        }
                    }
                    else {
                        for (int j = 0; j < numSamples; j++) {
                            dvar.set(j, DashboardUtils.FP_MISSING_VALUE);
                        }
                    }
                    ncfile.write(var, dvar);
                }
                else {
                    // Should have been caught above
                    throw new RuntimeException("Unexpected unknown data type: " + dtype.toString());
                }
            }

        } finally {
            ncfile.close();
        }
    }

    /**
     * Creates and assigns the internal metadata reference from the contents of this netCDF DSG file.
     *
     * @param metadataTypes
     *         metadata file types to read
     *
     * @return variable names of the metadata fields not assigned from this netCDF file (will have its default/missing
     *         value)
     *
     * @throws IllegalArgumentException
     *         if there are no metadata types given, or if an invalid type for metadata is encountered
     * @throws IOException
     *         if there are problems opening or reading from the netCDF file
     */
    public ArrayList<String> readMetadata(KnownDataTypes metadataTypes) throws IllegalArgumentException, IOException {
        if ( (metadataTypes == null) || metadataTypes.isEmpty() )
            throw new IllegalArgumentException("no metadata file types given");
        ArrayList<String> namesNotFound = new ArrayList<String>();
        NetcdfFile ncfile = NetcdfFile.open(getPath());
        try {
            // Create the metadata with default (missing) values
            metadata = new DsgMetadata(metadataTypes);

            for (DashDataType<?> dtype : metadataTypes.getKnownTypesSet()) {
                String varName = dtype.getVarName();
                Variable var = ncfile.findVariable(varName);
                if ( var == null ) {
                    namesNotFound.add(varName);
                    continue;
                }
                if ( var.getShape(0) != 1 )
                    throw new IOException("more than one value for a metadata type");
                if ( dtype instanceof StringDashDataType ) {
                    ArrayChar.D2 mvar = (ArrayChar.D2) var.read();
                    String strval = mvar.getString(0).trim();
                    if ( !DashboardUtils.STRING_MISSING_VALUE.equals(strval) )
                        metadata.setValue(dtype, strval);
                }
                else if ( dtype instanceof IntDashDataType ) {
                    ArrayInt.D1 mvar = (ArrayInt.D1) var.read();
                    Integer intval = mvar.getInt(0);
                    if ( !DashboardUtils.INT_MISSING_VALUE.equals(intval) )
                        metadata.setValue(dtype, intval);
                }
                else if ( dtype instanceof DoubleDashDataType ) {
                    ArrayDouble.D1 mvar = (ArrayDouble.D1) var.read();
                    Double dblval = mvar.getDouble(0);
                    if ( !DashboardUtils.closeTo(DashboardUtils.FP_MISSING_VALUE, dblval,
                            0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
                        metadata.setValue(dtype, dblval);
                }
                else {
                    throw new IllegalArgumentException("invalid metadata file type " + dtype.getVarName());
                }
            }
        } finally {
            ncfile.close();
        }
        return namesNotFound;
    }

    /**
     * Creates and assigns the internal standard data array reference from the contents of this netCDF DSG file.
     *
     * @param dataTypes
     *         data files types to read
     *
     * @return variable names of the data types not assigned from this netCDF file (will have its default/missing value)
     *
     * @throws IllegalArgumentException
     *         if no known data types are given, or if an invalid type for data files is encountered
     * @throws IOException
     *         if the netCDF file is invalid: it must have a 'time' variable and all data variables must have the same
     *         number of values as the 'time' variable, or if there are problems opening or reading from the netCDF
     *         file
     */
    public ArrayList<String> readData(KnownDataTypes dataTypes) throws IllegalArgumentException, IOException {
        if ( (dataTypes == null) || dataTypes.isEmpty() )
            throw new IllegalArgumentException("no data file types given");
        int numColumns;
        DashDataType<?>[] dataTypesArray;
        {
            TreeSet<DashDataType<?>> dataTypesSet = dataTypes.getKnownTypesSet();
            numColumns = dataTypesSet.size();
            dataTypesArray = new DashDataType<?>[numColumns];
            int idx = -1;
            for (DashDataType<?> dtype : dataTypesSet) {
                idx++;
                dataTypesArray[idx] = dtype;
            }
        }

        ArrayList<String> namesNotFound = new ArrayList<String>();
        NetcdfFile ncfile = NetcdfFile.open(getPath());
        try {
            // Get the number of samples from the length of the time 1D array
            String varName = DashboardServerUtils.TIME.getVarName();
            Variable var = ncfile.findVariable(varName);
            if ( var == null )
                throw new IOException("unable to find variable 'time' in " + getName());
            int numSamples = var.getShape(0);

            // Create the array of data values
            Object[][] dataArray = new Object[numSamples][numColumns];

            for (int k = 0; k < numColumns; k++) {
                DashDataType<?> dtype = dataTypesArray[k];
                varName = dtype.getVarName();
                var = ncfile.findVariable(varName);
                if ( var == null ) {
                    namesNotFound.add(varName);
                    for (int j = 0; j < numSamples; j++) {
                        dataArray[j][k] = null;
                    }
                    continue;
                }

                if ( var.getShape(0) != numSamples )
                    throw new IOException("number of values for '" + varName +
                            "' (" + Integer.toString(var.getShape(0)) + ") does not match " +
                            "the number of values for 'time' (" + Integer.toString(numSamples) + ")");

                if ( dtype instanceof StringDashDataType ) {
                    ArrayChar.D2 dvar = (ArrayChar.D2) var.read();
                    for (int j = 0; j < numSamples; j++) {
                        String strval = dvar.getString(j).trim();
                        if ( DashboardUtils.STRING_MISSING_VALUE.equals(strval) )
                            dataArray[j][k] = null;
                        else
                            dataArray[j][k] = strval;
                    }
                }
                else if ( dtype instanceof IntDashDataType ) {
                    ArrayInt.D1 dvar = (ArrayInt.D1) var.read();
                    for (int j = 0; j < numSamples; j++) {
                        Integer intval = dvar.get(j);
                        if ( DashboardUtils.INT_MISSING_VALUE.equals(intval) )
                            dataArray[j][k] = null;
                        else
                            dataArray[j][k] = intval;
                    }
                }
                else if ( dtype instanceof DoubleDashDataType ) {
                    ArrayDouble.D1 dvar = (ArrayDouble.D1) var.read();
                    for (int j = 0; j < numSamples; j++) {
                        Double dblval = dvar.get(j);
                        if ( DashboardUtils.closeTo(DashboardUtils.FP_MISSING_VALUE, dblval,
                                0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
                            dataArray[j][k] = null;
                        else
                            dataArray[j][k] = dblval;
                    }
                }
                else {
                    throw new IllegalArgumentException("invalid data file type " + dtype.toString());
                }
            }
            stddata = new StdDataArray(dataTypesArray, dataArray);
        } finally {
            ncfile.close();
        }
        return namesNotFound;
    }

    /**
     * @return the internal metadata reference; may be null
     */
    public DsgMetadata getMetadata() {
        return metadata;
    }

    /**
     * @return the internal standard data array reference; may be null
     */
    public StdDataArray getStdDataArray() {
        return stddata;
    }

    /**
     * Reads and returns the array of data values for the specified variable contained in this DSG file.  The variable
     * must be saved in the DSG file as Strings.  For some variables, this DSG file must have been processed by Ferret
     * for the data values to be meaningful.  Missing values are left as in the DSG file.
     *
     * @param varName
     *         name of the variable to read
     *
     * @return array of values for the specified variable
     *
     * @throws IOException
     *         if there is a problem opening or reading from this DSG file
     * @throws IllegalArgumentException
     *         if the variable name is invalid, or if the variable is not a String array variable
     */
    public String[] readStringVarDataValues(String varName) throws IOException, IllegalArgumentException {
        String[] dataVals;
        NetcdfFile ncfile = NetcdfFile.open(getPath());
        try {
            Variable var = ncfile.findVariable(varName);
            if ( var == null )
                throw new IllegalArgumentException("Unable to find variable '" + varName + "' in " + getName());
            ArrayChar.D2 cvar = (ArrayChar.D2) var.read();
            int numVals = var.getShape(0);
            dataVals = new String[numVals];
            for (int k = 0; k < numVals; k++) {
                dataVals[k] = cvar.getString(k).trim();
            }
        } finally {
            ncfile.close();
        }
        return dataVals;
    }

    /**
     * Write the given array of strings as the values for the given string data variable.
     *
     * @param varName
     *         string data variable name
     * @param dataVals
     *         string values to assign
     *
     * @throws IOException
     *         if read from or writing to the DSG file throws one
     * @throws IllegalArgumentException
     *         if the variable name is invalid,
     *         if the number of replacement strings is incorrect for this DSG file, or
     *         if the length of a replacement string is too long for this DSG file,
     */
    public void writeStringVarDataValues(String varName, String[] dataVals)
            throws IOException, IllegalArgumentException {
        int maxlen = 0;
        for (String val : dataVals) {
            if ( maxlen < val.length() )
                maxlen = val.length();
        }
        NetcdfFileWriter ncfile = NetcdfFileWriter.openExisting(getPath());
        try {
            Variable var = ncfile.findVariable(varName);
            if ( var == null )
                throw new IllegalArgumentException("Unable to find variable '" + varName + "' in " + getName());
            int numVals = var.getShape(0);
            if ( numVals != dataVals.length )
                throw new IllegalArgumentException("invalid number of replacement strings for this DSG file");
            if ( maxlen > var.getShape(1) )
                throw new IOException("a replacement string too long for this DSG file");
            // Data Stings
            ArrayChar.D2 dvar = new ArrayChar.D2(numVals, var.getShape(1));
            for (int j = 0; j < numVals; j++) {
                dvar.setString(j, dataVals[j]);
            }
            try {
                ncfile.write(var, dvar);
            } catch ( InvalidRangeException ex ) {
                // Should not happen given the checks above
                throw new IllegalArgumentException(ex);
            }
        } finally {
            ncfile.close();
        }
    }

    /**
     * Reads and returns the array of data values for the specified variable contained in this DSG file.
     * The variable must be saved in the DSG file as integers.  For some variables, this DSG file must have been
     * processed by Ferret for the data values to be meaningful.  Missing values are left as in the DSG file.
     *
     * @param varName
     *         name of the variable to read
     *
     * @return array of values for the specified variable
     *
     * @throws IOException
     *         if there is a problem opening or reading from this DSG file
     * @throws IllegalArgumentException
     *         if the variable name is invalid
     */
    public int[] readIntVarDataValues(String varName) throws IOException, IllegalArgumentException {
        int[] dataVals;
        NetcdfFile ncfile = NetcdfFile.open(getPath());
        try {
            Variable var = ncfile.findVariable(varName);
            if ( var == null )
                throw new IllegalArgumentException("Unable to find variable '" + varName + "' in " + getName());
            ArrayInt.D1 dvar = (ArrayInt.D1) var.read();
            int numVals = var.getShape(0);
            dataVals = new int[numVals];
            for (int k = 0; k < numVals; k++) {
                dataVals[k] = dvar.get(k);
            }
        } finally {
            ncfile.close();
        }
        return dataVals;
    }

    /**
     * Reads and returns the array of data values for the specified variable contained in this DSG file.
     * The variable must be saved in the DSG file as doubles. NaN and infinite values are changed to
     * {@link DashboardUtils#FP_MISSING_VALUE}, but otherwise missing values are left as in the DSG file.
     * For some variables, this DSG file must have been processed by Ferret for the data values to be meaningful.
     *
     * @param varName
     *         name of the variable to read
     *
     * @return array of values for the specified variable
     *
     * @throws IOException
     *         if there is a problem opening or reading from this DSG file
     * @throws IllegalArgumentException
     *         if the variable name is invalid
     */
    public double[] readDoubleVarDataValues(String varName) throws IOException, IllegalArgumentException {
        double[] dataVals;
        NetcdfFile ncfile = NetcdfFile.open(getPath());
        try {
            Variable var = ncfile.findVariable(varName);
            if ( var == null )
                throw new IllegalArgumentException("Unable to find variable '" + varName + "' in " + getName());
            ArrayDouble.D1 dvar = (ArrayDouble.D1) var.read();
            int numVals = var.getShape(0);
            dataVals = new double[numVals];
            for (int k = 0; k < numVals; k++) {
                double value = dvar.get(k);
                if ( Double.isNaN(value) || Double.isInfinite(value) )
                    value = DashboardUtils.FP_MISSING_VALUE;
                dataVals[k] = value;
            }
        } finally {
            ncfile.close();
        }
        return dataVals;
    }

    /**
     * Reads and returns the longitudes, latitudes, and times contained in this DSG file.  NaN and infinite values are
     * changed to {@link DashboardUtils#FP_MISSING_VALUE} but otherwise missing values are left as in the DSG file.
     *
     * @return the array { lons, lats, times } for this cruise, where lons are the array of longitudes,
     *         lats are the array of latitudes, times are the array of times.
     *
     * @throws IOException
     *         if problems opening or reading from this DSG file, or
     *         if any of the data arrays are not given in this DSG file
     */
    public double[][] readLonLatTimeDataValues() throws IOException {
        double[] lons;
        double[] lats;
        double[] times;

        NetcdfFile ncfile = NetcdfFile.open(getPath());
        try {
            Variable lonVar = ncfile.findVariable(DashboardServerUtils.LONGITUDE.getVarName());
            if ( lonVar == null )
                throw new IOException("Unable to find longitudes in " + getName());
            int numVals = lonVar.getShape(0);

            Variable latVar = ncfile.findVariable(DashboardServerUtils.LATITUDE.getVarName());
            if ( latVar == null )
                throw new IOException("Unable to find latitudes in " + getName());
            if ( latVar.getShape(0) != numVals )
                throw new IOException("Unexpected number of latitudes in " + getName());

            Variable timeVar = ncfile.findVariable(DashboardServerUtils.TIME.getVarName());
            if ( timeVar == null )
                throw new IOException("Unable to find times in " + getName());
            if ( timeVar.getShape(0) != numVals )
                throw new IOException("Unexpected number of time values in " + getName());

            lons = new double[numVals];
            lats = new double[numVals];
            times = new double[numVals];

            ArrayDouble.D1 dvar = (ArrayDouble.D1) lonVar.read();
            for (int k = 0; k < numVals; k++) {
                double value = dvar.get(k);
                if ( Double.isNaN(value) || Double.isInfinite(value) )
                    value = DashboardUtils.FP_MISSING_VALUE;
                lons[k] = value;
            }

            dvar = (ArrayDouble.D1) latVar.read();
            for (int k = 0; k < numVals; k++) {
                double value = dvar.get(k);
                if ( Double.isNaN(value) || Double.isInfinite(value) )
                    value = DashboardUtils.FP_MISSING_VALUE;
                lats[k] = value;
            }

            dvar = (ArrayDouble.D1) timeVar.read();
            for (int k = 0; k < numVals; k++) {
                double value = dvar.get(k);
                if ( Double.isNaN(value) || Double.isInfinite(value) )
                    value = DashboardUtils.FP_MISSING_VALUE;
                times[k] = value;
            }
        } finally {
            ncfile.close();
        }

        return new double[][] { lons, lats, times };
    }

    /**
     * Reads and returns the longitudes, latitudes, times, SST values, and fCO2_recommended values contained in this
     * DSG file.  NaN and infinite values are changed to {@link DashboardUtils#FP_MISSING_VALUE} but otherwise
     * missing values are left as in the DSG file.  This DSG file must have been processed by Ferret for the
     * fCO2_recommended values to be meaningful.
     *
     * @return the array { lons, lats, times, SSTs, fCO2s } for this cruise, where lons are the array of longitudes,
     *         lats are the array of latitudes, times are the array of times, SSTs are the array of SST values, and
     *         fCO2s are the array of fCO2_recommended values.
     *
     * @throws IOException
     *         if problems opening or reading from this DSG file, or
     *         if any of the data arrays are not given in this DSG file
     */
    public double[][] readLonLatTimeSstFco2DataValues() throws IOException {
        double[] lons;
        double[] lats;
        double[] times;
        double[] ssts;
        double[] fco2s;

        NetcdfFile ncfile = NetcdfFile.open(getPath());
        try {
            Variable lonVar = ncfile.findVariable(DashboardServerUtils.LONGITUDE.getVarName());
            if ( lonVar == null )
                throw new IOException("Unable to find longitudes in " + getName());
            int numVals = lonVar.getShape(0);

            Variable latVar = ncfile.findVariable(DashboardServerUtils.LATITUDE.getVarName());
            if ( latVar == null )
                throw new IOException("Unable to find latitudes in " + getName());
            if ( latVar.getShape(0) != numVals )
                throw new IOException("Unexpected number of latitudes in " + getName());

            Variable timeVar = ncfile.findVariable(DashboardServerUtils.TIME.getVarName());
            if ( timeVar == null )
                throw new IOException("Unable to find times in " + getName());
            if ( timeVar.getShape(0) != numVals )
                throw new IOException("Unexpected number of time values in " + getName());

            Variable sstVar = ncfile.findVariable(SocatTypes.SST.getVarName());
            if ( sstVar == null )
                throw new IOException("Unable to find SST in " + getName());
            if ( sstVar.getShape(0) != numVals )
                throw new IOException("Unexpected number of SST values in " + getName());

            Variable fco2Var = ncfile.findVariable(SocatTypes.FCO2_REC.getVarName());
            if ( fco2Var == null )
                throw new IOException("Unable to find fCO2_recommended in " + getName());
            if ( fco2Var.getShape(0) != numVals )
                throw new IOException("Unexpected number of fCO2_recommeded values in " + getName());

            lons = new double[numVals];
            lats = new double[numVals];
            times = new double[numVals];
            ssts = new double[numVals];
            fco2s = new double[numVals];

            ArrayDouble.D1 dvar = (ArrayDouble.D1) lonVar.read();
            for (int k = 0; k < numVals; k++) {
                double value = dvar.get(k);
                if ( Double.isNaN(value) || Double.isInfinite(value) )
                    value = DashboardUtils.FP_MISSING_VALUE;
                lons[k] = value;
            }

            dvar = (ArrayDouble.D1) latVar.read();
            for (int k = 0; k < numVals; k++) {
                double value = dvar.get(k);
                if ( Double.isNaN(value) || Double.isInfinite(value) )
                    value = DashboardUtils.FP_MISSING_VALUE;
                lats[k] = value;
            }

            dvar = (ArrayDouble.D1) timeVar.read();
            for (int k = 0; k < numVals; k++) {
                double value = dvar.get(k);
                if ( Double.isNaN(value) || Double.isInfinite(value) )
                    value = DashboardUtils.FP_MISSING_VALUE;
                times[k] = value;
            }

            dvar = (ArrayDouble.D1) sstVar.read();
            for (int k = 0; k < numVals; k++) {
                double value = dvar.get(k);
                if ( Double.isNaN(value) || Double.isInfinite(value) )
                    value = DashboardUtils.FP_MISSING_VALUE;
                ssts[k] = value;
            }

            dvar = (ArrayDouble.D1) fco2Var.read();
            for (int k = 0; k < numVals; k++) {
                double value = dvar.get(k);
                if ( Double.isNaN(value) || Double.isInfinite(value) )
                    value = DashboardUtils.FP_MISSING_VALUE;
                fco2s[k] = value;
            }
        } finally {
            ncfile.close();
        }

        return new double[][] { lons, lats, times, ssts, fco2s };
    }

    /**
     * @return the dataset QC flag (first element) and the version (second element) contained in this DSG file
     *
     * @throws IllegalArgumentException
     *         if this DSG file is not valid
     * @throws IOException
     *         if opening or reading from the DSG file throws one
     */
    public String[] getDatasetQCFlagAndVersion() throws IllegalArgumentException, IOException {
        String flag;
        String version;
        NetcdfFile ncfile = NetcdfFile.open(getPath());
        try {
            String varName = DashboardServerUtils.DATASET_QC_FLAG.getVarName();
            Variable var = ncfile.findVariable(varName);
            if ( var == null )
                throw new IllegalArgumentException("Unable to find variable '" + varName + "' in " + getName());
            ArrayChar.D2 flagArray = (ArrayChar.D2) var.read();
            flag = flagArray.getString(0).trim();
            varName = DashboardServerUtils.VERSION.getVarName();
            var = ncfile.findVariable(varName);
            if ( var == null )
                throw new IllegalArgumentException("Unable to find variable '" + varName + "' in " + getName());
            ArrayChar.D2 versionArray = (ArrayChar.D2) var.read();
            version = flagArray.getString(0).trim();
        } finally {
            ncfile.close();
        }
        return new String[] { flag, version };
    }

    /**
     * Updates this DSG file with the given QC flag and version number
     *
     * @param qcFlag
     *         the QC flag to assign
     * @param version
     *         version to assign
     *
     * @throws IllegalArgumentException
     *         if this DSG file is not valid
     * @throws IOException
     *         if opening or writing to the DSG file throws one
     */
    public void updateDatasetQCFlagAndVersion(String qcFlag, String version)
            throws IllegalArgumentException, IOException {
        NetcdfFileWriter ncfile = NetcdfFileWriter.openExisting(getPath());
        try {
            String varName = DashboardServerUtils.DATASET_QC_FLAG.getVarName();
            Variable var = ncfile.findVariable(varName);
            if ( var == null )
                throw new IllegalArgumentException("Unable to find variable '" + varName + "' in " + getName());
            ArrayChar.D2 flagArray = new ArrayChar.D2(1, var.getShape(1));
            flagArray.setString(0, qcFlag.trim());
            try {
                ncfile.write(var, flagArray);
            } catch ( InvalidRangeException ex ) {
                throw new IOException(ex);
            }

            varName = DashboardServerUtils.VERSION.getVarName();
            var = ncfile.findVariable(varName);
            if ( var == null )
                throw new IllegalArgumentException("Unable to find variable '" + varName + "' in " + getName());
            ArrayChar.D2 versionArray = new ArrayChar.D2(1, var.getShape(1));
            versionArray.setString(0, version.trim());
            try {
                ncfile.write(var, versionArray);
            } catch ( InvalidRangeException ex ) {
                throw new IOException(ex);
            }
        } finally {
            ncfile.close();
        }
    }

    /**
     * Updates the all_region_ids metadata variable in this DSG file.
     *
     * @param newValue
     *         the all_region_ids value to assign; if null, the all_region_ids value is obtained
     *         from the values of the region_id data variable in this DSG file.
     *
     * @return the all_region_ids value assigned
     *
     * @throws IllegalArgumentException
     *         if this DSG file is not valid
     * @throws IOException
     *         if opening or writing to the DSG file throws one
     * @throws InvalidRangeException
     *         if writing the updated QC flag to the DSG file throws one
     */
    public String updateAllRegionIDs(String newValue)
            throws IllegalArgumentException, IOException, InvalidRangeException {
        String allRegionIDs;
        if ( newValue == null ) {
            // Generate the String of sorted unique IDs
            String[] regionIDs = readStringVarDataValues(DashboardServerUtils.REGION_ID.getVarName());
            TreeSet<String> allRegionIDsSet = new TreeSet<String>();
            for (String id : regionIDs) {
                allRegionIDsSet.add(id.trim());
            }
            allRegionIDs = "";
            for (String id : allRegionIDsSet) {
                allRegionIDs += id.toString();
            }
        }
        else
            allRegionIDs = newValue;

        // Write this String of all region IDs to the NetCDF file
        NetcdfFileWriter ncfile = NetcdfFileWriter.openExisting(getPath());
        try {
            String varName = DashboardServerUtils.ALL_REGION_IDS.getVarName();
            Variable var = ncfile.findVariable(varName);
            if ( var == null )
                throw new IllegalArgumentException("Unable to find variable '" + varName + "' in " + getName());
            if ( var.getShape(1) < allRegionIDs.length() )
                throw new IllegalArgumentException("Not enough space (max " + Integer.toString(var.getShape(1)) +
                        ") for the string of all region IDs (" + allRegionIDs + ")");
            ArrayChar.D2 allRegionIDsArray = new ArrayChar.D2(1, var.getShape(1));
            allRegionIDsArray.setString(0, allRegionIDs.trim());
            ncfile.write(var, allRegionIDsArray);
        } finally {
            ncfile.close();
        }
        return allRegionIDs;
    }

    /**
     * Updates this DSG file with the given data QC flags.
     * Optionally, will also update the row number in the data QC flags from the data in this DSG file.
     *
     * @param woceEvent
     *         data QC flags to set
     * @param updateWoceEvent
     *         if true, update the row numbers in the data QC flags from this DSG file
     *
     * @return list of the data QC event data locations not found in this DSG file; never null but may be empty
     *
     * @throws IllegalArgumentException
     *         if the DSG file or the data QC flags are not valid
     * @throws IOException
     *         if opening, reading from, or writing to the DSG file throws one
     */
    public ArrayList<DataLocation> updateDataQCFlags(DataQCEvent woceEvent, boolean updateWoceEvent)
            throws IllegalArgumentException, IOException {
        ArrayList<DataLocation> unidentified = new ArrayList<DataLocation>();
        NetcdfFileWriter ncfile = NetcdfFileWriter.openExisting(getPath());
        try {

            String varName = DashboardServerUtils.LONGITUDE.getVarName();
            Variable var = ncfile.findVariable(varName);
            if ( var == null )
                throw new IllegalArgumentException("Unable to find variable '" + varName + "' in " + getName());
            ArrayDouble.D1 longitudes = (ArrayDouble.D1) var.read();

            varName = DashboardServerUtils.LATITUDE.getVarName();
            var = ncfile.findVariable(varName);
            if ( var == null )
                throw new IllegalArgumentException("Unable to find variable '" + varName + "' in " + getName());
            ArrayDouble.D1 latitudes = (ArrayDouble.D1) var.read();

            varName = DashboardServerUtils.TIME.getVarName();
            var = ncfile.findVariable(varName);
            if ( var == null )
                throw new IllegalArgumentException("Unable to find variable '" + varName + "' in " + getName());
            ArrayDouble.D1 times = (ArrayDouble.D1) var.read();

            String dataname = woceEvent.getVarName();
            ArrayDouble.D1 datavalues;
            if ( DashboardUtils.STRING_MISSING_VALUE.equals(dataname) ) {
                // WOCE based on longitude/latitude/time
                datavalues = null;
            }
            else {
                var = ncfile.findVariable(dataname);
                if ( var == null )
                    throw new IllegalArgumentException("Unable to find variable '" + dataname + "' in " + getName());
                datavalues = (ArrayDouble.D1) var.read();
            }

            varName = woceEvent.getFlagName();
            Variable wocevar = ncfile.findVariable(varName);
            if ( wocevar == null )
                throw new IllegalArgumentException("Unable to find variable '" + varName + "' in " + getName());
            ArrayChar.D2 wocevalues = (ArrayChar.D2) wocevar.read();

            String newFlag = woceEvent.getFlagValue();

            // Identify the data points using a round-robin search
            // just in case there is more than one matching point
            int startIdx = 0;
            int arraySize = (int) times.getSize();
            HashSet<Integer> assignedRowIndices = new HashSet<Integer>();
            for (DataLocation dataloc : woceEvent.getLocations()) {
                boolean valueFound = false;
                int idx;
                for (idx = startIdx; idx < arraySize; idx++) {
                    if ( dataMatches(dataloc, longitudes, latitudes, times, datavalues, idx) ) {
                        if ( assignedRowIndices.add(idx) ) {
                            valueFound = true;
                            break;
                        }
                    }
                }
                if ( idx >= arraySize ) {
                    for (idx = 0; idx < startIdx; idx++) {
                        if ( dataMatches(dataloc, longitudes, latitudes, times, datavalues, idx) ) {
                            if ( assignedRowIndices.add(idx) ) {
                                valueFound = true;
                                break;
                            }
                        }
                    }
                }
                if ( valueFound ) {
                    wocevalues.setString(idx, newFlag.trim());
                    if ( updateWoceEvent ) {
                        dataloc.setRowNumber(idx + 1);
                    }
                    // Start the next search from the next data point
                    startIdx = idx + 1;
                }
                else {
                    unidentified.add(dataloc);
                }
            }

            // Save the updated WOCE flags to the DSG file
            try {
                ncfile.write(wocevar, wocevalues);
            } catch ( InvalidRangeException ex ) {
                throw new IOException(ex);
            }
        } finally {
            ncfile.close();
        }
        return unidentified;
    }

    /**
     * Compares the data location information given in a DataLocation with the longitude, latitude,
     * time, and (if applicable) data value at a given index into arrays of these values.
     *
     * @param dataloc
     *         data location to compare
     * @param longitudes
     *         array of longitudes to use
     * @param latitudes
     *         array of latitudes to use
     * @param times
     *         array of times (seconds since 1970-01-01 00:00:00) to use
     * @param datavalues
     *         if not null, array of data values to use
     * @param idx
     *         index into the arrays of the values to compare
     *
     * @return true if the data locations match
     */
    private boolean dataMatches(DataLocation dataloc, ArrayDouble.D1 longitudes, ArrayDouble.D1 latitudes,
            ArrayDouble.D1 times, ArrayDouble.D1 datavalues, int idx) {
        // Check if longitude is within 0.001 degrees of each other
        if ( !DashboardUtils.longitudeCloseTo(dataloc.getLongitude(), longitudes.get(idx), 0.0, 0.001) )
            return false;

        // Check if latitude is within 0.0001 degrees of each other
        if ( !DashboardUtils.closeTo(dataloc.getLatitude(), latitudes.get(idx), 0.0, 0.0001) )
            return false;

        // Check if times are within a second of each other
        if ( !DashboardUtils.closeTo(dataloc.getDataDate().getTime() / 1000.0, times.get(idx), 0.0, 1.0) )
            return false;

        // If given, check if data values are close to each other
        if ( datavalues != null ) {
            if ( !DashboardUtils.closeTo(dataloc.getDataValue(), datavalues.get(idx),
                    DashboardUtils.MAX_RELATIVE_ERROR, DashboardUtils.MAX_ABSOLUTE_ERROR) )
                return false;
        }

        return true;
    }

}
