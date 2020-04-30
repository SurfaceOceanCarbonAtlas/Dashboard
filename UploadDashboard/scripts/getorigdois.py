#! /usr/bin/python

from __future__ import print_function
import sys
import re
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
# skip any "DOIs" that do not match (start with) usual DOI pattern
DOI_REGEX = re.compile(r'[0-9]+\.[0-9]+/[A-Za-z0-9/_\.]+')


def resemblesExpocode(value):
    '''
        Returns: whether the given value resembles an expocode
    '''
    return EXPO_REGEX.match(value)


def getDOIFromValue(value):
    '''
        Returns: the "bare" DOI value if the value resembles a DOI,
                 or None if the value does not resemble a DOI
    '''
    if value.startswith('https://doi.org/'):
        doi = value[16:]
    else
        doi = value
    if not DOI_REGEX.match(doi):
        doi = None
    return doi


class LinkedObjectsContentHandler(xml.sax.handler.ContentHandler):
    '''
        Extracts information from parsed XML content into a doubly-linked list of Objects that 
        can be retrieved from this instance.  Each object in this linked list has attributes:
            fullname: the "full path" name of the element
            attrs: attributes associated with the element
            value: value associated with that element
            prevobj: prior object in the linked list
            nextobj: next object in the linked list
    '''

    def __init__(self):
        self.rootobj = Object()
        self.rootobj.fullname = ''
        self.rootobj.attrs = None
        self.rootobj.data = None
        self.rootobj.prevobj = None
        self.rootobj.nextobj = None
        self.currobj = self.rootobj
        self.fullname = ''

    def startElement(self, name, attrs):
        currobj = Object()
        self.fullname += '/' + name
        currobj.fullname = self.fullname
        currobj.attrs = attrs
        currobj.data = None
        currobj.prevobj = self.currobj
        currobj.nextobj = None
        self.currobj.nextobj = currobj
        self.currobj = currobj

    def startElementNS(self, name, qname, attrs):
        self.startElement(qname, attrs)

    def endElement(self, name):
        self.fullname = self.currobj.prevobj.fullname

    def endElementNS(self, name, qname):
        self.endElement(qname)

    def characters(self, value):
        self.currobj.value = value

    def getLinkedObjects(self):
        return self.rootobj.nextobj


def getXmlContent(url):
    '''
        Reads the XML file at the given URL and generates a doubly-linked list
        of Objects containing the contents of the XML.  Each object in this
        linked list has attributes:
            fullname: the "full path" name of the element
            attrs: attributes associated with the element
            value: value associated with that element
            prevobj: prior object in the linked list
            nextobj: next object in the linked list

        Returns: the doubly-linked list of Object containing the contents of the XML
    '''
    handler = LinkedObjectsContentHandler()
    parser = xml.sax.make_parser()
    parser.setContentHandler(handler)
    try:
        if sys.version_info[0] > 2:
            req = urllib.request.urlopen(url)
        else
            req = urllib2.urlopen(url)
    except Exception as ex:
        print('Problems accessing URL ' + url + " : " + repr(ex), file=sys.stderr)
        return None
    try:
        parser.parse(req)
    except Exception as ex:
        # Probably not an XML document
        print('Problems reading XML from ' + url + " : " + repr(ex), file=sys.stderr)
        return None
    return handler.getLinkedObjects()


def getLandingLinks(linkedObjects):
    '''
        Returns: list of land page links found in the linked-list of objects; 
                 may be empty or have multiple entries, but should be a singleton list
    '''
    links = [ ]
    for (obj = linkedObjects; obj is not None; obj = obj.nextobj):
        if obj.fullname == '/metadata/link_landing':
            value = obj.value
            if value.startswith('http')
                links.append(value)
        elif obj.fullname == '/gmi:MI_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:identifier/gmd:MD_Identifier/gmd:code/gmx:Anchor':
            value = obj.attrs.getValueByQName('xlink:title')
            if not value:
                value = obj.attrs.getValue('xlink:title')
            if value == 'NCEI Accession Number':
                value = obj.attrs.getValueByQName('xlink:href')
                if not value:
                    value = obj.attrs.getValue('xlink:href')
                if value.startswith('http')
                    links.append(value)
     return links


def getDois(linkedObjects):
    '''
        Returns: list of DOIs found in the linked-list of objects; may be empty or
                 have multiple entries, but should be a singleton list
    '''
    dois = [ ]
    for (obj = linkedObjects; obj is not None; obj = obj.nextobj):
        if obj.fullname == '/metadata/doi':
            value = getDOIFromValue(obj.value)
            if value is not None:
                dois.append(value)
         elif obj.fullname == '/gmi:MI_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:identifier/gmd:MD_Identifier/gmd:code/gmx:Anchor':
            value = obj.attrs.getValueByQName('xlink:title')
            if not value:
                value = obj.attrs.getValue('xlink:title')
            if value == 'DOI':
                value = getDOIFromValue(obj.value)
                if value is not None:
                    dois.append(value)
    return dois


def getExpocodes(linkedObjects):
    '''
        Returns: list of expocodes found in the linked-list of objects; may be empty
    '''
    expocodes = [ ]
    tmplist = [ ]
    for (obj = linkedObjects; obj is not None; obj = obj.nextobj):
        if obj.fullname == '/metadata/expocode':
            value = obj.value
            if resemblesExpocode(value):
                expocodes.append(value)
        elif obj.fullname == '/gmi:MI_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords':
            # clear the list whenever a new gmd:MD_Keywords is found
            tmplist = [ ]
        elif obj.fullname == '/gmi:MI_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/gco:CharacterString'
            # possibly an expocode, add to tmplist for now
            value = obj.value
            if resemblesExpocode(value):
                tmplist.append(value)
        elif obj.fullname == '/gmi:MI_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:thesaurusName/gmd:CI_Citation/gmd:title/gco:CharacterString':
            if value == 'EXPOCODE':
                # this set of "keywords" were expocodes; append expocodes in tmplist to expocodes
                expocodes.extend(tmplist)
                tmplist = [ ]
    return expocodes


if __name__ == '__main__':
    if len(sys.argv) != 2:
        print('', file=sys.stderr)
        print('    Usage:  ' + sys.argv[0] + '  OCADS_Archive_DOIs.tsv', file=sys.stderr)
        print('', file=sys.stderr)
        print('    Reads expocodes (fourth column), URLs (fifth column), and DOIs (sixth column) from ', file=sys.stderr)
        print('    the TSV file, reads the XML from each URL to extract more expocodes and possibly a ', file=sys.stderr)
        print('    DOI, then writes out triplets of expocode, URL, and DOI to standard output. ', file=sys.stderr)
        print('', file=sys.stderr)
        print('    If the value in the URL column for a row is not present or does not start with "http", ', file=sys.stderr)
        print('    the row is skipped.  If the URL is for an XML file, or an XML file can be obtained by ', file=sys.stderr)
        print('    adding ";view=xml;responseType=text/xml" to the URL, this XML file is examined for ', file=sys.stderr)
        print('    additional expocodes and possibly a DOI. ', file=sys.stderr)
        print('', file=sys.stderr)
        print('    If the value in the DOI column for a row is not present or does not resemble a DOI, ', file=sys.stderr)
        print('    then the XML file from the URL for the row is examined for a DOI.  If no DOI can be ', file=sys.stderr)
        print('    found, the row is skipped. ', file=sys.stderr)
        print('', file=sys.stderr)
        print('    If the value in the expocode column for a row is a double-quoted (optional), comma-', file=sys.stderr)
        print('    separated list of values instead of a single value, then each value in that list that ', file=sys.stderr)
        print('    resembles an expocode is used as an expocode associated with the URL and DOI for that ', file=sys.stderr)
        print('    row.  Furthermore, if an XML file can be obtained from the URL given in the row, any ', file=sys.stderr)
        print('    expocodes given in that XML file are also associated with the URL and DOI for that row. ', file=sys.stderr)
        print('    with the URL and DOI. ', file=sys.stderr)
        print('', file=sys.stderr)
        sys.exit(1)
    problems = False
    urldoi = open(sys.argv[1])
    try:
        for dataline in urldoi:
            pieces = dataline.split('\t')
            try:
                doi = pieces[5].strip()
                if doi.startswith('https://doi.org/'):
                    doi = doi[16:]
                if not DOI_REGEX.match(doi):
                    doi = None
            except Exception:
                doi = None
            url = pieces[4].strip()
            if not url.startswith('http'):
                print('Ignoring entry: "' + dataline.strip() + '"', file=sys.stderr)
                problems = True
                continue
            givenexpos = pieces[3].strip().strip('"').split(',')
            for expo in givenexpos:
                expo = expo.strip()
                if doi and EXPO_REGEX.match(expo):
                    print(expo + '\t' + url + '\t' + doi)
            # Attempt to read an XML document given by the URL to obtain additional expocodes to output
            if printExposInUrl(url, doi) != 0:
                print('Problems with entry: ' + dataline.strip(), file=sys.stderr)
                problems = True
    finally:
        urldoi.close()
    if problems:
        sys.exit(1)
