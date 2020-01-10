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

public class DrawObjects {
    private static void DrawCubicBezier() {
        OEImage image = new OEImage(100, 100);

        OE2DPoint b = new OE2DPoint(20, 70);
        OE2DPoint e = new OE2DPoint(60, 70);
        OE2DPoint c1 = new OE2DPoint(b.GetX() + 50, b.GetY() - 60);
        OE2DPoint c2 = new OE2DPoint(e.GetX() + 50, e.GetY() - 60);

        OEPen pen = new OEPen(oechem.getOELightGreen(), oechem.getOEBlack(), OEFill.On, 2.0);
        image.DrawCubicBezier(b, c1, c2, e, pen);

        oedepict.OEWriteImage("DrawCubicBezier.png", image);
    }

    private static void DrawPath() {
        OEImage image = new OEImage(100, 100);

        OE2DPath path = new OE2DPath(new OE2DPoint(20, 80));
        path.AddLineSegment(new OE2DPoint(80, 80));
        path.AddLineSegment(new OE2DPoint(80, 40));
        path.AddCurveSegment(new OE2DPoint(80, 10), new OE2DPoint(20, 10), new OE2DPoint(20, 40));

        OEPen pen = new OEPen(oechem.getOELightGreen(), oechem.getOEBlack(), OEFill.On, 2.0);
        image.DrawPath(path, pen);
        oedepict.OEWriteImage("DrawPath.png", image);

        for (OE2DPathPoint p : path.GetPoints()) {
            OE2DPoint pos = new OE2DPoint(p.GetPoint());
            System.out.println(String.format("%3.1f %3.1f %d",
                    pos.GetX(), pos.GetY(), p.GetPointType()));
        }
    }
    private static void DrawPolygon() {
        OEImage image = new OEImage(100, 100);

        OE2DPointVector polygon = new OE2DPointVector();

        polygon.add(new OE2DPoint(20, 20));
        polygon.add(new OE2DPoint(40, 40));
        polygon.add(new OE2DPoint(60, 20));
        polygon.add(new OE2DPoint(80, 40));
        polygon.add(new OE2DPoint(80, 80));
        polygon.add(new OE2DPoint(20, 80));

        OEPen pen = new OEPen(oechem.getOELightGreen(), oechem.getOEBlack(), OEFill.On, 2.0);
        image.DrawPolygon(polygon, pen);
        oedepict.OEWriteImage("DrawPolygon.png", image);
    }
    private static void DrawQuadraticBezier() {
        OEImage image = new OEImage(100, 100);

        OE2DPoint b = new OE2DPoint(20, 70);
        OE2DPoint e = new OE2DPoint(80, 70);
        OE2DPoint c = new OE2DPoint(b.GetX() + 30, b.GetY() - 80);

        OEPen pen = new OEPen(oechem.getOELightGreen(), oechem.getOEBlack(), OEFill.On, 2.0);
        image.DrawQuadraticBezier(b, c, e, pen);
        oedepict.OEWriteImage("DrawQuadraticBezier.png", image);
    }
    public static void main(String argv[]) {
        DrawCubicBezier();
        DrawPath();
        DrawPolygon();
        DrawQuadraticBezier();
    }
}
