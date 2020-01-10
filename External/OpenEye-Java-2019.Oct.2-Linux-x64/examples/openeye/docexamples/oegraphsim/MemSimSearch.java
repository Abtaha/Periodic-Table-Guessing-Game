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
package openeye.docexamples.oegraphsim;

import java.io.*;
import openeye.oechem.*;
import openeye.oegraphsim.*;

public class MemSimSearch {

    public static void main(String argv[]) {
        if (argv.length != 1)
            oechem.OEThrow.Usage("MemSimSearch <database>");

        oemolistream ifs = new oemolistream();
        if (!ifs.open(argv[0]))
            oechem.OEThrow.Fatal("Cannot open database molecule file!");

        // load molecules

        OEMolDatabase moldb = new OEMolDatabase(ifs);
        int nrmols = moldb.GetMaxMolIdx();

        // generate fingerprints

        OEFPDatabase fpdb = new OEFPDatabase(OEFPType.Path);

        OEFingerPrint emptyfp = new OEFingerPrint();
        emptyfp.SetFPTypeBase(fpdb.GetFPTypeBase());

        OEGraphMol mol = new OEGraphMol();
        for (int idx = 0; idx < nrmols; ++idx) {
            if (moldb.GetMolecule(mol, idx))
                fpdb.AddFP(mol);
            else
                fpdb.AddFP(emptyfp);
        }
        int nrfps = fpdb.NumFingerPrints();

        OEWallTimer timer = new OEWallTimer();

        while (true) {
            try {
                // read query SMILES from System.in

                System.out.format("Enter SMILES> ");
                InputStreamReader isr = new InputStreamReader(System.in);
                BufferedReader br = new BufferedReader(isr);

                // parse query

                String str = new String(br.readLine());
                OEGraphMol query = new OEGraphMol();
                if (!oechem.OEParseSmiles(query, str)) {
                    oechem.OEThrow.Warning("Invalid SMILES string");
                    continue;
                }

                // calculate similarity scores

                timer.Start();
                OESimScoreIter siter = fpdb.GetSortedScores(query, 5);
                System.out.format("%f seconds to search %d fingerprints\n",
                    timer.Elapsed(), nrfps);

                OEGraphMol hit = new OEGraphMol();
                for (siter.ToFirst(); siter.IsValid(); siter.Increment()) {
                    if (moldb.GetMolecule(hit, (int)siter.Target().GetIdx())) {
                        String smiles = oechem.OEMolToSmiles(hit);
                        System.out.format("Tanimoto score %4.3f %s\n",
                              siter.Target().GetScore(), smiles);
                    }
                }
            }
            catch (Exception e) {
                System.err.println(e);
            }
        }
    }
}
