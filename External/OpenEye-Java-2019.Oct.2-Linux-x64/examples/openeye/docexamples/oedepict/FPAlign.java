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
package openeye.docexamples.oedepict;

import openeye.oechem.*;
import openeye.oedepict.*;
import openeye.oegraphsim.*;

public class FPAlign {
    public static void main(String argv[]) {
        OEGraphMol refmol = new OEGraphMol();
        oechem.OESmilesToMol(refmol, "C[C@H](C(=O)N1CCC[C@H]1C(=O)O)OC(=O)[C@@H](Cc2ccccc2)S");
        oedepict.OEPrepareDepiction(refmol);

        OEGraphMol fitmol = new OEGraphMol();
        oechem.OESmilesToMol(fitmol, "C[C@H](C(=O)N1CCC[C@H]1C(=O)O)NC(=O)[C@@H](Cc2ccccn2)S");
        oedepict.OEPrepareDepiction(fitmol);

        OEImage image = new OEImage(500, 300);

        int rows = 1;
        int cols = 2;
        OEImageGrid grid = new OEImageGrid(image, rows, cols);

        OEFPTypeBase fptype = oegraphsim.OEGetFPType(OEFPType.Tree);
        oedepict.OEPrepareMultiAlignedDepiction(fitmol, refmol,
              oegraphsim.OEGetFPOverlap(refmol, fitmol, fptype));

        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(grid.GetCellWidth(),
                grid.GetCellHeight(),
                OEScale.AutoScale);
        opts.SetTitleLocation(OETitleLocation.Hidden);

        double refscale = oedepict.OEGetMoleculeScale(refmol, opts);
        double fitscale = oedepict.OEGetMoleculeScale(fitmol, opts);
        opts.SetScale(Math.min(refscale, fitscale));

        OEImageBase refcell = grid.GetCell(1, 1);
        OE2DMolDisplay refdisp = new OE2DMolDisplay(refmol, opts);
        oedepict.OERenderMolecule(refcell, refdisp);

        OEImageBase fitcell = grid.GetCell(1, 2);
        OE2DMolDisplay fitdisp = new OE2DMolDisplay(fitmol, opts);
        oedepict.OERenderMolecule(fitcell, fitdisp);

        oedepict.OEWriteImage("FPAlign.png", image);
    }
}
