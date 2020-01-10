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
import openeye.oegrid.*;

public class Overlap {

    public static void main(String[] args) {  
        if (args.length != 2)
            oechem.OEThrow.Usage("Overlap <ref> <fit>");
        final String refFile = args[0];
        final String fitFile = args[1];

        oemolistream refifs = new oemolistream();
        if (!refifs.open(refFile))
            oechem.OEThrow.Fatal("Unable to open " + refFile + " for reading");

        OEGraphMol refmol = new OEGraphMol();
        oechem.OEReadMolecule(refifs, refmol);
        oechem.OEAssignBondiVdWRadii(refmol);
        refifs.close();

        oemolistream fitifs = new oemolistream();
        if (!fitifs.open(fitFile))
            oechem.OEThrow.Fatal("Unable to open " + fitFile + " for reading");

        OEGraphMol fitmol = new OEGraphMol();
        oechem.OEReadMolecule(fitifs, fitmol);
        oechem.OEAssignBondiVdWRadii(fitmol);
        fitifs.close();

        // Map the reference molecule onto a grid
        OEScalarGrid grid = new OEScalarGrid();
        oegrid.OEMakeMolecularGaussianGrid(grid, refmol, 0.5f);

        // Get the total volume of the reference molecule
        OESurface refsrf = new OESurface();
        oespicoli.OEMakeSurfaceFromGrid(refsrf, grid, 1.0f);
        float totalv = oespicoli.OESurfaceVolume(refsrf);

        // Mask out the fit molecule
        oegrid.OEMaskGridByMolecule(grid, fitmol);

        // Find how much of the reference volume is remaining
        OESurface fitsrf = new OESurface();
        oespicoli.OEMakeSurfaceFromGrid(fitsrf, grid, 1.0f);
        grid.delete();
        float remaining = oespicoli.OESurfaceVolume(fitsrf);

        System.out.println(String.format("Percent overlap: %f.4",  (1.0f - (remaining/totalv)) * 100.0f)); 
    }
}
