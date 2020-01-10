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
 * Utility to load a pregenerated index and return the highest MCS similarity
 *  structures from an query structure(s)
 * ---------------------------------------------------------------------------
 * MCSFragDatabase [ -indb index.mcsfrag ]
 *                 [ -query query_file ]
 *                 [ -type bond -limit 10 ]    // 10 Tanimoto bond similarity scores
 *                 [ -verbose 1 ]              // optional verbosity
 *                 [ -outdb outindex.mcsfrag ] // optional save index file
 *
 * index.mcsfrag: filename of index file to load
 * query_file: filename of query to report MCS similarity results
 *****************************************************************************/
package openeye.examples.oemedchem;

import openeye.oechem.*;
import openeye.oemedchem.*;

public class MCSFragDatabase {
    private static void MCSFragQuery(OEInterface itf) {

        boolean verbose = itf.GetBool("-verbose");

        String indb = itf.GetString("-indb");
        if (!oemedchem.OEIsMCSFragDatabaseFileType(indb)) {
            oechem.OEThrow.Fatal("Option -indb must specify a .mcsfrag filename: "
                                 + indb);
        }
        if (verbose) {
            oechem.OEThrow.Info("Loading index from " + indb);
        }
        
        OEMCSFragDatabase mcsdb = new OEMCSFragDatabase();
        if (!oemedchem.OEReadMCSFragDatabase(indb, mcsdb)) {
            oechem.OEThrow.Fatal("Error deserializing MCS fragment database: "
                                 + indb);
        }

        if (mcsdb.NumMols() == 0) {
            oechem.OEThrow.Fatal("Loaded empty index");
        }

        if (mcsdb.NumFragments() == 0) {
            oechem.OEThrow.Fatal("No fragments loaded from index, " +
                                 "use -fragGe,-fragLe options to " +
                                 "extend indexable range");
        }

        oemolistream ifsquery = new oemolistream();
        if (!ifsquery.open(itf.GetString("-query"))) {
            oechem.OEThrow.Fatal("Unable to open query file: " + itf.GetString("-query"));
        }

        // process the query options
        int qmaxrec = 1;
        if (itf.HasInt("-qlim"))
            qmaxrec = itf.GetInt("-qlim");

        int numscores = 10;
        if (itf.HasInt("-limit"))
            numscores = itf.GetInt("-limit");

        int cnttype = OEMCSScoreType.BondCount;
        if (itf.HasString("-type"))
        {
            if (itf.GetString("-type").contains("atom"))
                cnttype = OEMCSScoreType.AtomCount;
            else if (itf.GetString("-type").contains("bond"))
                cnttype = OEMCSScoreType.BondCount;
            else
                oechem.OEThrow.Warning("Ignoring unrecognized -type option, expecting atom or bond: " + itf.GetString("-type"));
        }

        // process the queries
        OEGraphMol qmol = new OEGraphMol();
        int qnum = 0;
        while (oechem.OEReadMolecule(ifsquery, qmol)) {
            ++qnum;

            int numhits = 0;
            for (OEMCSMolSimScore score : mcsdb.GetSortedScores(qmol, numscores, 0, 0, true, cnttype)) {
                if (numhits++ == 0)
                    System.out.printf("Query: %d: %s\n", qnum, qmol.GetTitle());

                System.out.printf("\trecord: %6d\ttanimoto_%s_score: %.2f\tmcs_core: %s\n",
                                  score.GetIdx(), 
                                  ((cnttype == OEMCSScoreType.BondCount) ? "bond" : "atom" ), 
                                  score.GetScore(), 
                                  score.GetMCSCore());
            }
            if (numhits == 0)
                oechem.OEThrow.Warning("Query: " + qnum + ": " + qmol.GetTitle() + " - no hits");

            if (qmaxrec > 0 && qnum >= qmaxrec)
                break;
        }
        ifsquery.close();

        if (qnum == 0)
            oechem.OEThrow.Fatal("Error reading query structure(s): " + itf.GetString("-query"));
    }

    public static void main (String[] args) {
        OEInterface itf = new OEInterface ();
        oechem.OEConfigure(itf, interfaceData);

        if (oechem.OEParseCommandLine (itf, args, "MCSFragDatabase")) {
            MCSFragQuery(itf);
        }
    }

    private static String interfaceData =
"#MCSFragmentDatabase interface file\n" +
"!CATEGORY MCSFragmentDatabase\n" +
"    !CATEGORY I/O\n" +
"        !PARAMETER -indb 1\n" +
"          !TYPE string\n" +
"          !REQUIRED true\n" +
"          !BRIEF Input filename of index file to load\n" +
"          !KEYLESS 1\n" +
"        !END\n" +
"        !PARAMETER -query 2\n" +
"          !ALIAS -q\n" +
"          !TYPE string\n" +
"          !REQUIRED true\n" +
"          !BRIEF query structure file to report similarity results against\n" +
"          !KEYLESS 2\n" +
"        !END\n" +
"    !END\n" +
"    !CATEGORY options\n" +
"        !PARAMETER -type 1\n" +
"           !TYPE string\n" +
"           !DEFAULT bond\n" +
"           !BRIEF MCS core counts to use for reported scores: atom or bond\n" +
"        !END\n" +
"        !PARAMETER -limit 2\n" +
"           !TYPE int\n" +
"           !DEFAULT 10\n" +
"           !BRIEF report -limit scores for the query structure\n" +
"        !END\n" +
"        !PARAMETER -verbose 3\n" +
"           !TYPE bool\n" +
"           !DEFAULT 0\n" +
"           !BRIEF generate verbose output\n" +
"        !END\n" +
"        !PARAMETER -qlim 4\n" +
"           !TYPE int\n" +
"           !DEFAULT 0\n" +
"           !BRIEF limit query processing to -qlim records from the -query structures\n" +
"        !END\n" +
"    !END\n" +
"!END\n";
}
