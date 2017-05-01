# OAPDashboard
The Upload Dashboard for the Ocean Acidification Project.


This project is derived from the Upload Dashboard of the
Surface Ocean Carbon Atlas (SOCAT) Project.  The link to
the SOCAT Upload Dashboard is maintained so as to easily
incorporate updates from that project.  However, this
dashboard will diverge from the SOCAT dashboard as this
project incorporates profile data, trajectory-profile data,
more data types, and different requirements on uploaded
data.


## Building the war file
### Add missing jar files
This project is an Eclipse IDE project, uses the GWT plugin
for Eclipse, and building the war file is done through Eclipse.
(Maybe one day there will be a script.)  After import the
projects (one for OAPOmeMetadata, the other OAPUploadDashboard),
a jar file is created from the uk.ac.uea.socat.omemetadata
package using the Eclipse `File -> Export -> Java -> JAR file`
option.  This jar file is saved as `war/WEB-INF/lib/omemetadata.jar`
under the OAPUploadDashboard project.  The `gwt-servlet.jar`
file from the GWT plugin (for GWT 2.7.0, it is under the
`plugins/com.google.gwt.eclipse.sdkbundle_2.7.0/gwt-2.7.0/`
subdirectory of the Eclipse installation) is copied to the
`war/WEB-INF/lib/` subdirectory of the OAPUploadDashboard project.
This should resolve the missing resources or libraries issues
in Eclipse.

### Creating the war file
Use the GWT menu item in Eclipse to GWT Compile the OAPUploadDashboard
project.  The war file is then created by using the Eclipse
`File -> Export -> Archive File` option with everything under the
war subdirectory (but not the war directory itself) of the 
OAPUploadDashboard project, saving it in zip format, compressing
the contents of the file, and creating only selected directories.


## Configuration for using the UploadDashboard
