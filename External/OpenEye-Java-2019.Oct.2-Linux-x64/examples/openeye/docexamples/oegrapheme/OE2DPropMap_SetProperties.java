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

/**************************************************************
 * USED TO GENERATE CODE SNIPPETS FOR THE GRAPHEME DOCUMENTATION
 **************************************************************/

public class OE2DPropMap_SetProperties {
    public static void depictPropMap(OE2DPropMap propmap, OEMolBase mol,
            String tagname, String basefilename) {
        int width  = 350;
        int height = 250;

        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, OEScale.AutoScale);
        opts.SetTitleLocation(OETitleLocation.Bottom);
        opts.SetAtomColorStyle(OEAtomColorStyle.WhiteMonochrome);

        OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);
        OEImage below = disp.GetLayer(OELayerPosition.Below);
        propmap.Render(below, disp, tagname);

        oedepict.OERenderMolecule(basefilename+".pdf", disp);
        oedepict.OERenderMolecule(basefilename+".png", disp);
    }

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
        oechem.OESmilesToMol(mol, "Cc1cc(cc(c1[N+](=O)[O-])F)CC[N+]#C");
        oedepict.OEPrepareDepiction(mol);

        String tagname = new String("PartialCharge");
        setPartialCharge(mol, tagname);

        oedepict.OEPrepareDepiction(mol);

        {
            OE2DPropMap propmap = new OE2DPropMap();
            depictPropMap(propmap, mol, tagname, "OE2DPropMap_Default");
        }

        {
            OE2DPropMap propmap = new OE2DPropMap();
            propmap.SetNegativeColor(oechem.getOEDarkGreen());
            depictPropMap(propmap, mol, tagname, "OE2DPropMap_SetNegativeColor");
        }

        {
            OE2DPropMap propmap = new OE2DPropMap();
            propmap.SetMinValue(0.0);
            depictPropMap(propmap, mol, tagname, "OE2DPropMap_SetMinValue");
        }

        {
            OE2DPropMap propmap = new OE2DPropMap();
            propmap.SetPositiveColor(oechem.getOEDarkGreen());
            depictPropMap(propmap, mol, tagname, "OE2DPropMap_SetPositiveColor");
        }

        {
            OE2DPropMap propmap = new OE2DPropMap();
            propmap.SetMaxValue(0.0);
            depictPropMap(propmap, mol, tagname, "OE2DPropMap_SetMaxValue");
        }

        {
            OE2DPropMap propmap = new OE2DPropMap();
            OEFont font = new OEFont();
            font.SetStyle(OEFontStyle.Bold);
            font.SetColor(oechem.getOEDarkBlue());
            propmap.SetLegendFont(font);
            depictPropMap(propmap, mol, tagname,"OE2DPropMap_SetLegendFont");
        }

        {
            OE2DPropMap propmap = new OE2DPropMap();
            propmap.SetLegendFontScale(1.5);
            depictPropMap(propmap, mol, tagname,"OE2DPropMap_SetLegendFontScale");
        }

        {
            OE2DPropMap propmap = new OE2DPropMap();
            propmap.SetLegendLocation(OELegendLocation.Left);
            depictPropMap(propmap, mol, tagname, "OE2DPropMap_SetLegendLocation");
        }

        {
            OE2DPropMap propmap = new OE2DPropMap();
            propmap.SetResolution(5);
            depictPropMap(propmap, mol, tagname, "OE2DPropMap_SetResolution");
        }

        {
            OE2DPropMap propmap = new OE2DPropMap();
            propmap.SetRadiusRatio(0.75);
            depictPropMap(propmap, mol, tagname, "OE2DPropMap_SetRadiusRatio");
        }
    }
}
