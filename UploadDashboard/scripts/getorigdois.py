#! /usr/bin/python

from __future__ import print_function

import re
import sys

if sys.version_info[0] > 2:
    import urllib
    import urllib.request
else:
    import urllib2

import xml
import xml.sax
import xml.sax.handler

# skip any "expocodes" that do not match (start with) usual expocode pattern
EXPO_REGEX = re.compile(r'([A-Z0-9]{4,5}?[0-9]{8}(-[1-9])?)')


def getExpocodeFromValue(myvalue):
    # type: (str) -> (str, None)
    """
        Determines if the given value resembles an expocode, although the date in the expocode
        is not checked for validity.  Prior to examination, the value is converted to uppercase,
        and whitespace is removed from each end.  If the resulting value starts with what resembles
        be an expocode but has additional trailing characters, a warning is printed to sys.stderr
        but the expocode found (without the additional trainling characters) is returned.

        :param myvalue: value to check

        :return: the expocode in the value if the value resembles an expocode,
                 or None if the value does not resemble an expocode
    """
    myvalue = myvalue.strip().upper()
    match = EXPO_REGEX.match(myvalue)
    if match:
        expocode = match.group(1)
        if len(expocode) != len(myvalue):
            print('    Warning: extra characters at the end of expocode "' + myvalue + '"', file=sys.stderr)
    else:
        expocode = None
    return expocode


# skip any "DOIs" that do not match (start with) usual DOI pattern
DOI_REGEX = re.compile(r'[0-9]+\.[0-9]+/[A-Z0-9/_.]+')


def getDOIFromValue(myvalue):
    # type: (str) -> (str, None)
    """
        Determines if the given value represents a DOI. Prior to examination, the value is converted
        to uppercase, and whitespace is removed from each end.  If the DOI is in the form of a hyperlink
        to https://doi.org/, this prefix is removed to give the "bare" DOI value.

        :param myvalue: value to check

        :return: the "bare" DOI value if the value resembles a DOI,
                 or None if the value does not resemble a DOI
    """
    mydoi = myvalue.strip().upper()
    if mydoi.startswith('HTTPS://DOI.ORG/'):
        mydoi = mydoi[16:]
    if not DOI_REGEX.match(mydoi):
        mydoi = None
    return mydoi


class LinkedObject(object):
    """
        A simple object for use in doubly-linked lists the following attributes
          - fullname: the "full path" name of the element
          - attrs: attributes associated with the element
          - value: value associated with that element
          - prevobj: prior object in the linked list
          - nextobj: next object in the linked list

    """

    def __init__(self, fullname, attrs, prevobj):
        # type: (str, object, LinkedObject) -> None
        """
            Create with the given values for fullname, attrs, and prevobj.  The value attribute is
            assigned as an empty string.  The nextobj and parent attributes are assigned as None.

            :param fullname: value for the fullname attribute
            :param attrs: value for the attrs attribute
            :param prevobj: value for the prevobj attribute, either None or another LinedObject
        """
        self.fullname = fullname
        self.attrs = attrs
        self.value = ''
        self.prevobj = prevobj
        self.nextobj = None
        self.parent = None

    # @override
    def __str__(self):
        """
            :return: string giving the fullname, string representation of the attribute object,
                     and value of this object
        """
        return '{ fullname: "' + self.fullname + '", attrs: "' + \
               str(self.attrs) + '", value: "' + self.value + '" }'


class LinkedObjectsContentHandler(xml.sax.handler.ContentHandler):
    """
        Extracts information from parsed XML content into a doubly-linked list of objects that
        can be retrieved from this instance.  Each object in this linked list has attributes:
          - fullname: the "full path" name of the element
          - attrs: attributes associated with the element
          - value: value associated with that element
          - prevobj: prior object in the linked list
          - nextobj: next object in the linked list
    """

    def __init__(self):
        # calling the superclass constructor fails in production
        # super(LinkedObjectsContentHandler, self).__init__()
        self.__rootobj = LinkedObject('', None, None)
        self.__rootobj.parent = self.__rootobj
        self.__currobj = self.__rootobj
        self.__parent = self.__rootobj

    # @override
    def startElement(self, name, attrs):
        # type: (str, object) -> None
        currobj = LinkedObject(self.__parent.fullname + '/' + name, attrs, self.__currobj)
        currobj.parent = self.__parent
        self.__currobj.nextobj = currobj
        self.__currobj = currobj
        self.__parent = currobj

    # @override
    def startElementNS(self, name, qname, attrs):
        # type: (str, str, object) -> None
        self.startElement(qname, attrs)

    # @override
    def endElement(self, name):
        # type: (str) -> None
        self.__parent = self.__parent.parent

    # @override
    def endElementNS(self, name, qname):
        # type: (str, str) -> None
        self.endElement(qname)

    # @override
    def characters(self, data):
        # type: (bytearray) -> None
        self.__currobj.value += data

    def getLinkedObjects(self):
        # type: () -> (LinkedObject, None)
        """
        Provides the doubly-linked list of objects generated from the XML.
        Each object in this linked list has attributes:
          - fullname: the "full path" name of the element
          - attrs: attributes associated with the element
          - value: value (as a unicode string) associated with that element
          - prevobj: prior object in the linked list
          - nextobj: next object in the linked list

        :return: the head element in the linked list of objects,
                 which could be None if no XML has been read
        """
        # clean up all the values
        thisobj = self.__rootobj.nextobj
        while thisobj:
            if sys.version_info[0] > 2:
                thisobj.value = str(thisobj.value).strip()
            else:
                thisobj.value = unicode(thisobj.value).strip()
            thisobj = thisobj.nextobj
        return self.__rootobj.nextobj


def getXmlContent(myurl):
    # type: (str) -> (LinkedObject,None)
    """
        Reads the XML file at the given URL and generates a doubly-linked list of objects
        containing the contents of the XML.  Each object in this linked list has attributes:
          - fullname: the "full path" name of the element
          - attrs: attributes associated with the element
          - value: value associated with that element
          - prevobj: prior object in the linked list
          - nextobj: next object in the linked list

        :param myurl: URL of site to obtain the XML

        :return: the head element of the linked list of objects,
                  or None if the there were problems with getting or interpreting the XML
    """
    handler = LinkedObjectsContentHandler()
    parser = xml.sax.make_parser()
    parser.setContentHandler(handler)
    try:
        if sys.version_info[0] > 2:
            req = urllib.request.urlopen(myurl)
        else:
            req = urllib2.urlopen(myurl)
        parser.parse(req)
    except Exception:
        return None
    return handler.getLinkedObjects()


def getLandingLinks(mylinkedobjs):
    # type: (LinkedObject) -> set
    """
        :return: set of landing page links found in the given linked-list of objects;
                 may be empty or have multiple entries, but should be a singleton set
    """
    links = set()
    obj = mylinkedobjs
    while obj:
        if obj.fullname == '/metadata/link_landing':
            myvalue = obj.value
            if myvalue.startswith('http'):
                links.add(myvalue)
        elif obj.fullname == '/gmi:MI_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/' + \
                'gmd:citation/gmd:CI_Citation/gmd:identifier/gmd:MD_Identifier/gmd:code/gmx:Anchor':
            myvalue = obj.attrs.getValueByQName('xlink:title')
            if not myvalue:
                myvalue = obj.attrs.getValue('xlink:title')
            if myvalue == 'NCEI Accession Number':
                myvalue = obj.attrs.getValueByQName('xlink:href')
                if not myvalue:
                    myvalue = obj.attrs.getValue('xlink:href')
                if myvalue.startswith('http'):
                    links.add(myvalue)
        obj = obj.nextobj
    return links


def getDois(mylinkedobjs):
    # type: (LinkedObject) -> set
    """
        :return: set of DOIs found in the linked-list of objects; may be empty or
                 have multiple entries, but should be a singleton set
    """
    dois = set()
    obj = mylinkedobjs
    while obj:
        if obj.fullname == '/metadata/doi':
            myvalue = getDOIFromValue(obj.value)
            if myvalue is not None:
                dois.add(myvalue)
        elif obj.fullname == '/gmi:MI_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/' + \
                'gmd:citation/gmd:CI_Citation/gmd:identifier/gmd:MD_Identifier/gmd:code/gmx:Anchor':
            myvalue = obj.attrs.getValueByQName('xlink:title')
            if not myvalue:
                myvalue = obj.attrs.getValue('xlink:title')
            if myvalue == 'DOI':
                myvalue = getDOIFromValue(obj.value)
                if myvalue:
                    dois.add(myvalue)
        obj = obj.nextobj
    return dois


def getExpocodes(mylinkedobjs):
    # type: (LinkedObject) -> set
    """
        :return: set of expocodes found in the linked-list of objects; may be empty
    """
    expocodes = set()
    tmpset = set()
    obj = mylinkedobjs
    while obj:
        if obj.fullname == '/metadata/expocode':
            myvalue = getExpocodeFromValue(obj.value)
            if myvalue:
                expocodes.add(myvalue)
        elif obj.fullname == '/gmi:MI_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/' + \
                'gmd:descriptiveKeywords/gmd:MD_Keywords':
            # clear tmpset whenever a new gmd:MD_Keywords is found
            tmpset = set()
        elif obj.fullname == '/gmi:MI_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/' + \
                'gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/gco:CharacterString':
            # possibly an expocode, add to tmpset for now
            myvalue = getExpocodeFromValue(obj.value)
            if myvalue:
                tmpset.add(myvalue)
        elif obj.fullname == '/gmi:MI_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/' + \
                'gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:thesaurusName/gmd:CI_Citation/' + \
                'gmd:title/gco:CharacterString':
            myvalue = obj.value.strip()
            if myvalue == 'EXPOCODE':
                # this set of "keywords" were expocodes; add all the expocodes in tmpset to expocodes
                expocodes.update(tmpset)
                tmpset = set()
        obj = obj.nextobj
    return expocodes


# Extracts the accession number from these URLs
ACCESSION_URL = re.compile(r'https://accession.nodc.noaa.gov/([0-9]+)$')
DATAISOID_URL = re.compile(r'https://data\.nodc\.noaa\.gov/cgi-bin/iso\?id=gov\.noaa\.nodc:([0-9]+)$')
TESTDATA_URL = re.compile(r'https://test\.nodc\.noaa\.gov/ocads/data/([0-9]+)\.xml$')


def getAlternateURL(myurl):
    # type: (str) -> (str, None)
    """
        Check the given URL for an alternative URL to use.  The alternative URL may fix errors
        when trying to access and interpret the XML using the given URL.  The XML at the alternative
        URL may also be simpler.

        :param myurl: URL to check

        :return: alternate URL to use, or None if no alternate found
    """
    # check if the URL matches one of the known patterns with an accession number
    match = ACCESSION_URL.match(myurl)
    if not match:
        match = DATAISOID_URL.match(myurl)
    if not match:
        match = TESTDATA_URL.match(myurl)
    if match:
        # provide the standard XML URL using the accession number
        return 'https://www.nodc.noaa.gov/ocads/data/' + match.group(1) + '.xml'
    return None


if __name__ == '__main__':
    if len(sys.argv) != 2:
        print('', file=sys.stderr)
        print('    Usage:  ' + sys.argv[0] + '  OCADS_Archive_DOIs.tsv', file=sys.stderr)
        print('', file=sys.stderr)
        print('    Reads expocodes (fourth column), URLs (fifth column), and DOIs (sixth column) ', file=sys.stderr)
        print('    from the TSV file, reads the XML from each URL to extract more expocodes and ', file=sys.stderr)
        print('    possibly a DOI, then writes out triplets of expocode, landing page URL, and ', file=sys.stderr)
        print('    DOI to standard output. ', file=sys.stderr)
        print('', file=sys.stderr)
        print('    If the value in the URL column for a row is not present or does not start with ', file=sys.stderr)
        print('    "http", the row is skipped.  If the URL is for an XML file, or an XML file can be ', file=sys.stderr)
        print('    obtained by adding ";view=xml;responseType=text/xml" to the URL, this XML file ', file=sys.stderr)
        print('    is examined for additional expocodes and possibly a DOI. ', file=sys.stderr)
        print('', file=sys.stderr)
        print('    If the value in the DOI column for a row is not present or does not resemble ', file=sys.stderr)
        print('    a DOI, then the XML file from the URL for the row is examined for a DOI. ', file=sys.stderr)
        print('', file=sys.stderr)
        print('    If the value in the expocode column for a row is a double-quoted (optional), ', file=sys.stderr)
        print('    comma-separated list of values instead of a single value, then each value in ', file=sys.stderr)
        print('    the list which resembles an expocode is used as an expocode associated with ', file=sys.stderr)
        print('    the URL and DOI for that row.  Furthermore, if an XML file can be obtained ', file=sys.stderr)
        print('    from the URL given in the row, any expocodes given in that XML file are also ', file=sys.stderr)
        print('    associated with the URL and DOI for that row. ', file=sys.stderr)
        print('', file=sys.stderr)
        sys.exit(1)
    tsvfile = open(sys.argv[1])

    try:
        for dataline in tsvfile:
            pieces = dataline.split('\t')

            print('', file=sys.stderr)
            print('Examining: "' + dataline.strip('\r\n') + '"', file=sys.stderr)
            sys.stderr.flush()

            if len(pieces) < 5:
                print('    Warning: ignoring entry; insufficient number of values', file=sys.stderr)
                continue
            if len(pieces) > 5:
                doi = getDOIFromValue(pieces[5])
            else:
                doi = None

            origurl = pieces[4].strip()
            if not origurl.startswith('http'):
                print('    Warning: ignoring entry; no URL found', file=sys.stderr)
                continue

            linkedobjs = None
            # check if there is a alternate URL which might work is the original fails (and might be simpler XML)
            alturl = getAlternateURL(origurl)
            # make sure the given URL is going to return XML
            if not origurl.endswith(('.xml', '.XML', '/xml')):
                origurl += ';view=xml;responseType=text/xml'

            # first try the given URL
            url = origurl
            linkedobjs = getXmlContent(url)
            # if there is an alternate URL and the original failed, try the alternate
            if alturl and not linkedobjs:
                url = alturl
                linkedobjs = getXmlContent(url)
            if not linkedobjs:
                print('    Warning: ignoring entry; problems accessing or interpreting the XML ', file=sys.stderr)
                print('        from the site: ' + origurl, file=sys.stderr)
                if alturl:
                    print('        or the site: ' + alturl, file=sys.stderr)
                continue

            givenexpos = getExpocodes(linkedobjs)
            for value in pieces[3].strip().strip('"').split(','):
                value = getExpocodeFromValue(value)
                if value and (value not in givenexpos):
                    givenexpos.add(value)
                    print('    Warning: expocode "' + value + '" not given in the XML', file=sys.stderr)
            if not givenexpos:
                print('    Warning: ignoring entry; no expocodes found', file=sys.stderr)
                continue

            doiSet = getDois(linkedobjs)
            # make sure any given DOI is present
            if doi and (doi not in doiSet):
                doiSet.add(doi)
                print('    Warning: DOI "' + doi + '" not given in the XML', file=sys.stderr)
            # allow missing DOIs since there will be a landing page
            # allow multiple DOIs - maybe okay?

            urlSet = getLandingLinks(linkedobjs)
            if len(urlSet) == 0:
                # just use the URL used here as the landing page URL
                pass
            elif len(urlSet) == 1:
                url = urlSet.pop()
            else:
                print('    Warning: Ignoring entry; multiple landing pages found: ', file=sys.stderr)
                for url in urlSet:
                    print('        ' + url, file=sys.stderr)
                continue

            for expo in givenexpos:
                for doi in doiSet:
                    print(expo + '\t' + url + '\t' + doi)

    finally:
        tsvfile.close()
