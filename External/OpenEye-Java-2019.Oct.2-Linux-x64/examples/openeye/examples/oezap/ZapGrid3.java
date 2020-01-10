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
import openeye.oegrid.*;
import openeye.oezap.*;

public class ZapGrid3 {

    public static void main(String argv[]) {
        if (argv.length != 1) {
            System.err.println("usage: ZapGrid3 <molfile>");
            System.exit(1);
        }

        OEGraphMol mol = new OEGraphMol();
        oemolistream ifs = new oemolistream();
        if (!ifs.open(argv[0])) {
            System.err.println("Unable to open for reading: " + argv[0]);
            System.exit(1);
        }
        oechem.OEReadMolecule(ifs, mol);
        ifs.close();

        oechem.OEAssignBondiVdWRadii(mol);
        oechem.OEMMFFAtomTypes(mol);
        oechem.OEMMFF94PartialCharges(mol);

        OEZap zap = new OEZap();
        zap.SetInnerDielectric(1.0f);
        zap.SetGridSpacing(0.5f);
        zap.SetMolecule(mol);

        // calculate standard 2-dielectric grid
        OEScalarGrid grid1 = new OEScalarGrid();
        zap.CalcPotentialGrid(grid1);

        // calculate grid with single dielectric
        OEScalarGrid grid2 = new OEScalarGrid();
        zap.SetOuterDielectric(zap.GetInnerDielectric());
        zap.CalcPotentialGrid(grid2);

        // take the difference
        oegrid.OESubtractScalarGrid(grid1, grid2);

        // mask out everything outside the molecule
        oegrid.OEMaskGridByMolecule(grid1, mol, OEGridMaskType.GaussianMinus);

        oegrid.OEWriteGrid("zap_diff.grd", grid1);

        // explicit clean ups
        grid1.delete();
        grid2.delete();
        zap.delete();
    }
}
