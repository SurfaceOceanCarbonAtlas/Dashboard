#! /bin/sh
export UPLOAD_DASHBOARD_SERVER_NAME="SocatUploadDashboard"
export CATALINA_BASE="${HOME}/Tomcat"
export CATALINA_HOME="${CATALINA_BASE}"
export DASHBOARD_WEBINF="${CATALINA_HOME}/webapps/SocatUploadDashboard/WEB-INF"
export DASHBOARDAPPS_JAR="${CATALINA_HOME}/content/SocatUploadDashboard/scripts/SocatDashboardApps.jar"

if [ ! -r "${DASHBOARDAPPS_JAR}" ]; then
    echo ""
    echo "   \${DASHBOARDAPPS_JAR} (${DASHBOARDAPPS_JAR}) not found"
    echo ""
    exit 1
fi
if [ ! -d "${DASHBOARD_WEBINF}/classes" ]; then
    echo ""
    echo "    \${DASHBOARD_WEBINF}/classes (${DASHBOARD_WEBINF}/classes) not found"
    echo ""
    exit 1
fi
if [ ! -d "${DASHBOARD_WEBINF}/lib" ]; then
    echo ""
    echo "    \${DASHBOARD_WEBINF}/lib (${DASHBOARD_WEBINF}/lib) not found"
    echo ""
    exit 1
fi

export CLASSPATH="${DASHBOARDAPPS_JAR}:${DASHBOARD_WEBINF}/classes:${DASHBOARD_WEBINF}/lib/*"
java -Dcatalina.base="${CATALINA_BASE}" gov.noaa.pmel.dashboard.programs.RecheckDatasets "$@"