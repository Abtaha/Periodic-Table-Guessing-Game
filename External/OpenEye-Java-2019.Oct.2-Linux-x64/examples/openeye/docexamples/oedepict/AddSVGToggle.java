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

import openeye.oedepict.*;

public class AddSVGToggle {
    public static void main(String argv[]) {
        int width = 200;
        int height = 100;
        OEImage image = new OEImage(width, height);

        OESVGGroup toggle_area = image.NewSVGGroup("toggle_area");
        OESVGGroup target_area = image.NewSVGGroup("toggle_target_area");

        oedepict.OEAddSVGToggle(toggle_area, target_area);

        image.PushGroup(toggle_area);
        image.DrawRectangle(new OE2DPoint(30, 30), new OE2DPoint(70, 70), oedepict.getOERedBoxPen());
        image.PopGroup(toggle_area);

        image.PushGroup(target_area);
        image.DrawCircle(new OE2DPoint(150, 50), 30, oedepict.getOEBlueBoxPen());
        image.PopGroup(target_area);

        oedepict.OEAddInteractiveIcon(image);

        oedepict.OEWriteImage("AddSVGToggle.svg", image);
    }
}
