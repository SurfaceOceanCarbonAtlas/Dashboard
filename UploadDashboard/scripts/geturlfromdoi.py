#! /usr/bin/env python

import re
import sys
import time

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


def readUrlFromDoi(mydoi):
    # type: (str) -> (str,None)
    """
        Reads the endpoint URL for the given DOI.  If the endpoint URL matches one of the OCADS URLs
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


def getUrlFromDoi(mydoi, mydoiurldict):
    # type: (str, dict) -> str
    """
        Return the endpoint URL(s) for the given DOI(s).  The given dictionary for DOIs to URLs is first
        searched.  If not found, the endpoint URL is obtained using readUrlFromDoi(str).  If a URL is
        found, it is added to the dictionary with the DOI as the key.

        :param mydoi: DOI to get the URL; can be multiple DOIs with space-semicolon-space separators
        :param mydoiurldict: dictionary containing DOI:URL for DOIs already searched

        :return: URL corresponding to the DOI, or None if the DOI is invalid.  If multiple DOIs are given,
                 the URLs are returned in the same order with space-semicolon-space separators
    """
    myurl = None
    try:
        myurl = mydoiurldict[mydoi]
    except KeyError:
        # Possibility of multiple DOIs
        urllist = []
        for subdoi in mydoi.split(' ; '):
            try:
                suburl = mydoiurldict[subdoi]
            except KeyError:
                time.sleep(1)
                suburl = readUrlFromDoi(subdoi)
                if suburl:
                    mydoiurldict[subdoi] = suburl
            if suburl and suburl not in urllist:
                urllist.append(suburl)
        for suburl in urllist:
            if not myurl:
                myurl = suburl
            else:
                myurl += ' ; ' + suburl
        if myurl:
            mydoiurldict[mydoi] = myurl
    return myurl


def readDoiUrlFromPropFile(mypropfilename):
    # type: (str) -> tuple[str,str]
    """
         Read the properties file with the given name and extract the DOI and URL from the file

        :param mypropfilename: name (path optional) of the file to read

        :return: (doi, url) where either or both could be None if not found

        :raise ValueError: if more than property line matches the DOI or URL property being searched
    """
    ORIGDATADOI = "origdatadoi="
    SOURCEDOI = "sourcedoi="
    SOURCEURL = "sourceurl="

    mydoi = None
    myurl = None
    propfile = open(mypropfilename)
    try:
        for propline in propfile:
            propline = propline.strip()
            if propline.startswith(ORIGDATADOI):
                doival = propline[len(ORIGDATADOI):]
                if doival:
                    if mydoi:
                        raise ValueError("More than one original-data DOI for " + propfilename)
                    mydoi = doival
            elif propline.startswith(SOURCEDOI):
                doival = propline[len(SOURCEDOI):]
                if doival:
                    if mydoi:
                        raise ValueError("More than one original-data DOI for " + propfilename)
                    mydoi = doival
            elif propline.startswith(SOURCEURL):
                urlval = propline[len(SOURCEURL):]
                if urlval:
                    if myurl:
                        raise ValueError("More than one original-data URL for " + propfilename)
                    myurl = urlval

    finally:
        propfile.close()
    return mydoi, myurl


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print('', file=sys.stderr)
        print('    Usage:  ' + sys.argv[0] + '  propsfilenames', file=sys.stderr)
        print('', file=sys.stderr)
        print('    For each properties file listed in the given file, if a DOI is given for the ', file=sys.stderr)
        print('    original data but a URL is not given, obtains the URL associated with the DOI ', file=sys.stderr)
        print('    and then prints a tab-seperated line to stdout giving the properties filename, ', file=sys.stderr)
        print('    the URL, and the DOI. ', file=sys.stderr)
        print('    If the URL is an OCADS URL with an accesssion ID, the standard OCADS landing ', file=sys.stderr)
        print('    page URL (which is typically a redirect) is returned if this URL redirects ', file=sys.stderr)
        print('    to the same landing page URL as the DOI. ', file=sys.stderr)
        print('    If no URL is associated with the given DOI, an error is print to stderr and ', file=sys.stderr)
        print('    nothing is printed to stdout. ', file=sys.stderr)
        print('', file=sys.stderr)
        sys.exit(1)
    propnamesfilename = sys.argv[1]

    doiurls = {}
    propnamesfile = open(propnamesfilename)
    try:
        for propfilename in propnamesfile:
            propfilename = propfilename.strip()
            try:
                (doi, url) = readDoiUrlFromPropFile(propfilename)
            except ValueError as ex:
                print(ex, file=sys.stderr)
                continue
            if doi and not url:
                try:
                    url = getUrlFromDoi(doi, doiurls)
                except Exception:
                    url = None
                if url:
                    print(propfilename + '\t' + url + '\t' + doi)
                else:
                    print("No URL found for DOI " + doi + " given in " + propfilename, file=sys.stderr)
    finally:
        propnamesfile.close()
