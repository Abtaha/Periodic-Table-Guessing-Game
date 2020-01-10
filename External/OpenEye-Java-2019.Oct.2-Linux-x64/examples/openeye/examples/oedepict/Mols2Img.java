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
/****************************************************************************
 * Converts molecules into an image with grid layout.
 * The output file format depends on its file extension.
 ****************************************************************************/
package openeye.examples.oedepict;

import java.net.URL;
import java.util.*;

import openeye.oechem.*;
import openeye.oedepict.*;

public class Mols2Img {

    public static void main(String argv[]) {
        URL fileURL = Mols2Img.class.getResource("Mols2Img.txt");
        try {
            OEInterface itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);
            oedepict.OEConfigureImageWidth (itf, 400.0);
            oedepict.OEConfigureImageHeight(itf, 400.0);
            oedepict.OEConfigureImageGridParams(itf);
            oedepict.OEConfigurePrepareDepictionOptions(itf);
            oedepict.OEConfigure2DMolDisplayOptions(itf);

            if (!oechem.OEParseCommandLine(itf, argv, "Mols2Img")) {
                oechem.OEThrow.Fatal("Unable to interpret command line!");
            }

            String oname = itf.GetString("-out");
            String ext = oechem.OEGetFileExtension(oname);
            if (!oedepict.OEIsRegisteredImageFile(ext)) {
                oechem.OEThrow.Fatal("Unknown image type!");
            }

            oeofstream ofs = new oeofstream();
            if (!ofs.open(oname)) {
                oechem.OEThrow.Fatal("Cannot open output file!");
            }

            double width  = oedepict.OEGetImageWidth (itf);
            double height = oedepict.OEGetImageHeight(itf);
            OEImage image = new OEImage(width, height);

            int rows = oedepict.OEGetImageGridNumRows(itf);
            int cols = oedepict.OEGetImageGridNumColumns(itf);
            OEImageGrid grid = new OEImageGrid(image, rows, cols);

            OEPrepareDepictionOptions popts = new OEPrepareDepictionOptions();
            oedepict.OESetupPrepareDepictionOptions(popts, itf);

            OE2DMolDisplayOptions dopts = new OE2DMolDisplayOptions();
            oedepict.OESetup2DMolDisplayOptions(dopts, itf);
            dopts.SetDimensions(grid.GetCellWidth(), grid.GetCellHeight(), OEScale.AutoScale);

            OEImageBaseIter celliter = grid.GetCells();
            for (String iname : itf.GetStringList("-in")) {
                oemolistream ifs = new oemolistream();
                if (!ifs.open(iname)) {
                    oechem.OEThrow.Warning("Cannot open " + iname + " input file!");
                    continue;
                }

                OEGraphMol mol = new OEGraphMol();
                while (oechem.OEReadMolecule(ifs, mol) && celliter.IsValid()) {
                    oedepict.OEPrepareDepiction(mol, popts);
                    OE2DMolDisplay disp = new OE2DMolDisplay(mol, dopts);
                    oedepict.OERenderMolecule(celliter.Target(), disp);
                    celliter.Increment();
                }
                ifs.close();
            }
            oedepict.OEWriteImage(ofs, ext, image);
            ofs.close();
        }
        catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
