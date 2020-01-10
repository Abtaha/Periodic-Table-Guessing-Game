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
 * Performs substructure search on a molecule using a MDL query and
 * generates an image file depicting one match per molecule.
 * The output file format depends on its file extension.
 ****************************************************************************/
package openeye.examples.oedepict;

import java.net.URL;
import java.util.*;

import openeye.oechem.*;
import openeye.oedepict.*;

public class ViewMDLSearch {

    public static void main(String argv[]) {
        URL fileURL = ViewMDLSearch.class.getResource("ViewMDLSearch.txt");
        try {
            OEInterface itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);
            oedepict.OEConfigureReportOptions(itf);
            oedepict.OEConfigure2DMolDisplayOptions(itf);
            oedepict.OEConfigureHighlightParams(itf);

            if (!oechem.OEParseCommandLine(itf, argv, "ViewMDLSearch"))
                oechem.OEThrow.Fatal("Unable to interpret command line!");

            String qname = itf.GetString("-query");
            String tname = itf.GetString("-target");
            String oname = itf.GetString("-out");

            String ext = oechem.OEGetFileExtension(oname);
            if (!oedepict.OEIsRegisteredMultiPageImageFile(ext)) {
                oechem.OEThrow.Fatal("Unknown multipage image type!");
            }

            oemolistream qfile = new oemolistream();
            if (!qfile.open(qname)) {
                oechem.OEThrow.Fatal("Cannot open mdl query file!");
            }
            if (qfile.GetFormat() != OEFormat.MDL && qfile.GetFormat() != OEFormat.SDF) {
                oechem.OEThrow.Fatal("Query file has to be an MDL file!");
            }

            oemolistream ifs = new  oemolistream();
            if (!ifs.open(tname)) {
                oechem.OEThrow.Fatal("Cannot open target input file!");
            }

            OEGraphMol depictquery = new OEGraphMol();
            if (!oechem.OEReadMDLQueryFile(qfile, depictquery)) {
                oechem.OEThrow.Fatal("Cannot read query molecule!");
            }
            qfile.close();

            oedepict.OEPrepareDepiction(depictquery);
            int queryopts = OEMDLQueryOpts.Default | OEMDLQueryOpts.SuppressExplicitH;
            OEQMol qmol = new OEQMol();
            oechem.OEBuildMDLQueryExpressions(qmol, depictquery, queryopts);

            OESubSearch ss = new OESubSearch();
            if (!ss.Init(qmol)) {
                oechem.OEThrow.Fatal("Cannot initialize substructure search!");
            }

            int  hstyle = oedepict.OEGetHighlightStyle(itf);
            OEColor  hcolor = oedepict.OEGetHighlightColor(itf);
            boolean align  = itf.GetBool("-align");

            OEReportOptions ropts = new OEReportOptions();
            oedepict.OESetupReportOptions(ropts, itf);
            ropts.SetHeaderHeight(140.0);
            OEReport report = new OEReport(ropts);

            OE2DMolDisplayOptions dopts = new  OE2DMolDisplayOptions();
            oedepict.OESetup2DMolDisplayOptions(dopts, itf);
            dopts.SetDimensions(report.GetCellWidth(), report.GetCellHeight(), OEScale.AutoScale);

            boolean unique = true;
            OEGraphMol mol = new OEGraphMol();
            while (oechem.OEReadMolecule(ifs, mol)) {
                oechem.OEPrepareSearch(mol, ss);
                Iterator<OEMatchBase> miter = ss.Match(mol, unique);
                if (miter != null && miter.hasNext()) {
                    OEMatchBase match = miter.next();

                    OEAlignmentResult alignres = new OEAlignmentResult(match);
                    if (align)
                        alignres = oedepict.OEPrepareAlignedDepiction(mol, ss);
                    else
                        oedepict.OEPrepareDepiction(mol);

                    OEImageBase cell = report.NewCell();
                    OE2DMolDisplay disp = new OE2DMolDisplay(mol, dopts);
                    if (alignres.IsValid())
                        oedepict.OEAddHighlighting(disp, hcolor, hstyle, alignres);
                    oedepict.OERenderMolecule(cell, disp);
                    oedepict.OEDrawBorder(cell, oedepict.getOELightGreyPen());
                }
            }
            ifs.close();

            // render query structure in each header
            dopts.SetDimensions(report.GetHeaderWidth(), report.GetHeaderHeight(), OEScale.AutoScale);
            OE2DMolDisplay disp = new OE2DMolDisplay(depictquery, dopts);
            for (OEImageBase header : report.GetHeaders()) {
                oedepict.OERenderMolecule(header, disp);
                oedepict.OEDrawBorder(header, oedepict.getOELightGreyPen());
            }

            oedepict.OEWriteReport(oname, report);
        }
        catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }
}
