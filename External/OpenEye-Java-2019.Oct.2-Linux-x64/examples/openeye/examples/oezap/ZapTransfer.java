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
package openeye.examples.oezap;

import openeye.oechem.*;
import openeye.oezap.*;

public class ZapTransfer {

    private static float epsin = 1.0f;

    public static void main(String argv[]) {
        if (argv.length != 1) {
            System.err.println("usage: ZapTransfer <molfile>");
            System.exit(1);
        }

        OEZap zap = new OEZap();
        zap.SetInnerDielectric(epsin);
        zap.SetGridSpacing(0.5f);

        OEArea area = new OEArea();

        oemolistream ifs = new oemolistream();
        if (!ifs.open(argv[0])) {
            System.err.println("Unable to open for reading: " + argv[0]);
            System.exit(1);
        }
        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            oechem.OEAssignBondiVdWRadii(mol);
            oechem.OEMMFFAtomTypes(mol);
            oechem.OEMMFF94PartialCharges(mol);
            zap.SetMolecule(mol);
            float solv = zap.CalcSolvationEnergy();
            float aval = area.GetArea(mol);
            float total = 0.59f * solv + 0.010f * aval;
            System.out.println("Title: " + mol.GetTitle());
            System.out.println("Vacuum->Water(kcal)        : " + total);
            System.out.println();
        }
        ifs.close();
        area.delete();
        zap.delete();
    }
}
