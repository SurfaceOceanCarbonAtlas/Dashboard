#! /bin/sh
if [ -n "${CATALINA_BASE}" ]; then
    DASHBOARD_WEBINF="${CATALINA_BASE}/webapps/SocatUploadDashboard/WEB-INF"
elif [ -n "${CATALINA_HOME}" ]; then
    CATALINA_BASE="${CATALINA_HOME}"
    DASHBOARD_WEBINF="${CATALINA_HOME}/webapps/SocatUploadDashboard/WEB-INF"
else
    CATALINA_BASE="${HOME}"
    DASHBOARD_WEBINF="${HOME}/workspace/SocatUploadDashboard/war/WEB-INF"
fi

if [ ! -d "${CATALINA_BASE}/content" ]; then
    echo ''
    echo '${CATALINA_BASE}/content not found'
    echo ''
    exit 1
fi
if [ ! -d "${DASHBOARD_WEBINF}/classes" ]; then
    echo ''
    echo '${DASHBOARD_WEBINF}/classes not found'
    echo ''
    exit 1
fi
if [ ! -d "${DASHBOARD_WEBINF}/lib" ]; then
    echo ''
    echo '${DASHBOARD_WEBINF}/lib not found'
    echo ''
    exit 1
fi

export CLASSPATH="${DASHBOARD_WEBINF}/classes:${DASHBOARD_WEBINF}/lib/*"
java -Dcatalina.base="${CATALINA_BASE}" gov.noaa.pmel.socat.dashboard.server.DashboardDataStore "$@"
