#

.PHONY: configure
configure:
	configure

.PHONY: clean-ant
clean-ant:
	ant clean

.PHONY: clean-noant
clean-noant:
	rm -f  JavaSource/log4j.xml
	rm -f  JavaSource/log4j2.xml
	rm -f  JavaSource/log4j2.properties
	rm -f  JavaSource/resources/database/DatabaseBackendConfig.xml
	rm -f  JavaSource/resources/ferret/FerretBackendConfig.xml
	rm -f  JavaSource/resources/ferret/FerretBackendConfig.xml.base
	rm -f  JavaSource/resources/ferret/FerretBackendConfig.xml.old
	rm -f  JavaSource/resources/ferret/FerretBackendConfig.xml.pybase
	rm -f  JavaSource/resources/kml/KMLBackendConfig.xml
	rm -f  WebContent/TestLinks.html
	rm -f  WebContent/WEB-INF/struts-config.xml
	rm -f  WebContent/WEB-INF/web.xml
	rm -f  WebContent/productserver/templates/revision.vm
	rm -f  WebContent/productserver/templates/svn.vm
	rm -f  bin/addDiscrete.sh
	rm -f  bin/addXML.sh
	rm -f  bin/initialize_check.sh
	rm -f  bin/lasTest.sh
	rm -f  bin/las_ui_check.sh
	rm -f  build.xml
	rm -f  conf/example/LAS_config.pl
	rm -f  conf/example/LAS_Config.pl
	rm -f  conf/example/productserver.xml
	rm -f  conf/example/sample_las.xml
	rm -f  conf/example/sample_insitu_las.xml
	rm -f  conf/example/sample_insitu_ui.xml
	rm -f  conf/example/sample_ui.xml
	rm -f  config.results
	rm -f  rebootserver.sh
	rm -f  startserver.sh
	rm -f  stopserver.sh
	rm -f  test/LASTest/las_test_config.xml
	rm -f  velocity.log
	rm -f  xml/perl/genLas.pl
	rm -fr WebContent/WEB-INF/classes
	rm -fr WebContent/classes
	rm -fr WebContent/docs
	rm -fr build
	rm -fr conf/server
	rm -fr dist

.PHONY: clean-more
clean-more:
	rm -f  config7.results
	rm -fr WebContent/JavaScript/components/WEB-INF
	rm -fr WebContent/JavaScript/components/gov.noaa.pmel.tmap.las.*
	rm -fr WebContent/JavaScript/gwt-unitCache

.PHONY: clean 
clean: clean-ant clean-noant

.PHONY: ultra-clean
ultra-clean: clean-ant clean-noant clean-more

