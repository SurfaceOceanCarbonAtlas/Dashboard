#! /usr/bin/env python

from __future__ import print_function

import sys


def adddatacounts(mytsvfile, myyearqccount):
    # type: (iter, dict) -> int
    """
        Reads the TSV data given in mytsvfile and increments the counts in myyearqccount,
        a dictionary giving the number of observations for each year and QC flag.
        Assumes the header to the data in mytsvfile starts with the line:
            Expocode<tab>version<tab>SOCAT_DOI<tab>QC_Flag<tab>yr<tab>

        :param mytsvfile: File containing the TSV data (SOCAT global synthesis data file)

        :param myyearqccount:  dictionary whose keys are the four-digit year followed by
                              the QC flag (e.g., '2010B') and whose values are the number
                              of observations for that year and QC flag.

        :return: the number of data lines processed
    """
    datafound = False
    linecount = 0
    columncount = 0
    for dataline in mytsvfile:
        if not datafound:
            if dataline.startswith('Expocode\tversion\tSOCAT_DOI\tQC_Flag\tyr\t'):
                datafound = True
                columncount = len(dataline.split('\t'))
            continue
        values = dataline.split('\t')
        if len(values) != columncount:
            raise ValueError('inconsistent number of data columns (%d vs %d)' % (len(values), columncount))
        myyearqc = values[4] + values[3]
        mycount = myyearqccount.get(myyearqc, 0)
        myyearqccount[myyearqc] = mycount + 1
        linecount += 1
    return linecount


if __name__ == '__main__':
    if (len(sys.argv) < 2) or sys.argv[1].startswith('-'):
        print('', file=sys.stderr)
        print('Usage:  %s  SOCATvN.tsv  [ SOCATvN_FlagE.tsv ... ] ' % sys.argv[0], file=sys.stderr)
        print('', file=sys.stderr)
        print('Reads the given SOCAT synthesis files and prints the accumulated ', file=sys.stderr)
        print('number of observations for each year and QC flag in these files. ', file=sys.stderr)
        print('', file=sys.stderr)
        sys.exit(1)

    yearqccount = {}
    for tsvfilename in sys.argv[1:]:
        try:
            tsvfile = open(tsvfilename, 'r')
            try:
                if adddatacounts(tsvfile, yearqccount) < 1:
                    raise IOError('no data values read')
            finally:
                tsvfile.close()
        except Exception as ex:
            print('Problems reading data from %s: %s' % (tsvfilename, str(ex)), file=sys.stderr)
            sys.exit(1)

    yearqc = list(yearqccount.keys())
    yearqc.sort()
    for key in yearqc:
        year = key[0:4]
        qc = key[4]
        count = yearqccount[key]
        print('%s\t%s\t%d' % (year, qc, count))

    sys.exit(0)
