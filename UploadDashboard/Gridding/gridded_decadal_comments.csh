#!/bin/csh 
#

ncatted -O -h -a title,global,o,c,'SOCAT gridded v2020 Decadal 1x1 degree gridded dataset' SOCAT_tracks_gridded_decadal.nc
ncatted -O -h -a summary,global,o,c,'Surface Ocean Carbon Atlas (SOCAT) Gridded (binned) SOCAT observations, with a spatial grid of \n1x1 degree and yearly in time. The gridded fields are computed from the monthly 1-degree gridded data, \nwhich uses only SOCAT datasets with QC flags of A through D and SOCAT data points flagged with WOCE \nflag values of 2. This decacal data is computed using data from the start to the end of each decade as \ndescribed in the summary attribute of each variable. The first decade is 1-Jan-1970 through 31-Dec-1979, \nthe last decade is generally a partial decade.' SOCAT_tracks_gridded_decadal.nc
ncatted -O -h -a references,global,o,c,'http://www.socat.info/' SOCAT_tracks_gridded_decadal.nc
ncatted -O -h -a SOCAT_Notes,global,o,c,'SOCAT gridded v2020 05-June-2020' SOCAT_tracks_gridded_decadal.nc
ncatted -O -h -a caution,global,o,c,'NO INTERPOLATION WAS PERFORMED. SIGNIFICANT BIASES ARE PRESENT IN THESE GRIDDED RESULTS DUE TO THE \nARBITRARY AND SPARSE LOCATIONS OF DATA VALUES IN BOTH SPACE AND TIME.' SOCAT_tracks_gridded_decadal.nc

ncatted -O -h -a summary,count_ncruise_decade,o,c,'Counts cruises which returned any data in the month and grid cell. The cruise counts within each decade are summed.' SOCAT_tracks_gridded_decadal.nc
ncatted -O -h -a summary,fco2_count_nobs_decade,o,c,'Counts all observations in the month and grid cell. The observation counts within each decade are summed.' SOCAT_tracks_gridded_decadal.nc
ncatted -O -h -a summary,fco2_ave_unwtd_decade,o,c,'Mean of all observations from all cruises. The means for the months within each decade are averaged.' SOCAT_tracks_gridded_decadal.nc
ncatted -O -h -a summary,fco2_ave_weighted_decade,o,c,'The weighted cruise means for the months within each decade is averaged. ' SOCAT_tracks_gridded_decadal.nc
ncatted -O -h -a summary,fco2_max_unwtd_decade,o,c,'The maximum monthly value for the decade.' SOCAT_tracks_gridded_decadal.nc
ncatted -O -h -a summary,fco2_min_unwtd_decade,o,c,'The minimum monthly value for the decade.' SOCAT_tracks_gridded_decadal.nc

ncatted -O -h -a summary,sst_count_nobs_decade,o,c,'Counts all observations in the month and grid cell. The observation counts within each decade are summed.' SOCAT_tracks_gridded_decadal.nc
ncatted -O -h -a summary,sst_ave_unwtd_decade,o,c,'Mean of all observations from all cruises. The means for the months within each decade are averaged.' SOCAT_tracks_gridded_decadal.nc
ncatted -O -h -a summary,sst_ave_weighted_decade,o,c,'The weighted cruise means for the months within each decade is averaged. ' SOCAT_tracks_gridded_decadal.nc
ncatted -O -h -a summary,sst_max_unwtd_decade,o,c,'The maximum monthly value for the decade.' SOCAT_tracks_gridded_decadal.nc
ncatted -O -h -a summary,sst_min_unwtd_decade,o,c,'The minimum monthly value for the decade.' SOCAT_tracks_gridded_decadal.nc

ncatted -O -h -a summary,salinity_count_nobs_decade,o,c,'Counts all observations in the month and grid cell. The observation counts within each decade are summed.' SOCAT_tracks_gridded_decadal.nc
ncatted -O -h -a summary,salinity_ave_unwtd_decade,o,c,'Mean of all observations from all cruises. The means for the months within each decade are averaged.' SOCAT_tracks_gridded_decadal.nc
ncatted -O -h -a summary,salinity_ave_weighted_decade,o,c,'The weighted cruise means for the months within each decade is averaged. ' SOCAT_tracks_gridded_decadal.nc
ncatted -O -h -a summary,salinity_max_unwtd_decade,o,c,'The maximum monthly value for the decade.' SOCAT_tracks_gridded_decadal.nc
ncatted -O -h -a summary,salinity_min_unwtd_decade,o,c,'The minimum monthly value for the decade.' SOCAT_tracks_gridded_decadal.nc

ncdump -h SOCAT_tracks_gridded_decadal.nc
