
0. First look at all the scripts and makes sure the version number, dates,
   and lengths of time axes are what they should be for this SOCAT version.
   The script define_grid_time_axes.jnl is called by scripts to set up the
   monthly time axis.  Run it and check the axis. Check the count for data
   in the triples file. Also update the shell scripts (step 4) for the version
   number and date of release.

1. Run triples2nc.jnl to convert the tsv file into a netCDF file
   This will take a couple of minutes to run.
   Make sure that the variables this script defines and writes to the nc file
   have correct units - use the same units as in the ungridded colection.

2. Write the monthly 1-degree file and a report summarizing data statistics.
   - Run tracks2grid_ave_fco2_xyt_stats.jnl
   - Run socat_decadal.jnl
   - Run socat_yearly.jnl

3. Write the coastal quarter-degree files.
   - Run tracks2grid_monthly_quarterdeg.jnl, writes a set of quarter-degree
       files and a report summarizing some data statistics
   - Run mask_deflate.jnl to apply the coastal mask and save a single monthly
       coastal file with deflation. The script writes one variable then has a
       pause: check time axis.

4. Run the shell scripts to add global and variable summary attributes to the files.
   - gridded_coastal_monthly_comments.csh
   - gridded_decadal_comments.csh
   - gridded_monthly_comments.csh
   - gridded_yearly_comments.csh

