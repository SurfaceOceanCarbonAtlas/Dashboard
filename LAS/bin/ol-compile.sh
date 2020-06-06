#!/bin/sh
APPDIR=`dirname $0`;
sed -e 's/The nifty printing utility//' -e "s;<inherits name='br.com.freller.tool.PrintTest'/>;;" -e "s/not needed for this code, but other things in the package need it.//" -e 's;<inherits name="com.google.gwt.maps.GoogleMaps" />;;' < $APPDIR/../src/com/weathertopconsulting/olmapwidget/NativeMapWidget.gwt.xml > native.xml
mv native.xml $APPDIR/../src/com/weathertopconsulting/olmapwidget/NativeMapWidget.gwt.xml
java  -Xmx256M -cp "$APPDIR/../src:$APPDIR/../lib/gwtopenlayers.jar:/home/porter/rhs/gwt/gwt-user.jar:/home/porter/rhs/gwt/gwt-dev.jar" com.google.gwt.dev.Compiler -war "$APPDIR/../war/JavaScript/components" "$@" com.weathertopconsulting.olmapwidget.NativeMapWidget;
