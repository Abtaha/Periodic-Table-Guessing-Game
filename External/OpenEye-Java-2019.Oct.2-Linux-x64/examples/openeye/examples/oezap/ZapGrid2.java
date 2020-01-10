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

import java.net.URL;
import openeye.oechem.*;
import openeye.oegrid.*;
import openeye.oezap.*;

public class ZapGrid2 {
    static { 
        oechem.OEThrow.SetStrict(true);
    }

    private void createGrid(OEInterface itf) {
        OEGraphMol mol = new OEGraphMol();
        oemolistream ifs = new oemolistream();
        if (!ifs.open(itf.GetString("-in"))) {
            System.err.println("Unable to open for reading: " + itf.GetString("-in"));
            System.exit(1);
        }
        oechem.OEReadMolecule(ifs, mol);
        oechem.OEAssignBondiVdWRadii(mol);
        oechem.OEMMFFAtomTypes(mol);
        oechem.OEMMFF94PartialCharges(mol);
        ifs.close();

        OEZap zap = new OEZap();
        zap.SetInnerDielectric(itf.GetFloat("-epsin"));
        zap.SetOuterDielectric(itf.GetFloat("-epsout"));
        zap.SetGridSpacing(itf.GetFloat("-grid_spacing"));
        zap.SetBoundarySpacing(itf.GetFloat("-buffer"));
        zap.SetMolecule(mol);

        OEScalarGrid grid = new OEScalarGrid();
        if (zap.CalcPotentialGrid(grid)) {
            if (itf.GetBool("-mask")) {
                oegrid.OEMaskGridByMolecule(grid, mol);
            }
        }
        oegrid.OEWriteGrid(itf.GetString("-out"), grid);
        zap.delete();
    }

    public static void main(String argv[]) {
        ZapGrid2 app = new ZapGrid2();
        OEInterface itf = new OEInterface();
        URL fileURL = app.getClass().getResource("ZapGrid2.txt");
        try {
            oechem.OEConfigureFromURL(itf, fileURL);
            if (oechem.OECheckHelp(itf, argv)) {
                return;
            }
            oechem.OEParseCommandLine(itf, argv);
            app.createGrid(itf);
        } catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
