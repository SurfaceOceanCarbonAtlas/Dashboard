#!/usr/bin/perl
if (! -f "../../LFDS/LFDS.war"){
    print "Can't find FDS WAR file ../../LFDS/LFDS.war\n";
    print "Try building it yourself by running these commands.\n";
    print "cd ../../LFDS\n";
    print "ant\n";
    exit 1;
}

