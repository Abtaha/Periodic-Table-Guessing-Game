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

import openeye.oechem.*;
import openeye.oegraphsim.*;

public class GetScores {
    public static void main(String argv[]) {
        if (argv.length != 2)
            oechem.OEThrow.Usage("GetScores <queryfile> <targetfile>");

        oemolistream ifs = new oemolistream();
        if (!ifs.open(argv[0]))
            oechem.OEThrow.Fatal("Unable to open " + argv[0] + " for reading");

        OEGraphMol qmol = new OEGraphMol();
        if (!oechem.OEReadMolecule(ifs, qmol))
            oechem.OEThrow.Fatal("Unable to read query molecule");
        OEFingerPrint qfp = new OEFingerPrint();
        oegraphsim.OEMakeFP(qfp, qmol, OEFPType.Path);

        if (!ifs.open(argv[1]))
            oechem.OEThrow.Fatal("Unable to open " + argv[1] + " for reading");

        OEFPDatabase fpdb = new OEFPDatabase(qfp.GetFPTypeBase());
        OEGraphMol tmol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, tmol))
            fpdb.AddFP(tmol);
        ifs.close();

        System.out.println("Tanimoto scores:");
        boolean descending = true;
        fpdb.SetSimFunc(OESimMeasure.Tanimoto, descending);
        fpdb.SetCutoff(0.1f);
        for (OESimScore score : fpdb.GetScores(qfp, 0, 100))
            System.out.format("%.3f\n", score.GetScore());

        System.out.println("Tversky scores:");
        fpdb.ClearCutoff(); // default
        fpdb.SetSimFunc(new OETverskySim(0.9f));
        for (OESimScore score : fpdb.GetScores(qfp))
            System.out.format("%.3f\n", score.GetScore());

        System.out.println("Dice scores:");
        descending = true;
        fpdb.SetSimFunc(OESimMeasure.Dice, !descending);
        fpdb.SetCutoff(0.5f);
        for (OESimScore score : fpdb.GetScores(qfp, 100))
            System.out.format("%.3f\n", score.GetScore());

        {
            System.out.println("Cosine scores with option:");
            OEFPDatabaseOptions opts = new OEFPDatabaseOptions();
            opts.SetCutoff(0.3f);
            opts.SetSimFunc(OESimMeasure.Cosine);
            for (OESimScore score : fpdb.GetScores(qfp, opts))
                System.out.format("%.3f\n", score.GetScore());
        }

        System.out.println("Tanimoto sorted scores:");
        fpdb.ClearCutoff();                           // default
        fpdb.SetSimFunc(OESimMeasure.Tanimoto, descending); // defualt
        for (OESimScore score : fpdb.GetSortedScores(qfp, 10))
            System.out.format("%.3f\n", score.GetScore());

        System.out.println("Dice sorted scores:");
        descending =  true;
        fpdb.SetSimFunc(OESimMeasure.Dice, descending);
        fpdb.SetCutoff(0.5f);
        for (OESimScore score : fpdb.GetSortedScores(qfp, 0, 0, 100))
            System.out.format("%.3f\n", score.GetScore());

        System.out.println("Manhattan sorted scores:");
        descending = true;
        fpdb.SetSimFunc(OESimMeasure.Manhattan, !descending);
        fpdb.SetCutoff(0.3f);
        for (OESimScore score : fpdb.GetSortedScores(qfp, 5, 100))
            System.out.format("%.3f\n", score.GetScore());

        {
            System.out.println("Tversky sorted scores with option:");
            OEFPDatabaseOptions opts = new OEFPDatabaseOptions();
            opts.SetDescendingOrder(true);
            opts.SetCutoff(0.3f);
            opts.SetSimFunc(OESimMeasure.Tversky);
            opts.SetTverskyCoeffs(0.9f, 0.1f);
            opts.SetLimit(10);
            for (OESimScore score : fpdb.GetSortedScores(qfp, opts))
                System.out.format("%.3f\n", score.GetScore());
        }

    }
}
