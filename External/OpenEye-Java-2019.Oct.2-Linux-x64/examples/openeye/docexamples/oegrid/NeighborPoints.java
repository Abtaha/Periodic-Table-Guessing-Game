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
//@ <SNIPPET>
package openeye.docexamples.oegrid;

import openeye.oechem.*;
import openeye.oegrid.*;

public class NeighborPoints {
    public static void main(String argv[]) {
        oemolistream ifs = new oemolistream(argv[0]);
        OEGraphMol mol = new OEGraphMol();
        oechem.OEReadMolecule(ifs, mol);
        ifs.close();

        OEScalarGrid grid = new OEScalarGrid();
        oegrid.OEMakeMolecularGaussianGrid(grid, mol, 0.5f);

        for (OEAtomBase atom : mol.GetAtoms()) {
            float xyz[] = new float[3];
            mol.GetCoords(atom, xyz);

            int ixyz[] = new int[3];
            grid.SpatialCoordToGridIdx(xyz[0], xyz[1], xyz[2], ixyz);

            // Make sure not to go past grid bounds
            int mini = Math.max(ixyz[0] - 1, 0);
            int minj = Math.max(ixyz[1] - 1, 0);
            int mink = Math.max(ixyz[2] - 1, 0);
            int maxi = Math.min(ixyz[0] + 1, grid.GetXDim());
            int maxj = Math.min(ixyz[1] + 1, grid.GetYDim());
            int maxk = Math.min(ixyz[2] + 1, grid.GetZDim());

            for (int k = mink; k < maxk; k++)
                for (int j = minj; j < maxj; j++)
                    for (int i = mini; i < maxi; i++)
                        System.out.printf("value = %.5f\n", 
                                grid.GetValue(i, j, k));

        } 
    }
}
//@ </SNIPPET>
