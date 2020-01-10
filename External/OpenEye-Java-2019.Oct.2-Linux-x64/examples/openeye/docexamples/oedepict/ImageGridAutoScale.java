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

public class ImageGridAutoScale {

    public static void main(String[] args) {
        ArrayList<String> smiles = new ArrayList<String>();
        smiles.add("c1ccccc1");
        smiles.add("c1cccc2c1ccnc2");
        smiles.add("c1ccc2c(c1)cc3ccccc3n2");

        OEImage image = new OEImage(400, 200);

        int rows = 1;
        int cols = 3;
        OEImageGrid grid = new OEImageGrid(image, rows, cols);

        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(grid.GetCellWidth(),
                grid.GetCellHeight(),
                OEScale.AutoScale);

        int counter = 0;
        for (OEImageBase cell : grid.GetCells()) {
            OEGraphMol mol = new OEGraphMol();
            oechem.OESmilesToMol(mol, smiles.get(counter));
            oedepict.OEPrepareDepiction(mol);

            OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);
            oedepict.OERenderMolecule(cell, disp);
            counter += 1;
        }

        oedepict.OEWriteImage("ImageGridAutoScale.svg", image);
    }
}
