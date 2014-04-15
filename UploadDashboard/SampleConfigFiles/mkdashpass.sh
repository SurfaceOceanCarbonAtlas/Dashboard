#! /bin/sh
if [ -z "${DASHBOARD_WEBINF}" ]; then
    if [ -n "${CATALINA_HOME}" ]; then
        export DASHBOARD_WEBINF="${CATALINA_HOME}/webapps/SocatUploadDashboard/WEB-INF"
    else
        echo ''
        echo 'Define DASHBOARD_WEBINF to be the full path of the'
        echo 'WEB_INF directory under webapps/SocatUploadDashboard'
        echo ''
        echo 'Alternatively define CATALINA_HOME to be the full path'
        echo 'of the tomcat home directory containing webapps in which'
        echo 'case $CATALINA_HOME/webapps/SocatUploadDashboard/WEB-INF'
        echo 'will be used for DASHBOARD_WEBINF'
        echo ''
        exit 1
    fi
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
java gov.noaa.pmel.socat.dashboard.server.DashboardDataStore "$@"
