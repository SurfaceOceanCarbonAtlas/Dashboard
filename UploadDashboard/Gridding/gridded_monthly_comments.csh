#!/bin/csh 
#

ncatted -O -h -a title,global,o,c,'SOCAT gridded v2020 Monthly 1x1 degree gridded dataset' SOCAT_tracks_gridded_monthly.nc
ncatted -O -h -a summary,global,o,c,'Surface Ocean Carbon Atlas (SOCAT) Gridded (binned) SOCAT observations, with a spatial \ngrid of 1x1 degree and monthly in time. The gridded fields are computed using only SOCAT \ndatasets with QC flags of A through D and SOCAT data points flagged with WOCE flag values of 2.' SOCAT_tracks_gridded_monthly.nc
ncatted -O -h -a references,global,o,c,'http://www.socat.info/' SOCAT_tracks_gridded_monthly.nc
ncatted -O -h -a SOCAT_Notes,global,o,c,'SOCAT gridded v2020 05-June-2020' SOCAT_tracks_gridded_monthly.nc
ncatted -O -h -a caution,global,o,c,'NO INTERPOLATION WAS PERFORMED. SIGNIFICANT BIASES ARE PRESENT IN THESE GRIDDED RESULTS DUE TO THE \nARBITRARY AND SPARSE LOCATIONS OF DATA VALUES IN BOTH SPACE AND TIME.' SOCAT_tracks_gridded_monthly.nc

ncatted -O -h -a summary,count_ncruise,o,c,'Number of datasets containing observations in the grid cell' SOCAT_tracks_gridded_monthly.nc
ncatted -O -h -a summary,fco2_count_nobs,o,c,'Total number of observations in the grid cell.' SOCAT_tracks_gridded_monthly.nc
ncatted -O -h -a summary,fco2_ave_unwtd,o,c,'Arithmetic mean of all fco2 recomputed values found in the grid cell.' SOCAT_tracks_gridded_monthly.nc
ncatted -O -h -a summary,fco2_ave_weighted,o,c,'Mean of fco2 recomputed computed by calculating the arithmetic mean value for each cruise passing through the cell and then averaging these datasets.' SOCAT_tracks_gridded_monthly.nc
ncatted -O -h -a summary,fco2_min_unwtd,o,c,'Maximum value of fco2 recomputed observed in the grid cell.' SOCAT_tracks_gridded_monthly.nc
ncatted -O -h -a summary,fco2_min_unwtd,o,c,'Minimum value of fco2 recomputed observed in the grid cell.' SOCAT_tracks_gridded_monthly.nc
ncatted -O -h -a summary,fco2_std_unwtd,o,c,'The standard deviation of fco2 recomputed computed from the unweighted mean.' SOCAT_tracks_gridded_monthly.nc
ncatted -O -h -a summary,fco2_std_weighted,o,c,'A weighted standard deviation of fco2 recomputed computed to account for the differing \nvariance estimates for each cruise passing through the cell. The statistical technique is \ndescribed at See http://wapedia.mobi/en/Weighted_mean#7.' SOCAT_tracks_gridded_monthly.nc

ncatted -O -h -a summary,sst_count_nobs,o,c,'Total number of observations in the grid cell.' SOCAT_tracks_gridded_monthly.nc
ncatted -O -h -a summary,sst_ave_unwtd,o,c,'Arithmetic mean of all sst values found in the grid cell.' SOCAT_tracks_gridded_monthly.nc
ncatted -O -h -a summary,sst_ave_weighted,o,c,'Mean of sst computed by calculating the arithmetic mean value for each cruise passing through the cell and then averaging these datasets.' SOCAT_tracks_gridded_monthly.nc
ncatted -O -h -a summary,sst_min_unwtd,o,c,'Maximum value of sst observed in the grid cell.' SOCAT_tracks_gridded_monthly.nc
ncatted -O -h -a summary,sst_min_unwtd,o,c,'Minimum value of sst observed in the grid cell.' SOCAT_tracks_gridded_monthly.nc
ncatted -O -h -a summary,sst_std_unwtd,o,c,'The standard deviation of sst computed from the unweighted mean.' SOCAT_tracks_gridded_monthly.nc
ncatted -O -h -a summary,sst_std_weighted,o,c,'A weighted standard deviation of sst computed to account for the differing \nvariance estimates for each cruise passing through the cell. The statistical technique is \ndescribed at See http://wapedia.mobi/en/Weighted_mean#7.' SOCAT_tracks_gridded_monthly.nc

ncatted -O -h -a summary,salinity_count_nobs,o,c,'Total number of observations in the grid cell.' SOCAT_tracks_gridded_monthly.nc
ncatted -O -h -a summary,salinity_ave_unwtd,o,c,'Arithmetic mean of all salinity values found in the grid cell.' SOCAT_tracks_gridded_monthly.nc
ncatted -O -h -a summary,salinity_ave_weighted,o,c,'Mean of salinity computed by calculating the arithmetic mean value for each cruise passing through the cell and then averaging these datasets.' SOCAT_tracks_gridded_monthly.nc
ncatted -O -h -a summary,salinity_min_unwtd,o,c,'Maximum value of salinity observed in the grid cell.' SOCAT_tracks_gridded_monthly.nc
ncatted -O -h -a summary,salinity_min_unwtd,o,c,'Minimum value of salinity observed in the grid cell.' SOCAT_tracks_gridded_monthly.nc
ncatted -O -h -a summary,salinity_std_unwtd,o,c,'The standard deviation of salinity computed from the unweighted mean.' SOCAT_tracks_gridded_monthly.nc
ncatted -O -h -a summary,salinity_std_weighted,o,c,'A weighted standard deviation of salinity computed to account for the differing \nvariance estimates for each cruise passing through the cell. The statistical technique is \ndescribed at See http://wapedia.mobi/en/Weighted_mean#7.' SOCAT_tracks_gridded_monthly.nc

ncatted -O -h -a summary,lat_offset_unwtd,o,c,'The arithmetic average of latitude offsets from the grid cell center for all observations in \nthe grid cell. The value of this offset can vary from -0.5 to 0.5. A value of zero indicates \nthat the computed fco2 mean values are representative of the grid cell center position.' SOCAT_tracks_gridded_monthly.nc
ncatted -O -h -a summary,lon_offset_unwtd,o,c,'The arithmetic average of longitude offsets from the grid cell center for all observations in \nthe grid cell. The value of this offset can vary from -0.5 to 0.5. A value of zero indicates \nthat the computed fco2 mean values are representative of the grid cell center position.' SOCAT_tracks_gridded_monthly.nc

ncdump -h SOCAT_tracks_gridded_monthly.nc
