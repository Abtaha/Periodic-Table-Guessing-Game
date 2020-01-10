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

public class MCSNoAlignment {
    public static void main(String argv[]) {
        OEGraphMol refmol = new OEGraphMol();
        oechem.OESmilesToMol(refmol, "c1cc(c2cc(cnc2c1)CCCO)C(=O)CCO");
        oedepict.OEPrepareDepiction(refmol);

        OEGraphMol fitmol = new OEGraphMol();
        oechem.OESmilesToMol(fitmol, "c1cc2ccc(cc2c(c1)C(=O)O)CCO");
        oedepict.OEPrepareDepiction(fitmol);

        OEMCSSearch mcss = new OEMCSSearch(OEMCSType.Approximate);
        int atomexpr = OEExprOpts.DefaultAtoms;
        int bondexpr = OEExprOpts.DefaultBonds;
        mcss.Init(refmol, atomexpr, bondexpr);
        mcss.SetMCSFunc(new OEMCSMaxBondsCompleteCycles());

        OEImage image = new OEImage(400, 200);

        int rows = 1;
        int cols = 2;
        OEImageGrid grid = new OEImageGrid(image, rows, cols);
        OEImageBase refcell = grid.GetCell(1, 1);
        OEImageBase fitcell = grid.GetCell(1, 2);

        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(grid.GetCellWidth(),
                grid.GetCellHeight(),
                OEScale.AutoScale);
        opts.SetTitleLocation(OETitleLocation.Hidden);

        double refscale = oedepict.OEGetMoleculeScale(refmol, opts);
        double fitscale = oedepict.OEGetMoleculeScale(fitmol, opts);
        opts.SetScale(Math.min(refscale, fitscale));

        int hstyle = OEHighlightStyle.BallAndStick;

        boolean unique = true;
        OEMatchBaseIter matchiter = mcss.Match(fitmol, unique);
        if (matchiter.IsValid()) {
            OEMatchBase match = matchiter.Target();

            // depict reference molecule with MCS highlighting
            OE2DMolDisplay refdisp  = new OE2DMolDisplay(mcss.GetPattern(), opts);
            OEAtomBondSet  refabset = new OEAtomBondSet(match.GetPatternAtoms(), match.GetPatternBonds());
            oedepict.OEAddHighlighting(refdisp, oechem.getOEBlueTint(), hstyle, refabset);
            oedepict.OERenderMolecule(refcell, refdisp);

            // depict fit molecule with MCS highlighting
            OE2DMolDisplay fitdisp  = new OE2DMolDisplay(fitmol, opts);
            OEAtomBondSet  fitabset = new OEAtomBondSet(match.GetTargetAtoms(), match.GetTargetBonds());
            oedepict.OEAddHighlighting(fitdisp, oechem.getOEBlueTint(), hstyle, fitabset);
            oedepict.OERenderMolecule(fitcell, fitdisp);
        }
        oedepict.OEWriteImage("MCSNoAlignment.png", image);
    }
}
