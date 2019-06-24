package gov.noaa.pmel.dashboard.metadata;

import gov.noaa.pmel.dashboard.datatype.SocatTypes;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.sdimetadata.SDIMetadata;
import gov.noaa.pmel.sdimetadata.translate.CdiacReader;
import gov.noaa.pmel.sdimetadata.translate.OcadsWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class OmeTranslator {

    private static final HashMap<String,CdiacReader.VarType> DASH_TYPE_TO_CDIAC_TYPE;

    static {
        DASH_TYPE_TO_CDIAC_TYPE = new HashMap<String,CdiacReader.VarType>();
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.FCO2_WATER_TEQU_WET.getVarName(), CdiacReader.VarType.FCO2_WATER_EQU);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.FCO2_WATER_SST_WET.getVarName(), CdiacReader.VarType.FCO2_WATER_SST);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.PCO2_WATER_TEQU_WET.getVarName(), CdiacReader.VarType.PCO2_WATER_EQU);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.PCO2_WATER_SST_WET.getVarName(), CdiacReader.VarType.PCO2_WATER_SST);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.XCO2_WATER_TEQU_DRY.getVarName(), CdiacReader.VarType.XCO2_WATER_EQU);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.XCO2_WATER_SST_DRY.getVarName(), CdiacReader.VarType.XCO2_WATER_SST);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.FCO2_ATM_DRY_ACTUAL.getVarName(), CdiacReader.VarType.FCO2_ATM_ACTUAL);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.FCO2_ATM_DRY_INTERP.getVarName(), CdiacReader.VarType.FCO2_ATM_INTERP);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.PCO2_ATM_DRY_ACTUAL.getVarName(), CdiacReader.VarType.PCO2_ATM_ACTUAL);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.PCO2_ATM_DRY_INTERP.getVarName(), CdiacReader.VarType.PCO2_ATM_INTERP);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.XCO2_ATM_DRY_ACTUAL.getVarName(), CdiacReader.VarType.XCO2_ATM_ACTUAL);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.XCO2_ATM_DRY_INTERP.getVarName(), CdiacReader.VarType.XCO2_ATM_INTERP);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.SST.getVarName(), CdiacReader.VarType.SEA_SURFACE_TEMPERATURE);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.TEQU.getVarName(), CdiacReader.VarType.EQUILIBRATOR_TEMPERATURE);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.PATM.getVarName(), CdiacReader.VarType.SEA_LEVEL_PRESSURE);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.PEQU.getVarName(), CdiacReader.VarType.EQUILIBRATOR_PRESSURE);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.SALINITY.getVarName(), CdiacReader.VarType.SALINITY);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.WOCE_CO2_WATER.getVarName(), CdiacReader.VarType.WOCE_CO2_WATER);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.WOCE_CO2_ATM.getVarName(), CdiacReader.VarType.WOCE_CO2_ATM);
    }

    /**
     * Using the given data column names and types for a datasets, creates an SDIMetadata object
     * from the contents of a CDIAC OME metadata file.
     *
     * @param omeFile
     *         CDIAC OME metadata file to read
     * @param dataColNames
     *         data column names for this dataset
     * @param dataColTypes
     *         data column types for this dataset
     *
     * @return SDIMetadata object created from the CDIAC OME metadata file contents
     *
     * @throws FileNotFoundException
     *         if the CDIAC OME metadata file does not exist
     * @throws IOException
     *         if an error occurs when reading the CDIAC OME metadata file
     * @throws IllegalArgumentException
     *         if the contents of the CDIAC OME metadata file are invalid
     */
    public static SDIMetadata createSdiMetadataFromCdiacOme(File omeFile,
            ArrayList<String> dataColNames, ArrayList<DataColumnType> dataColTypes)
            throws FileNotFoundException, IOException, IllegalArgumentException {
        // Read the CDIAC XML into an XML Document in memory
        CdiacReader reader;
        FileReader xmlReader = new FileReader(omeFile);
        try {
            reader = new CdiacReader(xmlReader);
        } finally {
            xmlReader.close();
        }
        // Make sure all data column names used are mapped to the correct CdiacReader.VarType
        for (int k = 0; k < dataColNames.size(); k++) {
            CdiacReader.VarType vtype = DASH_TYPE_TO_CDIAC_TYPE.get(dataColTypes.get(k).getVarName());
            if ( vtype != null )
                reader.associateColumnNameWithVarType(dataColNames.get(k), vtype);
        }
        // Create an SDIMetadata object from the contents of the CDIAC XML Document
        return reader.createSDIMetadata();
    }

    /**
     * Creates an OCADS OME metdata file from the contents of the given SDIMetadata object.
     *
     * @param omeFile
     *         OCADS OME metadata file to create
     * @param sdiMData
     *         metadata to write
     *
     * @throws IOException
     *         if there is a problem writing the file
     */
    public static void createOcadsOmeFromSdiMetadata(File omeFile, SDIMetadata sdiMData) throws IOException {
        OcadsWriter ocadsWriter = new OcadsWriter(new FileWriter(omeFile));
        try {
            ocadsWriter.writeOcadsXml(sdiMData);
        } finally {
            ocadsWriter.close();
        }
    }

}
