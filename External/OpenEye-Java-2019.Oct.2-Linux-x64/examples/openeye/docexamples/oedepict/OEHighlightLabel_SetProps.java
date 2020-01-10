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

public class OEHighlightLabel_SetProps {

    private static void depictMoleculesWithLabel(String smiles, OESubSearch ss,
                                                 OEHighlightBase highlight, OEHighlightLabel label,
                                                 String basefilename) {
        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, smiles);
        oedepict.OEPrepareDepiction(mol);

        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(220, 160, OEScale.AutoScale);
        OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);

        boolean unique = true;
        for (OEMatchBase match : ss.Match(mol, unique)) {
            oedepict.OEAddHighlighting(disp, highlight, match);
            oedepict.OEAddLabel(disp, label, match);
        }

        OEImage image = new OEImage(disp.GetWidth(), disp.GetHeight());
        oedepict.OERenderMolecule(image, disp);

        oedepict.OEWriteImage(basefilename+".png", image);
    }

    public static void main(String argv[]) {

        String smiles = "c1ccc2c(c1)cc(O)cn2";
        OESubSearch ss = new OESubSearch("c1ncc(O)cc1");

        OEHighlightByLasso highlight = new OEHighlightByLasso(oechem.getOEDarkRed());
        highlight.SetConsiderAtomLabelBoundingBox(true);

        {
        OEHighlightLabel label = new OEHighlightLabel("match");
        depictMoleculesWithLabel(smiles, ss, highlight, label, "OEHighlightLabel_Default");
    }

    {
        OEHighlightLabel label = new OEHighlightLabel("match", oechem.getOEDarkRed());
        depictMoleculesWithLabel(smiles, ss, highlight, label, "OEHighlightLabel_Color");
    }

    {
        OEFont font = new OEFont(OEFontFamily.Courier, OEFontStyle.Bold, 12,
                                     OEAlignment.Center, oechem.getOEDarkRed());
        OEHighlightLabel label = new OEHighlightLabel("match", font);
        depictMoleculesWithLabel(smiles, ss, highlight, label, "OEHighlightLabel_Font");
    }

    {
        OEHighlightLabel label = new OEHighlightLabel("match");
        OEFont font = new OEFont(OEFontFamily.Default, OEFontStyle.Default, 12,
                                     OEAlignment.Center, oechem.getOEDarkRed());
        label.SetFont(font);
        depictMoleculesWithLabel(smiles, ss, highlight, label, "OEHighlightLabel_SetFont");
    }

    {
        OEHighlightLabel label = new OEHighlightLabel("match");
        OEPen pen = new OEPen(new OEColor(255, 220, 220), oechem.getOEDarkRed(), OEFill.On, 16.0, OEStipple.ShortDash);
        label.SetBoundingBoxPen(pen);
        depictMoleculesWithLabel(smiles, ss, highlight, label, "OEHighlightLabel_SetBoundingBoxPen");
    }

    {
        OEHighlightLabel label = new OEHighlightLabel("match");
        label.SetFontScale(1.2);
        depictMoleculesWithLabel(smiles, ss, highlight, label, "OEHighlightLabel_SetFontScale");
    }
    }
}
