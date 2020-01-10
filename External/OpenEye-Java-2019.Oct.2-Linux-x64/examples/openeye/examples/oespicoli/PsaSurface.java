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
package openeye.examples.oespicoli;

import openeye.oechem.*;
import openeye.oespicoli.*;

public class PsaSurface
{
    private static void ColorSurface(OESurface surf, OEMolBase mol) {
        for (int i=0; i < surf.GetNumVertices(); ++i) {
            OEAtomBase atom = mol.GetAtom(new OEHasAtomIdx(surf.GetAtomsElement(i)));
            if ((atom.IsOxygen() || atom.IsNitrogen() || atom.IsPolarHydrogen()))
              surf.SetColorElement(i, 0.0f, 0.0f, 1.0f);
            else
              surf.SetColorElement(i, 1.0f, 0.0f, 0.0f);
        }
    }

    public static void main(String[] args) {
        if (args.length != 2)
            oechem.OEThrow.Usage("PsaSurface <molecules> <oebfile>");

        String inputmols  = args[0];
        String outfile = args[1];

        oemolistream ifs = new oemolistream();
        if (!ifs.open(inputmols))
            oechem.OEThrow.Fatal("Unable to open " + inputmols + " for reading");

        oemolostream ofs = new oemolostream();
        if (!ofs.open(outfile))
            oechem.OEThrow.Fatal("Unable to open " + outfile + " for writing");

        OEGraphMol mol = new OEGraphMol();
        while(oechem.OEReadMolecule(ifs, mol)) {
            oechem.OEAssignBondiVdWRadii(mol);

            OESurface surf = new OESurface();
            oespicoli.OEMakeMolecularSurface(surf, mol, 0.5f);
            ColorSurface(surf, mol);
            oespicoli.OESetSurface(mol, "psasurf", surf);

            oechem.OEWriteMolecule(ofs, mol);
        }
        ifs.close();
        ofs.close();
    }
}
