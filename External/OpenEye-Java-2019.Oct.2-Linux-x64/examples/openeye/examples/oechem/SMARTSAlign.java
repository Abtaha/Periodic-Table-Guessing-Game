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
 * Align two compounds based on smarts match
 ****************************************************************************/
package openeye.examples.oechem;

import openeye.oechem.*;

public class SMARTSAlign {

    public static void smartsAlign(OEMolBase refmol, OEMolBase fitmol,
            OESubSearch ss, oemolostream ofs) {
        boolean unique = true;
        for (OEMatchBase match1 : ss.Match(refmol, unique)) {
            for (OEMatchBase match2 : ss.Match(fitmol, unique)) {
                OEMatch match = new OEMatch();
                OEMatchPairAtomIter mbi1 = match1.GetAtoms();
                OEMatchPairAtomIter mbi2 = match2.GetAtoms();
                while (mbi1.hasNext() && mbi2.hasNext()) {
                    match.AddPair(mbi1.next().getTarget(), mbi2.next().getTarget());
                }
                boolean overlay = true;
                double rmat[] = new double[9];
                double trans[] = new double[3];
                oechem.OERMSD(refmol, fitmol, match, overlay, rmat, trans);
                oechem.OERotate(fitmol, rmat);
                oechem.OETranslate(fitmol, trans);
                oechem.OEWriteConstMolecule(ofs, fitmol);
            }
        }
    }

    public static void main(String argv[]) {
        if (argv.length != 4) {
            oechem.OEThrow.Usage("SMARTSAlign <refmol> <fitmol> <outfile> <smarts>");
        }

        oemolistream reffs = new oemolistream();
        if (!reffs.open(argv[0])) {
            oechem.OEThrow.Fatal("Unable to open " + argv[0] + " for reading");
        }
        if (!oechem.OEIs3DFormat(reffs.GetFormat())) {
            oechem.OEThrow.Fatal("Invalid input format: need 3D coordinates");
        }
        OEGraphMol refmol = new OEGraphMol();
        if (!oechem.OEReadMolecule(reffs, refmol)) {
            oechem.OEThrow.Fatal("Unable to read molecule in " + argv[0]);
        }
        if (refmol.GetDimension() != 3) {
            oechem.OEThrow.Fatal(refmol.GetTitle() + " doesn't have 3D coordinates");
        }
        reffs.close();

        oemolistream fitfs = new oemolistream();
        if (!fitfs.open(argv[1])) {
            oechem.OEThrow.Fatal("Unable to open " + argv[1] + " for reading");
        }
        if (!oechem.OEIs3DFormat(fitfs.GetFormat())) {
            oechem.OEThrow.Fatal("Invalid input format: need 3D coordinates");
        }

        oemolostream ofs = new oemolostream();
        if (!ofs.open(argv[2])) {
            oechem.OEThrow.Fatal("Unable to open " + argv[2] + " for writing");
        }
        if (!oechem.OEIs3DFormat(ofs.GetFormat())) {
            oechem.OEThrow.Fatal("Invalid output format: need 3D coordinates");
        }

        oechem.OEWriteConstMolecule(ofs, refmol);

        OESubSearch ss = new OESubSearch();
        if (!ss.Init(argv[3])) {
            oechem.OEThrow.Fatal("Unable to parse SMARTS: " + argv[3]);
        }

        oechem.OEPrepareSearch(refmol, ss);
        if (!ss.SingleMatch(refmol)) {
            oechem.OEThrow.Fatal("SMARTS fails to match refmol");
        }

        OEGraphMol fitmol = new OEGraphMol();
        while (oechem.OEReadMolecule(fitfs, fitmol)) {
            if (fitmol.GetDimension() != 3) {
                oechem.OEThrow.Warning(fitmol.GetTitle() + " doesn't have 3D coordinates");
                continue;
            }
            oechem.OEPrepareSearch(fitmol, ss);
            if (!ss.SingleMatch(fitmol))
            {
                oechem.OEThrow.Warning("SMARTS fails to match fitmol " + fitmol.GetTitle());
                continue;
            }

            smartsAlign(refmol, fitmol, ss, ofs);
        }
        fitfs.close();
        ofs.close();
    }
}
