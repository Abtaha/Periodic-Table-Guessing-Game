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
import openeye.oeshape.*;

public class RenderShapeOverlap {
    public static void main(String argv[]) {
        if (argv.length != 2) {
            oechem.OEThrow.Usage("RenderShapeOverlap <refmol file> <fitmol file>");
        }

        oemolistream rifs = new oemolistream(argv[0]);
        OEGraphMol refmol= new OEGraphMol();
        oechem.OEReadMolecule(rifs, refmol);
        rifs.close();

        oemolistream fifs = new oemolistream(argv[1]);
        OEGraphMol fitmol = new OEGraphMol();
        oechem.OEReadMolecule(fifs, fitmol);
        fifs.close();

        OEColorForceField cff = new OEColorForceField();
        cff.Init(OEColorFFType.ImplicitMillsDean);
        OEShapeQueryDisplay qdisp = new OEShapeQueryDisplay(refmol, cff);

        OEShapeOverlapDisplayOptions opts = new OEShapeOverlapDisplayOptions();
        opts.SetTitleLocation(OETitleLocation.Hidden);
        OEPen arcpen = new OEPen(oechem.getOEGrey(), oechem.getOEGrey(), OEFill.Off, 2.0, OEStipple.ShortDash);
        opts.SetQuerySurfaceArcFxn(new OEDefaultArcFxn(arcpen));
        OEShapeOverlapDisplay odisp = new OEShapeOverlapDisplay(qdisp, fitmol, opts);

        OEImage image = new OEImage(420.0, 280.0);
        oegrapheme.OERenderShapeOverlap(image, odisp);
        oedepict.OEWriteImage("RenderShapeOverlap.png", image);
    }
}
