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
EXPO_REGEX = re.compile(r'[A-Z0-9]{4,5}?[0-9]{8}')


def resemblesExpocode(myvalue):
    """
        Returns: whether the given value resembles an expocode
    """
    return EXPO_REGEX.match(myvalue)


# skip any "DOIs" that do not match (start with) usual DOI pattern
DOI_REGEX = re.compile(r'[0-9]+\.[0-9]+/[A-Z0-9/_.]+')


def getDOIFromValue(myvalue):
    """
        Returns: the "bare" DOI value if the value resembles a DOI,
                 or None if the value does not resemble a DOI
    """
    if myvalue.startswith('https://doi.org/'):
        mydoi = myvalue[16:]
    else:
        mydoi = myvalue
    mydoi = mydoi.upper()
    if not DOI_REGEX.match(mydoi):
        mydoi = None
    return mydoi


class MyObject(object):
    def __init__(self, fullname, attrs, prevobj):
        self.fullname = fullname
        self.attrs = attrs
        self.value = ''
        self.prevobj = prevobj
        self.nextobj = None
        self.parent = None

    def __str__(self):
        return '{ fullname: "' + self.fullname + '", attrs: "' + \
               str(self.attrs) + '", value: "' + self.value + '" }'


class LinkedObjectsContentHandler(xml.sax.handler.ContentHandler):
    """
        Extracts information from parsed XML content into a doubly-linked list of objects that
        can be retrieved from this instance.  Each object in this linked list has attributes:
            fullname: the "full path" name of the element
            attrs: attributes associated with the element
            value: value associated with that element
            prevobj: prior object in the linked list
            nextobj: next object in the linked list
    """

    def __init__(self):
        # calling the superclass constructor fails in production
        # super(LinkedObjectsContentHandler, self).__init__()
        self.__rootobj = MyObject('', None, None)
        self.__rootobj.parent = self.__rootobj
        self.__currobj = self.__rootobj
        self.__parent = self.__rootobj

    def startElement(self, name, attrs):
        currobj = MyObject(self.__parent.fullname + '/' + name, attrs, self.__currobj)
        currobj.parent = self.__parent
        self.__currobj.nextobj = currobj
        self.__currobj = currobj
        self.__parent = currobj

    def startElementNS(self, name, qname, attrs):
        self.startElement(qname, attrs)

    def endElement(self, name):
        self.__parent = self.__parent.parent

    def endElementNS(self, name, qname):
        self.endElement(qname)

    def characters(self, data):
        self.__currobj.value += data

    def getLinkedObjects(self):
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
    """
        Reads the XML file at the given URL and generates a doubly-linked list
        of objects containing the contents of the XML.  Each object in this
        linked list has attributes:
            fullname: the "full path" name of the element
            attrs: attributes associated with the element
            value: value associated with that element
            prevobj: prior object in the linked list
            nextobj: next object in the linked list

        Returns: the doubly-linked list of objects containing the contents of the XML,
                  or None if the there were problems accessing the URL or interpreting
                  its contents
    """
    handler = LinkedObjectsContentHandler()
    parser = xml.sax.make_parser()
    parser.setContentHandler(handler)
    try:
        if sys.version_info[0] > 2:
            req = urllib.request.urlopen(myurl)
        else:
            req = urllib2.urlopen(myurl)
    except Exception as ex:
        print('Problems accessing URL ' + myurl + " : " + repr(ex), file=sys.stderr)
        return None
    try:
        parser.parse(req)
    except Exception as ex:
        # Probably not an XML document
        print('Problems reading XML from ' + myurl + " : " + repr(ex), file=sys.stderr)
        return None
    return handler.getLinkedObjects()


def getLandingLinks(mylinkedobjs):
    """
        Returns: set of landing page links found in the linked-list of objects;
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
    """
        Returns: set of DOIs found in the linked-list of objects; may be empty or
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
                if myvalue is not None:
                    dois.add(myvalue.upper())
        obj = obj.nextobj
    return dois


def getExpocodes(mylinkedobjs):
    """
        Returns: set of expocodes found in the linked-list of objects; may be empty
    """
    expocodes = set()
    tmpset = set()
    obj = mylinkedobjs
    while obj:
        if obj.fullname == '/metadata/expocode':
            myvalue = obj.value.strip().upper()
            if resemblesExpocode(myvalue):
                expocodes.add(myvalue)
        elif obj.fullname == '/gmi:MI_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/' + \
                'gmd:descriptiveKeywords/gmd:MD_Keywords':
            # clear tmpset whenever a new gmd:MD_Keywords is found
            tmpset = set()
        elif obj.fullname == '/gmi:MI_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/' + \
                'gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/gco:CharacterString':
            # possibly an expocode, add to tmpset for now
            myvalue = obj.value.strip().upper()
            if resemblesExpocode(myvalue):
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

    problems = False
    try:
        for dataline in tsvfile:
            pieces = dataline.split('\t')

            if len(pieces) < 5:
                print('Ignoring entry: "' + dataline + '"', file=sys.stderr)
                print('    insufficient number of values')
                problems = True
                continue
            if len(pieces) > 5:
                doi = getDOIFromValue(pieces[5].strip())
            else:
                doi = None

            url = pieces[4].strip()
            if not url.startswith('http'):
                print('Ignoring entry: "' + dataline.strip() + '"', file=sys.stderr)
                print('    no URL found', file=sys.stderr)
                problems = True
                continue

            alturl = getAlternateURL(url)
            if alturl:
                url = alturl
            linkedobjs = getXmlContent(url)

            givenexpos = getExpocodes(linkedobjs)
            # make sure the given expocodes are found in the XML
            numgiven = len(givenexpos)
            for value in pieces[3].strip().strip('"').split(','):
                value = value.strip().upper()
                if resemblesExpocode(value):
                    givenexpos.add(value)
            if (numgiven < 1) or (len(givenexpos) != numgiven):
                print('Ignoring entry: "' + dataline.strip() + '"', file=sys.stderr)
                print('    provided expocodes not found in XML', file=sys.stderr)
                problems = True
                continue

            doiSet = getDois(linkedobjs)
            # make sure the given DOI matches that in the XML
            if doi:
                doiSet.add(doi.upper())
            if len(doiSet) == 1:
                doi = doiSet.pop()
            elif len(doiSet) > 1:
                print('Ignoring entry: "' + dataline.strip() + '"', file=sys.stderr)
                print('    multiple DOIs found: ' + str(doiSet), file=sys.stderr)
                problems = True
                continue
            else:
                doi = ''

            urlSet = getLandingLinks(linkedobjs)
            if len(urlSet) == 1:
                url = urlSet.pop()
            elif len(urlSet) > 1:
                print('Ignoring entry: "' + dataline.strip() + '"', file=sys.stderr)
                print('    multiple landing pages found: ' + str(urlSet), file=sys.stderr)
                problems = True
                continue
            else:
                # just use the URL used here as the landing page URL
                pass

            for expo in givenexpos:
                print(expo + '\t' + url + '\t' + doi)
    finally:
        tsvfile.close()

    if problems:
        sys.exit(1)
