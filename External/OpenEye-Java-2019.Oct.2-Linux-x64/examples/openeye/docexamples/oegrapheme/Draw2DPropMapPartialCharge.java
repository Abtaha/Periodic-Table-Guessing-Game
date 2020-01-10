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

public class Draw2DPropMapPartialCharge {
    public static void setPartialCharge(OEMolBase mol, String tagname) {
        oechem.OEMMFFAtomTypes(mol);
        oechem.OEMMFF94PartialCharges(mol);
        int tag = oechem.OEGetTag(tagname);
        for (OEAtomBase ai : mol.GetAtoms()) {
            ai.SetDoubleData(tag, ai.GetPartialCharge());
        }
    }

    public static void main(String argv[]) {
        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "Cc1cc(cc(c1[N+](=O)[O-])F)[N+]#C");
        oedepict.OEPrepareDepiction(mol);

        String tagname = new String("PartialCharge");
        setPartialCharge(mol, tagname);

        oedepict.OEPrepareDepiction(mol);

        int width  = 450;
        int height = 350;

        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, OEScale.AutoScale);
        opts.SetAtomColorStyle(OEAtomColorStyle.WhiteMonochrome);
        opts.SetTitleLocation(OETitleLocation.Hidden);
        opts.SetScale(oegrapheme.OEGetMoleculeSurfaceScale(mol, opts));
        OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);

        OE2DPropMap propmap = new OE2DPropMap(opts.GetBackgroundColor());
        propmap.SetNegativeColor(oechem.getOEDarkRed());
        propmap.SetPositiveColor(oechem.getOEDarkBlue());
        propmap.Render(disp, tagname);

        oedepict.OERenderMolecule("Draw2DPropMapPartialCharge.png", disp);
    }
}
