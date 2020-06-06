#! /usr/bin/perl -w

# get_las_page.pl
# program to get LAS servlets/dataset response and print to file.

use strict;
use warnings;
use LWP::UserAgent;
use HTTP::Cookies;

# Question: is this a good name and path? presumably $lasroot/bin/
my $cookie_file = 'read_las_cookies.txt';
#
# Read the URL from the command line.
# Note: the configure script sets this up to access the tomcat URL directly.
# If you use a connector or a proxy config to pass the request from Apache
# to tomcat you may want to use the apache URL instead

if ($#ARGV < 1) {
print STDERR "usage: get_las_page.pl LAS_URL output_file\n";
}


my $url = shift @ARGV;
my $output_file = shift @ARGV;

my $ua = new LWP::UserAgent;
$ua->cookie_jar(HTTP::Cookies->new(file => "$cookie_file", autosave => 1));

# first request
my $request = HTTP::Request->new(GET => $url);
my $response = $ua->request($request);


# deal with LAS cookie check response (sends back '302 - Temporarily Moved'
#  message and cookie check URL
while ($response->{_rc} eq '302'){
    $url=${$response->{_request}->{_uri}};
    $request = HTTP::Request->new(GET => $url);
    $response = $ua->request($request);
}
if ($response->is_success) {
    unlink $cookie_file;
    open (OUT,">$output_file");
    my $stuff = $response->content;
    print OUT "\n$stuff\n";
    close OUT;
}else{
    my $status_line = $response->status_line;
    die "get_las_page.pl failed, server response was: $status_line\n";
}

