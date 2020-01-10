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
 * Depict a molecule and highlight the substructure specified by
 * the given SMARTS pattern
 ****************************************************************************/
package openeye.examples.oedepict;

import java.net.URL;
import java.util.*;

import openeye.oechem.*;
import openeye.oedepict.*;

public class Match2Img {

    public static void main(String argv[]) {
        URL fileURL = Match2Img.class.getResource("Match2Img.txt");
        try {
            OEInterface itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);
            oedepict.OEConfigureImageOptions(itf);
            oedepict.OEConfigurePrepareDepictionOptions(itf);
            oedepict.OEConfigure2DMolDisplayOptions(itf);
            oedepict.OEConfigureHighlightParams(itf);

            if (!oechem.OEParseCommandLine(itf, argv, "Match2Img")) {
                oechem.OEThrow.Fatal("Unable to interpret command line!");
            }

            String iname = itf.GetString("-in");
            String oname = itf.GetString("-out");

            String ext = oechem.OEGetFileExtension(oname);
            if (!oedepict.OEIsRegisteredImageFile(ext)) {
                oechem.OEThrow.Fatal("Unknown image type!");
            }

            oemolistream ifs = new oemolistream();
            if (!ifs.open(iname)) {
                oechem.OEThrow.Fatal("Cannot open input file!");
            }

            oeofstream ofs = new oeofstream();
            if (!ofs.open(oname)) {
                oechem.OEThrow.Fatal("Cannot open output file!");
            }

            OEGraphMol mol = new OEGraphMol();
            if (!oechem.OEReadMolecule(ifs, mol)) {
                oechem.OEThrow.Fatal("Cannot read input file!");
            }
            ifs.close();

            String  smarts = itf.GetString("-smarts");

            OESubSearch ss = new OESubSearch();
            if (!ss.Init(smarts)) {
                oechem.OEThrow.Fatal("Cannot parse smarts: " + smarts);
            }

            OEPrepareDepictionOptions popts = new OEPrepareDepictionOptions();
            oedepict.OESetupPrepareDepictionOptions(popts, itf);
            oedepict.OEPrepareDepiction(mol, popts);

            double width  = oedepict.OEGetImageWidth (itf);
            double height = oedepict.OEGetImageHeight(itf);
            OE2DMolDisplayOptions dopts = new OE2DMolDisplayOptions(width, height, OEScale.AutoScale);
            oedepict.OESetup2DMolDisplayOptions(dopts, itf);
            dopts.SetMargins(10.0);

            OE2DMolDisplay disp = new OE2DMolDisplay(mol, dopts);

            int     hstyle = oedepict.OEGetHighlightStyle(itf);
            OEColor hcolor = oedepict.OEGetHighlightColor(itf);

            oechem.OEPrepareSearch(mol, ss);

            boolean unique = true;
            for (OEMatchBase match : ss.Match(mol, unique)) {
                oedepict.OEAddHighlighting(disp, hcolor, hstyle, match);
            }

            oedepict.OERenderMolecule(ofs, ext, disp);
            ofs.close();
        }
        catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
