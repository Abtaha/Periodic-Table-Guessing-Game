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

import java.util.ArrayList;
import openeye.oechem.*;
import openeye.oedepict.*;

public class ImageGridFixedScale {
    public static void main(String[] args) {
        ArrayList<String> smiles = new ArrayList<String>();
        smiles.add("c1ccccc1");
        smiles.add("c1cccc2c1ccnc2");
        smiles.add("c1ccc2c(c1)cc3ccccc3n2");

        ArrayList<OEGraphMol> mollist = new ArrayList<OEGraphMol>();
        for (String smi : smiles) {
            OEGraphMol mol = new OEGraphMol();
            oechem.OESmilesToMol(mol, smi);
            oedepict.OEPrepareDepiction(mol);
            mollist.add(mol);
        }

        OEImage image = new OEImage(400, 200);

        int rows = 1;
        int cols = 3;
        OEImageGrid grid = new OEImageGrid(image, rows, cols);

        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(grid.GetCellWidth(),
                grid.GetCellHeight(),
                OEScale.AutoScale);

        double minscale = Double.MAX_VALUE;
        for (OEGraphMol mol : mollist) {
            minscale = Math.min(minscale, oedepict.OEGetMoleculeScale(mol, opts));
        }
        opts.SetScale(minscale);

        OEImageBaseIter celliter = grid.GetCells();
        for (OEGraphMol mol : mollist) {
            OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);
            oedepict.OERenderMolecule(celliter.Target(), disp);
            celliter.Increment();
        }

        oedepict.OEWriteImage("ImageGridFixedScale.svg", image);
    }
}
