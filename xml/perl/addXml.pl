#!/usr/bin/perl -w
# Copyright (c) 2001 TMAP, NOAA
# ALL RIGHTS RESERVED
#
# Please read the full copyright notice in the file COPYRIGHT
# included with this code distribution

# $Id: addXml.pl.in,v 1.11.8.1 2005/07/08 15:26:16 rhs Exp $
use strict;
use Config;
use lib ('../xml/perl','.', '../xml/perl/dods','../xml/perl/install/lib/' . $Config{version},
         '../xml/perl/install/lib/site_perl/' . $Config{version},
         '../xml/perl/install/lib/perl5/' . $Config{version},
         '../xml/perl/install/lib/perl5/site_perl/' . $Config{version});
use LASDods;
use Getopt::Long;
BEGIN {
    use File::Basename;
    unshift(@INC,dirname $0);
}

sub getHandler {
    my $url = shift;
    return new LAS::DODS($url) if $url =~ /^\s*http:/;
    return new LAS::NetCDF($url);
}

sub usage {
    print <<EOF;
Usage: addXml.pl [--name dsetname] [--docurl url] 
                 [--out_inc] [--in_inc] 
                 infile outfile NetCDFfile

    --name        dsetname  display name of dataset. Defaults to file name.
    --docurl      url       URL of dataset documentation. Default to none.
    --out_inc               XML will be written in a format suitable for
                            inclusion in a master XML file.
    --in_inc                infile is a XML fragment created by --out_inc.
                            Implies --out_inc.
    infile                  Add file metadata to this XML file.
    outfile                 Write output to this file. Can be same as infile.
    NetCDFfile              NetCDF file to add to XML.
EOF
    exit 1;
}

use LAS;
use LASNetCDF;

my ($Name, $Docurl, $OutInclude, $InInclude);


if (!GetOptions('name=s' => \$Name, 'docurl=s' => \$Docurl,
		out_inc => \$OutInclude, in_inc => \$InInclude)){
    usage;
}

my $inFile = shift @ARGV;
my $outFile = shift @ARGV;

my $first = shift @ARGV;
usage if ! $inFile || ! $outFile || ! $first;

my $input = "-n $first";
foreach my $f (@ARGV) {
   $input = $input." -n $f";
}

print <<WARN;
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- WARNING +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-


This code is going to be phased out soon.  A new tool written in Java is now
available.  This new tool reads more netCDF conventions and can prepare 
LAS configuration information for an entire THREDDS catalog.

You can get the usage information by running:

/var/www/htdocs/java/las/bin/addXML.sh


You can try it on this input by running: 

/var/www/htdocs/java/las/bin/addXML.sh $input -x $inFile $outFile


+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
WARN

# To keep the operations.xml file in correct XML include syntax, we need
# to copy it from the server directory to this perl script's directory, 
# and /tmp/ directory. Then we edit the final file to replace verbose
# output under the <operations> tag with the external entity pointer

my $ops_file =  dirname($0)."/operations.xml";

if (-f $ops_file){
    unlink($ops_file);
}

link(dirname($0)."/../../server/operations.xml",$ops_file) or die "Cannot link $ops_file$!";

# Finished creating copies of operation.xml

my $parser;
if ($InInclude){		# Generate full XML file from template
    $OutInclude = 1;
    my $fname = dirname($0) . "/addxml$$" . time;
    my $template = dirname($0) . "/template.xml";
    open TMPOUT,">$fname" or die "Can't open temp file $fname";
    open IN ,"$inFile" or die "Can't open input file $inFile";
    open TMPIN, "$template" or die "Can't open template file $template";
    while (<TMPIN>){
	my $datain;
	if (/\<\/lasdata\>/){
	    while ($datain=<IN>){
		print TMPOUT $datain;
	    }
	}
	print TMPOUT;
    }
    close TMPIN;
    close IN;
    close TMPOUT;
    $parser = new LAS::Parser($fname);
    unlink $fname;
} else {
    $parser = new LAS::Parser($inFile);
}
my $config = new LAS::Config($parser);

print STDERR "**File: $first\n";
my $xml;
my $cdf = getHandler($first);
$xml = new LAS::NetCDF::XML($cdf, $config, $parser, 1, $Name, $Docurl);
$cdf->close;
if ($xml){
    foreach my $fname (@ARGV){
	print STDERR "**File: Adding virtual $fname\n";
	$cdf = getHandler($fname);
	if (! $config->findURL($fname)){
	    $xml->addVirtualVariable($cdf);
	} else {
	    print STDERR "Warning: URL ", $fname, " already in XML -- skipping\n";
	}
	$cdf->close;
    }
} else {
    print STDERR "Warning: URL ", $first, " already in XML -- skipping\n";
}

close STDOUT;
open STDOUT, ">$outFile" or die "Couldn't open $outFile";

if ($OutInclude){
    my @children = $config->getElement->getChildNodes;
    foreach my $child (@children){
	if ($child->getNodeType == XML::DOM::ELEMENT_NODE){
	    my $name = $child->getTagName;
	    next if $name eq "operations" || 
                    $name eq "properties" ||
                    $name eq "institution";
	    prettyPrint $child->toString;
	}
    }
} else {
    prettyPrint $config->toXML;
    close STDOUT;

    # now we get rid of the verbose operations information and substitue external entity pointer
    my $fname = "addxml$$" . time;
    open TOUT,">$fname" or die "Can't open temp file $fname";    
    open OUTF,"<$outFile" or die "Couldn't open $outFile";
    while (my $line = <OUTF>){
	if ($line =~ m/\<operations.+\>/){
            print TOUT $line;
	    while ($line = <OUTF>){
		if($line =~ m/(^\s*)\<\/operations\>/){
		    print TOUT "$1 &operations;\n";
		    last;
		}

	    }
	}
	print TOUT "$line";
    }
    close TOUT;
    close OUTF;
    rename($fname, $outFile);
}


unlink($ops_file);






