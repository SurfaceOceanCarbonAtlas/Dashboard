#! /bin/sh
if [ -z "${CATALINA_HOME}" ]; then
    export CATALINA_HOME="${HOME}"
fi
export DASHBOARD_INGEST="/home/flat/ksmith/workspace/SocatUploadDashboard/ingest-classes"
export SOCATJAVAPROGRAMS="/home/flat/ksmith/workspace/SocatJavaPrograms"
export DASHBOARD_WEBINF="/home/flat/ksmith/workspace/SocatUploadDashboard/war/WEB-INF"
export CLASSPATH="${DASHBOARD_INGEST}:${SOCATJAVAPROGRAMS}:${DASHBOARD_WEBINF}/classes:${DASHBOARD_WEBINF}/lib/*"
java -Dcatalina.base=${CATALINA_HOME} gov.noaa.pmel.socat.dashboard.ingest.Socat2Transfer "$@"
