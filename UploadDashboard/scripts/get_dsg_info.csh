#! /bin/csh
set currvers = "2020.0"
set dsgsdir = "${HOME}/Tomcat/content/SocatUploadDashboard/DsgNcFiles"
if ( $# != 2 ) then
    echo ""
    echo "Usage: $0 expocodes_file ignore_prev"
    echo ""
    echo "    expocodes_file - list of expocodes of datasets to examine"
    echo "    ignore_prev - if one of T, t, Y, or y, then ignore any "
    echo "                  datasets that are not SOCAT version ${currvers}"
    echo ""
    echo "    Prints some of the metadata-type information contained "
    echo "    in the DSG files of the specified datasets"
    echo ""
    exit 1
endif
set expocodesfile = $1
if ( ("$2" == "T") || ("$2" == 't') || ("$2" == 'Y') || ("$2" == 'y') ) then
    set ignoreprev = 1
else
    set ignoreprev = 0
endif

# echo "expocode\tversion\tqc_flag\tnum_obs"
echo "expocode\tversion\tqc_flag\tnum_obs\tplatform\tinvestigators\torganizations"
foreach expocode (`cat $expocodesfile`)
    set nodc = `echo $expocode | awk '{printf "%.4s",$1}'`
    set ncfile = ${dsgsdir}/${nodc}/${expocode}.nc
    set vers = `ncdump -v socat_version ${ncfile} | awk -F'"' '/socat_version =/ {getline; print $2}'`
    if ( $ignoreprev != 0 ) then
        if ( ("$vers" != "${currvers}N") && ("$vers" != "${currvers}U") ) continue
    endif
    set expo = `ncdump -v expocode ${ncfile} | awk -F'"' '/expocode =/ {getline; print $2}'`
    set qc = `ncdump -v qc_flag ${ncfile} | awk -F'"' '/qc_flag =/ {getline; print $2}'`
    set num_obs = `ncdump -v num_obs ${ncfile} | awk '/num_obs = / {print $3}'`
    set platform = `ncdump -v platform_name ${ncfile} | awk -F'"' '/platform_name =/ {getline; print $2}'`
    set investigators = `ncdump -v investigators ${ncfile} | awk -F'"' '/investigators =/ {getline; print $2}'`
    set organizations = `ncdump -v organization ${ncfile} | awk -F'"' '/organization =/ {getline; print $2}'`
#    echo "${expo}\t${vers}\t${qc}\t${num_obs}"
    echo "${expo}\t${vers}\t${qc}\t${num_obs}\t${platform}\t${investigators}\t${organizations}"
end
