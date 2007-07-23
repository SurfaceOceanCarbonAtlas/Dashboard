#
# $Id: code.pl,v 1.2 2002/06/26 18:20:13 sirott Exp $
#
#  Copyright (c) 1999, Raphael Manfredi
#  
#  You may redistribute only under the terms of the Artistic License,
#  as specified in the README file that comes with the distribution.
#
# HISTORY
# $Log: code.pl,v $
# Revision 1.2  2002/06/26 18:20:13  sirott
# 	* Merged v_6_0_base_branch with main branch
#
# Revision 1.1.2.1  2002/06/02 01:10:59  sirott
# 	* Added all required Perl modules to CVS distribution
#
# Revision 1.1  2002/03/09 16:16:55  wendigo
# New maintainer
#
# Revision 0.2  2000/11/06 19:30:33  ram
# Baseline for second Alpha release.
#
# $EndLog$
#

sub ok {
	my ($num, $ok) = @_;
	print "not " unless $ok;
	print "ok $num\n";
}

sub contains {
	my ($file, $pattern) = @_;
	local *FILE;
	local $_;
	open(FILE, $file) || die "can't open $file: $!\n";
	my $found = 0;
	my $line = 0;
	while (<FILE>) {
		$line++;
		if (/$pattern/) {
			$found = 1;
			last;
		}
	}
	close FILE;
	return $found ? $line : 0;
}

1;

