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

public class OEFont_SetProperties {
    private static void DrawHelloWorld(OEImageBase image, OEFont font) {
        double w = image.GetWidth();
        double h = image.GetHeight();
        image.Clear(oechem.getOEWhite());

        OEPen pen = new OEPen();
        image.DrawRectangle(new OE2DPoint(1.0, 1.0), new OE2DPoint(w-1.0, h-1.0), pen);

        image.DrawText(new OE2DPoint(w/2.0, h/2.0), "Hello World!", font);
    }

    private static void DrawHelloWorlds(OEFont font, String basefilename) {
        OEImage image = new OEImage(200, 60);
        DrawHelloWorld(image, font);

        oedepict.OEWriteImage(basefilename+".png", image);
        oedepict.OEWriteImage(basefilename+".pdf", image);
    }

    public static void main(String argv[]) {
        {
            OEFont font = new OEFont();
            DrawHelloWorlds(font, "OEFont_Default");
        }
        {
            OEFont font = new OEFont();
            font.SetFamily(OEFontFamily.Times);
            DrawHelloWorlds(font, "OEFont_SetFamily");
        }
        {
            OEFont font = new OEFont();
            font.SetStyle(OEFontStyle.Bold|OEFontStyle.Italic);
            DrawHelloWorlds(font, "OEFont_SetStyle");
        }
        {
            OEFont font = new OEFont();
            font.SetSize(30);
            DrawHelloWorlds(font, "OEFont_SetSize");
        }
        {
            OEFont font = new OEFont();
            font.SetAlignment(OEAlignment.Right);
            DrawHelloWorlds(font, "OEFont_SetAlignment");
        }
        {
            OEFont font = new OEFont();
            font.SetColor(oechem.getOERed());
            DrawHelloWorlds(font, "OEFont_SetColor");
        }
        {
        OEFont font = new OEFont();
        font.SetRotationAngle(180.0);
        DrawHelloWorlds(font, "OEFont_SetRotationAngle");
        }
    }
}
