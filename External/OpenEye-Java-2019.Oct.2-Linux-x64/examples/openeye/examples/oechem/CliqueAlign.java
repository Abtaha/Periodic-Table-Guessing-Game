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
 * Align two compounds based on the clique match
 ****************************************************************************/
package openeye.examples.oechem;

import openeye.oechem.*;

public class CliqueAlign {

    public static void cliqueAlign(OEMolBase refmol, OEMolBase fitmol,
            oemolostream ofs) {
        int atomexpr = OEExprOpts.DefaultAtoms;
        int bondexpr = OEExprOpts.DefaultBonds;
        OECliqueSearch cs = new OECliqueSearch(refmol, atomexpr, bondexpr);
        cs.SetSaveRange(5);
        cs.SetMinAtoms(6);
        for (OEMatchBase match : cs.Match(fitmol)) {
            double rmat[] = new double[9];
            double trans[] = new double[3];
            boolean overlay = true;
            oechem.OERMSD(cs.GetPattern(), fitmol, match, overlay, rmat, trans);
            oechem.OERotate(fitmol, rmat);
            oechem.OETranslate(fitmol, trans);
            oechem.OEWriteMolecule(ofs, fitmol);
        }
    }

    public static boolean is3DFormat(int fmt) {
        if (fmt == OEFormat.SMI || fmt == OEFormat.ISM ||
                fmt == OEFormat.CAN || fmt == OEFormat.MF) {
            return false;
        }
        return true;
    }

    public static void main(String argv[]) {
        if (argv.length != 3) {
            oechem.OEThrow.Usage("CliqueAlign <refmol> <fitmol> <outfile>");
        }

        oemolistream reffs = new oemolistream();
        if (!reffs.open(argv[0])) {
            oechem.OEThrow.Fatal("Unable to open " + argv[0] + " for reading");
        }
        if (!is3DFormat(reffs.GetFormat())) {
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
        if (!is3DFormat(fitfs.GetFormat())) {
            oechem.OEThrow.Fatal("Invalid input format: need 3D coordinates");
        }

        oemolostream ofs = new oemolostream();
        if (!ofs.open(argv[2])) {
            oechem.OEThrow.Fatal("Unable to open " + argv[2] + " for writing");
        }
        if (!is3DFormat(ofs.GetFormat())) {
            oechem.OEThrow.Fatal("Invalid output format: need 3D coordinates");
        }

        oechem.OEWriteConstMolecule(ofs, refmol);
        oechem.OESuppressHydrogens(refmol);

        OEGraphMol fitmol = new OEGraphMol();
        while (oechem.OEReadMolecule(fitfs, fitmol)) {
            if (fitmol.GetDimension() != 3) {
                oechem.OEThrow.Warning(fitmol.GetTitle() + " doesn't have 3D coordinates");
                continue;
            }
            cliqueAlign(refmol, fitmol, ofs);
        }
        fitfs.close();
        ofs.close();
    }
}
