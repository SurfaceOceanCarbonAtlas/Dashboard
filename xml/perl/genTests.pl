#!/usr/bin/perl
# Copyright (c) 2001 TMAP, NOAA
# ALL RIGHTS RESERVED
#
# Please read the full copyright notice in the file COPYRIGHT
# included with this code distribution
# $Id: genTests.pl.in,v 1.8 2004/11/05 23:21:17 mclean Exp $


# Generate a LAS test suite using source XML

use lib qw(.. ../xml/perl/);
use LAS;
use TMAPDate;
use LWP::UserAgent;
use URI::URL;
use strict;
use Getopt::Long;
use LASUI;

my $VERSION = "1.11";
my (@Datasets, @Institutions, $Use1D, $Use2D, $DieOnFail, $MaxFailures, $MaxEnd, $Verbose);
$| = 1;
my $MaxFailures = 1;
my $MaxEnd = 2000000000;
my %VarToFailHash;
my @FailedServers;

my $TestCount = 1;
my $FailCount = 0;

my $XMLFile;

sub issueTest {
    my ($var, $urlstr, $dsetname, $varname, $opName, $opFormat, $xmlRegion) = @_;
    my $hdrs = new HTTP::Headers;
    my $url = new URI::URL($urlstr);
    my $req = new HTTP::Request('POST', $url, $hdrs);
    my $fileloc = $var->getURL;
    my $fileserver = '';
    if($fileloc =~ m/(^http:\/\/.+?\/)/){
	$fileserver = "$1";
    }
    my $xmlHead = <<EOF;
<?xml version="1.0"?>
<lasRequest href="file:$XMLFile" useCache="false">
    <link match="/lasdata/operations/$opName" />
    <properties>
        <ferret>
	    <format>$opFormat</format>
	    <size>.25</size>
        </ferret>
    </properties>
    <args>
        <link match="/lasdata/datasets/$dsetname/variables/$varname"/>
        <region>
EOF
    my $xmlFoot = <<EOF;
        </region>
    </args>
</lasRequest>
EOF

    my $xml = $xmlHead . $xmlRegion . $xmlFoot;

    $req->content_type('application/x-www-form-urlencoded');
    $req->content('xml=' . $xml);

    my $ua = new LWP::UserAgent;
    my ($resp, $loc);
    while(1){
	$resp = $ua->request($req);
	$loc = $resp->header('Location');
	last if ! $loc;
	$url = new URI::URL($loc);
	$req = new HTTP::Request('GET', $url, $hdrs);
    }

    my $isOK = 0;
    my $result = "";
    # LAS v5.0 returns "image/gif" instead of html with "<img" tags.
    if ($resp->is_success && $resp->header('Content-type') =~ /image\/gif/){
      $isOK = 1;
    }
    if ($resp->is_success && $resp->header('Content-type') =~ /text\/html/){
	$result = $resp->content;
	if ($result =~ /<img/){
	    $isOK = 1;
        } 

        # Test for other, expected responses
        #  - database access: all bad message
        #  - database access: all same message
        #  - insitu_single_profile result
        #  - insitu_single_profile no-data
	if (!$isOK){
	    if ($result =~ /data are all flagged as bad/ ||
		$result =~ /all data have same value/ ||
		$result =~ /Profile Number/ ||
		$result =~ /No data are available/){
		$isOK = 1;
	    }
	}
    }

    if ($isOK){
        if ($Verbose) {
    	  print "\n------------\nTest: $TestCount\n";
    	  print "URL: $url\n";
          print "FILELOC: $fileloc\n";
    	  print "Dataset/Variable: $dsetname:$varname\n";
    	  print "Op: $opName\n";
    	  print "Region: $xmlRegion";
	  print "Test passed: \n";
    	  print "------------\n";
        }
    } else {
        # If the error was on a remote server, check the server
        # connection one time.  If the server is down or
        # non-existent (HTTP error code 500) add it to
        # the FailedServers array and do not test again.
        my $ServerIsBad = 0;
        my $ServerIsBad_msg = '';
        foreach my $badserver (@FailedServers){
	    $ServerIsBad = 1 if($fileserver eq $badserver);
	}
        if (!$ServerIsBad){
	    my $ua = new LWP::UserAgent;
	    my $req = new HTTP::Request('GET', $fileserver);
            my $resp = $ua->request($req);
            if (!$resp->is_success){
		if ($resp->{_rc} =~ /500/){
		    push(@FailedServers,$fileserver);
                    $ServerIsBad_msg =  "$resp->{_msg}\n";
		}
	    }
        }
    	print "\n------------\nTest: $TestCount\n";
    	print "URL: $url\n";
        print "FILELOC: $fileloc\n";
    	print "Dataset/Variable: $dsetname:$varname\n";
    	print "Op: $opName\n";
    	print "Region: $xmlRegion";
	print "Test failed: \n";
	print "Response code: ", $resp->code, ":", $resp->message,"\n";
	print "Content-type: ", $resp->header('Content-type'), "\n";
	print "\n$result\n";
        if ($ServerIsBad_msg =~ /.+/){
	    print "Will not continue testing this remote data server:\n$ServerIsBad_msg\n";
	}
	exit 1 if $DieOnFail;
	++$VarToFailHash{$var};
    	print "------------\n";
        $FailCount++;
    }
    $TestCount++;
#    print $xml,"\n";
}

sub validAttribute {
    my ($obj, $att) = @_;
    my $rval = $obj->getAttribute($att);
    die "Can't find attribute '$att'"
	if ! defined $rval;
    return $rval;
}

sub genXML {
    my ($url, $var, $axisHash, $rangeHash, $opName, $opFormat,
	$testCount) = @_;
    my $varname = $var->getName;
    my $dsetname = $var->getDataset->getName;

    my (@regionAxes, @pointAxes);
#
# Divide axes into region/points
#
    foreach my $type (keys %{$axisHash}){
	my $axis = $axisHash->{$type};
	my $isRange = $rangeHash->{$type};
	if ($isRange){
	    push(@regionAxes, $axis);
	} else {
	    push(@pointAxes, $axis);
	}
    }

#
# Generate ranges
#
    my $xmlRanges;
    foreach my $axis (@regionAxes){
	my $lo = $axis->getLo;
	my $hi = $axis->getHi;
	my $type = validAttribute($axis, 'type');
        if ($type eq 't') {
          # If it's not a date AND not a float, use the FORTRAN index value.
          # This is the same logic that exists in Ferret.pl.
          my $date = new TMAP::Date($lo);
          if (!$date->isOK) {
            if ($lo !~ /^[+-]?\d*\.?\d*([eE][+-]?\d+)?$/){
              $lo = "1";
              $hi = $axis->getSize;
            }
          }
        }
	$xmlRanges .= qq:<range type="$type" low="$lo" high="$hi"/>\n:;
    }
#
# If only regions, generate test and exit
    if ($#pointAxes < 0){
        issueTest($var, $url, $dsetname, $varname, $opName, $opFormat, 
                  $xmlRanges);
	return;
    }
	
#
# If only points, return
    if ($#regionAxes < 0){
	return;
    }
	

#
# Generate tests for low and hi values of point axes
#
    for my $i (0..$#pointAxes){
	my $xmlPoints = "";
	for my $j (0..$#pointAxes){
	    my $axis = $pointAxes[$j];
	    my $lo = $axis->getLo;
	    my $type = validAttribute($axis, 'type');
            if ($type eq 't') {
              # If it's not a date AND not a float, use the FORTRAN index value.
              # This is the same logic that exists in Ferret.pl.
              my $date = new TMAP::Date($lo);
              if (!$date->isOK) {
                if ($lo !~ /^[+-]?\d*\.?\d*([eE][+-]?\d+)?$/){
                  $lo = "1";
                }
              }
            }
	    if ($i != $j){
		$xmlPoints .= qq:<point type="$type" v="$lo"/>\n:;
	    }
	}
	my $axis = $pointAxes[$i];
	my $lo = $axis->getLo;
	my $hi = $axis->getHi;
	my $type = validAttribute($axis, 'type');
        if ($type eq 't') {
          # If it's not a date AND not a float, use the FORTRAN index value.
          # This is the same logic that exists in Ferret.pl.
          my $date = new TMAP::Date($lo);
          if (!$date->isOK) {
            if ($lo !~ /^[+-]?\d*\.?\d*([eE][+-]?\d+)?$/){
              $lo = "1";
              $hi = $axis->getSize;
            }
          }
        }
	return if $$testCount++ >= $MaxEnd;
	my $xmlTestPoint = qq:<point type="$type" v="$lo"/>\n:;
        issueTest($var, $url, $dsetname, $varname, $opName, $opFormat, 
                  $xmlRanges . $xmlPoints . $xmlTestPoint);
	return if $$testCount++ >= $MaxEnd;
        # Some axes are defined but contain only a single point.
        # Don't repeat tests for the HI end of such an axis.
        my $pointTest = 0;
        if ($type eq 't') {
          $pointTest = ($hi eq $lo);
        } else { 
          $pointTest = ($hi == $lo);
        }
        if (!$pointTest) {
  	  $xmlTestPoint = qq:<point type="$type" v="$hi"/>\n:;
          issueTest($var, $url, $dsetname, $varname, $opName, $opFormat, 
                    $xmlRanges . $xmlPoints . $xmlTestPoint);
        }
    }
}

sub genRangeHash {
    my ($arg,$axisHash) = @_;
    my $rangeHash = {};
    foreach my $slice (split('',$arg)){
      my $lo = $axisHash->{$slice}->getLo;
      my $hi = $axisHash->{$slice}->getHi;
      my $pointTest = 0;
      if ($slice eq 't') {
         $pointTest = ($hi eq $lo);
      } else {
         $pointTest = ($hi == $lo);
      }
      if (!$pointTest) {
        $rangeHash->{$slice} = 1;
      }
    }
    $rangeHash;
}

sub genTests {
    my ($url,$var, $ui) = @_;
    my (@slices,$default,$uiprop);
    my $axisHash = {};

# Use the user interface info in ui.xml to specify
# the operation to use for 1D and 2D data
# Major hack -- if the UI XML changes, this is hosed

    my %viewToOpsHash;		# Mapping from view to name of operation
    my %viewToValueHash;	# Mapping from view to real slice
    my $props = $var->getProperties('ui');
    my $jsVarName = $var->getAttribute('js');
    if (!($props || $jsVarName)){
	$props = $var->getDataset->getProperties('ui');
	$jsVarName = $var->getDataset->getAttribute('js');
    }
	
    if ($props){
	$uiprop = $props->{default};
	my $url = new URI::URL($uiprop);
	if ($url->scheme ne 'file' || $url->path ne 'ui.xml'){
	    die "UI property in '$url' must refer to file:ui.xml";
	}
	$default = $url->frag;
	die "'$url' missing reference to UI map" if ! $default;
	$default =~ s/^#//;
    }
    $default = defined($default) ? $default : $jsVarName;
    $default = "" if ! defined($default);
    my $defObj = $ui->getDefaultByName($default);
    die "Internal error: ui.xml doesn't have definition for default: $default"
	if ! $defObj;
    foreach my $map ($defObj->getChildren){
	my $type = $map->getAttribute('type');
	if ($type eq 'ops'){
	    foreach my $menu ($map->getChildren){
		my $viewStr = $menu->getAttribute('view');
		my @views = split(/\s*,\s*/,$viewStr);
		my $href = $menu->getAttribute('href');
		$href =~ s/^#//;
		if ($href){
		    foreach my $view (@views){
			my $opMenu = $LAS::UI::Generator::Menus{$href};
# Use the first operation in the menu
			my $firstOp = ($opMenu->getChildren)[0];
			my @values =
			    split(/\s*,\s*/, $firstOp->getAttribute('values'));
			$viewToOpsHash{$view} = \@values;
#			print "$view: ", $viewToOpsHash{$view}, "\n";
			     
		    }
		}
	    }
	} elsif ($type eq 'views'){
	    foreach my $menu ($map->getChildren){
		my $href = $menu->getAttribute('href');
		$href =~ s/^#//;
		if ($href){
		    my $viewMenu = $LAS::UI::Generator::Menus{$href};
		    foreach my $item ($viewMenu->getChildren){
			my $view = $item->getAttribute('view');
			my $value = $item->getAttribute('values');
			$viewToValueHash{$view} = $value;
		    }
		}
	    }
	}
    }

    foreach my $axis ($var->getChildren){
	my $type = validAttribute($axis, 'type');
	$axisHash->{$type} = $axis;
	push(@slices, $type);
    }

    # 1D slices
    my $testCount = 0;
    if ($Use1D){
	for my $i (0..$#slices){
	    my $slice = $slices[$i];
	    my $ops  = $viewToOpsHash{$slice};
	    next if !$ops;
	    return if $VarToFailHash{$var} > $MaxFailures;
	    my ($op,$format) = @{$ops};

	    my $values = $viewToValueHash{$slice};
	    my $rangeHash = genRangeHash($values,$axisHash);
            # Some axes are defined but contain only a single point.
            # Don't generate tests which require ranges on such an axis.
            next if ($rangeHash->{$slice} == 0);
	    genXML($url,$var, $axisHash, $rangeHash,$op, $format,
		   \$testCount);
	}
    }

    # 2D slices
    if ($Use2D){
	for my $i (0..$#slices){
	    for my $j ($i+1..$#slices){
                my $s_i = $slices[$i];
                my $s_j = $slices[$j];
		my $slice = join('',($slices[$i],$slices[$j]));
		my $ops  = $viewToOpsHash{$slice};
		next if !$ops;
		return if $VarToFailHash{$var} > $MaxFailures;
		my ($op,$format) = @{$ops};
		my $values = $viewToValueHash{$slice};
		my $rangeHash = genRangeHash($values,$axisHash);
                # Some axes are defined but contain only a single point.
                # Don't generate tests which require ranges on such an axis.
                next if ($rangeHash->{$s_i} == 0);
                next if ($rangeHash->{$s_j} == 0);
		genXML($url,$var, $axisHash, $rangeHash,$op, $format,
		       \$testCount);
	    }
	}
    }
}

&GetOptions('d=s' => \@Datasets, 'i=s' => \@Institutions,
            '1' => \$Use1D, '2' => \$Use2D,
	    'die' => \$DieOnFail, 'maxend=i' => \$MaxEnd,
            'allow=i' => \$MaxFailures,
            'verbose' => \$Verbose);
if (!($Use1D || $Use2D)){
    $Use1D = 1;
    $Use2D = 1;
}

my $inFile = shift @ARGV;
$XMLFile = $inFile;
if (!$inFile){
    print STDERR <<EOF;

    Usage: genTests.pl [--allow #][--die ] [--verbose] [-d dsetpattern [-d dsetpatter]] 
                       [-1] [-2] xmlfile
           -d dsetpattern  Only test datasets where XML tags match pattern
           -i instpattern  Only test datasets where Institution names match pattern
           -1              Test 1D operations
           -2              Test 2D operations
                           (if both -1 and 2 are missing, both
                            1D and 2D operations are tested)
           --die           Exit on first test failure
           --maxend #      Maximum number of endpoint tests for each
                           variable. Default is all tests.
	   --allow #       Allow this many failures per variable. Default
                           is 1
	   --verbose       Report successful attempts as well as failures.
                           Default is to only report failures.


    Generates a set of LAS requests to a LAS data server. The end points
    of regions in the requests are tested. 

    For instance, if a variable is defined in a LAS XML configuration file to 
    have the range:
        (0:360),(-90:90),(1-Jan:1-Dec)
    and genTests.pl is invoked with the -2 arg, the following regions are
    tested:
        (0:360),(-90,90), (1-Jan)
        (0:360),(-90,90), (1-Dec)
        (0),(-90,90), (1-Jan:1-Dec)
        (360),(-90,90), (1-Jan:1-Dec)
	...

    Only the first product in the LAS product menu is tested; for the 
    default LAS configuration, this will be a shade or line.

    Example: genTests.pl las.xml http://ferret.wrc.noaa.gov/las-bin/LASserver.pl
EOF
    exit 1;
}
my $parser = new LAS::Parser($inFile);
my $config = new LAS::Config($parser);
my @ops = $config->getOps;
my $url = new URI::URL($ops[0]->getURL); # Assumed that all op URLS same
my $uiparser = new LAS::Parser('ui.xml');
my $ui = new LAS::UI::Generator($uiparser);


foreach my $dset ($config->getChildren){
    my $found = 1;
    if (@Datasets){
	$found = 0;
	my $name = $dset->getName;
	foreach my $pattern (@Datasets){
	    if ($name =~ /$pattern/){
		$found = 1;
		last;
	    }
	}
    }
    if (@Institutions && $found == 1){
	$found = 0;
        my $inst;
        if($inst = $dset->getInstitution){
        }else{
           $inst = $config->getInstitution;
        }
        my $name = $inst->getInstName;
        foreach my $pattern (@Institutions){
           if ($name =~ /$pattern/){
	       $found = 1;
	       last;
	   }
        }
    }
    if ($found){
	foreach my $var ($dset->getChildren){
            my $fileloc = $var->getURL;
            my $fileserver = '';
            if($fileloc =~ m/(^http:\/\/.+?\/)/){
		$fileserver = "$1";
            }
            # Check FailedServers array for list of remote
            # servers which are down (set in IssueTest isOK
            # conditional)
            my $ServerIsBad = 0;
            foreach my $badserver (@FailedServers){
	        $ServerIsBad = 1 if($fileserver eq $badserver);
	    }
	    genTests($url, $var, $ui) if !$ServerIsBad;
	}
    }
}

$TestCount--;
print "\nFinished $TestCount tests with $FailCount failures\n";
    if (@FailedServers > 0){
	print "Could not connect to ".@FailedServers." remote data server(s)\n";
        foreach my $server (@FailedServers){
	    print " --  $server\n";
	}
    }
