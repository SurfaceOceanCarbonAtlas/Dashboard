/**
 *
 */
package gov.noaa.pmel.dashboard.dsg;

import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.DoubleDashDataType;
import gov.noaa.pmel.dashboard.datatype.IntDashDataType;
import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.datatype.SocatTypes;
import gov.noaa.pmel.dashboard.datatype.StringDashDataType;
import gov.noaa.pmel.dashboard.datatype.ValueConverter;
import gov.noaa.pmel.dashboard.qc.DataLocation;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.ADCMessage;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetData;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.QCFlag;
import gov.noaa.pmel.dashboard.shared.QCFlag.Severity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TreeSet;

/**
 * A 2-D array of objects corresponding to the standardized values of string values provided by the user.
 * Also contains 1-D arrays of information describing each data column.
 *
 * @author Karl Smith
 */
public class StdUserDataArray extends StdDataArray {

    public static final String INCONSISTENT_NUMBER_OF_DATA_VALUES_MSG =
            "inconstistent number of data values";

    private String[] userColNames;
    private String[] userUnits;
    private String[] userMissVals;
    private Boolean[] standardized;
    private ArrayList<ADCMessage> stdMsgList;

    /**
     * Create from the user's data column descriptions, data strings, data row numbers, and data check flags
     * given for this dataset.  Any data columns types matching {@link DashboardServerUtils#UNKNOWN} or
     * {@link DashboardServerUtils#OTHER} are ignored; {@link #isUsableIndex(int)} will return false, and
     * {@link #getStdVal(int, int)} will throw an exception for data columns of these types.  Values that
     * match a missing value for that data column are set to null.
     *
     * <p>
     * The list of automated data check messages describing problems (critical errors) encountered when
     * standardizing the data can be retrieved using {@link #getStandardizationMessages()}.
     * <p>
     * No bounds checking of standardized data values is performed.
     *
     * @param dataset
     *         dataset, with user's strings data, to use
     * @param knownTypes
     *         all known user data types
     *
     * @throws IllegalArgumentException
     *         if there are no data values,
     *         if a data column description is not a known user data type,
     *         if a required unit conversion is not supported, or
     *         if a standardizer for a given data type is not known
     */
    public StdUserDataArray(DashboardDatasetData dataset, KnownDataTypes knownTypes) throws IllegalArgumentException {
        super(dataset.getDataColTypes(), knownTypes);

        // Add the user's units, missing values, and user column names
        userUnits = new String[numDataCols];
        userMissVals = new String[numDataCols];
        userColNames = new String[numDataCols];
        ArrayList<DataColumnType> dataColumnTypes = dataset.getDataColTypes();
        int numUserDataCols = dataColumnTypes.size();
        ArrayList<String> names = dataset.getUserColNames();
        if ( names.size() != numUserDataCols )
            throw new IllegalArgumentException("number of user column names (" + names.size() +
                    ") does not match the number of user column types (" + numUserDataCols + ")");
        for (int k = 0; k < numUserDataCols; k++) {
            DataColumnType dataColType = dataColumnTypes.get(k);
            userUnits[k] = dataColType.getUnits().get(dataColType.getSelectedUnitIndex());
            if ( DashboardUtils.STRING_MISSING_VALUE.equals(userUnits[k]) )
                userUnits[k] = null;
            userMissVals[k] = dataColType.getSelectedMissingValue();
            if ( DashboardUtils.STRING_MISSING_VALUE.equals(userMissVals[k]) )
                userMissVals[k] = null;
            userColNames[k] = names.get(k);
        }
        // the StdDataArray constructor that was used adds any required WOCE column types that are missing
        // as well as SAMPLE_NUMBER
        for (int k = numUserDataCols; k < numDataCols; k++) {
            userUnits[k] = dataTypes[k].getUnits().get(0);
            if ( DashboardUtils.STRING_MISSING_VALUE.equals(userUnits[k]) )
                userUnits[k] = null;
            userMissVals[k] = null;
            userColNames[k] = dataTypes[k].getDisplayName();
        }

        standardized = new Boolean[numDataCols];
        for (int k = 0; k < numDataCols; k++) {
            standardized[k] = null;
        }

        ArrayList<ArrayList<String>> dataVals = dataset.getDataValues();
        if ( dataVals.isEmpty() )
            throw new IllegalArgumentException("no data values given");
        numSamples = dataVals.size();

        ArrayList<Integer> rowNums = dataset.getRowNums();
        if ( rowNums.size() != numSamples )
            throw new IllegalArgumentException("number of row numbers (" + rowNums.size() +
                    ") does not match the number of samples (" + numSamples + ")");

        stdObjects = new Object[numSamples][numDataCols];
        stdMsgList = new ArrayList<ADCMessage>();

        // Create a 2-D array of these Strings for efficiency
        String[][] strDataVals = new String[numSamples][numDataCols];
        int woceIdx = -1;
        for (int j = 0; j < numSamples; j++) {
            ArrayList<String> rowVals = dataVals.get(j);
            if ( rowVals.size() != numUserDataCols ) {
                // Generate a general message for this row - in case too long
                ADCMessage msg = new ADCMessage();
                msg.setSeverity(Severity.CRITICAL);
                msg.setRowNumber(j + 1);
                msg.setGeneralComment(INCONSISTENT_NUMBER_OF_DATA_VALUES_MSG);
                msg.setDetailedComment(INCONSISTENT_NUMBER_OF_DATA_VALUES_MSG + "; " +
                        numUserDataCols + " expected but " + rowVals.size() + " found");
                stdMsgList.add(msg);
                // Continue on, assuming the missing values are at the end
            }
            for (int k = 0; k < numUserDataCols; k++) {
                try {
                    strDataVals[j][k] = rowVals.get(k);
                } catch ( IndexOutOfBoundsException ex ) {
                    // Setting it to null will generate a "no value given" message
                    strDataVals[j][k] = null;
                }
            }
            for (int k = numUserDataCols; k < numDataCols; k++) {
                if ( DashboardServerUtils.SAMPLE_NUMBER.typeNameEquals(dataTypes[k]) ) {
                    strDataVals[j][k] = rowNums.get(j).toString();
                }
                else if ( SocatTypes.WOCE_CO2_WATER.typeNameEquals(dataTypes[k]) ) {
                    strDataVals[j][k] = DashboardUtils.STRING_MISSING_VALUE;
                }
                else {
                    throw new IllegalArgumentException("unexpected unknown added data types");
                }
            }
        }

        // Standardize data columns
        boolean needsAnotherPass;
        do {
            needsAnotherPass = false;
            for (int k = 0; k < numDataCols; k++) {
                DashDataType<?> colType = dataTypes[k];
                if ( DashboardServerUtils.UNKNOWN.typeNameEquals(colType) ||
                        DashboardServerUtils.OTHER.typeNameEquals(colType) ) {
                    for (int j = 0; j < numSamples; j++) {
                        stdObjects[j][k] = null;
                    }
                    standardized[k] = null;
                }
                else {
                    try {
                        ValueConverter<?> stdizer = colType.getStandardizer(userUnits[k], userMissVals[k], this);
                        for (int j = 0; j < numSamples; j++) {
                            try {
                                stdObjects[j][k] = stdizer.convertValueOf(strDataVals[j][k]);
                            } catch ( IllegalArgumentException ex ) {
                                stdObjects[j][k] = null;
                                ADCMessage msg = new ADCMessage();
                                if ( colType.isCritical() )
                                    msg.setSeverity(Severity.CRITICAL);
                                else
                                    msg.setSeverity(Severity.ERROR);
                                msg.setRowNumber(j + 1);
                                msg.setColNumber(k + 1);
                                msg.setColName(userColNames[k]);
                                msg.setGeneralComment(ex.getMessage());
                                if ( strDataVals[j][k] == null )
                                    msg.setDetailedComment(ex.getMessage());
                                else
                                    msg.setDetailedComment(ex.getMessage() + ": \"" + strDataVals[j][k] + "\"");
                                stdMsgList.add(msg);
                            }
                        }
                        standardized[k] = true;
                    } catch ( IllegalStateException ex ) {
                        standardized[k] = false;
                        needsAnotherPass = true;
                    }
                }
            }
        } while ( needsAnotherPass );
    }

    /**
     * Check for missing longitude, latitude, and time columns or data values.
     * Any problems found generate messages that are added to the internal list of messages.
     *
     * @return the sample times for the data;  may be null if there was incomplete specification of
     *         sample time, or may contain null values if there were problems computing the sample time
     */
    public Double[] checkMissingLonLatTime() {
        try {
            Double[] longitudes = getSampleLongitudes();
            for (int j = 0; j < numSamples; j++) {
                if ( longitudes[j] == null ) {
                    ADCMessage msg = new ADCMessage();
                    msg.setSeverity(Severity.CRITICAL);
                    msg.setRowNumber(j + 1);
                    msg.setColNumber(longitudeIndex + 1);
                    msg.setColName(userColNames[longitudeIndex]);
                    String comment = "missing longitude";
                    msg.setGeneralComment(comment);
                    msg.setDetailedComment(comment);
                    stdMsgList.add(msg);
                }
            }
        } catch ( Exception ex ) {
            ADCMessage msg = new ADCMessage();
            msg.setSeverity(Severity.CRITICAL);
            String comment = "no longitude column";
            msg.setGeneralComment(comment);
            msg.setDetailedComment(comment);
            stdMsgList.add(msg);
        }

        try {
            Double[] latitudes = getSampleLatitudes();
            for (int j = 0; j < numSamples; j++) {
                if ( latitudes[j] == null ) {
                    ADCMessage msg = new ADCMessage();
                    msg.setSeverity(Severity.CRITICAL);
                    msg.setRowNumber(j + 1);
                    msg.setColNumber(latitudeIndex + 1);
                    msg.setColName(userColNames[latitudeIndex]);
                    String comment = "missing latitude";
                    msg.setGeneralComment(comment);
                    msg.setDetailedComment(comment);
                    stdMsgList.add(msg);
                }
            }
        } catch ( Exception ex ) {
            ADCMessage msg = new ADCMessage();
            msg.setSeverity(Severity.CRITICAL);
            String comment = "no latitude column";
            msg.setGeneralComment(comment);
            msg.setDetailedComment(comment);
            stdMsgList.add(msg);
        }

        Double[] times = null;
        try {
            times = calcSampleTimes();
            for (int j = 0; j < numSamples; j++) {
                if ( times[j] == null ) {
                    for (int k = 0; k < indicesForTime.length; k++) {
                        ADCMessage msg = new ADCMessage();
                        msg.setSeverity(Severity.CRITICAL);
                        msg.setRowNumber(j + 1);
                        msg.setColNumber(indicesForTime[k] + 1);
                        msg.setColName(userColNames[indicesForTime[k]]);
                        String comment = "invalid sample date/time specification";
                        msg.setGeneralComment(comment);
                        msg.setDetailedComment(comment);
                        stdMsgList.add(msg);
                    }
                }
            }
        } catch ( Exception ex ) {
            ADCMessage msg = new ADCMessage();
            msg.setSeverity(Severity.CRITICAL);
            String comment = "missing columns for sample date/time specification";
            msg.setGeneralComment(comment);
            msg.setDetailedComment(comment);
            stdMsgList.add(msg);
        }

        return times;
    }

    /**
     * Verifies data samples are ascending in time (oldest to newest).  Also checks for excessive
     * speed between two data points.  Any misorderings or excessive speeds detected generate error
     * messages that are added to the internal list of automated data checker messages.
     *
     * @param times
     *         sample times to be used for this data array
     */
    public void checkDataOrder(Double[] times) {
        Double[] longitudes;
        Double[] latitudes;
        try {
            longitudes = getSampleLongitudes();
            latitudes = getSampleLatitudes();
        } catch ( Exception ex ) {
            // Messages about problems getting the longitudes or latitudes should already have been generated
            return;
        }
        TreeSet<DataLocation> orderedSet = new TreeSet<DataLocation>();
        for (int rowIdx = 0; rowIdx < numSamples; rowIdx++) {
            if ( times[rowIdx] != null ) {
                DataLocation dataLoc = new DataLocation();
                // Messages about missing longitude, latitude, or times should already have been generated
                if ( (times[rowIdx] == null) || (longitudes[rowIdx] == null) || (latitudes[rowIdx] == null) )
                    continue;
                dataLoc.setDataDate(new Date(Math.round(times[rowIdx] * 1000.0)));
                dataLoc.setLongitude(longitudes[rowIdx]);
                dataLoc.setLatitude(latitudes[rowIdx]);
                dataLoc.setRowNumber(rowIdx + 1);
                if ( !orderedSet.add(dataLoc) )
                    throw new RuntimeException("Unexpected duplicate data location with row number");
            }
        }

        // TODO: needs a better method of figuring out which rows are actually misordered
        // the following works okay if there is only one block of misordered data
        // or if multiple blocks are consistent in the "direction" they are misordered.

        double[] maxSpeeds = DashboardConfigStore.getMaxCalcSpeedsKnots();

        // The following will say:
        // 4,5,6 are misordered in 1,2,3,7,8,9,4,5,6,10,11,12;
        // 1,2 are misordered in 3,4,1,2,5,6
        // 3 is misordered in 1,2,4,5
        TreeSet<Integer> forwardErrs = new TreeSet<Integer>();
        ArrayList<ADCMessage> forwardSpeedMsgs = new ArrayList<ADCMessage>();
        int lastRowNum = 0;
        int expectedRowNum = 1;
        for (DataLocation dataLoc : orderedSet) {
            int actualRowNum = dataLoc.getRowNumber();
            while ( expectedRowNum < actualRowNum ) {
                forwardErrs.add(expectedRowNum);
                expectedRowNum += 1;
            }
            if ( expectedRowNum == actualRowNum ) {
                if ( lastRowNum > 0 ) {
                    double kmdelta = DashboardServerUtils.distanceBetween(longitudes[actualRowNum - 1],
                            latitudes[actualRowNum - 1], longitudes[lastRowNum - 1], latitudes[lastRowNum - 1]);
                    double hourdelta = (times[actualRowNum - 1] - times[lastRowNum - 1]) / 3600.0;
                    double speed = 0.539957 * kmdelta / hourdelta;
                    if ( speed > maxSpeeds[1] ) {
                        // Add one message at this time - later repeat with all the columns
                        ADCMessage msg = new ADCMessage();
                        msg.setSeverity(Severity.ERROR);
                        msg.setRowNumber(actualRowNum);
                        msg.setGeneralComment(String.format(
                                "calculated speed exceeds %g knots", maxSpeeds[1]));
                        msg.setDetailedComment(String.format(
                                "calculated speed of %g knots exceeds %g knots", speed, maxSpeeds[1]));
                        forwardSpeedMsgs.add(msg);
                    }
                    else if ( speed > maxSpeeds[0] ) {
                        // Add one message at this time - later repeat with all the columns
                        ADCMessage msg = new ADCMessage();
                        msg.setSeverity(Severity.WARNING);
                        msg.setRowNumber(actualRowNum);
                        msg.setGeneralComment(String.format(
                                "calculated speed exceeds %g knots", maxSpeeds[0]));
                        msg.setDetailedComment(String.format(
                                "calculated speed of %g knots exceeds %g knots", speed, maxSpeeds[0]));
                        forwardSpeedMsgs.add(msg);
                    }
                    else if ( speed < 0.0 ) {
                        // Just to make sure the calculation was done correctly
                        throw new RuntimeException("Negative calculated speed obtained");
                    }
                }
                lastRowNum = actualRowNum;
                expectedRowNum += 1;
            }
        }
        // The following will say:
        // 7,8,9 are misordered in 1,2,3,7,8,9,4,5,6,10,11,12;
        // 3,4 are misordered in 3,4,1,2,5,6
        // 3 is misordered in 1,2,4,5
        TreeSet<Integer> reverseErrs = new TreeSet<Integer>();
        ArrayList<ADCMessage> reverseSpeedMsgs = new ArrayList<ADCMessage>();
        lastRowNum = 0;
        expectedRowNum = numSamples;
        for (DataLocation dataLoc : orderedSet.descendingSet()) {
            int actualRowNum = dataLoc.getRowNumber();
            while ( expectedRowNum > actualRowNum ) {
                reverseErrs.add(expectedRowNum);
                expectedRowNum -= 1;
            }
            if ( expectedRowNum == actualRowNum ) {
                if ( lastRowNum > 0 ) {
                    double kmdelta = DashboardServerUtils.distanceBetween(longitudes[lastRowNum - 1],
                            latitudes[lastRowNum - 1], longitudes[actualRowNum - 1], latitudes[actualRowNum - 1]);
                    double hourdelta = (times[lastRowNum - 1] - times[actualRowNum - 1]) / 3600.0;
                    double speed = 0.539957 * kmdelta / hourdelta;
                    if ( speed > maxSpeeds[1] ) {
                        // Add one message at this time - later repeat with all the columns
                        ADCMessage msg = new ADCMessage();
                        msg.setSeverity(Severity.ERROR);
                        msg.setRowNumber(actualRowNum);
                        msg.setGeneralComment(String.format(
                                "calculated speed exceeds %g knots", maxSpeeds[1]));
                        msg.setDetailedComment(String.format(
                                "calculated speed of %g knots exceeds %g knots", speed, maxSpeeds[1]));
                        reverseSpeedMsgs.add(msg);
                    }
                    else if ( speed > maxSpeeds[0] ) {
                        // Add one message at this time - later repeat with all the columns
                        ADCMessage msg = new ADCMessage();
                        msg.setSeverity(Severity.WARNING);
                        msg.setRowNumber(actualRowNum);
                        msg.setGeneralComment(String.format(
                                "calculated speed exceeds %g knots", maxSpeeds[0]));
                        msg.setDetailedComment(String.format(
                                "calculated speed of %g knots exceeds %g knots", speed, maxSpeeds[0]));
                        reverseSpeedMsgs.add(msg);
                    }
                    else if ( speed < 0.0 ) {
                        // Just to make sure the calculation was done correctly
                        throw new RuntimeException("Negative calculated speed obtained");
                    }
                }
                lastRowNum = actualRowNum;
                expectedRowNum -= 1;
            }
        }
        // Guess that the set with fewer errors is the correct one
        TreeSet<Integer> errorRowsNums;
        ArrayList<ADCMessage> speedMsgs;
        if ( (forwardErrs.size() + forwardSpeedMsgs.size()) <= (reverseErrs.size() + reverseSpeedMsgs.size()) ) {
            errorRowsNums = forwardErrs;
            speedMsgs = forwardSpeedMsgs;
        }
        else {
            errorRowsNums = reverseErrs;
            speedMsgs = reverseSpeedMsgs;
        }
        for (Integer rowNum : errorRowsNums) {
            for (Integer colIdx : indicesForTime) {
                ADCMessage msg = new ADCMessage();
                msg.setSeverity(Severity.CRITICAL);
                msg.setRowNumber(rowNum);
                msg.setColNumber(colIdx + 1);
                msg.setColName(userColNames[colIdx]);
                String comment = "time-misordered data row";
                msg.setGeneralComment(comment);
                msg.setDetailedComment(comment);
                stdMsgList.add(msg);
            }
        }
        for (ADCMessage spdmsg : speedMsgs) {
            // Speed problems could be in longitude ...
            ADCMessage msg = new ADCMessage();
            msg.setSeverity(spdmsg.getSeverity());
            msg.setRowNumber(spdmsg.getRowNumber());
            msg.setColNumber(longitudeIndex + 1);
            msg.setColName(userColNames[longitudeIndex]);
            msg.setGeneralComment(spdmsg.getGeneralComment());
            msg.setDetailedComment(spdmsg.getDetailedComment());
            stdMsgList.add(msg);
            // ... or latitude ...
            msg = new ADCMessage();
            msg.setSeverity(spdmsg.getSeverity());
            msg.setRowNumber(spdmsg.getRowNumber());
            msg.setColNumber(latitudeIndex + 1);
            msg.setColName(userColNames[latitudeIndex]);
            msg.setGeneralComment(spdmsg.getGeneralComment());
            msg.setDetailedComment(spdmsg.getDetailedComment());
            stdMsgList.add(msg);
            // ... or time
            for (Integer colIdx : indicesForTime) {
                msg = new ADCMessage();
                msg.setSeverity(spdmsg.getSeverity());
                msg.setRowNumber(spdmsg.getRowNumber());
                msg.setColNumber(colIdx + 1);
                msg.setColName(userColNames[colIdx]);
                msg.setGeneralComment(spdmsg.getGeneralComment());
                msg.setDetailedComment(spdmsg.getDetailedComment());
                stdMsgList.add(msg);
            }
        }
    }

    /**
     * Checks that all values given (not missing values) are within the acceptable range for that data type.
     * Any problems found generate (error or warning) messages that are added to the internal list of messages.
     */
    public void checkBounds() {
        for (int k = 0; k < numDataCols; k++) {
            DashDataType<?> dtype = dataTypes[k];

            if ( dtype instanceof StringDashDataType ) {
                StringDashDataType strtype = (StringDashDataType) dtype;
                for (int j = 0; j < numSamples; j++) {
                    ADCMessage msg = strtype.boundsCheckStandardValue((String) stdObjects[j][k]);
                    if ( msg != null ) {
                        msg.setRowNumber(j + 1);
                        msg.setColNumber(k + 1);
                        msg.setColName(userColNames[k]);
                        stdMsgList.add(msg);
                    }
                }
            }
            else if ( dtype instanceof IntDashDataType ) {
                IntDashDataType inttype = (IntDashDataType) dtype;
                for (int j = 0; j < numSamples; j++) {
                    ADCMessage msg = inttype.boundsCheckStandardValue((Integer) stdObjects[j][k]);
                    if ( msg != null ) {
                        msg.setRowNumber(j + 1);
                        msg.setColNumber(k + 1);
                        msg.setColName(userColNames[k]);
                        stdMsgList.add(msg);
                    }
                }
            }
            else if ( dtype instanceof DoubleDashDataType ) {
                DoubleDashDataType dbltype = (DoubleDashDataType) dtype;
                for (int j = 0; j < numSamples; j++) {
                    ADCMessage msg = dbltype.boundsCheckStandardValue((Double) stdObjects[j][k]);
                    if ( msg != null ) {
                        msg.setRowNumber(j + 1);
                        msg.setColNumber(k + 1);
                        msg.setColName(userColNames[k]);
                        stdMsgList.add(msg);
                    }
                }
            }
            else {
                throw new IllegalArgumentException(
                        "unexpected data type encountered in bounds checking: " + dtype);
            }
        }
    }

    /**
     * Checks that data column values for any metadata items are either all the same value or are missing
     */
    public void checkMetadataTypeValues() {
        for (int k = 0; k < numDataCols; k++) {
            DashDataType<?> dtype = dataTypes[k];
            if ( !dtype.hasRole(DashDataType.Role.FILE_METADATA) )
                continue;

            if ( dtype instanceof StringDashDataType ) {
                String singleVal = null;
                for (int j = 0; j < numSamples; j++) {
                    String thisVal = (String) stdObjects[j][k];
                    if ( thisVal == null )
                        continue;
                    if ( singleVal == null ) {
                        singleVal = thisVal;
                        continue;
                    }
                    if ( singleVal.equals(thisVal) )
                        continue;

                    ADCMessage msg = new ADCMessage();
                    // Metadata in data columns is never required
                    msg.setSeverity(Severity.ERROR);
                    msg.setGeneralComment(dtype.getDisplayName() + " has differing given values");
                    msg.setDetailedComment(dtype.getDisplayName() + " has differeing given values '" +
                            singleVal + "' and " + thisVal + "'");
                    msg.setRowNumber(j + 1);
                    msg.setColNumber(k + 1);
                    msg.setColName(userColNames[k]);
                    stdMsgList.add(msg);
                }
            }
            else if ( dtype instanceof IntDashDataType ) {
                Integer singleVal = null;
                for (int j = 0; j < numSamples; j++) {
                    Integer thisVal = (Integer) stdObjects[j][k];
                    if ( thisVal == null )
                        continue;
                    if ( singleVal == null ) {
                        singleVal = thisVal;
                        continue;
                    }
                    if ( singleVal.equals(thisVal) )
                        continue;

                    ADCMessage msg = new ADCMessage();
                    // Metadata in data columns is never required
                    msg.setSeverity(Severity.ERROR);
                    msg.setGeneralComment(dtype.getDisplayName() + " has differing given values");
                    msg.setDetailedComment(dtype.getDisplayName() + " has differing given values '" +
                            singleVal.toString() + "' and '" + thisVal.toString() + "'");
                    msg.setRowNumber(j + 1);
                    msg.setColNumber(k + 1);
                    msg.setColName(userColNames[k]);
                    stdMsgList.add(msg);
                }
            }
            else if ( dtype instanceof DoubleDashDataType ) {
                Double singleVal = null;
                for (int j = 0; j < numSamples; j++) {
                    Double thisVal = (Double) stdObjects[j][k];
                    if ( thisVal == null )
                        continue;
                    if ( singleVal == null ) {
                        singleVal = thisVal;
                        continue;
                    }
                    if ( singleVal.equals(thisVal) )
                        continue;
                    if ( Math.abs(singleVal - thisVal) < 1.0E-6 )
                        continue;

                    ADCMessage msg = new ADCMessage();
                    // Metadata in data columns is never required
                    msg.setSeverity(Severity.ERROR);
                    msg.setGeneralComment(dtype.getDisplayName() + " has differing given values");
                    msg.setDetailedComment(String.format("%s has differing given values '%g' and '%g'",
                            dtype.getDisplayName(), singleVal, thisVal));
                    msg.setRowNumber(j + 1);
                    msg.setColNumber(k + 1);
                    msg.setColName(userColNames[k]);
                    stdMsgList.add(msg);
                }
            }
            else {
                throw new IllegalArgumentException(
                        "unexpected data type encountered in metadata column checking: " + dtype);
            }
        }
    }

    /**
     * @return the list of automated data check messages describing problems detected in the data.
     *         The messages that are in this list comes from the constructor as well as any check
     *         methods that were called.  Never null.
     */
    public ArrayList<ADCMessage> getStandardizationMessages() {
        return stdMsgList;
    }

    /**
     * Adds data QC flags derived from the messages from standardization and
     * automated data checking to appropriate data QC columns in userStdData.
     */
    public void addAutomatedDataQC() {
        // For current SOCAT, all the automated data checker flags are put under WOCE_CO2_water.
        // In general (and possibly future SOCAT), the QC column(s) to assign depend on the error.
        int qcColIdx = 0;
        for (DashDataType<?> dtype : dataTypes) {
            if ( dtype.typeNameEquals(SocatTypes.WOCE_CO2_WATER) ) {
                break;
            }
            qcColIdx++;
        }
        if ( qcColIdx >= dataTypes.length )
            throw new RuntimeException("WOCE_CO2_water not found in StdUserDataArray.addAutomatedDataQC");

        for (ADCMessage msg : stdMsgList) {
            // Data QC always has a positive row number.  Dataset QC as well as general and summaries
            // QC messages have a negative row number (DashboardUtils.INT_MISSING_VALUE)
            int rowNum = msg.getRowNumber();
            if ( rowNum <= 0 )
                continue;

            // TODO: in the general case, get the correct data QC column and flag value
            String flagValue;
            QCFlag.Severity severity = msg.getSeverity();
            switch ( severity ) {
                case UNASSIGNED:
                case ACCEPTABLE:
                    flagValue = null;
                    break;
                case WARNING:
                    // flagValue = DashboardServerUtils.WOCE_QUESTIONABLE;
                    // Ignore automated data checker warnings as the are just pointing out
                    // potential issues which may not be a problem or have any consequence
                    flagValue = null;
                    break;
                case ERROR:
                case CRITICAL:
                    flagValue = DashboardServerUtils.WOCE_BAD;
                    break;
                default:
                    throw new IllegalArgumentException("unexpected messages severity of " + severity);
            }
            if ( flagValue == null )
                continue;

            // Do not worry about any existing flags as this is always a WOCE-4, and thus, more severe
            stdObjects[rowNum - 1][qcColIdx] = flagValue;
        }
    }

    /**
     * Determines is this data column is an appropriate index. Checks that the value is in
     * the appropriate range and that the column with this index has been standardized.
     *
     * @param idx
     *         index to test
     *
     * @return if the index is valid
     */
    @Override
    public boolean isUsableIndex(int idx) {
        if ( idx < 0 )
            return false;
        if ( idx >= numDataCols )
            return false;
        return Boolean.TRUE.equals(standardized[idx]);
    }

    /**
     * Get the standard value object for the specified value (column index) of the specified sample (row index).
     *
     * @param sampleIdx
     *         index of the sample (row)
     * @param columnIdx
     *         index of the data column
     *
     * @return standard value object; null is returned for "missing value" or values that could not be interpreted
     *
     * @throws IndexOutOfBoundsException
     *         if either the sample index or the column index is invalid
     * @throws IllegalArgumentException
     *         if the value cannot be standardized
     * @throws IllegalStateException
     *         if the value has not been standardized
     */
    @Override
    public Object getStdVal(int sampleIdx, int columnIdx)
            throws IndexOutOfBoundsException, IllegalArgumentException, IllegalStateException {
        if ( (sampleIdx < 0) || (sampleIdx >= numSamples) )
            throw new IndexOutOfBoundsException("sample index is invalid: " + sampleIdx);
        if ( (columnIdx < 0) || (columnIdx >= numDataCols) )
            throw new IndexOutOfBoundsException("data column index is invalid: " + columnIdx);
        if ( standardized[columnIdx] == null )
            throw new IllegalArgumentException("value cannot be standardized");
        if ( !standardized[columnIdx] )
            throw new IllegalStateException("value has not been standardized");
        return stdObjects[sampleIdx][columnIdx];
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = super.hashCode();
        result = prime * result + stdMsgList.hashCode();
        result = prime * result + Arrays.hashCode(standardized);
        result = prime * result + Arrays.hashCode(userMissVals);
        result = prime * result + Arrays.hashCode(userUnits);
        result = prime * result + Arrays.hashCode(userColNames);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( !super.equals(obj) )
            return false;

        if ( !(obj instanceof StdUserDataArray) )
            return false;
        StdUserDataArray other = (StdUserDataArray) obj;

        if ( !stdMsgList.equals(other.stdMsgList) )
            return false;
        if ( !Arrays.equals(standardized, other.standardized) )
            return false;
        if ( !Arrays.equals(userColNames, other.userColNames) )
            return false;
        if ( !Arrays.equals(userMissVals, other.userMissVals) )
            return false;
        if ( !Arrays.equals(userUnits, other.userUnits) )
            return false;

        return true;
    }

    @Override
    public String toString() {
        String repr = "StdUserDataArray[numSamples=" + numSamples + ", numDataCols=" + numDataCols +
                ",\n  stdMsgList=[";
        boolean first = true;
        for (ADCMessage msg : stdMsgList) {
            if ( first )
                first = false;
            else
                repr += ",";
            repr += "\n    " + msg.toString();
        }
        repr += "\n  ]";
        repr += ",\n  userColNames=" + Arrays.toString(userColNames);
        repr += ",\n  userUnits=" + Arrays.toString(userUnits);
        repr += ",\n  userMissVals=" + Arrays.toString(userMissVals);
        repr += ",\n  standardized=" + Arrays.toString(standardized);
        String superRepr = super.toString();
        int idx = superRepr.indexOf('\n');
        repr += "," + superRepr.substring(idx);
        return repr;
    }

}
