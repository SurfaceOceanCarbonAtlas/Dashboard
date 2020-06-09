import time
import unittest

import geturlfromdoi


class Test(unittest.TestCase):

    def test_get_url_from_doi(self):
        doiurldict = {}

        doi = ' '
        url = geturlfromdoi.getUrlFromDoi(doi, doiurldict)
        self.assertEqual(None, url)

        doi = '10.1594/PANGAEA.849862'
        url = geturlfromdoi.getUrlFromDoi(doi, doiurldict)
        self.assertEqual('https://doi.pangaea.de/10.1594/PANGAEA.849862', url)

        time.sleep(1)

        doi = '10.7289/V5TT4P5V'
        url = geturlfromdoi.getUrlFromDoi(doi, doiurldict)
        self.assertEqual('https://accession.nodc.noaa.gov/0162432', url)

        time.sleep(1)

        doi = '10.3334/CDIAC/OTG.OA_VOS_POLARSTERN_2012'
        url = geturlfromdoi.getUrlFromDoi(doi, doiurldict)
        self.assertEqual('https://accession.nodc.noaa.gov/0157350', url)

        time.sleep(1)

        fakedoi = '12.3456/GARBAGE'
        url = geturlfromdoi.getUrlFromDoi(fakedoi, doiurldict)
        self.assertEqual(None, url)

        fakeurl = "https://garbage.in/garbage.out"
        doiurldict[fakedoi] = fakeurl
        url = geturlfromdoi.getUrlFromDoi(fakedoi, doiurldict)
        self.assertEqual(fakeurl, url)


if __name__ == '__main__':
    unittest.main()
