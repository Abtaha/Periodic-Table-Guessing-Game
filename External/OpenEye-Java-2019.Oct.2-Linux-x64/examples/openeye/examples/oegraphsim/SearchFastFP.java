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
/****************************************************************************
* Searching fast fingerprint database
****************************************************************************/

package openeye.examples.oegraphsim;

import java.io.*;
import java.net.URL;
import java.io.IOException;

import openeye.oechem.*;
import openeye.oegraphsim.*;

public class SearchFastFP
{
    private static final URL fileURL = SearchFastFP.class.getResource("SearchFastFP.txt");

    public static void main(String[] argv) throws IOException {
        OEInterface itf = new OEInterface();
        oechem.OEConfigureFromURL(itf, fileURL);
        OEFPDatabaseOptions defopts = new OEFPDatabaseOptions(10, OESimMeasure.Tanimoto);
        oegraphsim.OEConfigureFPDatabaseOptions(itf, defopts);
        oegraphsim.OEConfigureFPDatabaseMemoryType(itf);

        if (!oechem.OEParseCommandLine(itf, argv, "SearchFastFP")) {
            oechem.OEThrow.Fatal("Unable to interpret command line!");
        }

        String qfname = itf.GetString("-query");
        String mfname = itf.GetString("-molfname");
        String ffname = itf.GetString("-fpdbfname");
        String ofname = itf.GetString("-out");

        // initialize databases

        OEWallTimer timer = new OEWallTimer();
        timer.Start();

        oemolistream ifs = new oemolistream();
        if (!ifs.open(qfname))
            oechem.OEThrow.Fatal("Cannot open input file!");

        OEGraphMol query = new OEGraphMol();
        if (!oechem.OEReadMolecule(ifs, query))
            oechem.OEThrow.Fatal("Cannot read query molecule!");

        OEMolDatabase moldb = new OEMolDatabase();
        if (!moldb.Open(mfname))
            oechem.OEThrow.Fatal("Cannot open molecule database!");

        int memtype = oegraphsim.OEGetFPDatabaseMemoryType(itf);

        OEFastFPDatabase fpdb = new OEFastFPDatabase(ffname, memtype);
        if (!fpdb.IsValid())
            oechem.OEThrow.Fatal("Cannot open fingerprint database!");
        long nrfps = fpdb.NumFingerPrints();
        String memtypestr = fpdb.GetMemoryTypeString();

        OEFPTypeBase fptype = fpdb.GetFPTypeBase();
        oechem.OEThrow.Info("Using fingerprint type " + fptype.GetFPTypeString());

        oemolostream ofs = new oemolostream();
        if (!ofs.open(ofname))
            oechem.OEThrow.Fatal("Cannot open output file!");

        if (!oegraphsim.OEAreCompatibleDatabases(moldb, fpdb))
            oechem.OEThrow.Fatal("Databases are not compatible!");

        oechem.OEThrow.Info(String.format("%5.2f", timer.Elapsed()) + " sec to initialize databases");

        OEFPDatabaseOptions opts = new OEFPDatabaseOptions();
        oegraphsim.OESetupFPDatabaseOptions(opts, itf);

        // search fingerprint database

        timer.Start();
        OESimScoreIter siter = fpdb.GetSortedScores(query, opts);
        oechem.OEThrow.Info(String.format("%5.2f", timer.Elapsed()) + " sec to search " +
                            nrfps + " fingerprints " +  memtypestr);

        timer.Start();
        int nrhits = 0;
        OEGraphMol hit = new OEGraphMol();
        for (siter.ToFirst(); siter.IsValid(); siter.Increment()) {
            if (moldb.GetMolecule(hit, (int)siter.Target().GetIdx())) {
                nrhits += 1;
                oechem.OESetSDData(hit, "Similarity score", String.format("%.3f", siter.Target().GetScore()));
                oechem.OEWriteMolecule(ofs, hit);
            }
        }
        oechem.OEThrow.Info(String.format("%5.2f", timer.Elapsed()) + " sec to write " + nrhits + " hits");
    }
}
