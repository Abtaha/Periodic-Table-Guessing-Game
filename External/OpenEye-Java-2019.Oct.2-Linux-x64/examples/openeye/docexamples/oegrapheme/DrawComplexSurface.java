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

public class DrawComplexSurface {

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
        oechem.OEAssignBondiVdWRadii(mol);
        oechem.OESuppressHydrogens(mol);

        return mol;
    }

    public static void main(String argv[]) {

        if (argv.length != 2) {
            oechem.OEThrow.Usage("DrawComplexSurface <receptor> <ligand>");
        }

        OEGraphMol receptor = importMolecule(argv[0]);

        OEGraphMol ligand = importMolecule(argv[1]);

        int width  = 450;
        int height = 350;

        oegrapheme.OEAddComplexSurfaceArcs(ligand, receptor);

        oegrapheme.OEPrepareDepictionFrom3D(ligand);
        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, OEScale.AutoScale);
        opts.SetScale(oegrapheme.OEGetMoleculeSurfaceScale(ligand, opts));

        OE2DMolDisplay disp = new OE2DMolDisplay(ligand, opts);
        oegrapheme.OEDraw2DSurface(disp);

        oedepict.OERenderMolecule("DrawComplexSurface.png", disp);
    }
}
