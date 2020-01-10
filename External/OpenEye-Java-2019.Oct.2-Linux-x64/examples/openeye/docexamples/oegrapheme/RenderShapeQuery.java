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

public class RenderShapeQuery {
    public static void main(String argv[]) {
        if (argv.length != 1) {
            oechem.OEThrow.Usage("RenderShapeQuery <molecule file>");
        }

        oemolistream ifs = new oemolistream(argv[0]);
        OEGraphMol refmol = new OEGraphMol();
        oechem.OEReadMolecule(ifs, refmol);
        ifs.close();

        OEShapeQueryDisplayOptions opts = new OEShapeQueryDisplayOptions();
        opts.SetTitleLocation(OETitleLocation.Hidden);
        OEPen arcpen = new OEPen(oechem.getOEWhite(), oechem.getOELightGrey(), OEFill.On, 2.0);
        opts.SetSurfaceArcFxn(new OEDefaultArcFxn(arcpen));

        OEColorForceField cff = new OEColorForceField();
        cff.Init(OEColorFFType.ImplicitMillsDean);
        OEShapeQueryDisplay qdisp = new OEShapeQueryDisplay(refmol, cff, opts);

        OEImage image = new OEImage(420.0, 280.0);
        oegrapheme.OERenderShapeQuery(image, qdisp);
        oedepict.OEWriteImage("RenderShapeQuery.png", image);
    }
}
