/*
(C) 2017 OpenEye Scientific Software Inc. All rights reserved.

TERMS FOR USE OF SAMPLE CODE The software below ("Sample Code") is
provided to current licensees or subscribers of OpenEye products or
SaaS offerings (each a "Customer").
Customer is hereby permitted to use, copy, and modify the Sample Code,
subject to these terms. OpenEye claims no rights to Customer's
modifications. Modification of Sample Code is at Customer's sole and
exclusive risk. Sample Code may require Customer to have a then
current license or subscription to the applicable OpenEye offering.
THE SAMPLE CODE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED.  OPENEYE DISCLAIMS ALL WARRANTIES, INCLUDING, BUT
NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE AND NONINFRINGEMENT. In no event shall OpenEye be
liable for any damages or liability in connection with the Sample Code
or its use.
*/
/*****************************************************************************
 * Utility to perform a matched pair analysis on a set of structures
 *  and save the index for subsequent analysis
 * ---------------------------------------------------------------------------
 * CreateMMPIndex index_mols output_index
 *
 * index_mols: filename of input molecules to analyze
 * output_index: filename of generated MMP index
 *****************************************************************************/
package openeye.examples.oemedchem;

import static java.lang.Math.*;
import java.util.ArrayList;
import java.util.List;

import openeye.oechem.*;
import openeye.oemedchem.*;

public class CreateMMPIndex {
    private static void MMPIndex(OEInterface itf) {

        // output index file
        String mmpindexfile = itf.GetString("-output");
        if (!oemedchem.OEIsMatchedPairAnalyzerFileType(mmpindexfile)) {
            oechem.OEThrow.Fatal("Output file is not a matched pair index type - needs .mmpidx extension: " +
                                 mmpindexfile);
        }

        // create options class with defaults
        OEMatchedPairAnalyzerOptions mmpopts = new OEMatchedPairAnalyzerOptions();
        // set up options from command line
        if (!oemedchem.OESetupMatchedPairIndexOptions(mmpopts, itf)) {
            oechem.OEThrow.Fatal("Error setting matched pair indexing options!");
        }

        // input structures to index
        oemolistream ifsindex = new oemolistream();
        if (!ifsindex.open(itf.GetString("-input"))) {
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-input") + " for reading");
        }

        // get requested verbosity setting
        boolean verbose = itf.GetBool("-verbose");
        boolean vverbose = itf.GetBool("-vverbose");
        if (vverbose) {
            verbose = vverbose;
        }

        int maxrec = max(itf.GetInt("-maxrec"), 0);
        int statusrec = itf.GetInt("-status");

        if (itf.GetBool("-exportcompress"))
        {
            if (!mmpopts.SetOptions(mmpopts.GetOptions() | OEMatchedPairOptions.ExportCompression)) {
                oechem.OEThrow.Warning("Error enabling export compression!");
            }
        }

        boolean stripstereo = itf.GetBool("-stripstereo");
        boolean stripsalts = itf.GetBool("-stripsalts");

        boolean alldata = itf.GetBool("-allSD");
        boolean cleardata = itf.GetBool("-clearSD");

        List<String> keepFields = new ArrayList<String>();
        if (itf.HasString("-keepSD"))
        {
            for (String field : itf.GetStringList("-keepSD")) {
                keepFields.add(field);
            }
            if (verbose && (alldata || cleardata)) {
                oechem.OEThrow.Info("Option -keepSD overriding -allSD, -clearSD");
            }
            alldata = false;
            cleardata = false;
        }
        else if (cleardata) {
            alldata = false;
            keepFields.add("-CLEARSD");
        }
        else {
            keepFields.add("-ALLSD");
            if (verbose && !alldata)
                oechem.OEThrow.Info("No SD data handling option specified, -allSD assumed");
            alldata = true;
            cleardata = false;
        }

        if (verbose)
        {
            if (!mmpopts.HasIndexableFragmentHeavyAtomRange()) {
                oechem.OEThrow.Info("Indexing all fragments");
            }
            else {
                System.out.printf("Limiting fragment cores to %.2f-%.2f%% of input molecules\n",
                                  mmpopts.GetIndexableFragmentRangeMin(), mmpopts.GetIndexableFragmentRangeMax());
            }

            if (statusrec != 0) {
                oechem.OEThrow.Info("Status output after every " + statusrec + " records");
            }

            if (maxrec != 0) {
                oechem.OEThrow.Info("Indexing a maximum of " + maxrec + " records");
            }

            if (itf.GetBool("-exportcompress")) {
                oechem.OEThrow.Info("Removing singleton index nodes from index");
            }

            if (stripstereo) {
                oechem.OEThrow.Info("Stripping stereo");
            }

            if (stripsalts) {
                oechem.OEThrow.Info("Stripping salts");
            }

            if (cleardata) {
                oechem.OEThrow.Info("Clearing all input SD data");
            }
            else if (alldata) {
                oechem.OEThrow.Info("Retaining all input SD data");
            }
            else if (!keepFields.isEmpty())
            {
                String allfields = new String();
                for (String field : itf.GetStringList("-i")) {
                        allfields = allfields + field;
                }
                oechem.OEThrow.Info("Retaining floating point SD data fields: " + allfields);
            }
        }

        // create indexing engine
        OEMatchedPairAnalyzer mmp = new OEMatchedPairAnalyzer(mmpopts);

        // interpret SD fields as floating point data
        FilterSDData validdata = new FilterSDData(keepFields, true);

        // add molecules to be indexed
        int record = 0;
        int unindexed = 0;
        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifsindex, mol))
        {
            if (!alldata)
            {
                // filter the input molecule SD data based on allowed fields
                validdata.FilterMolData(mol);
            }

            if (stripsalts) {
                oechem.OEDeleteEverythingExceptTheFirstLargestComponent(mol);
            }

            if (stripstereo) {
                oechem.OEUncolorMol(mol,
                                    (OEUncolorStrategy.RemoveAtomStereo |
                                     OEUncolorStrategy.RemoveBondStereo |
                                     OEUncolorStrategy.RemoveGroupStereo));
            }

            int status = mmp.AddMol(mol, record);
            if (status != record) {
                ++unindexed;
                if (vverbose) {
                    oechem.OEThrow.Info("Input structure not added to index, record=" + record + " status=" + oemedchem.OEMatchedPairIndexStatusName(status));
                }
            }
            ++record;

            if (maxrec != 0 && record >= maxrec) {
                break;
            }

            if (statusrec != 0 && (record % statusrec) == 0) {
                oechem.OEThrow.Info("Records: " + record + " Indexed: " + (record - unindexed) + " Unindexed: " + unindexed);
            }
        }

        if (mmp.NumMols() == 0) {
            oechem.OEThrow.Fatal("No records in index structure file");
        }

        if (mmp.NumMatchedPairs() == 0) {
            oechem.OEThrow.Fatal("No matched pairs found from indexing, use -fragGe,-fragLe options to extend indexing range");
        }

        if (!oemedchem.OEWriteMatchedPairAnalyzer(mmpindexfile, mmp)) {
            oechem.OEThrow.Fatal("Error serializing MMP index: " + mmpindexfile);
        }

        // return some status information
        oechem.OEThrow.Info("Records: " + record + ", Indexed: " + mmp.NumMols() + ", matched pairs: " + mmp.NumMatchedPairs());
    }

    public static void main (String[] args) {
        OEInterface itf = new OEInterface ();
        oechem.OEConfigure(itf, interfaceData);
        oemedchem.OEConfigureMatchedPairIndexOptions(itf);

        if (oechem.OEParseCommandLine (itf, args, "CreateMMPIndex")) {
            MMPIndex(itf);
        }
    }

    // simple SD data filter class
    private static class FilterSDData {
        private List<String> fields;
        private boolean asFloating;
        private boolean allfields;
        private boolean clearfields;

        private FilterSDData() {
        }

        public FilterSDData(List<String> fieldlist, boolean asfloating) {
            fields = fieldlist;
            asFloating = asfloating;
            allfields = false;
            clearfields = false;
            if (fieldlist.size() == 1) {
                String opt = fieldlist.get(0).toUpperCase();
                if (opt == "-ALLSD") {
                    allfields = true;
                }
                else if (opt == "-CLEARSD") {
                    clearfields = true;
                }
            }
        }

        public int FilterMolData(OEMolBase mol) {
            if (!oechem.OEHasSDData(mol)) {
                return 0;
            }

            if (allfields) {
                return -1;
            }

            if (clearfields) {
                oechem.OEClearSDData(mol);
                return 0;
            }

            if (fields.isEmpty()) {
                return -1;
            }

            int validdata = 0;
            List<String> deletefields = new ArrayList<String>();

            for (OESDDataPair dp : oechem.OEGetSDDataPairs(mol)) {
                String tag  = dp.GetTag();
                if (!fields.contains(tag)) {
                    deletefields.add(tag);
                    continue;
                }
                if (asFloating) {
                    Float asfloat = null;
                    try {
                        asfloat = Float.valueOf(dp.GetValue());
                    } catch (NumberFormatException e) {
                        oechem.OEThrow.Warning("Failed to convert " + tag + " to numeric value (" + oechem.OEGetSDData(mol, tag) + "%) in " + mol.GetTitle());
                    }
                    if (asfloat == null) {
                        deletefields.add(tag);
                        continue;
                    }
                }
                ++validdata;
            }

            if (validdata == 0) {
                oechem.OEClearSDData(mol);
            }
            else {
                for (String nuke : deletefields) {
                    oechem.OEDeleteSDData(mol, nuke);
                }
            }

            return validdata;
        }
    }

    private static String interfaceData =
"!BRIEF [ -input ] index_mols [ -output ] output_index\n" +
"# createmmpindex interface file\n" +
"!CATEGORY CreateMMPIndex\n" +
"    !CATEGORY I/O\n" +
"        !PARAMETER -input 1\n" +
"          !ALIAS -in\n" +
"          !TYPE string\n" +
"          !REQUIRED true\n" +
"          !BRIEF Input filename of structures to index\n" +
"          !KEYLESS 1\n" +
"        !END\n" +
"        !PARAMETER -output 2\n" +
"          !ALIAS -out\n" +
"          !TYPE string\n" +
"          !REQUIRED true\n" +
"          !BRIEF Output filename for serialized MMP index\n" +
"          !KEYLESS 2\n" +
"        !END\n" +
"    !END\n" +
"    !CATEGORY indexing_options\n" +
"        !PARAMETER -maxrec 1\n" +
"           !TYPE int\n" +
"           !DEFAULT 0\n" +
"           !LEGAL_RANGE 0 inf\n" +
"           !BRIEF process at most -maxrec records from -input (0: all)\n" +
"        !END\n" +
"        !PARAMETER -status 2\n" +
"           !TYPE int\n" +
"           !DEFAULT 0\n" +
"           !LEGAL_RANGE 0 inf\n" +
"           !BRIEF emit progress information every -status records (0: off)\n" +
"        !END\n" +
"        !PARAMETER -exportcompress 3\n" +
"          !TYPE bool\n" +
"          !DEFAULT 0\n" +
"          !BRIEF Whether to remove singleton nodes on export of the MMP index (i.e., no additional structures will be added to the index)\n" +
"        !END\n" +
"        !PARAMETER -verbose 4\n" +
"           !TYPE bool\n" +
"           !DEFAULT 0\n" +
"           !BRIEF generate verbose output\n" +
"        !END\n" +
"        !PARAMETER -vverbose 5\n" +
"           !TYPE bool\n" +
"           !DEFAULT 0\n" +
"           !BRIEF generate very verbose output\n" +
"        !END\n" +
"    !END\n" +
"    !CATEGORY molecule_SDData\n" +
"        !PARAMETER -allSD 1\n" +
"           !TYPE bool\n" +
"           !DEFAULT 0\n" +
"           !BRIEF retain all input SD data\n" +
"        !END\n" +
"        !PARAMETER -clearSD 2\n" +
"           !TYPE bool\n" +
"           !DEFAULT 0\n" +
"           !BRIEF clear all input SD data\n" +
"        !END\n" +
"        !PARAMETER -keepSD 3\n" +
"           !TYPE string\n" +
"           !LIST true\n" +
"           !BRIEF list of SD data tags of floating point data to *retain* for indexing (all other SD data is removed)\n" +
"        !END\n" +
"    !END\n" +
"    !CATEGORY molecule_processing\n" +
"        !PARAMETER -stripstereo 1\n" +
"          !TYPE bool\n" +
"          !DEFAULT 0\n" +
"          !BRIEF Whether to strip stereo from -input structures\n" +
"        !END\n" +
"        !PARAMETER -stripsalts 2\n" +
"          !TYPE bool\n" +
"          !DEFAULT 0\n" +
"          !BRIEF Whether to strip salt fragments from -input structures\n" +
"        !END\n" +
"    !END\n" +
"!END\n";
}
