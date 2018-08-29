package gov.noaa.pmel.sdimetadata;

import gov.noaa.pmel.sdimetadata.instrument.Analyzer;
import gov.noaa.pmel.sdimetadata.instrument.Instrument;
import gov.noaa.pmel.sdimetadata.instrument.Sampler;
import gov.noaa.pmel.sdimetadata.instrument.Sensor;
import gov.noaa.pmel.sdimetadata.person.Investigator;
import gov.noaa.pmel.sdimetadata.person.Submitter;
import gov.noaa.pmel.sdimetadata.variable.Variable;

import java.util.ArrayList;

public class SDIMetadata implements Cloneable {

    protected Submitter submitter;
    protected ArrayList<Investigator> investigators;
    protected Platform platform;
    protected Coverage coverage;
    protected ArrayList<Sampler> samplers;
    protected ArrayList<Analyzer> analyzers;
    protected ArrayList<Variable> variables;
    protected MiscInfo miscInfo;

    public SDIMetadata() {
        submitter = new Submitter();
        investigators = new ArrayList<Investigator>();
        platform = new Platform();
        coverage = new Coverage();
        samplers = new ArrayList<Sampler>();
        analyzers = new ArrayList<Analyzer>();
        variables = new ArrayList<Variable>();
        miscInfo = new MiscInfo();
    }

    public boolean isValid() {
        // Check that each component is valid
        if ( !submitter.isValid() )
            return false;
        for (Investigator pi : investigators) {
            if ( !pi.isValid() )
                return false;
        }
        if ( ! platform.isValid() )
            return false;
        if ( !coverage.isValid() )
            return false;
        for (Sampler collector : samplers) {
            if ( !collector.isValid() )
                return false;
        }
        for (Analyzer detector : analyzers) {
            if ( !detector.isValid() )
                return false;
        }
        for (Variable var : variables) {
            if ( !var.isValid() )
                return false;
        }
        if ( !miscInfo.isValid() )
            return false;

        // check that the data times are all within the time range for the dataset
        double startTime = miscInfo.getStartDatestamp().getEarliestTime();
        double endTime = miscInfo.getEndDatestamp().getEarliestTime();
        // set end time to the end of the day
        endTime += 24.0 * 60.0 * 60.0;
        if ( coverage.getEarliestDataTime() < startTime )
            return false;
        if ( coverage.getLatestDataTime() > endTime )
            return false;

        // TODO: verify researcher, sampler names, and analyzer names in variables match some entry in investogators, samplers, and analyzers

        return true;
    }

    @Override
    public SDIMetadata clone() {
        SDIMetadata dup;
        try {
            dup = (SDIMetadata) super.clone();
        } catch ( CloneNotSupportedException ex ) {
            throw new RuntimeException(ex);
        }
        dup.submitter = submitter.clone();
        dup.investigators = new ArrayList<Investigator>(investigators.size());
        for (Investigator pi : investigators) {
            dup.investigators.add(pi.clone());
        }
        dup.platform = platform.clone();
        dup.coverage = coverage.clone();
        dup.samplers = new ArrayList<Sampler>(samplers.size());
        for (Sampler collector : samplers) {
            dup.samplers.add(collector.clone());
        }
        dup.analyzers = new ArrayList<Analyzer>(analyzers.size());
        for (Analyzer detector : analyzers) {
            dup.analyzers.add(detector.clone());
        }
        dup.variables = new ArrayList<Variable>(variables.size());
        for (Variable var : variables) {
            dup.variables.add(var.clone());
        }
        dup.miscInfo = miscInfo.clone();
        return dup;
    }

}

