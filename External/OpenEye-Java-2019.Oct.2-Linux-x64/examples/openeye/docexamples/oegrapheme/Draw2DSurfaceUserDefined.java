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

public class Draw2DSurfaceUserDefined {

    public static class AtomColorArcFxn extends OESurfaceArcFxnBase {
        @Override
        public boolean constCall(OEImageBase image, OESurfaceArc arc) {
            OE2DAtomDisplay adisp = arc.GetAtomDisplay();
            if (adisp == null || !adisp.IsVisible())
                return false;

            OEPen pen = new OEPen();
            OEColor color = adisp.GetLabelFont().GetColor();
            pen.SetForeColor(color);

            OE2DPoint center = arc.GetCenter();
            double radius    = arc.GetRadius();
            double bgnAngle  = arc.GetBgnAngle();
            double endAngle  = arc.GetEndAngle();

            oegrapheme.OEDrawEyelashSurfaceArc(image, center, bgnAngle, endAngle, radius, pen);
            return true;
        }

        @Override
        public OESurfaceArcFxnBase CreateCopy() {
            AtomColorArcFxn copy = new AtomColorArcFxn();
            copy.swigReleaseOwnership();
            return copy;
        }
    }
    static void draw2DSurface(OEImageBase image, OEMolBase mol) {

        oedepict.OEPrepareDepiction(mol);

        double scale =  OEScale.AutoScale;
        OE2DMolDisplayOptions opts =  new OE2DMolDisplayOptions(image.GetWidth(), image.GetHeight(), scale);
        opts.SetTitleLocation(OETitleLocation.Hidden);
        opts.SetScale(oegrapheme.OEGetMoleculeSurfaceScale(mol, opts));

        AtomColorArcFxn arcfxn = new AtomColorArcFxn();
        for (OEAtomBase atom : mol.GetAtoms()) {
            oegrapheme.OESetSurfaceArcFxn(mol, atom, arcfxn);
        }

        OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);
        oegrapheme.OEDraw2DSurface(disp);
        oedepict.OERenderMolecule(image, disp);
    }

    public static void main(String argv[]) {
        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "OC(=O)c1cnc(c(Cl)c1)-c2ccccc2");

        double imagewidth  = 350;
        double imageheight = 250;
        OEImage image = new OEImage(imagewidth, imageheight);
        draw2DSurface(image, mol);

        oedepict.OEWriteImage("Draw2DSurfaceUserDefined.png", image);
    }
}
