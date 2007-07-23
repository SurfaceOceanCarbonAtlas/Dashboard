#!/usr/bin/perl -w
# Copyright (c) 2001 TMAP, NOAA
# ALL RIGHTS RESERVED
#
# Please read the full copyright notice in the file COPYRIGHT
# included with this code distribution


use LAS;
use URI::URL;
use Carp qw(carp croak);

my $IndentLevel = 0;

sub printVar {
    my $var = shift;
    my $url = $var->getURL;
    $IndentLevel++;
    printlni "Variable: URL: ", $url->full_path, "#", $url->frag;
    $IndentLevel--;
}

sub printRegion {
}

sub printProps {
    my ($title, $propsRef) = @_;
    printlni $title;
    if ($propsRef){
	my %props = %{$propsRef};
	$IndentLevel++;
	foreach (keys %props){
	    printlni "Property: $_ = $props{$_}";
	}
	$IndentLevel--;
    }
}    

sub printReq {
    my $req = shift;
    my %props = $req->getProperties('ferret');
    printProps("Request properties:", \%props);
    println "Operation properties:";
    %props = $req->getOp->getProperties('ferret');
    $IndentLevel++;
    foreach (keys %props){
	my $value = $props{$_};
	if (defined $value){
	    printlni "Property: $_ = $props{$_}";
	} else {
	    printlni "Property: $_ = (null)";
	}
    }

    $IndentLevel--;
    println "Arguments:";
    foreach ($req->getChildren){
	my $class = ref($_);
	if ($class eq "LAS::Variable"){
	    printVar $_;
	} elsif ($class eq "LAS::Region"){
	    printRegion $_;
	}
    }
    
}

sub dump {
    my $req = shift;
    my %props = $req->getProperties('ferret');
    foreach (keys %props){
	println "Property: $_ : value: $props{$_}";
    }
    
    println "Op: ",$req->getOp;
    foreach ($req->getChildren){
	println "Arg: $_";
	my $class = ref($_);
	if ($class eq "LAS::Variable"){
	    println "Variable: ",$_->getAttribute("name"),
	    " Dataset: ",$_->getDataset->getAttribute("name");
	} elsif ($class eq "LAS::Region"){
	    println "Region:";
	    foreach ($_->getChildren){
		println ref($_),"lo:",$_->getLo," hi:",$_->getHi;
	    }
	}
    }
}
    
my $parser = new LAS::Parser("lasRequest.xml");
my $req = new LAS::Request($parser);
printReq $req;


