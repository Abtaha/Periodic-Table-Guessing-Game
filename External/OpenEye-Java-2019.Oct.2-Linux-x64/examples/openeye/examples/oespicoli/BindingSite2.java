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

public class BindingSite2
{
    private static final float _maxDist = 8.0f;

    public static void main(String [] args) {
        if (args.length != 3) {
            oechem.OEThrow.Usage("BindingSite2 <protein> <ligand> <surface>");
        }
        String proteinFile = args[0];
        String ligandFile = args[1];
        String surfaceFile = args[2];

        oemolistream pfs = new oemolistream(proteinFile);
        OEGraphMol prot = new OEGraphMol();
        oechem.OEReadMolecule(pfs, prot);
        pfs.close();
        oechem.OEAssignBondiVdWRadii(prot);

        oemolistream lfs = new oemolistream(ligandFile);
        OEGraphMol lig = new OEGraphMol();
        oechem.OEReadMolecule(lfs, lig);
        lfs.close();

        OESurface surf = new OESurface();
        oespicoli.OEMakeMolecularSurface(surf, prot);
        
        oespicoli.OESurfaceToMoleculeDistance(surf, lig);

        // define clique id
        int cliqueId = 1;

        // Mark the vertices to keep
        for (int i = 0;  i < surf.GetNumVertices(); i++) {
            if (surf.GetDistanceElement(i) < _maxDist)
                surf.SetVertexCliqueElement(i, cliqueId);
        }
            
        // Crop to the binding site and output
        oespicoli.OESurfaceCropToClique(surf, cliqueId);
        oespicoli.OEWriteSurface(surfaceFile, surf);
    }
}

