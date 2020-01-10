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

/**************************************************************
 * USED TO GENERATE CODE SNIPPETS FOR THE OEDEPICT DOCUMENTATION
 **************************************************************/

public class HighlightByColor {

    private static void depictMoleculesWithHighlight(String smiles, OESubSearch ss, OEHighlightBase highlight, String basefilename) {
        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, smiles);
        oedepict.OEPrepareDepiction(mol);

        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(220, 160, OEScale.AutoScale);
        OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);

        boolean unique = true;
        for (OEMatchBase match : ss.Match(mol, unique)) {
            oedepict.OEAddHighlighting(disp, highlight, match);
        }

        OEImage image = new OEImage(disp.GetWidth(), disp.GetHeight());
        oedepict.OERenderMolecule(image, disp);

        oedepict.OEWriteImage(basefilename+".png", image);
        oedepict.OEWriteImage(basefilename+".pdf", image);
    }

    public static void main(String argv[]) {
        String smiles = "c1ccc2c(c1)cc(O)cn2";
        OESubSearch ss = new OESubSearch("c1ncc(O)cc1");

        {
            OEHighlightByColor highlight = new OEHighlightByColor(oechem.getOEDarkBlue());
            highlight.SetColor(oechem.getOEDarkRed());
            depictMoleculesWithHighlight(smiles, ss, highlight, "OEHighlightByColor_SetColor");
        }

        {
            OEHighlightByColor highlight = new OEHighlightByColor(oechem.getOEDarkRed());
            highlight.SetLineWidthScale(3.0);
            depictMoleculesWithHighlight(smiles, ss, highlight, "OEHighlightByColor_SetLineWidthScale");
        }

        {
            OEHighlightByColor highlight = new OEHighlightByColor(oechem.getOELightOrange());
            highlight.SetAtomExternalHighlight(true);
            depictMoleculesWithHighlight(smiles, ss, highlight, "OEHighlightByColor_SetAtomExternalHighlight");
        }
    }
}
