#!/bin/sh
#
if [ ! -d JavaSource ]; then
echo "Must be run from LAS home."
exit 1
fi
REV=`svnversion -n`
if [ "$?" -ne "0" ]; then
       echo "Subversion not installed.  That is ok..."
       REV="No version information available."
fi
echo -n "#set (\$revision=\"$REV\")" > WebContent/productserver/templates/revision.vm

echo '<h3>Subversion Info:</h3>' > WebContent/productserver/templates/svn.vm
echo '<pre>' >> WebContent/productserver/templates/svn.vm
svn info >> WebContent/productserver/templates/svn.vm
if [ "$?" -ne "0" ]; then
       echo "Subversion not installed.  That is ok..." >> WebContent/productserver/templates/info_tmp.vm
fi
echo '</pre>' >> WebContent/productserver/templates/svn.vm
