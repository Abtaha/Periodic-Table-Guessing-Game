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

import openeye.oechem.*;
import openeye.oedepict.*;
import java.util.ArrayList;

/**************************************************************
 * USED TO GENERATE CODE SNIPPETS FOR THE OEDEPICT DOCUMENTATION
 **************************************************************/

public class OEImageTableOpts_SetProps {

    private static void writeTable(OEImageTableOptions tableopts, String basefilename) {

        OEImage image = new OEImage(300, 200);
        OEImageTable table = new OEImageTable(image, tableopts);

        int idx = 1;
        for (OEImageBase cell : table.GetHeaderCells()) {
            table.DrawText(cell, String.format("(header %d)", idx));
            ++idx;
        }

        idx = 1;
        for (OEImageBase cell : table.GetStubColumnCells()) {
            table.DrawText(cell, String.format("(stub %d)", idx));
             ++idx;
        }
        boolean onlybody = true;
        for (int row = 1; row <= table.NumRows(onlybody); ++row) {
            for (int col = 1; col <= table.NumColumns(onlybody); ++col) {
                OEImageBase cell = table.GetBodyCell(row, col);
                table.DrawText(cell,  String.format("(body %d, %d)", row, col));
            }
        }

        oedepict.OEDrawBorder(image, oedepict.getOELightGreyPen());
        oedepict.OEWriteImage(basefilename + ".png", image);
    }

    public static void main(String argv[]) {
        {
            OEImageTableOptions tableopts = new OEImageTableOptions(4, 4, OEImageTableStyle.MediumBlue);
            writeTable(tableopts, "OEImageTableOptions_Default");
        }

        {
            OEImageTableOptions tableopts = new OEImageTableOptions(4, 4, OEImageTableStyle.MediumBlue);
            ArrayList<Integer> widths = new ArrayList<Integer>();
            widths.add(20);
            widths.add(10);
            widths.add(20);
            widths.add(10);
            tableopts.SetColumnWidths(widths);
            writeTable(tableopts, "OEImageTableOptions_SetColumnWidths");
        }

        {
            OEImageTableOptions tableopts = new OEImageTableOptions(4, 4, OEImageTableStyle.MediumBlue);
            ArrayList<Integer> heights = new ArrayList<Integer>();
            heights.add(10);
            heights.add(20);
            heights.add(30);
            heights.add(40);
            tableopts.SetRowHeights(heights);
            writeTable(tableopts, "OEImageTableOptions_SetRowHeights");
        }

        {
            OEImageTableOptions tableopts = new OEImageTableOptions(4, 4, OEImageTableStyle.MediumBlue);
            tableopts.SetBaseFontSize(6);
            writeTable(tableopts, "OEImageTableOptions_SetBaseFontSize");
        }

        {
            OEImageTableOptions tableopts = new OEImageTableOptions(4, 4, OEImageTableStyle.MediumBlue);
            OEPen pen = new OEPen(oechem.getOEBlack(), oechem.getOEDarkBlue(), OEFill.Off, 2.0);
            tableopts.SetCellBorderPen(pen);
            writeTable(tableopts, "OEImageTableOptions_SetCellBorderPen");
        }

        {
            OEImageTableOptions tableopts = new OEImageTableOptions(4, 4, OEImageTableStyle.MediumBlue);
            boolean even = true;
            tableopts.SetCellColor(oechem.getOELightGrey(), !even);
            writeTable(tableopts, "OEImageTableOptions_SetCellColor");
        }

        {
            OEImageTableOptions tableopts = new OEImageTableOptions(4, 4, OEImageTableStyle.MediumBlue);
            OEFont font = new OEFont(OEFontFamily.Default, OEFontStyle.Italic | OEFontStyle.Bold,
                                     8, OEAlignment.Left, oechem.getOEDarkRed());
            tableopts.SetCellFont(font);
            writeTable(tableopts, "OEImageTableOptions_SetCellFont");
        }

        {
            OEImageTableOptions tableopts = new OEImageTableOptions(4, 4, OEImageTableStyle.MediumBlue);
            tableopts.SetHeader(false);
            writeTable(tableopts, "OEImageTableOptions_SetHeader");
        }

        {
            OEImageTableOptions tableopts = new OEImageTableOptions(4, 4, OEImageTableStyle.MediumBlue);
            tableopts.SetHeaderColor(oechem.getOELightGrey());
            writeTable(tableopts, "OEImageTableOptions_SetHeaderColor");
        }

        {
            OEImageTableOptions tableopts = new OEImageTableOptions(4, 4, OEImageTableStyle.MediumBlue);
            OEFont font = new OEFont(OEFontFamily.Default, OEFontStyle.Default,
                                     12, OEAlignment.Right, oechem.getOEPinkTint());
            tableopts.SetHeaderFont(font);
            writeTable(tableopts, "OEImageTableOptions_SetHeaderFont");
        }

        {
            OEImageTableOptions tableopts = new OEImageTableOptions(4, 4, OEImageTableStyle.MediumBlue);
            tableopts.SetMargin(OEMargin.Left, 10.0);
            tableopts.SetMargin(OEMargin.Right, 10.0);
            writeTable(tableopts, "OEImageTableOptions_SetMargin");
        }

        {
            OEImageTableOptions tableopts = new OEImageTableOptions(4, 4, OEImageTableStyle.MediumBlue);
            tableopts.SetMargins(10.0);
            writeTable(tableopts, "OEImageTableOptions_SetMargins");
        }

        {
            OEImageTableOptions tableopts = new OEImageTableOptions(4, 4, OEImageTableStyle.MediumBlue);
            tableopts.SetStubColumn(true);
            writeTable(tableopts, "OEImageTableOptions_SetStubColumn");
        }

        {
            OEImageTableOptions tableopts = new OEImageTableOptions(4, 4, OEImageTableStyle.MediumBlue);
            tableopts.SetStubColumn(true);
            tableopts.SetStubColumnColor(oechem.getOELightGrey());
            writeTable(tableopts, "OEImageTableOptions_SetStubColumnColor");
        }

        {
            OEImageTableOptions tableopts = new OEImageTableOptions(4, 4, OEImageTableStyle.MediumBlue);
            tableopts.SetStubColumn(true);
            OEFont font = new OEFont(OEFontFamily.Default, OEFontStyle.Default,
                                     12, OEAlignment.Right, oechem.getOEPinkTint());
            tableopts.SetStubColumnFont(font);
            writeTable(tableopts, "OEImageTableOptions_SetStubColumnFont");
        }
    }
}
