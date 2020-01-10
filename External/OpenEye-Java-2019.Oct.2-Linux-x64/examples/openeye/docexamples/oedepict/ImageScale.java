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

public class ImageScale {

    public static void main(String argv[]) {
        OEImage image = new OEImage(100.0, 100.0);

        image.DrawCircle(oedepict.OEGetCenter(image), 40.0, oedepict.getOEBlackPen());
        image.DrawText(oedepict.OEGetCenter(image), "circle", oedepict.getOEDefaultFont());
        oedepict.OEDrawBorder(image, oedepict.getOELightGreyPen());

        oedepict.OEWriteImage("image.png", image);

        OEImage halfimage = new OEImage(image, 0.5);
        oedepict.OEWriteImage("halfimage.png", halfimage);

        OEImage doubleimage = new OEImage(image, 2.0);
        oedepict.OEWriteImage("doubleimage.png", doubleimage);
    }
}
