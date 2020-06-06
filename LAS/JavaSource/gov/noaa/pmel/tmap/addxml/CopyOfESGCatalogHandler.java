package gov.noaa.pmel.tmap.addxml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class CopyOfESGCatalogHandler extends DefaultHandler {
	String[] ncar_prefixes = new String[] { 
			"narccap.mm5i",
			"cmip5.output1",
			"narccap.wrfg",
			"narccap.hadcm3",
			"narccap.hrm3",
			"cmip5.gfdl_esm2m",
			"narccap.rcm3",
			"narccap.ecp2",
			"ucar.cgd",
			"narccap.ecpc",
			"narccap.crcm",
			"narccap.wrfp"
			};
	String[] ncarcdg_prefixes = new String[] { 
			"ucar.cgd.pcm",
			"ucar.cgd.ccsm4",
			"ucar.cgd.ccsm"
			};
	String[] cdgpcm_prefixes = new String[] { 
			"ucar.cgd.pcm.PIcntrl",
			"ucar.cgd.pcm.B05",
			"ucar.cgd.pcm.B06",
			"ucar.cgd.pcm.B04",
			"ucar.cgd.pcm.B07"
			};
	String[] ccsm4_prefixes = new String[] { 
			"ucar.cgd.ccsm4.trk1_1deg_chm_1850_b55",
			"ucar.cgd.ccsm4.c40",
			"ucar.cgd.ccsm4.g",
			"ucar.cgd.ccsm4.CLM4SP",
			"ucar.cgd.ccsm4.CLM4CN",
			"ucar.cgd.ccsm4.f40",
			"ucar.cgd.ccsm4.c",
			"ucar.cgd.ccsm4.joc",
			"ucar.cgd.ccsm4.f40_amip_cam5_c03_78b",
			"ucar.cgd.ccsm4.CLM3",
			"ucar.cgd.ccsm4.CLMAF",
			"ucar.cgd.ccsm4.b40"
			};
	String[] gc = new String[] {
			"ucar.cgd.ccsm.b30.020.ES02.ice_sh.proc.monthly_ave.v1",
			"cmip5.output1.NCAR.CCSM4.historical.mon.atmos.Amon.r4i1p1.v1",
			"ucar.cgd.ccsm.b30.004.ocn.proc.annual_ave.v1",
			"cmip5.output1.NCAR.CCSM4.rcp85.mon.atmos.Amon.r1i1p1.v1",
			"ucar.cgd.ccsm.b30.032a.atm.proc.monthly_ave.v1",
			"ucar.cgd.ccsm.b30.009.ice_nh.proc.monthly_ave.v1",
			"narccap.wrfg.ccsm-future.table2.v1",
			"ucar.cgd.ccsm.b30.020.ES02.ocn.proc.annual_ave.v1",
			"cmip5.output1.NCAR.CCSM4.historical.mon.atmos.Amon.r2i1p1.v1",
			"cmip5.output1.NCAR.CCSM4.rcp60.mon.atmos.Amon.r1i1p1.v1",
			"cmip5.output1.NCAR.CCSM4.rcp60.mon.atmos.Amon.r4i1p1.v1",
			"cmip5.output1.NCAR.CCSM4.rcp45.mon.atmos.Amon.r5i1p1.v1",
			"ucar.cgd.ccsm.b30.040e.atm.proc.6hourly_ave.v1",
			"narccap.wrfg.ncep.table1.v1",
			"narccap.crcm.ncep.table3.v5",
			"cmip5.output1.NCAR.CCSM4.rcp60.mon.atmos.Amon.r2i1p1.v1",
			"narccap.crcm.cgcm3-future.table1.v2",
			"cmip5.output1.NCAR.CCSM4.historical.mon.atmos.Amon.r1i1p1.v1",
			"cmip5.output1.NCAR.CCSM4.rcp26.mon.atmos.Amon.r1i1p1.v1",
			"narccap.wrfg.ccsm-current.table1.v4",
			"ucar.cgd.ccsm.b30.009.ocn.proc.annual_ave.v1",
			"cmip5.output1.NCAR.CCSM4.rcp60.mon.atmos.Amon.r5i1p1.v1",
			"cmip5.output1.NCAR.CCSM4.rcp85.mon.atmos.Amon.r3i1p1.v1",
			"narccap.wrfg.ccsm-current.table3.v2",
			"cmip5.output1.NCAR.CCSM4.historical.mon.atmos.Amon.r3i1p1.v1",
			"cmip5.output1.NCAR.CCSM4.rcp45.mon.atmos.Amon.r2i1p1.v1",
			"cmip5.output1.NCAR.CCSM4.rcp85.mon.atmos.Amon.r5i1p1.v1",
			"ucar.cgd.ccsm.b30.025.ES01.ocn.proc.monthly_ave.v1",
			"narccap.crcm.cgcm3-current.table3.v3",
			"cmip5.output1.NCAR.CCSM4.rcp60.mon.atmos.Amon.r3i1p1.v1",
			"cmip5.output1.NCAR.CCSM4.historical.mon.atmos.Amon.r6i1p1.v1",
			"narccap.wrfg.cgcm3-future.table2.v1",
			"narccap.crcm.ccsm-current.table1.v1",
			"cmip5.output1.NCAR.CCSM4.rcp85.mon.atmos.Amon.r2i1p1.v1",
			"narccap.crcm.ncep.table2.v4",
			"narccap.mm5i.ccsm-future.table2.v1",
			"ucar.cgd.ccsm.b30.032b.atm.proc.monthly_ave.v1",
			"narccap.wrfg.cgcm3-future.table3.v1",
			"narccap.mm5i.ncep.table3.v2",
			"narccap.crcm.ccsm-future.table1.v1",
			"narccap.crcm.ccsm-current.table3.v2",
			"ucar.cgd.ccsm.b30.026b.ocn.proc.annual_ave.v1",
			"ucar.cgd.ccsm.b30.032.ocn.proc.annual_ave.v1",
			"ucar.cgd.ccsm.b30.032.atm.proc.monthly_ave.v1",
			"ucar.cgd.ccsm4.c.b27.01.ocn.proc.annual_ave.v2",
			"cmip5.output1.NCAR.CCSM4.rcp26.mon.atmos.Amon.r3i1p1.v1",
			"ucar.cgd.ccsm.b30.031.atm.proc.monthly_ave.v1",
			"narccap.crcm.cgcm3-future.table3.v3",
			"ucar.cgd.ccsm.b30.026b.ocn.proc.monthly_ave.v1",
			"narccap.crcm.cgcm3-current.table2.v2",
			"cmip5.output1.NCAR.CCSM4.rcp26.mon.atmos.Amon.r4i1p1.v1",
			"cmip5.output1.NCAR.CCSM4.rcp45.mon.atmos.Amon.r1i1p1.v1",
			"ucar.cgd.ccsm.b30.009.ocn.proc.monthly_ave.v1",
			"ucar.cgd.ccsm.b30.032a.ocn.proc.annual_ave.v1",
			"ucar.cgd.ccsm.b30.031.ice_nh.proc.monthly_ave.v1",
			"ucar.cgd.ccsm.b30.043.atm.proc.monthly_ave.v1",
			"narccap.wrfg.ncep.table2.v2",
			"narccap.mm5i.ccsm-current.table2.v3",
			"ucar.cgd.ccsm.b30.042e.atm.proc.6hourly_ave.v1",
			"ucar.cgd.ccsm.b30.048.atm.proc.monthly_ave.v1",
			"narccap.wrfg.ccsm-current.table2.v1",
			"ucar.cgd.ccsm.b30.020.ES02.ice_nh.proc.monthly_ave.v1",
			"ucar.cgd.ccsm.b30.009.ice_sh.proc.monthly_ave.v1",
			"ucar.cgd.ccsm4.g.b29.01.ocn.proc.annual_ave.v2",
			"ucar.cgd.ccsm.b30.004.ice.proc.monthly_ave.v1",
			"cmip5.output1.NCAR.CCSM4.rcp45.mon.atmos.Amon.r4i1p1.v1",
			"ucar.cgd.ccsm.b30.031.ice_sh.proc.monthly_ave.v1",
			"cmip5.output1.NCAR.CCSM4.rcp85.mon.atmos.Amon.r4i1p1.v1",
			"narccap.crcm.ccsm-future.table2.v2",
			"narccap.crcm.ccsm-current.table2.v2",
			"narccap.wrfg.ccsm-future.table1.v3",
			"ucar.cgd.ccsm.b30.031.ocn.proc.monthly_ave.v1",
			"ucar.cgd.ccsm.b30.032.ocn.proc.monthly_ave.v1",
			"cmip5.output1.NCAR.CCSM4.rcp26.mon.atmos.Amon.r2i1p1.v1",
			"narccap.crcm.ncep.table1.v2",
			"narccap.wrfg.ccsm-future.table3.v2",
			"cmip5.output1.NCAR.CCSM4.historical.mon.atmos.Amon.r5i1p1.v1",
			"narccap.mm5i.ccsm-future.table3.v2",
			"ucar.cgd.ccsm.b30.032a.ocn.proc.monthly_ave.v1",
			"ucar.cgd.ccsm.b30.004.ocn.proc.monthly_ave.v1",
			"narccap.hrm3.ncep.table3.v4",
			"narccap.hrm3.ncep.table3.v3",
			"ucar.cgd.ccsm.b30.031.ocn.proc.annual_ave.v1",
			"ucar.cgd.ccsm.b30.026b.atm.proc.monthly_ave.v1",
			"ucar.cgd.ccsm.b30.030e.atm.proc.6hourly_ave.v1",
			"narccap.wrfg.cgcm3-current.table3.v1",
			"ucar.cgd.ccsm.b30.025.ES01.ocn.proc.annual_ave.v1",
			"cmip5.output1.NCAR.CCSM4.rcp26.mon.atmos.Amon.r5i1p1.v1",
			"narccap.crcm.ccsm-future.table3.v1",
			"ucar.cgd.ccsm.b30.032b.ocn.proc.monthly_ave.v1",
			"narccap.mm5i.ccsm-current.table3.v4",
			"narccap.wrfg.cgcm3-current.table2.v1",
			"narccap.mm5i.ncep.table2.v3",
			"narccap.crcm.cgcm3-future.table2.v2",
			"ucar.cgd.ccsm.b30.009.atm.proc.monthly_ave.v1",
			"ucar.cgd.ccsm4.b40.1850.track1.2deg.003.atm.proc.monthly_ave.v1",
			"ucar.cgd.ccsm.b30.032b.ocn.proc.annual_ave.v1",
			"ucar.cgd.pcm.B07.58.atm.proc.daily_ave.v1",
			"cmip5.output1.NCAR.CCSM4.rcp85.mon.atmos.Amon.r6i1p1.v1",
			"narccap.ecp2.gfdl-current.table2.v1",
			"narccap.crcm.cgcm3-current.table1.v7",
			"cmip5.output1.NCAR.CCSM4.rcp45.mon.atmos.Amon.r3i1p1.v1"};
	/*
	 
	 The good catalogs:
	 
	 102 out of 3145 contain LAS data.
ucar.cgd.ccsm.b30.020.ES02.ice_sh.proc.monthly_ave.v1,1/ucar.cgd.ccsm.b30.020.ES02.ice_sh.proc.monthly_ave.v1.xml
cmip5.output1.NCAR.CCSM4.historical.mon.atmos.Amon.r4i1p1.v1,7/cmip5.output1.NCAR.CCSM4.historical.mon.atmos.Amon.r4i1p1.v1.xml
ucar.cgd.ccsm.b30.004.ocn.proc.annual_ave.v1,1/ucar.cgd.ccsm.b30.004.ocn.proc.annual_ave.v1.xml
cmip5.output1.NCAR.CCSM4.rcp85.mon.atmos.Amon.r1i1p1.v1,7/cmip5.output1.NCAR.CCSM4.rcp85.mon.atmos.Amon.r1i1p1.v1.xml
ucar.cgd.ccsm.b30.032a.atm.proc.monthly_ave.v1,1/ucar.cgd.ccsm.b30.032a.atm.proc.monthly_ave.v1.xml
ucar.cgd.ccsm.b30.009.ice_nh.proc.monthly_ave.v1,1/ucar.cgd.ccsm.b30.009.ice_nh.proc.monthly_ave.v1.xml
narccap.wrfg.ccsm-future.table2.v1,5/narccap.wrfg.ccsm-future.table2.v1.xml
ucar.cgd.ccsm.b30.020.ES02.ocn.proc.annual_ave.v1,1/ucar.cgd.ccsm.b30.020.ES02.ocn.proc.annual_ave.v1.xml
cmip5.output1.NCAR.CCSM4.historical.mon.atmos.Amon.r2i1p1.v1,7/cmip5.output1.NCAR.CCSM4.historical.mon.atmos.Amon.r2i1p1.v1.xml
cmip5.output1.NCAR.CCSM4.rcp60.mon.atmos.Amon.r1i1p1.v1,7/cmip5.output1.NCAR.CCSM4.rcp60.mon.atmos.Amon.r1i1p1.v1.xml
cmip5.output1.NCAR.CCSM4.rcp60.mon.atmos.Amon.r4i1p1.v1,7/cmip5.output1.NCAR.CCSM4.rcp60.mon.atmos.Amon.r4i1p1.v1.xml
cmip5.output1.NCAR.CCSM4.rcp45.mon.atmos.Amon.r5i1p1.v1,7/cmip5.output1.NCAR.CCSM4.rcp45.mon.atmos.Amon.r5i1p1.v1.xml
ucar.cgd.ccsm.b30.040e.atm.proc.6hourly_ave.v1,3/ucar.cgd.ccsm.b30.040e.atm.proc.6hourly_ave.v1.xml
narccap.wrfg.ncep.table1.v1,6/narccap.wrfg.ncep.table1.v1.xml
narccap.crcm.ncep.table3.v5,7/narccap.crcm.ncep.table3.v5.xml
cmip5.output1.NCAR.CCSM4.rcp60.mon.atmos.Amon.r2i1p1.v1,7/cmip5.output1.NCAR.CCSM4.rcp60.mon.atmos.Amon.r2i1p1.v1.xml
narccap.crcm.cgcm3-future.table1.v2,5/narccap.crcm.cgcm3-future.table1.v2.xml
cmip5.output1.NCAR.CCSM4.historical.mon.atmos.Amon.r1i1p1.v1,7/cmip5.output1.NCAR.CCSM4.historical.mon.atmos.Amon.r1i1p1.v1.xml
cmip5.output1.NCAR.CCSM4.rcp26.mon.atmos.Amon.r1i1p1.v1,7/cmip5.output1.NCAR.CCSM4.rcp26.mon.atmos.Amon.r1i1p1.v1.xml
narccap.wrfg.ccsm-current.table1.v4,7/narccap.wrfg.ccsm-current.table1.v4.xml
ucar.cgd.ccsm.b30.009.ocn.proc.annual_ave.v1,1/ucar.cgd.ccsm.b30.009.ocn.proc.annual_ave.v1.xml
cmip5.output1.NCAR.CCSM4.rcp60.mon.atmos.Amon.r5i1p1.v1,7/cmip5.output1.NCAR.CCSM4.rcp60.mon.atmos.Amon.r5i1p1.v1.xml
cmip5.output1.NCAR.CCSM4.rcp85.mon.atmos.Amon.r3i1p1.v1,7/cmip5.output1.NCAR.CCSM4.rcp85.mon.atmos.Amon.r3i1p1.v1.xml
narccap.wrfg.ccsm-current.table3.v2,6/narccap.wrfg.ccsm-current.table3.v2.xml
cmip5.output1.NCAR.CCSM4.historical.mon.atmos.Amon.r3i1p1.v1,7/cmip5.output1.NCAR.CCSM4.historical.mon.atmos.Amon.r3i1p1.v1.xml
cmip5.output1.NCAR.CCSM4.rcp45.mon.atmos.Amon.r2i1p1.v1,7/cmip5.output1.NCAR.CCSM4.rcp45.mon.atmos.Amon.r2i1p1.v1.xml
cmip5.output1.NCAR.CCSM4.rcp85.mon.atmos.Amon.r5i1p1.v1,7/cmip5.output1.NCAR.CCSM4.rcp85.mon.atmos.Amon.r5i1p1.v1.xml
ucar.cgd.ccsm.b30.025.ES01.ocn.proc.monthly_ave.v1,1/ucar.cgd.ccsm.b30.025.ES01.ocn.proc.monthly_ave.v1.xml
narccap.crcm.cgcm3-current.table3.v3,7/narccap.crcm.cgcm3-current.table3.v3.xml
cmip5.output1.NCAR.CCSM4.rcp60.mon.atmos.Amon.r3i1p1.v1,7/cmip5.output1.NCAR.CCSM4.rcp60.mon.atmos.Amon.r3i1p1.v1.xml
cmip5.output1.NCAR.CCSM4.historical.mon.atmos.Amon.r6i1p1.v1,7/cmip5.output1.NCAR.CCSM4.historical.mon.atmos.Amon.r6i1p1.v1.xml
narccap.wrfg.cgcm3-future.table2.v1,6/narccap.wrfg.cgcm3-future.table2.v1.xml
narccap.crcm.ccsm-current.table1.v1,6/narccap.crcm.ccsm-current.table1.v1.xml
cmip5.output1.NCAR.CCSM4.rcp85.mon.atmos.Amon.r2i1p1.v1,7/cmip5.output1.NCAR.CCSM4.rcp85.mon.atmos.Amon.r2i1p1.v1.xml
narccap.crcm.ncep.table2.v4,5/narccap.crcm.ncep.table2.v4.xml
narccap.mm5i.ccsm-future.table2.v1,6/narccap.mm5i.ccsm-future.table2.v1.xml
ucar.cgd.ccsm.b30.032b.atm.proc.monthly_ave.v1,1/ucar.cgd.ccsm.b30.032b.atm.proc.monthly_ave.v1.xml
narccap.wrfg.cgcm3-future.table3.v1,6/narccap.wrfg.cgcm3-future.table3.v1.xml
narccap.mm5i.ncep.table3.v2,5/narccap.mm5i.ncep.table3.v2.xml
narccap.crcm.ccsm-future.table1.v1,6/narccap.crcm.ccsm-future.table1.v1.xml
narccap.crcm.ccsm-current.table3.v2,7/narccap.crcm.ccsm-current.table3.v2.xml
ucar.cgd.ccsm.b30.026b.ocn.proc.annual_ave.v1,1/ucar.cgd.ccsm.b30.026b.ocn.proc.annual_ave.v1.xml
ucar.cgd.ccsm.b30.032.ocn.proc.annual_ave.v1,1/ucar.cgd.ccsm.b30.032.ocn.proc.annual_ave.v1.xml
ucar.cgd.ccsm.b30.032.atm.proc.monthly_ave.v1,1/ucar.cgd.ccsm.b30.032.atm.proc.monthly_ave.v1.xml
ucar.cgd.ccsm4.c.b27.01.ocn.proc.annual_ave.v2,5/ucar.cgd.ccsm4.c.b27.01.ocn.proc.annual_ave.v2.xml
cmip5.output1.NCAR.CCSM4.rcp26.mon.atmos.Amon.r3i1p1.v1,7/cmip5.output1.NCAR.CCSM4.rcp26.mon.atmos.Amon.r3i1p1.v1.xml
ucar.cgd.ccsm.b30.031.atm.proc.monthly_ave.v1,1/ucar.cgd.ccsm.b30.031.atm.proc.monthly_ave.v1.xml
narccap.crcm.cgcm3-future.table3.v3,7/narccap.crcm.cgcm3-future.table3.v3.xml
ucar.cgd.ccsm.b30.026b.ocn.proc.monthly_ave.v1,1/ucar.cgd.ccsm.b30.026b.ocn.proc.monthly_ave.v1.xml
narccap.crcm.cgcm3-current.table2.v2,5/narccap.crcm.cgcm3-current.table2.v2.xml
cmip5.output1.NCAR.CCSM4.rcp26.mon.atmos.Amon.r4i1p1.v1,7/cmip5.output1.NCAR.CCSM4.rcp26.mon.atmos.Amon.r4i1p1.v1.xml
cmip5.output1.NCAR.CCSM4.rcp45.mon.atmos.Amon.r1i1p1.v1,7/cmip5.output1.NCAR.CCSM4.rcp45.mon.atmos.Amon.r1i1p1.v1.xml
ucar.cgd.ccsm.b30.009.ocn.proc.monthly_ave.v1,1/ucar.cgd.ccsm.b30.009.ocn.proc.monthly_ave.v1.xml
ucar.cgd.ccsm.b30.032a.ocn.proc.annual_ave.v1,1/ucar.cgd.ccsm.b30.032a.ocn.proc.annual_ave.v1.xml
ucar.cgd.ccsm.b30.031.ice_nh.proc.monthly_ave.v1,1/ucar.cgd.ccsm.b30.031.ice_nh.proc.monthly_ave.v1.xml
ucar.cgd.ccsm.b30.043.atm.proc.monthly_ave.v1,1/ucar.cgd.ccsm.b30.043.atm.proc.monthly_ave.v1.xml
narccap.wrfg.ncep.table2.v2,6/narccap.wrfg.ncep.table2.v2.xml
narccap.mm5i.ccsm-current.table2.v3,6/narccap.mm5i.ccsm-current.table2.v3.xml
ucar.cgd.ccsm.b30.042e.atm.proc.6hourly_ave.v1,3/ucar.cgd.ccsm.b30.042e.atm.proc.6hourly_ave.v1.xml
ucar.cgd.ccsm.b30.048.atm.proc.monthly_ave.v1,1/ucar.cgd.ccsm.b30.048.atm.proc.monthly_ave.v1.xml
narccap.wrfg.ccsm-current.table2.v1,5/narccap.wrfg.ccsm-current.table2.v1.xml
ucar.cgd.ccsm.b30.020.ES02.ice_nh.proc.monthly_ave.v1,1/ucar.cgd.ccsm.b30.020.ES02.ice_nh.proc.monthly_ave.v1.xml
ucar.cgd.ccsm.b30.009.ice_sh.proc.monthly_ave.v1,1/ucar.cgd.ccsm.b30.009.ice_sh.proc.monthly_ave.v1.xml
ucar.cgd.ccsm4.g.b29.01.ocn.proc.annual_ave.v2,5/ucar.cgd.ccsm4.g.b29.01.ocn.proc.annual_ave.v2.xml
ucar.cgd.ccsm.b30.004.ice.proc.monthly_ave.v1,1/ucar.cgd.ccsm.b30.004.ice.proc.monthly_ave.v1.xml
cmip5.output1.NCAR.CCSM4.rcp45.mon.atmos.Amon.r4i1p1.v1,7/cmip5.output1.NCAR.CCSM4.rcp45.mon.atmos.Amon.r4i1p1.v1.xml
ucar.cgd.ccsm.b30.031.ice_sh.proc.monthly_ave.v1,1/ucar.cgd.ccsm.b30.031.ice_sh.proc.monthly_ave.v1.xml
cmip5.output1.NCAR.CCSM4.rcp85.mon.atmos.Amon.r4i1p1.v1,7/cmip5.output1.NCAR.CCSM4.rcp85.mon.atmos.Amon.r4i1p1.v1.xml
narccap.crcm.ccsm-future.table2.v2,6/narccap.crcm.ccsm-future.table2.v2.xml
narccap.crcm.ccsm-current.table2.v2,6/narccap.crcm.ccsm-current.table2.v2.xml
narccap.wrfg.ccsm-future.table1.v3,6/narccap.wrfg.ccsm-future.table1.v3.xml
ucar.cgd.ccsm.b30.031.ocn.proc.monthly_ave.v1,1/ucar.cgd.ccsm.b30.031.ocn.proc.monthly_ave.v1.xml
ucar.cgd.ccsm.b30.032.ocn.proc.monthly_ave.v1,1/ucar.cgd.ccsm.b30.032.ocn.proc.monthly_ave.v1.xml
cmip5.output1.NCAR.CCSM4.rcp26.mon.atmos.Amon.r2i1p1.v1,7/cmip5.output1.NCAR.CCSM4.rcp26.mon.atmos.Amon.r2i1p1.v1.xml
narccap.crcm.ncep.table1.v2,5/narccap.crcm.ncep.table1.v2.xml
narccap.wrfg.ccsm-future.table3.v2,6/narccap.wrfg.ccsm-future.table3.v2.xml
cmip5.output1.NCAR.CCSM4.historical.mon.atmos.Amon.r5i1p1.v1,7/cmip5.output1.NCAR.CCSM4.historical.mon.atmos.Amon.r5i1p1.v1.xml
narccap.mm5i.ccsm-future.table3.v2,7/narccap.mm5i.ccsm-future.table3.v2.xml
ucar.cgd.ccsm.b30.032a.ocn.proc.monthly_ave.v1,1/ucar.cgd.ccsm.b30.032a.ocn.proc.monthly_ave.v1.xml
ucar.cgd.ccsm.b30.004.ocn.proc.monthly_ave.v1,1/ucar.cgd.ccsm.b30.004.ocn.proc.monthly_ave.v1.xml
narccap.hrm3.ncep.table3.v4,7/narccap.hrm3.ncep.table3.v4.xml
narccap.hrm3.ncep.table3.v3,7/narccap.hrm3.ncep.table3.v3.xml
ucar.cgd.ccsm.b30.031.ocn.proc.annual_ave.v1,1/ucar.cgd.ccsm.b30.031.ocn.proc.annual_ave.v1.xml
ucar.cgd.ccsm.b30.026b.atm.proc.monthly_ave.v1,1/ucar.cgd.ccsm.b30.026b.atm.proc.monthly_ave.v1.xml
ucar.cgd.ccsm.b30.030e.atm.proc.6hourly_ave.v1,6/ucar.cgd.ccsm.b30.030e.atm.proc.6hourly_ave.v1.xml
narccap.wrfg.cgcm3-current.table3.v1,6/narccap.wrfg.cgcm3-current.table3.v1.xml
ucar.cgd.ccsm.b30.025.ES01.ocn.proc.annual_ave.v1,1/ucar.cgd.ccsm.b30.025.ES01.ocn.proc.annual_ave.v1.xml
cmip5.output1.NCAR.CCSM4.rcp26.mon.atmos.Amon.r5i1p1.v1,7/cmip5.output1.NCAR.CCSM4.rcp26.mon.atmos.Amon.r5i1p1.v1.xml
narccap.crcm.ccsm-future.table3.v1,7/narccap.crcm.ccsm-future.table3.v1.xml
ucar.cgd.ccsm.b30.032b.ocn.proc.monthly_ave.v1,1/ucar.cgd.ccsm.b30.032b.ocn.proc.monthly_ave.v1.xml
narccap.mm5i.ccsm-current.table3.v4,7/narccap.mm5i.ccsm-current.table3.v4.xml
narccap.wrfg.cgcm3-current.table2.v1,6/narccap.wrfg.cgcm3-current.table2.v1.xml
narccap.mm5i.ncep.table2.v3,5/narccap.mm5i.ncep.table2.v3.xml
narccap.crcm.cgcm3-future.table2.v2,5/narccap.crcm.cgcm3-future.table2.v2.xml
ucar.cgd.ccsm.b30.009.atm.proc.monthly_ave.v1,1/ucar.cgd.ccsm.b30.009.atm.proc.monthly_ave.v1.xml
ucar.cgd.ccsm4.b40.1850.track1.2deg.003.atm.proc.monthly_ave.v1,4/ucar.cgd.ccsm4.b40.1850.track1.2deg.003.atm.proc.monthly_ave.v1.xml
ucar.cgd.ccsm.b30.032b.ocn.proc.annual_ave.v1,1/ucar.cgd.ccsm.b30.032b.ocn.proc.annual_ave.v1.xml
ucar.cgd.pcm.B07.58.atm.proc.daily_ave.v1,4/ucar.cgd.pcm.B07.58.atm.proc.daily_ave.v1.xml
cmip5.output1.NCAR.CCSM4.rcp85.mon.atmos.Amon.r6i1p1.v1,7/cmip5.output1.NCAR.CCSM4.rcp85.mon.atmos.Amon.r6i1p1.v1.xml
narccap.ecp2.gfdl-current.table2.v1,7/narccap.ecp2.gfdl-current.table2.v1.xml
narccap.crcm.cgcm3-current.table1.v7,5/narccap.crcm.cgcm3-current.table1.v7.xml
cmip5.output1.NCAR.CCSM4.rcp45.mon.atmos.Amon.r3i1p1.v1,7/cmip5.output1.NCAR.CCSM4.rcp45.mon.atmos.Amon.r3i1p1.v1.xml

	 
	 
	 */
	Map<String, String> catalogs = new HashMap<String, String>();
	Set<String> prefixes = new HashSet<String>();
	List<String> goodcats;
	
	@Override
	public void startDocument() throws SAXException {
		goodcats = new ArrayList<String>(Arrays.asList(gc));
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ( qName.equals("catalogRef")) {
			String name = attributes.getValue("xlink:title");
			String catalog = attributes.getValue("xlink:href");
			String[] parts = name.split("\\.");
			StringBuilder prefix = new StringBuilder("");
			int end = Math.min(parts.length, 4);
			for (int i = 0; i < end; i++) {
				prefix.append(parts[i]);
				if ( i < end - 1 ) prefix.append(".");
			}
			prefixes.add(prefix.toString());
			
//			if (goodcats.contains(name)) catalogs.put(name, catalog);
			catalogs.put(name, catalog);
		}
	}

	public Map<String, String> getCatalogs() {
		return catalogs;
	}

	@Override
	public void endDocument() throws SAXException {
		System.out.println("String[] ncar_prefixes = new String[] { ");
		for (Iterator preIt = prefixes.iterator(); preIt.hasNext();) {
			String prfix = (String) preIt.next();
			System.out.print("\""+prfix+"\"");
			if ( preIt.hasNext() ) System.out.println(",");
		}
		System.out.println("\n};");
	}

	
	

}
