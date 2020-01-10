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
import java.util.ArrayList;

public class Draw2DSurfaceVariousRadii {

    public static void drawSurfaces (OEImageBase image, OEMolBase mol) {
        oechem.OEAssignCovalentRadii(mol);
        double minradius = oechem.OEGetCovalentRadius(OEElemNo.H);

        ArrayList<Double> radiusScales = new ArrayList<Double>(mol.GetMaxAtomIdx());

        double maxrscale = Double.MIN_VALUE;

        for (OEAtomBase atom : mol.GetAtoms()) {
            double rscale = (atom.GetRadius() - minradius) + OESurfaceArcScale.Minimum;
            radiusScales.add(atom.GetIdx(), rscale);
            maxrscale = Math.max(maxrscale, rscale);
        }

        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(image.GetWidth(),
                                                               image.GetHeight(), OEScale.AutoScale);
        opts.SetTitleLocation(OETitleLocation.Hidden);
        opts.SetScale(oegrapheme.OEGetMoleculeSurfaceScale(mol, opts, maxrscale));

        OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);

        OEImage layer = disp.GetLayer(OELayerPosition.Below);

        OEPen penA = new OEPen(oechem.getOELightGrey(), oechem.getOELightGrey(), OEFill.Off, 2.0, OEStipple.ShortDash);
        OEDefaultArcFxn arcfxnA = new OEDefaultArcFxn(penA);

        for (OESurfaceArc arc : oegrapheme.OEGet2DSurfaceArcs(disp, OESurfaceArcScale.Minimum)) {
            arcfxnA.constCall(layer, arc);
        }

        OEPen penB = new OEPen(oechem.getOEGrey(), oechem.getOEGrey(), OEFill.Off, 2.0);
        OEDefaultArcFxn arcfxnB = new OEDefaultArcFxn(penB);

        for (OESurfaceArc arc : oegrapheme.OEGet2DSurfaceArcs(disp, radiusScales)) {
            arcfxnB.constCall(layer, arc);
        }

        oedepict.OERenderMolecule(image, disp);
    }

    public static void main(String argv[]) {

        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "c1cc(sc1CCl)Br");
        oedepict.OEPrepareDepiction(mol);

        double imagewidth = 300.0;
        double imageheight = 240.0;
        OEImage image = new OEImage(imagewidth, imageheight);
        drawSurfaces(image, mol);

        oedepict.OEWriteImage("Draw2DSurfaceVariousRadii.png", image);
    }
}

