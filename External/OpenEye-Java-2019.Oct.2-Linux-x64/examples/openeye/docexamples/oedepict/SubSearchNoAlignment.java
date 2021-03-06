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

public class SubSearchNoAlignment {
    public static void main(String argv[]) {
        OESubSearch ss = new OESubSearch("O=C(O)C(N)");

        ArrayList<String> aminosmiles = new ArrayList<String>();
        aminosmiles.add("O=C(O)[C@@H](N)Cc1c[nH]cn1 Histidine");
        aminosmiles.add("CC(C)[C@@H](C(=O)O)N Valine");
        aminosmiles.add("C[C@H]([C@@H](C(=O)O)N)O Threonine");
        aminosmiles.add("C1C[C@H](NC1)C(=O)O Proline");

        ArrayList<OEGraphMol> aminoacids = new ArrayList<OEGraphMol>();
        for (String smiles : aminosmiles) {
            OEGraphMol mol = new OEGraphMol();
            oechem.OESmilesToMol(mol, smiles);
            oedepict.OEPrepareDepiction(mol);
            aminoacids.add(mol);
        }

        OEImage image = new OEImage(400, 400);

        int rows = 2;
        int cols = 2;
        OEImageGrid grid = new OEImageGrid(image, rows, cols);

        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(grid.GetCellWidth(),
                grid.GetCellHeight(),
                OEScale.AutoScale);
        opts.SetAtomStereoStyle(OEAtomStereoStyle.Display.All);

        double minscale = Double.MAX_VALUE;
        for (OEGraphMol mol : aminoacids) {
            minscale = Math.min(minscale, oedepict.OEGetMoleculeScale(mol, opts));
        }
        opts.SetScale(minscale);

        OEImageBaseIter celliter = grid.GetCells();
        for (OEGraphMol mol : aminoacids) {
            OEMatchBaseIter matchiter = ss.Match(mol, true);
            if (matchiter.IsValid()) {
                OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);
                int hstyle = OEHighlightStyle.Stick;
                oedepict.OEAddHighlighting(disp, oechem.getOELightGreen(), hstyle, matchiter.Target());
                oedepict.OERenderMolecule(celliter.Target(), disp);
                celliter.Increment();
            }
        }
        oedepict.OEWriteImage("SubSearchNoAlignment.png", image);
    }
}
