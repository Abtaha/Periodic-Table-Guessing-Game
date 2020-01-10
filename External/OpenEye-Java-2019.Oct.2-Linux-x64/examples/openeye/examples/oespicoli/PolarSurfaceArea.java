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

public class PolarSurfaceArea {

    private static void MakeCliques(OESurface surf, OEMolBase mol) {
        for (int i = 0; i < surf.GetNumVertices(); ++i) {
            OEAtomBase atom = mol.GetAtom(new OEHasAtomIdx(surf.GetAtomsElement(i)));
            if (atom.IsOxygen() || atom.IsNitrogen() || atom.IsPolarHydrogen()) {
                surf.SetVertexCliqueElement(i, 1);
                surf.SetColorElement(i,0.0f,0.0f,1.0f);
            } else   {
                surf.SetColorElement(i,1.0f,0.0f,0.0f);
            }
        }
    }
    private static class SurfArea {
        public float area;
        public float polararea;
    }

    private static void AverageSurfaceArea(OEMCMolBase mcmol, SurfArea sa) {
        sa.area=0.0f;
        sa.polararea=0.0f;
        OESurface surf = new OESurface();

        OEConfBaseIter confiter=mcmol.GetConfs();
        while (confiter.hasNext()) {
            OEConfBase conf = confiter.next();

            oespicoli.OEMakeMolecularSurface(surf, conf, 0.5f);
            MakeCliques(surf, conf);

            sa.area += oespicoli.OESurfaceArea(surf);
            sa.polararea += oespicoli.OESurfaceCliqueArea(surf, 1);
        }
        sa.area /= mcmol.NumConfs();
        sa.polararea /= mcmol.NumConfs();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("PolarSurfaceArea <infile>");
            System.exit(0);
        }

        oemolistream ifs = new oemolistream(args[0]);

        OEMol mol = new OEMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            oechem.OEAssignBondiVdWRadii(mol);
            SurfArea sa = new SurfArea();
            AverageSurfaceArea(mol, sa);

            System.out.println(mol.GetTitle());
            System.out.println("  molecule has " + mol.NumConfs() + " conformers");
            System.out.println("  Average total surface area: " + sa.area);
            System.out.println("  Average polar area        : " + sa.polararea);
            System.out.println("  Average % polar           : " + (sa.polararea*100.0/sa.area));
            System.out.println();
        }
        ifs.close();
    }
}
