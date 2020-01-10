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

public class AttachGaussianGrid {
    public static void main(String argv[]) {
        oemolistream ifs = new oemolistream(argv[0]);
        oemolostream ofs = new oemolostream(argv[1]);
        if (ofs.GetFormat() != OEFormat.OEB)
            oechem.OEThrow.Fatal("Can only write to .oeb files");

        OEGraphMol mol = new OEGraphMol();
        while (oechem.OEReadMolecule(ifs, mol)) {
            OEScalarGrid grid = new OEScalarGrid();
            oegrid.OEMakeMolecularGaussianGrid(grid, mol, 0.5f);

            oegrid.OESetGrid(mol, "Gaussian Grid", grid);

            oechem.OEWriteMolecule(ofs, mol);
        }
        ofs.close();
        ifs.close();
    }
}
//@ </SNIPPET>
