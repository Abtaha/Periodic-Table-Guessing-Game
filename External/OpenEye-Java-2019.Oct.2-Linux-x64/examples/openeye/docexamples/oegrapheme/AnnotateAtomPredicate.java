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
package openeye.docexamples.oegrapheme;

import openeye.oechem.*;
import openeye.oedepict.*;
import openeye.oegrapheme.*;

public class AnnotateAtomPredicate {
    public static void main(String argv[]) {
        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "c1cc(N)cc(S(=O)(=O)O)c1");
        oechem.OEAssignHybridization(mol);
        oedepict.OEPrepareDepiction(mol);

        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(350, 250, OEScale.AutoScale);
        opts.SetTitleLocation(OETitleLocation.Hidden);
        OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);

        OEPen penSP2 = new OEPen(oechem.getOEWhite(), oechem.getOEBlueTint(), OEFill.Off, 1.5);
        OEAtomGlyphCircle glyphSP2 = new OEAtomGlyphCircle(penSP2, OECircleStyle.Sun, 1.2);
        oegrapheme.OEAddGlyph(disp, glyphSP2, new OEIsAtomHybridization(OEHybridization.sp2));

        OEPen penSP3 = new OEPen(oechem.getOEWhite(), oechem.getOEPinkTint(), OEFill.Off, 1.5);
        OEAtomGlyphCircle glyphSP3 = new OEAtomGlyphCircle(penSP3, OECircleStyle.Eyelash, 1.2);
        oegrapheme.OEAddGlyph(disp, glyphSP3, new OEIsAtomHybridization(OEHybridization.sp3));

        oedepict.OERenderMolecule("AnnotateAtomPredicate.png", disp);
    }
}
