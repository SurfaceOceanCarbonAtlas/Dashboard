#! /bin/csh
if ( $# != 5 ) then
    echo ""
    echo "Usage:  $0  rm_expocodes_file  CruiseFiles  MetadataDocs  DsgFilesDir  DecDsgFilesDir"
    echo ""
    echo "    rm_expocodes_file - list of expocodes of datasets to remove (F,S,X,unsubmitted)"
    echo "    CruiseFiles - subversion working directory of data files which will have bad datasets removed"
    echo "    MetadataDocs - subversion working directory of metadata files which will have bad datasets removed"
    echo ""
    exit 1
endif

set badexpocodesfile = $1
set datafilesdir = $2
set metadatadocsdir = $3
set dsgfilesdir = $4
set decdsgfilesdir = $5

foreach expocode ( `cat ${badexpocodesfile}` )
    set nodc = `echo ${expocode} | awk '{printf "%.4s",$1}'`
    svn rm --force ${datafilesdir}/${nodc}/${expocode}.{tsv,properties,messages}
    svn rm --force ${metadatadocsdir}/${nodc}/${expocode}
    /bin/rm -f ${dsgfilesdir}/${nodc}/${expocode}.nc
    /bin/rm -f ${decdsgfilesdir}/${nodc}/${expocode}.nc
end

