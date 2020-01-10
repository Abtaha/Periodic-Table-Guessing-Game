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

/**************************************************************
 * USED TO GENERATE CODE SNIPPETS FOR THE OEDEPICT DOCUMENTATION
 **************************************************************/

public class ImageGridIter {

    private static OEImageGrid CreateGrid(OEImageBase image) {
        int rows = 3;
        int cols = 3;
        OEImageGrid grid = new OEImageGrid(image, rows, cols);
        grid.SetCellGap(5);
        grid.SetMargins(0);

        for (OEImageBase cell : grid.GetCells())
            oedepict.OEDrawBorder(cell, oedepict.getOERedPen());

        return grid;
    }

    public static void main(String[] args) {
        OEPen pen = new OEPen(new OEColor(225, 200, 200),
                oechem.getOERed(), OEFill.On, 1.0);
        OEFont font = new OEFont();

        OEImage image = new OEImage(100, 100);
        image.Clear(oechem.getOELightGrey());
        OEImageGrid grid = CreateGrid(image);

        int i = 1;
        for (OEImageBase cell : grid.GetCells()) {
            oedepict.OEDrawBorder(cell, pen);
            OE2DPoint c = new OE2DPoint(cell.GetWidth()/2.0,
                    (cell.GetHeight()+font.GetSize())/2.0);
            cell.DrawText(c, String.format("(%d)", i), font);
            i += 1;
        }

        oedepict.OEWriteImage("ImageGridIter-all.png", image);
        oedepict.OEWriteImage("ImageGridIter-all.pdf", image);

        image.Clear(oechem.getOELightGrey());
        grid = CreateGrid(image);

        i = 1;
        int row = 2;
        for (OEImageBase cell : grid.GetCells(row, 0)) {
            oedepict.OEDrawBorder(cell, pen);
            OE2DPoint c = new OE2DPoint(cell.GetWidth()/2.0,
                    (cell.GetHeight()+font.GetSize())/2.0);
            cell.DrawText(c, String.format("(%d)", i), font);
            i += 1;
        }

        oedepict.OEWriteImage("ImageGridIter-row.png", image);
        oedepict.OEWriteImage("ImageGridIter-row.pdf", image);

        image.Clear(oechem.getOELightGrey());
        grid = CreateGrid(image);

        i = 1;
        int col = 2;
        for (OEImageBase cell : grid.GetCells(0, col)) {
            oedepict.OEDrawBorder(cell, pen);
            OE2DPoint c = new OE2DPoint(cell.GetWidth()/2.0,
                    (cell.GetHeight()+font.GetSize())/2.0);
            cell.DrawText(c, String.format("(%d)", i), font);
            i += 1;
        }

        oedepict.OEWriteImage("ImageGridIter-col.png", image);
        oedepict.OEWriteImage("ImageGridIter-col.pdf", image);
    }
}
