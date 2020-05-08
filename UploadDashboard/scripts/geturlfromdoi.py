#! /usr/bin/python

import re
import sys

if sys.version_info[0] > 2:
    from urllib.request import urlopen
    from urllib.error import HTTPError
else:
    from urllib2 import urlopen
    from urllib2 import HTTPError

# Extracts the accession number from these URLs
ACCESSION_URL = re.compile(r'https://accession\.nodc\.noaa\.gov/([0-9]+)$', re.IGNORECASE)
DATAISOID_URL = re.compile(r'https://data\.nodc\.noaa\.gov/cgi-bin/iso\?id=gov\.noaa\.nodc:([0-9]+)$', re.IGNORECASE)
ACCESSXML_URL = re.compile(r'https://www\.nodc\.noaa\.gov/ocads/data/([0-9]+)\.xml$', re.IGNORECASE)
TESTDATA_URL = re.compile(r'https://test\.nodc\.noaa\.gov/ocads/data/([0-9]+)\.xml$', re.IGNORECASE)


def getUrlFromDoi(mydoi):
    # type: (str) -> (str,None)
    """
        Gets the endpoint URL for the given DOI.  If the endpoint URL matches one of the OCADS URLs
        with a accession ID, checks if the standard https://accession.nod.noaa/gov/<accessionID>
        landing page URL (if not that URL already), redirects to the same page.  If so, returns this
        standard landing page URL; otherwise, returns the endpoint URL.

        :param mydoi: DOI to examine

        :return: the standard OCADS landing page URL, if appropriate, for the given DOI, or
        the endpoint URL for the given DOI, or None if there is no landing page for the given DOI
    """
    if not mydoi.strip():
        return None
    # get the endpoint URL for the DOI
    try:
        res = urlopen("http://www.doi.org/" + mydoi)
        if not res:
            return None
    except HTTPError:
        return None

    endurl = res.geturl()
    # first check if the given URL is already the standard OCADS landing page URL
    # (unlikely as these are typically redirects)
    match = ACCESSION_URL.match(endurl)
    if match:
        return endurl
    # check if the URL matches one of the known patterns with an accession number
    match = DATAISOID_URL.match(endurl)
    if not match:
        match = ACCESSXML_URL.match(endurl)
    if not match:
        match = TESTDATA_URL.match(endurl)
    if match:
        # create the standard URL using the accession number, and verify the endpoint
        stdurl = 'https://accession.nodc.noaa.gov/' + match.group(1)
        res = urlopen(stdurl)
        if res.geturl() == endurl:
            endurl = stdurl
    return endurl


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print('', file=sys.stderr)
        print('    Usage:  ' + sys.argv[0] + '  DOI', file=sys.stderr)
        print('', file=sys.stderr)
        print('    Prints the given DOI, a tab, and the landing page URL for the given DOI.  ', file=sys.stderr)
        print('    If this is an OCADS URL with an accesssion ID, the standard OCADS landing ', file=sys.stderr)
        print('    page URL (which is typically a redirect) is returned if this URL redirects ', file=sys.stderr)
        print('    to the same landing page URL as the DOI.  If no URL is associated with the ', file=sys.stderr)
        print('    given DOI, an error is print to stderr and nothing is printed to stdout. ', file=sys.stderr)
        print('', file=sys.stderr)
        sys.exit(1)
    doi = sys.argv[1]

    url = getUrlFromDoi(doi)
    if not url:
        print("No URL found for DOI " + doi, file=sys.stderr)
        sys.exit(1)
    print(doi + '\t' + url)
