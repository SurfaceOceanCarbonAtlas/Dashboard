package gov.noaa.pmel.dashboard.programs;

import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.metadata.DashboardOmeMetadata;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * Updates dashboard's OME.xml from PI name corrections and organization names
 * given in tab-separated-values files.
 *
 * @author Karl Smith
 */
public class FixPIsOrgsPlatforms {

    HashMap<String,ArrayList<String>> piNameFixMap;
    HashMap<String,String> piNameOrgMap;
    HashMap<String,String> platformNameFixMap;
    HashMap<String,String> platformTypeFixMap;

    /**
     * Corrects names of PIs and platforms, and adds organizations, in OME metadata
     *
     * @param piNameFixesFile
     *         TSV file of erroneous and correct PI names;
     *         each line consists of the erroneous PI 'name', a tab, and the correct PI name(s);
     *         multiple correct names are separated by semicolons
     * @param piNameOrgFile
     *         TSV file of (correct) PI names and associated organizations;
     *         each line consists of the PI's name, a tab, and the PI's organization
     * @param platformNameFixesFile
     *         TSV file of erroneous and correct platform names;
     *         each line consists of the erroneous platform name, a tab, and the correct platform name
     * @param platformTypeFixesFile
     *         TSV file of erroneous and correct platform types;
     *         each line consists of the erroneous platform type, a tab, and the correct platform type
     *
     * @throws IllegalArgumentException
     *         if problems reading either of the files
     */
    public FixPIsOrgsPlatforms(File piNameFixesFile, File piNameOrgFile,
            File platformNameFixesFile, File platformTypeFixesFile) throws IllegalArgumentException {
        BufferedReader reader;

        piNameFixMap = new HashMap<String,ArrayList<String>>();
        try {
            reader = new BufferedReader(new FileReader(piNameFixesFile));
            try {
                String dataline = reader.readLine();
                while ( dataline != null ) {

                    String[] pieces = dataline.trim().split("\\t");
                    if ( pieces.length != 2 )
                        throw new IllegalArgumentException("not two values separated by a tab in '" + dataline + "'");
                    String errName = pieces[0].trim();
                    if ( errName.isEmpty() )
                        throw new IllegalArgumentException("empty erroneous PI name in '" + dataline + "'");
                    String[] fixNames = pieces[1].split(";");
                    ArrayList<String> fixList = new ArrayList<String>(fixNames.length);
                    for (String fix : fixNames) {
                        fix = fix.trim();
                        if ( fix.isEmpty() )
                            throw new IllegalArgumentException("empty corrected PI name in '" + dataline + "'");
                        fixList.add(fix);
                    }
                    if ( piNameFixMap.put(errName, fixList) != null )
                        throw new IllegalArgumentException("more than one correction for PI name '" + errName + "'");

                    dataline = reader.readLine();
                }
            } finally {
                reader.close();
            }
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Problems with " + piNameFixesFile.getPath() +
                    ": " + ex.getMessage(), ex);
        }

        piNameOrgMap = new HashMap<String,String>();
        try {
            reader = new BufferedReader(new FileReader(piNameOrgFile));
            try {
                String dataline = reader.readLine();
                while ( dataline != null ) {

                    String[] pieces = dataline.trim().split("\\t");
                    if ( pieces.length != 2 )
                        throw new IllegalArgumentException("not two values separated by a tab in '" + dataline + "'");
                    String piName = pieces[0].trim();
                    if ( piName.isEmpty() )
                        throw new IllegalArgumentException("empty PI name in '" + dataline + "'");
                    String piOrg = pieces[1].trim();
                    if ( piOrg.isEmpty() )
                        throw new IllegalArgumentException("empty organization name in '" + dataline + "'");
                    if ( piNameOrgMap.put(piName, piOrg) != null )
                        throw new IllegalArgumentException("more than one organization for PI '" + piName + "'");

                    dataline = reader.readLine();
                }
            } finally {
                reader.close();
            }
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Problems with " + piNameOrgFile.getPath() +
                    ": " + ex.getMessage(), ex);
        }

        platformNameFixMap = new HashMap<String,String>();
        try {
            reader = new BufferedReader(new FileReader(platformNameFixesFile));
            try {
                String dataline = reader.readLine();
                while ( dataline != null ) {

                    String[] pieces = dataline.trim().split("\\t");
                    if ( pieces.length != 2 )
                        throw new IllegalArgumentException("not two values separated by a tab in '" + dataline + "'");
                    String errName = pieces[0].trim();
                    if ( errName.isEmpty() )
                        throw new IllegalArgumentException("empty erroneous platform name in '" + dataline + "'");
                    String fixName = pieces[1].trim();
                    if ( fixName.isEmpty() )
                        throw new IllegalArgumentException("empty corrected platform name in '" + dataline + "'");
                    if ( platformNameFixMap.put(errName, fixName) != null )
                        throw new IllegalArgumentException(
                                "more than one correction for platform name '" + errName + "'");

                    dataline = reader.readLine();
                }
            } finally {
                reader.close();
            }
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Problems with " + platformNameFixesFile.getPath() +
                    ": " + ex.getMessage(), ex);
        }

        platformTypeFixMap = new HashMap<String,String>();
        try {
            reader = new BufferedReader(new FileReader(platformTypeFixesFile));
            try {
                String dataline = reader.readLine();
                while ( dataline != null ) {

                    String[] pieces = dataline.trim().split("\\t");
                    if ( pieces.length != 2 )
                        throw new IllegalArgumentException("not two values separated by a tab in '" + dataline + "'");
                    String errType = pieces[0].trim();
                    if ( errType.isEmpty() )
                        throw new IllegalArgumentException("empty erroneous platform type in '" + dataline + "'");
                    String fixType = pieces[1].trim();
                    if ( fixType.isEmpty() )
                        throw new IllegalArgumentException("empty corrected platform type in '" + dataline + "'");
                    if ( platformTypeFixMap.put(errType, fixType) != null )
                        throw new IllegalArgumentException(
                                "more than one correction for platform type '" + errType + "'");

                    dataline = reader.readLine();
                }
            } finally {
                reader.close();
            }
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Problems with " + platformTypeFixesFile.getPath() +
                    ": " + ex.getMessage(), ex);
        }

    }

    /**
     * Correct the PI name(s) and platforms, then add/correct the organization for each PI.
     *
     * @param omeMData
     *         OME metadata to correct
     *
     * @return if any values were changed
     *
     * @throws IllegalArgumentException
     *         if assigning a value throws an exception
     */
    public boolean fixNamesAndOrganizations(DashboardOmeMetadata omeMData) throws IllegalArgumentException {
        boolean changed = false;
        ArrayList<String> errNames = omeMData.getInvestigators();
        ArrayList<String> errOrgs = omeMData.getOrganizations();
        String errPlatformName = omeMData.getPlatformName();
        String errPlatformType = omeMData.getPlatformType();
        int numNames = errNames.size();

        ArrayList<String> piNames = new ArrayList<String>(numNames);
        ArrayList<String> piOrgs = new ArrayList<String>(numNames);
        for (int k = 0; k < numNames; k++) {
            String name = errNames.get(k);
            String org = errOrgs.get(k);

            ArrayList<String> replacement = piNameFixMap.get(name);
            if ( replacement != null ) {
                // Name correction
                changed = true;
                piNames.addAll(replacement);
                // Get the organization for each new name
                if ( org.isEmpty() ) {
                    for (String newName : replacement) {
                        String newOrg = piNameOrgMap.get(newName);
                        if ( newOrg != null )
                            piOrgs.add(newOrg);
                        else
                            piOrgs.add("");
                    }
                }
                else {
                    for (String ignored : replacement) {
                        piOrgs.add(org);
                    }
                }
            }
            else {
                // No correction to the name; check the organization
                piNames.add(name);
                if ( org.isEmpty() ) {
                    String newOrg = piNameOrgMap.get(name);
                    if ( newOrg != null ) {
                        piOrgs.add(newOrg);
                        changed = true;
                    }
                    else
                        piOrgs.add("");
                }
                else
                    piOrgs.add(org);
            }
        }

        String platformName = platformNameFixMap.get(errPlatformName);
        if ( platformName != null )
            changed = true;
        else
            platformName = errPlatformName;

        String platformType = platformTypeFixMap.get(errPlatformType);
        if ( platformType != null )
            changed = true;
        else
            platformType = errPlatformType;

        if ( changed ) {
            omeMData.setInvestigatorsAndOrganizations(piNames, piOrgs);
            omeMData.setPlatformName(platformName);
            omeMData.setPlatformType(platformType);
        }
        return changed;
    }

    /**
     * @param args
     *         ExpocodesFile.txt  PINameCorrections.tsv  PINameOrganization.tsv  PlatformNameCorrections.tsv
     */
    public static void main(String[] args) {
        if ( args.length != 5 ) {
            System.err.println("Arguments:  " +
                    "Expocodes.txt  PINameFixes.tsv  PINameOrgs.tsv  PlatformNameFixes.tsv  PlatformTypeFixes.tsv ");
            System.err.println();
            System.err.println("Modifies the OME.xml document for datasets, correcting PI names, platform names, and ");
            System.err.println("platform types, as well as adding organization names.  No changes are made to the ");
            System.err.println("original data files or any DSG files. ");
            System.err.println();
            System.err.println("Expocodes.txt - file of expocodes, one per line, identifying the datasets to examine");
            System.err.println();
            System.err.println("PINameFixes.tsv - file of PI name corrections; each line contains the misspelled ");
            System.err.println("    PI name', a tab, and the correct name(s), where multiple correct names are ");
            System.err.println("    separated by semicolons. ");
            System.err.println();
            System.err.println("PINameOrgs.tsv  - file of organization names to associate with PI names without an ");
            System.err.println("    organization specified; each line contains a (correct) PI name, a tab, and the ");
            System.err.println("    organization name to associate with this PI. ");
            System.err.println();
            System.err.println("PlatformNameFixes.tsv - file of platform name corrections; each line contains the ");
            System.err.println("    misspelled platform name, a tab, and the correct platform name. ");
            System.err.println();
            System.err.println("PlatformTypeFixes.tsv - file of platform type corrections; each line contains the ");
            System.err.println("    misspelled platform type, a tab, and the correct platform type. ");

            System.err.println();
            System.exit(1);
        }
        File expocodesFile = new File(args[0]);
        File piNameFixesFile = new File(args[1]);
        File piNameOrgFile = new File(args[2]);
        File platformNameFixesFile = new File(args[3]);
        File platformTypeFixesFile = new File(args[4]);

        FixPIsOrgsPlatforms fixer = null;
        try {
            fixer = new FixPIsOrgsPlatforms(piNameFixesFile, piNameOrgFile,
                    platformNameFixesFile, platformTypeFixesFile);
        } catch ( Exception ex ) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        // Get the expocodes of the datasets to update
        TreeSet<String> allExpocodes = new TreeSet<String>();
        try {
            BufferedReader expoReader = new BufferedReader(new FileReader(expocodesFile));
            try {
                String dataline = expoReader.readLine();
                while ( dataline != null ) {

                    dataline = dataline.trim();
                    if ( !(dataline.isEmpty() || dataline.startsWith("#")) ) {
                        String expocode = DashboardServerUtils.checkDatasetID(dataline);
                        allExpocodes.add(expocode);
                    }

                    dataline = expoReader.readLine();
                }
            } finally {
                expoReader.close();
            }
        } catch ( Exception ex ) {
            System.err.println("Error getting expocodes from " + expocodesFile.getPath() + ": " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        // Get the default dashboard configuration
        DashboardConfigStore configStore = null;
        try {
            configStore = DashboardConfigStore.get(false);
        } catch ( Exception ex ) {
            System.err.println("Problems reading the default dashboard configuration file: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
        try {
            MetadataFileHandler mdataHandler = configStore.getMetadataFileHandler();

            for (String expocode : allExpocodes) {
                DashboardOmeMetadata omeMData = mdataHandler.getOmeFromFile(expocode, DashboardServerUtils.OME_FILENAME);

                boolean changed = false;
                try {
                    changed = fixer.fixNamesAndOrganizations(omeMData);
                } catch ( Exception ex ) {
                    System.err.println("Problems correcting the OME file for " + expocode + ": " + ex.getMessage());
                    ex.printStackTrace();
                    System.exit(1);
                }

                if ( changed ) {
                    try {
                        mdataHandler.saveOmeToFile(omeMData, "Correcting PI names, organizations, platform name");
                    } catch ( Exception ex ) {
                        System.err.println("Problems writing the updated OME file for " + expocode +
                                ": " + ex.getMessage());
                        ex.printStackTrace();
                        System.exit(1);
                    }
                    System.err.println(expocode + ": updated");
                }
                else {
                    System.err.println(expocode + ": unchanged");
                }
            }

        } finally {
            DashboardConfigStore.shutdown();
        }
        System.exit(0);
    }

}
