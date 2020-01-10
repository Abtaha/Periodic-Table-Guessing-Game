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
 *  and save the index for subsequent analysis using a multithreaded API
 * ---------------------------------------------------------------------------
 * CreateMMPIndexThreaded index_mols output_index
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

public class CreateMMPIndexThreaded {

    private static void MMPIndex(OEInterface itf) {
        // checking input structures
        oemolistream ifsindex = new oemolistream();
        if (!ifsindex.open(itf.GetString("-input"))) {
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-input") + " for reading");
        }
        ifsindex.close();

        // get requested verbosity setting
        boolean verbose = itf.GetBool("-verbose");
        boolean vverbose = itf.GetBool("-vverbose");
        if (vverbose) {
            verbose = vverbose;
        }

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

        int maxrec = max(itf.GetInt("-maxrec"), 0);

        if (verbose) {
            if (!mmpopts.HasIndexableFragmentHeavyAtomRange()) {
                oechem.OEThrow.Info("Indexing all fragments");
            }
            else {
                System.out.printf("Limiting fragment cores to %.2f-%.2f%% of input molecules\n",
                                  mmpopts.GetIndexableFragmentRangeMin(), mmpopts.GetIndexableFragmentRangeMax());
            }

            if (maxrec != 0) {
                oechem.OEThrow.Info("Indexing a maximum of " + maxrec + " records");
            }

            if (itf.GetBool("-exportcompress")) {
                oechem.OEThrow.Info("Removing singleton index nodes from index");
            }
        }

        if (itf.GetBool("-exportcompress"))
        {
            if (!mmpopts.SetOptions(mmpopts.GetOptions() | OEMatchedPairOptions.ExportCompression)) {
                oechem.OEThrow.Warning("Error enabling export compression!");
            }
        }

        // set indexing options
        OECreateMMPIndexOptions indexopts = new OECreateMMPIndexOptions(mmpopts);

        // set requested verbosity setting
        if (vverbose) {
            indexopts.SetVerbose(2);
        }
        else if (verbose) {
            indexopts.SetVerbose(1);
        }

        // limit number of records to process
        indexopts.SetMaxRecord(maxrec);

        // set number of threads to use
        indexopts.SetNumThreads(itf.GetInt("-threads"));
        if (verbose) {
            if (indexopts.GetNumThreads() == 0) {
                oechem.OEThrow.Info("Using the maximum number of threads available");
            }
            else {
                oechem.OEThrow.Info("Limiting indexing to " + indexopts.GetNumThreads() + " thread(s)");
            }
        }

        if (verbose)
            oechem.OEThrow.Info("Threaded indexing of " + itf.GetString("-input") + ", all SD data will be preserved");

        // create index
        OECreateMMPIndexStatus indexstatus = oemedchem.OECreateMMPIndexFile(mmpindexfile,
                                                                            itf.GetString("-input"),
                                                                            indexopts);
        if (!indexstatus.IsValid()) {
            oechem.OEThrow.Fatal("Invalid status returned from indexing!");
        }

        if (indexstatus.GetTotalMols() == 0) {
            oechem.OEThrow.Fatal("No records in index structure file: " + itf.GetString("-input"));
        }

        if (indexstatus.GetNumMatchedPairs() == 0) {
            oechem.OEThrow.Fatal("No matched pairs found from indexing, " +
                                 "use -fragGe,-fragLe options to extend indexing range");
        }

        // return some status information
        oechem.OEThrow.Info("Records: " + indexstatus.GetTotalMols() + ", Indexed: " + indexstatus.GetNumMols() + ", matched pairs: " +indexstatus.GetNumMatchedPairs());
    }

    public static void main (String[] args) {
        OEInterface itf = new OEInterface ();
        oechem.OEConfigure(itf, interfaceData);
        oemedchem.OEConfigureMatchedPairIndexOptions(itf);

        if (oechem.OEParseCommandLine (itf, args, "CreateMMPIndexThreaded")) {
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
"!CATEGORY CreateMMPIndexThreaded\n" +
"    !CATEGORY I/O\n" +
"        !PARAMETER -input 1\n" +
"          !TYPE string\n" +
"          !REQUIRED true\n" +
"          !BRIEF Input filename of structures to index\n" +
"          !KEYLESS 1\n" +
"        !END\n" +
"        !PARAMETER -output 2\n" +
"          !TYPE string\n" +
"          !REQUIRED true\n" +
"          !BRIEF Output filename for serialized MMP index\n" +
"          !KEYLESS 2\n" +
"        !END\n" +
"    !END\n" +
"    !CATEGORY options\n" +
"        !PARAMETER -maxrec 1\n" +
"           !TYPE int\n" +
"           !DEFAULT 0\n" +
"           !LEGAL_RANGE 0 inf\n" +
"           !BRIEF process at most -maxrec records from -input (0: all)\n" +
"        !END\n" +
"        !PARAMETER -verbose 2\n" +
"           !TYPE bool\n" +
"           !DEFAULT 0\n" +
"           !BRIEF generate verbose output\n" +
"        !END\n" +
"        !PARAMETER -threads 3\n" +
"           !TYPE int\n" +
"           !DEFAULT 0\n" +
"           !LEGAL_RANGE 0 inf\n" +
"           !BRIEF limit number of indexing threads to -threads (0:default)\n" +
"        !END\n" +
"        !PARAMETER -exportcompress 4\n" +
"          !TYPE bool\n" +
"          !DEFAULT 0\n" +
"          !BRIEF Whether to remove singleton nodes on export of the MMP index\n" +
"        !END\n" +
"        !PARAMETER -vverbose 5\n" +
"           !TYPE bool\n" +
"           !DEFAULT 0\n" +
"           !BRIEF generate very verbose output\n" +
"        !END\n" +
"    !END\n" +
"!END\n";
}
