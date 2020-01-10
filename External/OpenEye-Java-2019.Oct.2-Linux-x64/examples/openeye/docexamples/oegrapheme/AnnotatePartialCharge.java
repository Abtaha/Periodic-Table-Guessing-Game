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

public class AnnotatePartialCharge {
    private static class ColorCharge extends OEAtomGlyphBase {
        private OELinearColorGradient colorg;

        private ColorCharge() {}
        public ColorCharge(OELinearColorGradient cg) {
            colorg = new OELinearColorGradient(cg);
        }

        @Override
        public boolean RenderGlyph(OE2DMolDisplay disp, OEAtomBase atom) {
            OE2DAtomDisplay adisp = disp.GetAtomDisplay(atom);
            if (adisp == null || !adisp.IsVisible())
                return false;

            double charge = atom.GetPartialCharge();
            if (charge == 0.0)
                return true;
            OEColor color = colorg.GetColorAt(charge);

            OEPen pen = new OEPen();
            pen.SetForeColor(color);
            color.SetA(100);
            pen.SetBackColor(color);
            pen.SetFill(OEFill.On);
            double radius = disp.GetScale()/2.5;

            OEImage layer = disp.GetLayer(OELayerPosition.Below);
            oegrapheme.OEDrawCircle(layer, OECircleStyle.Simpson, adisp.GetCoords(), radius, pen);

            return true;
        }

        @Override
        public OEAtomGlyphBase CreateCopy() {
            ColorCharge copy = new ColorCharge(colorg);
            copy.swigReleaseOwnership();
            return copy;
        }
    }

    public static void main(String argv[]) {
        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "Cc1cc(cc(c1[N+](=O)[O-])F)[N+]#C");
        oechem.OEMMFFAtomTypes(mol);
        oechem.OEMMFF94PartialCharges(mol);

        oedepict.OEPrepareDepiction(mol);

        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(350, 250, OEScale.AutoScale);
        opts.SetAtomColorStyle(OEAtomColorStyle.WhiteMonochrome);
        opts.SetTitleLocation(OETitleLocation.Hidden);
        OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);

        OEColorStop coloranion = new OEColorStop(-1.0, oechem.getOEDarkRed());
        OEColorStop colorcation = new OEColorStop(+1.0, oechem.getOEDarkBlue());
        OELinearColorGradient grad = new OELinearColorGradient(coloranion, colorcation);
        grad.AddStop(new OEColorStop(0.0, oechem.getOEWhite()));

        ColorCharge colorcharge = new ColorCharge(grad);

        oegrapheme.OEAddGlyph(disp, colorcharge, new OEIsTrueAtom());

        oedepict.OERenderMolecule("AnnotatePartialCharge.png", disp);
    }
}
