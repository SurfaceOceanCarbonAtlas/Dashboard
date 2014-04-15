#! /bin/sh
export DASHBOARD_INGEST="/home/flat/ksmith/workspace/SocatUploadDashboard/ingest-classes"
export SOCATJAVAPROGRAMS="/home/flat/ksmith/workspace/SocatJavaPrograms"
export DASHBOARD_WEBINF="/home/flat/ksmith/workspace/SocatUploadDashboard/war/WEB-INF"
export CLASSPATH="${DASHBOARD_INGEST}:${SOCATJAVAPROGRAMS}:${DASHBOARD_WEBINF}/classes:${DASHBOARD_WEBINF}/lib/*"
java gov.noaa.pmel.socat.dashboard.ingest.Socat2Transfer "$@"
