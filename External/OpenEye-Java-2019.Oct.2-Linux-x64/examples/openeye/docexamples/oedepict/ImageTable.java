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

public class ImageTable {

    public static void main(String[] args) {

        ArrayList<String> smiles = new ArrayList<String>();
        smiles.add("c1ccccc1 benzene");
        smiles.add("c1cccnc1 pyridine");
        smiles.add("c1cncnc1 pyrimidine");

        int imagewidth = 350;
        int imageheight = 250;
        OEImage image = new OEImage(imagewidth, imageheight);

        int mainrows = 3;
        int maincols = 2;
        OEImageTableOptions maintableopts = new OEImageTableOptions(mainrows, maincols, OEImageTableStyle.NoStyle);
        maintableopts.SetHeader(false);
        ArrayList<Integer> colwidths = new ArrayList<Integer>();
        colwidths.add(10);
        colwidths.add(20);
        maintableopts.SetColumnWidths(colwidths);
        OEImageTable maintable = new OEImageTable(image, maintableopts);

        int datarows = 4;
        int datacols = 2;
        OEImageTableOptions datatableopts = new OEImageTableOptions(datarows, datacols, OEImageTableStyle.LightGreen);
        datatableopts.SetStubColumn(true);
        datatableopts.SetMargins(5.0);
        datatableopts.SetColumnWidths(colwidths);

        for (int r = 0; r < mainrows; ++r) {
            // depict molecule in first column
            OEImageBase cell = maintable.GetBodyCell(r + 1, 1);
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(cell.GetWidth(), cell.GetHeight(), OEScale.AutoScale);
            opts.SetTitleLocation(OETitleLocation.Hidden);

            OEGraphMol mol = new OEGraphMol();
            oechem.OESmilesToMol(mol, smiles.get(r));
            oedepict.OEPrepareDepiction(mol);

            OE2DMolDisplay disp = new OE2DMolDisplay(mol, opts);
            oedepict.OERenderMolecule(cell, disp);

            // depicting data in table
            cell = maintable.GetBodyCell(r + 1, 2);
            OEImageTable datatable = new OEImageTable(cell, datatableopts);

            datatable.DrawText(datatable.GetHeaderCell(1, false), "Property");
            datatable.DrawText(datatable.GetHeaderCell(1), "Value");

            datatable.DrawText(datatable.GetStubColumnCell(1), "Name");
            datatable.DrawText(datatable.GetBodyCell(1, 1), mol.GetTitle());

            datatable.DrawText(datatable.GetStubColumnCell(2), "SMILES");
            datatable.DrawText(datatable.GetBodyCell(2, 1), oechem.OEMolToSmiles(mol));

            datatable.DrawText(datatable.GetStubColumnCell(3), "MV");
            String mwdata = String.format("%.3f", oechem.OECalculateMolecularWeight(mol));
            datatable.DrawText(datatable.GetBodyCell(3, 1), mwdata);
        }
        oedepict.OEWriteImage("ImageTable.png", image);
    }
}
