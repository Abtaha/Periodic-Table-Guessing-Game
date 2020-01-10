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

public class OEReportIter {
    private static OEReport createReportLayout() {

        int nrrows = 3;
        int nrcols = 2;

        OEReportOptions reportopts = new OEReportOptions(nrrows, nrcols);
        reportopts.SetPageWidth (100.0);
        reportopts.SetPageHeight(150.0);
        reportopts.SetPageMargins(5.0);
        reportopts.SetCellGap(5.0);
        reportopts.SetHeaderHeight(15.0);
        reportopts.SetFooterHeight(15.0);
        OEReport report = new OEReport(reportopts);

        OEPen borderpen = new OEPen(oechem.getOELightGrey(), oechem.getOELightGrey(), OEFill.Off, 1.0);

        OEImageBase first = report.NewBody();
        oedepict.OEDrawBorder(first, borderpen);

        int nrgridpages = 3;
        for (int p = 0; p < nrgridpages; ++p)
        {
            for (int c = 0; c < report.NumRowsPerPage() * report.NumColsPerPage(); ++c)
            {
                OEImageBase cell = report.NewCell();
                oedepict.OEDrawBorder(cell, borderpen);
            }
        }
        OEImageBase last = report.NewBody();
        oedepict.OEDrawBorder(last, borderpen);

        for (OEImageBase page : report.GetPages()) {
            oedepict.OEDrawBorder(page, oedepict.getOEBlackPen());
        }
        for (OEImageBase header : report.GetHeaders()) {
            oedepict.OEDrawBorder(header, borderpen);
        }
        for (OEImageBase footer : report.GetFooters()) {
            oedepict.OEDrawBorder(footer, borderpen);
        }
        return report;
    }

    private static void highlightCell(OEImageBase cell, int idx) {

        OEFont font = new OEFont(OEFontFamily.Default, OEFontStyle.Default, 10, OEAlignment.Center, oechem.getOEBlack());
        OEPen borderpen = new OEPen(oechem.getOELightGrey(), oechem.getOELightGrey(), OEFill.On, 1.0);

        oedepict.OEDrawBorder(cell, borderpen);
        OE2DPoint p = new OE2DPoint(cell.GetWidth()/2.0, cell.GetHeight()/2.0 + font.GetSize()/2.0);
        cell.DrawText(p, "(" + Integer.toString(idx) + ")", font);
    }

    private static void writeReportPageByPage(OEReport report, String filenamebase) {
        for (int pidx = 1; pidx <= report.NumPages(); ++pidx) {
            oedepict.OEWriteReport(filenamebase + "-" + Integer.toString(pidx) + ".pdf", report, pidx);
            oedepict.OEWriteReport(filenamebase + "-" + Integer.toString(pidx) + ".png", report, pidx);
        }
    }

    public static void main(String argv[]) {

        {
            OEReport report = createReportLayout();
            int idx = 0;
            for (OEImageBase cell : report.GetCells())
            {
                highlightCell(cell, ++idx);
            }
            writeReportPageByPage(report, "OEReportIter-cells-all");
        }

        {
            OEReport report = createReportLayout();
            int idx = 0;
            int row = 2;
            for (OEImageBase cell : report.GetCells(0, row, 0))
            {
                highlightCell(cell, ++idx);
            }
            writeReportPageByPage(report, "OEReportIter-cells-allpages-row-allcols");
        }

        {
            OEReport report = createReportLayout();
            int idx = 0;
            int col = 2;
            for (OEImageBase cell : report.GetCells(0, 0, col))
            {
                highlightCell(cell, ++idx);
            }
            writeReportPageByPage(report, "OEReportIter-cells-allpages-allrows-col");
        }

        {
            OEReport report = createReportLayout();
            int idx = 0;
            int row = 2;
            int col = 2;
            for (OEImageBase cell : report.GetCells(0, row, col))
            {
                highlightCell(cell, ++idx);
            }
            writeReportPageByPage(report, "OEReportIter-cells-allpages-row-col");
        }

        {
            OEReport report = createReportLayout();
            int idx = 0;
            int page = 3;
            int row = 2;
            for (OEImageBase cell : report.GetCells(page, row, 0))
            {
                highlightCell(cell, ++idx);
            }
            writeReportPageByPage(report, "OEReportIter-cells-page-row-allcols");
        }

        {
            OEReport report = createReportLayout();
            int idx = 0;
            int page = 3;
            int col = 2;
            for (OEImageBase cell : report.GetCells(page, 0, col))
            {
                highlightCell(cell, ++idx);
            }
            writeReportPageByPage(report, "OEReportIter-cells-page-allrows-col");
        }

        {
            OEReport report = createReportLayout();
            int idx = 0;
            int page = 3;
            int row = 2;
            int col = 2;
            for (OEImageBase cell : report.GetCells(page, row, col))
            {
                highlightCell(cell, ++idx);
            }
            writeReportPageByPage(report, "OEReportIter-cells-page-row-col");
        }

        {
            OEReport report = createReportLayout();
            int idx = 0;
            for (OEImageBase header : report.GetHeaders())
            {
                highlightCell(header, ++idx);
            }
            writeReportPageByPage(report, "OEReportIter-headers");
        }

        {
            OEReport report = createReportLayout();
            int idx = 0;
            for (OEImageBase footer : report.GetFooters())
            {
                highlightCell(footer, ++idx);
            }
            writeReportPageByPage(report, "OEReportIter-footers");
        }

        {
            OEReport report = createReportLayout();
            int idx = 0;
            for (OEImageBase body : report.GetBodies())
            {
                highlightCell(body, ++idx);
            }
            writeReportPageByPage(report, "OEReportIter-bodies");
        }

        OEReport report = createReportLayout();
        writeReportPageByPage(report, "OEReportIter-pages");
    }
}