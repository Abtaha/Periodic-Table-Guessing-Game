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
 * Converts molecules into a multi-page PDF document.
 ****************************************************************************/
package openeye.examples.oedepict;

import java.net.URL;
import java.util.*;

import openeye.oechem.*;
import openeye.oedepict.*;

public class Mols2PDF {

    public static void main(String argv[]) {
        URL fileURL = Mols2PDF.class.getResource("Mols2PDF.txt");
        try {
            OEInterface itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);
            oedepict.OEConfigureReportOptions(itf);
            oedepict.OEConfigurePrepareDepictionOptions(itf);
            oedepict.OEConfigure2DMolDisplayOptions(itf);

            if (!oechem.OEParseCommandLine(itf, argv, "Mols2PDF")) {
                return;
            }

            String iname = itf.GetString("-in");
            oemolistream ifs = new  oemolistream();
            if (!ifs.open(iname)) {
                oechem.OEThrow.Fatal("Cannot open" + iname + " input file!");
            }

            String oname = itf.GetString("-out");
            String ext = oechem.OEGetFileExtension(oname);
            if (!ext.equals("pdf")) {
                oechem.OEThrow.Fatal("Output must be PDF format.");
            }

            oeofstream ofs = new oeofstream();
            if (!ofs.open(oname)) {
                oechem.OEThrow.Fatal("Cannot open " + oname + " output file!");
            }

            if (itf.HasString("-ringdict")) {
                String rdfname = itf.GetString("-ringdict");
                if (!oechem.OEInit2DRingDictionary(rdfname))
                    oechem.OEThrow.Warning("Cannot use user-defined ring dictionary!");
            }

            OEReportOptions ropts = new OEReportOptions();
            oedepict.OESetupReportOptions(ropts, itf);
            ropts.SetFooterHeight(25.0);
            OEReport report = new OEReport(ropts);

            OEPrepareDepictionOptions popts = new OEPrepareDepictionOptions();
            oedepict.OESetupPrepareDepictionOptions(popts, itf);

            OE2DMolDisplayOptions dopts = new OE2DMolDisplayOptions();
            oedepict.OESetup2DMolDisplayOptions(dopts, itf);
            dopts.SetDimensions(report.GetCellWidth(), report.GetCellHeight(), OEScale.AutoScale);

            OEGraphMol mol = new OEGraphMol();
            while (oechem.OEReadMolecule(ifs, mol)) {
                OEImageBase cell = report.NewCell();
                oedepict.OEPrepareDepiction(mol, popts);
                OE2DMolDisplay disp = new OE2DMolDisplay(mol, dopts);
                oedepict.OERenderMolecule(cell, disp);
            }
            ifs.close();

            int pagenum = 1;
            OEFont font = new OEFont(OEFontFamily.Default, OEFontStyle.Bold, 12, OEAlignment.Center, oechem.getOEBlack());
            for (OEImageBase footer : report.GetFooters()) {
                String text = "Page " + Integer.toString(pagenum) + " of " + Integer.toString(report.NumPages());
                oedepict.OEDrawTextToCenter(footer, text, font);
                pagenum += 1;
            }
            oedepict.OEWriteReport(ofs, ext, report);
            ofs.close();

        }
        catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
