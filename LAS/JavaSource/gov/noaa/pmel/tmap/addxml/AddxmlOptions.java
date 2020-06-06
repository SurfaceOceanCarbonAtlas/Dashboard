package gov.noaa.pmel.tmap.addxml;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class AddxmlOptions extends Options {
    public AddxmlOptions() {

        Option verbose = new Option("v", "verbose", false, "Switch to print out lots of helpful information about what addXML is doing.  Default is off.");
        addOption(verbose);

        Option generate_names = new Option("N", "generate_Names", false, "Switch that causes addXML to read the data source to generate the data set name in the category.  Default is off.");
        addOption(generate_names);

        Option version = Option.builder("V")
                               .desc("Print version information.")
                               .longOpt("version")
                               .build();
        addOption(version);


        Option esg = Option.builder("E")
                           .longOpt("esg")
                           .desc("Look for ESG metadata in the data source.").build();
        addOption(esg);

        Option uaf = Option.builder("m")
                           .longOpt("metadata")
                           .desc("Look for UAF metadata in the source THREDDS catalog.")
                           .build();

        addOption(uaf);


        Option dataset = Option.builder("d")
                               .longOpt("dataset")
                               .optionalArg(true)
                               .desc("If present, all netCDF arguements will combined into one LAS dataset.  Optionally include the name to be given to the dataset")
                               .build();
        addOption(dataset);


        Option category = Option.builder("c")
                                .longOpt("category")
                                .desc("Create categories for the netCDF data sets being processed.").build();
        addOption(category);


        Option arange = Option.builder("a")
                              .hasArgs()
                              .valueSeparator(',')
                              .longOpt("arange")
                              .desc("Force an arange element to be created for each listed (x,y,z,t) axes even if it is not regular.  E.g. -a t,z")
                              .build();
        addOption(arange);


        Option in_xml = Option.builder("x")
                              .longOpt("xml")
                              .hasArg()
                              .desc("The file name of the las.xml file to which these data will be added.")
                              .build();
        addOption(in_xml);


        Option title_attribute = Option.builder("g")
                                       .longOpt("title_attribute")
                                       .hasArg()
                                       .desc("The NC_GLOBAL attribute that contains the text that should be used as the LAS dataset name.")
                                       .build();
        addOption(title_attribute);


        Option in_thredds = Option.builder("t")
                                  .longOpt("thredds")
                                  .hasArg()
                                  .desc("The file name or http:// URL of a THREDDS Catalog to be added.")
                                  .build();
        addOption(in_thredds);


        Option in_data = Option.builder("n")
                .longOpt("netcdf")
                .hasArg()
                .desc("The file name or OPeNDAP URL of the netCDF file to be added.")
                .build();
        addOption(in_data);


        Option in_regex = Option.builder("r")
                .longOpt("regex")
                .hasArg()
                .desc("A regular expression which if present must match the OPeNDAP URL in order for that URL to be included.  Handy for finding aggregations from among a bunch of files.").build();
        addOption(in_regex);


        Option format = Option.builder("f")
                .longOpt("format")
                .hasArg()
                .desc("Set the format of how the time string is printed.  E.g. \"yyyy-MM\"")
                .build();
        addOption(format);

        Option units_format = Option.builder("u")
                .longOpt("units_format")
                .desc("Set the format the date/time stamp in the origin part of the time units string.  E.g. for \"days since 1-1-1 00:00:00\" use \"y-M-d HH:mm:ss\".  You only need this if you calendar is not a Julian/Gregorian mix and your time format is not one of yyyy-MM-dd'T'HH:mm:ss.SSSZ or yyyy-MM-dd HH:mm:ss.")
                .hasArg()
                .build();
        addOption(units_format);

        // Secret debugging option to only do a few things and then stop.
        Option limit = Option.builder("l")
                .longOpt("limit").build();
        addOption(limit);

        // auth_provider, username, password. Skipping these for now, they're just clutter at this point.

        // Use helpformatter to say basename is the unflagged option at the end
//        UnflaggedOption basename = new UnflaggedOption("basename")
//                .setStringParser(new StringStringParser())
//                .setDefault("las_from_addXML")
//                .setRequired(false)
//                .setGreedy(false);
//        basename.setHelp("Base name that will be used to construct output files names.  The edited --xml file goes in basename.xml and the data set information goes in basename_000.xml, basename_001.xml, ...");


        Option irregular = Option.builder("i")
                .longOpt("irregular")
                .desc("Treat time axis as irregular axis and make give it an <arange> of whole hours.  Typically used for high-frequncey sesnor data.")
                .build();
        addOption(irregular);

        // Skipping groupname, grouptype. These were added for time series, but we do this in ERDDAP now.
        // More clutter

    }
}
