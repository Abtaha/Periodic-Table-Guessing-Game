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

public class PrepareAlignedDepictFrom3D {

    public static OEGraphMol importMolecule (String filename) {

        oemolistream ifs = new oemolistream();
        OEGraphMol mol = new OEGraphMol();
        if (!ifs.open(filename)) {
            oechem.OEThrow.Fatal("Unable to open " + filename + " for reading");
        }

        if (!oechem.OEReadMolecule(ifs, mol)) {
            oechem.OEThrow.Fatal("Unable to read molecule in " + filename);
        }
        ifs.close();
        return mol;
    }

    public static void main(String argv[]) {

        if (argv.length != 2) {
            oechem.OEThrow.Usage("PrepareAlignedDepictionFrom3D <ref> <fit>");
        }

        OEGraphMol refmol3D = importMolecule(argv[0]);
        OEGraphMol fitmol3D = importMolecule(argv[1]);

        int width  = 300;
        int height = 300;
        OEGraphMol refmol2D = new OEGraphMol(refmol3D);
        oegrapheme.OEPrepareDepictionFrom3D(refmol2D);

        OEGraphMol fitmol2D = new OEGraphMol(fitmol3D);
        oegrapheme.OEPrepareAlignedDepictionFrom3D(fitmol2D, fitmol3D, refmol2D, refmol3D);

        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, OEScale.AutoScale);
        double refscale = oedepict.OEGetMoleculeScale(refmol2D, opts);
        double fitscale = oedepict.OEGetMoleculeScale(fitmol2D, opts);
        opts.SetScale(Math.min(refscale, fitscale));

        OE2DMolDisplay refdisp = new OE2DMolDisplay(refmol2D, opts);
        oedepict.OERenderMolecule("OEPrepareAlignedDepictionFrom3D-ref-2D.png", refdisp);

        OE2DMolDisplay fitdisp = new OE2DMolDisplay(fitmol2D, opts);
        oedepict.OERenderMolecule("OEPrepareAlignedDepictionFrom3D-fit-2D.png", fitdisp);

    }
}
