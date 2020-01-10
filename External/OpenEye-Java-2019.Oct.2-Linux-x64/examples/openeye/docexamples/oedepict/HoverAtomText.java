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

public class HoverAtomText {
    public static void main(String argv[]) {
        int width  = 400;
        int height = 200;

        OEImage image = new OEImage(width, height);

        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "Cc1cccnc1/C=C/[C@H](C(=O)O)O");
        oedepict.OEPrepareDepiction(mol);

        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, OEScale.AutoScale);
        opts.SetMargins(10);
        OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);

        OEFont font = new OEFont(OEFontFamily.Default, OEFontStyle.Default, 12,
                                 OEAlignment.Center, oechem.getOEDarkRed());

        for (OE2DAtomDisplay adisp : disp.GetAtomDisplays()) {
            OEAtomBase atom = adisp.GetAtom();
            String hovertext = "atom idx=" + Integer.toString(atom.GetIdx());
            oedepict.OEDrawSVGHoverText(disp, adisp, hovertext, font);
        }
        oedepict.OERenderMolecule("HoverAtomText.svg", disp);
    }
}
