package gov.noaa.pmel.sdimetadata.test;

import gov.noaa.pmel.sdimetadata.variable.VarType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VarTypeTest {

    @Test
    public void getVarTypeFromColumnName() {
        assertEquals(VarType.FCO2_WATER_EQU, VarType.getVarTypeFromColumnName("fCO2_equ_w"));
        assertEquals(VarType.FCO2_WATER_SST, VarType.getVarTypeFromColumnName("fCO2_SST_100_hum [uatm]"));
        assertEquals(VarType.PCO2_WATER_EQU, VarType.getVarTypeFromColumnName("pCO2 SW Equi"));
        assertEquals(VarType.PCO2_WATER_SST, VarType.getVarTypeFromColumnName("pCO2w-SST (uatm)"));
        assertEquals(VarType.XCO2_WATER_EQU, VarType.getVarTypeFromColumnName("xco2sw ( ppm )!"));
        assertEquals(VarType.XCO2_WATER_SST, VarType.getVarTypeFromColumnName("xCO2,Water,SST"));
        assertEquals(VarType.FCO2_ATM_ACTUAL, VarType.getVarTypeFromColumnName("fCO2_Atm-Wet"));
        assertEquals(VarType.FCO2_ATM_INTERP, VarType.getVarTypeFromColumnName("fCO2-Atm (Interp)"));
        assertEquals(VarType.PCO2_ATM_ACTUAL, VarType.getVarTypeFromColumnName("pCO2AtmActual"));
        assertEquals(VarType.PCO2_ATM_INTERP, VarType.getVarTypeFromColumnName("pCO2AtmInterp"));
        assertEquals(VarType.XCO2_ATM_ACTUAL, VarType.getVarTypeFromColumnName("xCO2AirDry (umol/mol)"));
        assertEquals(VarType.XCO2_ATM_INTERP, VarType.getVarTypeFromColumnName("xCO2 AtmI nte rp"));
        assertEquals(VarType.SEA_SURFACE_TEMPERATURE, VarType.getVarTypeFromColumnName("SST °C"));
        assertEquals(VarType.EQUILIBRATOR_TEMPERATURE, VarType.getVarTypeFromColumnName("Equ Temp"));
        assertEquals(VarType.SEA_LEVEL_PRESSURE, VarType.getVarTypeFromColumnName("PPPP"));
        assertEquals(VarType.EQUILIBRATOR_PRESSURE, VarType.getVarTypeFromColumnName("PRS EQ"));
        assertEquals(VarType.SALINITY, VarType.getVarTypeFromColumnName("Sal <permil>"));
        assertEquals(VarType.WOCE_CO2_WATER, VarType.getVarTypeFromColumnName("WOCE {Water}"));
        assertEquals(VarType.WOCE_CO2_ATM, VarType.getVarTypeFromColumnName("QC CO2 Air"));
        assertEquals(VarType.OTHER, VarType.getVarTypeFromColumnName("Sta"));
        assertEquals(VarType.OTHER, VarType.getVarTypeFromColumnName("Day"));
        assertEquals(VarType.OTHER, VarType.getVarTypeFromColumnName("Lon"));
        assertEquals(VarType.OTHER, VarType.getVarTypeFromColumnName("Lat"));
        assertEquals(VarType.OTHER, VarType.getVarTypeFromColumnName("Depth"));
        assertEquals(VarType.OTHER, VarType.getVarTypeFromColumnName("Time"));
        assertEquals(VarType.OTHER, VarType.getVarTypeFromColumnName("WOCE SST"));
        assertEquals(VarType.OTHER, VarType.getVarTypeFromColumnName(""));
    }

}

