/**
 *
 */
package gov.noaa.pmel.dashboard.programs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.TreeSet;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import uk.ac.uea.socat.omemetadata.OmeMetadata;

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
        } catch (Exception ex) {
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
        } catch (Exception ex) {
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
        } catch (Exception ex) {
            throw new IllegalArgumentException("Problems with " + platformNameFixesFile.getPath() +
                                                       ": " + ex.getMessage(), ex);
        }

    }

    /**
     * Correct the PI name(s) and platforms, then add/correct the organization for each PI.
     *
     * @param omeMData
     *         OME metadata to correct
     * @return if any values were changed
     * @throws IllegalArgumentException
     *         if assigning a value throws an exception
     */
    public boolean fixNamesAndOrganizations(OmeMetadata omeMData) throws IllegalArgumentException {
        boolean changed = false;
        ArrayList<String> errNames = omeMData.getInvestigators();
        ArrayList<String> errOrgs = omeMData.getOrganizations();
        String errPlatform = omeMData.getVesselName();
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
            numNames = piNames.size();
            try {
                omeMData.clearCompositeValueList(OmeMetadata.INVESTIGATOR_COMP_NAME);
                for (int k = 0; k < numNames; k++) {
                    Properties props = new Properties();
                    props.setProperty(OmeMetadata.NAME_ELEMENT_NAME, piNames.get(k));
                    props.setProperty(OmeMetadata.ORGANIZATION_ELEMENT_NAME, piOrgs.get(k));
                    omeMData.storeCompositeValue(OmeMetadata.INVESTIGATOR_COMP_NAME, props, -1);
                }
                omeMData.replaceValue(OmeMetadata.VESSEL_NAME_STRING, platformName, -1);
            } catch (Exception ex) {
                throw new IllegalArgumentException("problems updating names: " + ex.getMessage(), ex);
            }
        }
        return changed;
    }

    /**
     * @param args
     *         ExpocodesFile.txt  PINameCorrections.tsv  PINameOrganization.tsv  PlatformNameCorrections.tsv
     */
    public static void main(String[] args) {
        if ( args.length != 4 ) {
            System.err.println("Arguments:  ExpocodesFile  PINameCorrections.tsv  PINameOrganization.tsv PlatformNameCorrections.tsv");
            System.err.println();
            System.err.println("Corrects PI and platform names, and adds organization names, in the OME.xml ");
            System.err.println("files for datasets whose expocodes are given in ExpocodesFile.txt, one expocode ");
            System.err.println("per line.  Each line of PINameCorrections.tsv contains the misspelled PI 'name', ");
            System.err.println("a tab, and the correct name(s), where multiple correct names are be separated ");
            System.err.println("by semicolons.  Each line of PINameOrganization.tsv contains the (correct) PI ");
            System.err.println("name, a tab, and the organization name.  Each line of PlatformNameCorrections.tsv ");
            System.err.println("contains the misspelled platform name, a tab, and the correct platform name. ");
            System.err.println();
            System.exit(1);
        }
        String expocodesFilename = args[0];
        String piNameFixesFilename = args[1];
        String piNameOrgFilename = args[2];
        String platformNameFixesFilename = args[3];

        FixPIsOrgsPlatforms fixer = null;
        try {
            fixer = new FixPIsOrgsPlatforms(new File(piNameFixesFilename), new File(piNameOrgFilename), new File(platformNameFixesFilename));
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        // Get the expocodes of the datasets to update
        TreeSet<String> allExpocodes = new TreeSet<String>();
        try {
            BufferedReader expoReader = new BufferedReader(new FileReader(expocodesFilename));
            try {
                String dataline = expoReader.readLine();
                while ( dataline != null ) {

                    dataline = dataline.trim();
                    if ( !( dataline.isEmpty() || dataline.startsWith("#") ) ) {
                        String expocode = DashboardServerUtils.checkExpocode(dataline);
                        allExpocodes.add(expocode);
                    }

                    dataline = expoReader.readLine();
                }
            } finally {
                expoReader.close();
            }
        } catch (Exception ex) {
            System.err.println("Error getting expocodes from " + expocodesFilename + ": " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        // Get the default dashboard configuration
        DashboardConfigStore configStore = null;
        try {
            configStore = DashboardConfigStore.get(false);
        } catch (Exception ex) {
            System.err.println("Problems reading the default dashboard configuration file: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
        try {
            MetadataFileHandler mdataHandler = configStore.getMetadataFileHandler();

            for (String expocode : allExpocodes) {
                OmeMetadata omeMData = null;
                File omeFile = mdataHandler.getMetadataFile(expocode, DashboardUtils.OME_FILENAME);
                try {
                    Document omeDoc = ( new SAXBuilder() ).build(omeFile);
                    omeMData = new OmeMetadata(expocode);
                    omeMData.assignFromOmeXmlDoc(omeDoc);
                } catch (Exception ex) {
                    System.err.println("Problems reading the OME file for " + expocode + ": " + ex.getMessage());
                    ex.printStackTrace();
                    System.exit(1);
                }

                boolean changed = false;
                try {
                    changed = fixer.fixNamesAndOrganizations(omeMData);
                } catch (Exception ex) {
                    System.err.println("Problems correcting the OME file for " + expocode + ": " + ex.getMessage());
                    ex.printStackTrace();
                    System.exit(1);
                }

                if ( changed ) {
                    try {
                        Document dashDoc = omeMData.createOmeXmlDoc();
                        FileOutputStream out = new FileOutputStream(omeFile);
                        try {
                            ( new XMLOutputter(Format.getPrettyFormat()) ).output(dashDoc, out);
                        } finally {
                            out.close();
                        }
                    } catch (Exception ex) {
                        throw new IllegalArgumentException("Problems writing the updated OME file: " + ex.getMessage());
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
