#

.PHONY: configure
configure:
	configure

.PHONY: clean-ant
clean-ant:
	ant clean

.PHONY: clean-noant
clean-noant:
	rm -rf velocity.log
	rm -rf config.results
	rm -rf bin/las_ui_check.sh
	rm -rf bin/initialize_check.sh
	rm -rf conf/example/LAS_Config.pl
	rm -rf conf/example/sample_las.xml
	rm -rf conf/example/sample_ui.xml
	rm -rf conf/example/sample_insitu_las.xml
	rm -rf conf/example/sample_insitu_ui.xml
	rm -rf conf/example/productserver.xml
	rm -rf conf/example/LAS_config.pl
	rm -rf startserver.sh rebootserver.sh stopserver.sh
	rm -rf JavaSource/resources/ferret/FerretBackendConfig.xml
	rm -rf JavaSource/resources/database/DatabaseBackendConfig.xml
	rm -rf JavaSource/resources/ferret/FerretBackendConfig.xml.base
	rm -rf WebContent/fds/fds.xml
	rm -rf WebContent/classes
	rm -rf WebContent/docs
	rm -rf WebContent/WEB-INF/classes
	rm -rf WebContent/WEB-INF/struts-config.xml
	rm -rf xml/perl/genLas.pl
	rm -rf build.xml

.PHONY: clean 
clean: clean-ant clean-noant
