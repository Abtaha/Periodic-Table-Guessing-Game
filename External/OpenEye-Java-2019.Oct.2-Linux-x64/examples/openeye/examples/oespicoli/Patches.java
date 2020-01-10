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

public class Patches { 

    private static boolean AtomInHydrophobicResidue(OEAtomBase atom) {
        switch(oechem.OEGetResidueIndex(atom)) {
            case OEResidueIndex.ALA:
            case OEResidueIndex.ILE:
            case OEResidueIndex.LEU:
            case OEResidueIndex.MET:
            case OEResidueIndex.PHE:
            case OEResidueIndex.PRO:
            case OEResidueIndex.TRP:
            case OEResidueIndex.VAL:
                return true;
        }
        return false;
    }

    public static void main(String[] args) {  
        if (args.length != 2)
            oechem.OEThrow.Usage("Patches <protein> <surface>");

        final String protFile = args[0];
        final String surfFile = args[1];

        oemolistream ifs = new oemolistream();
        if (!ifs.open(protFile))
            oechem.OEThrow.Fatal("Unable to open " + protFile + " for reading");
        

        OEGraphMol mol = new OEGraphMol();
        if (!oechem.OEReadMolecule(ifs, mol))
            oechem.OEThrow.Fatal("Invalid molecule file: " + protFile);
        ifs.close();

        oechem.OEPerceiveResidues(mol);
        oechem.OEAssignBondiVdWRadii(mol);

        // Generate the molecular surface
        OESurface surf = new OESurface();
        oespicoli.OEMakeMolecularSurface(surf, mol, 0.5f);

        // Mark all the vertices associated with hydrophobic atoms
        for (int i = 0; i < surf.GetNumVertices(); ++i) {
            OEAtomBase atom = mol.GetAtom(new OEHasAtomIdx(surf.GetAtomsElement(i)));

            if (AtomInHydrophobicResidue(atom))
                surf.SetVertexCliqueElement(i, 1);
        }

        // Crop to only those triangles
        oespicoli.OESurfaceCropToClique(surf, 1);

        // nlqs is the number of different connected components
        int nclqs = oespicoli.OEMakeConnectedSurfaceCliques(surf);

        // Find the largest component
        int maxclq = 0;
        double maxarea = 0.0;
        for (int i = 1; i <= nclqs; ++i) {
            double area = oespicoli.OESurfaceCliqueArea(surf, i);
            System.out.println("clique: " + i  + "  area: "  + area);
            if (area > maxarea) {
                maxclq = i;
                maxarea = area;
            }
        }

        // Crop to it
        oespicoli.OESurfaceCropToClique(surf, maxclq);
        oespicoli.OEWriteSurface(surfFile, surf);
    }
}

