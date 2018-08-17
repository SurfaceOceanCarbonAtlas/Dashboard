/**
 *
 */
package gov.noaa.pmel.dashboard.programs;

import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.server.CdiacOmeMetadata;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

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
     *
     * @throws IllegalArgumentException
     *         if problems reading either of the files
     */
    public FixPIsOrgsPlatforms(File piNameFixesFile, File piNameOrgFile, File platformNameFixesFile)
            throws IllegalArgumentException {
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
                        throw new IllegalArgumentException("more than one correction for platform '" + errName + "'");

                    dataline = reader.readLine();
                }
            } finally {
                reader.close();
            }
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Problems with " + platformNameFixesFile.getPath() +
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
        String errPlatform = omeMData.getPlatformName();
        int numNames = errNames.size();

        // TODO: match ? in error name correction template with any letter in err name
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
                // Get the organization of the new name(s)
                for (String newName : replacement) {
                    String newOrg = piNameOrgMap.get(newName);
                    if ( newOrg == null ) {
                        newOrg = org;
                    }
                    piOrgs.add(newOrg);
                }
            }
            else {
                // No correction to the name; check the organization
                piNames.add(name);
                String newOrg = piNameOrgMap.get(name);
                if ( newOrg == null ) {
                    newOrg = org;
                }
                else if ( !newOrg.equals(org) ) {
                    changed = true;
                }
                piOrgs.add(newOrg);
            }
        }
        String platformName = platformNameFixMap.get(errPlatform);
        if ( platformName != null ) {
            changed = true;
        }
        else {
            platformName = errPlatform;
        }

        if ( changed ) {
            omeMData.setInvestigatorsAndOrganizations(piNames, piOrgs);
            omeMData.setPlatformName(platformName);
        }
        return changed;
    }

    /**
     * @param args
     *         ExpocodesFile.txt  PINameCorrections.tsv  PINameOrganization.tsv  PlatformNameCorrections.tsv
     */
    public static void main(String[] args) {
        if ( args.length != 4 ) {
            System.err.println("Arguments:  Expocodes.txt  PINameFixes.tsv  PINameOrgs.tsv PlatformNameFixes.tsv");
            System.err.println();
            System.err.println("Corrects PI and platform names, and adds organization names, in the OME.xml ");
            System.err.println("files for datasets whose expocodes are given in Expocodes.txt, one expocode ");
            System.err.println("per line.  Each line of PINameFixes.tsv contains the misspelled PI 'name', ");
            System.err.println("a tab, and the correct name(s), where multiple correct names are be separated ");
            System.err.println("by semicolons.  Each line of PINameOrgs.tsv contains the (correct) PI name, ");
            System.err.println("a tab, and the organization name.  Each line of PlatformNameFixes.tsv ");
            System.err.println("contains the misspelled platform name, a tab, and the correct platform name. ");
            System.err.println();
            System.exit(1);
        }
        File expocodesFile = new File(args[0]);
        File piNameFixesFile = new File(args[1]);
        File piNameOrgFile = new File(args[2]);
        File platformNameFixesFile = new File(args[3]);

        FixPIsOrgsPlatforms fixer = null;
        try {
            fixer = new FixPIsOrgsPlatforms(piNameFixesFile, piNameOrgFile, platformNameFixesFile);
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
                DashboardMetadata mdata = mdataHandler.getMetadataInfo(expocode, DashboardUtils.OME_FILENAME);
                DashboardOmeMetadata omeMData = new DashboardOmeMetadata(CdiacOmeMetadata.class, mdata, mdataHandler);

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
                        mdataHandler.saveAsOmeXmlDoc(omeMData, "Correcting PI names, organizations, platform name");
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
