package gov.noaa.pmel.socatmetadata.test;

import gov.noaa.pmel.socatmetadata.shared.Coverage;
import gov.noaa.pmel.socatmetadata.shared.MiscInfo;
import gov.noaa.pmel.socatmetadata.shared.SocatMetadata;
import gov.noaa.pmel.socatmetadata.shared.instrument.Instrument;
import gov.noaa.pmel.socatmetadata.shared.person.Investigator;
import gov.noaa.pmel.socatmetadata.shared.person.Submitter;
import gov.noaa.pmel.socatmetadata.shared.platform.Platform;
import gov.noaa.pmel.socatmetadata.shared.variable.Variable;
import gov.noaa.pmel.socatmetadata.translate.CdiacReader;
import org.junit.Before;
import org.junit.Test;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

/**
 * Many tests of the SocatMetadata class occur under CdiacReaderTest
 */
public class SocatMetadataTest {

    private SocatMetadata mdataFromCdiac;
    private Submitter submitter;
    private ArrayList<Investigator> investigators;
    private Platform platform;
    private Coverage coverage;
    private ArrayList<Instrument> instruments;
    private ArrayList<Variable> variables;
    private MiscInfo miscInfo;

    @Before
    public void setUp() throws Exception {
        CdiacReader reader = new CdiacReader(new StringReader(CdiacReaderTest.AOML_CDIAC_XML_DATA_STRING));
        mdataFromCdiac = reader.createSocatMetadata();
        submitter = mdataFromCdiac.getSubmitter();
        investigators = mdataFromCdiac.getInvestigators();
        platform = mdataFromCdiac.getPlatform();
        coverage = mdataFromCdiac.getCoverage();
        instruments = mdataFromCdiac.getInstruments();
        variables = mdataFromCdiac.getVariables();
        miscInfo = mdataFromCdiac.getMiscInfo();
    }

    @Test
    public void testDuplicate() {
        SocatMetadata mdata = new SocatMetadata();
        SocatMetadata dup = (SocatMetadata) (mdata.duplicate(null));
        assertEquals(mdata, dup);
        assertNotSame(mdata, dup);

        mdata.setSubmitter(submitter);
        mdata.setInvestigators(investigators);
        mdata.setPlatform(platform);
        mdata.setCoverage(coverage);
        mdata.setInstruments(instruments);
        mdata.setVariables(variables);
        mdata.setMiscInfo(miscInfo);
        assertNotEquals(mdata, dup);

        dup = (SocatMetadata) (mdata.duplicate(null));
        assertEquals(mdata, dup);
        assertNotSame(mdata, dup);
        assertNotSame(mdata.getSubmitter(), dup.getSubmitter());
        assertNotSame(mdata.getInvestigators(), dup.getInvestigators());
        assertNotSame(mdata.getPlatform(), dup.getPlatform());
        assertNotSame(mdata.getCoverage(), dup.getCoverage());
        assertNotSame(mdata.getInstruments(), dup.getInstruments());
        assertNotSame(mdata.getVariables(), dup.getVariables());
        assertNotSame(mdata.getMiscInfo(), dup.getMiscInfo());
    }

    @Test
    public void testHashCodeEquals() {
        SocatMetadata first = new SocatMetadata();
        assertFalse(first.equals(null));
        assertFalse(first.equals(""));
        assertFalse(first.equals(mdataFromCdiac));

        SocatMetadata second = new SocatMetadata();
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setSubmitter(submitter);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setSubmitter(submitter);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setInvestigators(investigators);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setInvestigators(investigators);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setPlatform(platform);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setPlatform(platform);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setCoverage(coverage);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setCoverage(coverage);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setInstruments(instruments);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setInstruments(instruments);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setVariables(variables);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setVariables(variables);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setMiscInfo(miscInfo);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setMiscInfo(miscInfo);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        assertEquals(first.hashCode(), mdataFromCdiac.hashCode());
        assertTrue(first.equals(mdataFromCdiac));
    }

    @Test
    public void testSerialization() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(mdataFromCdiac);
        oos.close();
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        SocatMetadata mdata = (SocatMetadata) ois.readObject();
        assertEquals(mdataFromCdiac, mdata);
        assertNotSame(mdataFromCdiac, mdata);
    }

    @Test
    public void testXmlEncodeDecode() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLEncoder xenc = new XMLEncoder(bos);
        xenc.writeObject(mdataFromCdiac);
        xenc.close();
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        XMLDecoder xdec = new XMLDecoder(bis);
        SocatMetadata mdata = (SocatMetadata) xdec.readObject();
        assertEquals(mdataFromCdiac, mdata);
        assertNotSame(mdataFromCdiac, mdata);
    }

}
