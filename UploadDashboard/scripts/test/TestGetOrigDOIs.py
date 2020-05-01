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
        self.assertIsNone(doi)
        doi = getorigdois.getDOIFromValue('no')
        self.assertIsNone(doi)
        mydoi = '10.3334/CDIAC/OTG.GO_SHIP_P16N_2015'
        doi = getorigdois.getDOIFromValue(mydoi)
        self.assertEqual(mydoi, doi)
        doi = getorigdois.getDOIFromValue('https://doi.org/' + mydoi)
        self.assertEqual(mydoi, doi)

    def test_MyObject(self):
        firstname = 'name/first'
        firstattrs = {'pos': 1}
        firstobj = getorigdois.MyObject(firstname, firstattrs, None)
        self.assertIsNotNone(firstobj)
        self.assertEqual(firstname, firstobj.fullname)
        self.assertEqual(firstattrs, firstobj.attrs)
        self.assertEqual('', firstobj.value)
        self.assertIsNone(firstobj.prevobj)
        self.assertIsNone(firstobj.nextobj)
        secname = 'name/second'
        secattrs = {'pos': 2}
        secobj = getorigdois.MyObject(secname, secattrs, firstobj)
        self.assertIsNotNone(secobj)
        self.assertEqual(secname, secobj.fullname)
        self.assertEqual(secattrs, secobj.attrs)
        self.assertEqual('', secobj.value)
        self.assertEqual(firstobj, secobj.prevobj)
        self.assertIsNone(secobj.nextobj)

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
        self.assertIsNone(thisobj.attrs)
        self.assertEqual('', thisobj.value)
        self.assertIsNotNone(thisobj.nextobj)

        prevobj = thisobj
        thisobj = thisobj.nextobj
        self.assertEqual('/metadata/name', thisobj.fullname)
        self.assertIsNone(thisobj.attrs)
        self.assertEqual('', thisobj.value)
        self.assertEqual(prevobj, thisobj.prevobj)
        self.assertIsNotNone(thisobj.nextobj)

        prevobj = thisobj
        thisobj = thisobj.nextobj
        self.assertEqual('/metadata/name/first', thisobj.fullname)
        self.assertEqual(firstattrs, thisobj.attrs)
        self.assertEqual('myfirstname', thisobj.value)
        self.assertEqual(prevobj, thisobj.prevobj)
        self.assertIsNotNone(thisobj.nextobj)

        prevobj = thisobj
        thisobj = thisobj.nextobj
        self.assertEqual('/metadata/name/second', thisobj.fullname)
        self.assertEqual(secattrs, thisobj.attrs)
        self.assertEqual('mysecondname', thisobj.value)
        self.assertEqual(prevobj, thisobj.prevobj)
        self.assertIsNotNone(thisobj.nextobj)

        prevobj = thisobj
        thisobj = thisobj.nextobj
        self.assertEqual('/metadata/address', thisobj.fullname)
        self.assertIsNone(thisobj.attrs)
        self.assertEqual('', thisobj.value)
        self.assertEqual(prevobj, thisobj.prevobj)
        self.assertIsNotNone(thisobj.nextobj)

        prevobj = thisobj
        thisobj = thisobj.nextobj
        self.assertEqual('/metadata/address/first', thisobj.fullname)
        self.assertEqual(firstattrs, thisobj.attrs)
        self.assertEqual('myfirstaddress', thisobj.value)
        self.assertEqual(prevobj, thisobj.prevobj)
        self.assertIsNotNone(thisobj.nextobj)

        prevobj = thisobj
        thisobj = thisobj.nextobj
        self.assertEqual('/metadata/address/second', thisobj.fullname)
        self.assertEqual(secattrs, thisobj.attrs)
        self.assertEqual('mysecondaddress', thisobj.value)
        self.assertEqual(prevobj, thisobj.prevobj)
        self.assertIsNone(thisobj.nextobj)

    def test_getXmlContentExpocodesDois(self):
        linkedobjs = getorigdois.getXmlContent("https://www.nodc.noaa.gov/ocads/data/0208441.xml")
        self.assertIsNotNone(linkedobjs)
        thisobj = linkedobjs;
        while thisobj:
            print(str(thisobj))
            thisobj = thisobj.nextobj
        expocodes = getorigdois.getExpocodes(linkedobjs)
        self.assertEqual(set(("35MV20190109",)), expocodes)
        dois = getorigdois.getDois(linkedobjs)
        self.assertEqual(set(("10.25921/0pmp-1r57",)), dois)
        urls = getorigdois.getLandingLinks(linkedobjs)
        self.assertEqual(set(("https://accession.nodc.noaa.gov/0208441",)), urls)

if __name__ == '__main__':
    unittest.main()
