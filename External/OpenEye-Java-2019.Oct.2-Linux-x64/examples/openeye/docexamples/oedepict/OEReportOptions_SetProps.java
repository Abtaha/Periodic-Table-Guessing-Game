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

public class OEReportOptions_SetProps {

    public static void scaleReportOptions(OEReportOptions opts, double scale) {
        opts.SetPageMargin(OEMargin.Top, scale * opts.GetPageMargin(OEMargin.Top));
        opts.SetPageMargin(OEMargin.Bottom, scale * opts.GetPageMargin(OEMargin.Bottom));
        opts.SetPageMargin(OEMargin.Left, scale * opts.GetPageMargin(OEMargin.Left));
        opts.SetPageMargin(OEMargin.Right, scale * opts.GetPageMargin(OEMargin.Right));

        opts.SetCellGap(scale * opts.GetCellGap());
        opts.SetHeaderHeight(scale * opts.GetHeaderHeight());
        opts.SetFooterHeight(scale * opts.GetFooterHeight());
        opts.SetPageWidth (scale * opts.GetPageWidth());
        opts.SetPageHeight(scale * opts.GetPageHeight());
    }

    public static OEReport createScaleReportLayout(OEReportOptions opts, double scale) {
        scaleReportOptions(opts, scale);
        OEReport report =  new OEReport(opts);

        OEPen borderpen = new OEPen(oechem.getOEGrey(), oechem.getOEGrey(), OEFill.Off, 1.0);

        OEImageBase first = report.NewBody();
        oedepict.OEDrawBorder(first, borderpen);

        int nrgridpages = 1;
        for (int p = 0; p < nrgridpages; ++p) {
            for (int ci = 0; ci < report.NumRowsPerPage() * report.NumColsPerPage(); ++ci) {
                OEImageBase cell = report.NewCell();
                oedepict.OEDrawBorder(cell, borderpen);
            }
        }

        for (OEImageBase page : report.GetPages())
            oedepict.OEDrawBorder(page,  oedepict.getOEBlackPen());

        if (opts.GetHeaderHeight() > 0.0) {
            for (OEImageBase header : report.GetHeaders()) {
                oedepict.OEDrawBorder(header, borderpen);
            }
        }
        if (opts.GetFooterHeight() > 0.0) {
            for (OEImageBase footer : report.GetFooters()) {
                oedepict.OEDrawBorder(footer, borderpen);
            }
        }
        return report;
    }

    private static void writeReportPageByPage(OEReport report, String filenamebase) {
        for (int pidx = 1; pidx <= report.NumPages(); ++pidx) {
            oedepict.OEWriteReport(filenamebase + "-" + Integer.toString(pidx) + ".pdf", report, pidx);
            oedepict.OEWriteReport(filenamebase + "-" + Integer.toString(pidx) + ".png", report, pidx);
        }
    }

    public static void main(String argv[]) {

        double scale = 0.25;
        {
            OEReportOptions opts = new OEReportOptions(3, 2);
            OEReport report = createScaleReportLayout(opts, scale);
            writeReportPageByPage(report, "OEReportOptions_Default");
        }

        {
            OEReportOptions opts = new OEReportOptions(3, 2);
            opts.SetPageSize(OEPageSize.US_Legal);
            OEReport report = createScaleReportLayout(opts, scale);
            writeReportPageByPage(report, "OEReportOptions_SetPageSize");
        }

        {
            OEReportOptions opts = new OEReportOptions(3, 2);
            opts.SetPageOrientation(OEPageOrientation.Landscape);
            OEReport report = createScaleReportLayout(opts, scale);
            writeReportPageByPage(report, "OEReportOptions_SetPageOrientation");
        }

        {
            OEReportOptions opts = new OEReportOptions(3, 2);
            opts.SetPageWidth(opts.GetPageWidth()*0.50);
            OEReport report = createScaleReportLayout(opts, scale);
            writeReportPageByPage(report, "OEReportOptions_SetPageWidth");
        }

        {
            OEReportOptions opts = new OEReportOptions(3, 2);
            opts.SetPageHeight(opts.GetPageHeight()*0.50);
            OEReport report = createScaleReportLayout(opts, scale);
            writeReportPageByPage(report, "OEReportOptions_SetPageHeight");
        }

        {
            OEReportOptions opts = new OEReportOptions(3, 2);
            opts.SetPageMargin(OEMargin.Left,  100);
            opts.SetPageMargin(OEMargin.Right, 100);
            OEReport report = createScaleReportLayout(opts, scale);
            writeReportPageByPage(report, "OEReportOptions_SetPageMargin");
        }

        {
            OEReportOptions opts = new OEReportOptions(3, 2);
            opts.SetPageMargins(100);
            OEReport report = createScaleReportLayout(opts, scale);
            writeReportPageByPage(report, "OEReportOptions_SetPageMargins");
        }

        {
            OEReportOptions opts = new OEReportOptions(3, 2);
            opts.SetCellGap(100);
            OEReport report = createScaleReportLayout(opts, scale);
            writeReportPageByPage(report, "OEReportOptions_SetCellGap");
        }

        {
            OEReportOptions opts = new OEReportOptions(3, 2);
            opts.SetHeaderHeight(100);
            OEReport report = createScaleReportLayout(opts, scale);
            writeReportPageByPage(report, "OEReportOptions_SetHeaderHeight");
        }

        {
            OEReportOptions opts = new OEReportOptions(3, 2);
            opts.SetFooterHeight(100);
            OEReport report = createScaleReportLayout(opts, scale);
            writeReportPageByPage(report, "OEReportOptions_SetFooterHeight");
        }
    }
}