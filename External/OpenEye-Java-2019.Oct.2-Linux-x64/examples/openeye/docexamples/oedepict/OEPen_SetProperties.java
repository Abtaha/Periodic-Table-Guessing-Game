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

public class OEPen_SetProperties {
    static private void DrawFigure(OEImageBase image, OEPen pen) {
        double w = image.GetWidth();
        double h = image.GetHeight();
        image.Clear(oechem.getOEWhite());

        OE2DPoint a = new OE2DPoint(10.0, 10.0);
        OE2DPoint b = new OE2DPoint(w-10, h-10.0);
        image.DrawRectangle(a, b, pen);

        a = new OE2DPoint(20.0, 20.0);
        b = new OE2DPoint(w-20, h-20.0);
        image.DrawLine(a, b, pen);

        a = new OE2DPoint(60.0, 20.0);
        b = new OE2DPoint(20.0, 60.0);
        image.DrawLine(a, b, pen);
    }

    static private void DrawFigures(OEPen pen, String basefilename) {
        OEImage image = new OEImage(80, 80);
        DrawFigure(image, pen);

        oedepict.OEWriteImage(basefilename+".png", image);
        oedepict.OEWriteImage(basefilename+".pdf", image);
    }

    public static void main(String argv[]) {
        {
            OEPen pen = new OEPen();
            DrawFigures(pen, "OEPen_Default");
        }
        {
            OEPen pen = new OEPen();
            pen.SetForeColor(oechem.getOERed());
            DrawFigures(pen, "OEPen_SetForeColor");
        }
        {
            OEPen pen = new OEPen();
            pen.SetFill(OEFill.On);
            pen.SetBackColor(oechem.getOEBlueTint());
            DrawFigures(pen, "OEPen_SetBackColor");
        }
        {
            OEPen pen = new OEPen();
            pen.SetLineWidth(4);
            pen.SetLineCap(OELineCap.Square);
            DrawFigures(pen, "OEPen_SetLineCap");
        }
        {
            OEPen pen = new OEPen();
            pen.SetLineWidth(4);
            pen.SetLineJoin(OELineJoin.Miter);
            DrawFigures(pen, "OEPen_SetLineJoin");
        }
        {
            OEPen pen = new OEPen();
            pen.SetLineWidth(5.0);
            DrawFigures(pen, "OEPen_SetLineWidth");
        }
        {
            OEPen pen = new OEPen();
            pen.SetLineStipple(OEStipple.ShortDash);
            DrawFigures(pen, "OEPen_SetLineStipple");
        }
        {
            OEPen pen = new OEPen();
            pen.SetLineStipple(OEStipple.ShortDash);
            pen.SetStippleFactor(2);
            DrawFigures(pen, "OEPen_SetStippleFactor");
        }        
    }
}
