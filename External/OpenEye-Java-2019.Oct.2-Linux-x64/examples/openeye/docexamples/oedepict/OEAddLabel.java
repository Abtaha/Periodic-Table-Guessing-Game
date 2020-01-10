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

public class OEAddLabel {
    private static void OEAddLabel_Predicate(OE2DMolDisplay disp) {
        OEHighlightByBallAndStick ringhighlight = new OEHighlightByBallAndStick(oechem.getOELightGreen());
        oedepict.OEAddHighlighting(disp, ringhighlight, new OEAtomIsInRing(), new OEBondIsInRing());
        OEHighlightLabel ringlabel = new OEHighlightLabel("ring", oechem.getOELightGreen());
        oedepict.OEAddLabel(disp, ringlabel, new OEAtomIsInRing());

        OEHighlightByBallAndStick chainhighlight = new OEHighlightByBallAndStick(oechem.getOEBlueTint());
        oedepict.OEAddHighlighting(disp, chainhighlight, new OEAtomIsInChain(), new OEBondIsInChain());
        OEHighlightLabel chainlabel = new OEHighlightLabel("chain", oechem.getOEBlueTint());
        oedepict.OEAddLabel(disp, chainlabel, new OEAtomIsInChain());
    }

    private static void OEAddLabel_OEMatch(OE2DMolDisplay disp) {
        OEMolBase mol = disp.GetMolecule();

        OESubSearch subs = new OESubSearch("a1aaaaa1");
        boolean unique = true;
        OEHighlightByBallAndStick highlight = new OEHighlightByBallAndStick(oechem.getOELightGreen());
        for (OEMatchBase match : subs.Match(mol, unique)) {
            oedepict.OEAddHighlighting(disp, highlight, match);
            OEHighlightLabel label = new OEHighlightLabel("aromatic", oechem.getOELightGreen());
            oedepict.OEAddLabel(disp, label, match);
        }
    }

    private static void OEAddLabel_OEAtomBondSet(OE2DMolDisplay disp) {
        OEMolBase mol = disp.GetMolecule();

        OEAtomBondSet ringset = new OEAtomBondSet(mol.GetAtoms(new OEAtomIsInRing()),
              mol.GetBonds(new OEBondIsInRing()));
        OEHighlightByBallAndStick ringhighlight = new OEHighlightByBallAndStick(oechem.getOELightGreen());
        oedepict.OEAddHighlighting(disp, ringhighlight, ringset);
        OEHighlightLabel ringlabel = new OEHighlightLabel("ring", oechem.getOELightGreen());
        oedepict.OEAddLabel(disp, ringlabel, ringset);

        OEAtomBondSet chainset = new OEAtomBondSet(mol.GetAtoms(new OEAtomIsInChain()),
                                                   mol.GetBonds(new OEBondIsInChain()));
        OEHighlightByBallAndStick chainhighlight = new OEHighlightByBallAndStick(oechem.getOEBlueTint());
        oedepict.OEAddHighlighting(disp, chainhighlight, chainset);
        OEHighlightLabel chainlabel = new OEHighlightLabel("chain", oechem.getOEBlueTint());
        oedepict.OEAddLabel(disp, chainlabel, chainset);
    }

    private static void OEAddLabel_OEImage(OEImageBase image) {

        OEHighlightLabel label = new OEHighlightLabel("Hello!");
        oedepict.OEAddLabel(image, new OE2DPoint(50, 50), label);

        label.SetBoundingBoxPen(oedepict.getOETransparentPen());
        oedepict.OEAddLabel(image, new OE2DPoint(100, 50), label);

        label.SetBoundingBoxPen(oedepict.getOELightGreyPen());
        oedepict.OEAddLabel(image, new OE2DPoint(150, 50), label);
    }

    ///////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////

    public static void main(String argv[]) {
        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "CC(=O)C1Cc2ccccc2N1");
        oedepict.OEPrepareDepiction(mol);

        int width  = 250;
        int height = 180;
        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, OEScale.AutoScale);
        opts.SetTitleLocation(OETitleLocation.Hidden);

        {
            OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);
            OEAddLabel_Predicate(disp);
            oedepict.OERenderMolecule("OEAddLabel-Predicate.png", disp);
        }

        {
            OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);
            OEAddLabel_OEMatch(disp);
            oedepict.OERenderMolecule("OEAddLabel-OEMatch.png", disp);
        }

        {
            OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);
            OEAddLabel_OEAtomBondSet(disp);
            oedepict.OERenderMolecule("OEAddLabel-OEAtomBondSet.png", disp);
        }

        {
            OEImage image = new OEImage(200, 100);
            OEAddLabel_OEImage(image);
            oedepict.OEWriteImage("OEAddLabel-OEImageBase.png", image);
        }
    }
}
