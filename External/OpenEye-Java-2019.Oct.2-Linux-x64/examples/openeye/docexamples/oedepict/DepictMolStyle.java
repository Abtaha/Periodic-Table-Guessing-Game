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

public class DepictMolStyle {
    public static void main(String argv[]) {
        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "c1cc(N)cc(S(=O)(=O)O)c1 3-aminobenzenesulfonic acid");
        oedepict.OEPrepareDepiction(mol);

        double width  = 200;
        double height = 200;
        double scale  = OEScale.AutoScale;
        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);

        opts.SetAromaticStyle(OEAromaticStyle.Circle);
        opts.SetAtomColorStyle(OEAtomColorStyle.BlackCPK);
        opts.SetTitleLocation(OETitleLocation.Bottom);

        OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);
        oedepict.OERenderMolecule("DepictMolStyle.svg", disp);
    }
}
