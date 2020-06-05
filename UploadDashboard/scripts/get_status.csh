#! /bin/csh
set cruisedir="${HOME}/Tomcat/content/SOCATv2020/CruiseFiles"
if ( $# != 1 ) then
    echo ""
    echo "   Usage: $0 expocodes_file"
    echo ""
    echo "       expocodes_file - list of expocodes of datasets to examine"
    echo ""
    echo "   Prints the owner, version, data check status, and submit/qc status"
    echo "   from the data properties file for the dataset specified by each"
    echo "   expocode in the expocodes file"
    echo ""
    exit 1
endif
set expocodesfile = $1

echo "expocode\towner\tversion\tstatus"
foreach expocode (`cat $expocodesfile`)
    set nodc = `echo $expocode | awk '{printf "%.4s",$1}'`
    set propsfile = ${cruisedir}/${nodc}/${expocode}.properties
    set owner = `awk -F'=' '/dataowner=/ {print $2}' ${propsfile}`
    set vers = `awk -F'=' '/version=/ {print $2}' ${propsfile}`
    set dstat = `awk -F'=' '/datacheckstatus=/ {print $2}' ${propsfile}`
    set sstat = `awk -F'=' '/submitstatus=/ {print $2}' ${propsfile}`
    if ( "$sstat" == "" ) then
        set sstat = `awk -F'=' '/qcstatus=/ {print $2}' ${propsfile}`
    endif
    echo "${expocode}\t${owner}\t${vers}\t${dstat}\t${sstat}"
end
