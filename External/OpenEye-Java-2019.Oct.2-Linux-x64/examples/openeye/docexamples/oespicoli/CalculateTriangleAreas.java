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
package openeye.docexamples.oespicoli;

import openeye.oechem.*;
import openeye.oespicoli.*;

public class CalculateTriangleAreas {
    public static void main(String argv[]) {
        if (argv.length != 1)
            oechem.OEThrow.Usage("CalculateTriangleAreas <input>");

        oemolistream ims = new oemolistream();
        if (!ims.open(argv[0]))
            oechem.OEThrow.Fatal("Unable to open " + argv[0]);

        OEGraphMol mol = new OEGraphMol();
        if (!oechem.OEReadMolecule(ims, mol))
            oechem.OEThrow.Fatal("Unable to read a molecule");
        ims.close();
        oechem.OEAssignBondiVdWRadii(mol);

        OESurface surf = new OESurface();
        oespicoli.OEMakeMolecularSurface(surf, mol);

        OEFloatArray areas = new OEFloatArray(surf.GetNumTriangles());
        oespicoli.OECalculateTriangleAreas(surf, areas);

        float[] atomareas = new float[mol.GetMaxAtomIdx()];
        OEUIntArray tri = new OEUIntArray(3);
        for (int i=0; i < surf.GetNumTriangles(); i++)
        {
            surf.GetTriangle(i, tri);

            int a1 = surf.GetAtomsElement(tri.getItem(0));
            int a2 = surf.GetAtomsElement(tri.getItem(1));
            int a3 = surf.GetAtomsElement(tri.getItem(2));

            atomareas[a1] += areas.getItem(i)/3.0;
            atomareas[a2] += areas.getItem(i)/3.0;
            atomareas[a3] += areas.getItem(i)/3.0;
        }

        for (OEAtomBase atom : mol.GetAtoms())
            System.out.printf("atom %d area = %2.4f\n", atom.GetIdx(), atomareas[atom.GetIdx()]);
    }
}
