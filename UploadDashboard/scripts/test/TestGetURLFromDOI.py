import unittest
import time

import geturlfromdoi


class Test(unittest.TestCase):

    def test_get_url_from_doi(self):
        doi = ' '
        url = geturlfromdoi.getUrlFromDoi(doi)
        self.assertEqual(None, url)

        doi = '10.1594/PANGAEA.849862'
        url = geturlfromdoi.getUrlFromDoi(doi)
        self.assertEqual('https://doi.pangaea.de/10.1594/PANGAEA.849862', url)

        time.sleep(1)

        doi = '10.7289/V5TT4P5V'
        url = geturlfromdoi.getUrlFromDoi(doi)
        self.assertEqual('https://accession.nodc.noaa.gov/0162432', url)

        time.sleep(1)

        doi = '10.3334/CDIAC/OTG.OA_VOS_POLARSTERN_2012'
        url = geturlfromdoi.getUrlFromDoi(doi)
        self.assertEqual('https://accession.nodc.noaa.gov/0157350', url)

        time.sleep(1)

        doi = '12.3456/GARBAGE'
        url = geturlfromdoi.getUrlFromDoi(doi)
        self.assertEqual(None, url)

if __name__ == '__main__':
    unittest.main()
