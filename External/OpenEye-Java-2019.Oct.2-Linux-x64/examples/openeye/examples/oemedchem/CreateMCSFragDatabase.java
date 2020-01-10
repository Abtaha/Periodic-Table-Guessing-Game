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
 * Utility to perform an MCS fragmentation on an input set of structures
 *  and save the index for subsequent analysis
 * ---------------------------------------------------------------------------
 * CreateMCSFragDatabase index_mols output_mcsindex
 *                       [ -verbose 1 ]  // optional verbosity
 *
 * index_mols: filename of input molecules to analyze
 * output_index: filename of MCS fragment index
 *****************************************************************************/
package openeye.examples.oemedchem;

import static java.lang.Math.*;

import openeye.oechem.*;
import openeye.oemedchem.*;

public class CreateMCSFragDatabase {
    private static void MCSFragIndex(OEInterface itf) {

        // output index file
        String mcsindexfile = itf.GetString("-output");
        if (!oemedchem.OEIsMCSFragDatabaseFileType(mcsindexfile)) {
            oechem.OEThrow.Fatal("Output file is not a matched pair index type - needs .mcsfrag extension: " +
                                 mcsindexfile);
        }

        // create options class with defaults
        OEMCSFragDatabaseOptions mcsopts = new OEMCSFragDatabaseOptions();
        // set up options from command line
        if (!oemedchem.OESetupMCSFragDatabaseOptions(mcsopts, itf)) {
            oechem.OEThrow.Fatal("Error setting matched pair indexing options!");
        }

        // input structures to index
        oemolistream ifsindex = new oemolistream();
        if (!ifsindex.open(itf.GetString("-input"))) {
            oechem.OEThrow.Fatal("Unable to open " + itf.GetString("-input") + " for reading");
        }

        // get requested verbosity setting
        boolean verbose = itf.GetBool("-verbose");

        int maxrec = max(itf.GetInt("-maxrec"), 0);
        int statusrec = itf.GetInt("-status");

        if (verbose) {
            if (!mcsopts.HasIndexableFragmentHeavyAtomRange()) {
                oechem.OEThrow.Info("Indexing all fragments");
            }
            else {
                System.out.printf("Limiting fragment cores to %.2f-%.2f%% of input molecules\n",
                                  mcsopts.GetIndexableFragmentRangeMin(), mcsopts.GetIndexableFragmentRangeMax());
            }

            if (statusrec != 0) {
                oechem.OEThrow.Info("Status output after every " + statusrec + " records");
            }

            if (maxrec != 0) {
                oechem.OEThrow.Info("Indexing a maximum of " + maxrec + " records");
            }
        }

        // create indexing engine
        OEMCSFragDatabase mcsdb = new OEMCSFragDatabase(mcsopts);

        // add molecules to be indexed
        int record = 0;
        int unindexed = 0;
        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifsindex, mol)) {
            int status = mcsdb.AddMol(mol, record);
            if (status != record) {
                ++unindexed;
                if (verbose) {
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

        if (record == 0)
            oechem.OEThrow.Fatal("No records in index structure file");

        if (mcsdb.NumFragments() == 0) {
            oechem.OEThrow.Fatal("No fragments found from indexing, use -fragGe,-fragLe options to extend indexing range");
        }

        if (!oemedchem.OEWriteMCSFragDatabase(mcsindexfile, mcsdb)) {
            oechem.OEThrow.Fatal("Error serializing MCS fragment database: " + mcsindexfile);
        }

        // return some status information
        oechem.OEThrow.Info("Records: " + record + ", Indexed: " + mcsdb.NumMols() + ", fragments: " + mcsdb.NumFragments());
    }

    public static void main (String[] args) {
        OEInterface itf = new OEInterface ();
        oechem.OEConfigure(itf, interfaceData);
        oemedchem.OEConfigureMCSFragDatabaseOptions(itf);

        if (oechem.OEParseCommandLine (itf, args, "CreateMCSFragDatabase")) {
            MCSFragIndex(itf);
        }
    }

    private static String interfaceData =
"!BRIEF [ -input ] index_mols [ -output ] output_mcsindex\n" +
"#createmcsfragdatabase interface file\n" +
"!CATEGORY CreateMCSFragDatabase\n" +
"    !CATEGORY I/O\n" +
"        !PARAMETER -input 1\n" +
"          !TYPE string\n" +
"          !REQUIRED true\n" +
"          !BRIEF Input filename of structure(s) to index\n" +
"          !KEYLESS 1\n" +
"        !END\n" +
"        !PARAMETER -output 2\n" +
"          !TYPE string\n" +
"          !REQUIRED true\n" +
"          !BRIEF Output filename of MCS fragment serialized index\n" +
"          !KEYLESS 2\n" +
"        !END\n" +
"    !END\n" +
"    !CATEGORY options\n" +
"        !PARAMETER -verbose 1\n" +
"           !TYPE bool\n" +
"           !DEFAULT 0\n" +
"           !BRIEF generate verbose output\n" +
"        !END\n" +
"        !PARAMETER -maxrec 2\n" +
"           !TYPE int\n" +
"           !DEFAULT 0\n" +
"           !BRIEF limit indexing to -maxrec records from the -input structures\n" +
"        !END\n" +
"        !PARAMETER -status 3\n" +
"           !TYPE int\n" +
"           !DEFAULT 0\n" +
"           !BRIEF print indexing status every -status records\n" +
"        !END\n" +
"    !END\n" +
"!END\n";
}
