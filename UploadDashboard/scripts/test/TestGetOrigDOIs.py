import unittest

import getorigdois


class GetOrigDOIsTestCase(unittest.TestCase):
    def test_resemblesExpocode(self):
        self.assertTrue(getorigdois.resemblesExpocode('ABCD20041215'))
        self.assertTrue(getorigdois.resemblesExpocode('ABCDE20041215'))
        self.assertFalse(getorigdois.resemblesExpocode('multiple'))

    def test_getDOIFromValue(self):
        mydoi = '10.25921/0pmp-1r57'
        doi = getorigdois.getDOIFromValue(mydoi)
        self.assertEqual(mydoi, doi)
        doi = getorigdois.getDOIFromValue('https://doi.org/' + mydoi)
        self.assertEqual(mydoi, doi)
        doi = getorigdois.getDOIFromValue('yes')
        self.assertEqual(None, doi)
        doi = getorigdois.getDOIFromValue('no')
        self.assertEqual(None, doi)
        mydoi = '10.3334/CDIAC/OTG.GO_SHIP_P16N_2015'
        doi = getorigdois.getDOIFromValue(mydoi)
        self.assertEqual(mydoi, doi)
        doi = getorigdois.getDOIFromValue('https://doi.org/' + mydoi)
        self.assertEqual(mydoi, doi)

    def test_MyObject(self):
        firstname = 'name/first'
        firstattrs = {'pos': 1}
        firstobj = getorigdois.MyObject(firstname, firstattrs, None)
        self.assertNotEqual(None, firstobj)
        self.assertEqual(firstname, firstobj.fullname)
        self.assertEqual(firstattrs, firstobj.attrs)
        self.assertEqual('', firstobj.value)
        self.assertEqual(None, firstobj.prevobj)
        self.assertEqual(None, firstobj.nextobj)
        secname = 'name/second'
        secattrs = {'pos': 2}
        secobj = getorigdois.MyObject(secname, secattrs, firstobj)
        self.assertNotEqual(None, secobj)
        self.assertEqual(secname, secobj.fullname)
        self.assertEqual(secattrs, secobj.attrs)
        self.assertEqual('', secobj.value)
        self.assertEqual(firstobj, secobj.prevobj)
        self.assertEqual(None, secobj.nextobj)

    def test_LinkedObjectsContentHandler(self):
        firstattrs = {'pos': 1}
        secattrs = {'pos': 2}
        handler = getorigdois.LinkedObjectsContentHandler()
        handler.startElement('metadata', None)
        handler.startElement('name', None)
        handler.startElement('first', firstattrs)
        handler.characters('myfirstname')
        handler.endElement('first')
        handler.startElement('second', secattrs)
        handler.characters('mysecondname')
        handler.endElement('second')
        handler.endElement('name')
        handler.startElement('address', None)
        handler.startElement('first', firstattrs)
        handler.characters('myfirstaddress')
        handler.endElement('first')
        handler.startElement('second', secattrs)
        handler.characters('mysecondaddress')
        handler.endElement('second')
        handler.endElement('address')
        handler.endElement('metadata')

        thisobj = handler.getLinkedObjects()
        self.assertEqual('/metadata', thisobj.fullname)
        self.assertEqual(None, thisobj.attrs)
        self.assertEqual('', thisobj.value)
        self.assertNotEqual(None, thisobj.nextobj)

        prevobj = thisobj
        thisobj = thisobj.nextobj
        self.assertEqual('/metadata/name', thisobj.fullname)
        self.assertEqual(None, thisobj.attrs)
        self.assertEqual('', thisobj.value)
        self.assertEqual(prevobj, thisobj.prevobj)
        self.assertNotEqual(None, thisobj.nextobj)

        prevobj = thisobj
        thisobj = thisobj.nextobj
        self.assertEqual('/metadata/name/first', thisobj.fullname)
        self.assertEqual(firstattrs, thisobj.attrs)
        self.assertEqual('myfirstname', thisobj.value)
        self.assertEqual(prevobj, thisobj.prevobj)
        self.assertNotEqual(None, thisobj.nextobj)

        prevobj = thisobj
        thisobj = thisobj.nextobj
        self.assertEqual('/metadata/name/second', thisobj.fullname)
        self.assertEqual(secattrs, thisobj.attrs)
        self.assertEqual('mysecondname', thisobj.value)
        self.assertEqual(prevobj, thisobj.prevobj)
        self.assertNotEqual(None, thisobj.nextobj)

        prevobj = thisobj
        thisobj = thisobj.nextobj
        self.assertEqual('/metadata/address', thisobj.fullname)
        self.assertEqual(None, thisobj.attrs)
        self.assertEqual('', thisobj.value)
        self.assertEqual(prevobj, thisobj.prevobj)
        self.assertNotEqual(None, thisobj.nextobj)

        prevobj = thisobj
        thisobj = thisobj.nextobj
        self.assertEqual('/metadata/address/first', thisobj.fullname)
        self.assertEqual(firstattrs, thisobj.attrs)
        self.assertEqual('myfirstaddress', thisobj.value)
        self.assertEqual(prevobj, thisobj.prevobj)
        self.assertNotEqual(None, thisobj.nextobj)

        prevobj = thisobj
        thisobj = thisobj.nextobj
        self.assertEqual('/metadata/address/second', thisobj.fullname)
        self.assertEqual(secattrs, thisobj.attrs)
        self.assertEqual('mysecondaddress', thisobj.value)
        self.assertEqual(prevobj, thisobj.prevobj)
        self.assertEqual(None, thisobj.nextobj)

    def test_getAlternateURL(self):
        alturl = getorigdois.getAlternateURL('https://www.nodc.noaa.gov/ocads/data/0208441.xml')
        self.assertEqual(None, alturl)
        alturl = getorigdois.getAlternateURL('https://data.nodc.noaa.gov/cgi-bin/iso?id=gov.noaa.nodc:0115402')
        self.assertEqual('https://www.nodc.noaa.gov/ocads/data/0115402.xml', alturl)
        alturl = getorigdois.getAlternateURL('https://accession.nodc.noaa.gov/0163186')
        self.assertEqual('https://www.nodc.noaa.gov/ocads/data/0163186.xml', alturl)
        alturl = getorigdois.getAlternateURL('https://test.nodc.noaa.gov/ocads/data/0169950.xml')
        self.assertEqual('https://www.nodc.noaa.gov/ocads/data/0169950.xml', alturl)

    def test_getXmlContentExpocodesDois(self):
        # URL that gives the simpler XML
        linkedobjs = getorigdois.getXmlContent('https://www.nodc.noaa.gov/ocads/data/0208441.xml')
        self.assertNotEqual(None, linkedobjs)
        expocodes = getorigdois.getExpocodes(linkedobjs)
        self.assertEqual(set(('35MV20190109',)), expocodes)
        dois = getorigdois.getDois(linkedobjs)
        self.assertEqual(set(('10.25921/0pmp-1r57',)), dois)
        urls = getorigdois.getLandingLinks(linkedobjs)
        self.assertEqual(set(('https://accession.nodc.noaa.gov/0208441',)), urls)

        # URL that gives the more complex XML
        linkedobjs = getorigdois.getXmlContent('https://data.nodc.noaa.gov/cgi-bin/iso' +
                                               '?id=gov.noaa.nodc:0115402;view=xml;responseType=text/xml')
        self.assertNotEqual(None, linkedobjs)
        expocodes = getorigdois.getExpocodes(linkedobjs)
        self.assertEqual(set(('316420060713', '316420070829', '316420081014', '316420081209',
                              '316420090902', '316420100908', '316420110706', '316420120822',
                              '316420131105', '316420140925', '316420160107', '316420161115')), expocodes)
        dois = getorigdois.getDois(linkedobjs)
        self.assertEqual(set(('10.3334/cdiac/otg.tsm_nh_70w_43n',)), dois)
        urls = getorigdois.getLandingLinks(linkedobjs)
        self.assertEqual(set(('https://accession.nodc.noaa.gov/0115402',)), urls)

        # the following is a problematic so need special rewriting of the URLs
        url = getorigdois.getAlternateURL('https://accession.nodc.noaa.gov/0163186')
        self.assertNotEqual(None, url)
        linkedobjs = getorigdois.getXmlContent(url)
        self.assertNotEqual(None, linkedobjs)
        expocodes = getorigdois.getExpocodes(linkedobjs)
        self.assertEqual(set(('325020131025',)), expocodes)
        dois = getorigdois.getDois(linkedobjs)
        self.assertEqual(set(('10.3334/cdiac/otg.goship_p21_325020131025',)), dois)
        urls = getorigdois.getLandingLinks(linkedobjs)
        self.assertEqual(set(('https://accession.nodc.noaa.gov/0163186',)), urls)

        # the following is a problematic so need special rewriting of the URLs
        url = getorigdois.getAlternateURL('https://test.nodc.noaa.gov/ocads/data/0169950.xml')
        self.assertNotEqual(None, url)
        linkedobjs = getorigdois.getXmlContent(url)
        self.assertNotEqual(None, linkedobjs)
        expocodes = getorigdois.getExpocodes(linkedobjs)
        self.assertEqual(set(('PANC20160103', 'PANC20160125', 'PANC20160211', 'PANC20160302',
                              'PANC20160319', 'PANC20160412', 'PANC20160501', 'PANC20160525',
                              'PANC20160608', 'PANC20160625', 'PANC20160722', 'PANC20160826',
                              'PANC20160911', 'PANC20160928', 'PANC20161014', 'PANC20161031',
                              'PANC20161117', 'PANC20161203',)), expocodes)
        dois = getorigdois.getDois(linkedobjs)
        self.assertEqual(set(('10.7289/v5kd1w5w',)), dois)
        urls = getorigdois.getLandingLinks(linkedobjs)
        self.assertEqual(set(('https://accession.nodc.noaa.gov/0169950',)), urls)

        # the following does not have a DOI at this time
        url = getorigdois.getAlternateURL('https://data.nodc.noaa.gov/cgi-bin/iso?id=gov.noaa.nodc:0163181')
        self.assertNotEqual(None, url)
        linkedobjs = getorigdois.getXmlContent(url)
        self.assertNotEqual(None, linkedobjs)
        expocodes = getorigdois.getExpocodes(linkedobjs)
        self.assertEqual(set(('09AR20160111',)), expocodes)
        dois = getorigdois.getDois(linkedobjs)
        self.assertEqual(set(), dois)
        urls = getorigdois.getLandingLinks(linkedobjs)
        self.assertEqual(set(('https://accession.nodc.noaa.gov/0163181',)), urls)

        # repeat with the more complex XML
        linkedobjs = getorigdois.getXmlContent('https://data.nodc.noaa.gov/cgi-bin/iso' +
                                               '?id=gov.noaa.nodc:0163181;view=xml;responseType=text/xml')
        self.assertNotEqual(None, linkedobjs)
        expocodes = getorigdois.getExpocodes(linkedobjs)
        self.assertEqual(set(('09AR20160111',)), expocodes)
        dois = getorigdois.getDois(linkedobjs)
        self.assertEqual(set(), dois)
        urls = getorigdois.getLandingLinks(linkedobjs)
        self.assertEqual(set(('https://accession.nodc.noaa.gov/0163181',)), urls)


if __name__ == '__main__':
    unittest.main()
