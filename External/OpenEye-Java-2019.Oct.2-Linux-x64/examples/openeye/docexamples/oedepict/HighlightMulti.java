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

public class HighlightMulti {
    public static void main(String argv[]) {
        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "c1cncc2c1cc3c(cncc3)c2");
        oedepict.OEPrepareDepiction(mol);

        OESubSearch ss = new OESubSearch("c1cc[c,n]cc1");

        double width  = 350;
        double height = 250;
        OEImage image = new OEImage(width, height);

        int rows = 2;
        int cols = 2;
        OEImageGrid grid = new OEImageGrid(image, rows, cols);
        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(grid.GetCellWidth(),
                grid.GetCellHeight(),
                OEScale.AutoScale);

        boolean unique = true;
        OEImageBaseIter celliter = grid.GetCells();
        for (OEMatchBase match : ss.Match(mol, unique)) {
            OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);
            oedepict.OEAddHighlighting(disp, oechem.getOEPink(), OEHighlightStyle.Color, match);
            oedepict.OERenderMolecule(celliter.Target(), disp);
            celliter.Increment();
        }
        oedepict.OEWriteImage("HighlightMulti.svg", image);
    }
}
