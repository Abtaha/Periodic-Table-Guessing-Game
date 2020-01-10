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

import java.util.ArrayList;
import java.util.Iterator;
import openeye.oechem.*;
import openeye.oedepict.*;
import openeye.oegrapheme.*;

public class Draw2DPropMapPChargeMulti {

    static double minvalue = Double.MAX_VALUE;
    static double maxvalue = Double.MIN_VALUE;

    public static void setPartialCharge(OEMolBase mol, String tagname) {
        oechem.OEMMFFAtomTypes(mol);
        oechem.OEMMFF94PartialCharges(mol);

        int tag = oechem.OEGetTag(tagname);
        for (OEAtomBase ai : mol.GetAtoms()) {
            double charge = ai.GetPartialCharge();
            ai.SetDoubleData(tag, charge);
            minvalue = Math.min(minvalue, charge);
            maxvalue = Math.max(maxvalue, charge);
        }
    }

    public static void main(String argv[]) {

        ArrayList<String> smiles = new ArrayList<String>();
        smiles.add("c1ccnc(c1)[N+](=O)[O-]");
        smiles.add("c1ccccc1F");
        smiles.add("c1ccc(cc1)S(=O)(=O)[O-]");

        String tagname = new String("PartialCharge");

        ArrayList<OEGraphMol> molecules = new ArrayList<OEGraphMol>();

        for (String s : smiles) {
            OEGraphMol mol = new OEGraphMol();
            oechem.OESmilesToMol(mol, s);
            oedepict.OEPrepareDepiction(mol);
            setPartialCharge(mol, tagname);
            molecules.add(mol);
        }

        int width  = 750;
        int height = 250;
        OEImage image = new OEImage(width, height);

        int rows = 1;
        int cols = 3;
        OEImageGrid grid = new OEImageGrid(image, rows, cols);

        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(grid.GetCellWidth(),
                grid.GetCellHeight(),
                OEScale.Default);
        opts.SetAtomColorStyle(OEAtomColorStyle.WhiteMonochrome);
        opts.SetTitleLocation(OETitleLocation.Hidden);

        OE2DPropMap propmap = new OE2DPropMap(opts.GetBackgroundColor());
        propmap.SetNegativeColor(oechem.getOEDarkRed());
        propmap.SetPositiveColor(oechem.getOEDarkBlue());
        propmap.SetLegendLocation(OELegendLocation.Left);

        propmap.SetMinValue(minvalue);
        propmap.SetMaxValue(maxvalue);
        Iterator<OEGraphMol> m = molecules.iterator();
        Iterator<OEImageBase> ci = grid.GetCells();
        while (m.hasNext() && ci.hasNext()) {
            OE2DMolDisplay disp = new OE2DMolDisplay(m.next(), opts);
            propmap.Render(disp, tagname);
            oedepict.OERenderMolecule(ci.next(), disp);
        }
        oedepict.OEWriteImage("Draw2DPropMapPartialChargeMulti.png", image);
    }
}
