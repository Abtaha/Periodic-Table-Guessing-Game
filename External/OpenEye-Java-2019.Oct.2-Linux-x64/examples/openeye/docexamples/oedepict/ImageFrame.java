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

public class ImageFrame {
    private static void DrawMolecule(OEImageBase image, OEMolBase mol,
            double width, double height, OE2DPoint offset) {
        OEImageFrame frame = new OEImageFrame(image, width, height, offset);

        oedepict.OEDrawBorder(frame, oedepict.getOELightGreyPen());

        double scale = OEScale.AutoScale;
        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, scale);
        OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);
        boolean clearBackground = true;
        oedepict.OERenderMolecule(frame, disp, !clearBackground);
    }

    public static void main(String argv[]) {
        OEImage image = new OEImage(400, 400);

        OEGraphMol mol = new OEGraphMol();
        oechem.OESmilesToMol(mol, "C1CC(O)CNC1");
        oedepict.OEPrepareDepiction(mol);

        DrawMolecule(image, mol,  60,  60, new OE2DPoint(50.0,   40.0));
        DrawMolecule(image, mol, 180, 180, new OE2DPoint(180.0, 120.0));
        DrawMolecule(image, mol,  80,  80, new OE2DPoint(300.0,  20.0));
        DrawMolecule(image, mol,  50,  50, new OE2DPoint(150.0, 320.0));
        DrawMolecule(image, mol,  20,  20, new OE2DPoint(360.0, 360.0));

        oedepict.OEWriteImage("ImageFrame.png", image);
    }
}
