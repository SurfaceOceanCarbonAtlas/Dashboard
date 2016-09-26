/**
 * 
 */
package gov.noaa.pmel.dashboard.server;

import java.util.Properties;

/**
 * Temporary hack giving the SOCAT standard types.
 * 
 * @author Karl Smith
 */
public class SocatTypes {

	/** Known types for SOCAT dashboard users */
	public static final KnownDataTypes KNOWN_SOCAT_USER_TYPES;

	/** Known SOCAT metadata types for files */
	public static final KnownDataTypes KNOWN_SOCAT_METADATA_FILE_TYPES;

	/** Known SOCAT data types for files */
	public static final KnownDataTypes KNOWN_SOCAT_DATA_FILE_TYPES;

	static {
		KNOWN_SOCAT_USER_TYPES = new KnownDataTypes();
		KNOWN_SOCAT_USER_TYPES.addStandardTypesForUsers();
		Properties typeProps = new Properties();
		typeProps.setProperty(SocatCruiseData.SALINITY.getVarName(), SocatCruiseData.SALINITY.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.TEQU.getVarName(), SocatCruiseData.TEQU.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.SST.getVarName(), SocatCruiseData.SST.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.TATM.getVarName(), SocatCruiseData.TATM.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.PEQU.getVarName(), SocatCruiseData.PEQU.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.PATM.getVarName(), SocatCruiseData.PATM.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.XCO2_WATER_TEQU_DRY.getVarName(), SocatCruiseData.XCO2_WATER_TEQU_DRY.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.XCO2_WATER_SST_DRY.getVarName(), SocatCruiseData.XCO2_WATER_SST_DRY.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.XCO2_WATER_TEQU_WET.getVarName(), SocatCruiseData.XCO2_WATER_TEQU_WET.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.XCO2_WATER_SST_WET.getVarName(), SocatCruiseData.XCO2_WATER_SST_WET.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.PCO2_WATER_TEQU_WET.getVarName(), SocatCruiseData.PCO2_WATER_TEQU_WET.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.PCO2_WATER_SST_WET.getVarName(), SocatCruiseData.PCO2_WATER_SST_WET.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.FCO2_WATER_TEQU_WET.getVarName(), SocatCruiseData.FCO2_WATER_TEQU_WET.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.FCO2_WATER_SST_WET.getVarName(), SocatCruiseData.FCO2_WATER_SST_WET.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.XCO2_ATM_DRY_ACTUAL.getVarName(), SocatCruiseData.XCO2_ATM_DRY_ACTUAL.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.XCO2_ATM_DRY_INTERP.getVarName(), SocatCruiseData.XCO2_ATM_DRY_INTERP.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.PCO2_ATM_DRY_ACTUAL.getVarName(), SocatCruiseData.PCO2_ATM_DRY_ACTUAL.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.PCO2_ATM_DRY_INTERP.getVarName(), SocatCruiseData.PCO2_ATM_DRY_INTERP.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.FCO2_ATM_DRY_ACTUAL.getVarName(), SocatCruiseData.FCO2_ATM_DRY_ACTUAL.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.FCO2_ATM_DRY_INTERP.getVarName(), SocatCruiseData.FCO2_ATM_DRY_INTERP.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.DELTA_XCO2.getVarName(), SocatCruiseData.DELTA_XCO2.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.DELTA_PCO2.getVarName(), SocatCruiseData.DELTA_PCO2.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.DELTA_FCO2.getVarName(), SocatCruiseData.DELTA_FCO2.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.XH2O_EQU.getVarName(), SocatCruiseData.XH2O_EQU.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.RELATIVE_HUMIDITY.getVarName(), SocatCruiseData.RELATIVE_HUMIDITY.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.SPECIFIC_HUMIDITY.getVarName(), SocatCruiseData.SPECIFIC_HUMIDITY.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.SHIP_SPEED.getVarName(), SocatCruiseData.SHIP_SPEED.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.SHIP_DIRECTION.getVarName(), SocatCruiseData.SHIP_DIRECTION.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.WIND_SPEED_TRUE.getVarName(), SocatCruiseData.WIND_SPEED_TRUE.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.WIND_SPEED_RELATIVE.getVarName(), SocatCruiseData.WIND_SPEED_RELATIVE.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.WIND_DIRECTION_TRUE.getVarName(), SocatCruiseData.WIND_DIRECTION_TRUE.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.WIND_DIRECTION_RELATIVE.getVarName(), SocatCruiseData.WIND_DIRECTION_RELATIVE.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.WOCE_CO2_WATER.getVarName(), SocatCruiseData.WOCE_CO2_WATER.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.WOCE_CO2_ATM.getVarName(), SocatCruiseData.WOCE_CO2_ATM.toPropertyValue());
		KNOWN_SOCAT_USER_TYPES.addTypesFromProperties(typeProps);

		KNOWN_SOCAT_METADATA_FILE_TYPES = new KnownDataTypes();
		KNOWN_SOCAT_METADATA_FILE_TYPES.addStandardTypesForMetadataFiles();
		typeProps = new Properties();
		typeProps.setProperty(SocatMetadata.SOCAT_VERSION.getVarName(), SocatMetadata.SOCAT_VERSION.toPropertyValue());
		typeProps.setProperty(SocatMetadata.ALL_REGION_IDS.getVarName(), SocatMetadata.ALL_REGION_IDS.toPropertyValue());
		typeProps.setProperty(SocatMetadata.SOCAT_DOI.getVarName(), SocatMetadata.SOCAT_DOI.toPropertyValue());
		KNOWN_SOCAT_METADATA_FILE_TYPES.addTypesFromProperties(typeProps);

		KNOWN_SOCAT_DATA_FILE_TYPES = new KnownDataTypes();
		KNOWN_SOCAT_DATA_FILE_TYPES.addStandardTypesForDataFiles();
		typeProps = new Properties();
		typeProps.setProperty(SocatCruiseData.SALINITY.getVarName(), SocatCruiseData.SALINITY.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.TEQU.getVarName(), SocatCruiseData.TEQU.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.SST.getVarName(), SocatCruiseData.SST.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.TATM.getVarName(), SocatCruiseData.TATM.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.PEQU.getVarName(), SocatCruiseData.PEQU.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.PATM.getVarName(), SocatCruiseData.PATM.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.XCO2_WATER_TEQU_DRY.getVarName(), SocatCruiseData.XCO2_WATER_TEQU_DRY.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.XCO2_WATER_SST_DRY.getVarName(), SocatCruiseData.XCO2_WATER_SST_DRY.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.XCO2_WATER_TEQU_WET.getVarName(), SocatCruiseData.XCO2_WATER_TEQU_WET.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.XCO2_WATER_SST_WET.getVarName(), SocatCruiseData.XCO2_WATER_SST_WET.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.PCO2_WATER_TEQU_WET.getVarName(), SocatCruiseData.PCO2_WATER_TEQU_WET.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.PCO2_WATER_SST_WET.getVarName(), SocatCruiseData.PCO2_WATER_SST_WET.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.FCO2_WATER_TEQU_WET.getVarName(), SocatCruiseData.FCO2_WATER_TEQU_WET.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.FCO2_WATER_SST_WET.getVarName(), SocatCruiseData.FCO2_WATER_SST_WET.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.XCO2_ATM_DRY_ACTUAL.getVarName(), SocatCruiseData.XCO2_ATM_DRY_ACTUAL.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.XCO2_ATM_DRY_INTERP.getVarName(), SocatCruiseData.XCO2_ATM_DRY_INTERP.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.PCO2_ATM_DRY_ACTUAL.getVarName(), SocatCruiseData.PCO2_ATM_DRY_ACTUAL.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.PCO2_ATM_DRY_INTERP.getVarName(), SocatCruiseData.PCO2_ATM_DRY_INTERP.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.FCO2_ATM_DRY_ACTUAL.getVarName(), SocatCruiseData.FCO2_ATM_DRY_ACTUAL.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.FCO2_ATM_DRY_INTERP.getVarName(), SocatCruiseData.FCO2_ATM_DRY_INTERP.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.DELTA_XCO2.getVarName(), SocatCruiseData.DELTA_XCO2.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.DELTA_PCO2.getVarName(), SocatCruiseData.DELTA_PCO2.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.DELTA_FCO2.getVarName(), SocatCruiseData.DELTA_FCO2.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.XH2O_EQU.getVarName(), SocatCruiseData.XH2O_EQU.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.RELATIVE_HUMIDITY.getVarName(), SocatCruiseData.RELATIVE_HUMIDITY.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.SPECIFIC_HUMIDITY.getVarName(), SocatCruiseData.SPECIFIC_HUMIDITY.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.SHIP_SPEED.getVarName(), SocatCruiseData.SHIP_SPEED.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.SHIP_DIRECTION.getVarName(), SocatCruiseData.SHIP_DIRECTION.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.WIND_SPEED_TRUE.getVarName(), SocatCruiseData.WIND_SPEED_TRUE.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.WIND_SPEED_RELATIVE.getVarName(), SocatCruiseData.WIND_SPEED_RELATIVE.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.WIND_DIRECTION_TRUE.getVarName(), SocatCruiseData.WIND_DIRECTION_TRUE.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.WIND_DIRECTION_RELATIVE.getVarName(), SocatCruiseData.WIND_DIRECTION_RELATIVE.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.WOCE_CO2_WATER.getVarName(), SocatCruiseData.WOCE_CO2_WATER.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.WOCE_CO2_ATM.getVarName(), SocatCruiseData.WOCE_CO2_ATM.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.FCO2_FROM_XCO2_TEQU.getVarName(), SocatCruiseData.FCO2_FROM_XCO2_TEQU.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.FCO2_FROM_XCO2_SST.getVarName(), SocatCruiseData.FCO2_FROM_XCO2_SST.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.FCO2_FROM_PCO2_TEQU.getVarName(), SocatCruiseData.FCO2_FROM_PCO2_TEQU.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.FCO2_FROM_PCO2_SST.getVarName(), SocatCruiseData.FCO2_FROM_PCO2_SST.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.FCO2_FROM_FCO2_TEQU.getVarName(), SocatCruiseData.FCO2_FROM_FCO2_TEQU.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.FCO2_FROM_FCO2_SST.getVarName(), SocatCruiseData.FCO2_FROM_FCO2_SST.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.FCO2_FROM_PCO2_TEQU_NCEP.getVarName(), SocatCruiseData.FCO2_FROM_PCO2_TEQU_NCEP.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.FCO2_FROM_PCO2_SST_NCEP.getVarName(), SocatCruiseData.FCO2_FROM_PCO2_SST_NCEP.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.FCO2_FROM_XCO2_TEQU_WOA.getVarName(), SocatCruiseData.FCO2_FROM_XCO2_TEQU_WOA.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.FCO2_FROM_XCO2_SST_WOA.getVarName(), SocatCruiseData.FCO2_FROM_XCO2_SST_WOA.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.FCO2_FROM_XCO2_TEQU_NCEP.getVarName(), SocatCruiseData.FCO2_FROM_XCO2_TEQU_NCEP.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.FCO2_FROM_XCO2_SST_NCEP.getVarName(), SocatCruiseData.FCO2_FROM_XCO2_SST_NCEP.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.FOC2_FROM_XCO2_TEQU_NCEP_WOA.getVarName(), SocatCruiseData.FOC2_FROM_XCO2_TEQU_NCEP_WOA.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.FCO2_FROM_XCO2_SST_NCEP_WOA.getVarName(), SocatCruiseData.FCO2_FROM_XCO2_SST_NCEP_WOA.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.FCO2_REC.getVarName(), SocatCruiseData.FCO2_REC.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.FCO2_SOURCE.getVarName(), SocatCruiseData.FCO2_SOURCE.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.WOA_SALINITY.getVarName(), SocatCruiseData.WOA_SALINITY.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.NCEP_SLP.getVarName(), SocatCruiseData.NCEP_SLP.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.DELTA_TEMP.getVarName(), SocatCruiseData.DELTA_TEMP.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.CALC_SPEED.getVarName(), SocatCruiseData.CALC_SPEED.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.ETOPO2_DEPTH.getVarName(), SocatCruiseData.ETOPO2_DEPTH.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.GVCO2.getVarName(), SocatCruiseData.GVCO2.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.REGION_ID.getVarName(), SocatCruiseData.REGION_ID.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.DIST_TO_LAND.getVarName(), SocatCruiseData.DIST_TO_LAND.toPropertyValue());
		typeProps.setProperty(SocatCruiseData.DAY_OF_YEAR.getVarName(), SocatCruiseData.DAY_OF_YEAR.toPropertyValue());
		KNOWN_SOCAT_DATA_FILE_TYPES.addTypesFromProperties(typeProps);
	}

}
