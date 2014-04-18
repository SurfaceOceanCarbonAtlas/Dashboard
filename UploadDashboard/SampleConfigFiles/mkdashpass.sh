#! /bin/sh
if [ -n "${DASHBOARD_WEBINF}" ]; then
    if [ -z "${CATALINA_HOME}" ]; then
        export CATALINA_HOME="${HOME}"
    fi
elif [ -n "${CATALINA_HOME}" ]; then
    export DASHBOARD_WEBINF="${CATALINA_HOME}/webapps/SocatUploadDashboard/WEB-INF"
else
    echo ''
    echo 'Define DASHBOARD_WEBINF to be the full path of the WEB_INF '
    echo 'directory under webapps/SocatUploadDashboard.  The '
    echo 'content/SocatUploadDashboard/SocatUploadDashboard.properties '
    echo 'file under $CATALINA_HOME, if defined, or $HOME will used for '
    echo 'encryption keys. '
    echo ''
    echo 'Alternatively define CATALINA_HOME to be the full path of '
    echo 'the tomcat home directory containing webapps, in which case '
    echo '$CATALINA_HOME/webapps/SocatUploadDashboard/WEB-INF will be '
    echo 'used for DASHBOARD_WEBINF and, as before, the dashboard '
    echo 'properties file under this directory will be used for the '
    echo 'encryption keys. '
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
java -Dcatalina.base="${CATALINA_HOME}" gov.noaa.pmel.socat.dashboard.server.DashboardDataStore "$@"
