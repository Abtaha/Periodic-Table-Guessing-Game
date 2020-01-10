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
 * Depicts shape and color overlap between a 3D reference structure and
 * a sets of 3D fit molecules. The molecules have to be pre-aligned.
 * The first molecule is expected be the reference.
 ****************************************************************************/
package openeye.examples.oegrapheme;

import java.net.URL;
import java.util.*;

import openeye.oechem.*;
import openeye.oedepict.*;
import openeye.oegrapheme.*;
import openeye.oeshape.*;

public class ShapeOverlap2PDF {

    public static void main(String argv[]) {
        URL fileURL = ShapeOverlap2PDF.class.getResource("ShapeOverlap2PDF.txt");
        try {
            OEInterface itf = new OEInterface();
            oechem.OEConfigureFromURL(itf, fileURL);

            if (!oechem.OEParseCommandLine(itf, argv, "ShapeOverlap2PDF"))
                return;

            String iname = itf.GetString("-in");
            String oname = itf.GetString("-out");
            int maxhits  = itf.GetInt("-maxhits");

            String ext = oechem.OEGetFileExtension(oname);
            if (!oedepict.OEIsRegisteredMultiPageImageFile(ext)) {
                oechem.OEThrow.Fatal("Unknown multipage image type!");
            }

            oemolistream ifs = new oemolistream();
            if (!ifs.open(iname)) {
                oechem.OEThrow.Fatal("Cannot open input molecule file!");
            }

            OEGraphMol refmol = new OEGraphMol();
            if (!oechem.OEReadMolecule(ifs, refmol)) {
                oechem.OEThrow.Fatal("Cannot read reference molecule!");
            }

            OEReportOptions ropts = new OEReportOptions(3, 1);
            ropts.SetHeaderHeight(40.0);
            ropts.SetFooterHeight(20.0);
            OEReport report = new OEReport(ropts);

            OEColorForceField cff = new OEColorForceField();
            cff.Init(OEColorFFType.ImplicitMillsDean);
            OEColorForceFieldDisplay cffdisplay = new OEColorForceFieldDisplay(cff);

            OEShapeQueryDisplayOptions   qopts = getShapeQueryDisplayOptions();
            OEShapeOverlapDisplayOptions sopts = getShapeOverlapDisplayOptions();
            OEColorOverlapDisplayOptions copts = getColorOverlapDisplayOptions();

            OEShapeQueryDisplay refdisp = new OEShapeQueryDisplay(refmol, cff, qopts);

            OEDots dots = new OEDots(100, 10, "shape overlaps");

            OEGraphMol fitmol = new OEGraphMol();
            while (oechem.OEReadMolecule(ifs, fitmol)) {

                if (maxhits > 0 && dots.GetCounts() >= maxhits)
                    break;
                dots.Update();

                OEImageBase maincell = report.NewCell();
                OEImageGrid grid = new OEImageGrid(maincell, 1, 3);
                grid.SetMargins(5.0);
                grid.SetCellGap(5.0);

                // SD DATA + SCORE GRAPH + QUERY
                OEImageBase cell = grid.GetCell(1, 1);
                double cellw = cell.GetWidth();
                double cellh = cell.GetHeight();

                OEFont tfont = new OEFont(OEFontFamily.Default, OEFontStyle.Bold, 10,
                                          OEAlignment.Left, oechem.getOEBlack());
                OE2DPoint tpos = new OE2DPoint(10.0, 10.0);
                cell.DrawText(tpos, fitmol.GetTitle(), tfont, cell.GetWidth());

                OEImageFrame rframe = new OEImageFrame(cell, cellw, cellh * 0.35, new OE2DPoint(0.0, cellh * 0.10));
                OEImageFrame mframe = new OEImageFrame(cell, cellw, cellh * 0.50, new OE2DPoint(0.0, cellh * 0.50));

                renderScoreRadial(rframe, fitmol);
                oegrapheme.OERenderShapeQuery(mframe, refdisp);

                OEFont font = new OEFont(OEFontFamily.Default, OEFontStyle.Bold, 8,
                                         OEAlignment.Center, oechem.getOEGrey());
                OE2DPoint pos = new OE2DPoint(20.0, 10.0);
                mframe.DrawText(pos, "query", font);
                oedepict.OEDrawCurvedBorder(mframe, oedepict.getOELightGreyPen(), 10.0);

                OEShapeOverlapDisplay odisp = new OEShapeOverlapDisplay(refdisp, fitmol, sopts, copts);

                // SHAPE OVERLAP
                cell = grid.GetCell(1, 2);
                oegrapheme.OERenderShapeOverlap(cell, odisp);
                renderScore(cell, fitmol, "ROCS_ShapeTanimoto", "Shape Tanimoto");

                // COLOR OVERLAP
                cell = grid.GetCell(1, 3);
                oegrapheme.OERenderColorOverlap(cell, odisp);
                renderScore(cell, fitmol, "ROCS_ColorTanimoto", "Color Tanimoto");

                oedepict.OEDrawCurvedBorder(maincell, oedepict.getOELightGreyPen(), 10.0);
            }

            ifs.close();
            dots.Total();

            OEColorForceFieldLegendDisplayOptions cffopts = new OEColorForceFieldLegendDisplayOptions(1, 6);
            for (OEImageBase header : report.GetHeaders()) {
                oegrapheme.OEDrawColorForceFieldLegend(header, cffdisplay, cffopts);
                oedepict.OEDrawCurvedBorder(header, oedepict.getOELightGreyPen(), 10.0);
            }


            OEFont font = new OEFont(OEFontFamily.Default, OEFontStyle.Default, 12,
                                     OEAlignment.Center, oechem.getOEBlack());
            int idx = 0;
            for (OEImageBase footer : report.GetFooters()) {
                idx = idx + 1;
                String text = "-" + Integer.toString(idx) + "-";
                oedepict.OEDrawTextToCenter(footer, text, font);
            }

            oedepict.OEWriteReport(oname, report);
        }
        catch (java.io.IOException e) {
            System.err.println("Unable to open interface file");
        }
    }

    private static OEShapeQueryDisplayOptions getShapeQueryDisplayOptions() {
        OEShapeQueryDisplayOptions qopts = new OEShapeQueryDisplayOptions();
        qopts.SetTitleLocation(OETitleLocation.Hidden);
        qopts.SetAtomLabelFontScale(1.5);
        OEPen pen = new OEPen(oechem.getOEBlack(), oechem.getOEBlack(), OEFill.Off, 1.5);
        qopts.SetDefaultBondPen(pen);
        qopts.SetSurfaceArcFxn(new OEDefaultArcFxn(oedepict.getOELightGreyPen()));
        return qopts;
    }

    private static OEShapeOverlapDisplayOptions getShapeOverlapDisplayOptions() {
        OEShapeOverlapDisplayOptions sopts = new OEShapeOverlapDisplayOptions();
        sopts.SetTitleLocation(OETitleLocation.Hidden);
        sopts.SetAtomLabelFontScale(1.5);
        OEPen pen = new OEPen(oechem.getOEBlack(), oechem.getOEBlack(), OEFill.Off, 1.5);
        sopts.SetDefaultBondPen(pen);
        OEPen arcpen = new OEPen(oechem.getOEGrey(), oechem.getOEGrey(), OEFill.Off, 1.0, 0x1111);
        sopts.SetQuerySurfaceArcFxn(new OEDefaultArcFxn(arcpen));
        sopts.SetOverlapColor(new OEColor(110, 110, 190));
        return sopts;
    }

    private static OEColorOverlapDisplayOptions getColorOverlapDisplayOptions() {
        OEColorOverlapDisplayOptions copts = new OEColorOverlapDisplayOptions();
        copts.SetTitleLocation(OETitleLocation.Hidden);
        copts.SetAtomLabelFontScale(1.5);
        OEPen pen = new OEPen(oechem.getOEBlack(), oechem.getOEBlack(), OEFill.Off, 1.5);
        copts.SetDefaultBondPen(pen);
        OEPen arcpen = new OEPen(oechem.getOEGrey(), oechem.getOEGrey(), OEFill.Off, 1.0, 0x1111);
        copts.SetQuerySurfaceArcFxn(new OEDefaultArcFxn(arcpen));
        return copts;
    }

    private static double getScore(OEMolBase mol, String sdtag) {
        if (oechem.OEHasSDData(mol, sdtag)) {
            String scorestr = oechem.OEGetSDData(mol, sdtag);
            double score = Double.parseDouble(scorestr);
            return score;
        }
        return 0.0;
    }

    private static void renderScoreRadial(OEImageBase image, OEMolBase mol) {

        double sscore = Math.max(Math.min(getScore(mol, "ROCS_ShapeTanimoto"), 1.0), 0.00);
        double cscore = Math.max(Math.min(getScore(mol, "ROCS_ColorTanimoto"), 1.0), 0.00);

        if (sscore > 0.0 || cscore > 0.0) {
            ArrayList<Double>scores = new ArrayList<Double>();
            scores.add(sscore);
            scores.add(cscore);
            oegrapheme.OEDrawROCSScores(image, scores);
        }
    }

    private static void renderScore(OEImageBase image, OEMolBase mol, 
                                    String sdtag, String label) {

        double score = getScore(mol, sdtag);
        if (score == 0.0)
            return;

        double w = image.GetWidth();
        double h = image.GetHeight();
        OEImageFrame frame = new OEImageFrame(image, w, h * 0.10, new OE2DPoint(0.0, h * 0.90));
        OEFont font = new OEFont(OEFontFamily.Default, OEFontStyle.Default, 9,
                                 OEAlignment.Center, oechem.getOEBlack());

        String text = label +  " = " + Double.toString(score);
        oedepict.OEDrawTextToCenter(frame, text, font);
    }
}
