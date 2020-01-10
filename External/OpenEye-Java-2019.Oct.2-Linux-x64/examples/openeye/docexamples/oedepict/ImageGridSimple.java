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

public class ImageGridSimple {
    public static void main(String args[]) {
        ArrayList<String> smiles = new ArrayList<String>();
        smiles.add("C1CC(C)CCC1");
        smiles.add("C1CC(O)CCC1");
        smiles.add("C1CC(Cl)CCC1");

        OEImage image = new OEImage(200, 200);

        int rows = 2;
        int cols = 2;
        OEImageGrid grid = new OEImageGrid(image, rows, cols);

        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(grid.GetCellWidth(),
                grid.GetCellHeight(),
                OEScale.AutoScale);
        OEImageBaseIter celliter = grid.GetCells();
        for (String smi : smiles) {
            OEGraphMol mol = new OEGraphMol();
            oechem.OESmilesToMol(mol, smi);
            oedepict.OEPrepareDepiction(mol);

            OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);
            oedepict.OERenderMolecule(celliter.Target(), disp);
            celliter.Increment();
        }

        oedepict.OEWriteImage("ImageGridSimple.svg", image);
    }
}
