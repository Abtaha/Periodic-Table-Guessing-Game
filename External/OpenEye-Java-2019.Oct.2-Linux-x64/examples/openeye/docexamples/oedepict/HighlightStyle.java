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

public class HighlightStyle {
    public static void main(String argv[]) {
        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "c1cncc2c1cc3c(cncc3)c2");
        oedepict.OEPrepareDepiction(mol);

        OESubSearch ss =  new OESubSearch("c1ncccc1");

        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(240.0, 100.0, OEScale.AutoScale);
        opts.SetMargins(10.0);
        OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);

        double stickWidthScale = 6.0;
        boolean monochrome = true;
        OEHighlightByStick highlight = new OEHighlightByStick(oechem.getOERed(),
                stickWidthScale,
                !monochrome);

        boolean unique = true;
        for (OEMatchBase match : ss.Match(mol, unique)) {
            oedepict.OEAddHighlighting(disp, highlight, match);
        }

        oedepict.OERenderMolecule("HighlightStyle.svg", disp);
    }
}
