#! /bin/csh
if ( $# != 2 ) then
    echo ""
    echo "Usage:  $0  ABCDE_expocodes_file  ABCDE_MetadataDocs"
    echo ""
    echo "    ABCDE_expocodes_file - list of expocodes of A-E (good) datasets"
    echo "    ABCDE_MetadataDocs - copy of the directory MetadataDocs which will have non-ABCDE (bad) datasets removed"
    echo ""
    exit 1
endif

set goodexpocodesfile = $1
set metadatadocsdir = $2

foreach metadatadir ( ${metadatadocsdir}/*/* )
    set expocode = "${metadatadir:t}"
    grep -q "${expocode}" "${goodexpocodesfile}"
    if ( $status != 0 ) then
        echo "/bin/rm -fr '${metadatadir}'"
    endif
end

foreach datafile ( ${metadatadocsdir}/*/*/* )
    set extension = "${datafile:e}"
    if ( "${extension}" == "properties" ) then
        echo "/bin/rm '${datafile}'"
    else 
        set filename = "${datafile:t}"
        if ( "${filename}" == "OME.xml" ) then
            echo "/bin/rm '${datafile}'"
        endif
    endif
end

