java -jar compiler.jar --compilation_level=WHITESPACE_ONLY --js=WebContent/JavaScript/ui/LASUI.js --js_output_file=WebContent/JavaScript/ui/LASUI_compiled.js
#cp ui/LASUI.js ui/LASUI_compiled.js
java -jar compiler.jar --js=WebContent/JavaScript/components/xmldom.js --js=WebContent/JavaScript/components/LASRequest.js --js=WebContent/JavaScript/components/DateWidget.js --js=WebContent/JavaScript/components/LASGetGridResponse.js --js=WebContent/JavaScript/components/LASGetCategoriesResponse.js --js=WebContent/JavaScript/components/LASGetViewsResponse.js --js=WebContent/JavaScript/components/LASGetOperationsResponse.js --js=WebContent/JavaScript/components/LASGetOptionsResponse.js --js=WebContent/JavaScript/components/LASGetDataConstraintsResponse.js --js_output_file=WebContent/JavaScript/ui/LASUI_compiled_components.js
