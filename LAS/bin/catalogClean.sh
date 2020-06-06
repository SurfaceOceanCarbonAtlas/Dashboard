#!/bin/sh
#
ADDXML_HOME=""
if [ "$JAVA_HOME" == "" ]
then
   echo "Please set the JAVA_HOME environment variable"
   exit
fi
if [ "$ADDXML_HOME" == "" ]
then
   echo "Please edit the catalogClean.sh file and set the home directory of your addXML installation."
   exit
fi
$JAVA_HOME/bin/java -cp $ADDXML_HOME/dist/lib/addXML.jar:$ADDXML_HOME/dist/lib/ gov.noaa.pmel.tmap.addxml.Cleaner "$@"
