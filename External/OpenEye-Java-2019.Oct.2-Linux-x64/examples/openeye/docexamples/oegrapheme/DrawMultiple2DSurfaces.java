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

public class DrawMultiple2DSurfaces {

    static void draw2DSurface(OE2DMolDisplay disp,  OEUnaryAtomPred atompred,
                              double radius,  OEColor color) {

        OEPen penA = new OEPen(color, color, OEFill.Off, 2.0);
        OEDefaultArcFxn arcfxnA = new OEDefaultArcFxn(penA);

        OEColor grey = new OEColor(oechem.getOELightGrey());
        OEPen penB = new OEPen(grey, grey, OEFill.Off, 2.0, OEStipple.ShortDash);
        OEDefaultArcFxn arcfxnB = new OEDefaultArcFxn(penB);

        OEImage layer = disp.GetLayer(OELayerPosition.Below);

        for (OE2DAtomDisplay adisp : disp.GetAtomDisplays()) {
            for (OESurfaceArc arc : oegrapheme.OEGet2DSurfaceArcs(disp, adisp, radius)) {
                if (atompred.constCall(adisp.GetAtom()))
                    arcfxnA.constCall(layer, arc);
                else
                    arcfxnB.constCall(layer, arc);
            }
        }
    }

    static void draw2DSurfaces(OEImageBase image, OEMolBase mol) {

        oedepict.OEPrepareDepiction(mol);

        double scale =  OEScale.AutoScale;
        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(image.GetWidth(), image.GetHeight(), scale);
        opts.SetTitleLocation(OETitleLocation.Hidden);
        opts.SetScale(oegrapheme.OEGetMoleculeSurfaceScale(mol, opts, 1.50));

        OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);

        draw2DSurface(disp, new OEHasAtomicNum(OEElemNo.C), 1.00, oechem.getOEBlack());
        draw2DSurface(disp, new OEHasAtomicNum(OEElemNo.N), 1.25, oechem.getOEDarkBlue());
        draw2DSurface(disp, new OEHasAtomicNum(OEElemNo.O), 1.50, oechem.getOEDarkRed());

        oedepict.OERenderMolecule(image, disp);

    }

    public static void main(String argv[]) {
        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "c1cc(N)ccc1O");

        double imagewidth  = 300;
        double imageheight = 260;
        OEImage image = new OEImage(imagewidth, imageheight);
        draw2DSurfaces(image, mol);

        oedepict.OEWriteImage("DrawMultiple2DSurfaces.png", image);
    }
}
