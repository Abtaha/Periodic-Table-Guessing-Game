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
package openeye.docexamples.oedepict;

import java.util.ArrayList;
import openeye.oechem.*;
import openeye.oedepict.*;

public class MultiPageReport {

    public static void main(String argv[]) {
        ArrayList<String> smiles = new ArrayList<String>();
        smiles.add("C1CC(C)CCC1");
        smiles.add("C1CC(O)CCC1");
        smiles.add("C1CC(Cl)CCC1");
        smiles.add("C1CC(F)CCC1");
        smiles.add("C1CC(Br)CCC1");
        smiles.add("C1CC(N)CCC1");

        int rows = 2;
        int cols = 2;
        OEReportOptions reportopts = new OEReportOptions(rows, cols);
        reportopts.SetPageOrientation(OEPageOrientation.Landscape);
        reportopts.SetCellGap(20);
        reportopts.SetPageMargins(20);
        OEReport report = new OEReport(reportopts);

        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(report.GetCellWidth(), report.GetCellHeight(), OEScale.AutoScale);

        for (String smi : smiles) {
            OEGraphMol mol = new OEGraphMol();
            oechem.OESmilesToMol(mol, smi);
            oedepict.OEPrepareDepiction(mol);

            OEImageBase cell = report.NewCell();
            OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);
            oedepict.OERenderMolecule(cell, disp);
            oedepict.OEDrawBorder(cell, oedepict.getOERedPen());
        }

        oedepict.OEWriteReport("MultiPageReport.pdf", report);
    }
}
