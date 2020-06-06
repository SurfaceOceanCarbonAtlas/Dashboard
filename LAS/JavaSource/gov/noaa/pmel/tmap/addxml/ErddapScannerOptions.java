package gov.noaa.pmel.tmap.addxml;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class ErddapScannerOptions extends Options {
    
    public ErddapScannerOptions() {
        Option url = new Option("u", "url", true, "The base URL of the tabledap section of the ERDDAP server (ends in \"/tabledap\")");
        url.setRequired(true);
        addOption(url);
        
        Option verbose = new Option("v", "verbose", false, "Verbose output while processing.");
        addOption(verbose);
        
        Option title = new Option("t", "title", true, "The title to use for the category for these data. Only used when ID is not specified.");
        addOption(title);
        
        Option id = new Option("i", "id", true, "The ERDDAP ID of the trajectory data set (see the \"/tabledap\" page on the server.");
        addOption(id);
        
        Option axes = new Option("a", "axes", true, "By default, program will attempt to download the range of an axis. Use this option list which axes (xyzt) to skip.");
        addOption(axes);
        
        Option category = new Option("c", "category", true, "Read a category file and use it to organized the data sets.");
        addOption(category);
        
        Option hours = new Option("h", "hours", true, "Use the value as the step in hours instead of the default step in days");
        addOption(hours);
        
        Option minutes = new Option("m", "minutes", false, "Use the value as the step in minutes instead of the default step in days");
        addOption(minutes);
        
        Option property = new Option("p", "property", true, "A property to add to the data set of the form group:name:value, e.g. -p product_server:default_operation:Timeseries_station_plot");
        addOption(property);
        
        Option varproperty = new Option("r", "varprop", true, "A property to add to the named variable of the form variable:group:name:value, e.g. -r variable_name:ferret:dep_axis_scale:\"0.5,3.5,0.5\"");
        addOption(varproperty);
        
        Option skip = new Option("s", "skip", true, "The short name of a variable to leave out of the LAS configuration.");
        addOption(skip);
        
        Option display = new Option("d", "display", true, "Dates to display by default. Earliest or only is used as display_lo. Latest used as display_hi. -d 15-Oct-2015 -d 31-Oct-2015");
        display.setOptionalArg(true);
        addOption(display);
        
        Option file = new Option("f", "files", false, "Write output in separate files.");
        file.setOptionalArg(true);
        addOption(file);
    }

}
