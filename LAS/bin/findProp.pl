#!/usr/bin/perl -w
# Copyright (c) 2001 TMAP, NOAA
# ALL RIGHTS RESERVED
#
# Please read the full copyright notice in the file COPYRIGHT
# included with this code distribution

use strict;

sub usage {
    print "usage: findProp.pl file [file...]\n";
    exit 1;
}

sub doFile {
    my ($file) = @_;
    if (! -r $file){
	warn "Can't open file: $file";
	return;
    }
    my $doPrint = 0;
    open INPUT, $file or die "Can't open $file";
    while(<INPUT>){
	if (/<\s*ferret\s*>/){
	    $doPrint = 1;
	    print;
	} elsif (/<\/\s*ferret\s*>/){
	    $doPrint = 0;
	    print;
	} else {
	    print if $doPrint;
	}
    }
    close INPUT;
}

usage if $#ARGV < 0;

foreach my $file (@ARGV){
    doFile($file);
}

    
