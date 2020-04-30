#! /bin/csh
cd ${HOME}/Tomcat/content/SocatUploadDashboard/
rm -f tmp.$$
foreach f ( CruiseFiles/*/*.tsv )
    echo $f:r:t >>! tmp.$$
end
mv -f tmp.$$ all_expos.txt
foreach f ( DsgNcFiles/*/*.nc )
    echo $f:r:t >>! tmp.$$
end
mv -f tmp.$$ all_nc_expos.txt
