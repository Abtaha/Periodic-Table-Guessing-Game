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

public class Draw2DSurfacePartialCharge {
    private static class AtomPartialChargeArcFxn extends OESurfaceArcFxnBase {
        private OELinearColorGradient colorg;
        private AtomPartialChargeArcFxn() {}
        public AtomPartialChargeArcFxn(OELinearColorGradient cg) {
            colorg = new OELinearColorGradient(cg);
        }

        @Override
        public boolean constCall(OEImageBase image, OESurfaceArc arc) {
            OE2DAtomDisplay adisp = arc.GetAtomDisplay();
            if (adisp == null || !adisp.IsVisible())
                return false;

            OEAtomBase atom = adisp.GetAtom();
            if (atom == null)
                return false;

            double charge = atom.GetPartialCharge();
            if (charge == 0.0)
                return true;
            OEColor color = colorg.GetColorAt(charge);

            OEPen pen = new OEPen();
            pen.SetForeColor(color);
            pen.SetLineWidth(2.0);

            OE2DPoint center = arc.GetCenter();
            double bAngle = arc.GetBgnAngle();
            double eAngle = arc.GetEndAngle();
            double radius = arc.GetRadius();

            double edgeAngle = 5.0;
            int dir = OEPatternDirection.Outside;
            double patternAngle = 10.0;
            oegrapheme.OEDrawBrickRoadSurfaceArc(image, center, bAngle, eAngle, radius,
                    pen, edgeAngle, dir, patternAngle);
            return true;
        }

        @Override
        public OESurfaceArcFxnBase CreateCopy() {
            AtomPartialChargeArcFxn copy = new AtomPartialChargeArcFxn(colorg);
            copy.swigReleaseOwnership();
            return copy;
        }
    }
    static void draw2DSurfacePartialCharge(OEImageBase image, OEMolBase mol) {

        oedepict.OEPrepareDepiction(mol);

        oechem.OEMMFFAtomTypes(mol);
        oechem.OEMMFF94PartialCharges(mol);

        double scale  = OEScale.AutoScale;
        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(image.GetWidth(), image.GetHeight(), scale);
        opts.SetTitleLocation(OETitleLocation.Hidden);
        opts.SetScale(oegrapheme.OEGetMoleculeSurfaceScale(mol, opts));

        OEColorStop coloranion = new OEColorStop(-1.0, oechem.getOEDarkRed());
        OEColorStop colorcation = new OEColorStop(+1.0, oechem.getOEDarkBlue());
        OELinearColorGradient colorg = new OELinearColorGradient(coloranion, colorcation);
        colorg.AddStop(new OEColorStop(0.0, oechem.getOEWhite()));

        AtomPartialChargeArcFxn arcfxn = new AtomPartialChargeArcFxn(colorg);

        for (OEAtomBase atom : mol.GetAtoms()) {
            oegrapheme.OESetSurfaceArcFxn(mol, atom, arcfxn);
        }

        OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);
        oegrapheme.OEDraw2DSurface(disp);

        oedepict.OERenderMolecule(image, disp);
    }

    public static void main(String argv[]) {
        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "Cc1cc(cc(c1[N+](=O)[O-])F)[N+]#C");

        oedepict.OEPrepareDepiction(mol);

        double imagewidth  = 350.0;
        double imageheight = 250.0;
        OEImage image = new OEImage(imagewidth, imageheight);
        draw2DSurfacePartialCharge(image, mol);

        oedepict.OEWriteImage("Draw2DSurfacePartialCharge.png", image);
    }
}
