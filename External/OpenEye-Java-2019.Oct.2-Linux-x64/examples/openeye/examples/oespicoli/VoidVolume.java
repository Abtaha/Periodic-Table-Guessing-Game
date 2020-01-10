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
import openeye.oegrid.*;
import openeye.oespicoli.*;

public class VoidVolume { 
    public static void main(String[] args)
    {  
        if (args.length != 3)
            oechem.OEThrow.Usage("VoidVolume <protein> <ligand> <surface>");
        
        final String protFile = args[0];
        final String ligFile = args[1];
        final String surfFile= args[2];


        oemolistream prtfs = new oemolistream(protFile);
        OEGraphMol prt = new OEGraphMol();
        oechem.OEReadMolecule(prtfs, prt);
        oechem.OEAssignBondiVdWRadii(prt);
        prtfs.close();

        oemolistream ligfs = new oemolistream(ligFile);
        OEGraphMol lig = new OEGraphMol();
        oechem.OEReadMolecule(ligfs, lig);
        oechem.OEAssignBondiVdWRadii(lig);
        ligfs.close();

        OEScalarGrid grid = new OEScalarGrid();
        oespicoli.OEMakeVoidVolume(prt, lig, grid, 0.5f);

        OESurface surf = new OESurface();
        oespicoli.OEMakeSurfaceFromGrid(surf, grid, 0.5f);
        grid.delete();
        oespicoli.OEWriteSurface(surfFile, surf);
    }
}

