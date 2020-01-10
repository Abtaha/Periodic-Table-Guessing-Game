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
package openeye.examples.oegrid;

import java.net.URL;
import openeye.oechem.*;
import openeye.oegrid.*;

public class GridBabel {

    private void convert(OEInterface itf) {
        float res = itf.GetFloat("-res");

        boolean regularize = itf.GetBool("-regularize");

        String outfile = itf.GetString("-o");
        if (!oegrid.OEIsWriteableGrid(oegrid.OEGetGridFileType(oechem.OEGetFileExtension(outfile)))) {
            oechem.OEThrow.Fatal("Not a writeable grid file: " + outfile);
        }

        String infile = itf.GetString("-i");

        if (oegrid.OEIsReadableGrid(oegrid.OEGetGridFileType(oechem.OEGetFileExtension(infile)))) {
            if (regularize) {
                OEScalarGrid grid = new OEScalarGrid();
                if (oegrid.OEReadGrid(infile, grid)) {
                    oegrid.OEWriteGrid(outfile, grid);
                } else {
                    oechem.OEThrow.Fatal("Problem reading grid file: " + infile);
                }
                grid.delete();
            } else {
                OESkewGrid grid = new OESkewGrid();
                if (oegrid.OEReadGrid(infile, grid)) {
                    oegrid.OEWriteGrid(outfile, grid);
                } else {
                    oechem.OEThrow.Fatal("Problem reading grid file: " + infile);
                }
                grid.delete();
            }
        } else if (oechem.OEIsReadable(oechem.OEGetFileType(oechem.OEGetFileExtension(infile)))) {
            oemolistream ifs = new oemolistream(infile);
            OEMol mol = new OEMol();
            if (oechem.OEReadMolecule(ifs, mol)) {
                oechem.OEAssignBondiVdWRadii(mol);
                OEScalarGrid grid = new OEScalarGrid();
                oegrid.OEMakeMolecularGaussianGrid(grid, mol, res);
                oegrid.OEWriteGrid(outfile, grid);
                grid.delete();
            } else {
                oechem.OEThrow.Fatal("Problem reading molecule file: " + infile);
            }
        } else {
            oechem.OEThrow.Fatal("Not a readable grid file: " + infile);
        }

        oechem.OEThrow.Info("Converted " + infile + " into " + outfile);
    }

    public static void main(String argv[]) {
        GridBabel app = new GridBabel();
        URL fileURL = app.getClass().getResource("GridBabel.txt");
        try {
            OEInterface itf = new OEInterface(fileURL, "GridBabel", argv);
            app.convert(itf);
        } catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
